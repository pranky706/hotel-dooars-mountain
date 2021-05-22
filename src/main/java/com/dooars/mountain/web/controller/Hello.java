/**
 * 
 */
package com.dooars.mountain.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Prantik Guha
 * 20-May-2021 
 * Hello.java
 */

@RestController
public class Hello {
	
	@PostMapping("/hello")
	public ResponseEntity<String> uploadFile() {
		System.out.println("Entering into uploadFile method in S3Controller with{}");
		return new ResponseEntity<>("OK", HttpStatus.OK);		
	}

}
