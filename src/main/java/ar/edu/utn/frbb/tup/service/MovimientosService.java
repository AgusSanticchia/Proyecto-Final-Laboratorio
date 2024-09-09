package ar.edu.utn.frbb.tup.service;

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

        TipoMoneda tipoMonedaDeposito = TipoMoneda.fromString(deposito.getTipoMoneda());
        if (!cuenta.getTipoMoneda().equals(tipoMonedaDeposito)) {
            throw new MonedasIncompatiblesException("Moneda incompatible");
        }

        cuenta.setBalance(cuenta.getBalance() + deposito.getMonto());
        cuentaDao.update(cuenta);

        return new Movimientos(deposito);
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
        cuentaDao.update(cuenta);

        return new Movimientos(retiro);
    }


    public void transferir(MovimientosTransferenciasDto transferencia) throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        Cuenta cuentaOrigen = cuentaDao.find(transferencia.getNumeroCuentaOrigen());
        Cuenta cuentaDestino = cuentaDao.find(transferencia.getNumeroCuentaDestino());

        if (cuentaOrigen != null) {
            Movimientos movimiento = new Movimientos(transferencia);

            if (cuentaOrigen.getTipoMoneda().equals(movimiento.getTipoMoneda())) {  // Validamos que el tipo de moneda que se introduce en el JSON sea el mismo que la cuenta

                if (cuentaDestino != null) { // Si la cuenta destino no existe en nuestro banco "Utilizamos" el servicio Banelco

                    if (cuentaOrigen.getBalance() >= transferencia.getMonto()) {  // Claramente el balance de la cuenta tiene que ser mayor al monto a transferir

                        if (cuentaOrigen.getTipoMoneda().equals(cuentaDestino.getTipoMoneda())) { // Si el tipo de moneda de la cuenta origen coincide con el tipo de moneda de la cuentaDestino

                            cuentaOrigen.setBalance(cuentaOrigen.getBalance() - transferencia.getMonto());
                            cuentaDestino.setBalance(cuentaDestino.getBalance() + transferencia.getMonto());


                            MovimientosTransferenciasDto movimientoOrigen = new MovimientosTransferenciasDto(transferencia);
                            MovimientosTransferenciasDto movimientoDestino = new MovimientosTransferenciasDto(transferencia);

                            movimientoOrigen.setTipoOperacion(TipoOperacion.TRANSFERENCIA);
                            movimientoDestino.setTipoOperacion(TipoOperacion.TRANSFERENCIA);

                            cuentaOrigen.addMovimiento(movimientoOrigen);
                            cuentaDestino.addMovimiento(movimientoDestino);

                            cuentaDao.save(cuentaOrigen);
                            cuentaDao.save(cuentaDestino);

                        } else {
                            throw new MonedasIncompatiblesException("Las monedas entre cuentas debe ser la misma");
                        }
                    } else {
                        throw new FondosInsuficientesException("El monto supera al dinero disponible en la cuenta");
                    }

                } else {
                    // Invocacion al servicio balenco
                    banelcoExternal(transferencia, cuentaOrigen);
                }
            } else {
                throw new MonedasIncompatiblesException("Son diferentes monedas");
            }
        } else {
            throw new CuentaNotFoundException("La cuenta de origen no existe");
        }


        private double calcularComision (MovimientosTransferenciasDto transferencia, TipoMoneda tipoMoneda){
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

        public void banelcoExternal (MovimientosTransferenciasDto transferencia, Cuenta cuentaOrigen)
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
}