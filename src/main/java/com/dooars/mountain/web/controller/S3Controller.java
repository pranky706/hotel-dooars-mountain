/**
 * 
 */
package com.dooars.mountain.web.controller;

import com.dooars.mountain.constants.AllEndPoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.service.s3.AWSS3Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Prantik Guha
 * 20-May-2021 
 * S3Controller.java
 */

@CrossOrigin
@RestController
@RequestMapping()
public class S3Controller {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(S3Controller.class);
	
	private final AWSS3Service service;
	
	@Autowired
	public S3Controller(AWSS3Service service) {
		this.service = service;
	}
	

	@PostMapping(AllEndPoints.UPLOAD_FILE)
	public <T> ResponseEntity<T> uploadFile(@RequestPart(value= "file") final MultipartFile multipartFile) {
		LOGGER.trace("Entering into uploadFile method in S3Controller with{}", multipartFile.getOriginalFilename());
		try {
			String name = service.uploadFile(multipartFile);
			Map<String, String> map = new HashMap<>();
			map.put("imageName", name);
	        return new ResponseEntity<T>((T) map, HttpStatus.OK);
		} catch (BaseException ex) {
			LOGGER.error("Error= {} while uploading file.", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}

}
