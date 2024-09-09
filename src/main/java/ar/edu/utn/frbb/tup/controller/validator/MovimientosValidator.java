package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.exception.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaException;
import org.springframework.stereotype.Component;

@Component
public class MovimientosValidator {

  public void validateMovimientos(MovimientosDto movimientosDto) throws DatosIncorrectosException {
    if (movimientosDto == null) {
      throw new DatosIncorrectosException("El movimiento no puede ser nulo");
    }
    validateMonto(movimientosDto.getMonto());
    validateTipoMoneda(movimientosDto.getTipoMoneda());
  }

  public void validateMovimientosTransferencias(MovimientosTransferenciasDto movimientosTransferenciasDto)
          throws TipoCuentaNoSoportadaException, DatosIncorrectosException {
    if (movimientosTransferenciasDto == null) {
      throw new DatosIncorrectosException("La transferencia no puede ser nula");
    }

    validateMonto(movimientosTransferenciasDto.getMonto());
    validateTipoMoneda(movimientosTransferenciasDto.getTipoMoneda());

    if (movimientosTransferenciasDto.getNumeroCuentaOrigen() == movimientosTransferenciasDto.getNumeroCuentaDestino()) {
      throw new TipoCuentaNoSoportadaException("La cuenta origen y destino no pueden ser la misma");
    }
  }

  private void validateMonto(Double monto) throws DatosIncorrectosException {
    if (monto == null || monto <= 0.0) {
      throw new DatosIncorrectosException("El monto debe ser mayor que cero");
    }
  }

  private void validateTipoMoneda(String tipoMoneda) throws MonedasIncompatiblesException, DatosIncorrectosException {
    if (tipoMoneda == null || tipoMoneda.trim().isEmpty()) {
      throw new DatosIncorrectosException("El tipo de moneda no puede ser nulo o vacío");
    }
    try {
      TipoMoneda.fromString(tipoMoneda.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new MonedasIncompatiblesException("Tipo de moneda no válido: " + tipoMoneda);
    }
  }

}
