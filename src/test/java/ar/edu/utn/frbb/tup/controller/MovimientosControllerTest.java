package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.controller.validator.MovimientosValidator;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.exception.cuentas.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.service.MovimientosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovimientosControllerTest {

    @Mock
    private CuentaService cuentaService;

    @Mock
    private MovimientosService movimientosService;

    @Mock
    private MovimientosValidator movimientosValidator;

    @InjectMocks
    private MovimientosController movimientosController;

    private Cuenta cuentaMock;
    private Movimientos movimientosMock;
    private MovimientosDto movimientosDtoMock;
    private MovimientosTransferenciasDto transferenciasDtoMock;

    @BeforeEach
    void setUp() {
        cuentaMock = new Cuenta();
        cuentaMock.setNumeroCuenta(12345678L);
        cuentaMock.setMovimientos(new LinkedList<>());

        movimientosMock = new Movimientos();
        movimientosMock.setMonto(100.0);

        movimientosDtoMock = new MovimientosDto(100.0, 12345678L, "ARS");
        transferenciasDtoMock = new MovimientosTransferenciasDto(12345678L, 87654321L, 100.0, "ARS");
    }

    @Test
    void testTransferencia_ValidationError() throws DatosIncorrectosException {
        
        doThrow(new DatosIncorrectosException("Error de validación"))
                .when(movimientosValidator).validateMovimientosTransferencias(transferenciasDtoMock);

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosController.transferencia(transferenciasDtoMock)
        );

        assertEquals("Error de validación", exception.getMessage());

        verify(movimientosValidator, times(1)).validateMovimientosTransferencias(transferenciasDtoMock);
        verifyNoInteractions(movimientosService);
        verifyNoInteractions(cuentaService);
    }

    @Test
    void testTransferencia_MonedasIncompatibles() throws DatosIncorrectosException, MonedasIncompatiblesException, FondosInsuficientesException, CuentaNotExistException {
        
        doNothing().when(movimientosValidator).validateMovimientosTransferencias(transferenciasDtoMock);
        doThrow(new MonedasIncompatiblesException("Monedas incompatibles"))
                .when(movimientosService).transferir(transferenciasDtoMock);

        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> movimientosController.transferencia(transferenciasDtoMock)
        );

        assertEquals("Monedas incompatibles", exception.getMessage());

        verify(movimientosValidator, times(1)).validateMovimientosTransferencias(transferenciasDtoMock);
        verify(movimientosService, times(1)).transferir(transferenciasDtoMock);
        verifyNoInteractions(cuentaService);
    }

    @Test
    void testDepositos_ValidationError() throws DatosIncorrectosException {
        
        doThrow(new DatosIncorrectosException("Error de validación"))
                .when(movimientosValidator).validateMovimientos(movimientosDtoMock);

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosController.depositos(movimientosDtoMock)
        );

        assertEquals("Error de validación", exception.getMessage());

        verify(movimientosValidator, times(1)).validateMovimientos(movimientosDtoMock);
        verifyNoInteractions(movimientosService);
        verifyNoInteractions(cuentaService);
    }

    @Test
    void testDepositos_MonedasIncompatibles() throws CuentaNotExistException, DatosIncorrectosException, MonedasIncompatiblesException {
        
        doNothing().when(movimientosValidator).validateMovimientos(movimientosDtoMock);
        doThrow(new MonedasIncompatiblesException("Monedas incompatibles"))
                .when(movimientosService).depositar(movimientosDtoMock);

        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> movimientosController.depositos(movimientosDtoMock)
        );

        assertEquals("Monedas incompatibles", exception.getMessage());

        verify(movimientosValidator, times(1)).validateMovimientos(movimientosDtoMock);
        verify(movimientosService, times(1)).depositar(movimientosDtoMock);
        verifyNoInteractions(cuentaService);
    }

    @Test
    void testRetiros_ValidationError() throws DatosIncorrectosException {
        
        doThrow(new DatosIncorrectosException("Error de validación"))
                .when(movimientosValidator).validateMovimientos(movimientosDtoMock);

        DatosIncorrectosException exception = assertThrows(
                DatosIncorrectosException.class,
                () -> movimientosController.retiros(movimientosDtoMock)
        );

        assertEquals("Error de validación", exception.getMessage());

        verify(movimientosValidator, times(1)).validateMovimientos(movimientosDtoMock);
        verifyNoInteractions(movimientosService);
        verifyNoInteractions(cuentaService);
    }

    @Test
    void testRetiros_FondosInsuficientes() throws DatosIncorrectosException, FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotExistException {
        
        doNothing().when(movimientosValidator).validateMovimientos(movimientosDtoMock);
        doThrow(new FondosInsuficientesException("Fondos insuficientes"))
                .when(movimientosService).retirar(movimientosDtoMock);

        FondosInsuficientesException exception = assertThrows(
                FondosInsuficientesException.class,
                () -> movimientosController.retiros(movimientosDtoMock)
        );

        assertEquals("Fondos insuficientes", exception.getMessage());

        verify(movimientosValidator, times(1)).validateMovimientos(movimientosDtoMock);
        verify(movimientosService, times(1)).retirar(movimientosDtoMock);
        verifyNoInteractions(cuentaService);
    }

    @Test
    void testRetiros_MonedasIncompatibles() throws DatosIncorrectosException, MonedasIncompatiblesException, CuentaNotExistException, FondosInsuficientesException {
        
        doNothing().when(movimientosValidator).validateMovimientos(movimientosDtoMock);
        doThrow(new MonedasIncompatiblesException("Monedas incompatibles"))
                .when(movimientosService).retirar(movimientosDtoMock);

        MonedasIncompatiblesException exception = assertThrows(
                MonedasIncompatiblesException.class,
                () -> movimientosController.retiros(movimientosDtoMock)
        );

        assertEquals("Monedas incompatibles", exception.getMessage());

        verify(movimientosValidator, times(1)).validateMovimientos(movimientosDtoMock);
        verify(movimientosService, times(1)).retirar(movimientosDtoMock);
        verifyNoInteractions(cuentaService);
    }

    @Test
    void testObtenerMovimientos_Success() throws CuentaNotExistException {
        
        LinkedList<Movimientos> movimientosList = new LinkedList<>();
        movimientosList.add(movimientosMock);

        cuentaMock.setMovimientos(movimientosList);
        when(cuentaService.findById(12345678L)).thenReturn(cuentaMock);

        LinkedList<Movimientos> response = movimientosController.obtenerMovimientos(12345678L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(movimientosMock, response.getFirst());

        verify(cuentaService, times(1)).findById(12345678L);
    }

    @Test
    void testObtenerMovimientos_CuentaNoExiste() throws CuentaNotExistException {
        
        when(cuentaService.findById(99999999L)).thenThrow(new CuentaNotExistException("Cuenta no encontrada"));

        CuentaNotExistException exception = assertThrows(
                CuentaNotExistException.class,
                () -> movimientosController.obtenerMovimientos(99999999L)
        );

        assertEquals("Cuenta no encontrada", exception.getMessage());

        verify(cuentaService, times(1)).findById(99999999L);
    }
}

