package edu.bu.logfunctions.copylambdalogstos3.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.bu.logfunctions.copylambdalogstos3.repositories.S3Repository;

@Service
public class S3Service {
	
	@Autowired
	private S3Repository repository;
	
	@Value("${S3_BUCKET}")
	private String bucketName;
	
	@Value("${S3_FOLDER}")
	private String folderName;
	
	public List<String> getLogs() {
		
		return repository.getBucketFiles(bucketName, folderName);
	}
	
	public void putLogEntry(String folder, String type, String content) {
		repository.copyLogEntryToS3(bucketName, folderName, folder, type, content);
	}

}
