package com.pearson.dashboard.vo;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Mohammed Saquib (mohammed.saquib)
 */
public class Tab implements Serializable, Comparable<Tab> {
	
	private int tabIndex;
	private String tabUniqueId;
	private String tabDisplayName;
	private String tabType;
	private boolean regressionData;
	private String cutoffDate;
	private String release;
	private List<Tab> subTabs;
	private String information;

	public int getTabIndex() {
		return tabIndex;
	}
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}
	public String getTabUniqueId() {
		return tabUniqueId;
	}
	public void setTabUniqueId(String tabUniqueId) {
		this.tabUniqueId = tabUniqueId;
	}
	public String getTabDisplayName() {
		return tabDisplayName;
	}
	public void setTabDisplayName(String tabDisplayName) {
		this.tabDisplayName = tabDisplayName;
	}
	public String getTabType() {
		return tabType;
	}
	public void setTabType(String tabType) {
		this.tabType = tabType;
	}
	public boolean isRegressionData() {
		return regressionData;
	}
	public void setRegressionData(boolean regressionData) {
		this.regressionData = regressionData;
	}
	public String getCutoffDate() {
		return cutoffDate;
	}
	public void setCutoffDate(String cutoffDate) {
		this.cutoffDate = cutoffDate;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public List<Tab> getSubTabs() {
		return subTabs;
	}
	public void setSubTabs(List<Tab> subTabs) {
		this.subTabs = subTabs;
	}
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	@Override
	public int compareTo(Tab tab) {
		Integer tabIndex = Integer.valueOf(this.tabIndex);
		return tabIndex.compareTo(tab.getTabIndex());
	}
}