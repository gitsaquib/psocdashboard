package com.pearson.dashboard.vo;

import java.util.List;

/**
 * 
 * @author Mohammed Saquib (mohammed.saquib)
 *
 */
public class Configuration {
	
	private String rallyURL;
	private String rallyUser;
	private String rallyPassword;
	private List<Tab> tabs;
	private String useRegressionInputFile;
	
	public String getRallyURL() {
		return rallyURL;
	}
	public void setRallyURL(String rallyURL) {
		this.rallyURL = rallyURL;
	}
	public String getRallyUser() {
		return rallyUser;
	}
	public void setRallyUser(String rallyUser) {
		this.rallyUser = rallyUser;
	}
	public String getRallyPassword() {
		return rallyPassword;
	}
	public void setRallyPassword(String rallyPassword) {
		this.rallyPassword = rallyPassword;
	}
	public List<Tab> getTabs() {
		return tabs;
	}
	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}
	public String getUseRegressionInputFile() {
		return useRegressionInputFile;
	}
	public void setUseRegressionInputFile(String useRegressionInputFile) {
		this.useRegressionInputFile = useRegressionInputFile;
	}
}
