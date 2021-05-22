package com.dooars.mountain.model.common;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.MoreObjects;

public class ApiResult<T> implements Serializable{

	public static class Builder<T> {
		private List<FieldError> filedErrors;
		private T payload;
		private List<Error> errors;
		
		public Builder withPayload(T val) {
			this.payload = val;
			return this;
		}
		
		public Builder withFiledErrors(List<FieldError> val) {
			this.filedErrors = val;
			return this;
		}
		
		public Builder withErrors(List<Error> val) {
			this.errors = val;
			return this;
		}
		
		public ApiResult<T> build() {
			return new ApiResult<T>(this);
		}
	}
	private static final long serialVersionUID = -1812400454603998286L;
	private List<FieldError> filedErrors;
	private T payload;
	private List<Error> errors;
	
	private ApiResult(Builder<T> builder) {
		this.errors = builder.errors;
		this.payload = builder.payload;
		this.filedErrors = builder.filedErrors;
	}
	
	public static Builder newInstance() {
		return new Builder();
	}

	public List<FieldError> getFiledErrors() {
		return filedErrors;
	}

	public T getPayload() {
		return payload;
	}

	public List<Error> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("payload", payload)
				.add("filedErrors", filedErrors)
				.add("errors", errors)
				.toString();
	}
	
	

}
