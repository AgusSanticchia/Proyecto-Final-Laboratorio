package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaValidator cuentaValidator;

    @PostMapping
    public ResponseEntity<Cuenta> createCuenta(@RequestBody CuentaDto cuentaDto) throws TipoCuentaNoSoportadaException, CuentaAlreadyExistsException, ClienteAlreadyExistsException, Exception {
        cuentaValidator.validateCuenta(cuentaDto);
        Cuenta cuenta = cuentaService.darDeAltaCuenta(cuentaDto);
        return new ResponseEntity<>(cuenta, HttpStatus.CREATED);
    }
    @GetMapping("/{idCuenta}")
    public ResponseEntity<Cuenta> getCuentaById(@PathVariable long idCuenta) {
        Cuenta cuenta = cuentaService.findById(idCuenta);
        if (cuenta != null) {
            return ResponseEntity.ok(cuenta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity <List<Cuenta>> getAllCuentas() {
        List<Cuenta> cuentas = cuentaService.showCuentas();
        return ResponseEntity.ok(cuentas);
    }
}