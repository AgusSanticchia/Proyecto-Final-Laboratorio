package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovimientosService {

    @Autowired
    BanelcoService banelcoService;

    @Autowired
    CuentaDao cuentaDao;

    public Movimientos depositar(MovimientosDto deposito)
            throws CuentaNotFoundException, MonedasIncompatiblesException {

        Cuenta cuenta = cuentaDao.find(deposito.getNumeroCuenta());
        if (cuenta == null) {
            throw new CuentaNotFoundException("Cuenta no encontrada");
        }

        if (deposito.getMonto() < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }

        TipoMoneda tipoMonedaDeposito = TipoMoneda.fromString(deposito.getTipoMoneda());
        if (!cuenta.getTipoMoneda().equals(tipoMonedaDeposito)) {
            throw new MonedasIncompatiblesException("Moneda incompatible");
        }

        cuenta.setBalance(cuenta.getBalance() + deposito.getMonto());

        // Crear el objeto Movimientos con el tipo de operación DEPOSITO
        Movimientos movimientos = new Movimientos(deposito);
        movimientos.setTipoOperacion(TipoOperacion.DEPOSITO);
        cuenta.addMovimiento(movimientos);

        cuentaDao.update(cuenta);

        // Devolver el mismo objeto Movimientos que ya tiene el tipo de operación DEPOSITO asignado
        return movimientos;
    }


    public Movimientos retirar(MovimientosDto retiro)
            throws FondosInsuficientesException, CuentaNotFoundException, MonedasIncompatiblesException {

        Cuenta cuenta = cuentaDao.find(retiro.getNumeroCuenta());
        if (cuenta == null) {
            throw new CuentaNotFoundException("Cuenta no encontrada");
        }

        TipoMoneda tipoMonedaRetiro = TipoMoneda.fromString(retiro.getTipoMoneda());
        if (!cuenta.getTipoMoneda().equals(tipoMonedaRetiro)) {
            throw new MonedasIncompatiblesException("Moneda incompatible");
        }

        if (cuenta.getBalance() < retiro.getMonto()) {
            throw new FondosInsuficientesException("Fondos insuficientes");
        }

        cuenta.setBalance(cuenta.getBalance() - retiro.getMonto());

        Movimientos movimientos = new Movimientos(retiro);
        movimientos.setTipoOperacion(TipoOperacion.RETIRO);
        cuenta.addMovimiento(movimientos);

        cuentaDao.update(cuenta);

        return movimientos;
    }

    public void transferir(MovimientosTransferenciasDto transferencia)
            throws FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotFoundException {

        Cuenta cuentaOrigen = cuentaDao.find(transferencia.getNumeroCuentaOrigen());
        Cuenta cuentaDestino = cuentaDao.find(transferencia.getNumeroCuentaDestino());

        if (cuentaOrigen == null) {
            throw new CuentaNotFoundException("Cuenta origen no encontrada");
        }

        // Verificar si la cuenta destino es externa (nula) o del mismo banco
        if (cuentaDestino != null) {
            // Verificar que ambas cuentas tengan la misma moneda
            if (!cuentaOrigen.getTipoMoneda().equals(cuentaDestino.getTipoMoneda())) {
                throw new MonedasIncompatiblesException("Moneda incompatible entre la cuenta origen y destino");
            }

            // Verificar que la cuenta origen tenga fondos suficientes
            double comision = calcularComision(transferencia, cuentaOrigen.getTipoMoneda());
            double montoTotal = transferencia.getMonto() + comision;

            if (cuentaOrigen.getBalance() < montoTotal) {
                throw new FondosInsuficientesException("Fondos insuficientes para realizar la transferencia");
            }

            // Actualizar los saldos de ambas cuentas
            cuentaOrigen.setBalance(cuentaOrigen.getBalance() - montoTotal);
            cuentaDestino.setBalance(cuentaDestino.getBalance() + transferencia.getMonto());

            // Registrar movimientos
            Movimientos movimientoCuentaOrigen = new Movimientos(transferencia);
            Movimientos movimientoCuentaDestino = new Movimientos(transferencia);
            movimientoCuentaOrigen.setTipoOperacion(TipoOperacion.TRANSFERENCIA);
            movimientoCuentaDestino.setTipoOperacion(TipoOperacion.TRANSFERENCIA);

            cuentaOrigen.addMovimiento(movimientoCuentaOrigen);
            cuentaDestino.addMovimiento(movimientoCuentaDestino);

            // Guardar actualizaciones en la base de datos
            cuentaDao.update(cuentaOrigen);
            cuentaDao.update(cuentaDestino);

        } else {
            // Si la cuenta destino es externa, se hace a través de Banelco
            banelcoExternal(transferencia, cuentaOrigen);
        }
    }

    private double calcularComision(MovimientosTransferenciasDto transferencia, TipoMoneda tipoMoneda) {
        double monto = transferencia.getMonto();
        double comision = 0;

        // Aplicar comisión según la moneda y monto de transferencia
        switch (tipoMoneda) {
            case PESOS -> {
                if (monto > 1000000) {
                    comision = monto * 0.02; // 2% de comisión si supera $1,000,000
                }
            }
            case DOLARES -> {
                if (monto > 5000) {
                    comision = monto * 0.005; // 0.5% de comisión si supera U$S 5,000
                }
            }
        }

        return comision;
    }

    public void banelcoExternal(MovimientosTransferenciasDto transferencia, Cuenta cuentaOrigen)
            throws CuentaNotFoundException, FondosInsuficientesException {

        // Verificar que la cuenta origen tenga fondos suficientes
        double comision = calcularComision(transferencia, cuentaOrigen.getTipoMoneda());
        double montoTotal = transferencia.getMonto() + comision;

        if (cuentaOrigen.getBalance() < montoTotal) {
            throw new FondosInsuficientesException("Fondos insuficientes para realizar la transferencia");
        }

        // Llamar al servicio externo para realizar la transferencia
        boolean transferenciaExitosa = banelcoService.realizarTransferenciaBanelco(
                transferencia.getNumeroCuentaOrigen(),
                transferencia.getNumeroCuentaDestino(),
                transferencia.getMonto()
        );

        if (transferenciaExitosa) {
            // Actualizar el saldo de la cuenta origen y registrar el movimiento
            cuentaOrigen.setBalance(cuentaOrigen.getBalance() - montoTotal);

            Movimientos movimiento = new Movimientos(transferencia);
            movimiento.setTipoOperacion(TipoOperacion.TRANSFERENCIA);
            cuentaOrigen.addMovimiento(movimiento);

            // Guardar las actualizaciones en la base de datos
            cuentaDao.update(cuentaOrigen);
        } else {
            throw new CuentaNotFoundException("Transferencia fallida: cuenta destino no encontrada");
        }
    }
}
