<div id="nav">
	<div style="width: 100%; height: 1em"></div>
	<c:if test="${DashboardForm.subTabs != null && !(empty DashboardForm.subTabs)}">
	<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;" align="center">
		<div style="width: 100%; height: 1em"></div>
		<select name="subProject" id="subProject" style="width: 120px;" onchange="retrieveSubProject('${DashboardForm.tabIndex}', this.value);">
		    <c:forEach items="${DashboardForm.subTabs}" var="project">
		        <option value="${project.tabIndex}" ${project.tabIndex == DashboardForm.subProject ? 'selected' : ''}>
		        	<font color="gray" size="2" face="Tahoma">${project.tabDisplayName}</font>
		        </option>
		    </c:forEach>
		</select>&nbsp;
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
</div>