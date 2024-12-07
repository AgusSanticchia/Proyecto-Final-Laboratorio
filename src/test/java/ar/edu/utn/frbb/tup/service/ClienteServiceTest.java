package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;
import ar.edu.utn.frbb.tup.exception.clientes.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.exception.clientes.MenorDeEdadException;
import ar.edu.utn.frbb.tup.exception.clientes.TipoPersonaNoSoportadaException;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteDao clienteDao;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteDto clienteDto;
    private Cliente cliente;
    private DateTimeFormatter formatter;

    @BeforeEach
    public void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaNacimiento = LocalDate.of(2005, 5, 21);
        clienteDto = new ClienteDto(
                "Rino",
                "Pepe",
                12345678,
                fechaNacimiento.format(formatter),
                "Calle Falsa 123",
                "1234567890",
                "Provincia",
                "F"
        );

        cliente = new Cliente(clienteDto);
    }

    @Test
    public void testDarDeAltaClienteNuevo() throws MenorDeEdadException, DatosIncorrectosException, TipoPersonaNoSoportadaException, ClienteAlreadyExistsException {
        when(clienteDao.find(clienteDto.getDni())).thenReturn(null);
        doNothing().when(clienteDao).save(any(Cliente.class));

        Cliente result = clienteService.darDeAltaCliente(clienteDto);

        assertNotNull(result);
        assertEquals(12345678, result.getDni());
        assertEquals(TipoPersona.PERSONA_FISICA, result.getTipoPersona());
        assertEquals("Provincia", result.getBanco());
        verify(clienteDao).save(any(Cliente.class));
    }

    @Test
    void testTieneCuenta() {
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);
        cuenta1.setTipoMoneda(TipoMoneda.PESOS);
        cliente.addCuenta(cuenta1);

        assertTrue(cliente.tieneCuenta(TipoCuenta.CAJA_AHORRO_PESOS, TipoMoneda.PESOS));
        assertFalse(cliente.tieneCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS, TipoMoneda.PESOS));
        assertFalse(cliente.tieneCuenta(TipoCuenta.CAJA_AHORRO_DOLAR_US, TipoMoneda.DOLARES));
    }

    @Test
    void testEdad() {
        Cliente cliente = new Cliente(new ClienteDto(
                "Agus",
                "Santi",
                12345678,
                LocalDate.now().minusYears(18).format(formatter),
                "123 Main St",
                "1234567890",
                "Provincia",
                "J"
        ));
        int expectedAge = Period.between(cliente.getFechaNacimiento(), LocalDate.now()).getYears();
        assertEquals(expectedAge, cliente.edad());
    }

    @Test
    public void testDarDeAltaClienteExistente() throws DatosIncorrectosException {
        when(clienteDao.find(clienteDto.getDni())).thenReturn(cliente);

        ClienteAlreadyExistsException exception = assertThrows(
                ClienteAlreadyExistsException.class,
                () -> clienteService.darDeAltaCliente(clienteDto)
        );

        assertEquals("Ya existe un cliente con el DNI: 12345678", exception.getMessage());
        verify(clienteDao, never()).save(any(Cliente.class));
    }

    @Test
    public void testAddCuentaToCliente() throws ClienteNotFoundException, CuentaAlreadyExistsException, DatosIncorrectosException {
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        when(clienteDao.find(cliente.getDni())).thenReturn(cliente);
        doNothing().when(clienteDao).save(any(Cliente.class));

        clienteService.addCuentaToCliente(cuenta, cliente.getDni());

        verify(clienteDao).save(cliente);
        assertTrue(cliente.tieneCuenta(TipoCuenta.CAJA_AHORRO_PESOS, TipoMoneda.PESOS));
    }

    @Test
    public void testAddCuentaToClienteAlreadyExists() throws ClienteNotFoundException {
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        cliente.addCuenta(cuenta);
        when(clienteDao.find(cliente.getDni())).thenReturn(cliente);

        CuentaAlreadyExistsException exception = assertThrows(
                CuentaAlreadyExistsException.class,
                () -> clienteService.addCuentaToCliente(cuenta, cliente.getDni())
        );

        assertEquals("El cliente ya tiene una cuenta de tipo: CAJA_AHORRO_PESOS y de tipo de moneda: PESOS", exception.getMessage());
    }

    @Test
    public void testBuscarClientePorDni() throws ClienteNotFoundException {
        when(clienteDao.find(cliente.getDni())).thenReturn(cliente);

        Cliente result = clienteService.buscarClientePorDni(cliente.getDni());

        assertNotNull(result);
        assertEquals(cliente, result);
    }
    @Test
    void testToString() {
        String expectedString = "Cliente{" +
                "nombre='" + cliente.getNombre() + '\'' +
                ", apellido='" + cliente.getApellido() + '\'' +
                ", dni=" + cliente.getDni() +
                ", fechaNacimiento=" + cliente.getFechaNacimiento() +
                ", telefono='" + cliente.getTelefono() + '\'' +
                ", direccion='" + cliente.getDireccion() + '\'' +
                ", edad=" + cliente.edad() +
                ", tipoPersona=" + cliente.getTipoPersona() +
                ", banco='" + cliente.getBanco() + '\'' +
                ", fechaAlta=" + cliente.getFechaAlta() +
                ", cuentas=" + cliente.getCuentas() +
                '}';

        assertEquals(expectedString, cliente.toString());
    }

    @Test
    public void testBuscarClientePorDniNotFound() {
        when(clienteDao.find(anyLong())).thenReturn(null);

        ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> clienteService.buscarClientePorDni(123L)
        );

        assertEquals("No se encontró un cliente con el DNI: 123", exception.getMessage());
    }


    @Test
    public void testShowClientes() {
        List<Cliente> clientes = Arrays.asList(cliente, new Cliente(new ClienteDto(
                "Agus",
                "Santi",
                12345678,
                LocalDate.now().minusYears(30).format(formatter),
                "Calle falsa 123",
                "1234567890",
                "Provincia",
                "J"
        )));
        when(clienteDao.findAllClientes()).thenReturn(clientes);
        List<Cliente> result = clienteService.showClientes();

        assertNotNull(result);
        assertEquals(clientes.size(), result.size());
        assertEquals(clientes, result);
    }

    @Test
    void testAddCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);

        cliente.addCuenta(cuenta);

        assertEquals(1, cliente.getCuentas().size());
        assertTrue(cliente.getCuentas().contains(cuenta));
    }

    @Test
    void testSetTipoPersona() {
        cliente.setTipoPersona(TipoPersona.PERSONA_JURIDICA);
        assertEquals(TipoPersona.PERSONA_JURIDICA, cliente.getTipoPersona());
    }


    @Test
    void testSetCuentas() {
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);
        cuenta1.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);
        cuenta2.setTipoMoneda(TipoMoneda.PESOS);

        Set<Cuenta> cuentas = new HashSet<>(Arrays.asList(cuenta1, cuenta2));
        cliente.setCuenta(Arrays.asList(cuenta1, cuenta2));

        assertEquals(cuentas, cliente.getCuentas());
    }

    @Test
    public void testAddMultipleCuentasToCliente() throws ClienteNotFoundException, CuentaAlreadyExistsException, DatosIncorrectosException {
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setTipoCuenta(TipoCuenta.CAJA_AHORRO_PESOS);
        cuenta1.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS);
        cuenta2.setTipoMoneda(TipoMoneda.PESOS);

        when(clienteDao.find(cliente.getDni())).thenReturn(cliente);

        clienteService.addCuentaToCliente(cuenta1, cliente.getDni());
        clienteService.addCuentaToCliente(cuenta2, cliente.getDni());

        verify(clienteDao, times(2)).save(cliente);
        assertTrue(cliente.tieneCuenta(TipoCuenta.CAJA_AHORRO_PESOS, TipoMoneda.PESOS));
        assertTrue(cliente.tieneCuenta(TipoCuenta.CUENTA_CORRIENTE_PESOS, TipoMoneda.PESOS));
    }

    @Test
    public void testClienteAlreadyExistsExceptionMessage() {
        when(clienteDao.find(clienteDto.getDni())).thenReturn(cliente);

        ClienteAlreadyExistsException exception = assertThrows(
                ClienteAlreadyExistsException.class,
                () -> clienteService.darDeAltaCliente(clienteDto)
        );

        assertEquals("Ya existe un cliente con el DNI: 12345678", exception.getMessage());
    }

}