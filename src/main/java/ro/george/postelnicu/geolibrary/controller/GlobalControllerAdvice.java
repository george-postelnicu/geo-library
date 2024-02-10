package ro.george.postelnicu.geolibrary.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ro.george.postelnicu.geolibrary.dto.ErrorDto;
import ro.george.postelnicu.geolibrary.exception.EntityAlreadyExistException;
import ro.george.postelnicu.geolibrary.exception.EntityNotFoundException;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);
    public static final String BAD_REQUEST_ERROR_TYPE = "Bad Request";
    public static final String APPLICATION_ERROR_TYPE = "Application Error";

    @ResponseBody
    @ExceptionHandler(EntityAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> handleAlreadyExists(EntityAlreadyExistException ex, WebRequest request) {
        log.error("handleAlreadyExists: ", ex);
        return handleExceptionInternal(ex, new ErrorDto(
                        UUID.randomUUID().toString(),
                        BAD_REQUEST_ERROR_TYPE,
                        ex.getMessage(),
                        Collections.emptySet(),
                        HttpStatus.resolve(HttpStatus.BAD_REQUEST.value())),
                getProblemJsonHeader(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<Object> handleNotFound(EntityNotFoundException ex, WebRequest request) {
        log.error("handleNotFound: ", ex);
        return handleExceptionInternal(ex, new ErrorDto(
                        UUID.randomUUID().toString(),
                        BAD_REQUEST_ERROR_TYPE,
                        ex.getMessage(),
                        Collections.emptySet(),
                        HttpStatus.resolve(HttpStatus.NOT_FOUND.value())),
                getProblemJsonHeader(),
                HttpStatus.NOT_FOUND,
                request);
    }

    @ResponseBody
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleUncaughtException(Exception ex, WebRequest request) {
        log.error("handleUncaughtException: ", ex);
        HttpStatus status = INTERNAL_SERVER_ERROR;

        return handleExceptionInternal(
                ex,
                new ErrorDto(
                        UUID.randomUUID().toString(),
                        APPLICATION_ERROR_TYPE,
                        ex.getMessage(),
                        Collections.emptySet(),
                        HttpStatus.resolve(status.value())),
                getProblemJsonHeader(),
                status,
                request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NotNull Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        HttpHeaders writeableHeaders = HttpHeaders.writableHttpHeaders(headers);
        writeableHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return super.handleExceptionInternal(ex, body, writeableHeaders, status, request);
    }

    private HttpHeaders getProblemJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return headers;
    }
}
