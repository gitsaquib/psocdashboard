<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link rel="stylesheet" href="css/dashboard.css" type="text/css">
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
		<script src="js/jquery-1.11.1.min.js"></script> 
		<title>::: Dashboard :::</title>
	</head>
	<body>
		<form name="loginForm" method="post" action="login.do">
			<div id="container" align="center">
				<%@include file="jsp/common/header.jsp" %>
				<div style="width: 100%; height: 100px;"></div>
				<div style="border:1px solid #46464f; width: 40%; border-style: dotted; border-radius: 15px;" align="center">
					<div style="width: 100%; height: 1em"></div>
					<table>
	                    <tr>
	                        <td><font size="2" face="Tahoma">Username  : </font></td><td> <input name="username" id="username" size=15 type="text" /> </td> 
	                    </tr>
	                    <tr>
	                        <td><font size="2" face="Tahoma">Password  : </font></td><td> <input name="password" id="password" size=15 type="password" /> </td> 
	                    </tr>
	                </table>
	                <div style="width: 100%; height: 1em"></div>
	                <input type="submit" value="Login" />
	                <input type="reset" value="Reset"/>
	                <div style="width: 100%; height: 1em"></div>
                </div>
                <div style="width: 100%; height: 100px;"></div>
				<div id="footer">
			 		&nbsp;
				</div>
			</div>
		</form>
	</body>
</html>
