package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.exception.cuentas.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaService {

    @Autowired
    private CuentaDao cuentaDao;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CuentaValidator cuentaValidator;

    public Cuenta darDeAltaCuenta(CuentaDto cuentaDto) throws CuentaAlreadyExistsException, ClienteNotFoundException, DatosIncorrectosException {
        Cuenta cuenta = new Cuenta(cuentaDto);

        if (cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }
        clienteService.addCuentaToCliente(cuenta, cuentaDto.getDniTitular());
        cuentaDao.save(cuenta);
        return cuenta;
    }

    public List<Cuenta> showCuentas() {
        return cuentaDao.findAll();
    }

    public Cuenta findById(long id) throws CuentaNotExistException {
        Cuenta cuenta = cuentaDao.find(id);
        if (cuenta == null) {
            throw new CuentaNotExistException("La cuenta no existe.");
        }
        return cuenta;
    }
}
