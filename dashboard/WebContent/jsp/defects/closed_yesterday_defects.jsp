<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
	<div id="subheader" style="border-radius: 15px;">
		<font color="white" size="2" face="Tahoma"><b>Closed [Yesterday] Defects</b></font>
	</div>
	<div style="width:45%; float:left;">
		<p align="center">
			<font color="blue" size="8" face="Tahoma">
				<c:out value="${DashboardForm.closedYesterdayDefectCount}"/>
			</font>
			<font color="blue" size="4" face="Tahoma">
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
	<div style="width:55%; float:right;">
		<table style="width:100%">
			<c:choose>
				<c:when test="${DashboardForm.expandType != null && DashboardForm.expandType == 'ClosedY'}">
					<c:forEach var="defect" items="${DashboardForm.closedYesterdayDefects}" varStatus="status">
						<tr class="${loop.index % 2 == 0 ? 'even' : 'odd'}">
		                    <td>
		                    	<a href="${defect.defectUrl}" target="_blank">
	                    			<font color="blue" size="3" face="Tahoma"><c:out value="${defect.defectId}"/></font>
		                    	</a>
		                    </td>
		                    <td><font color="blue" size="3" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
		                    <td><font color="blue" size="3" face="Tahoma"><c:out value="${defect.project}"/></font></td>
		                    <td><font color="blue" size="3" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
		                </tr>
		            </c:forEach>
		            <tr>
		            	<a href="dashboard.do?collapse=true&tab=${DashboardForm.tabIndex}">
		            		<font color="grey" size="2" face="Tahoma">Collapse</font>
		            	</a>
		            </tr>
				</c:when>
				<c:otherwise>
					<c:forEach var="defect" items="${DashboardForm.closedYesterdayDefects}" varStatus="status" begin="0" end="9">
						<tr class="${loop.index % 2 == 0 ? 'even' : 'odd'}">
		                    <td>
	                    		<a href="${defect.defectUrl}" target="_blank">
	                    			<font color="blue" size="3" face="Tahoma"><c:out value="${defect.defectId}"/></font>
		                    	</a>
		                    </td>
		                    <td><font color="blue" size="3" face="Tahoma"><c:out value="${defect.priority}"/></font></td>
		                    <td><font color="blue" size="3" face="Tahoma"><c:out value="${defect.project}"/></font></td>
		                    <td><font color="blue" size="3" face="Tahoma"><c:out value="${defect.lastUpdateDate}"/></font></td>
		                </tr>
		            </c:forEach>	
		            <tr>
		            	<a href="dashboard.do?expandType=ClosedY&tab=${DashboardForm.tabIndex}">
		            		<font color="grey" size="2" face="Tahoma">Expand</font>
		            	</a>
		            </tr>
				</c:otherwise>
			</c:choose>
            
		</table>
	</div>
	<div style="width: 100%; height: 1em; clear:both"></div>
</div>