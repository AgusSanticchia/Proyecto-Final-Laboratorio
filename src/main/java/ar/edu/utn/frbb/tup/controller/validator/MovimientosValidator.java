package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.exception.cuentas.TipoCuentaNoSoportadaException;
import org.springframework.stereotype.Component;

@Component
public class  MovimientosValidator {

  public void validateMovimientos(MovimientosDto movimientosDto) throws DatosIncorrectosException{
    if (movimientosDto == null) {
      throw new DatosIncorrectosException("El movimiento no puede ser nulo");
    }
    validateMonto(movimientosDto.getMonto());
  }

  public void validateMovimientosTransferencias(MovimientosTransferenciasDto movimientosTransferenciasDto)
          throws TipoCuentaNoSoportadaException, DatosIncorrectosException {
    if (movimientosTransferenciasDto == null) {
      throw new DatosIncorrectosException("La transferencia no puede ser nula");
    }

    validateMonto(movimientosTransferenciasDto.getMonto());

    if (movimientosTransferenciasDto.getCuentaOrigen() == movimientosTransferenciasDto.getCuentaDestino()) {
      throw new TipoCuentaNoSoportadaException("La cuenta origen y destino no pueden ser la misma");
    }
  }

  private void validateMonto(Double monto) throws DatosIncorrectosException {
    if (monto == null || monto <= 0.0) {
      throw new DatosIncorrectosException("El monto debe ser mayor que cero");
    }
  }
}
