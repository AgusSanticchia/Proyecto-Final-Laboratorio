package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.clientes.MenorDeEdadException;
import ar.edu.utn.frbb.tup.exception.clientes.TipoPersonaNoSoportadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClienteValidatorTest {

    private ClienteValidator clienteValidator;

    @BeforeEach
    public void setUp() {
        clienteValidator = new ClienteValidator();
    }

    @Test
    public void testValidCliente() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertDoesNotThrow(() -> clienteValidator.validate(clienteDto));
    }

    @Test
    public void testNombreVacio() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un nombre");
    }

    @Test
    public void testApellidoVacio() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un apellido");
    }

    @Test
    public void testDniInvalido() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 123L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: El dni debe tener 8 digitos");
    }

    @Test
    public void testDireccionVacia() {
        ClienteDto clienteDto = new ClienteDto("", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese una direccion");
    }

    @Test
    public void testFechaNacimientoVacia() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese una fecha de nacimiento");
    }

    @Test
    public void testTelefonoVacio() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un telefono");
    }

    @Test
    public void testBancoVacio() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un banco");
    }

    @Test
    public void testTipoPersonaInvalido() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "A");
        assertThrows(DatosIncorrectosException.class, () -> clienteValidator.validate(clienteDto), "El tipo de persona no es correcto. Debe ser 'F' o 'J'.");
    }

    @Test
    public void testFechaNacimientoFormatoIncorrecto() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "01/01/1990", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(DatosIncorrectosException.class, () -> clienteValidator.validate(clienteDto), "Fecha de nacimiento incorrecta. Debe estar en formato yyyy-MM-dd");
    }

    @Test
    public void testMenorDeEdad() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, LocalDate.now().minusYears(17).toString(), "Nombre", "123456789", "Banco Test", "F");
        assertThrows(MenorDeEdadException.class, () -> clienteValidator.validate(clienteDto), "El cliente debe ser mayor de edad");
    }

    @Test
    public void testEdadValida() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, LocalDate.now().minusYears(20).toString(), "Nombre", "123456789", "Banco Test", "F");
        assertDoesNotThrow(() -> clienteValidator.validate(clienteDto));
    }
    @Test
    public void testDniCero() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 0L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un dni");
    }

    @Test
    public void testDniFueraDeRangoBajo() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 9999999L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: El dni debe tener 8 digitos");
    }

    @Test
    public void testDniFueraDeRangoAlto() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 100000000L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: El dni debe tener 8 digitos");
    }

    @Test
    public void testTipoPersonaNulo() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", null);
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un tipo de persona");
    }

    @Test
    public void testTipoPersonaVacio() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un tipo de persona");
    }

    @Test
    public void testBancoNulo() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", null, "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un banco");
    }

    @Test
    public void testTipoPersonaFemeninaValida() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertDoesNotThrow(() -> clienteValidator.validate(clienteDto));
    }

    @Test
    public void testTipoPersonaJuridicaValida() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "J");
        assertDoesNotThrow(() -> clienteValidator.validate(clienteDto));
    }

    @Test
    public void testNombreNulo() {
        ClienteDto clienteDto = new ClienteDto(
                "Direccion", "Apellido", 12345678L, "1990-01-01", null, "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un nombre");
    }

    @Test
    public void testDniFueraDeRango() {
        ClienteDto clienteDto = new ClienteDto(
                "Direccion", "Apellido", 9999999L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: El dni debe tener 8 digitos");
    }

    @Test
    public void testDireccionNula() {
        ClienteDto clienteDto = new ClienteDto(
                null, "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese una direccion");
    }

    @Test
    public void testFechaNacimientoInvalida() {
        ClienteDto clienteDto = new ClienteDto(
                "Direccion", "Apellido", 12345678L, "invalid-date", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(DatosIncorrectosException.class, () -> clienteValidator.validate(clienteDto), "Formato de fecha de nacimiento incorrecto");
    }

    @Test
    public void testApellidoNulo() {
        ClienteDto clienteDto = new ClienteDto("Direccion", null, 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un apellido");
    }

    @Test
    public void testTelefonoNulo() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", null, "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese un telefono");
    }

    @Test
    public void testValidClienteJuridico() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, "1990-01-01", "Nombre", "123456789", "Banco Test", "J");
        assertDoesNotThrow(() -> clienteValidator.validate(clienteDto));
    }

    @Test
    public void testEdadExactamente18() {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, eighteenYearsAgo.toString(), "Nombre", "123456789", "Banco Test", "F");
        assertDoesNotThrow(() -> clienteValidator.validate(clienteDto));
    }

    @Test
    public void testFechaNacimientoNull() {
        ClienteDto clienteDto = new ClienteDto("Direccion", "Apellido", 12345678L, null, "Nombre", "123456789", "Banco Test", "F");
        assertThrows(IllegalArgumentException.class, () -> clienteValidator.validate(clienteDto), "Error: Ingrese una fecha de nacimiento");
    }

}
