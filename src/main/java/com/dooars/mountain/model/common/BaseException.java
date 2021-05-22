package com.dooars.mountain.model.common;

import com.google.common.base.MoreObjects;

public class BaseException extends Exception{

	private static final long serialVersionUID = -56856565676767L;
	private String message;
	private String layer;
	private String stacktrace;
	
	public BaseException(String message,String layer,String stacktrace) {
		this.message = message;
		this.layer = layer;
		this.stacktrace = stacktrace;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getLayer() {
		return layer;
	}
	public void setLayer(String layer) {
		this.layer = layer;
	}
	public String getStacktrace() {
		return stacktrace;
	}
	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("message", message)
				.add("layer", layer)
				.add("stacktrace", stacktrace)
				.toString();
	}
}
