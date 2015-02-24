<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link class="include" rel="stylesheet" type="text/css" href="syntaxhighlighter/styles/jquery.jqplot.min.css" />
    	<link rel="stylesheet" type="text/css" href="syntaxhighlighter/styles/examples.min.css" />
    	<link type="text/css" rel="stylesheet" href="syntaxhighlighter/styles/shCoreDefault.min.css" />
    	<link type="text/css" rel="stylesheet" href="syntaxhighlighter/styles/shThemejqPlot.min.css" />
    	<script class="include" type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<link rel="stylesheet" href="css/dashboard.css" type="text/css"/>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
		<script src="js/jquery-1.11.1.min.js"></script> 
		<script language="javascript" type="text/javascript" src='js/jquery.js'></script>
		<script type="text/javascript" src="js/tabber.js"></script>
		<link rel="stylesheet" href="css/tabber.css" TYPE="text/css" MEDIA="screen"/>
		<script language="javascript" type="text/javascript" src="js/jquery.colorbox.js"></script>
		<link href="css/colorbox.css" type="text/css" rel="stylesheet" />
		<link href="css/jqModal.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript">
			jQuery(document).ready(function() {
				jQuery('.tabs .tab-links a').on('click', function(e)  {
					var currentAttrValue = jQuery(this).attr('href');
					jQuery('.tabs ' + currentAttrValue).show().siblings().hide();
	 				jQuery(this).parent('li').addClass('active').siblings().removeClass('active');
	 				e.preventDefault();
				});
			});
			
			function drawPie()
			{
				var pass = parseInt(document.getElementById('Pass').value);
				var blocked = parseInt(document.getElementById('Blocked').value);
				var error = parseInt(document.getElementById('Error').value);
				var fail = parseInt(document.getElementById('Fail').value);
				var inconclusive = parseInt(document.getElementById('Inconclusive').value);
				var notAttempted = parseInt(document.getElementById('NotAttempted').value);
				var plot1 = $.jqplot('pie1', [[['Pass',pass],
				                               ['Blocked',blocked],
				                               ['Error',error],
				                               ['Fail',fail],
				                               ['Inconclusive',inconclusive],
				                               ['NotAttempted',notAttempted]]], {
				gridPadding: {top:0, bottom:38, left:200, right:0},
				seriesDefaults:{
					renderer:$.jqplot.PieRenderer, 
					trendline:{ show:false }, 
					rendererOptions: { padding: 8, showDataLabels: true }
				},
				legend:{
					show:true, 
					placement: 'outside', 
					rendererOptions: {
						numberRows: 2
					}, 
					location:'s',
					marginTop: '15px'
				}       
				});
			}
			
		</script>	
		<title>::: Dashboard :::</title>
	</head>
	<body onload="drawPie();">
		<form id="dashboardForm" name="dashboardForm" method="post">
			<div id="container">
				<%@include file="jsp/common/header.jsp" %>
				<div style="width: 100%; height: 1em; clear:both"></div>
				<div class="tabs">
				    <div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
				    	<div id="filterheader" style="border-radius: 15px;">
				    		<table width="100%">
				    		<tr>
				    		<td>
				    			<font color="white" size="2" face="Tahoma"><b>Welcome ${DashboardForm.loginUser}!!!</b></font>
				    		</td>
				    		<td width="55%" align="right">
								<c:if test="${DashboardForm.projectId != null}">
									<a href="dashboard.do?export=true">
										<font color="white" size="2" face="Tahoma">Export to Excel</font>
									</a>
								</c:if>
							</td>
							<td>&nbsp;</td>
							<td>
								<a href="login.do?logout=true">
									<font color="white" size="2" face="Tahoma">Logout</font>
								</a>
							</td>
							</tr>
							</table>
						</div>
				 	</div>
				 	<div class="tabber">
				 		<c:forEach var="tab" items="${DashboardForm.tabs}" varStatus="tabVarStatus">
				 			<div id="tab${tab.tabIndex}" class="tabbertab">
					 			<%@include file="jsp/common/left_nav.jsp" %>
					    		<h3>${tab.tabDisplayName}</h3>
					        	<div class="siteWidth">
									<table>
										<tr>
											<td>
												<%@include file="jsp/defects/submitted_defects.jsp" %>
											</td>
											<td>
												<c:choose>
													<c:when test="${!DashboardForm.regressionData}">
														<%@include file="jsp/testcases/testcases.jsp" %>
													</c:when>
													<c:otherwise>
														<%@include file="jsp/defects/closed_defects.jsp" %>
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
										<tr>
											<td>
												<%@include file="jsp/defects/open_defects.jsp" %>
											</td>
											<td>
												<%@include file="jsp/defects/open_yesterday_defects.jsp" %>
											</td>
										</tr>
										<tr>
											<td>
												<%@include file="jsp/defects/fixed_defects.jsp" %>
											</td>
											<td>
												<%@include file="jsp/defects/closed_yesterday_defects.jsp" %>
											</td>
										</tr>
										<c:choose>
											<c:when test="${DashboardForm.regressionData}">
												<tr>
													<td>
														<%@include file="jsp/defects/closed_defects.jsp" %>
													</td>
													<td>
														&nbsp;
													</td>
												</tr>
											</c:when>
										</c:choose>	
									</table>
									<div class="clr"></div>
								</div>		
				    		</div>
				 		</c:forEach>
				 		<div id="loading" style="display: none;margin: auto;" align="center" class="loadingDiv">
				    		<img src="loading.gif" alt="loading..."/>
				    		<div class="clr"></div>
				    	</div>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>
