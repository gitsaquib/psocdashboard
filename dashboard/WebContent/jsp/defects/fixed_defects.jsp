<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
	<div id="subheader" style="border-radius: 15px;">
		<font color="white" size="2" face="Tahoma"><b>Fixed Defects</b></font>
	</div>
	<div style="width:45%; float:left;">
		<p align="center">
			<font color="green" size="8" face="Tahoma">
				<c:out value="${DashboardForm.fixedDefectCount}"/>
			</font>
			<font color="green" size="4" face="Tahoma">
				<br>P1 & P2: <c:out value="${DashboardForm.fixedP1AndP2Count}"/>
			</font>
		</p>
		<table align="center" width="80%" class="myTable">
			<c:forEach var="priority" items="${DashboardForm.fixedPriorities}">
				<tr>
					<td><font color="green" size="2" face="Tahoma"><c:out value="${priority.priorityName}"/></font></td>
					<td><font color="green" size="2" face="Tahoma"><c:out value="${priority.priorityCount}"/></font></td>
					<td>
						<div class="gauge">
							<span class="text"></span>
							<span class="green" style="width: ${priority.pxSize}%"></span>
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>
		<div style="width: 100%; height: 1em"></div>
	</div>
	<div style="width:55%; float:right;">
		<table style="width:100%">
			<c:forEach var="defect" items="${DashboardForm.fixedDefects}" varStatus="status" begin="0" end="9">
                <tr class="${loop.index % 2 == 0 ? 'even' : 'odd'}">
                    <td><font color="green" size="3" face="Tahoma"><c:out value="${defect.defectId}"/></font></td>
                    <td><font color="green" size="3" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
                    <td><font color="green" size="3" face="Tahoma"><c:out value="${defect.project}"/></font></td>
                    <td><font color="green" size="3" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
                </tr>
            </c:forEach>
		</table>
	</div>
	<div style="width: 100%; height: 1em; clear:both"></div>
</div>