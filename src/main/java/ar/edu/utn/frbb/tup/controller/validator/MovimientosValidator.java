package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import org.springframework.stereotype.Component;

@Component
public class MovimientosValidator {

  public void validateMovimientos(MovimientosDto movimientoDto){
      validateMoneda(movimientoDto);
  }

  public void validateMoneda(MovimientosDto movimientoDto) throws DatosIncorrectosException {
    try {
      TipoMoneda.fromString(movimientoDto.getTipoMoneda());
    } catch (IllegalArgumentException ex) {
      throw new DatosIncorrectosException("La moneda no es correcta");
    }
  }
}
