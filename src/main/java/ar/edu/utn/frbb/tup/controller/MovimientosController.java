package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.controller.validator.MovimientosValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.service.MovimientosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@RequestMapping("/movimientos")
public class MovimientosController {

    @Autowired
    CuentaService cuentaService;

    @Autowired
    private MovimientosService movimientosService;

    @Autowired
    private MovimientosValidator movimientosValidator;

    @PostMapping("/transferencia")
    public Movimientos transferencia(@RequestBody MovimientosTransferenciasDto movimientosDto) throws CuentaNotFoundException, TipoMonedaNoSoportadaException, FondosInsuficientesException, MonedasIncompatiblesException, TipoCuentaNoSoportadaException {
        movimientosValidator.validateMovimientosTransferencias(movimientosDto);
        movimientosService.transferir(movimientosDto);
        Cuenta cuenta = cuentaService.findById(movimientosDto.getNumeroCuentaDestino());
        return cuenta.getMovimientos().getLast();
    }

    @PostMapping("/depositos")
    public ResponseEntity<Movimientos> depositos(@RequestBody MovimientosDto movimientosDto)
            throws CuentaNotFoundException, MonedasIncompatiblesException {

        movimientosValidator.validateMovimientos(movimientosDto);
        Movimientos deposito = movimientosService.depositar(movimientosDto);
        return ResponseEntity.ok(deposito);
    }

    @PostMapping("/retiros")
    public ResponseEntity<Movimientos> retiros(@RequestBody MovimientosDto movimientosDto)
            throws FondosInsuficientesException, CuentaNotFoundException, MonedasIncompatiblesException {

        movimientosValidator.validateMovimientos(movimientosDto);
        Movimientos retiro = movimientosService.retirar(movimientosDto);
        return ResponseEntity.ok(retiro);
    }

    @GetMapping("/{cuentaId}")
    public LinkedList<Movimientos> obtenerMovimientos(@PathVariable long numeroCuenta) throws CuentaNotFoundException {
        Cuenta cuenta = cuentaService.findById(numeroCuenta);
        return cuenta.getMovimientos();
    }
}