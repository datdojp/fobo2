package com.forboss.data.api;

public class APIException extends Exception {
	public APIException(Exception rootCause) {
		super();
		this.rootCause = rootCause;
	}
	public APIException(Exception rootCause, String message) {
		super();
		this.rootCause = rootCause;
		this.message = message;
	}
	private Exception rootCause;
	private String message;
	public Exception getRootCause() {
		return rootCause;
	}
	public void setRootCause(Exception rootCause) {
		this.rootCause = rootCause;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
