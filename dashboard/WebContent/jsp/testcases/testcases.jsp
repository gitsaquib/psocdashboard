<html>
	<head>
		<link class="include" rel="stylesheet" type="text/css" href="syntaxhighlighter/styles/jquery.jqplot.min.css" />
	   	<link rel="stylesheet" type="text/css" href="syntaxhighlighter/styles/examples.min.css" />
	   	<link type="text/css" rel="stylesheet" href="syntaxhighlighter/styles/shCoreDefault.min.css" />
	   	<link type="text/css" rel="stylesheet" href="syntaxhighlighter/styles/shThemejqPlot.min.css" />
	</head>
	<body onload ="drawPie();">
	<div style="width: 590px;">
	<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
		<div id="subheader" style="border-radius: 15px;">
			<font color="white" size="2" face="Tahoma"><b>Regression Test Cases</b></font>
		</div>
		<div style="align=center;">
			<c:forEach var="priority" items="${DashboardForm.testCasesPriorities}">
				<input type="hidden" id="${priority.priorityName}" value="${priority.priorityCount}">
			</c:forEach>
			<span title="${DashboardForm.regressionMsg}">
				<div id="pie1" style="margin-top:20px; margin-left:20px; width:400px; height:210px;"></div>
			</span>
			<div style="width: 100%; height: 1em"></div>
		</div>
		<div style="width: 100%; height: 1em; clear:both"></div>
	</div>
	</div>
	<script class="include" type="text/javascript" src="syntaxhighlighter/scripts/jquery.jqplot.min.js"></script>
	<script type="text/javascript" src="syntaxhighlighter/scripts/shCore.min.js"></script>
	<script type="text/javascript" src="syntaxhighlighter/scripts/shBrushJScript.min.js"></script>
	<script type="text/javascript" src="syntaxhighlighter/scripts/shBrushXml.min.js"></script>
	<script class="include" type="text/javascript" src="syntaxhighlighter/scripts/jqplot.pieRenderer.min.js"></script>
	<script type="text/javascript" src="syntaxhighlighter/scripts/example.min.js"></script>
	</body>
</html>