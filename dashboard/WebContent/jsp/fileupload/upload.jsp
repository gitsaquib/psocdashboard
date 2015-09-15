<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link rel="stylesheet" href="../../css/dashboard.css" type="text/css"/>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
		<script src="../../js/jquery-1.11.1.min.js"></script> 
		<script language="javascript" type="text/javascript" src='../../js/jquery.js'></script>
		<script type="text/javascript" src="../../js/tabber.js"></script>
		<script type="text/javascript" src="../../js/common.js"></script>
		<link rel="stylesheet" href="../../css/tabber.css" TYPE="text/css" MEDIA="screen"/>
		<script language="javascript" type="text/javascript" src="../../js/jquery.colorbox.js"></script>
		<link href="../../css/colorbox.css" type="text/css" rel="stylesheet" />
		<link href="../../css/jqModal.css" rel="stylesheet" type="text/css"/>
		<title>::: Test Cases Results :::</title>
		<style type="text/css">
			body {
				background: #fafafa url(http://jackrugile.com/images/misc/noise-diagonal.png);
				color: #444;
				font: 100%/30px 'Helvetica Neue', helvetica, arial, sans-serif;
				text-shadow: 0 1px 0 #fff;
			}

			strong {
				font-weight: bold; 
			}

			em {
				font-style: italic; 
			}

			table {
				background: #f5f5f5;
				border-collapse: separate;
				box-shadow: inset 0 1px 0 #fff;
				font-size: 12px;
				line-height: 24px;
				margin: 30px auto;
				text-align: left;
				width: 800px;
			}	

			th {
				background: url(http://jackrugile.com/images/misc/noise-diagonal.png), linear-gradient(#777, #444);
				border-left: 1px solid #555;
				border-right: 1px solid #777;
				border-top: 1px solid #555;
				border-bottom: 1px solid #333;
				box-shadow: inset 0 1px 0 #999;
				color: #fff;
			  font-weight: bold;
				padding: 10px 15px;
				position: relative;
				text-shadow: 0 1px 0 #000;	
			}

			th:after {
				background: linear-gradient(rgba(255,255,255,0), rgba(255,255,255,.08));
				content: '';
				display: block;
				height: 25%;
				left: 0;
				margin: 1px 0 0 0;
				position: absolute;
				top: 25%;
				width: 100%;
			}

			th:first-child {
				border-left: 1px solid #777;	
				box-shadow: inset 1px 1px 0 #999;
			}

			th:last-child {
				box-shadow: inset -1px 1px 0 #999;
			}

			td {
				border-right: 1px solid #fff;
				border-left: 1px solid #e8e8e8;
				border-top: 1px solid #fff;
				border-bottom: 1px solid #e8e8e8;
				padding: 10px 15px;
				position: relative;
				transition: all 300ms;
			}

			td:first-child {
				box-shadow: inset 1px 0 0 #fff;
			}	

			td:last-child {
				border-right: 1px solid #e8e8e8;
				box-shadow: inset -1px 0 0 #fff;
			}	

			tr {
				background: url(http://jackrugile.com/images/misc/noise-diagonal.png);	
			}

			tr:nth-child(odd) td {
				background: #f1f1f1 url(http://jackrugile.com/images/misc/noise-diagonal.png);	
			}

			tr:last-of-type td {
				box-shadow: inset 0 -1px 0 #fff; 
			}

			tr:last-of-type td:first-child {
				box-shadow: inset 1px -1px 0 #fff;
			}	

			tr:last-of-type td:last-child {
				box-shadow: inset -1px -1px 0 #fff;
			}	

			tbody:hover td {
				color: transparent;
				text-shadow: 0 0 3px #aaa;
			}

			tbody:hover tr:hover td {
				color: #444;
				text-shadow: 0 1px 0 #fff;
			}
			</style>
		</head>
	<body>
        <form method="post" id="uplaodfile" enctype="multipart/form-data" onsubmit="VerifyUploadFile();">
			<div style="border:1px solid #46464f; border-style: dotted; border-radius: 15px;">
				<div id="subheader" style="border-radius: 15px;">
					<font color="white" size="2" face="Tahoma"><b>Test Cases Results</b></font>
				</div>
				<div style="width:100%;" id="tableDiv">
					<table style="width:95%;">
						<thead>
							<tr>
			    				<td width="15%">Select file to upload: <font color="red">*</font></td>
			    				<td><input type="file" name="uploadFile" id="uploadFile"/></td>
			    				<td width="15%">Update results:<font color="red">*</font></td> 
			    				<td>
			    					<input type="radio" name="passOnly">Pass Only (Will update only Pass results)</input> 
			    					<input type="radio" name="both" checked="checked">All (Will update all results)</input> 
			    				</td>
			    			</tr>
			    			<tr>
			    				<td width="15%">Test Set: <font color="red">*</font></td>
			    				<td><input type="text" id="testSetId" name="testSetId"/></td>
			    				<td width="15%">Updated By: <font color="red">*</font></td>
			    				<td><input type="text" name="emailId" id="emailId"/> (Rally username)</td>
			    			</tr>
			    			<tr>
			    				<td width="15%">Build Number: <font color="red">*</font></td>
			    				<td><input type="text" id="buildNumber" name="buildNumber"/></td>
			    				<td colspan="2" align="right">
			    					<font color="red" size="1">Please verify Test Set number and Rally username before submitting</font>
			    					<input type="submit" value="Upload" />
			    				</td>
			    			</tr>
			    		</thead>
			    	</table>
				</div>
				<div style="width: 100%; height: 1em; clear:both"></div>
			</div>
		</form>
	</body>
</html>