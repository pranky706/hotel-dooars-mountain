/**
 * 
 */
package com.dooars.mountain.service.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;

/**
 * @author Prantik Guha
 * 20-May-2021 
 * AWSS3ServiceImpl.java
 */

@Service
public class AWSS3ServiceImpl implements AWSS3Service {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);
	
	private AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String bucketName;
	 
    @Autowired
    AWSS3ServiceImpl(AmazonS3 amazonS3) {
    	this.amazonS3 = amazonS3;
    }
    

	@Override
	@Async
	public String uploadFile(MultipartFile multipartFile) throws BaseException{
		LOGGER.info("File upload in progress.");
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            String name = uploadFileToS3Bucket(bucketName, file);
            LOGGER.info("File upload is completed.");
            file.delete();
            return name;
        } catch (final AmazonServiceException ex) {
            LOGGER.trace("File upload is failed.");
            LOGGER.error("Error= {} while uploading file.", ex.getMessage());
            throw new BaseException(ex.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
        } catch (IOException ex) {
        	LOGGER.trace("File upload is failed.");
            LOGGER.error("Error= {} while uploading file.", ex.getMessage());
            throw new BaseException(ex.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
		}
    }

    @Override
    public void uploadFile(File file) throws BaseException {
        LOGGER.info("File upload in progress.");
        try {
            uploadFileToS3BucketSameName(bucketName, file);
            LOGGER.info("File upload is completed.");
            file.delete();
        } catch (final AmazonServiceException ex) {
            LOGGER.trace("File upload is failed.");
            LOGGER.error("Error= {} while uploading file.", ex.getMessage());
            throw new BaseException(ex.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
        }
    }

    @Override
    public void deleteFile(String fileName) throws BaseException {
        LOGGER.info("File deletion in progress.");
        try {
            LOGGER.info("Deleting file with name= " + fileName);
            final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, fileName);
            amazonS3.deleteObject(deleteObjectRequest);
            LOGGER.info("File deleted successfully.");
        } catch (final AmazonServiceException ex) {
            LOGGER.trace("File upload is failed.");
            LOGGER.error("Error= {} while uploading file.", ex.getMessage());
            throw new BaseException(ex.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
        }
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) throws IOException{
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
            throw ex;
        }
        return file;
    }
 
    private String uploadFileToS3Bucket(final String bucketName, final File file) {
    	final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
        LOGGER.info("Uploading file with name= " + uniqueFileName);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file).withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(putObjectRequest);
        return uniqueFileName;
    }

    private void uploadFileToS3BucketSameName(final String bucketName, final File file) {
        final String uniqueFileName = "" + file.getName();
        LOGGER.info("Uploading file with name= " + uniqueFileName);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file).withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(putObjectRequest);
    }

}
