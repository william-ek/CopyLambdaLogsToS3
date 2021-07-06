package edu.bu.logfunctions.copylambdalogstos3.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;




@ComponentScan(basePackages = "edu.bu.logfunctions.copylambdalogstos3")
@SpringBootApplication
public class CopyLambdaLogsToS3Application {
	
	private static final Logger logger = LoggerFactory.getLogger(CopyLambdaLogsToS3Application.class);
	
	@Value("${LDAP_BIND_URL}")
	String ldapUrl;
	
	@Value("${LDAP_BIND_DN}")
	String ldapBindDn;
	
	@Value("${LDAP_BIND_PASSWORD}")
	String ldapBindPassword;
	
	@Value("${S3_REGION}")
	String s3Region;
	
	@Value("${S3_PROFILE}")
	String s3Profile;

	public static void main(String[] args) {
		
		ConfigurableApplicationContext applicationContext = SpringApplication.run(CopyLambdaLogsToS3Application.class, args);
		
		CopyBuLogsProcess process = applicationContext.getBean(CopyBuLogsProcess.class);
		
		
		try {
			process.doProcess();
		} catch (Exception e) {
			logger.error("Task ended with an error.");
			e.printStackTrace();
		}
		
		logger.info("Task Ending");
		
		applicationContext.close();
		
	}
	
	    @Bean
		public RestTemplate restTemplate(RestTemplateBuilder builder) {
			return builder.build();
		}
	
	   @Bean
	    public LdapContextSource contextSource() throws Exception {
	    	
	        LdapContextSource contextSource= new LdapContextSource();
	        contextSource.setUrl(ldapUrl);
	        contextSource.setUserDn(ldapBindDn);
	        contextSource.setPassword(ldapBindPassword);
	        return contextSource;
	    }

	    @Bean
	    public LdapTemplate ldapTemplate() throws Exception {
	        return new LdapTemplate(contextSource());        
	    }
	    

	    @Bean
	    public AmazonS3 amazonS3() {
	    	
	    	logger.debug("amazonS3" + " Profile: " + s3Profile + " Region: " + s3Region);
	    	
	        AWSCredentials credentials = null;
	        try {
	            credentials = new ProfileCredentialsProvider(s3Profile).getCredentials();
	        } catch (Exception e) {
	            System.out.println("Cannot load the credentials from the credential profiles file. ");
	            e.printStackTrace();
	        }
	        
	        return AmazonS3ClientBuilder.standard()
	                .withCredentials(new AWSStaticCredentialsProvider(credentials))
	                .withRegion(s3Region)
	                .build();
	        
	    }
	

}
