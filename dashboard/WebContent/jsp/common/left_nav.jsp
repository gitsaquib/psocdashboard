<div id="nav">
	<div style="width: 100%; height: 1em"></div>
	<c:if test="${DashboardForm.subTabs != null && !(empty DashboardForm.subTabs)}">
	<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;" align="center">
		<div style="width: 100%; height: 1em"></div>
		<select name="subProject" id="subProject" style="width: 120px;" onchange="retrieveSubProject('${DashboardForm.tabIndex}', this.value, '${DashboardForm.operatingSystem}');">
		    <c:forEach items="${DashboardForm.subTabs}" var="project">
		        <option value="${project.tabIndex}" ${project.tabIndex == DashboardForm.subProject ? 'selected' : ''}>
		        	<font color="gray" size="2" face="Tahoma">${project.tabDisplayName}</font>
		        </option>
		    </c:forEach>
		</select>&nbsp;
		<div style="width: 100%; height: 1em"></div>
	</div>
	<div style="width: 100%; height: 1em"></div>
	</c:if>
	<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;" align="center">
		<div style="width: 100%; height: 1em"></div>
		<select name="operatingSystem" id="operatingSystem" style="width: 120px;" onchange="retrieveOSDashboard(${DashboardForm.tabIndex}, ${DashboardForm.subProject}, this.value);">
		    <c:forEach items="${DashboardForm.operatingSystems}" var="os">
		        <option value="${os}" ${os == DashboardForm.operatingSystem ? 'selected' : ''}>
		        	<font color="gray" size="2" face="Tahoma">${os}</font>
		        </option>
		    </c:forEach>
		</select>&nbsp;
		<div style="width: 100%; height: 1em"></div>
	</div>
	<div style="width: 100%; height: 1em"></div>
	<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;" align="center">
		<span>
			<font color="gray" size="2" face="Tahoma">
				<b><u>Filter information:</u></b> 
				${DashboardForm.filterInfo}
			</font>
		</span>
		<div style="width: 100%; height: 1em"></div>
	</div>
</div>