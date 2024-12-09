package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.clientes.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.exception.clientes.MenorDeEdadException;
import ar.edu.utn.frbb.tup.exception.clientes.TipoPersonaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private ClienteValidator clienteValidator;

    @InjectMocks
    private ClienteController clienteController;

    private ClienteDto clienteDtoMock;
    private Cliente clienteMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        clienteDtoMock = new ClienteDto(
                "Direccion",
                "Apellido",
                12345678L,
                "1990-01-01",
                "Nombre",
                "123456789",
                "Banco Test",
                "F"
        );

        clienteMock = new Cliente();
        clienteMock.setDni(12345678L);
        clienteMock.setNombre("Nombre");
        clienteMock.setApellido("Apellido");
    }

    @Test
    void testGetAllClientes() {
        when(clienteService.showClientes()).thenReturn(Arrays.asList(clienteMock));

        ResponseEntity<List<Cliente>> response = clienteController.getAllClientes();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(clienteMock, response.getBody().get(0));

        verify(clienteService, times(1)).showClientes();
    }

    @Test
    void testCreateCliente_Success() throws DatosIncorrectosException, TipoPersonaNoSoportadaException, ClienteAlreadyExistsException, MenorDeEdadException {
        doNothing().when(clienteValidator).validate(clienteDtoMock);
        when(clienteService.darDeAltaCliente(clienteDtoMock)).thenReturn(clienteMock);

        ResponseEntity<Cliente> response = clienteController.createCliente(clienteDtoMock);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(clienteMock, response.getBody());

        verify(clienteValidator, times(1)).validate(clienteDtoMock);
        verify(clienteService, times(1)).darDeAltaCliente(clienteDtoMock);
    }

    @Test
    void testCreateCliente_ValidationError() throws DatosIncorrectosException, ClienteAlreadyExistsException, TipoPersonaNoSoportadaException, MenorDeEdadException {
        doThrow(new DatosIncorrectosException("Error de validación"))
                .when(clienteValidator).validate(clienteDtoMock);

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> clienteController.createCliente(clienteDtoMock)
        );

        assertEquals("Error de validación", exception.getMessage());

        verify(clienteValidator, times(1)).validate(clienteDtoMock);
        verify(clienteService, never()).darDeAltaCliente(any(ClienteDto.class));
    }

    @Test
    void testCreateCliente_AlreadyExists() throws DatosIncorrectosException, ClienteAlreadyExistsException, MenorDeEdadException, TipoPersonaNoSoportadaException {
        doNothing().when(clienteValidator).validate(clienteDtoMock);
        when(clienteService.darDeAltaCliente(clienteDtoMock)).thenThrow(new ClienteAlreadyExistsException("Cliente ya existe"));

        ClienteAlreadyExistsException exception = assertThrows(
                ClienteAlreadyExistsException.class,
                () -> clienteController.createCliente(clienteDtoMock)
        );

        assertEquals("Cliente ya existe", exception.getMessage());

        verify(clienteValidator, times(1)).validate(clienteDtoMock);
        verify(clienteService, times(1)).darDeAltaCliente(clienteDtoMock);
    }

    @Test
    void testGetClientById_Success() throws ClienteNotFoundException {
        when(clienteService.buscarClientePorDni(12345678L)).thenReturn(clienteMock);

        ResponseEntity<Cliente> response = clienteController.getClientById(12345678L);

        assertNotNull(response);
        assertEquals(clienteMock, response.getBody());

        verify(clienteService, times(1)).buscarClientePorDni(12345678L);
    }

    @Test
    void testGetClientById_NotFound() throws ClienteNotFoundException {
        when(clienteService.buscarClientePorDni(99999999L)).thenThrow(new ClienteNotFoundException("Cliente no encontrado"));

        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> clienteController.getClientById(99999999L)
        );

        assertEquals("Cliente no encontrado", exception.getMessage());

        verify(clienteService, times(1)).buscarClientePorDni(99999999L);
    }
}
