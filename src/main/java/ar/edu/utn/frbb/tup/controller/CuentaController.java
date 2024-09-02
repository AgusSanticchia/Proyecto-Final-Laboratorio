package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaValidator cuentaValidator;

    @Autowired
    private ClienteValidator clienteValidator;

    @PostMapping
    public ResponseEntity<String> createCuenta(@RequestBody CuentaDto cuentaDto, @RequestBody ClienteDto clienteDto) {
        cuentaValidator.validateCuenta(cuentaDto);
        try {
            cuentaService.darDeAltaCuenta(cuentaDto, clienteDto);
            return ResponseEntity.ok("Cuenta creada exitosamente");
        } catch (CuentaAlreadyExistsException | EdadInvalidaException | DatosIncorrectosException | TipoCuentaNoSoportadaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
}