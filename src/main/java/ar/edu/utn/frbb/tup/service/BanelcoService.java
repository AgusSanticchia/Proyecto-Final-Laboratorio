package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.BanelcoDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.CuentaNoPermitidaException;
import org.springframework.stereotype.Service;

@Service
public class BanelcoService {

    public BanelcoDto transferenciaPermitida (Cuenta cuentaOrigen, long cuentaDestino, double monto) throws CuentaNoPermitidaException {
        int random = (int) ((Math.random() * (10 - 1)) + 1);
        String message;
        if (random <= 2) {
            throw new CuentaNoPermitidaException("La cuenta no permite realizar transferencias");
        } else {
            message = "Transferencia realizada con éxito";
        }
        return new BanelcoDto(message);
    }
}
