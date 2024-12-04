package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.controller.validator.MovimientosValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.exception.cuentas.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovimientosService {

    @Autowired
    BanelcoService banelcoService;

    @Autowired
    CuentaDao cuentaDao;

    @Autowired
    MovimientosValidator MovimientosValidator;

    public void depositar(MovimientosDto movimientosDto) throws CuentaNotExistException, MonedasIncompatiblesException {
        Cuenta cuenta = cuentaDao.find(movimientosDto.getNumeroCuenta());

        if (cuenta != null) {
            Movimientos movimiento = new Movimientos(movimientosDto);
            if (cuenta.getTipoMoneda().equals(movimiento.getTipoMoneda())) {
                cuenta.setBalance(cuenta.getBalance() + movimientosDto.getMonto());
                movimiento.setTipoOperacion(TipoOperacion.DEPOSITO);
                cuenta.addMovimiento(movimiento);
                cuentaDao.save(cuenta);
            } else {
                throw new MonedasIncompatiblesException("No coinciden las monedas");
            }
        } else {
            throw new CuentaNotExistException("El numero de cuenta ingresado no coincide con una cuenta existente");
        }
    }
    public Movimientos retirar(MovimientosDto retiro) throws  MonedasIncompatiblesException, FondosInsuficientesException, CuentaNotExistException{
        Cuenta cuenta = cuentaDao.find(retiro.getNumeroCuenta());
        if (cuenta == null) {
            throw new CuentaNotExistException("Cuenta no encontrada");
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

    public Movimientos transferir(MovimientosTransferenciasDto transferencias) throws CuentaNotExistException,  MonedasIncompatiblesException, FondosInsuficientesException {
        Cuenta cuentaOrigen = cuentaDao.find(transferencias.getCuentaOrigen());
        Cuenta cuentaDestino = cuentaDao.find(transferencias.getCuentaDestino());

        if (cuentaOrigen != null) {
            Movimientos transferencia = new Movimientos(transferencias);

                if (cuentaDestino != null) { // Si la cuenta destino no existe ejecutamos la transferencia de Banelco

                    if (cuentaOrigen.getBalance() >= transferencia.getMonto()) {  // vemos si tiene suficientes fondos

                        if (cuentaOrigen.getTipoMoneda().equals(cuentaDestino.getTipoMoneda())) { // Si el tipo de moneda de la cuenta origen coincide con el tipo de moneda de la cuentaDestino

                            cuentaOrigen.setBalance(cuentaOrigen.getBalance() - transferencia.getMonto() - comision(transferencias, cuentaOrigen.getTipoMoneda())); //la cuenta origen se le da la comision

                            cuentaDestino.setBalance(cuentaDestino.getBalance() + (transferencia.getMonto()));

                            Movimientos movimientoOrigen = new Movimientos(transferencias);
                            Movimientos movimientoDestino = new Movimientos(transferencias);

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
                    banelcoExterno(transferencias, cuentaOrigen);
                }
        } else {
            throw new CuentaNotExistException("La cuenta de origen no existe");
        }

        return new Movimientos(transferencias);
    }

private double comision(MovimientosTransferenciasDto transferencia, TipoMoneda tipoMoneda){
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

public void banelcoExterno(MovimientosTransferenciasDto transferencia, Cuenta cuentaOrigen)
        throws FondosInsuficientesException, CuentaNotExistException {

    // Verificar que la cuenta origen tenga fondos suficientes
    double comision = comision(transferencia, cuentaOrigen.getTipoMoneda());
    double montoTotal = transferencia.getMonto() + comision;
    if (cuentaOrigen.getBalance() < montoTotal) {
        throw new FondosInsuficientesException("Fondos insuficientes para realizar la transferencia");
    }

    // Llamar al servicio externo para realizar la transferencia
    boolean transferenciaExitosa = banelcoService.realizarTransferenciaBanelco(
            transferencia.getCuentaOrigen(),
            transferencia.getCuentaDestino(),
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
        throw new CuentaNotExistException("Transferencia fallida: cuenta destino no encontrada");
    }
}
}