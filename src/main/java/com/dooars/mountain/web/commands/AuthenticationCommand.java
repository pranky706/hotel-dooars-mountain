/**
 * 
 */
package com.dooars.mountain.web.commands;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha
 * 19-May-2021 
 * AuthenticationCommand.java
 */
public class AuthenticationCommand implements Serializable{


	private static final long serialVersionUID = 1L;
	private long mobileNumber;
	
	public long getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("mobileNumber", mobileNumber)
				.toString();
	}
	
	

}
