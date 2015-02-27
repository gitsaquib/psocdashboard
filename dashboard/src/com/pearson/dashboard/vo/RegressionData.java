package com.pearson.dashboard.vo;

import java.io.Serializable;
import java.util.List;

public class RegressionData implements Serializable {
	
	private List<String> testSetsIds;
	private String cutoffDate;
	
	public List<String> getTestSetsIds() {
		return testSetsIds;
	}
	public void setTestSetsIds(List<String> testSetsIds) {
		this.testSetsIds = testSetsIds;
	}
	public String getCutoffDate() {
		return cutoffDate;
	}
	public void setCutoffDate(String cutoffDate) {
		this.cutoffDate = cutoffDate;
	}
}
