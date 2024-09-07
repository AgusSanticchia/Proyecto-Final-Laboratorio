package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.model.exception.TipoMonedaNoSoportadaException;
import org.springframework.stereotype.Component;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;

@Component
public class CuentaValidator {

    public void validateCuenta(CuentaDto cuentaDto) {
        validateTipoCuenta(cuentaDto);
        validateMoneda(cuentaDto);
    }

    private void validateTipoCuenta(CuentaDto cuentaDto) {
        if (!"CC$".equals(cuentaDto.getTipoCuenta()) && !"CAU$S".equals(cuentaDto.getTipoCuenta()) && !"CA$".equals(cuentaDto.getTipoCuenta())) {
            throw new IllegalArgumentException("El tipo de cuenta no es correcto");
        }
    }

    private void validateMoneda(CuentaDto cuentaDto) throws TipoMonedaNoSoportadaException {
        if (!"ARS".equals(cuentaDto.getTipoMoneda()) && !"USD".equals(cuentaDto.getTipoMoneda())) {
            throw new TipoMonedaNoSoportadaException("El tipo de moneda no es correcto");
        }
    }
}
