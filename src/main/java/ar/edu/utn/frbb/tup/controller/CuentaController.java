package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.exception.cuentas.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaValidator cuentaValidator;

    @Operation(summary = "Obtener todas las cuentas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cuentas")
    })
    @GetMapping("/")
    public List<Cuenta> getAllCuentas() {
        return cuentaService.showCuentas();
    }

    @Operation(summary = "Crear una nueva cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de entrada"),
            @ApiResponse(responseCode = "400", description = "La cuenta ya existe")
    })
    @PostMapping
    public ResponseEntity<Cuenta> createCuenta(@RequestBody CuentaDto cuentaDto)
            throws TipoCuentaNoSoportadaException, CuentaAlreadyExistsException, MonedasIncompatiblesException, CuentaNotExistException, ClienteNotFoundException, DatosIncorrectosException {
        cuentaValidator.validateCuenta(cuentaDto);
        return new ResponseEntity<>( cuentaService.darDeAltaCuenta(cuentaDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener cuenta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{idCuenta}")
    public ResponseEntity<Cuenta> getCuentaById(@Parameter(description = "ID de la cuenta a buscar", required = true) @PathVariable long idCuenta) throws CuentaNotExistException {
        return new ResponseEntity<>(cuentaService.findById(idCuenta), HttpStatus.OK);
    }
}
