package com.sandeep.backendtask.exception;

public class InvalidMessageException extends BaseException {
	private static final long serialVersionUID = 1L;

	public InvalidMessageException(String errorMsg) {
		super(errorMsg);
	}
}
