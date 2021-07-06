package edu.bu.logfunctions.copylambdalogstos3.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.bu.logfunctions.copylambdalogstos3.repositories.RestRepository;


@Service
public class RestService {
	
	private final Logger logger = LoggerFactory.getLogger(RestService.class);
	
	@Autowired
	RestRepository repository;
	
	public boolean isVDSLeader() {
		String isLeaderProperty = repository.getIsLeaderProperty();
		
		logger.debug("Returened from VDS: " + isLeaderProperty);
		
		String isLeader = isLeaderProperty.replace("\"", "");
		
		logger.debug("Cleaned: " + isLeader);
		
		return isLeader.contains("isVdsLeader:true");
	}

}
