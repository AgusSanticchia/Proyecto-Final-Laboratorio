package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import org.springframework.stereotype.Component;

@Component
public class MovimientosValidator {
    public void validateMovimientos(MovimientosDto movimientosDto){
        validateMoneda(movimientosDto);
        validateTipoOperacion(movimientosDto);
    }

    public void validateMoneda(MovimientosDto movimientosDto){
        try {
            TipoMoneda.fromString(movimientosDto.getMoneda());
        } catch (IllegalArgumentException e) {
            throw new DatosIncorrectosException("El tipo de moneda  no es valido");
        }
    }

    public void validateTipoOperacion(MovimientosDto movimientosDto){
        try{
            TipoOperacion.fromString(movimientosDto.getTipoOperacion());
        } catch (IllegalArgumentException e) {
            throw new DatosIncorrectosException("El tipo de operacion es invalida");
        }
    }
}
