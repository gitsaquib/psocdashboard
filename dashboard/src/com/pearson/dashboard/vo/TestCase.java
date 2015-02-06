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
}
