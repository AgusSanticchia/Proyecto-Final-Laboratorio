package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.model.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.monedas.TipoMonedaNoSoportadaException;
import org.springframework.stereotype.Component;
import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;

@Component
public class CuentaValidator {

    public void validateCuenta(CuentaDto cuentaDto) throws TipoCuentaNoSoportadaException, TipoMonedaNoSoportadaException, MonedasIncompatiblesException {
        validateTipoCuenta(cuentaDto);
        validateTipoMoneda(cuentaDto);
        validateMonedaCompatibilidad(cuentaDto);
    }

    private void validateTipoCuenta(CuentaDto cuentaDto) throws TipoCuentaNoSoportadaException {
        if (!"CC$".equals(cuentaDto.getTipoCuenta()) && !"CAU$S".equals(cuentaDto.getTipoCuenta()) && !"CA$".equals(cuentaDto.getTipoCuenta())) {
            throw new TipoCuentaNoSoportadaException("Tipo de cuenta no válido: " + cuentaDto.getTipoCuenta());
        }
    }

    private void validateTipoMoneda(CuentaDto cuentaDto) throws TipoMonedaNoSoportadaException {
        if (!"ARS".equals(cuentaDto.getTipoMoneda()) && !"USD".equals(cuentaDto.getTipoMoneda())) {
            throw new TipoMonedaNoSoportadaException("Tipo de moneda no válido: " + cuentaDto.getTipoMoneda());
        }
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
