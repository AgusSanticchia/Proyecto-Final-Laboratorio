package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.exception.EdadInvalidaException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

@Component
public class ClienteValidator {

    public void validate(ClienteDto clienteDto) throws DatosIncorrectosException {
        validateTipoPersona(clienteDto);
        validateFechaDeNacimiento(clienteDto);
        validateEdad(clienteDto);
    }

    private void validateEdad(ClienteDto clienteDto) throws DatosIncorrectosException, EdadInvalidaException {
        LocalDate fechaNacimiento = LocalDate.parse(clienteDto.getFechaNacimiento());
        LocalDate fechaActual = LocalDate.now();
        int edad = Period.between(fechaNacimiento, fechaActual).getYears();
        if (edad < 18) {
            throw new DatosIncorrectosException("El cliente debe ser mayor de edad");
        }
    }
    public void validateTipoPersona(ClienteDto clienteDto) throws DatosIncorrectosException {
        try {
            TipoPersona.fromString(clienteDto.getTipoPersona());
        } catch (IllegalArgumentException ex) {
            throw new DatosIncorrectosException("El tipo de persona no es correcto. Debe ser 'F' o 'J'.");
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
