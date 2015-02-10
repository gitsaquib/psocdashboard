/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.pearson.dashboard.vo.Project;

/**
 *
 * @author Mohammed Saquib (mohammed.saquib)
 */
public class DashboardAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	DashboardForm dashboardForm = (DashboardForm) form;
    	
    	if(null != request.getParameter("expandType")) {
    		dashboardForm.setExpandType(request.getParameter("expandType"));
    		return mapping.findForward("expandDashboard");
    	} else {
    		dashboardForm.setExpandType(null);
    	}
    	if(null != request.getParameter("collapse")) {
    		dashboardForm.setExpandType(null);
    		return mapping.findForward("showDashboard");
    	}
    	
    	if(null != request.getParameter("export")) {
    		request.getSession().setAttribute("dashboardForm", dashboardForm);
    		request.setAttribute("dashboardForm", dashboardForm);
    		request.getRequestDispatcher("export.do").forward(request, response);
    	}
    	String userName = null;
    	if(request.getSession().getAttribute("user") == null) {
    		return mapping.findForward("login");
    	} else 
    		userName = (String) request.getSession().getAttribute("user");
		Cookie[] cookies = request.getCookies();
		if(cookies !=null){
			for(Cookie cookie : cookies){
				if(cookie.getName().equals("user")) 
					userName = cookie.getValue();
			}
		}
		dashboardForm.setLoginUser(userName);
		Configuration configuration = Util.readConfigFile();
        List<Project> projects = configuration.getProjects();
    	
    	dashboardForm.setProjects(projects);
    	
    	int tab = 0;
    	if(null != request.getParameter("tab")) {
    		tab = Integer.parseInt(request.getParameter("tab"));
    		dashboardForm.setTabIndex(tab+"");
    	}
    	
    	if(tab == 2) {
    		List<Project> childProjects = new ArrayList<>();
    		for(Project project:projects) {
    			if("RUN".equalsIgnoreCase(project.getParentTab())) {
    				childProjects.add(project);
    			}
    		}
    		Collections.sort(childProjects);
    		dashboardForm.setSubProjects(childProjects);
    	} else {
    		dashboardForm.setSubProjects(null);
    		dashboardForm.setSubProject(null);
    	}
    	if(null != dashboardForm.getSubProject()) {
    		tab = Integer.parseInt(dashboardForm.getSubProject());
    	}
    	dashboardForm.setCutoffDate(Util.getProjectAttribute(configuration, "cutoffdate", tab));
    	dashboardForm.setProjectId(Util.getProjectAttribute(configuration, "project", tab));
    	dashboardForm.setSelectedRelease(Util.getProjectAttribute(configuration, "release", tab));
    	dashboardForm.setTabName(Util.getProjectAttribute(configuration, "tabname", tab));
		
    	Util.populateDefectData(dashboardForm, configuration);
		Util.retrieveTestCases(dashboardForm, configuration, Util.getProjectAttribute(configuration, "cutoffdate", tab));
        
		return mapping.findForward("showDashboard");
    }
    
    
}
