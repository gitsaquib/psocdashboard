package com.pearson.dashboard.vo;

import java.io.Serializable;

/**
 * 
 * @author Mohammed Saquib (mohammed.saquib)
 *
 */
public class TestCase implements Serializable {

	private String testCaseId;
	private String lastVerdict;
	private String name;
	private String description;
	private String lastRun;
	private String lastBuild;
	private String priority;
	
	public String getTestCaseId() {
		return testCaseId;
	}
	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}
	public String getLastVerdict() {
		return lastVerdict;
	}
	public void setLastVerdict(String lastVerdict) {
		this.lastVerdict = lastVerdict;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLastRun() {
		return lastRun;
	}
	public void setLastRun(String lastRun) {
		this.lastRun = lastRun;
	}
	public String getLastBuild() {
		return lastBuild;
	}
	public void setLastBuild(String lastBuild) {
		this.lastBuild = lastBuild;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
}
