/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pearson.dashboard.form.DashboardForm;
import com.pearson.dashboard.util.Util;
import com.pearson.dashboard.vo.Configuration;
import com.pearson.dashboard.vo.Defect;
import com.pearson.dashboard.vo.Priority;
import com.pearson.dashboard.vo.RegressionData;
import com.pearson.dashboard.vo.Tab;

/**
 *
 * @author Mohammed Saquib (mohammed.saquib)
 */
public class DashboardAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	DashboardForm dashboardForm = (DashboardForm) form;
    	
    	Util.setOperatingSystems(dashboardForm);
    	if(null != request.getParameter("os")) {
    		dashboardForm.setOperatingSystem(request.getParameter("os"));
    	} else {
    		dashboardForm.setOperatingSystem("All");
    	}
    	
    	if(null != request.getParameter("sort")) {
    		dashboardForm.setSort(request.getParameter("sort"));
    		String expandType = dashboardForm.getExpandType();
    		if("Submitted".equalsIgnoreCase(expandType)) {
    			for(Defect defect:dashboardForm.getSubmittedDefects()) {
    				defect.setSort(dashboardForm.getSort());
    			}
    			Collections.sort(dashboardForm.getSubmittedDefects());
    		} else if("Open".equalsIgnoreCase(expandType)) {
    			for(Defect defect:dashboardForm.getOpenDefects()) {
    				defect.setSort(dashboardForm.getSort());
    			}
    			Collections.sort(dashboardForm.getOpenDefects());
    		} else if("Fixed".equalsIgnoreCase(expandType)) {
    			for(Defect defect:dashboardForm.getFixedDefects()) {
    				defect.setSort(dashboardForm.getSort());
    			}
    			Collections.sort(dashboardForm.getFixedDefects());
    		} else if("Closed".equalsIgnoreCase(expandType)) {
    			for(Defect defect:dashboardForm.getClosedDefects()) {
    				defect.setSort(dashboardForm.getSort());
    			}
    			Collections.sort(dashboardForm.getClosedDefects());
    		} else if("OpenY".equalsIgnoreCase(expandType)) {
    			for(Defect defect:dashboardForm.getOpenYesterdayDefects()) {
    				defect.setSort(dashboardForm.getSort());
    			}
    			Collections.sort(dashboardForm.getOpenYesterdayDefects());
    		} else if("ClosedY".equalsIgnoreCase(expandType)) {
    			for(Defect defect:dashboardForm.getClosedYesterdayDefects()) {
    				defect.setSort(dashboardForm.getSort());
    			}
    			Collections.sort(dashboardForm.getClosedYesterdayDefects());
    		}
    		return mapping.findForward("sortDefect");
    	} else {
    		dashboardForm.setSort("defectId desc");
    	}
    	
    	if(null != request.getParameter("expandType")) {
    		dashboardForm.setExpandType(request.getParameter("expandType"));
    		return mapping.findForward("expandDashboard");
    	} else {
    		dashboardForm.setExpandType(null);
    	}
    	
    	if(null != request.getParameter("export")) {
    		request.getSession().setAttribute("dashboardForm", dashboardForm);
    		request.setAttribute("dashboardForm", dashboardForm);
    		request.getRequestDispatcher("export.do").forward(request, response);
    	}
    	String userName = null;
    	String role = null;
    	if(request.getSession().getAttribute("user") == null) {
    		return mapping.findForward("login");
    	} else {
    		userName = (String) request.getSession().getAttribute("user");
    		role = (String) request.getSession().getAttribute("role");
    	}
		
    	Cookie[] cookies = request.getCookies();
		if(cookies !=null){
			for(Cookie cookie : cookies){
				if(cookie.getName().equals("user")) 
					userName = cookie.getValue();
				else if(cookie.getName().equals("role")) 
					role = cookie.getValue();
			}
		}
		dashboardForm.setLoginUser(userName);
		dashboardForm.setRole(role);
		
		Configuration configuration = Util.readConfigFile();
        List<Tab> tabs = configuration.getTabs();
    	
    	dashboardForm.setTabs(tabs);
    	
    	int tab = 0;
    	if(null != request.getParameter("tab")) {
    		tab = Integer.parseInt(request.getParameter("tab"));
    		dashboardForm.setTabIndex(tab+"");
    	}
    	if(null != request.getParameter("subTab")) {
    		dashboardForm.setSubProject(request.getParameter("subTab"));
    	}
    	int subTab = Integer.parseInt(dashboardForm.getSubProject());
    	
		String tabName = Util.getTabAttribute(configuration, "tabname", tab, subTab);
		for(Tab selectedTab:tabs) {
			if(selectedTab.getTabIndex() ==  tab) {
				dashboardForm.setSubTabs(selectedTab.getSubTabs());
			}
		}
		if(configuration.getUseRegressionInputFile().equalsIgnoreCase("false")) {
			Map<String, List<String>> regressionData = Util.getIteration(configuration, Util.getTabAttribute(configuration, "project", tab, subTab));
			dashboardForm.setProjectId(Util.getTabAttribute(configuration, "project", tab, subTab));
			if(null != regressionData) {
				if(dashboardForm.getOperatingSystem().equalsIgnoreCase("IOS")) {
					Util.retrieveTestResults(dashboardForm, configuration, null, regressionData.get("IOS"));
				} else if(dashboardForm.getOperatingSystem().equalsIgnoreCase("WINDOWS")) {
					Util.retrieveTestResults(dashboardForm, configuration, null, regressionData.get("WIN"));
				} else {
					List<String> allTestSets = new ArrayList<String>();
					allTestSets.addAll(regressionData.get("IOS"));
					allTestSets.addAll(regressionData.get("WIN"));
					Util.retrieveTestResults(dashboardForm, configuration, null, allTestSets);
				}
				
			} else {
				Util.retrieveTestCases(dashboardForm, configuration, Util.getTabAttribute(configuration, "cutoffdate", tab, tab));
			}
		} else {
			RegressionData regressionData = Util.getRegressionSetDetails(Util.getTabAttribute(configuration, "tabUniqueId", tab, subTab));
			dashboardForm.setProjectId(Util.getTabAttribute(configuration, "project", tab, subTab));
			if(null != regressionData) {
				if(dashboardForm.getOperatingSystem().equalsIgnoreCase("IOS")) {
					Util.retrieveTestCasesUsingSets(dashboardForm, configuration, regressionData.getCutoffDate(), regressionData.getIosTestSetsIds());
				} else if(dashboardForm.getOperatingSystem().equalsIgnoreCase("WINDOWS")) {
					Util.retrieveTestCasesUsingSets(dashboardForm, configuration, regressionData.getCutoffDate(), regressionData.getWinTestSetsIds());
				} else {
					List<String> allTestSets = new ArrayList<String>();
					allTestSets.addAll(regressionData.getIosTestSetsIds());
					allTestSets.addAll(regressionData.getWinTestSetsIds());
					Util.retrieveTestResults(dashboardForm, configuration, regressionData.getCutoffDate(), allTestSets);
				}
				
			} else {
				Util.retrieveTestCases(dashboardForm, configuration, Util.getTabAttribute(configuration, "cutoffdate", tab, tab));
			}
		}
    	dashboardForm.setCutoffDate(Util.getTabAttribute(configuration, "cutoffdate", tab, subTab));
    	dashboardForm.setProjectId(Util.getTabAttribute(configuration, "project", tab, subTab));
    	dashboardForm.setSelectedRelease(Util.getTabAttribute(configuration, "release", tab, subTab));
    	dashboardForm.setTag(Util.getTabAttribute(configuration, "tag", tab, subTab));
    	dashboardForm.setTabName(tabName);
		
    	Util.populateDefectData(dashboardForm, configuration);
    	
    	if(dashboardForm.getRegressionData()) {
			List<Priority> testCases =  dashboardForm.getTestCasesPriorities();
			String regressionStr = "";
			for(Priority testCase : testCases) {
				if(regressionStr.equals("")) {
					regressionStr  = testCase.getPriorityName() + ": " + testCase.getPriorityCount();
				} else {
					regressionStr  = regressionStr + ", " + testCase.getPriorityName() + ": " + testCase.getPriorityCount();
				}
			}
			regressionStr = "Total: " + dashboardForm.getTestCasesCount() + ", " + regressionStr;
			dashboardForm.setRegressionMsg(regressionStr);
		}
		
		
    	return mapping.findForward("showDashboard");
    }
    
    
}
