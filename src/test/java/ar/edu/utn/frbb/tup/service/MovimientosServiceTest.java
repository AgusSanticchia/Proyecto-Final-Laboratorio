package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.exception.cuentas.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class    MovimientosServiceTest {

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

    // Deposit Tests
    @Test
    public void testDepositExitoso() throws Exception {
        // Arrange
        long numeroCuenta = 123L;
        MovimientosDto depositoDto = new MovimientosDto(100.0, numeroCuenta, "ARS");

        Cuenta cuenta = new Cuenta();
        cuenta.setMovimientos(new LinkedList<>());
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        // Act
        Movimientos resultado = movimientosService.depositar(depositoDto);

        // Assert
        assertEquals(600.0, cuenta.getBalance());
        assertEquals(100.0, resultado.getMonto());
        verify(cuentaDao).save(cuenta);
    }

    @Test
    public void testDepositoCuentaNoExiste() {
        // Arrange
        long numeroCuenta = 999L;
        MovimientosDto depositoDto = new MovimientosDto(100.0, numeroCuenta, "ARS");

        when(cuentaDao.find(numeroCuenta)).thenReturn(null);

        // Act & Assert
        assertThrows(CuentaNotExistException.class, () -> movimientosService.depositar(depositoDto));
    }

    @Test
    public void testDepositoMonedasIncompatibles() {
        // Arrange
        long numeroCuenta = 123L;
        MovimientosDto depositoDto = new MovimientosDto(100.0, numeroCuenta, "USD");

        Cuenta cuenta = new Cuenta();
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        // Act & Assert
        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.depositar(depositoDto));
    }
    // Withdrawal Tests
    @Test
    public void testRetiroExitoso() throws Exception {
        // Arrange
        long numeroCuenta = 456L;
        MovimientosDto retiroDto = new MovimientosDto(100.0, numeroCuenta, "ARS");

        Cuenta cuenta = new Cuenta();
        cuenta.setMovimientos(new LinkedList<>());
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        // Act
        Movimientos resultado = movimientosService.retirar(retiroDto);

        // Assert
        assertEquals(400.0, cuenta.getBalance());
        assertEquals(100.0, resultado.getMonto());
        verify(cuentaDao).update(cuenta);
    }

    @Test
    public void testRetiroSinFondosSuficientes() {
        // Arrange
        long numeroCuenta = 456L;
        MovimientosDto retiroDto = new MovimientosDto(600.0, numeroCuenta, "ARS");

        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        // Act & Assert
        assertThrows(FondosInsuficientesException.class, () -> movimientosService.retirar(retiroDto));
    }

    @Test
    public void testRetiroMonedasIncompatibles() {
        // Arrange
        long numeroCuenta = 456L;
        MovimientosDto retiroDto = new MovimientosDto(100.0, numeroCuenta, "USD");

        Cuenta cuenta = new Cuenta();
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        // Act & Assert
        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.retirar(retiroDto));
    }

    @Test
    public void testTransferirExitoso() throws FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotExistException {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(456L, 123L, 100.0, "ARS", "Transferencia exitosa");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);
        cuentaOrigen.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(200.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);
        cuentaDestino.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);

        when(cuentaDao.find(123L)).thenReturn(cuentaOrigen);
        when(cuentaDao.find(456L)).thenReturn(cuentaDestino);

        Movimientos resultado = movimientosService.transferir(transferencia);

        assertEquals(400.0, cuentaOrigen.getBalance());
        assertEquals(300.0, cuentaDestino.getBalance());
        assertEquals(100.0, resultado.getMonto());
        assertEquals(TipoMoneda.PESOS, resultado.getTipoMoneda());
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaSinFondos() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS", "Fondos insuficientes");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(100.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(200.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(transferencia.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferencia.getCuentaDestino())).thenReturn(cuentaDestino);

        assertThrows(FondosInsuficientesException.class, () -> movimientosService.transferir(transferencia));
    }

    @Test
    public void testTransferirMonedasIncompatibles() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "USD", "Monedas incompatibles");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(200.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(transferencia.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferencia.getCuentaDestino())).thenReturn(cuentaDestino);

        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.transferir(transferencia));
    }

    @Test
    public void testTransferenciaCuentaOrigenNoExiste() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS", "Cuenta origen no existe");

        when(cuentaDao.find(transferencia.getCuentaOrigen())).thenReturn(null);

        assertThrows(CuentaNotExistException.class, () -> movimientosService.transferir(transferencia));
    }

    @Test
    public void testTransferenciaCuentaDestinoNoExiste() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS", "Cuenta destino no existe");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(transferencia.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferencia.getCuentaDestino())).thenReturn(null);

        assertThrows(CuentaNotExistException.class, () -> movimientosService.transferir(transferencia));
    }

    @Test
    public void testTransferenciaExternaExitosa() throws Exception {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto =
                new MovimientosTransferenciasDto(789L, 123L, 100.0, "ARS", "Transferencia externa");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(banelcoService.realizarTransferenciaBanelco(
                transferenciaDto.getCuentaOrigen(),
                transferenciaDto.getCuentaDestino(),
                transferenciaDto.getMonto())
        ).thenReturn(true);

        // Act
        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        // Assert
        // Verify balance reduced by amount + any commission
        assertTrue(cuentaOrigen.getBalance() < 500.0);
        verify(cuentaDao).update(cuentaOrigen);
        verify(banelcoService).realizarTransferenciaBanelco(
                transferenciaDto.getCuentaOrigen(),
                transferenciaDto.getCuentaDestino(),
                transferenciaDto.getMonto()
        );
    }

    @Test
    public void testTransferenciaExternaFallida() {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto =
                new MovimientosTransferenciasDto(789L, 123L, 100.0, "ARS", "Transferencia externa fallida");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(banelcoService.realizarTransferenciaBanelco(
                transferenciaDto.getCuentaOrigen(),
                transferenciaDto.getCuentaDestino(),
                transferenciaDto.getMonto())
        ).thenReturn(false);

        // Act & Assert
        assertThrows(CuentaNotExistException.class, () -> movimientosService.transferir(transferenciaDto));
    }

    @Test
    public void testTransferenciaConComisionPesos() throws Exception {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 2000000.0, "ARS", "Transferencia con comisión alta");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(3000000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(500000.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act
        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        // Assert
        double comisionEsperada = 2000000.0 * 0.02; // Comisión del 2% para montos > 1,000,000
        assertEquals(3000000.0 - 2000000.0 - comisionEsperada, cuentaOrigen.getBalance(), 0.01);
        assertEquals(500000.0 + 2000000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaSinComisionPesos() throws Exception {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 500000.0, "ARS", "Transferencia sin comisión");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(1000000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(200000.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act
        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        // Assert
        double comisionEsperada = 0.0; // Sin comisión para montos <= 1,000,000
        assertEquals(1000000.0 - 500000.0 - comisionEsperada, cuentaOrigen.getBalance(), 0.01);
        assertEquals(200000.0 + 500000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaConComisionDolares() throws Exception {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 10000.0, "USD", "Transferencia con comisión alta");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(20000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.DOLARES);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(5000.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act
        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        // Assert
        double comisionEsperada = 10000.0 * 0.005; // Comisión del 0.5% para montos > 5,000
        assertEquals(20000.0 - 10000.0 - comisionEsperada, cuentaOrigen.getBalance(), 0.01);
        assertEquals(5000.0 + 10000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaSinComisionDolares() throws Exception {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 3000.0, "USD", "Transferencia sin comisión");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(10000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.DOLARES);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(2000.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act
        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        // Assert
        double comisionEsperada = 0.0; // Sin comisión para montos <= 5,000
        assertEquals(10000.0 - 3000.0 - comisionEsperada, cuentaOrigen.getBalance(), 0.01);
        assertEquals(2000.0 + 3000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testComisionConMonedaNoSoportada() {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 1000.0, "EURO", "Transferencia no soportada");

        // Act & Assert
        assertThrows(CuentaNotExistException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }

    @Test
    public void testTransferenciaConComisionExactaEnLimitePesos() throws Exception {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 1000000.0, "ARS", "Transferencia con comisión exacta");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(2000000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(500000.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act
        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        // Assert
        assertEquals(2000000.0 - 1000000.0, cuentaOrigen.getBalance(), 0.01);
        assertEquals(500000.0 + 1000000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaCuentaNoEncontrada() {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 1000.0, "ARS", "Transferencia no soportada");

        // Act & Assert
        assertThrows(CuentaNotExistException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });

    }

    @Test
    public void testTransferenciaFondosInsuficientes() {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 500.0, "ARS", "Fondos insuficientes");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(100.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(500.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act & Assert
        assertThrows(FondosInsuficientesException.class, () -> movimientosService.transferir(transferenciaDto));
    }


}
