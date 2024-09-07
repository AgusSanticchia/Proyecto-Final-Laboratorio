package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.model.exception.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovimientosServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private BanelcoService banelcoService;

    @InjectMocks
    private MovimientosService movimientosService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Test de transferencia

    @Test
    public void transferirMonedasIncompatiblesTest() {
        // Dado
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(4321L, 1234L, 1000.0, "USD");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(1234L);
        cuentaOrigen.setBalance(3000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setNumeroCuenta(4321L);
        cuentaDestino.setBalance(2000.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1234L)).thenReturn(cuentaOrigen);
        when(cuentaDao.find(4321L)).thenReturn(cuentaDestino);

        // Cuando y Entonces
        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.transferir(transferencia));
    }

    @Test
    public void testTransferirFondosInsuficientes() throws CuentaNotFoundException {
        // Dado
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(4321L, 1234L, 1000.0, "ARS");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(1234L);
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1234L)).thenReturn(cuentaOrigen);
        when(cuentaDao.find(4321L)).thenReturn(new Cuenta());

        // Cuando y Entonces
        assertThrows(FondosInsuficientesException.class, () -> movimientosService.transferir(transferencia));
    }

    @Test
    public void testTransferirFallaConBanelco() throws CuentaNotFoundException, MonedasIncompatiblesException {
        // Dado
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(4321L, 1234L, 1000.0, "USD");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(1234L);
        cuentaOrigen.setBalance(3000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(1234L)).thenReturn(cuentaOrigen);
        when(cuentaDao.find(4321L)).thenReturn(null); // Cuenta destino no existe

        // Simulamos que la transferencia externa falla
        when(banelcoService.realizarTransferenciaBanelco(anyLong(), anyLong(), anyDouble(), anyString())).thenReturn(false);

        // Cuando y Entonces
        assertThrows(CuentaNotFoundException.class, () -> movimientosService.transferir(transferencia));

        // Verificamos que la transferencia vía Banelco fue invocada
        verify(banelcoService).realizarTransferenciaBanelco(1234L, 4321L, 1000.0, "USD");
    }

    @Test
    public void testTransferirConComisionPesos() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Dado
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(4321L, 1234L, 2000000.0, "ARS");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(1234L);
        cuentaOrigen.setBalance(3000000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setNumeroCuenta(4321L);
        cuentaDestino.setBalance(500000.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1234L)).thenReturn(cuentaOrigen);
        when(cuentaDao.find(4321L)).thenReturn(cuentaDestino);

        // Cuando
        Movimientos movimiento = movimientosService.transferir(transferencia);

        // Entonces
        assertNotNull(movimiento);
        assertEquals(2000000.0, movimiento.getMonto());
        assertEquals(TipoOperacion.TRANSFERENCIA, movimiento.getTipoOperacion());
        assertEquals(980000.0, cuentaOrigen.getBalance()); // Se aplicó un cargo de 2% ($40,000)
        assertEquals(2500000.0, cuentaDestino.getBalance());
    }

    @Test
    public void testTransferirConComisionDolares() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Dado
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(4321L, 1234L, 6000.0, "USD");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(1234L);
        cuentaOrigen.setBalance(10000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.DOLARES);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setNumeroCuenta(4321L);
        cuentaDestino.setBalance(5000.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(1234L)).thenReturn(cuentaOrigen);
        when(cuentaDao.find(4321L)).thenReturn(cuentaDestino);

        // Cuando
        Movimientos movimiento = movimientosService.transferir(transferencia);

        // Entonces
        assertNotNull(movimiento);
        assertEquals(6000.0, movimiento.getMonto());
        assertEquals(TipoOperacion.TRANSFERENCIA, movimiento.getTipoOperacion());
        assertEquals(3950.0, cuentaOrigen.getBalance()); // Se aplicó un cargo de 0.5% ($30)
        assertEquals(11000.0, cuentaDestino.getBalance());
    }


    //Test para retiros
    @Test
    public void retirarExitosoTest() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Arrange
        MovimientosDto dto = new MovimientosDto(50.0, 1L, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(100.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1L)).thenReturn(cuenta);

        // Act
        Movimientos resultado = movimientosService.retirar(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(TipoOperacion.RETIRO, resultado.getTipoOperacion());
        assertEquals(50.0, cuenta.getBalance());
        verify(cuentaDao, times(2)).save(cuenta);
    }

    @Test
    public void testRetirarFondosInsuficientes() throws CuentaNotFoundException {

        MovimientosDto retiro = new MovimientosDto(1500.0, 1234L, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(1234L);
        cuenta.setBalance(1000.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1234L)).thenReturn(cuenta);

        assertThrows(FondosInsuficientesException.class, () -> movimientosService.retirar(retiro));
    }

    @Test
    public void testRetirarCuentaNoEncontrada() {
        // Dado
        MovimientosDto retiro = new MovimientosDto(500.0, 1234L, "ARS");
        when(cuentaDao.find(1234L)).thenReturn(null);

        // Cuando y Entonces
        assertThrows(CuentaNotFoundException.class, () -> movimientosService.retirar(retiro));
    }

    @Test
    public void testRetirarMontoMayorAlDisponible() throws CuentaNotFoundException {
        // Dado
        MovimientosDto retiro = new MovimientosDto(2000.0, 1234L, "USD");
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(1234L);
        cuenta.setBalance(1500.0);
        cuenta.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(1234L)).thenReturn(cuenta);

        // Cuando y Entonces
        assertThrows(FondosInsuficientesException.class, () -> movimientosService.retirar(retiro));
    }

    @Test
    public void testRetirarMonedaIncorrecta() throws CuentaNotFoundException {
        // Dado
        MovimientosDto retiro = new MovimientosDto(500.0, 1234L, "USD");
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(1234L);
        cuenta.setBalance(1500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1234L)).thenReturn(cuenta);

        // Cuando y Entonces
        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.retirar(retiro));
    }


    //Test para depositos
    @Test
    public void depositarExitosoTest() throws CuentaNotFoundException, MonedasIncompatiblesException {
        MovimientosDto movimientosDto = new MovimientosDto(50.0, 1, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(100.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.depositar(movimientosDto);

        assertNotNull(resultado);
        assertEquals(TipoOperacion.DEPOSITO, resultado.getTipoOperacion());
        assertEquals(150.0, cuenta.getBalance());
        verify(cuentaDao, times(2)).save(cuenta);
    }

    @Test
    public void depositarExitosoDolaresTest() throws CuentaNotFoundException, MonedasIncompatiblesException {
        MovimientosDto movimientosDto = new MovimientosDto(200.0, 1, "USD");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(800.0);
        cuenta.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(1)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.depositar(movimientosDto);

        assertNotNull(resultado);
        assertEquals(TipoOperacion.DEPOSITO, resultado.getTipoOperacion());
        assertEquals(1000.0, cuenta.getBalance());
        verify(cuentaDao, times(2)).save(cuenta);
    }

    @Test
    public void depositarExitosoPesosConGranMontoTest() throws CuentaNotFoundException, MonedasIncompatiblesException {
        MovimientosDto movimientosDto = new MovimientosDto(1000000.0, 1, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500000.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.depositar(movimientosDto);

        assertNotNull(resultado);
        assertEquals(TipoOperacion.DEPOSITO, resultado.getTipoOperacion());
        assertEquals(1500000.0, cuenta.getBalance());
        verify(cuentaDao, times(2)).save(cuenta);
    }

    @Test
    public void testDepositarMonedasIncompatibles() throws CuentaNotFoundException {
        // Dado
        MovimientosDto deposito = new MovimientosDto(500.0, 1234L, "USD");
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(1234L);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(1234L)).thenReturn(cuenta);

        // Cuando y Entonces
        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.depositar(deposito));
    }

    @Test
    public void testDepositarCuentaNoEncontrada() {
        // Dado
        MovimientosDto deposito = new MovimientosDto(500.0, 1234L, "ARS");
        when(cuentaDao.find(1234L)).thenReturn(null);

        // Cuando y Entonces
        assertThrows(CuentaNotFoundException.class, () -> movimientosService.depositar(deposito));
    }



}
