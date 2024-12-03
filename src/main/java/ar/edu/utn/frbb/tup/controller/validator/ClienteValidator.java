package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.clientes.MenorDeEdadException;
import ar.edu.utn.frbb.tup.exception.clientes.TipoPersonaNoSoportadaException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

@Component
public class ClienteValidator {

    public void validate(ClienteDto clienteDto) throws TipoPersonaNoSoportadaException, DatosIncorrectosException, MenorDeEdadException {
        validateDatosCompletos(clienteDto);
        validateTipoPersona(clienteDto);
        validateFechaDeNacimiento(clienteDto);
        validateEdad(clienteDto);
    }

    private void validateDatosCompletos(ClienteDto clienteDto) throws DatosIncorrectosException {
        //Nombre
        if (clienteDto.getNombre() == null || clienteDto.getNombre().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un nombre");
        //Apellido
        if (clienteDto.getApellido() == null || clienteDto.getApellido().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un apellido");
        //Dni
        if (clienteDto.getDni() == 0) throw new IllegalArgumentException("Error: Ingrese un dni");
        if (clienteDto.getDni() < 10000000 || clienteDto.getDni() > 99999999) throw new IllegalArgumentException("Error: El dni debe tener 8 digitos");
        //Direccion
        if (clienteDto.getDireccion() == null || clienteDto.getDireccion().isEmpty()) throw new IllegalArgumentException("Error: Ingrese una direccion");
        //Fecha Nacimiento
        if (clienteDto.getFechaNacimiento() == null || clienteDto.getFechaNacimiento().isEmpty()) throw new IllegalArgumentException("Error: Ingrese una fecha de nacimiento");
        //Telefono
        if (clienteDto.getTelefono() == null || clienteDto.getTelefono().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un telefono");
        //Banco
        if (clienteDto.getBanco() == null || clienteDto.getBanco().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un banco");
        //Tipo persona
        if (clienteDto.getTipoPersona() == null || clienteDto.getTipoPersona().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un tipo de persona");
    }

    private void validateEdad(ClienteDto clienteDto) throws DatosIncorrectosException, MenorDeEdadException {
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