package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.controller.validator.MovimientosValidator;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.model.exception.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.service.MovimientosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movimientos")
public class MovimientosController {

    @Autowired
    private MovimientosService movimientosService;

    @Autowired
    private MovimientosValidator movimientosValidator;

    @PostMapping("/transferencias")
    public ResponseEntity<Movimientos> transferencias(@RequestBody MovimientosTransferenciasDto movimientosTransferenciasDto)
            throws FondosInsuficientesException, MonedasIncompatiblesException, CuentaNotFoundException {

        movimientosValidator.validateMovimientosTransferencias(movimientosTransferenciasDto);
        Movimientos transferencia = movimientosService.transferir(movimientosTransferenciasDto);
        return ResponseEntity.ok(transferencia);
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
}
