<div id="section">
<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
	<div id="subheader" style="border-radius: 15px;">
		<font color="white" size="2" face="Tahoma"><b>Regression Test Cases</b></font>
	</div>
	<div style="align=center;">
		<p align="center">
			<font color="#3d838a" size="4" face="Tahoma">
				<c:out value="${DashboardForm.testCasesCount}"/>
			</font>
		</p>
		<table align="center" width="80%" class="myTable">
			<c:forEach var="priority" items="${DashboardForm.testCasesPriorities}">
				<tr>
					<td><font color="#3d838a" size="2" face="Tahoma"><c:out value="${priority.priorityName}"/></font></td>
					<td><font color="#3d838a" size="2" face="Tahoma"><span id="${priority.priorityName}"><c:out value="${priority.priorityCount}"/></span></font></td>
				</tr>
			</c:forEach>
			<tr>
				<div id="pie1" style="margin-top:20px; margin-left:20px; width:200px; height:200px;"></div>		
			</tr>
		</table>
		<div style="width: 100%; height: 1em"></div>
	</div>
	<div style="width: 100%; height: 1em; clear:both"></div>
</div>
</div>
<div id="pie1" style="margin-top:20px; margin-left:20px; width:200px; height:200px;"></div>
<script class="include" type="text/javascript" src="syntaxhighlighter/scripts/jquery.jqplot.min.js"></script>
<script type="text/javascript" src="syntaxhighlighter/scripts/shCore.min.js"></script>
<script type="text/javascript" src="syntaxhighlighter/scripts/shBrushJScript.min.js"></script>
<script type="text/javascript" src="syntaxhighlighter/scripts/shBrushXml.min.js"></script>
<script class="include" type="text/javascript" src="syntaxhighlighter/scripts/jqplot.pieRenderer.min.js"></script>
<script type="text/javascript" src="syntaxhighlighter/scripts/example.min.js"></script>