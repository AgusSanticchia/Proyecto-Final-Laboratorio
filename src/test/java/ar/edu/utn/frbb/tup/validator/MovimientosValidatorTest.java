package ar.edu.utn.frbb.tup.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.controller.validator.MovimientosValidator;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.exception.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MovimientosValidatorTest {

    private MovimientosValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new MovimientosValidator();
    }

    // Tests para validación de movimientos
    @Test
    public void testValidateMovimientosNulo() {
        // Arrange
        MovimientosDto movimientosDto = null;

        // Act & Assert
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientos(null));
    }

    @Test
    public void testValidateMovimientosMontoNulo() {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(null, 1L, "USD");

        // Act & Assert
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientos(movimientosDto));
    }

    @Test
    public void testValidateMovimientosMontoNegativo() {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(-1000.0, 1L, "USD");

        // Act & Assert
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientos(movimientosDto));
    }

    @Test
    public void testValidateMovimientosTipoMonedaInvalido() {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "EURO"); // Tipo de moneda inválido

        // Act & Assert
        assertThrows(MonedasIncompatiblesException.class, () -> validator.validateMovimientos(movimientosDto));
    }

    @Test
    public void testValidateMovimientosExitoso() throws DatosIncorrectosException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");

        // Act & Assert
        // No exception should be thrown
        validator.validateMovimientos(movimientosDto);
    }

    // Tests para validación de transferencias
    @Test
    public void testValidateMovimientosTransferenciasNulo() {
        // Arrange
        MovimientosTransferenciasDto transferenciasDto = null;

        // Act & Assert
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientosTransferencias(null));
    }

    @Test
    public void testValidateMovimientosTransferenciasMontoNulo() {
        // Arrange
        MovimientosTransferenciasDto transferenciasDto = new MovimientosTransferenciasDto(1L, 2L, null, "ARS");

        // Act & Assert
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientosTransferencias(transferenciasDto));
    }

    @Test
    public void testValidateMovimientosTransferenciasMontoNegativo() {
        // Arrange
        MovimientosTransferenciasDto transferenciasDto = new MovimientosTransferenciasDto(1L, 2L, -1000.0, "ARS");

        // Act & Assert
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientosTransferencias(transferenciasDto));
    }

    @Test
    public void testValidateMovimientosTransferenciasMonedaInvalida() {
        // Arrange
        MovimientosTransferenciasDto transferenciasDto = new MovimientosTransferenciasDto(1L, 2L, 1000.0, "EURO"); // Moneda inválida


        // Act & Assert
        assertThrows(MonedasIncompatiblesException.class, () -> validator.validateMovimientosTransferencias(transferenciasDto));
    }

    @Test
    public void testValidateMovimientosTransferenciasCuentaIgual() {
        // Arrange
        MovimientosTransferenciasDto transferenciasDto = new MovimientosTransferenciasDto(1L, 1L, 1000.0, "ARS");

        // Act & Assert
        assertThrows(TipoCuentaNoSoportadaException.class, () -> validator.validateMovimientosTransferencias(transferenciasDto));
    }

    @Test
    public void testValidateMovimientosTransferenciasExitoso() throws DatosIncorrectosException, MonedasIncompatiblesException, TipoCuentaNoSoportadaException {
        // Arrange
        MovimientosTransferenciasDto transferenciasDto = new MovimientosTransferenciasDto(1L, 2L, 1000.0, "ARS");

        // Act & Assert
        // No exception should be thrown
        validator.validateMovimientosTransferencias(transferenciasDto);
    }
}
