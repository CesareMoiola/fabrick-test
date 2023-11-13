package com.moiola.fabricktest.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URISyntaxException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class, URISyntaxException.class})
    public ResponseEntity<Object> exception(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Object> exception(RestClientResponseException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(e.getMessage().replaceAll("<EOL>",""), e.getStatusCode());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.error(errorMessage);
        return new ResponseEntity<>(errorMessage, status);
    }
}
