<div id="nav">
	<div style="width: 100%; height: 1em"></div>
	<form name="dashboardForm" method="post" id="dashboardForm">
		<c:if test="${DashboardForm.subProjects != null && DashboardForm.expandType == null}">
		<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;" align="center">
			<div style="width: 100%; height: 1em"></div>
			<select name="subProject" id="subProject">
			    <c:forEach items="${DashboardForm.subProjects}" var="project">
			        <option value="${project.tabIndex}" ${project.tabIndex == DashboardForm.subProject ? 'selected' : ''}>${project.projectKey}</option>
			    </c:forEach>
			</select>
			<input type="submit" value="Go" onclick="javascript: retrieveSubProject();"/>
			<div style="width: 100%; height: 1em"></div>
		</div>
		</c:if>
		<div style="width: 100%; height: 1em"></div>
		<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;" align="center">
			<font color="gray" size="2" face="Tahoma">Product:<br><b><c:out value="${DashboardForm.projectName}"/></b></font>
			<br>
			<font color="red" size="2" face="Tahoma"><c:out value="${DashboardForm.selectedRelease}"/></font>
		</div>
		<div style="width: 100%; height: 1em"></div>
	</form>
</div>