package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.model.exception.clientes.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.clientes.TipoPersonaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private ClienteValidator clienteValidator;

    public Cliente darDeAltaCliente(ClienteDto clienteDto) throws ClienteAlreadyExistsException, DatosIncorrectosException, TipoPersonaNoSoportadaException {
        clienteValidator.validate(clienteDto);

        Cliente clienteExistente = clienteDao.find(clienteDto.getDni());
        if (clienteExistente != null) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con el DNI: " + clienteDto.getDni());
        }

        Cliente nuevoCliente = new Cliente(clienteDto);
        clienteDao.save(nuevoCliente);

        return nuevoCliente;
    }

    public void addCuentaToCliente(Cuenta cuenta, long dniTitular) throws CuentaAlreadyExistsException, DatosIncorrectosException, ClienteNotFoundException {
        Cliente cliente = buscarClientePorDni(dniTitular);

        if (cliente.tieneCuenta(cuenta.getTipoCuenta(), cuenta.getTipoMoneda())) {
            throw new CuentaAlreadyExistsException(
                    "El cliente ya tiene una cuenta de tipo: " + cuenta.getTipoCuenta() +
                            " y de tipo de moneda: " + cuenta.getTipoMoneda());
        }

        cliente.addCuenta(cuenta);
        clienteDao.save(cliente);
    }

    public Cliente buscarClientePorDni(long dni) throws ClienteNotFoundException {
        Cliente cliente = clienteDao.find(dni);

        if (cliente == null) {
            throw new ClienteNotFoundException("No se encontró un cliente con el DNI: " + dni);
        }
        return cliente;
    }

    public List<Cliente> showClientes() {
        return clienteDao.findAllClientes();
    }
}
