package ar.edu.utn.frbb.tup.validator;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.exception.cuentas.TipoCuentaNoSoportadaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CuentaValidatorTest {

    // Crear una instancia del validador para usar en las pruebas
    private final CuentaValidator cuentaValidator = new CuentaValidator();

    @Test
    public void testValidateCuenta() {
        // Crear un objeto CuentaDto con datos válidos
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "ARS");
        // Verificar que no se lanzan excepciones para entradas válidas
        try {
            cuentaValidator.validateCuenta(cuentaDto);
        } catch (TipoCuentaNoSoportadaException | TipoMonedaNoSoportadaException | MonedasIncompatiblesException e) {
            // Si se lanza una excepción, la prueba falla
            throw new AssertionError("No debería haberse lanzado ninguna excepción", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testValidateMonedasIncompatibles() {
        // Crear un objeto CuentaDto con moneda incompatible para el tipo de cuenta
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "USD");
        // Verificar que se lanza una excepción de monedas incompatibles
        assertThrows(MonedasIncompatiblesException.class, () -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateTipoCuentaInvalida() {
        // Crear un objeto CuentaDto con un tipo de cuenta no soportado
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CAU$S", "ARS");
        // Verificar que se lanza una excepción de monedas incompatibles
        assertThrows(MonedasIncompatiblesException.class, () -> cuentaValidator.validateCuenta(cuentaDto));
    }

    @Test
    public void testValidateTipoMoneda() {
        // Crear un objeto CuentaDto con una moneda no soportada
        CuentaDto cuentaDto = new CuentaDto(12345678L, "CC$", "EURO");
        // Verificar que se lanza una excepción de tipo de moneda no soportada
        assertThrows(TipoMonedaNoSoportadaException.class, () -> cuentaValidator.validateCuenta(cuentaDto));
    }
}
