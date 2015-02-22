/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.pearson.dashboard.vo.Defect;
import com.pearson.dashboard.vo.Priority;
import com.pearson.dashboard.vo.Tab;
import com.pearson.dashboard.vo.TestCase;

/**
 *
 * @author Mohammed Saquib (mohammed.saquib)
 */
public class DashboardForm extends ActionForm {
	
	private String loginUser;
	
    private List<Defect> openDefects;
    private int openDefectCount;
    private int openP1AndP2Count;
    private List<Priority> openPriorities;
    
    private List<Defect> openYesterdayDefects;
    private int openYesterdayDefectCount;
    private int openYesterdayP1AndP2Count;
    private List<Priority> openYesterdayPriorities;
    
    private List<Defect> closedYesterdayDefects;
    private int closedYesterdayDefectCount;
    private int closedYesterdayP1AndP2Count;
    private List<Priority> closedYesterdayPriorities;
    
    private List<Defect> submittedDefects;
    private int submittedDefectCount;
    private int submittedP1AndP2Count;
    private List<Priority> submittedPriorities;
    
    private List<Defect> closedDefects;
    private int closedDefectCount;
    private int closedP1AndP2Count;
    private List<Priority> closedPriorities;
    
    private List<Defect> fixedDefects;
    private int fixedDefectCount;
    private int fixedP1AndP2Count;
    private List<Priority> fixedPriorities;
    
    private List<Tab> tabs;
    
    private String projectName;
	private String projectId;
	private String projectRelease;
	
    private String latestBuild;
    private String gitActivity;
    
    private String selectedRelease;
	private String cutoffDate;
	private String tabName;
	private String tabIndex;
	
	private List<TestCase> testCases;
	private int testCasesCount;
    private List<Priority> testCasesPriorities;
    
    private List<Tab> subTabs;
    private String subProject;
    
    private String expandType;
    
    private String sort;
    
    private String submittedMsg;
    private String openMsg;
    private String fixedMsg;
    private String closedMsg;
    private String closedYMsg;
    private String openYMsg;
    
