package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.exception.cuentas.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaValidatorTest {

    private final CuentaValidator cuentaValidator = new CuentaValidator();

    @Test
    public void testValidateCuentaCorrecta() throws DatosIncorrectosException, TipoCuentaNoSoportadaException, MonedasIncompatiblesException {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "ARS");
        cuentaValidator.validateCuenta(cuentaDto); // No debe lanzar excepciones
    }

    @Test
    public void testValidateCuentaTipoCuentaInvalida() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "INVALID", "ARS");
        assertThrows(TipoCuentaNoSoportadaException.class, () -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuentaMonedaIncompatible() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "USD");
        assertThrows(MonedasIncompatiblesException.class, () -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuentaDniInvalido() {
        CuentaDto cuentaDto = new CuentaDto(0L, "CC$", "ARS");
        assertThrows(DatosIncorrectosException.class, () -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuentaTipoMonedaFaltante() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", null);
        assertThrows(DatosIncorrectosException.class, () -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuentaDniNoOchoDigitos() {
        CuentaDto cuentaDto = new CuentaDto(123L, "CC$", "ARS");
        assertThrows(DatosIncorrectosException.class, () -> cuentaValidator.validateCuenta(cuentaDto));
    }
}
