package edu.bu.logfunctions.copylambdalogstos3.repositories;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Repository;

import edu.bu.logfunctions.copylambdalogstos3.models.BuLogs;

import java.util.List;


@Repository
public class LdapRepository {
	
	protected static final Log logger = LogFactory.getLog(LdapRepository.class);
	
	
	@Value("${LDAP_BASE_DN}")
	String baseDn;
	

	@Autowired
	private LdapTemplate ldapTemplate;
	
	
    
	public List<BuLogs> find(String searchTimestamp) {
		
		String filter = "(&(cn>=" + searchTimestamp +")(!(cn=" + searchTimestamp + ")))";
		
		logger.debug("Filter: " + filter);
		
		LdapQuery query = LdapQueryBuilder.query().base(baseDn).filter(filter);
		
		List<BuLogs> buLogs = ldapTemplate.find(query, BuLogs.class);
		
		return buLogs;
		
	}
	
	public void delete(BuLogs logEntry) {
		
		ldapTemplate.delete(logEntry);
	}

    
}