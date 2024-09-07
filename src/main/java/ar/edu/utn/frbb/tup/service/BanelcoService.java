package ar.edu.utn.frbb.tup.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BanelcoService {

    private final Random random = new Random();

    public boolean realizarTransferenciaBanelco(long numeroCuentaOrigen, long numeroCuentaDestino, double monto, String tipoMoneda) {
        return random.nextDouble() < 0.75;
    }
}
