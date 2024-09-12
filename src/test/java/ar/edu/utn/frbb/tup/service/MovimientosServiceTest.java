package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;
import ar.edu.utn.frbb.tup.model.exception.*;
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

    // Test para transferencias
    @Test
    public void transferirExitoso() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotExistException {
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
    public void testTransferenciaSinFondos() throws CuentaNotFoundException, CuentaNotExistException, MonedasIncompatiblesException, FondosInsuficientesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(100.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);
        cuentaOrigen.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(200.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);
        cuentaDestino.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act & Assert
        assertThrows(FondosInsuficientesException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }

    @Test
    public void testTransferirMonedasIncompatibles() throws CuentaNotFoundException, CuentaNotExistException, FondosInsuficientesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 500.0, "USD");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setMovimientos(new LinkedList<>());
        cuentaDestino.setBalance(200.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act & Assert
        assertThrows(MonedasIncompatiblesException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });

    }
    @Test
    public void testTransferenciaCuentaOrigenNoExiste() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotExistException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");
        Cuenta cuentaDestino = new Cuenta();

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(null); // Cuenta de origen no encontrada
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(cuentaDestino);

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }

    @Test
    void transferirBanelcoExterno() throws CuentaNotFoundException, FondosInsuficientesException, CuentaNotExistException, MonedasIncompatiblesException {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(456L, 123L, 100.0, "ARS");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);
        cuentaOrigen.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);

        when(cuentaDao.find(123L)).thenReturn(cuentaOrigen);
        when(cuentaDao.find(456L)).thenReturn(null);
        when(banelcoService.realizarTransferenciaBanelco(123L, 456L, 100.0)).thenReturn(true);

        Movimientos resultado = movimientosService.transferir(transferencia);

        assertEquals(400.0, cuentaOrigen.getBalance());
        assertEquals(100.0, resultado.getMonto());
        assertEquals(TipoMoneda.PESOS, resultado.getTipoMoneda());
        verify(cuentaDao).update(cuentaOrigen);
    }

    @Test
    public void testTransferenciaCuentaDestinoNoExiste() throws CuentaNotFoundException, CuentaNotExistException, MonedasIncompatiblesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);
        cuentaOrigen.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);

        when(cuentaDao.find(transferenciaDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getCuentaDestino())).thenReturn(null); // Cuenta de destino no encontrada

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }

    @Test
    public void testTransferenciaBanelcoExternoFallido() throws CuentaNotFoundException, CuentaNotExistException, MonedasIncompatiblesException {
        MovimientosTransferenciasDto transferencia = new MovimientosTransferenciasDto(456L, 123L, 100.0, "ARS");
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setMovimientos(new LinkedList<>());
        cuentaOrigen.setBalance(500.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);
        cuentaOrigen.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);

        when(cuentaDao.find(123L)).thenReturn(cuentaOrigen);
        when(cuentaDao.find(456L)).thenReturn(null);
        when(banelcoService.realizarTransferenciaBanelco(123L, 456L, 100.0)).thenReturn(false);

        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.transferir(transferencia);
        });
    }

    // Test para depositos
    @Test
    void depositarExitosoPesos() throws CuentaNotFoundException, MonedasIncompatiblesException, CuentaNotExistException {
        MovimientosDto deposito = new MovimientosDto(100.0, 123L, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(123L)).thenReturn(cuenta);

        Movimientos resultant = movimientosService.depositar(deposito);

        assertEquals(600.0, cuenta.getBalance());
        assertEquals(100.0, resultant.getMonto());
        verify(cuentaDao).save(cuenta);
    }

    @Test
    public void testDepositarExitosoDolares() throws CuentaNotFoundException, CuentaNotExistException, MonedasIncompatiblesException {
        // Dado
        MovimientosDto movimientosDto = new MovimientosDto(4000d, 12L, "USD");
        CuentaDto cuentaDto = new CuentaDto(44882709L, "CAU$S", "USD");
        Cuenta cuenta = new Cuenta(cuentaDto);
        cuenta.setBalance(1000d);

        when(cuentaDao.find(12L)).thenReturn(cuenta);

        // Cuando
        movimientosService.depositar(movimientosDto);

        // Entonces
        assertEquals(5000d, cuenta.getBalance());
        verify(cuentaDao, times(1)).save(cuenta);
        verify(cuentaDao, never()).update(any(Cuenta.class));
    }

    @Test
    public void testDepositoCuentaNoEncontrada() throws MonedasIncompatiblesException, CuentaNotExistException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(null); // Cuenta no encontrada

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.depositar(movimientosDto);
        });
    }

    @Test
    void depositarMonedaIncompatible() throws CuentaNotExistException {
        MovimientosDto deposito = new MovimientosDto(100.0, 123L, "USD");
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(123L)).thenReturn(cuenta);

        assertThrows(MonedasIncompatiblesException.class, () -> movimientosService.depositar(deposito));
    }

    @Test
    void depositarActualizaHistorialMovimientos() throws CuentaNotFoundException, MonedasIncompatiblesException, CuentaNotExistException {
        MovimientosDto deposito = new MovimientosDto(100.0, 123L, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setMovimientos(new LinkedList<>());

        cuenta.getMovimientos().add(new Movimientos(deposito));
        when(cuentaDao.find(123L)).thenReturn(cuenta);

        movimientosService.depositar(deposito);

        assertEquals(1, cuenta.getMovimientos().size());
        Movimientos movimiento = cuenta.getMovimientos().getFirst();
        assertEquals(100.0, movimiento.getMonto());
        assertEquals(TipoMoneda.PESOS, movimiento.getTipoMoneda());
    }

    @Test
    void depositarMontoMaximo() throws CuentaNotFoundException, MonedasIncompatiblesException, CuentaNotExistException {
        double montoMaximo = Double.MAX_VALUE - 1000; // Dejamos un margen para evitar overflow
        MovimientosDto deposito = new MovimientosDto(montoMaximo, 123L, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(1000.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(123L)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.depositar(deposito);

        assertEquals(montoMaximo + 1000.0, cuenta.getBalance());
        assertEquals(montoMaximo, resultado.getMonto());
    }

    // Test para retiros
    @Test
    void retirarExitoso() throws FondosInsuficientesException, CuentaNotFoundException, MonedasIncompatiblesException, CuentaNotExistException {
        MovimientosDto retiro = new MovimientosDto(100.0, 123L, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(123L)).thenReturn(cuenta);

        Movimientos resultado = movimientosService.retirar(retiro);

        assertEquals(400.0, cuenta.getBalance());
        assertEquals(100.0, resultado.getMonto());
        verify(cuentaDao).update(cuenta);
    }
    @Test
    void retirarFondosInsuficientes() throws CuentaNotExistException {
        MovimientosDto retiro = new MovimientosDto(600.0, 123L, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(123L)).thenReturn(cuenta);

        assertThrows(FondosInsuficientesException.class, () -> movimientosService.retirar(retiro));
    }
    @Test
    public void testRetiroMontoNegativo() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotExistException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(-1000.0, 1L, "PESOS"); // Monto negativo
        Cuenta cuenta = new Cuenta();

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            movimientosService.retirar(movimientosDto);
        });
    }
    @Test
    public void testRetiroCuentaNoEncontrada() throws FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotExistException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(null); // Cuenta no encontrada

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.retirar(movimientosDto);
        });
    }

    @Test
    void retiroActualizaHistorialMovimientos() throws CuentaNotFoundException, MonedasIncompatiblesException, CuentaNotExistException, FondosInsuficientesException {
        MovimientosDto retiro = new MovimientosDto(100.0, 123L, "ARS");
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(500.0);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setMovimientos(new LinkedList<>());

        cuenta.getMovimientos().add(new Movimientos(retiro));
        when(cuentaDao.find(123L)).thenReturn(cuenta);

        movimientosService.retirar(retiro);

        assertEquals(1, cuenta.getMovimientos().size());
        Movimientos movimiento = cuenta.getMovimientos().getFirst();
        assertEquals(100.0, movimiento.getMonto());
        assertEquals(TipoMoneda.PESOS, movimiento.getTipoMoneda());
    }

}
