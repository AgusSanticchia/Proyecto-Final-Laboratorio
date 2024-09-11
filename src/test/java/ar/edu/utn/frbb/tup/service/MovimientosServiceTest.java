package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotExistException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.model.exception.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // Test para depositos
    @Test
    public void testDepositarExitoso() throws CuentaNotFoundException, CuentaNotExistException, MonedasIncompatiblesException {
        // Dado
        MovimientosDto movimientosDto = new MovimientosDto(4000d, 12L, "USD");
        CuentaDto cuentaDto = new CuentaDto(44882709L, "CAU$S", "USD");
        Cuenta cuenta = new Cuenta(cuentaDto);
        cuenta.setBalance(1000d);  // Set initial balance

        when(cuentaDao.find(12L)).thenReturn(cuenta);

        // Cuando
        movimientosService.depositar(movimientosDto);

        // Entonces
        assertEquals(5000d, cuenta.getBalance());  // 1000 (initial) + 4000 (deposited)
        verify(cuentaDao, times(1)).save(cuenta);
        verify(cuentaDao, never()).update(any(Cuenta.class));  // Ensure update is never called
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
    
    // Test para retiros
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

}
