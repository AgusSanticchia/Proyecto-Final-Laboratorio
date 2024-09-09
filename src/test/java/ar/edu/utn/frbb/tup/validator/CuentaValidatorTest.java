package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.exception.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.TipoMonedaNoSoportadaException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CuentaValidatorTest {

    private final CuentaValidator cuentaValidator = new CuentaValidator();

    @Test
    public void testValidateCuenta_ValidInput_NoExceptionThrown() throws TipoCuentaNoSoportadaException {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "ARS");
        cuentaValidator.validateCuenta(cuentaDto);
        // No exceptions should be thrown
    }

    @Test
    public void testValidateTipoCuenta_InvalidType_ExceptionThrown() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CAU$S", "ARS");
        assertThrows(TipoCuentaNoSoportadaException.class, () -> {
            cuentaValidator.validateCuenta(cuentaDto);
        });
    }

    @Test
    public void testValidateTipoMoneda_InvalidCurrency_ExceptionThrown() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "ARS");
        assertThrows(TipoMonedaNoSoportadaException.class, () -> {
            cuentaValidator.validateCuenta(cuentaDto);
        });
    }

    @Test
    public void testValidateMonedasIncompatibles_ExceptionThrown() {
        // En tu código actual no hay manejo explícito para monedas incompatibles,
        // así que este test es solo un ejemplo si lo necesitas en el futuro.
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "ARS");
        // Cambia esto cuando manejes incompatibilidad de monedas si es necesario.
        assertThrows(MonedasIncompatiblesException.class, () -> {
            cuentaValidator.validateCuenta(cuentaDto);
        });
    }
}
