package com.sandeep.backendtask.exception;

public class UserNotFoundException extends BaseException {
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String errorMsg) {
		super(errorMsg);
	}
}
