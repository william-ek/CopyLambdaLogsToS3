package edu.bu.logfunctions.copylambdalogstos3.repositories;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;


@Repository
public class S3Repository {
	
	private final Logger logger = LoggerFactory.getLogger(S3Repository.class);
	
	@Autowired
	private AmazonS3 s3;

	public List<String> getBucketFiles(String bucketName, String prefix) {
		
		logger.debug("getBucketFiles: " + bucketName + " " + prefix);
		
		List<String> bucketContents = new ArrayList<>();
		
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            bucketContents.add(objectSummary.getKey());
        }
        
        return bucketContents;
		
	}
	
	public PutObjectResult copyLogEntryToS3(String bucketName, String prefix, String folder, String type, String content) {
		
		String key = prefix + "/" + folder + "/" + type + ".csv";
		
		return s3.putObject(bucketName, key, content);
		
	}
	
	

}

