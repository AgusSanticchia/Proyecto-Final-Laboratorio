package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.exception.cuentas.TipoCuentaNoSoportadaException;
import org.springframework.stereotype.Component;
import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;

@Component
public class CuentaValidator {

    public void validateCuenta(CuentaDto cuentaDto) throws TipoCuentaNoSoportadaException, MonedasIncompatiblesException {
        validateDatostosCuenta(cuentaDto);
        validateMonedaCompatibilidad(cuentaDto);
    }

    private void validateDatostosCuenta(CuentaDto cuentaDto) throws TipoCuentaNoSoportadaException {
        //Tipo de cuenta
        if (cuentaDto.getTipoCuenta() == null || cuentaDto.getTipoCuenta().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un tipo de cuenta");
        //Tipo de moneda
        if (cuentaDto.getTipoMoneda() == null || cuentaDto.getTipoMoneda().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un tipo de moneda");
        //DNI Titular
        if (cuentaDto.getDniTitular() == 0) throw new IllegalArgumentException("Error: Ingrese un dni");

        if (cuentaDto.getDniTitular() < 10000000 || cuentaDto.getDniTitular() > 99999999) throw new IllegalArgumentException("Error: El dni debe tener 8 digitos");
    }

    private void validateMonedaCompatibilidad(CuentaDto cuentaDto) throws MonedasIncompatiblesException {
        String tipoCuenta = cuentaDto.getTipoCuenta();
        String tipoMoneda = cuentaDto.getTipoMoneda();

        if ("CC$".equals(tipoCuenta) && !"ARS".equals(tipoMoneda)) {
            throw new MonedasIncompatiblesException("Moneda incompatible: " + tipoMoneda + " para tipo de cuenta " + tipoCuenta);
        } else if ("CAU$S".equals(tipoCuenta) && !"USD".equals(tipoMoneda)) {
            throw new MonedasIncompatiblesException("Moneda incompatible: " + tipoMoneda + " para tipo de cuenta " + tipoCuenta);
        } else if ("CA$".equals(tipoCuenta) && !("ARS".equals(tipoMoneda) || "USD".equals(tipoMoneda))) {
            throw new MonedasIncompatiblesException("Moneda incompatible: " + tipoMoneda + " para tipo de cuenta " + tipoCuenta);
        }
    }
}
