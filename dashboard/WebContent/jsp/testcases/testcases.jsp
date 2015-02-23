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
		<c:forEach var="priority" items="${DashboardForm.testCasesPriorities}">
			<input type="hidden" id="${priority.priorityName}" value="${priority.priorityCount}">
		</c:forEach>
		<div id="pie1" style="margin-top:20px; margin-left:20px; width:200px; height:200px;"></div>
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