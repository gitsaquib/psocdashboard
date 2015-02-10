package com.pearson.dashboard.vo;

import java.io.Serializable;

/**
 * 
 * @author Mohammed Saquib (mohammed.saquib)
 *
 */
public class Project implements Serializable, Comparable<Project> {

	private int tabIndex;
	private String projectId;
	private String projectKey;
	private String release;
	private String cutoffDate;
	private String parentTab;
	
	public int getTabIndex() {
		return tabIndex;
	}
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectKey() {
		return projectKey;
	}
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}	
	public String getCutoffDate() {
		return cutoffDate;
	}
	public void setCutoffDate(String cutoffDate) {
		this.cutoffDate = cutoffDate;
	}
	public String getParentTab() {
		return parentTab;
	}
	public void setParentTab(String parentTab) {
		this.parentTab = parentTab;
	}
	
	@Override
	public int compareTo(Project project) {
		return this.getProjectKey().compareTo(project.getProjectKey());
	}
}
