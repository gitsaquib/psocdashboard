<div id="section">
<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
	<div id="subheader" style="border-radius: 15px;">
		<a href='javascript: openDefectChart(escape("${DashboardForm.componentsCountOpen}"));'>
			<font color="white" size="2" face="Tahoma">
				Open Defects
			</font>
		</a>
	</div>
	<div style="align=center;">
		<p align="center">
			<a href="javascript: expandDefects('Open', ${DashboardForm.tabIndex});">
				<font color="orange" size="4" face="Tahoma">
					<span title="${DashboardForm.openMsg}">
						<c:out value="${DashboardForm.openDefectCount}"/>
					</span>
				</font>
			</a>
			<font color="orange" size="2" face="Tahoma">
				<br>P1 & P2: <c:out value="${DashboardForm.openP1AndP2Count}"/>
			</font>
		</p>
		<table align="center" width="80%" class="myTable">
			<c:forEach var="priority" items="${DashboardForm.openPriorities}">
				<tr>
					<td><font color="orange" size="2" face="Tahoma"><c:out value="${priority.priorityName}"/></font></td>
					<td><font color="orange" size="2" face="Tahoma"><c:out value="${priority.priorityCount}"/></font></td>
					<td>
						<div class="gauge">
							<span class="text"></span>
							<span class="orange" style="width: ${priority.pxSize}%"></span>
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