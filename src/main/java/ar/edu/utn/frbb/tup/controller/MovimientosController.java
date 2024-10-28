package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.controller.validator.MovimientosValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.model.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.CuentaNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.service.MovimientosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientosController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private MovimientosService movimientosService;

    @Autowired
    private MovimientosValidator movimientosValidator;

    @Operation(summary = "Realizar una transferencia entre cuentas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia realizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de entrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Fondos insuficientes o monedas incompatibles")
    })
    @PostMapping("/transferencia")
    public Movimientos transferencia(@RequestBody MovimientosTransferenciasDto movimientosDto) throws DatosIncorrectosException, MonedasIncompatiblesException, CuentaNotExistException, CuentaNotFoundException, TipoCuentaNoSoportadaException, FondosInsuficientesException {

        movimientosValidator.validateMovimientosTransferencias(movimientosDto);
        movimientosService.transferir(movimientosDto);
        Cuenta cuenta = cuentaService.findById(movimientosDto.getCuentaOrigen());
        return cuenta.getMovimientos().getLast();
    }

    @Operation(summary = "Realizar un depósito en la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de entrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Monedas incompatibles")
    })
    @PostMapping("/depositos")
    public Movimientos depositos(@RequestBody MovimientosDto movimientosDto) throws DatosIncorrectosException, MonedasIncompatiblesException, CuentaNotExistException {

        movimientosValidator.validateMovimientos(movimientosDto);
        movimientosService.depositar(movimientosDto);
        Cuenta cuenta = cuentaService.findById(movimientosDto.getNumeroCuenta());
        return cuenta.getMovimientos().getLast();
    }

    @Operation(summary = "Realizar un retiro de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retiro realizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de entrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Fondos insuficientes o monedas incompatibles")
    })
    @PostMapping("/retiros")
    public Movimientos retiros(@RequestBody MovimientosDto movimientosDto) throws DatosIncorrectosException, MonedasIncompatiblesException, CuentaNotExistException, FondosInsuficientesException, CuentaNotFoundException {

        movimientosValidator.validateMovimientos(movimientosDto);
        movimientosService.retirar(movimientosDto);
        Cuenta cuenta = cuentaService.findById(movimientosDto.getNumeroCuenta());
        return cuenta.getMovimientos().getLast();
    }

    @Operation(summary = "Obtener movimientos de una cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos obtenidos con éxito"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{cuentaId}")
    public LinkedList<Movimientos> obtenerMovimientos(@Parameter(description = "ID de la cuenta a buscar", required = true) @PathVariable long cuentaId)
            throws CuentaNotExistException {
        Cuenta cuenta = cuentaService.findById(cuentaId);
        return cuenta.getMovimientos();
    }
}
