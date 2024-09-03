package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaService {

    @Autowired
    CuentaDao cuentaDao;

    @Autowired
    ClienteService clienteService;

    public Cuenta darDeAltaCuenta(CuentaDto cuentaDto) throws CuentaAlreadyExistsException, TipoCuentaNoSoportadaException, ClienteAlreadyExistsException {
        Cuenta cuenta = new Cuenta(cuentaDto);

        if(cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        if (!tipoCuentaEstaSoportada(cuenta)) {
            throw new TipoCuentaNoSoportadaException("El tipo de cuenta " + cuenta.getTipoCuenta() + " no esta soportada.");
        }
        
        clienteService.addCuentaToCliente(cuenta, cuentaDto.getDniTitular());
        cuentaDao.save(cuenta);
        return cuenta;
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

    public List<Cuenta> showCuentas() {
        return cuentaDao.findAll();
    }

    public Cuenta findById(long id) {
        return cuentaDao.find(id);
    }
}