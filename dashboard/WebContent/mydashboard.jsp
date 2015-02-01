<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link rel="stylesheet" href="css/dashboard.css" type="text/css">
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
		<script src="js/jquery-1.11.1.min.js"></script> 
		<script type="text/javascript">
			jQuery(document).ready(function() {
				jQuery('.tabs .tab-links a').on('click', function(e)  {
					var currentAttrValue = jQuery(this).attr('href');
					jQuery('.tabs ' + currentAttrValue).show().siblings().hide();
	 				jQuery(this).parent('li').addClass('active').siblings().removeClass('active');
	 				e.preventDefault();
				});
			});
			
			function submitForm() {
				document.dashboardForm.action = "dashboard.do?release="+document.getElementById("selectedRelease").value+"&proj="+document.getElementById("projectId").value;
				document.dashboardForm.submit();
			}
		</script>	
		<title>::: Dashboard :::</title>
	</head>
	<body>
		<form name="dashboardForm" method="post">
			<input type="hidden" name="dashboard" value="${DashboardForm}" />
			<div id="container">
				<%@include file="jsp/common/header.jsp" %>
				<div style="width: 100%; height: 1em; clear:both"></div>
				<div class="tabs">
				    <div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
				    	<div id="filterheader" style="border-radius: 15px;">
				    		<table width="100%">
				    		<tr>
				    		<td>
							<font color="white" size="2" face="Tahoma"><b>Welcome ${DashboardForm.loginUser}!!!</b></font>
							</td>
							<td align="right">
							<font color="white" size="2" face="Tahoma">
								Project: &nbsp;
								<select id="projectId">
									<option value="-1">---Select---</option>
									<c:forEach items="${DashboardForm.projects}" var="prj">
									    <c:choose>
									    	<c:when test="${prj.projectId eq DashboardForm.projectId}">
									    		<option value="${prj.projectId}" selected>${prj.projectKey}</option>		
									    	</c:when>
									    	<c:otherwise>
									    		<option value="${prj.projectId}">${prj.projectKey}</option>
									    	</c:otherwise>
									    </c:choose>
									</c:forEach>
								</select>
								Release: &nbsp;
								<select id="selectedRelease">
									<option value="-1">---Select---</option>
									<c:forEach items="${DashboardForm.releases}" var="rel">
									    <c:choose>
									    	<c:when test="${rel.releaseName eq DashboardForm.selectedRelease}">
									    		<option value="${rel.releaseName}" selected>${rel.releaseName}</option>
									    	</c:when>
									    	<c:otherwise>
									    		<option value="${rel.releaseName}">${rel.releaseName}</option>
									    	</c:otherwise>
									    </c:choose>
									</c:forEach>
								</select>
								<input type="button" value="Go" onclick="javascript: submitForm();" />
							</font>
							</td>
							<td>&nbsp;</td>
							<td width="35%" align="right">
							<c:if test="${DashboardForm.projectId != null}">
								<a href="dashboard.do?export=true">
									<font color="white" size="2" face="Tahoma">Export to Excel</font>
								</a>
							</c:if>
							</td>
							<td>&nbsp;</td>
							<td>
								<a href="login.do?logout=true">
									<font color="white" size="2" face="Tahoma">Logout</font>
								</a>
							</td>
							</tr>
							</table>
						</div>
				 	</div>
				 	<c:choose>
				 	<c:when test="${DashboardForm.projectId != null}">
				 	<div class="tab-content">
				 		<%@include file="jsp/common/left_nav.jsp" %>
				    	<div id="tab1" class="tab active">
				        	<div class="siteWidth">
								<div id="section">
									<%@include file="jsp/defects/submitted_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/open_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/fixed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/closed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/open_yesterday_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/closed_yesterday_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
								</div>
								<div class="clr"></div>
							</div>		
				    	</div>
					</div>
					</c:when>
					<c:otherwise>
						<div style="width: 100%; height: 1em"></div>
						<font color="blue" size="4" face="Tahoma">Please select project and release</font>
					</c:otherwise>
					</c:choose>
				</div>
			</div>
		</form>
	</body>
</html>
