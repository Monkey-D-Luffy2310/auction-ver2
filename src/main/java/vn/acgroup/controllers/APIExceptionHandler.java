package vn.acgroup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import vn.acgroup.exception.CustomException;

@RestControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(value = {CustomException.class})
  public ResponseEntity<?> handleException(CustomException e) {
    return new ResponseEntity<Object>(e.getMessage(), HttpStatus.valueOf(e.getCode()));
  }
}
