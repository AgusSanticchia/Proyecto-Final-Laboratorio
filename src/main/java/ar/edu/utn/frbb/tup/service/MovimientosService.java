package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
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

    public Movimientos transferir(MovimientosTransferenciasDto transferencia)
            throws FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotFoundException {

        Cuenta cuentaOrigen = cuentaDao.find(transferencia.getNumeroCuentaOrigen());
        if (cuentaOrigen == null) {
            throw new CuentaNotFoundException("Cuenta origen no encontrada");
        }

        Cuenta cuentaDestino = cuentaDao.find(transferencia.getNumeroCuentaDestino());

        if (cuentaDestino == null) {
            boolean transferenciaExitosa = banelcoService.realizarTransferenciaBanelco(
                    transferencia.getNumeroCuentaOrigen(),
                    transferencia.getNumeroCuentaDestino(),
                    transferencia.getMonto(),
                    transferencia.getTipoMoneda());

            if (!transferenciaExitosa) {
                throw new CuentaNotFoundException("Transferencia fallida. Cuenta destino no existe.");
            }

            return new Movimientos(transferencia); // Registro exitoso de la transferencia externa
        }

        TipoMoneda tipoMonedaTransferencia = TipoMoneda.fromString(transferencia.getTipoMoneda());
        if (!cuentaOrigen.getTipoMoneda().equals(tipoMonedaTransferencia)
                || !cuentaDestino.getTipoMoneda().equals(tipoMonedaTransferencia)) {
            throw new MonedasIncompatiblesException("Las monedas no coinciden");
        }

        if (cuentaOrigen.getBalance() < transferencia.getMonto()) {
            throw new FondosInsuficientesException("Fondos insuficientes");
        }

        double cargo = calcularComision(cuentaOrigen.getTipoMoneda(), transferencia.getMonto());

        cuentaOrigen.setBalance(cuentaOrigen.getBalance() - (transferencia.getMonto() + cargo));
        cuentaDestino.setBalance(cuentaDestino.getBalance() + transferencia.getMonto());

        cuentaDao.update(cuentaOrigen);
        cuentaDao.update(cuentaDestino);

        return new Movimientos(transferencia);
    }

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

    private double calcularComision(TipoMoneda tipoMoneda, Double monto) {
        switch (tipoMoneda) {
            case tipoMoneda.DOLARES:
                return monto * 0.02;  // Comisión del 2% para USD
            case tipoMoneda.PESOS:
                return monto * 0.01;  // Comisión del 1% para ARS
            default:
                return 0;
        }
    }
}
