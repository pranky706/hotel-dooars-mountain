package com.dooars.mountain.model.common;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

public class Error implements Serializable {

	public static class Builder {
		private String layer;
		private String message;
		
		private Builder withMessage(String val) {
			this.message = val;
			return this;
		}
		
		private Builder withLayer(String val) {
			this.layer = val;
			return this;
		}
		
		private Error build() {
			return new Error(this);
		}
	}
	private static final long serialVersionUID = -8778879886565865L;
	private String layer;
	private String message;
	
	private Error(Builder builder) {
		this.layer = builder.layer;
		this.message = builder.message;
	}
	
	public static Builder newInstance() {
		return new Builder();
	}
	
	public static Error createInstance(String msg, String layer) {
		return Error.newInstance().withLayer(layer).withMessage(msg).build();
	}

	public String getLayer() {
		return layer;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("message", message)
				.add("layer", layer)
				.toString();
	}
	
	

}
