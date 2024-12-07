package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
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

    @InjectMocks
    private ClienteController clienteController;

    private Cliente clienteMock;
    private ClienteDto clienteDtoMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente();
        clienteMock.setDni(12345678L);
        clienteMock.setNombre("Juan");
        clienteMock.setApellido("Perez");
    }

    @Test
    void testGetAllClientes() {
        when(clienteService.showClientes()).thenReturn(Arrays.asList(clienteMock));


        ResponseEntity<List<Cliente>> response = clienteController.getAllClientes();


        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(clienteMock, response.getBody().get(0));


        verify(clienteService, times(1)).showClientes();
    }

    @Test
    void testCreateCliente() throws DatosIncorrectosException, TipoPersonaNoSoportadaException, ClienteAlreadyExistsException, MenorDeEdadException {

        when(clienteService.darDeAltaCliente(clienteDtoMock)).thenReturn(clienteMock);

        ResponseEntity<Cliente> response = clienteController.createCliente(clienteDtoMock);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(clienteMock, response.getBody());

        verify(clienteService, times(1)).darDeAltaCliente(clienteDtoMock);
    }

    @Test
    void testGetClientById() throws ClienteNotFoundException {

        long dni = 12345678L;
        when(clienteService.buscarClientePorDni(dni)).thenReturn(clienteMock);

        ResponseEntity<Cliente> response = clienteController.getClientById(dni);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(clienteMock, response.getBody());

        verify(clienteService, times(1)).buscarClientePorDni(dni);
    }

    @Test
    void testGetClientByIdNotFound() throws ClienteNotFoundException {
        long dni = 99999999L;
        when(clienteService.buscarClientePorDni(dni)).thenThrow(new ClienteNotFoundException("Cliente no encontrado"));

        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> clienteController.getClientById(dni)
        );

        assertEquals("Cliente no encontrado", exception.getMessage());

        verify(clienteService, times(1)).buscarClientePorDni(dni);
    }
}
