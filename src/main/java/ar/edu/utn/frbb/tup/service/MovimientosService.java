/*package ar.edu.utn.frbb.tup.service;

import org.springframework.stereotype.Service;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class MovimientosService {

    @Autowired CuentaDao cuentaDao;
    @Autowired MovimientosDto movimientosDto;
    @Autowired MovimientosTransferenciasDto movimientosTransferenciaDto;

    public void transferir(MovimientosTransferenciasDto movimientosTransferenciaDto, double monto) {
        Cuenta cuentaOrigen = cuentaDao.find(movimientosTransferenciaDto.getNumeroCuentaOrigen());
        Cuenta cuentaDestino = cuentaDao.find(movimientosTransferenciaDto.getNumeroCuentaDestino());

        cuentaOrigen.setBalance(cuentaOrigen.getBalance() - monto);
        cuentaDestino.setBalance(cuentaDestino.getBalance() + monto);
        System.out.println("Transferencia exitosa");
    }

    public void depositar(Cuenta cuentaParam, double monto) {
        Cuenta cuenta = cuentaDao.find(cuentaParam.getNumeroCuenta());
        if (cuenta == null) {
            System.out.println("Cuenta no encontrada");
        } else {
            cuenta.setBalance(cuenta.getBalance() + monto);
            System.out.println("Deposito exitoso");
        }
    }

    public void retirar(Cuenta cuentaParam, double monto) {
        Cuenta cuenta = cuentaDao.find(movimientosDto.getNumeroCuenta());
        if (cuenta == null) {
            System.out.println("Cuenta no encontrada");
        } else {
            cuenta.setBalance(cuenta.getBalance() - monto);
            System.out.println("Retiro exitoso");
            
        }
    }

}
*/