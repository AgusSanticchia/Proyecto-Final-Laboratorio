package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.model.exception.TipoMonedaNoCoincidenException;
import org.springframework.stereotype.Service;

@Service
public class BanelcoService {

    public void transferir(Cuenta cuentaOrigen, Cuenta cuentaDestino, double monto) throws TipoMonedaNoCoincidenException, FondosInsuficientesException {

        if (cuentaOrigen.getTipoMoneda() != cuentaDestino.getTipoMoneda()) {
            throw new TipoMonedaNoCoincidenException("Las monedas no coinciden");
        }

        if (cuentaOrigen.getTipoMoneda() == TipoMoneda.DOLARES) {
            if (cuentaOrigen.getBalance() < monto * 1.005) {
                throw new FondosInsuficientesException("No hay suficiente saldo");
            }
        } else {
            if (cuentaOrigen.getBalance() < monto * 1.02) {
                throw new FondosInsuficientesException("No hay suficiente saldo");
            }
        }

        if (cuentaOrigen.getTipoMoneda() == TipoMoneda.DOLARES) {
            cuentaOrigen.setBalance(cuentaOrigen.getBalance() - monto * 0.005);
        }else {
            cuentaOrigen.setBalance(cuentaOrigen.getBalance() - monto * 0.02);
        }

        cuentaOrigen.setBalance(cuentaOrigen.getBalance() - monto);
        cuentaDestino.setBalance(cuentaDestino.getBalance() + monto);
        System.out.println("Transferencia exitosa");
    }


}
