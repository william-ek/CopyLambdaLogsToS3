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
			 * Write accumulate the BuLog entries into StringBuffer(s) divide by \n
			 */
			StringBuilder auditLogs = new StringBuilder();
			StringBuilder matchLogs = new StringBuilder();
			

			
			for (BuLogs logEntry : buLogs) {
				
				switch (logEntry.getLogtype()) {
				case "audit":
					auditLogs.append(logEntry.getLogcontent() + "\n");
					break;
				case "match":
					matchLogs.append(logEntry.getLogcontent() + "\n");
					break;

				default:
					break;
				}
			}
			
			s3Service.putLogEntry(lastLog.getLogtimestamp(), "audit", auditLogs.toString());
			s3Service.putLogEntry(lastLog.getLogtimestamp(), "match", matchLogs.toString());
			
			for (BuLogs logEntry : buLogs) {
				ldapService.deleteLogs(logEntry);
				
				
		
			}
			

		}
	
		
	}

}
