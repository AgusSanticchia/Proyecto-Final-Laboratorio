package ar.edu.utn.frbb.tup.service;

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

    @Test
    public void testDepositExitoso() throws Exception {
        long numeroCuenta = 123L;
        MovimientosDto depositoDto = new MovimientosDto(100.0, numeroCuenta, "ARS");

        Cuenta cuenta = new Cuenta();
        cuenta.setMovimientos(new LinkedList<>());
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.depositar(depositoDto);

        assertEquals(600.0, cuenta.getBalance());
        assertEquals(100.0, resultado.getMonto());
        verify(cuentaDao).save(cuenta);
    }

    @Test
    public void testDepositoCuentaNoExiste() {
        long numeroCuenta = 999L;
        MovimientosDto depositoDto = new MovimientosDto(100.0, numeroCuenta, "ARS");

        when(cuentaDao.find(numeroCuenta)).thenReturn(null);

        assertThrows(CuentaNotExistException.class, () -> movimientosService.depositar(depositoDto));
    }

    @Test
    public void testDepositoMonedasIncompatibles() {
        long numeroCuenta = 123L;
        MovimientosDto depositoDto = new MovimientosDto(100.0, numeroCuenta, "USD");

        Cuenta cuenta = new Cuenta();
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);


        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.depositar(depositoDto));
    }

    @Test
    public void testRetiroExitoso() throws Exception {
        long numeroCuenta = 456L;
        MovimientosDto retiroDto = new MovimientosDto(100.0, numeroCuenta, "ARS");

        Cuenta cuenta = new Cuenta();
        cuenta.setMovimientos(new LinkedList<>());
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.retirar(retiroDto);

        assertEquals(400.0, cuenta.getBalance());
        assertEquals(100.0, resultado.getMonto());
        verify(cuentaDao).update(cuenta);
    }

    @Test
    public void testRetiroSinFondosSuficientes() {
        long numeroCuenta = 456L;
        MovimientosDto retiroDto = new MovimientosDto(600.0, numeroCuenta, "ARS");

        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        assertThrows(FondosInsuficientesException.class, () -> movimientosService.retirar(retiroDto));
    }

    @Test
    public void testRetiroMonedasIncompatibles() {
        long numeroCuenta = 456L;
        MovimientosDto retiroDto = new MovimientosDto(100.0, numeroCuenta, "USD");

        Cuenta cuenta = new Cuenta();
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.retirar(retiroDto));
    }

    @Test
    public void testTransferirExitoso() throws FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotExistException {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(456L, 123L, 100.0, "ARS");

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
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");

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
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "USD");

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
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");

        when(cuentaDao.find(transferencia.getCuentaOrigen())).thenReturn(null);

        assertThrows(CuentaNotExistException.class, () -> movimientosService.transferir(transferencia));
    }

    @Test
    public void testTransferenciaCuentaDestinoNoExiste() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");

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
        MovimientosTransferenciasDto transferenciaDto =
                new MovimientosTransferenciasDto(789L, 123L, 100.0, "ARS");

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

        Movimientos resultado = movimientosService.transferir(transferenciaDto);

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
        MovimientosTransferenciasDto transferenciaDto =
                new MovimientosTransferenciasDto(789L, 123L, 100.0, "ARS");

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

        assertThrows(CuentaNotExistException.class, () -> movimientosService.transferir(transferenciaDto));
    }

    @Test
    public void testTransferenciaConComisionPesos() throws Exception {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 2000000.0, "ARS");

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

        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        double comisionEsperada = 2000000.0 * 0.02;
        assertEquals(3000000.0 - 2000000.0 - comisionEsperada, cuentaOrigen.getBalance(), 0.01);
        assertEquals(500000.0 + 2000000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaSinComisionPesos() throws Exception {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 500000.0, "ARS");

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


        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        double comisionEsperada = 0.0;
        assertEquals(1000000.0 - 500000.0 - comisionEsperada, cuentaOrigen.getBalance(), 0.01);
        assertEquals(200000.0 + 500000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaConComisionDolares() throws Exception {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 10000.0, "USD");

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

        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        double comisionEsperada = 10000.0 * 0.005;
        assertEquals(20000.0 - 10000.0 - comisionEsperada, cuentaOrigen.getBalance(), 0.01);
        assertEquals(5000.0 + 10000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaSinComisionDolares() throws Exception {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 3000.0, "USD");

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

        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        double comisionEsperada = 0.0;
        assertEquals(10000.0 - 3000.0 - comisionEsperada, cuentaOrigen.getBalance(), 0.01);
        assertEquals(2000.0 + 3000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testComisionConMonedaNoSoportada() {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 1000.0, "EURO");

        assertThrows(CuentaNotExistException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }

    @Test
    public void testTransferenciaConComisionExactaEnLimitePesos() throws Exception {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 1000000.0, "ARS");

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

        Movimientos resultado = movimientosService.transferir(transferenciaDto);

        assertEquals(2000000.0 - 1000000.0, cuentaOrigen.getBalance(), 0.01);
        assertEquals(500000.0 + 1000000.0, cuentaDestino.getBalance(), 0.01);
        verify(cuentaDao, times(2)).save(any(Cuenta.class));
    }

    @Test
    public void testTransferenciaCuentaNoEncontrada() {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 1000.0, "ARS");

        assertThrows(CuentaNotExistException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });

    }

    @Test
    public void testTransferenciaFondosInsuficientes() {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 500.0, "ARS");

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

        assertThrows(FondosInsuficientesException.class, () -> movimientosService.transferir(transferenciaDto));
    }

    @Test
    public void testDepositarMonedaNoCoincide() {
        long numeroCuenta = 123L;
        MovimientosDto depositoDto = new MovimientosDto(100.0, numeroCuenta, "USD");

        Cuenta cuenta = new Cuenta();
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        assertThrows(MonedasIncompatiblesException.class, () ->
                movimientosService.depositar(depositoDto)
        );
    }

    @Test
    public void testRetirarCuentaNoEncontrada() {
        MovimientosDto retiroDto = new MovimientosDto(100.0, 999L, "ARS");

        when(cuentaDao.find(999L)).thenReturn(null);

        assertThrows(CuentaNotExistException.class, () ->
                movimientosService.retirar(retiroDto)
        );
    }

    @Test
    public void testComisionPesosMonto() {
        MovimientosTransferenciasDto transferenciaDto1 =
                new MovimientosTransferenciasDto(1L, 2L, 500000.0, "ARS");
        MovimientosTransferenciasDto transferenciaDto2 =
                new MovimientosTransferenciasDto(1L, 2L, 1500000.0, "ARS");

        double comision1 = invokeComisionMethod(transferenciaDto1, TipoMoneda.PESOS);
        double comision2 = invokeComisionMethod(transferenciaDto2, TipoMoneda.PESOS);

        assertEquals(0.0, comision1, 0.01);
        assertEquals(1500000.0 * 0.02, comision2, 0.01);
    }

    @Test
    public void testComisionDolaresMonto() {
        MovimientosTransferenciasDto transferenciaDto1 =
                new MovimientosTransferenciasDto(1L, 2L, 4000.0, "USD");
        MovimientosTransferenciasDto transferenciaDto2 =
                new MovimientosTransferenciasDto(1L, 2L, 6000.0, "USD");

        double comision1 = invokeComisionMethod(transferenciaDto1, TipoMoneda.DOLARES);
        double comision2 = invokeComisionMethod(transferenciaDto2, TipoMoneda.DOLARES);

        assertEquals(0.0, comision1, 0.01);
        assertEquals(6000.0 * 0.005, comision2, 0.01);
    }

    // Helper method to invoke private comision method via reflection
    private double invokeComisionMethod(MovimientosTransferenciasDto transferencia, TipoMoneda tipoMoneda) {
        try {
            java.lang.reflect.Method method = MovimientosService.class.getDeclaredMethod("comision", MovimientosTransferenciasDto.class, TipoMoneda.class);
            method.setAccessible(true);
            return (double) method.invoke(movimientosService, transferencia, tipoMoneda);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTransferenciaOtraMonedaNoSoportada() {
        MovimientosTransferenciasDto transferenciaDto =
                new MovimientosTransferenciasDto(789L, 123L, 1000.0, "EURO");

        assertThrows(CuentaNotExistException.class, () ->
                movimientosService.transferir(transferenciaDto)
        );
    }

    @Test
    public void testRetirarConSaldoExacto() throws Exception {
        long numeroCuenta = 456L;
        MovimientosDto retiroDto = new MovimientosDto(500.0, numeroCuenta, "ARS");

        Cuenta cuenta = new Cuenta();
        cuenta.setMovimientos(new LinkedList<>());
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.retirar(retiroDto);

        assertEquals(0.0, cuenta.getBalance());
        assertEquals(500.0, resultado.getMonto());
        verify(cuentaDao).update(cuenta);
    }

    @Test
    public void testDepositarMontoMinimoExitoso() throws Exception {
        long numeroCuenta = 123L;
        MovimientosDto depositoDto = new MovimientosDto(0.01, numeroCuenta, "ARS");

        Cuenta cuenta = new Cuenta();
        cuenta.setMovimientos(new LinkedList<>());
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);

        when(cuentaDao.find(numeroCuenta)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.depositar(depositoDto);

        assertEquals(500.01, cuenta.getBalance());
        assertEquals(0.01, resultado.getMonto());
        verify(cuentaDao).save(cuenta);
    }

    @Test
    public void testComisionConMonedaNoSoportadaExplicitamente() {
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(789L, 123L, 1000.0, "EUROS");

        assertThrows(CuentaNotExistException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }

    @Test
    public void testTransferenciaEntreCuentasDiferenteTipoMoneda() {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(1000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(500.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(transferencia.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferencia.getCuentaDestino())).thenReturn(cuentaDestino);

        assertThrows(MonedasIncompatiblesException.class, () -> {
            movimientosService.transferir(transferencia);
        });
    }
}
