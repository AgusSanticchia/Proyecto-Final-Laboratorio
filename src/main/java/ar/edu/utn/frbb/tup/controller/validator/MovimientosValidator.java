package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import org.springframework.stereotype.Component;

@Component
public class MovimientosValidator {

    public void validateMovimientos(MovimientosDto movimientosDto) throws DatosIncorrectosException {
        validateDatos(movimientosDto);
    }

    public void validateMovimientosTransferencias(MovimientosTransferenciasDto movimientosTransferenciasDto) throws DatosIncorrectosException {
        validateDatosTransferencias(movimientosTransferenciasDto);
    }

    private void validateDatosTransferencias(MovimientosTransferenciasDto movimientosTransferenciasDto) throws DatosIncorrectosException {
        if (movimientosTransferenciasDto.getMonto() == null) {
            throw new DatosIncorrectosException("Error: Ingrese un monto");
        }
        if (movimientosTransferenciasDto.getMonto() <= 0) {
            throw new DatosIncorrectosException("Error: El monto debe ser mayor que 0");
        }
        if (movimientosTransferenciasDto.getCuentaDestino() == movimientosTransferenciasDto.getCuentaOrigen()) {
            throw new DatosIncorrectosException("Error: La cuenta de origen y la cuenta de destino no pueden ser iguales");
        }
        if (movimientosTransferenciasDto.getCuentaDestino() == 0 || movimientosTransferenciasDto.getCuentaOrigen() == 0) {
            throw new DatosIncorrectosException("Error: La cuenta de origen o la cuenta de destino no pueden ser 0");
        }
    }

    private void validateDatos(MovimientosDto movimientosDto) throws DatosIncorrectosException {
        if (movimientosDto.getMonto() == null) {
            throw new DatosIncorrectosException("Error: Ingrese un monto");
        }
        if (movimientosDto.getMonto() <= 0) {
            throw new DatosIncorrectosException("Error: El monto debe ser mayor que 0");
        }
    }
}
