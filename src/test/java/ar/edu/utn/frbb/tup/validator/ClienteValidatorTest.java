package ar.edu.utn.frbb.tup.validator;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.exception.MenorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.TipoMonedaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.TipoPersonaNoSoportadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClienteValidatorTest {

    @InjectMocks
    private ClienteValidator clienteValidator;

    private ClienteDto clienteDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidCliente() {
        // Creacion de un cliente válido
        clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");

        assertDoesNotThrow(() -> clienteValidator.validate(clienteDto));
    }

    @Test
    public void testInvalidTipoPersona() {
        // TipoPersona debe setearse a 'F' o 'J'
        clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "X");

        assertThrows(TipoMonedaNoSoportadaException.class, () -> clienteValidator.validate(clienteDto));
    }

    @Test
    public void testInvalidFechaDeNacimientoFormat() {
        // Invalid date format
        clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "01/01/1990", "Nombre", "123456789", "Banco Test", "F");

        assertThrows(DatosIncorrectosException.class, () -> clienteValidator.validate(clienteDto));
    }

    @Test
    public void testMenorDeEdad() {
        // A customer who is under 18 years old
        clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, LocalDate.now().minusYears(17).toString(), "Nombre", "123456789", "Banco Test", "F");

        assertThrows(MenorDeEdadException.class, () -> clienteValidator.validate(clienteDto));
    }
}
