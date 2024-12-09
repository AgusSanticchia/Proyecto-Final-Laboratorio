package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MovimientosValidatorTest {

    private MovimientosValidator movimientosValidator;

    @BeforeEach
    public void setUp() {
        movimientosValidator = new MovimientosValidator();
    }

    @Test
    public void testValidateMovimientos_MontoValido() {
        MovimientosDto movimientosDto = new MovimientosDto(100.0, 123L, "ARS");
        assertDoesNotThrow(() -> movimientosValidator.validateMovimientos(movimientosDto));
    }

    @Test
    public void testValidateMovimientos_MontoNulo() {
        MovimientosDto movimientosDto = new MovimientosDto(null, 123L, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientos(movimientosDto)
        );

        assertEquals("Error: Ingrese un monto", exception.getMessage());
    }

    @Test
    public void testValidateMovimientos_MontoCero() {
        MovimientosDto movimientosDto = new MovimientosDto(0.0, 123L, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientos(movimientosDto)
        );

        assertEquals("Error: El monto debe ser mayor que 0", exception.getMessage());
    }

    @Test
    public void testValidateMovimientos_MontoNegativo() {
        MovimientosDto movimientosDto = new MovimientosDto(-100.0, 123L, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientos(movimientosDto)
        );

        assertEquals("Error: El monto debe ser mayor que 0", exception.getMessage());
    }

    @Test
    public void testValidateMovimientosTransferencias_DatosValidos() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(123L, 456L, 100.0, "ARS");

        assertDoesNotThrow(() -> movimientosValidator.validateMovimientosTransferencias(transferencia));
    }

    @Test
    public void testValidateMovimientosTransferencias_MontoNulo() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(123L, 456L, null, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientosTransferencias(transferencia)
        );

        assertEquals("Error: Ingrese un monto", exception.getMessage());
    }

    @Test
    public void testValidateMovimientosTransferencias_MontoCero() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(123L, 456L, 0.0, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientosTransferencias(transferencia)
        );

        assertEquals("Error: El monto debe ser mayor que 0", exception.getMessage());
    }

    @Test
    public void testValidateMovimientosTransferencias_MontoNegativo() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(123L, 456L, -100.0, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientosTransferencias(transferencia)
        );

        assertEquals("Error: El monto debe ser mayor que 0", exception.getMessage());
    }

    @Test
    public void testValidateMovimientosTransferencias_CuentasIguales() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(123L, 123L, 100.0, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientosTransferencias(transferencia)
        );

        assertEquals("Error: La cuenta de origen y la cuenta de destino no pueden ser iguales", exception.getMessage());
    }

    @Test
    public void testValidateMovimientosTransferencias_CuentaOrigenCero() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(0L, 456L, 100.0, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientosTransferencias(transferencia)
        );

        assertEquals("Error: La cuenta de origen o la cuenta de destino no pueden ser 0", exception.getMessage());
    }

    @Test
    public void testValidateMovimientosTransferencias_CuentaDestinoCero() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(123L, 0L, 100.0, "ARS");

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosValidator.validateMovimientosTransferencias(transferencia)
        );

        assertEquals("Error: La cuenta de origen o la cuenta de destino no pueden ser 0", exception.getMessage());
    }
}