    public String getSelectedRelease() {
		return selectedRelease;
	}
	public void setSelectedRelease(String selectedRelease) {
		this.selectedRelease = selectedRelease;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectRelease() {
		return projectRelease;
	}
	public void setProjectRelease(String projectRelease) {
		this.projectRelease = projectRelease;
	}
	public String getLatestBuild() {
		return latestBuild;
	}
	public void setLatestBuild(String latestBuild) {
		this.latestBuild = latestBuild;
	}
	public String getGitActivity() {
		return gitActivity;
	}
	public void setGitActivity(String gitActivity) {
		this.gitActivity = gitActivity;
	}
	public List<Defect> getOpenDefects() {
		return openDefects;
	}
	public void setOpenDefects(List<Defect> openDefects) {
		this.openDefects = openDefects;
	}
	public int getOpenDefectCount() {
		return openDefectCount;
	}
	public void setOpenDefectCount(int openDefectCount) {
		this.openDefectCount = openDefectCount;
	}
	public int getOpenP1AndP2Count() {
		return openP1AndP2Count;
	}
	public void setOpenP1AndP2Count(int openP1AndP2Count) {
		this.openP1AndP2Count = openP1AndP2Count;
	}
	public List<Priority> getOpenPriorities() {
		return openPriorities;
	}
	public void setOpenPriorities(List<Priority> openPriorities) {
		this.openPriorities = openPriorities;
	}
	public List<Defect> getSubmittedDefects() {
		return submittedDefects;
	}
	public void setSubmittedDefects(List<Defect> submittedDefects) {
		this.submittedDefects = submittedDefects;
	}
	public int getSubmittedDefectCount() {
		return submittedDefectCount;
	}
	public void setSubmittedDefectCount(int submittedDefectCount) {
		this.submittedDefectCount = submittedDefectCount;
	}
	public int getSubmittedP1AndP2Count() {
		return submittedP1AndP2Count;
	}
	public void setSubmittedP1AndP2Count(int submittedP1AndP2Count) {
		this.submittedP1AndP2Count = submittedP1AndP2Count;
	}
	public List<Priority> getSubmittedPriorities() {
		return submittedPriorities;
	}
	public void setSubmittedPriorities(List<Priority> submittedPriorities) {
		this.submittedPriorities = submittedPriorities;
	}
	public List<Defect> getClosedDefects() {
		return closedDefects;
	}
	public void setClosedDefects(List<Defect> closedDefects) {
		this.closedDefects = closedDefects;
	}
	public int getClosedDefectCount() {
		return closedDefectCount;
	}
	public void setClosedDefectCount(int closedDefectCount) {
		this.closedDefectCount = closedDefectCount;
	}
	public int getClosedP1AndP2Count() {
		return closedP1AndP2Count;
	}
	public void setClosedP1AndP2Count(int closedP1AndP2Count) {
		this.closedP1AndP2Count = closedP1AndP2Count;
	}
	public List<Priority> getClosedPriorities() {
		return closedPriorities;
	}
	public void setClosedPriorities(List<Priority> closedPriorities) {
		this.closedPriorities = closedPriorities;
	}
	public List<Defect> getFixedDefects() {
		return fixedDefects;
	}
	public void setFixedDefects(List<Defect> fixedDefects) {
		this.fixedDefects = fixedDefects;
	}
	public int getFixedDefectCount() {
		return fixedDefectCount;
	}
	public void setFixedDefectCount(int fixedDefectCount) {
		this.fixedDefectCount = fixedDefectCount;
	}
	public int getFixedP1AndP2Count() {
		return fixedP1AndP2Count;
	}
	public void setFixedP1AndP2Count(int fixedP1AndP2Count) {
		this.fixedP1AndP2Count = fixedP1AndP2Count;
	}
	public List<Priority> getFixedPriorities() {
		return fixedPriorities;
	}
	public void setFixedPriorities(List<Priority> fixedPriorities) {
		this.fixedPriorities = fixedPriorities;
	}
	public List<Defect> getOpenYesterdayDefects() {
		return openYesterdayDefects;
	}
	public void setOpenYesterdayDefects(List<Defect> openYesterdayDefects) {
		this.openYesterdayDefects = openYesterdayDefects;
	}
	public int getOpenYesterdayDefectCount() {
		return openYesterdayDefectCount;
	}
	public void setOpenYesterdayDefectCount(int openYesterdayDefectCount) {
		this.openYesterdayDefectCount = openYesterdayDefectCount;
	}
	public int getOpenYesterdayP1AndP2Count() {
		return openYesterdayP1AndP2Count;
	}
	public void setOpenYesterdayP1AndP2Count(int openYesterdayP1AndP2Count) {
		this.openYesterdayP1AndP2Count = openYesterdayP1AndP2Count;
	}
	public List<Priority> getOpenYesterdayPriorities() {
		return openYesterdayPriorities;
	}
	public void setOpenYesterdayPriorities(List<Priority> openYesterdayPriorities) {
		this.openYesterdayPriorities = openYesterdayPriorities;
	}
	public List<Defect> getClosedYesterdayDefects() {
		return closedYesterdayDefects;
	}
	public void setClosedYesterdayDefects(List<Defect> closedYesterdayDefects) {
		this.closedYesterdayDefects = closedYesterdayDefects;
	}
	public int getClosedYesterdayDefectCount() {
		return closedYesterdayDefectCount;
	}
	public void setClosedYesterdayDefectCount(int closedYesterdayDefectCount) {
		this.closedYesterdayDefectCount = closedYesterdayDefectCount;
	}
	public int getClosedYesterdayP1AndP2Count() {
		return closedYesterdayP1AndP2Count;
	}
	public void setClosedYesterdayP1AndP2Count(int closedYesterdayP1AndP2Count) {
		this.closedYesterdayP1AndP2Count = closedYesterdayP1AndP2Count;
	}
	public List<Priority> getClosedYesterdayPriorities() {
		return closedYesterdayPriorities;
	}
	public void setClosedYesterdayPriorities(
			List<Priority> closedYesterdayPriorities) {
		this.closedYesterdayPriorities = closedYesterdayPriorities;
	}
	public String getLoginUser() {
		return loginUser;
	}
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	public String getCutoffDate() {
		return cutoffDate;
	}
	public void setCutoffDate(String cutoffDate) {
		this.cutoffDate = cutoffDate;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public List<TestCase> getTestCases() {
		return testCases;
	}
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}
	public int getTestCasesCount() {
		return testCasesCount;
	}
	public void setTestCasesCount(int testCasesCount) {
		this.testCasesCount = testCasesCount;
	}
	public List<Priority> getTestCasesPriorities() {
		return testCasesPriorities;
	}
	public void setTestCasesPriorities(List<Priority> testCasesPriorities) {
		this.testCasesPriorities = testCasesPriorities;
	}
	public String getExpandType() {
		return expandType;
	}
	public void setExpandType(String expandType) {
		this.expandType = expandType;
	}
	public String getTabIndex() {
		return tabIndex;
	}
	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}
	public String getSubProject() {
		return subProject;
	}
	public void setSubProject(String subProject) {
		this.subProject = subProject;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getSubmittedMsg() {
		return submittedMsg;
	}
	public void setSubmittedMsg(String submittedMsg) {
		this.submittedMsg = submittedMsg;
	}
	public String getOpenMsg() {
		return openMsg;
	}
	public void setOpenMsg(String openMsg) {
		this.openMsg = openMsg;
	}
	public String getFixedMsg() {
		return fixedMsg;
	}
	public void setFixedMsg(String fixedMsg) {
		this.fixedMsg = fixedMsg;
	}
	public String getClosedMsg() {
		return closedMsg;
	}
	public void setClosedMsg(String closedMsg) {
		this.closedMsg = closedMsg;
	}
	public String getClosedYMsg() {
		return closedYMsg;
	}
	public void setClosedYMsg(String closedYMsg) {
		this.closedYMsg = closedYMsg;
	}
	public String getOpenYMsg() {
		return openYMsg;
	}
	public void setOpenYMsg(String openYMsg) {
		this.openYMsg = openYMsg;
	}
	public List<Tab> getTabs() {
		return tabs;
	}
	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}
	public List<Tab> getSubTabs() {
		return subTabs;
	}
	public void setSubTabs(List<Tab> subTabs) {
		this.subTabs = subTabs;
	}
}
