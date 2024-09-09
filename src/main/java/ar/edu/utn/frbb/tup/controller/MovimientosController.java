package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.controller.validator.MovimientosValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.model.exception.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaException;
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
    private MovimientosService movimientosService;

    @Autowired
    private MovimientosValidator movimientosValidator;

    @Autowired
    private CuentaService cuentaService;

    @PostMapping("/transferencias")
    public ResponseEntity<Movimientos> transferencias(@RequestBody MovimientosTransferenciasDto movimientosTransferenciasDto)
            throws FondosInsuficientesException, TipoCuentaNoSoportadaException {

        movimientosValidator.validateMovimientosTransferencias(movimientosTransferenciasDto);
        movimientosService.transferir(movimientosTransferenciasDto);
        Cuenta cuenta = cuentaService.findById(movimientosTransferenciasDto.getNumeroCuentaOrigen());
        return ResponseEntity.ok(cuenta.getMovimientos().getLast());
    }

    @PutMapping("/depositos")
    public ResponseEntity<Movimientos> depositos(@RequestBody MovimientosDto movimientosDto)
            throws CuentaNotFoundException, MonedasIncompatiblesException {

        movimientosValidator.validateMovimientos(movimientosDto);
        movimientosService.depositar(movimientosDto);
        Cuenta cuenta = cuentaService.findById(movimientosDto.getNumeroCuenta());
        return ResponseEntity.ok(cuenta.getMovimientos().getLast());
    }

    @PutMapping("/retiros")
    public ResponseEntity<Movimientos> retiros(@RequestBody MovimientosDto movimientosDto)
            throws FondosInsuficientesException, CuentaNotFoundException, MonedasIncompatiblesException {

        movimientosValidator.validateMovimientos(movimientosDto);
        movimientosService.retirar(movimientosDto);
        Cuenta cuenta = cuentaService.findById(movimientosDto.getNumeroCuenta());
        return ResponseEntity.ok(cuenta.getMovimientos().getLast());
    }



    @GetMapping("/cuentaId")
    public LinkedList<Movimientos> getMovimientos(@PathVariable Long cuentaId) {
        Cuenta cuenta = cuentaService.findById(cuentaId);
        return cuenta.getMovimientos();
    }
}
