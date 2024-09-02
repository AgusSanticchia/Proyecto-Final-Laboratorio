package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotExistException;
import ar.edu.utn.frbb.tup.model.exception.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.model.exception.MonedaDistintaException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.persistence.MovimientosDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MovimientosService {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaDao cuentaDao;

    @Autowired
    private BanelcoService banelcoService;

    @Autowired
    private MovimientosDao movimientosDao;

    public void transferencia(MovimientosDto movimientosDto) throws FondosInsuficientesException, CuentaNotExistException, MonedaDistintaException {
        Cuenta cuentaOrigen = cuentaDao.find(movimientosDto.getCuentaOrigen());
        Cuenta cuentaDestino = cuentaDao.find(movimientosDto.getCuentaDestino());

        if (cuentaOrigen == null || cuentaDestino == null) {
            throw new CuentaNotExistException("Una de las cuentas no existe");
        }

        if (!cuentaOrigen.getMoneda().equals(cuentaDestino.getMoneda()) || !cuentaOrigen.getMoneda().name().equals(movimientosDto.getMoneda())) {
            throw new MonedaDistintaException("Las monedas de las cuentas y la transferencia deben ser iguales");
        }

        if (cuentaOrigen.getBalance() < movimientosDto.getMonto()) {
            throw new FondosInsuficientesException("Fondos insuficientes en la cuenta de origen");
        }

        banelcoService.transferir(cuentaOrigen, cuentaDestino, movimientosDto.getMonto());

        Movimientos movimiento = registrarMovimiento(TipoOperacion.TRANSFERENCIA, movimientosDto.getMonto(), "Transferencia realizada",
                TipoMoneda.valueOf(movimientosDto.getMoneda()), cuentaOrigen.getNumeroCuenta(), cuentaDestino.getNumeroCuenta());
        movimientosDao.transferir(movimiento);
    }

    public void deposito(MovimientosDto movimientosDto) throws CuentaNotExistException, MonedaDistintaException {
        Cuenta cuenta = cuentaDao.find(movimientosDto.getCuentaOrigen());

        if (cuenta == null) {
            throw new CuentaNotExistException("La cuenta buscada no existe");
        }

        if (!cuenta.getMoneda().name().equals(movimientosDto.getMoneda())) {
            throw new MonedaDistintaException("La moneda del depósito no coincide con la moneda de la cuenta");
        }

        double nuevoBalance = cuenta.getBalance() + movimientosDto.getMonto();
        cuenta.setBalance(nuevoBalance);
        cuentaDao.update(cuenta);

        Movimientos movimiento = registrarMovimiento(TipoOperacion.DEPOSITO, movimientosDto.getMonto(), "Depósito realizado",
                TipoMoneda.valueOf(movimientosDto.getMoneda()), cuenta.getNumeroCuenta(), cuenta.getNumeroCuenta());
        movimientosDao.depositar(movimiento);
    }

    public void retiro(MovimientosDto movimientosDto) throws CuentaNotExistException, FondosInsuficientesException, MonedaDistintaException {
        Cuenta cuenta = cuentaDao.find(movimientosDto.getCuentaDestino());

        if (cuenta == null) {
            throw new CuentaNotExistException("La cuenta buscada no existe");
        }

        if (!cuenta.getMoneda().name().equals(movimientosDto.getMoneda())) {
            throw new MonedaDistintaException("La moneda del retiro no coincide con la moneda de la cuenta");
        }

        if (cuenta.getBalance() < movimientosDto.getMonto()) {
            throw new FondosInsuficientesException("Fondos insuficientes para realizar el retiro");
        }

        double nuevoBalance = cuenta.getBalance() - movimientosDto.getMonto();
        cuenta.setBalance(nuevoBalance);
        cuentaDao.update(cuenta);

        Movimientos movimiento = registrarMovimiento(TipoOperacion.RETIRO, movimientosDto.getMonto(), "Retiro realizado",
                TipoMoneda.valueOf(movimientosDto.getMoneda()), cuenta.getNumeroCuenta(), cuenta.getNumeroCuenta());
        movimientosDao.retirar(movimiento);
    }

    private Movimientos registrarMovimiento(TipoOperacion tipoOperacion, Double monto, String descripcion, TipoMoneda tipoMoneda, long cuentaOrigen, long cuentaDestino) {
        return new Movimientos(tipoOperacion, monto, descripcion, LocalDateTime.now(), tipoMoneda, cuentaOrigen, cuentaDestino, generateMovimientoId());
    }

    private long generateMovimientoId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}