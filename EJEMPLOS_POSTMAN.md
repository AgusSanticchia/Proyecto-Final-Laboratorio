# Banco - Datos de Prueba 🏦

Este archivo contiene todos los datos de prueba en un solo bloque JSON para facilitar pruebas rápidas.

```json
{
  "clientes": [
    {
      "nombre": "Agustin",
      "apellido": "Santicchia",
      "dni": 78945623,
      "fechaNacimiento": "2005-05-21",
      "direccion": "Calle Falsa 123",
      "telefono": "+54 9 11 4321-5678",
      "banco": "Banco de la Nación Argentina",
      "tipoPersona": "F"
    },
    {
      "nombre": "Andoni",
      "apellido": "Roque",
      "dni": 98765432,
      "fechaNacimiento": "2001-11-30",
      "direccion": "Calle Verdadera 321",
      "telefono": "+54 9 11 8765-4321",
      "banco": "Banco Galicia",
      "tipoPersona": "J"
    },
    {
      "nombre": "Luciano",
      "apellido": "Salotto",
      "dni": 12345678,
      "fechaNacimiento": "1978-02-07",
      "direccion": "Avenida Sospechosa 213",
      "telefono": "+54 9 11 555-4321",
      "banco": "Banco Comercial",
      "tipoPersona": "F"
    }
  ],

  "cuentas": [
    {
      "dniTitular": 78945623,
      "tipoCuenta": "CA$",
      "tipoMoneda": "ARS"
    },
    {
      "dniTitular": 98765432,
      "tipoCuenta": "CA$",
      "tipoMoneda": "ARS"
    },
    {
      "dniTitular": 12345678,
      "tipoCuenta": "CA$",
      "tipoMoneda": "ARS"
    }
  ],
  "depositos": [
    {
      "monto": 15000.00,
      "numeroCuenta": 10000001,
      "tipoMoneda": "ARS"
    },
    {
      "monto": 20000.00,
      "numeroCuenta": 10000002,
      "tipoMoneda": "ARS"
    },
    {
      "monto": 5000.00,
      "numeroCuenta": 10000003,
      "tipoMoneda": "ARS"
    }
  ],
  "transferencias": [
    {
      "monto": 3000.00,
      "cuentaOrigen": 10000001,
      "cuentaDestino": 10000002,
      "tipoMoneda": "ARS"
    },
    {
      "monto": 1000.00,
      "cuentaOrigen": 10000002,
      "cuentaDestino": 10000003,
      "tipoMoneda": "ARS"
    },
    {
      "monto": 2500.00,
      "cuentaOrigen": 10000003,
      "cuentaDestino": 10000001,
      "tipoMoneda": "ARS"
    }
  ]
}
