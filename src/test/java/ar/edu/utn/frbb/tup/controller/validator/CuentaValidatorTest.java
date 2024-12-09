package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.cuentas.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaValidatorTest {

    private final CuentaValidator cuentaValidator = new CuentaValidator();

    @Test
    public void testValidateCuenta_DatosValidos() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "ARS");

        assertDoesNotThrow(() -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuenta_TipoCuentaNull() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, null, "ARS");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Error: Ingrese un tipo de cuenta", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_TipoCuentaVacio() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "", "ARS");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Error: Ingrese un tipo de cuenta", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_DniCero() {
        CuentaDto cuentaDto = new CuentaDto(0L, "CC$", "ARS");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Error: Ingrese un dni", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_DniMenorA8Digitos() {
        CuentaDto cuentaDto = new CuentaDto(123L, "CC$", "ARS");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Error: El dni debe tener 8 digitos", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_DniMayorA8Digitos() {
        CuentaDto cuentaDto = new CuentaDto(123456789L, "CC$", "ARS");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Error: El dni debe tener 8 digitos", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_TipoMonedaNull() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Error: Ingrese un tipo de moneda", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_TipoMonedaVacia() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Error: Ingrese un tipo de moneda", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_MonedaIncompatible_CCUSD() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "USD");

        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Moneda incompatible: USD para tipo de cuenta CC$", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_MonedaIncompatible_CAUARS() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CAU$S", "ARS");

        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Moneda incompatible: ARS para tipo de cuenta CAU$S", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_MonedaIncompatible_CA() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CA$", "EURO");

        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Moneda incompatible: EURO para tipo de cuenta CA$", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_TipoCuentaNoSoportada() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "INVALIDO", "ARS");

        TipoCuentaNoSoportadaException exception = assertThrows(
                TipoCuentaNoSoportadaException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );

        assertEquals("Error: Tipo de cuenta no soportado", exception.getMessage());
    }
    @Test
    public void testValidateCuenta_ValidCCARS() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "ARS");
        assertDoesNotThrow(() -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuenta_ValidCAUUSD() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CAU$S", "USD");
        assertDoesNotThrow(() -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuenta_ValidCAARS() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CA$", "ARS");
        assertDoesNotThrow(() -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuenta_ValidCAUSD() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CA$", "USD");
        assertDoesNotThrow(() -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuenta_TipoCuentaNulo() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, null, "ARS");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );
        assertEquals("Error: Ingrese un tipo de cuenta", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_TipoMonedaNula() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );
        assertEquals("Error: Ingrese un tipo de moneda", exception.getMessage());
    }
    @Test
    public void testValidateCuenta_DniMenor8Digitos() {
        CuentaDto cuentaDto = new CuentaDto(123L, "CC$", "ARS");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );
        assertEquals("Error: El dni debe tener 8 digitos", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_DniMayor8Digitos() {
        CuentaDto cuentaDto = new CuentaDto(123456789L, "CC$", "ARS");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );
        assertEquals("Error: El dni debe tener 8 digitos", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_MonedaIncompatibleCCUSD() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "USD");
        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );
        assertEquals("Moneda incompatible: USD para tipo de cuenta CC$", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_MonedaIncompatibleCAUSD() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CA$", "EUR");
        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );
        assertEquals("Moneda incompatible: EUR para tipo de cuenta CA$", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_MonedaIncompatibleCAUARS() {
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CAU$S", "ARS");
        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> cuentaValidator.validateCuenta(cuentaDto)
        );
        assertEquals("Moneda incompatible: ARS para tipo de cuenta CAU$S", exception.getMessage());
    }

    @Test
    public void testValidateCuenta_DniExacto8Digitos() {
        CuentaDto cuentaDto = new CuentaDto(10000000L, "CC$", "ARS");
        assertDoesNotThrow(() -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateCuenta_DniMaximo8Digitos() {
        CuentaDto cuentaDto = new CuentaDto(99999999L, "CC$", "ARS");
        assertDoesNotThrow(() -> cuentaValidator.validateCuenta(cuentaDto));
    }

}
