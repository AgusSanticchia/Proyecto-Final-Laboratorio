package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.exception.MenorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteValidator clienteValidator;

    @PostMapping
    public ResponseEntity<Cliente>  createCliente(@RequestBody ClienteDto clienteDto) throws MenorDeEdadException {
        clienteValidator.validate(clienteDto);
        Cliente cliente = clienteService.darDeAltaCliente(clienteDto);
        return new ResponseEntity<>(cliente, HttpStatus.CREATED);

    }

    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> getClientById(@PathVariable long dni) throws ClienteNotFoundException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity <List<Cliente>> getAllClients() {
        List<Cliente> clientes = clienteService.showClientes();
        return ResponseEntity.ok(clientes);
    }
}