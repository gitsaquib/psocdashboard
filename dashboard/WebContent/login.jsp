<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link rel="stylesheet" href="css/dashboard.css" type="text/css">
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
		<script src="js/jquery-1.11.1.min.js"></script> 
		<title>::: Dashboard :::</title>
		<script type="text/javascript">
			function validateLoginFields() {
				if(document.getElementById("username").value == "" || document.getElementById("username").value == "password") {
					alert("Username and Password fields are mandatory");
					return false;
				} else {
					document.loginForm.submit();
				}
			}
		</script>
	</head>
	<body>
		<form name="loginForm" method="post" action="login.do" onsubmit="return validateLoginFields();">
			<div id="container" align="center">
				<%@include file="jsp/common/header.jsp" %>
				<div style="width: 100%; height: 100px;"></div>
				<div style="border:1px solid #46464f; width: 40%; border-style: dotted; border-radius: 15px;" align="center">
					<div style="width: 100%; height: 1em"></div>
					<img src="images/pearson.gif" alt="Always Learning" height=150" width="250">
					<table>
	                    <tr>
	                        <td>
	                        	<font size="2" face="Tahoma" color="red">*</font>
	                        	<font size="2" face="Tahoma"><b>Username  : </b></font>
	                        </td>
	                        <td> <input name="username" id="username" size=15 type="text" /> </td> 
	                    </tr>
	                    <tr>
	                        <td>
	                        	<font size="2" face="Tahoma" color="red">*</font>
	                        	<font size="2" face="Tahoma"><b>Password  : </b></font>
	                        </td>
	                        <td> <input name="password" id="password" size=15 type="password" /> </td> 
	                    </tr>
	                    <tr>
	                        <td>&nbsp;</td>
	                        <td> 
	                        	<div style="width: 100%; height: 1em"></div>
	                			<input type="submit" value="Login" />
	                			<input type="reset" value="Reset"/>
	                		</td> 
	                    </tr>
	                </table>
	               
	                <div style="width: 100%; height: 1em"></div>
                </div>
                <div style="width: 100%; height: 100px;"></div>
				<div id="footer" >
					<table align="center">
						<tr>
							<td align="center">
								<font color="white" face="Tahoma">© Pearson Education</font>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</form>
	</body>
</html>
