<div id="section">
<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
	<div id="subheader" style="border-radius: 15px;">
		<font color="white" size="2" face="Tahoma"><b>Closed [Yesterday] Defects</b></font>
	</div>
	<div style="align=center;">
		<p align="center">
			<a href="javascript: expandDefects('ClosedY', ${DashboardForm.tabIndex});">
				<font color="blue" size="4" face="Tahoma">
					<span title="${DashboardForm.closedYMsg}">
						<c:out value="${DashboardForm.closedYesterdayDefectCount}"/>
					</span>
				</font>
			</a>
			<font color="blue" size="2" face="Tahoma">
				<br>P1 & P2: <c:out value="${DashboardForm.closedYesterdayP1AndP2Count}"/>
			</font>
		</p>
		<table align="center" width="80%" class="myTable">
			<c:forEach var="priority" items="${DashboardForm.closedYesterdayPriorities}">
				<tr>
					<td><font color="blue" size="2" face="Tahoma"><c:out value="${priority.priorityName}"/></font></td>
					<td><font color="blue" size="2" face="Tahoma"><c:out value="${priority.priorityCount}"/></font></td>
					<td>
						<div class="gauge">
							<span class="text"></span>
							<span class="blue" style="width: ${priority.pxSize}%"></span>
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>
		<div style="width: 100%; height: 1em"></div>
	</div>
	<div style="width: 100%; height: 1em; clear:both"></div>
</div>
</div>