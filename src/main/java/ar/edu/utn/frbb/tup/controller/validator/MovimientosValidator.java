package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.TipoCuentaNoSoportadaException;
import org.springframework.stereotype.Component;

@Component
public class MovimientosValidator {

  public void validateMovimientos(MovimientosDto movimientosDto) throws DatosIncorrectosException, MonedasIncompatiblesException {
    if (movimientosDto == null) {
      throw new DatosIncorrectosException("El movimiento no puede ser nulo");
    }
    validateMonto(movimientosDto.getMonto());
    validateTipoMoneda(TipoMoneda.fromString(movimientosDto.getTipoMoneda()));
  }

  public void validateMovimientosTransferencias(MovimientosTransferenciasDto movimientosTransferenciasDto)
          throws TipoCuentaNoSoportadaException, DatosIncorrectosException, MonedasIncompatiblesException {
    if (movimientosTransferenciasDto == null) {
      throw new DatosIncorrectosException("La transferencia no puede ser nula");
    }

    validateMonto(movimientosTransferenciasDto.getMonto());
    validateTipoMoneda(TipoMoneda.fromString(movimientosTransferenciasDto.getTipoMoneda()));

    if (movimientosTransferenciasDto.getCuentaOrigen() == movimientosTransferenciasDto.getCuentaDestino()) {
      throw new TipoCuentaNoSoportadaException("La cuenta origen y destino no pueden ser la misma");
    }
  }

  private void validateMonto(Double monto) throws DatosIncorrectosException {
    if (monto == null || monto <= 0.0) {
      throw new DatosIncorrectosException("El monto debe ser mayor que cero");
    }
  }

  private void validateTipoMoneda(TipoMoneda tipoMoneda) throws MonedasIncompatiblesException {
    if (tipoMoneda == null) {
      throw new MonedasIncompatiblesException("El tipo de moneda no puede ser nulo");
    }
  }
}
