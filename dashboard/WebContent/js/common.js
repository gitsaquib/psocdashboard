function VerifyUploadFile() {
	var file = document.getElementById('uploadFile').value;
	var fileType = file.substring(file.lastIndexOf(".") + 1);
	if(fileType == 'html') {
		if(confirm("Are you uploading Seetest report?")) {
			SubmitUploadFileForm();
		} 
	} else if(fileType == 'txt') {
		if(confirm("Are you uploading tab delimited report?")) {
			SubmitUploadFileForm();
		}
	} else {
		alert("Incorrect file format. You can upload Seetest format HTML file or a tab delimited file.");
		return false;
	}
}

function SubmitUploadFileForm() {
	if(VerifyMandatoryFields()) {
		document.getElementById('uplaodfile').action="/dashboard/uploadFile";
		document.getElementById('uplaodfile').submit();
	}
}

function VerifyMandatoryFields() {
	var testSetId =	document.getElementById("testSetId").value;
	if(testSetId == null || testSetId == "") {
		alert("Test Set is a mandatory field");
		return false;
	}
	
	var email =	document.getElementById("emailId").value;
	if(email == null || email == "") {
		alert("Email is a mandatory field");
		return false;
	}
	return true;
}