package com.sandeep.backendtask.helper;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sandeep.backendtask.exception.ApplicationException;
import com.sandeep.backendtask.exception.DuplicateUserNameException;
import com.sandeep.backendtask.exception.InvalidMessageException;
import com.sandeep.backendtask.exception.UserException;


@ControllerAdvice
public class RestExceptionHandler{
	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(UserException.class)
	public ResponseEntity<CustomErrorResponse> exceptionCustomerHandler(Exception ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(MessagingAppConstant.VALIDATION_FAILED);
		errors.getErrors().put("UserException ", ex.getMessage());
		return new ResponseEntity<CustomErrorResponse>(errors, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<CustomErrorResponse> exceptionChatHandler(Exception ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(MessagingAppConstant.VALIDATION_FAILED);
		errors.getErrors().put("ChatException ", ex.getMessage());
		return new ResponseEntity<CustomErrorResponse>(errors, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CustomErrorResponse> exceptionHandler(Exception ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(MessagingAppConstant.VALIDATION_FAILED);
		errors.getErrors().put("Malformed syntax, server can not uderstand the request ", ex.getMessage());
		return new ResponseEntity<CustomErrorResponse>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomErrorResponse> processValidationError(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		CustomErrorResponse errors = processFieldErrors(fieldErrors);
		errors.setMessage(MessagingAppConstant.VALIDATION_FAILED);
	
		return new ResponseEntity<CustomErrorResponse>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateUserNameException.class)
	public ResponseEntity<CustomErrorResponse> duplicateUsernameExceptionHandler(DuplicateUserNameException ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(MessagingAppConstant.VALIDATION_FAILED);
		errors.getErrors().put("Username", ex.getMessage());
		return new ResponseEntity<CustomErrorResponse>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidMessageException.class)
	public ResponseEntity<CustomErrorResponse> invalidMessageExceptionHandler(InvalidMessageException ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(MessagingAppConstant.VALIDATION_FAILED);
		errors.getErrors().put("Message", ex.getMessage());
		return new ResponseEntity<CustomErrorResponse>(errors, HttpStatus.BAD_REQUEST);
	}
		

	private CustomErrorResponse processFieldErrors(List<FieldError> fieldErrors) {
		CustomErrorResponse errors = new CustomErrorResponse();

		for (FieldError fieldError : fieldErrors) {
			String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
			errors.getErrors().put(fieldError.getField(), localizedErrorMessage);
		}

		return errors;
	}

	private String resolveLocalizedErrorMessage(FieldError fieldError) {
		Locale currentLocale = LocaleContextHolder.getLocale();
		String localizedErrorMessage = messageSource.getMessage(fieldError, currentLocale);
		if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
		
			localizedErrorMessage = fieldError.getDefaultMessage();
		}

		return localizedErrorMessage;
	}
	

}
