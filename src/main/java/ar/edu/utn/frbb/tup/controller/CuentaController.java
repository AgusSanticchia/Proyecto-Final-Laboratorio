package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.exception.clientes.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.monedas.TipoMonedaNoSoportadaException;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaValidator cuentaValidator;

    @Operation(summary = "Crear una nueva cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de entrada"),
            @ApiResponse(responseCode = "409", description = "La cuenta ya existe")
    })
    @PostMapping
    public Cuenta createCuenta(@RequestBody CuentaDto cuentaDto)
            throws TipoCuentaNoSoportadaException, CuentaAlreadyExistsException, TipoMonedaNoSoportadaException, MonedasIncompatiblesException, CuentaNotExistException, ClienteNotFoundException, DatosIncorrectosException {
        cuentaValidator.validateCuenta(cuentaDto);
        return cuentaService.darDeAltaCuenta(cuentaDto);
    }

    @Operation(summary = "Obtener cuenta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{idCuenta}")
    public Cuenta getCuentaById(@Parameter(description = "ID de la cuenta a buscar", required = true) @PathVariable long idCuenta) throws CuentaNotExistException {
        return cuentaService.findById(idCuenta);
    }

    @Operation(summary = "Obtener todas las cuentas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cuentas")
    })
    @GetMapping("/all")
    public List<Cuenta> getAllCuentas() {
        return cuentaService.showCuentas();
    }
}
