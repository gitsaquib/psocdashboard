package com.pearson.dashboard.vo;

import java.io.Serializable;
import java.util.List;

public class RegressionData implements Serializable {
	
	private List<String> winTestSetsIds;
	private List<String> iosTestSetsIds;
	private String cutoffDate;
	
	public List<String> getWinTestSetsIds() {
		return winTestSetsIds;
	}
	public void setWinTestSetsIds(List<String> winTestSetsIds) {
		this.winTestSetsIds = winTestSetsIds;
	}
	public List<String> getIosTestSetsIds() {
		return iosTestSetsIds;
	}
	public void setIosTestSetsIds(List<String> iosTestSetsIds) {
		this.iosTestSetsIds = iosTestSetsIds;
	}
	public String getCutoffDate() {
		return cutoffDate;
	}
	public void setCutoffDate(String cutoffDate) {
		this.cutoffDate = cutoffDate;
	}
	
	
	
}
