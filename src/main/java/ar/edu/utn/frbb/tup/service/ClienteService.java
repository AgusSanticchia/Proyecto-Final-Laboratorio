package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ClienteService {

    @Autowired
    ClienteDao clienteDao;

    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    public Cliente darDeAltaCliente(ClienteDto clienteDto) throws MenorDeEdadException, DatosIncorrectosException {
        Cliente cliente = clienteDao.find(clienteDto.getDni());
        if (cliente != null) {
            return cliente;
        }
        validarEdad(clienteDto);
        Cliente nuevoCliente = new Cliente(clienteDto);
        clienteDao.save(nuevoCliente);
        return nuevoCliente;
    }

    private void validarEdad(ClienteDto clienteDto) throws MenorDeEdadException {
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaNacimiento = LocalDate.parse(clienteDto.getFechaNacimiento());
        Period periodo = Period.between(fechaNacimiento, fechaActual);

        if (periodo.getYears() < 18) {
            throw new MenorDeEdadException("Error en crear la cuenta, la persona es menor de edad");
        }
    }

    public void addCuentaToCliente(Cuenta cuenta, long dniTitular) throws ClienteAlreadyExistsException, ClienteNotFoundException {
        Cliente cliente = buscarClientePorDni(dniTitular);
        cuenta.setDniTitular(cliente.getDni());
        if (cliente.tieneCuenta(cuenta.getTipoCuenta(), cuenta.getTipoMoneda())){
            throw new ClienteAlreadyExistsException("El cliente ya tiene una cuenta de tipo: " + cuenta.getTipoCuenta() + " y de tipo de moneda: " + cuenta.getTipoMoneda());
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