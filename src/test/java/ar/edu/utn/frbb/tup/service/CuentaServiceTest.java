package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.*;
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
    public void darDeAltaCuenta_Success() throws ClienteAlreadyExistsException, TipoCuentaNoSoportadaException, CuentaAlreadyExistsException, ClienteNotFoundException, CuentaNotExistException {
        when(cuentaDao.find(anyLong())).thenReturn(null);
        doNothing().when(clienteService).addCuentaToCliente(any(Cuenta.class), anyLong());

        Cuenta result = cuentaService.darDeAltaCuenta(cuentaDto);

        assertNotNull(result);
        assertEquals(TipoCuenta.CAJA_AHORRO_PESOS, result.getTipoCuenta());
        assertEquals(TipoMoneda.PESOS, result.getTipoMoneda());
        assertEquals(12345678L, result.getDniTitular());

        verify(cuentaDao, times(1)).save(any(Cuenta.class));
        verify(clienteService, times(1)).addCuentaToCliente(any(Cuenta.class), eq(12345678L));
    }

    @Test
    public void darDeAltaCuenta_CuentaAlreadyExists() throws ClienteAlreadyExistsException, ClienteNotFoundException, CuentaNotExistException {
        when(cuentaDao.find(anyLong())).thenReturn(new Cuenta());

        assertThrows(CuentaAlreadyExistsException.class, () -> {
            cuentaService.darDeAltaCuenta(cuentaDto);
        });

        verify(cuentaDao, never()).save(any(Cuenta.class));
        verify(clienteService, never()).addCuentaToCliente(any(Cuenta.class), anyLong());
    }

    @Test
    public void darDeAltaCuenta_TipoCuentaNoSoportada() throws ClienteAlreadyExistsException, ClienteNotFoundException {
        cuentaDto = new CuentaDto(12345678L, "CA$", "USD");

        assertThrows(TipoCuentaNoSoportadaException.class, () -> {
            cuentaService.darDeAltaCuenta(cuentaDto);
        });

        verify(cuentaDao, never()).save(any(Cuenta.class));
        verify(clienteService, never()).addCuentaToCliente(any(Cuenta.class), anyLong());
    }

    @Test
    public void darDeAltaCuenta() throws Exception, ClienteAlreadyExistsException, ClienteNotFoundException, CuentaNotExistException {
        when(cuentaDao.find(anyLong())).thenReturn(null);
        doThrow(new IllegalArgumentException("Invalid argument")).when(clienteService).addCuentaToCliente(any(Cuenta.class), anyLong());

        assertThrows(IllegalArgumentException.class, () -> {
            cuentaService.darDeAltaCuenta(cuentaDto);
        });

        verify(cuentaDao, never()).save(any(Cuenta.class));
    }

    @Test
    public void darDeAltaCuentaDtosNulos() throws ClienteAlreadyExistsException, ClienteNotFoundException {
        CuentaDto nullDto = new CuentaDto(0L, null, null);

        assertThrows(IllegalArgumentException.class, () -> {
            cuentaService.darDeAltaCuenta(nullDto);
        });

        verify(cuentaDao, never()).save(any(Cuenta.class));
        verify(clienteService, never()).addCuentaToCliente(any(Cuenta.class), anyLong());
    }


    @Test
    public void showCuentas() {
        List<Cuenta> expectedCuentas = Arrays.asList(new Cuenta(), new Cuenta());
        when(cuentaDao.findAll()).thenReturn(expectedCuentas);

        List<Cuenta> result = cuentaService.showCuentas();

        assertEquals(expectedCuentas, result);
        verify(cuentaDao, times(1)).findAll();
    }

    @Test
    public void encontrarCuentaExistente() throws CuentaNotExistException {
        long accountId = 123L;
        Cuenta expectedCuenta = new Cuenta();
        when(cuentaDao.find(accountId)).thenReturn(expectedCuenta);

        Cuenta result = cuentaService.findById(accountId);

        assertEquals(expectedCuenta, result);
        verify(cuentaDao, times(1)).find(accountId);
    }

    @Test
    public void encontrarCuentaInexistente() throws CuentaNotExistException {
        long accountId = 123L;
        when(cuentaDao.find(accountId)).thenReturn(null);

        Cuenta result = cuentaService.findById(accountId);

        assertNull(result);
        verify(cuentaDao, times(1)).find(accountId);
    }

    @Test
    public void tipoCuentaEstaSoportadaDatosValidos() {
        Cuenta cuentaPesosCA = new Cuenta();
        cuentaPesosCA.setTipoMoneda(TipoMoneda.PESOS);
        cuentaPesosCA.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);
        assertTrue(cuentaService.tipoCuentaEstaSoportada(cuentaPesosCA));

        Cuenta cuentaPesosCC = new Cuenta();
        cuentaPesosCC.setTipoMoneda(TipoMoneda.PESOS);
        cuentaPesosCC.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);
        assertTrue(cuentaService.tipoCuentaEstaSoportada(cuentaPesosCC));

        Cuenta cuentaDolaresCA = new Cuenta();
        cuentaDolaresCA.setTipoMoneda(TipoMoneda.DOLARES);
        cuentaDolaresCA.setTipoCuenta(TipoCuenta.CAJA_AHORRO_DOLAR_US);
        assertTrue(cuentaService.tipoCuentaEstaSoportada(cuentaDolaresCA));
    }

    @Test
    public void tipoCuentaEstaSoportadaDatosInvalidos() {
        Cuenta cuentaDolaresCC = new Cuenta();
        cuentaDolaresCC.setTipoMoneda(TipoMoneda.DOLARES);
        cuentaDolaresCC.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);
        assertFalse(cuentaService.tipoCuentaEstaSoportada(cuentaDolaresCC));

        Cuenta cuentaPesosCAD = new Cuenta();
        cuentaPesosCAD.setTipoMoneda(TipoMoneda.PESOS);
        cuentaPesosCAD.setTipoCuenta(TipoCuenta.CAJA_AHORRO_DOLAR_US);
        assertFalse(cuentaService.tipoCuentaEstaSoportada(cuentaPesosCAD));
    }

    // Agregamos pruebas adicionales para cobertura completa
    @Test
    public void darDeAltaCuentaDatosValidos() throws ClienteAlreadyExistsException, TipoCuentaNoSoportadaException, CuentaAlreadyExistsException, ClienteNotFoundException, CuentaNotExistException {
        CuentaDto validDto = new CuentaDto(12345678L, "CC$", "ARS");

        when(cuentaDao.find(anyLong())).thenReturn(null);
        doNothing().when(clienteService).addCuentaToCliente(any(Cuenta.class), anyLong());

        Cuenta result = cuentaService.darDeAltaCuenta(validDto);

        assertNotNull(result);
        assertEquals(TipoCuenta.CUENTA_CORRIENTE_PESOS, result.getTipoCuenta());
        assertEquals(TipoMoneda.PESOS, result.getTipoMoneda());
        assertEquals(12345678L, result.getDniTitular());

        verify(cuentaDao, times(1)).save(any(Cuenta.class));
        verify(clienteService, times(1)).addCuentaToCliente(any(Cuenta.class), eq(12345678L));
    }

    @Test
    public void tipoCuentaEstaSoportada_TipoMonedaNoEspecificada() {
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoMoneda(null);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);
        assertFalse(cuentaService.tipoCuentaEstaSoportada(cuenta));
    }
}
