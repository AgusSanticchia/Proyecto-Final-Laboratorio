package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.exception.clientes.MenorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.clientes.TipoPersonaNoSoportadaException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

@Component
public class ClienteValidator {

    public void validate(ClienteDto clienteDto) throws TipoPersonaNoSoportadaException, DatosIncorrectosException {
        validateTipoPersona(clienteDto);
        validateFechaDeNacimiento(clienteDto);
        validateEdad(clienteDto);
    }

    private void validateEdad(ClienteDto clienteDto) throws DatosIncorrectosException {
        try {
            LocalDate fechaNacimiento = LocalDate.parse(clienteDto.getFechaNacimiento());
            int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
            if (edad < 18) {
                throw new MenorDeEdadException("El cliente debe ser mayor de edad");
            }
        } catch (DateTimeParseException e) {
            throw new DatosIncorrectosException("Formato de fecha de nacimiento incorrecto. Use yyyy-MM-dd.");
        }
    }

    private void validateTipoPersona(ClienteDto clienteDto) throws TipoPersonaNoSoportadaException {
        if (!"F".equals(clienteDto.getTipoPersona()) && !"J".equals(clienteDto.getTipoPersona())) {
            throw new TipoPersonaNoSoportadaException("El tipo de persona no es correcto. Debe ser 'F' o 'J'.");
        }
    }

    private void validateFechaDeNacimiento(ClienteDto clienteDto) throws DatosIncorrectosException {
        try {
            LocalDate.parse(clienteDto.getFechaNacimiento());
        } catch (DateTimeParseException e) {
            throw new DatosIncorrectosException("Fecha de nacimiento incorrecta. Debe estar en formato yyyy-MM-dd");
        }
    }
}