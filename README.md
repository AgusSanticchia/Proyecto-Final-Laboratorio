# Proyecto Final - Simulación de Banco

Este proyecto simula un sistema bancario y fue desarrollado como trabajo final para la materia **Laboratorio III**. El sistema permite la gestión de clientes, cuentas y movimientos bancarios como depósitos, extracciones y transferencias. Ademas, implementa validaciones, control de errores y pruebas unitarias.

## Tecnologías Utilizadas

- Java 17
- Spring Boot
- Maven
- Swagger
- JUnit 5 + Mockito
- Postman (para pruebas manuales)

## Requisitos

- Java 17+
- Maven 3.8+

## Cómo Ejecutar la Aplicación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/AgusSanticchia/Proyecto-Final-Laboratorio.git
   ```

2. Entra en el directorio del proyecto:
   ```bash
   cd Proyecto-Final-Laboratorio
   ```

3. Ejecute la aplicación:
   ```bash
   mvn spring-boot:run
   ```

Una vez que el proyecto esta en ejecución, accede a Swagger para probar los endpoints desde el navegador:

http://localhost:8080/swagger-ui/index.html

## Endpoints Principales

### Cliente

- POST /api/cliente  Crear un cliente
- GET /api/cliente/  Listar todos los clientes
- GET /api/cliente/{dni}  Buscar cliente por DNI

### Cuenta

- POST /api/cuenta  Crear una cuenta para un cliente
<<<<<<< HEAD
- GET /api/cuenta/{id}  Buscar cuenta por CBU
- GET /api/cuenta/cliente/{dni}  Listar cuentas de un cliente
=======
- GET /api/cuenta/  Listar cuentas de clientes
- GET /api/cuenta/{idCuenta}  Buscar cuenta por ID
>>>>>>> b69a71454548aa6c5a6fcd4ed88007f09c19d9be

### Movimientos

- POST /api/movimientos/depositos  Depositar dinero
- POST /api/movimientos/retiros  Retirar dinero
- POST /api/movimientos/transferencia  Transferir entre cuentas
- GET /api/movimientos/{cuentaId} Obtener movimientos de una cuenta

## Pruebas

Para ejecutar las pruebas unitarias:
```bash
mvn test
```
