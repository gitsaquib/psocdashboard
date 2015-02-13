<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link rel="stylesheet" href="css/dashboard.css" type="text/css">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" />
		<script src="js/jquery-1.11.1.min.js"></script> 
		<script language="javascript" type="text/javascript" src='js/jquery.js'></script>
		<script type="text/javascript" src="js/tabber.js"></script>
		<link rel="stylesheet" href="css/tabber.css" TYPE="text/css" MEDIA="screen">
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
			
			function retrieveSubProject() {
				document.getElementById("tab2").style.display="none";
				document.getElementById("loading").style.display="block";
				document.dashboardForm.action = "dashboard.do?tab=2";
				document.dashboardForm.submit();
			}
			
			function expandDefects(type, index) {
				$.colorbox({iframe:true, width:"100%", height:"100%", 
					title:type,href:'/dashboard/dashboard.do?expandType='+type+'&tab='+index});
			}
		</script>	
		<title>::: Dashboard :::</title>
	</head>
	<body>
		<form>
			<input type="hidden" name="dashboard" value="${DashboardForm}" />
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
				 		<div id="tab0" class="tabbertab">
				 			<%@include file="jsp/common/left_nav.jsp" %>
				    		<h3>2-12 [2.0]</h3>
				        	<div class="siteWidth">
								<table>
									<tr>
										<td>
											<%@include file="jsp/defects/submitted_defects.jsp" %>
										</td>
										<td>
											<%@include file="jsp/testcases/testcases.jsp" %>
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
									<tr>
										<td>
											<%@include file="jsp/defects/closed_defects.jsp" %>
										</td>
										<td>
											&nbsp;
										</td>
									</tr>
								</table>
								<div class="clr"></div>
							</div>		
				    	</div>
				    	<div id="tab1" class="tabbertab">
				    		<%@include file="jsp/common/left_nav.jsp" %>
				        	<h3>K1 [2.0]</h3>
				        	<div class="siteWidth">
								<div id="section">
									<%@include file="jsp/defects/submitted_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/open_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/fixed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/closed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/open_yesterday_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/closed_yesterday_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/testcases/testcases.jsp" %>
									<div style="width: 100%; height: 1em"></div>
								</div>
								<div class="clr"></div>
							</div>		
				    	</div>
				    	<div id="tab2" class="tabbertab">
				    		<%@include file="jsp/common/left_nav.jsp" %>
				        	<h3>Run [2.0]</h3>
				        	<div class="siteWidth">
								<div id="section">
									<%@include file="jsp/defects/submitted_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/open_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/fixed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/closed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/open_yesterday_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/closed_yesterday_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
								</div>
								<div class="clr"></div>
							</div>		
				    	</div>
				    	<div id="tab3" class="tabbertab">
				    		<%@include file="jsp/common/left_nav.jsp" %>
				        	<h3>Older [2.0]</h3>
				        	<div class="siteWidth">
								<div id="section">
									<%@include file="jsp/defects/submitted_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/open_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/fixed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/closed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
								</div>
								<div class="clr"></div>
							</div>		
				    	</div>
				    	<div id="tab4" class="tabbertab">
				    		<%@include file="jsp/common/left_nav.jsp" %>
				        	<h3>Authering [2.0]</h3>
				        	<div class="siteWidth">
								<div id="section">
									<%@include file="jsp/defects/submitted_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/open_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/fixed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
									<%@include file="jsp/defects/closed_defects.jsp" %>
									<div style="width: 100%; height: 1em"></div>
								</div>
								<div class="clr"></div>
							</div>		
				    	</div>
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
