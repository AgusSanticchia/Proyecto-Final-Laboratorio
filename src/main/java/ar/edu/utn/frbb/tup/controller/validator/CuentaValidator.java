package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.model.exception.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.TipoMonedaNoSoportadaException;
import org.springframework.stereotype.Component;
import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;

@Component
public class CuentaValidator {

    public void validateCuenta(CuentaDto cuentaDto) throws TipoCuentaNoSoportadaException, TipoMonedaNoSoportadaException, MonedasIncompatiblesException, Exception {
        validateTipoCuenta(cuentaDto);
        validateMoneda(cuentaDto);
        validateMonedasIncompatibles(cuentaDto);
    }

    private void validateTipoCuenta(CuentaDto cuentaDto) throws TipoCuentaNoSoportadaException {
        if (!"CC$".equals(cuentaDto.getTipoCuenta()) && !"CAU$S".equals(cuentaDto.getTipoCuenta()) && !"CA$".equals(cuentaDto.getTipoCuenta())) {
            throw new TipoCuentaNoSoportadaException("El tipo de cuenta no es correcto");
        }
    }

    private void validateMoneda(CuentaDto cuentaDto) throws TipoMonedaNoSoportadaException {
        if (!"ARS".equals(cuentaDto.getTipoMoneda()) && !"USD".equals(cuentaDto.getTipoMoneda())) {
            throw new TipoMonedaNoSoportadaException("El tipo de moneda no es correcto");
        }
    }

    private void validateMonedasIncompatibles(CuentaDto cuentaDto) throws MonedasIncompatiblesException {
        String tipoCuenta = cuentaDto.getTipoCuenta();
        String tipoMoneda = cuentaDto.getTipoMoneda();

        if ("CC$".equals(tipoCuenta) && !"ARS".equals(tipoMoneda)) {
            throw new MonedasIncompatiblesException("La moneda " + tipoMoneda + " no es compatible con el tipo de cuenta " + tipoCuenta);
        } else if ("CAU$S".equals(tipoCuenta) && !"USD".equals(tipoMoneda)) {
            throw new MonedasIncompatiblesException("La moneda " + tipoMoneda + " no es compatible con el tipo de cuenta " + tipoCuenta);
        } else if ("CA$".equals(tipoCuenta) && !("ARS".equals(tipoMoneda) || "USD".equals(tipoMoneda))) {
            throw new MonedasIncompatiblesException("La moneda " + tipoMoneda + " no es compatible con el tipo de cuenta " + tipoCuenta);
        }
    }

}
