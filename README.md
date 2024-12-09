# tup2024
Proyecto Final Laboratorio III 
Simulacion de Banco con Servicio Externo usando Java, Springboot, Junit + Mockito Swagger y Maven

Para correr la app: mvn spring-boot:run

Acceso a la API a través de Swagger: http://localhost:8080/swagger-ui/index.html

## POST /api/cliente
 Para crear un cliente se deben pasar los siguientes datos:
 {
  "nombre": "string",
  "apellido": "string",
  "dni": 0,
  "fechaNacimiento": "string",  //Debe estar en formato yyyy-MM-dd
  "telefono": "string",
  "direccion": "string",
  "tipoPersona": "string"  //(F o J)
  "banco": "string"
}

## GET /api/cliente/
Se obtiene la lista de todos los clientes

## GET /api/cliente/{dni} 
Para obtener un cliente solo se debe pasar el dni del mismo

## POST /api/cuenta/
Paraa crear una cuneta se deben pasar los siguientes parametros
{
  "dniTitular": 0,
  "tipoCuenta": "string", // CC$ (CUENTA_CORRIENTE_PESOS), CA$ (CAJA_AHORRO_PESOS), CAU$S (CAJA_AHORRO_DOLAR_US)
  "tipoMoneda": "string" // ARS o USD
}

## GET /api/cuenta/{idCuenta} 
Para obtener la cuenta y su titular solo se debe pasar el dni del mismo

## GET /api/cuentas/
Se obtiene la lista de todos las cuentas

## POST /api/movimientos/depositos
Para realizar un deposito se deben pasar los siguientes parametros
{
  "monto": 0,
  "tipoMoneda": "string",
  "numeroCuenta": 0
}

## GET /api/movimientos/retiros
Para realizar un retiro se deben pasar los siguientes parametros
{
  "monto": 0,
  "tipoMoneda": "string",
  "numeroCuenta": 0
}

## GET /api/movimientos/transferencia
Por ultimo para realizar una transferencia se deben pasar los siguientes parametros
{
  "monto": 0,
  "cuentaOrigen": 0,
  "cuentaDestino": 0,
  "tipoMoneda": "string"
}
