<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
	<div id="subheader" style="border-radius: 15px;">
		<font color="white" size="2" face="Tahoma"><b>Regression Test Cases</b></font>
	</div>
	<div style="width:45%; float:left;">
		<p align="center">
			<font color="#3d838a" size="8" face="Tahoma">
				<c:out value="${DashboardForm.testCasesCount}"/>
			</font>
		</p>
		<table align="center" width="80%" class="myTable">
			<c:forEach var="priority" items="${DashboardForm.testCasesPriorities}">
				<tr>
					<td><font color="#3d838a" size="2" face="Tahoma"><c:out value="${priority.priorityName}"/></font></td>
					<td><font color="#3d838a" size="2" face="Tahoma"><c:out value="${priority.priorityCount}"/></font></td>
					<td>
						<div class="gauge">
							<span class="text"></span>
							<span class="color" style="width: ${priority.pxSize}%"></span>
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>
		<div style="width: 100%; height: 1em"></div>
	</div>
	<div style="width: 100%; height: 1em; clear:both"></div>
</div>