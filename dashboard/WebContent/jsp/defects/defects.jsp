<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link rel="stylesheet" href="css/dashboard.css" type="text/css">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" />
		<script src="js/jquery-1.11.1.min.js"></script> 
		<script type="text/javascript" src="js/tabber.js"></script>
		<link rel="stylesheet" href="css/tabber.css" TYPE="text/css" MEDIA="screen">
		<title>::: Dashboard :::</title>
	</head>
	<body>
		<form>
			<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
				<div id="subheader" style="border-radius: 15px;">
					<font color="white" size="2" face="Tahoma"><b>${DashboardForm.expandType} Defects</b></font>
				</div>
				<div style="width: 100%; height: 1em; clear:both"></div>
				<div style="width:100%;">
					<table style="width:95%; border-collapse:collapse;" border="1" align="center">
						<thead>
							<tr>
						    	<th width="8%"><font color="black" size="2" face="Tahoma">Defect Id</font></th>
						    	<th><font color="black" size="2" face="Tahoma">Description</font></th>
						    	<th width="5%"><font color="black" size="2" face="Tahoma">Priority</font></th>
						    	<th width="5%"><font color="black" size="2" face="Tahoma">Project</font></th>
						    	<th width="15%"><font color="black" size="2" face="Tahoma">Modify Date</font></th>
						    </tr>
					    </thead>
						<c:choose>
							<c:when test="${DashboardForm.expandType == 'Submitted' }">
								<c:forEach var="defect" items="${DashboardForm.submittedDefects}" varStatus="status">
									<tr>
					                    <td width="8%">
					                    	<a href="${defect.defectUrl}" target="_blank">
					                   			<font color="red" size="2" face="Tahoma"><c:out value="${defect.defectId}"/></font>
					                    	</a>
					                    </td>
					                    <td>
					                    	<font color="red" size="2" face="Tahoma">
						                    	<span title="${defect.defectDesc}">
						                    		<c:out value="${fn:substring(defect.defectDesc, 0, 75)}"/>
						                    		<c:if test="${fn:length(defect.defectDesc) > 75}">
						                    			...
						                    		</c:if>
						                    	</span>
					                    	</font>
					                    </td>
					                    <td width="5%"><font color="red" size="2" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
					                    <td width="5%"><font color="red" size="2" face="Tahoma"><c:out value="${defect.project}"/></font></td>
					                    <td width="15%"><font color="red" size="2" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
					                </tr>
					            </c:forEach>
							</c:when>					           
							<c:when test="${DashboardForm.expandType == 'Open' }">
								<c:forEach var="defect" items="${DashboardForm.openDefects}" varStatus="status">
									<tr>
					                    <td width="8%">
					                    	<a href="${defect.defectUrl}" target="_blank">
					                   			<font color="orange" size="2" face="Tahoma"><c:out value="${defect.defectId}"/></font>
					                    	</a>
					                    </td>
					                    <td>
											<font color="orange" size="2" face="Tahoma">
						                    	<span title="${defect.defectDesc}">
						                    		<c:out value="${fn:substring(defect.defectDesc, 0, 75)}"/>
													<c:if test="${fn:length(defect.defectDesc) > 75}">
						                    			...
						                    		</c:if>
						                    	</span>
					                    	</font>
										</td>
					                    <td width="5%"><font color="orange" size="2" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
					                    <td width="5%"><font color="orange" size="2" face="Tahoma"><c:out value="${defect.project}"/></font></td>
					                    <td width="15%"><font color="orange" size="2" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
					                </tr>
					            </c:forEach>
							</c:when>
							<c:when test="${DashboardForm.expandType == 'Fixed' }">
								<c:forEach var="defect" items="${DashboardForm.fixedDefects}" varStatus="status">
									<tr>
					                    <td width="8%">
					                    	<a href="${defect.defectUrl}" target="_blank">
					                   			<font color="green" size="2" face="Tahoma"><c:out value="${defect.defectId}"/></font>
					                    	</a>
					                    </td>
					                    <td>
					                    	<font color="green" size="2" face="Tahoma">
						                    	<span title="${defect.defectDesc}">
						                    		<c:out value="${fn:substring(defect.defectDesc, 0, 75)}"/>
						                    		<c:if test="${fn:length(defect.defectDesc) > 75}">
						                    			...
						                    		</c:if>
						                    	</span>
					                    	</font>
					                    </td>
					                    <td width="5%"><font color="green" size="2" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
					                    <td width="5%"><font color="green" size="2" face="Tahoma"><c:out value="${defect.project}"/></font></td>
					                    <td width="15%"><font color="green" size="2" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
					                </tr>
					            </c:forEach>
							</c:when>
							<c:when test="${DashboardForm.expandType == 'Closed' }">
								<c:forEach var="defect" items="${DashboardForm.closedDefects}" varStatus="status">
									<tr>
					                    <td width="8%">
					                    	<a href="${defect.defectUrl}" target="_blank">
					                   			<font color="blue" size="2" face="Tahoma"><c:out value="${defect.defectId}"/></font>
					                    	</a>
					                    </td>
					                    <td>
					                    	<font color="blue" size="2" face="Tahoma">
						                    	<span title="${defect.defectDesc}">
						                    		<c:out value="${fn:substring(defect.defectDesc, 0, 75)}"/>
						                    		<c:if test="${fn:length(defect.defectDesc) > 75}">
						                    			...
						                    		</c:if>
						                    	</span>
					                    	</font>
					                    </td>
					                    <td width="5%"><font color="blue" size="2" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
					                    <td width="5%"><font color="blue" size="2" face="Tahoma"><c:out value="${defect.project}"/></font></td>
					                    <td width="15%"><font color="blue" size="2" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
					                </tr>
					            </c:forEach>
							</c:when>
							<c:when test="${DashboardForm.expandType == 'OpenY' }">
								<c:forEach var="defect" items="${DashboardForm.openYesterdayDefects}" varStatus="status">
									<tr>
					                    <td width="8%">
					                    	<a href="${defect.defectUrl}" target="_blank">
					                   			<font color="orange" size="2" face="Tahoma"><c:out value="${defect.defectId}"/></font>
					                    	</a>
					                    </td>
					                    <td>
					                    	<font color="orange" size="2" face="Tahoma">
						                    	<span title="${defect.defectDesc}">
						                    		<c:out value="${fn:substring(defect.defectDesc, 0, 75)}"/>
						                    		<c:if test="${fn:length(defect.defectDesc) > 75}">
						                    			...
						                    		</c:if>
						                    	</span>
					                    	</font>
					                    </td>
					                    <td width="5%"><font color="orange" size="2" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
					                    <td width="5%"><font color="orange" size="2" face="Tahoma"><c:out value="${defect.project}"/></font></td>
					                    <td width="15%"><font color="orange" size="2" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
					                </tr>
					            </c:forEach>
							</c:when>
							<c:when test="${DashboardForm.expandType == 'ClosedY' }">
								<c:forEach var="defect" items="${DashboardForm.closedYesterdayDefects}" varStatus="status">
									<tr>
					                    <td width="8%">
					                    	<a href="${defect.defectUrl}" target="_blank">
					                   			<font color="blue" size="2" face="Tahoma"><c:out value="${defect.defectId}"/></font>
					                    	</a>
					                    </td>
					                    <td>
					                    	<font color="blue" size="2" face="Tahoma">
						                    	<span title="${defect.defectDesc}">
						                    		<c:out value="${fn:substring(defect.defectDesc, 0, 75)}"/>
						                    		<c:if test="${fn:length(defect.defectDesc) > 75}">
						                    			...
						                    		</c:if>
						                    	</span>
					                    	</font>
					                    </td>
					                    <td width="5%"><font color="blue" size="2" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
					                    <td width="5%"><font color="blue" size="2" face="Tahoma"><c:out value="${defect.project}"/></font></td>
					                    <td width="15%"><font color="blue" size="2" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
					                </tr>
					            </c:forEach>
							</c:when>
			            </c:choose>
					</table>
				</div>
				<div style="width: 100%; height: 1em; clear:both"></div>
			</div>
		</form>
	</body>
</html>