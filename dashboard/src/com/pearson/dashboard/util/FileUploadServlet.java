package com.pearson.dashboard.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
 

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.rallydev.rest.RallyRestApi;
 
public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIRECTORY = "upload";
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
        	PrintWriter writer = response.getWriter();
            writer.println("Error: Form must has enctype=multipart/form-data.");
            writer.flush();
            return;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(MAX_REQUEST_SIZE);
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        try {
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
            	File storeFile = null;
            	String testSetId = "", emailId = "", buildNumber = "";
            	boolean updateAll = true;
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        storeFile = new File(filePath);
                        item.write(storeFile);
                        request.setAttribute("message", "Upload has been done successfully!");
                    } else {
                    	if("testSetId".equalsIgnoreCase(item.getFieldName())) {
                    		testSetId = item.getString();
                    	} else if("emailId".equalsIgnoreCase(item.getFieldName())) {
                    		emailId = item.getString();
                    	} else if("buildNumber".equalsIgnoreCase(item.getFieldName())) {
                    		buildNumber = item.getString();
                    	} else  if("passOnly".equalsIgnoreCase(item.getFieldName()) || "both".equalsIgnoreCase(item.getFieldName())) {
                    		if(item.getFieldName().equalsIgnoreCase("passOnly")) {
                    			updateAll = false;
                    		}
                    	}
                    }
                }
                String txtFilePath = "D:\\SeetestXlsReport\\tmp\\"+storeFile.getName().replace("html", "txt");
                SeetestReportUtil.uploadSeetestReport(storeFile, txtFilePath, testSetId);
                RallyRestApi restApi = SeetestReportUtil.loginRally(); 
                SeetestReportUtil.updateTestCaseResults(restApi, buildNumber, emailId, txtFilePath);
            }
        } catch (Exception ex) {
            request.setAttribute("message", "There was an error: " + ex.getMessage());
        }
        getServletContext().getRequestDispatcher("/jsp/fileupload/message.jsp").forward(request, response);
    }
}