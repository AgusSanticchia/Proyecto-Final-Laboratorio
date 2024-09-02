package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.validator.MovimientosValidator;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.exception.FondosInsuficientesException;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.service.MovimientosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/movimientos")
public class MovimientosController {
    @Autowired
    CuentaService cuentaService;

    @Autowired
    MovimientosService movimientosService;

    @Autowired
    private MovimientosValidator movimientosValidator;


    @PostMapping("/transferencia")
    public Movimientos transferencia(@RequestBody MovimientosDto movimientosDto) throws FondosInsuficientesException {
        movimientosValidator.validateMovimientos(movimientosDto);
        movimientosService.transferencia(movimientosDto);
        return null; //null provisorio como el resto de los nulls
    }

    @PostMapping("/depositos")
    public Movimientos deposito(@RequestBody MovimientosDto movimientosDto){
        movimientosValidator.validateMovimientos(movimientosDto);
        movimientosService.deposito(movimientosDto);
        return null;
    }

    @PostMapping("/retiros")
    public Movimientos retiro(@RequestBody MovimientosDto movimientosDto) throws FondosInsuficientesException {
        movimientosValidator.validateMovimientos(movimientosDto);
        movimientosService.retiro(movimientosDto);
        return null;
    }

}