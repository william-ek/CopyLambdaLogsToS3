package edu.bu.logfunctions.copylambdalogstos3.application;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.bu.logfunctions.copylambdalogstos3.models.BuLogs;
import edu.bu.logfunctions.copylambdalogstos3.services.LdapService;
import edu.bu.logfunctions.copylambdalogstos3.services.S3Service;

@Component
public class CopyBuLogsProcess {
	
	private final Logger logger = LoggerFactory.getLogger(CopyBuLogsProcess.class);
	
	@Autowired
	LdapService ldapService;
	
	@Autowired
	S3Service s3Service;
	
	public void doProcess() {
		logger.debug("doProcess");
		
		/*
		 * Get a list of all the entries for this bucket/folder
		 */
		List<String> s3Logs = s3Service.getLogs();
		
		/*
		 * Sort by key which contains the timestamp as a folder
		 */
		Collections.sort(s3Logs);
		
		/*
		 * Delete after debugging
		 */
		for (String string : s3Logs) {
			logger.debug("Log Entry: " + string);
		}
		
		String searchFilter = "0";
		
		/*
		 * The folder name for the last (latest) entry will be the cn of the last VDS entry loaded to S3.
		 */
		if (s3Logs != null && !s3Logs.isEmpty() && s3Logs.size() > 1) {
			String lastEntry = s3Logs.get(s3Logs.size() -1);
			String entryParts[] = lastEntry.split("/");
			if (entryParts.length > 1) {
				searchFilter = entryParts[1];
				logger.debug("searchFilter: " + searchFilter);
			}
		}
		
		logger.debug("searchFilter - end: " + searchFilter);
		
		/*
		 * Search for logs in VDS that were written after the last entry in S3
		 */
		List<BuLogs> buLogs = ldapService.findLogs(searchFilter);
		
		Collections.sort(buLogs);
		
		/*
		 * Delete this after testing
		 */
		for (BuLogs buLogEntry : buLogs) {
			logger.debug(buLogEntry.toString());
		}
		
		BuLogs lastLog = null;
		
		/*
		 * Find the latest log entry, the timestamp will be the S3 folder name
		 */
		if (buLogs != null && !buLogs.isEmpty() && buLogs.size() > 0) {
			logger.debug("Got One! " + buLogs.size());
			lastLog = buLogs.get(buLogs.size() -1);
			logger.debug("Last Entry: " + lastLog);
			
			/*
			 * Write the logs to S3 then delete from VDS
			 */
			for (BuLogs logEntry : buLogs) {
				s3Service.putLogEntry(lastLog.getLogtimestamp(), logEntry.getLogtype(), logEntry.getLogtimestamp(), logEntry.getLogcontent());
				
				ldapService.deleteLogs(logEntry);
			}

		}
	
		
	}

}
