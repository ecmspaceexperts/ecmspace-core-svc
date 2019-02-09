package com.ecmspace.core.rest;

import java.util.Date;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ecmspace.core.exception.DocumentNotFoundException;
import com.ecmspace.core.exception.ErrorDetails;

@ControllerAdvice
@RestController
@ComponentScan("com.ecmspace")
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	
	@ExceptionHandler(DocumentNotFoundException.class)
	public final ResponseEntity<ErrorDetails> handleDocumentNotFoundException(DocumentNotFoundException ex, WebRequest request) {
	    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
	    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorDetails> handleGenericException(Exception ex, WebRequest request) {
	    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
	    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

}
