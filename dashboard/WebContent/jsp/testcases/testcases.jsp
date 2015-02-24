<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
	<div id="subheader" style="border-radius: 15px;">
		<font color="white" size="2" face="Tahoma"><b>Regression Test Cases</b></font>
	</div>
	<div style="width:45%; float:left;">
		<p align="center">
			<font color="#3d838a" size="2" face="Tahoma">
				<c:out value="${DashboardForm.testCasesCount}"/>
			</font>
		</p>
		<img src="/dashboard/chart?query=${DashboardForm.regressionMsg}"></img>
		<div style="width: 100%; height: 1em"></div>
	</div>
	<div style="width: 100%; height: 1em; clear:both"></div>
</div>