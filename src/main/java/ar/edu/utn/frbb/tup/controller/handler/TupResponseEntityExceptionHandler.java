package ar.edu.utn.frbb.tup.controller.handler;

import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.model.exception.clientes.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.clientes.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.clientes.MenorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.clientes.TipoPersonaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.cuentas.*;
import ar.edu.utn.frbb.tup.model.exception.monedas.MonedasIncompatiblesException;
import ar.edu.utn.frbb.tup.model.exception.monedas.TipoMonedaNoCoincidenException;
import ar.edu.utn.frbb.tup.model.exception.monedas.TipoMonedaNoSoportadaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class TupResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            ClienteAlreadyExistsException.class,
            CuentaAlreadyExistsException.class,
            TipoCuentaAlreadyExistsException.class,
            IllegalStateException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<Object> handleConflictExceptions(Exception ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        CustomApiError error = new CustomApiError();
        error.setErrorCode(HttpStatus.CONFLICT.value());
        error.setErrorMessage(exceptionMessage != null ? exceptionMessage : "Conflicto en la solicitud.");
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({
            ClienteNotFoundException.class,
            CuentaNotFoundException.class,
            CuentaNotExistException.class
    })
    protected ResponseEntity<Object> handleNotFoundExceptions(Exception ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        CustomApiError error = new CustomApiError();
        error.setErrorCode(HttpStatus.NOT_FOUND.value());
        error.setErrorMessage(exceptionMessage != null ? exceptionMessage : "Recurso no encontrado.");
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({
            TipoCuentaNoSoportadaException.class,
            TipoMonedaNoCoincidenException.class,
            TipoMonedaNoSoportadaException.class,
            TipoPersonaNoSoportadaException.class,
            TransferenciaRechazadaException.class,
            MenorDeEdadException.class,
            MonedasIncompatiblesException.class
    })
    protected ResponseEntity<Object> handleForbiddenExceptions(Exception ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        CustomApiError error = new CustomApiError();
        error.setErrorCode(HttpStatus.FORBIDDEN.value());
        error.setErrorMessage(exceptionMessage != null ? exceptionMessage : "Acceso prohibido.");
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({
            FondosInsuficientesException.class,
            DatosIncorrectosException.class
    })
    protected ResponseEntity<Object> handleBadRequestExceptions(Exception ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        CustomApiError error = new CustomApiError();
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setErrorMessage(exceptionMessage != null ? exceptionMessage : "Solicitud incorrecta.");
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (body == null) {
            CustomApiError error = new CustomApiError();
            error.setErrorCode(status.value());
            error.setErrorMessage(ex.getMessage() != null ? ex.getMessage() : "Error inesperado.");
            body = error;
        }
        return new ResponseEntity<>(body, headers, status);
    }
}
