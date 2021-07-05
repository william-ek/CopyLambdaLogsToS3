package edu.bu.logfunctions.copylambdalogstos3.models;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entry(objectClasses = {"bulogs", "top"})
public class BuLogs implements Comparable<BuLogs> {
	
	@JsonIgnore
	public static final String AUDIT_TYPE = "audit";
	@JsonIgnore
	public static final String MATCH_TYPE = "match";
	
	@Id
	@JsonIgnore
	private Name dn;
	
	@Attribute(name= "logtimestamp")
	private String logtimestamp;
	
	@Attribute(name = "logcontent")
	private String logcontent;
	
	@Attribute(name = "logtype")
	private String logtype;

	@Override
	public int compareTo(BuLogs o) {

		return this.logtimestamp.compareTo(o.logtimestamp);
	}

	@Override
	public String toString() {
		return "BuLogs [logtimestamp=" + logtimestamp + ", logcontent=" + logcontent + ", logtype=" + logtype + "]";
	}

	public Name getDn() {
		return dn;
	}

	public void setDn(Name dn) {
		this.dn = dn;
	}

	public String getLogtimestamp() {
		return logtimestamp;
	}

	public void setLogtimestamp(String logtimestamp) {
		this.logtimestamp = logtimestamp;
	}

	public String getLogcontent() {
		return logcontent;
	}

	public void setLogcontent(String logcontent) {
		this.logcontent = logcontent;
	}

	public String getLogtype() {
		return logtype;
	}

	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}
	

}
