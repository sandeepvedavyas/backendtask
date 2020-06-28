package com.sandeep.backendtask.exception;

public class DuplicateUserNameException extends BaseException {
	private static final long serialVersionUID = 1L;

	public DuplicateUserNameException(String errorMsg) {
		super(errorMsg);
	}
}
