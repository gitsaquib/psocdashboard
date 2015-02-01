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
 * @author Dell
 */
public class LoginAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	if(null != request.getParameter("logout")) {
    		request.getSession().invalidate();
    	} else {
	    	LoginForm loginForm = (LoginForm) form;
	    	if(null != loginForm.getUsername()) {
	    		String password = Util.readUserFile(loginForm.getUsername());
	    		if(null != password && password.equals(loginForm.getPassword())) {
	    			HttpSession session = request.getSession();
					session.setAttribute("user", loginForm.getUsername());
					session.setMaxInactiveInterval(30*60);
					Cookie userName = new Cookie("user", loginForm.getUsername());
					userName.setMaxAge(30*60);
					response.addCookie(userName);
					return mapping.findForward("loginsuccess");
	    		}
	    	}
    	}
        return mapping.findForward("login");
    }
}
