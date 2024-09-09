package ar.edu.utn.frbb.tup.service;

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

    // Test para transferencias
    @Test
    public void testTransferenciaExitosa() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 1000.0, "ARS");
        Cuenta cuentaOrigen = new Cuenta();
        Cuenta cuentaDestino = new Cuenta();

        when(cuentaDao.find(transferenciaDto.getNumeroCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaDestino())).thenReturn(cuentaDestino);

        // Act
        movimientosService.transferir(transferenciaDto);

        // Assert
        assertEquals(4000.0, cuentaOrigen.getBalance()); // Balance después de la transferencia
        assertEquals(2000.0, cuentaDestino.getBalance()); // Balance después de recibir la transferencia
    }
    @Test
    public void testTransferenciaExternaExitosa() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");
        Cuenta cuentaOrigen = new Cuenta();
        Cuenta cuentaDestino = new Cuenta();

        when(cuentaDao.find(transferenciaDto.getNumeroCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaDestino())).thenReturn(cuentaDestino);
        when(banelcoService.realizarTransferenciaBanelco(anyLong(), anyLong(), anyDouble())).thenReturn(true); // Transferencia exitosa

        // Act
        movimientosService.transferir(transferenciaDto);

        // Assert
        assertEquals(1500.0, cuentaOrigen.getBalance()); // Balance después de la transferencia
        assertEquals(2000.0, cuentaDestino.getBalance()); // Balance después de recibir la transferencia
    }
    @Test
    public void testTransferenciaExternaError() throws CuentaNotFoundException, MonedasIncompatiblesException, FondosInsuficientesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 1000.0, "PESOS");
        Cuenta cuentaOrigen = new Cuenta();
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaDestino())).thenReturn(null); // Cuenta destino no encontrada
        when(banelcoService.realizarTransferenciaBanelco(anyLong(), anyLong(), anyDouble())).thenReturn(false); // Transferencia externa falla

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }
    @Test
    public void testTransferenciaCuentaOrigenNoExiste() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 500.0, "ARS");
        Cuenta cuentaDestino = new Cuenta(2L,"ARS", 1500.0);

        when(cuentaDao.find(transferenciaDto.getNumeroCuentaOrigen())).thenReturn(null); // Cuenta de origen no encontrada
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaDestino())).thenReturn(cuentaDestino);

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }
    @Test
    public void transferirMonedasIncompatiblesTest() throws CuentaNotFoundException, MonedasIncompatiblesException, FondosInsuficientesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 1000.0, "USD");
        Cuenta cuentaOrigen = new Cuenta();
        Cuenta cuentaDestino = new Cuenta();

        when(cuentaDao.find(transferenciaDto.getNumeroCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaDestino())).thenReturn(cuentaDestino);

        // Act & Assert
        assertThrows(MonedasIncompatiblesException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }
    @Test
    public void testTransferirFondosInsuficientes() throws CuentaNotFoundException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 1000.0, "PESOS");
        Cuenta cuentaOrigen = new Cuenta(); // Fondos insuficientes
        Cuenta cuentaDestino = new Cuenta();

        when(cuentaDao.find(transferenciaDto.getNumeroCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaDestino())).thenReturn(cuentaDestino);

        // Act & Assert
        assertThrows(FondosInsuficientesException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }
    @Test
    public void testTransferirFallaConBanelco() throws CuentaNotFoundException, MonedasIncompatiblesException, FondosInsuficientesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 1000.0, "PESOS");
        Cuenta cuentaOrigen = new Cuenta(); // Cuenta interna
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaDestino())).thenReturn(null); // Externa

        when(banelcoService.realizarTransferenciaBanelco(anyLong(), anyLong(), anyDouble())).thenReturn(false); // Falla la transferencia

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.transferir(transferenciaDto);
        });
    }
    @Test
    public void testTransferirConComisionPesos() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Arrange
        MovimientosTransferenciasDto transferenciaDto = new MovimientosTransferenciasDto(1L, 2L, 2000000.0, "PESOS"); // Monto alto para aplicar comisión
        Cuenta cuentaOrigen = new Cuenta();
        Cuenta cuentaDestino = new Cuenta();

        when(cuentaDao.find(transferenciaDto.getNumeroCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaDao.find(transferenciaDto.getNumeroCuentaDestino())).thenReturn(cuentaDestino);

        // Act
        movimientosService.transferir(transferenciaDto);

        // Assert
        assertEquals(2960000.0, cuentaOrigen.getBalance());// Balance después de aplicar comisión
        assertEquals(3000000.0, cuentaDestino.getBalance()); // Balance después de recibir la transferencia
    }

    // Test para depositos
    @Test
    public void depositarExitosoTest() throws CuentaNotFoundException, MonedasIncompatiblesException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");
        Cuenta cuenta = new Cuenta();

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act
        Movimientos movimiento = movimientosService.depositar(movimientosDto);

        // Assert
        assertEquals(6000.0, cuenta.getBalance()); // Verifica que el saldo sea correcto
        assertEquals(TipoOperacion.DEPOSITO, movimiento.getTipoOperacion()); // Verifica el tipo de operación
    }

    @Test
    public void testDepositarMonedasIncompatibles() throws CuentaNotFoundException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "USD");
        Cuenta cuenta = new Cuenta(1L, TipoMoneda.PESOS, 5000.0);

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act & Assert
        assertThrows(MonedasIncompatiblesException.class, () -> {
            movimientosService.depositar(movimientosDto);
        });
    }

    @Test
    public void testDepositoCuentaNoEncontrada() throws MonedasIncompatiblesException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(null); // Cuenta no encontrada

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.depositar(movimientosDto);
        });
    }

    @Test
    public void testDepositoMontoNegativo() throws CuentaNotFoundException, MonedasIncompatiblesException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(-1000.0, 1L, "ARS"); // Monto negativo
        Cuenta cuenta = new Cuenta(1L, TipoMoneda.PESOS, 5000.0);

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            movimientosService.depositar(movimientosDto);
        });
    }

    @Test
    public void testDepositoSaldoMaximo() throws CuentaNotFoundException, MonedasIncompatiblesException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(10000.0, 1L, "USD");
        Cuenta cuenta = new Cuenta(); // Saldo actual es 90000.0, límite de 100000.0

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act
        movimientosService.depositar(movimientosDto);

        // Assert
        assertEquals(100000.0, cuenta.getBalance()); // Verifica el saldo máximo permitido
    }

    // Test para retiros
    @Test
    public void retirarExitosoTest() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");
        Cuenta cuenta = new Cuenta(1L, TipoMoneda.PESOS, 5000.0);

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act
        Movimientos movimiento = movimientosService.retirar(movimientosDto);

        // Assert
        assertEquals(4000.0, cuenta.getBalance()); // Verifica que el saldo sea correcto
        assertEquals(TipoOperacion.RETIRO, movimiento.getTipoOperacion()); // Verifica el tipo de operación
    }
    @Test
    public void testRetirarFondosInsuficientes() throws CuentaNotFoundException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");
        Cuenta cuenta = new Cuenta(1L, TipoMoneda.PESOS, 500.0); // Fondos insuficientes

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act & Assert
        assertThrows(FondosInsuficientesException.class, () -> {
            movimientosService.retirar(movimientosDto);
        });
    }
    @Test
    public void testRetiroMontoNegativo() throws CuentaNotFoundException, FondosInsuficientesException, MonedasIncompatiblesException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(-1000.0, 1L, "PESOS"); // Monto negativo
        Cuenta cuenta = new Cuenta(1L, TipoMoneda.PESOS, 5000.0);

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            movimientosService.retirar(movimientosDto);
        });
    }
    @Test
    public void testRetiroCuentaNoEncontrada() throws FondosInsuficientesException, MonedasIncompatiblesException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(null); // Cuenta no encontrada

        // Act & Assert
        assertThrows(CuentaNotFoundException.class, () -> {
            movimientosService.retirar(movimientosDto);
        });
    }
    @Test
    public void testRetiroFondosInsuficientes() throws CuentaNotFoundException {
        // Arrange
        MovimientosDto movimientosDto = new MovimientosDto(1000.0, 1L, "ARS");
        Cuenta cuenta = new Cuenta(1L, TipoMoneda.PESOS, 500.0); // Fondos insuficientes

        when(cuentaDao.find(movimientosDto.getNumeroCuenta())).thenReturn(cuenta);

        // Act & Assert
        assertThrows(FondosInsuficientesException.class, () -> {
            movimientosService.retirar(movimientosDto);
        });
    }
}
