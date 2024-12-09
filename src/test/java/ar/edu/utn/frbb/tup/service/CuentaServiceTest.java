package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    private CuentaDto cuentaDto;

    @BeforeEach
    public void setUp() {
        cuentaDto = new CuentaDto(12345678L, "CA$", "ARS");
    }

    @Test
    public void testDarDeAltaCuenta_Success() throws Exception {
        // Arrange
        when(cuentaDao.find(anyLong())).thenReturn(null);
        doNothing().when(clienteService).addCuentaToCliente(any(Cuenta.class), anyLong());

        // Act
        Cuenta result = cuentaService.darDeAltaCuenta(cuentaDto);

        // Assert
        assertNotNull(result);
        assertEquals(TipoCuenta.CAJA_AHORRO_PESOS, result.getTipoCuenta());
        assertEquals(TipoMoneda.PESOS, result.getTipoMoneda());
        assertEquals(12345678L, result.getDniTitular());

        verify(cuentaDao).save(any(Cuenta.class));
        verify(clienteService).addCuentaToCliente(any(Cuenta.class), eq(12345678L));
    }

    @Test
    public void testDarDeAltaCuenta_CuentaAlreadyExists() throws DatosIncorrectosException, ClienteNotFoundException, CuentaAlreadyExistsException {
        Cuenta cuentaExistente = new Cuenta();
        cuentaExistente.setNumeroCuenta(12345678L);


        lenient().when(cuentaDao.find(anyLong())).thenReturn(cuentaExistente);

        assertThrows(CuentaAlreadyExistsException.class, () ->
                cuentaService.darDeAltaCuenta(cuentaDto)
        );

        verify(cuentaDao, never()).save(any(Cuenta.class));
        verify(clienteService, never()).addCuentaToCliente  (any(Cuenta.class), anyLong());
    }


    @Test
    public void testShowCuentas() {
        List<Cuenta> cuentas = Arrays.asList(new Cuenta(), new Cuenta());
        when(cuentaDao.findAll()).thenReturn(cuentas);

        List<Cuenta> result = cuentaService.showCuentas();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cuentaDao).findAll();
    }

    @Test
    public void testFindById_CuentaExiste() throws CuentaNotExistException {
        Cuenta expectedCuenta = new Cuenta();
        when(cuentaDao.find(anyLong())).thenReturn(expectedCuenta);

        Cuenta result = cuentaService.findById(123L);

        assertNotNull(result);
        assertEquals(expectedCuenta, result);
        verify(cuentaDao).find(123L);
    }

    @Test
    public void testFindById_CuentaNoExiste() {
        when(cuentaDao.find(anyLong())).thenReturn(null);

        CuentaNotExistException exception = assertThrows(
                CuentaNotExistException.class,
                () -> cuentaService.findById(123L)
        );

        assertEquals("La cuenta no existe.", exception.getMessage());
    }

    @Test
    public void testDarDeAltaCuenta_ClienteNoEncontrado() throws Exception {

        doThrow(new ClienteNotFoundException("Cliente no encontrado"))
                .when(clienteService).addCuentaToCliente(any(Cuenta.class), anyLong());

        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> cuentaService.darDeAltaCuenta(cuentaDto)
        );

        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(clienteService).addCuentaToCliente(any(Cuenta.class), eq(12345678L));
        verify(cuentaDao, never()).save(any(Cuenta.class));
    }
}