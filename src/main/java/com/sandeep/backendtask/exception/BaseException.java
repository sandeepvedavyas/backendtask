package com.sandeep.backendtask.exception;

public class BaseException extends Exception {
	private static final long serialVersionUID = 1L;

	public BaseException(String errorMsg) {
		super(errorMsg);
	}
}
