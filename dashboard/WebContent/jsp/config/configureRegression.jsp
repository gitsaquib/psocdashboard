<HTML>
<HEAD>
<title>Configure Test Sets</title>
<SCRIPT lang="javascript">
        function addRow(tableID) 
        {
                var table = document.getElementById(tableID);

                var rowCount = table.rows.length;
                var row = table.insertRow(rowCount);
                var counts = rowCount - 1;

                var cell1 = row.insertCell(0);
                var iOSTestSet = document.createElement("input");
                iOSTestSet.type = "text";
                iOSTestSet.id = "iOS["+counts+"]";
                cell1.appendChild(iOSTestSet);

        }
		function chedkValues() {
			var i = 0;
			var testSets = "";
			while(null != document.getElementById("iOS["+i+"]")) 
			{
				var testSet = document.getElementById("iOS["+i+"]").value;
				var pattern = /TS.*\d{2}$/;
				if(!pattern.test(testSet))
				{
					alert("Incorrect test set "+testSet);
					return false;
				} 
				else 
				{
					if(testSets ==  "") 
					{
						testSets = testSet;
					} 
					else 
					{
						testSets = testSets +","+ testSet;
					}
				}
				i++;
			}
			alert(testSets);
		}
</SCRIPT>
</HEAD>
<BODY>
	<form action="#" method="post" onsubmit="chedkValues();">
		<TABLE id="configureIosTestSetTable">
			<TR>
                <TD>iOS TestSets</TD>
			</TR>
			<TR>
                <TD><INPUT type="text" id="iOS[0]" /></TD>
			</TR>
		</TABLE>
		<INPUT type="button" value="Add More" onclick="addRow('configureIosTestSetTable')" /> 
		<input type="submit" value="SUBMIT" />
	</form>
</BODY>
</HTML>