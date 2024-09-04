package ar.edu.utn.frbb.tup.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class BanelcoService throws CuentaNoPermitaException {

    int random = (int) ((Math.random() * (10 - 1)) + 1);
    String message;
    if (random <= 2) {
      throw new CuentaNoPermitaException("La cuenta no permite realizar transferencias");
    } else {
        message = "Transferencia realizada con éxito";
    }
}
