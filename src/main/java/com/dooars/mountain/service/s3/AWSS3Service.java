/**
 * 
 */
package com.dooars.mountain.service.s3;

import org.springframework.web.multipart.MultipartFile;

import com.dooars.mountain.model.common.BaseException;

/**
 * @author Prantik Guha
 * 20-May-2021 
 * AWSS3Service.java
 */
public interface AWSS3Service {

	String uploadFile(final MultipartFile multipartFile) throws BaseException;
}
