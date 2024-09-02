package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class ClienteService {

    @Autowired
    ClienteDao clienteDao;

    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    public Cliente darDeAltaCliente(ClienteDto clienteDto) throws EdadInvalidaException, DatosIncorrectosException {
        Cliente cliente = clienteDao.find(clienteDto.getDni());
        if (cliente != null) {
            return cliente;
        }

        validarEdad(clienteDto);
        Cliente nuevoCliente = new Cliente(clienteDto);
        clienteDao.save(nuevoCliente);
        return nuevoCliente;
    }

    private void validarEdad(ClienteDto clienteDto) throws EdadInvalidaException {
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaNacimiento = LocalDate.parse(clienteDto.getFechaNacimiento());
        Period periodo = Period.between(fechaNacimiento, fechaActual);

        if (periodo.getYears() < 18) {
            throw new EdadInvalidaException("Error en crear la cuenta, la persona es menor de edad");
        }
    }

    public Cliente buscarClientePorDni(long dni) throws ClienteNotFoundException {
        Cliente cliente = clienteDao.find(dni);
        if (cliente == null) {
            throw new ClienteNotFoundException("No se encontró un cliente con el DNI: " + dni);
        }
        return cliente;
    }
}