package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.exception.EdadInvalidaException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class CuentaService {

    @Autowired
    CuentaDao cuentaDao;

    @Autowired
    ClienteService clienteService;

    public void darDeAltaCuenta(CuentaDto cuentaDto, ClienteDto clienteDto) throws CuentaAlreadyExistsException, TipoCuentaNoSoportadaException {
        Cliente cliente;
        try {
            cliente = clienteService.darDeAltaCliente(clienteDto);
        } catch (EdadInvalidaException | DatosIncorrectosException e) {
            throw e; // Re-lanzamos estas excepciones para que sean manejadas en el controlador
        }

        // Verificar si la cuenta ya existe
        if (cuentaDao.find(cuentaDto.getDniTitular()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuentaDto.getDniTitular() + " ya existe.");
        }

        // Crear la nueva cuenta
        Cuenta nuevaCuenta = new Cuenta(cuentaDto);
        nuevaCuenta.setDniTitular(cliente.getDni());

        // Verificar si el tipo de cuenta está soportado
        if (!tipoCuentaEstaSoportada(nuevaCuenta)) {
            throw new TipoCuentaNoSoportadaException("Este tipo de cuenta no es soportada. Por favor indicar si es de tipo CA$, CC$ o CAU$S");
        }

        // Guardar la nueva cuenta
        cuentaDao.save(nuevaCuenta);
    }
    public boolean tipoCuentaEstaSoportada(Cuenta cuenta){
        if (cuenta.getTipoMoneda() == TipoMoneda.DOLARES &&
                cuenta.getTipoCuenta() == TipoCuenta.CAJA_AHORRO_DOLAR_US){
            return true;
        }

        return cuenta.getTipoMoneda() == TipoMoneda.PESOS &&
                (cuenta.getTipoCuenta() == TipoCuenta.CUENTA_CORRIENTE_PESOS ||
                        cuenta.getTipoCuenta() == TipoCuenta.CAJA_AHORRO_PESOS);

    }

    public Cuenta findById(long id) {
        return cuentaDao.find(id);
    }
}