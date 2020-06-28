package com.sandeep.backendtask.exception;

public class MessageException extends BaseException {
	private static final long serialVersionUID = 1L;
	private String errorMsg;

	public MessageException(String errorMsg) {
		super(errorMsg);
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
