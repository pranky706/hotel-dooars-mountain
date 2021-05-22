/**
 * 
 */
package com.dooars.mountain.web.controller;

/**
 * @author Prantik Guha
 * 19-May-2021 
 * ServiceExecutor.java
 */
public interface ServiceExecutor<T> {
	T execute() throws Exception;
}
