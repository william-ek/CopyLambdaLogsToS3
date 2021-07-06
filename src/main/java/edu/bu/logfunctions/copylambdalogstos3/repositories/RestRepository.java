package edu.bu.logfunctions.copylambdalogstos3.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;


@Repository
public class RestRepository {
	
	private final Logger logger = LoggerFactory.getLogger(RestRepository.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${REST_VDS_URL}")
	String vdsUrl;
	
	public String getIsLeaderProperty() {
		
		String response = restTemplate.getForObject(vdsUrl, String.class);
		
		logger.debug("Response from VDS: " + response);
		
		return response;
		
	}
	

}
