package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MovimientosValidatorTest {

    private final MovimientosValidator validator = new MovimientosValidator();

    @Test
    public void testValidateMovimientosCorrectos() throws DatosIncorrectosException {
        MovimientosDto movimientosDto = new MovimientosDto(100.0, 123L, "ARS");
        validator.validateMovimientos(movimientosDto); // No debe lanzar excepciones
    }

    @Test
    public void testValidateMovimientosMontoNulo() {
        MovimientosDto movimientosDto = new MovimientosDto(null, 123L, "ARS");
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientos(movimientosDto));
    }

    @Test
    public void testValidateMovimientosMontoNegativo() {
        MovimientosDto movimientosDto = new MovimientosDto(-10.0, 123L, "ARS");
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientos(movimientosDto));
    }

    @Test
    public void testValidateTransferenciaCorrecta() throws DatosIncorrectosException {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(456L, 123L, 100.0, "ARS", "Transferencia válida");
        validator.validateMovimientosTransferencias(transferenciaDto); // No debe lanzar excepciones
    }

    @Test
    public void testValidateTransferenciaMontoNulo() {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(456L, 123L, null, "ARS", "Monto nulo");
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientosTransferencias(transferenciaDto));
    }

    @Test
    public void testValidateTransferenciaCuentasIguales() {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(123L, 123L, 100.0, "ARS", "Cuentas iguales");
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientosTransferencias(transferenciaDto));
    }

    @Test
    public void testValidateTransferenciaCuentasCero() {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(0L, 0L, 100.0, "ARS", "Cuentas cero");
        assertThrows(DatosIncorrectosException.class, () -> validator.validateMovimientosTransferencias(transferenciaDto));
    }
}
