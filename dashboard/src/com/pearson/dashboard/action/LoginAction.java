/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pearson.dashboard.form.LoginForm;
import com.pearson.dashboard.util.Util;

/**
 *
 * @author Mohammed Saquib (mohammed.saquib)
 */
public class LoginAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	if(null != request.getParameter("logout")) {
    		request.getSession().invalidate();
    	} else {
	    	LoginForm loginForm = (LoginForm) form;
	    	if(null != loginForm.getUsername()) {
	    		String userDetails = Util.readUserFile(loginForm.getUsername());
	    		String attributes[] = userDetails.split(":");
	    		String password = attributes[0];
	    		String displayName = attributes[1];
	    		String role = attributes[3];
	    		if(null != password && password.equals(loginForm.getPassword())) {
	    			HttpSession session = request.getSession();
					session.setAttribute("user", displayName);
					session.setAttribute("role", role);
					session.setMaxInactiveInterval(30*60);
					Cookie userName = new Cookie("user", displayName);
					userName.setMaxAge(30*60);
					response.addCookie(userName);
					
					Cookie roleC = new Cookie("role", role);
					roleC.setMaxAge(30*60);
					response.addCookie(roleC);
					
					return mapping.findForward("loginsuccess");
	    		}
	    	}
    	}
        return mapping.findForward("login");
    }
}
