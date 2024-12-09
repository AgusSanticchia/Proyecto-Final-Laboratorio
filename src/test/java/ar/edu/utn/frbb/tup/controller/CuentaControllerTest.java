package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.exception.cuentas.*;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.service.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaControllerTest {

    @Mock
    private CuentaService cuentaService;

    @Mock
    private CuentaValidator cuentaValidator;

    @InjectMocks
    private CuentaController cuentaController;

    private CuentaDto cuentaDtoMock;
    private Cuenta cuentaMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cuentaDtoMock = new CuentaDto(12345678L, "CA$", "ARS");
        cuentaMock = new Cuenta();
        cuentaMock.setNumeroCuenta(12345678L);
    }

    @Test
    void testCreateCuenta_Success() throws Exception {
        doNothing().when(cuentaValidator).validateCuenta(cuentaDtoMock);
        when(cuentaService.darDeAltaCuenta(cuentaDtoMock)).thenReturn(cuentaMock);

        ResponseEntity<Cuenta> response = cuentaController.createCuenta(cuentaDtoMock);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cuentaMock, response.getBody());

        verify(cuentaValidator, times(1)).validateCuenta(cuentaDtoMock);
        verify(cuentaService, times(1)).darDeAltaCuenta(cuentaDtoMock);
    }

    @Test
    void testCreateCuenta_ValidationError() throws DatosIncorrectosException, TipoCuentaNoSoportadaException, MonedasIncompatiblesException {
        doThrow(new DatosIncorrectosException("Datos incorrectos")).when(cuentaValidator).validateCuenta(cuentaDtoMock);

        DatosIncorrectosException exception = assertThrows(DatosIncorrectosException.class,
                () -> cuentaController.createCuenta(cuentaDtoMock)
        );

        assertEquals("Datos incorrectos", exception.getMessage());

        verify(cuentaValidator, times(1)).validateCuenta(cuentaDtoMock);
        verifyNoInteractions(cuentaService);
    }



    @Test
    void testCreateCuenta_ClienteNotFound() throws Exception {
        doNothing().when(cuentaValidator).validateCuenta(cuentaDtoMock);
        doThrow(new ClienteNotFoundException("Cliente no encontrado")).when(cuentaService).darDeAltaCuenta(cuentaDtoMock);

        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> cuentaController.createCuenta(cuentaDtoMock)
        );

        assertEquals("Cliente no encontrado", exception.getMessage());

        verify(cuentaValidator, times(1)).validateCuenta(cuentaDtoMock);
        verify(cuentaService, times(1)).darDeAltaCuenta(cuentaDtoMock);
    }

    @Test
    void testGetCuentaById_Success() throws CuentaNotExistException {
        when(cuentaService.findById(12345678L)).thenReturn(cuentaMock);

        ResponseEntity<Cuenta> response = cuentaController.getCuentaById(12345678L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cuentaMock, response.getBody());

        verify(cuentaService, times(1)).findById(12345678L);
    }

    @Test
    void testGetCuentaById_NotFound() throws CuentaNotExistException {
        when(cuentaService.findById(12345678L)).thenThrow(new CuentaNotExistException("La cuenta no existe"));

        CuentaNotExistException exception = assertThrows(
                CuentaNotExistException.class,
                () -> cuentaController.getCuentaById(12345678L)
        );

        assertEquals("La cuenta no existe", exception.getMessage());

        verify(cuentaService, times(1)).findById(12345678L);
    }

    @Test
    void testGetAllCuentas_Success() {
        when(cuentaService.showCuentas()).thenReturn(List.of(cuentaMock));

        ResponseEntity<List<Cuenta>> response = cuentaController.getAllCuentas();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(cuentaMock, response.getBody().get(0));

        verify(cuentaService, times(1)).showCuentas();
    }

    @Test
    void testCreateCuenta_MonedasIncompatibles() throws DatosIncorrectosException, TipoCuentaNoSoportadaException, MonedasIncompatiblesException, ClienteNotFoundException, CuentaAlreadyExistsException {
        doNothing().when(cuentaValidator).validateCuenta(cuentaDtoMock);

        when(cuentaService.darDeAltaCuenta(cuentaDtoMock))
                .thenThrow(new MonedasIncompatiblesException("Monedas incompatibles"));

        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> cuentaController.createCuenta(cuentaDtoMock)
        );

        assertEquals("Monedas incompatibles", exception.getMessage());

        verify(cuentaValidator, times(1)).validateCuenta(cuentaDtoMock);
        verify(cuentaService, times(1)).darDeAltaCuenta(cuentaDtoMock);
    }

    @Test
    void testCreateCuenta_TipoCuentaNoSoportada() throws Exception {
        doThrow(new TipoCuentaNoSoportadaException("Tipo de cuenta no soportada")).when(cuentaValidator).validateCuenta(cuentaDtoMock);

        TipoCuentaNoSoportadaException exception = assertThrows(
                TipoCuentaNoSoportadaException.class,
                () -> cuentaController.createCuenta(cuentaDtoMock)
        );

        assertEquals("Tipo de cuenta no soportada", exception.getMessage());

        verify(cuentaValidator, times(1)).validateCuenta(cuentaDtoMock);
        verifyNoInteractions(cuentaService);
    }
}
