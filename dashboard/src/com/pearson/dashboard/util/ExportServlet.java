package com.pearson.dashboard.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.pearson.dashboard.form.DashboardForm;
import com.pearson.dashboard.vo.Defect;

public class ExportServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OutputStream out = null;
	    try
	    {
	    	DashboardForm dashboardForm = (DashboardForm) request.getAttribute("dashboardForm");
	    	response.setContentType("application/vnd.ms-excel");
	    	response.setHeader("Content-Disposition","attachment; filename="+dashboardForm.getProjectName()+"-"+dashboardForm.getSelectedRelease()+".xls");
	    	out = response.getOutputStream();
	    	HSSFWorkbook wb = new HSSFWorkbook();
	    	
	    	List<Defect> submittedDefects = dashboardForm.getSubmittedDefects();
	    	HSSFSheet sheet = wb.createSheet();
	    	wb.setSheetName(0, "Submitted");
	    	createSheetHeader(sheet);
	    	createSheetData(sheet, submittedDefects);
	    	
	    	List<Defect> openDefects = dashboardForm.getOpenDefects();
	    	sheet = wb.createSheet();
	    	wb.setSheetName(1, "Open");
	    	createSheetHeader(sheet);
	    	createSheetData(sheet, openDefects);
	    	
	    	List<Defect> fixedDefects = dashboardForm.getFixedDefects();
	    	sheet = wb.createSheet();
	    	wb.setSheetName(2, "Fixed");
	    	createSheetHeader(sheet);
	    	createSheetData(sheet, fixedDefects);
	    	
	    	List<Defect> closedDefects = dashboardForm.getClosedDefects();
	    	sheet = wb.createSheet();
	    	wb.setSheetName(3, "Closed");
	    	createSheetHeader(sheet);
	    	createSheetData(sheet, closedDefects);
	    	
	    	wb.write(out);
	    } catch (Exception e) {
	    	throw new ServletException("Exception in Excel Sample Servlet", e);
	    } finally {
	    	if (out != null)
	    		out.close();
	    }
	}

	private void createSheetHeader(HSSFSheet sheet) {
		HSSFRow row = sheet.createRow(0);
		
		short cellNum = 0;
		
		HSSFCell cell = row.createCell(cellNum);
		cell.setCellValue("Defect Id");
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellValue("Description");
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellValue("Priority");
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellValue("Last Update Date");
		cellNum++;
	}
	
	private void createSheetData(HSSFSheet sheet, List<Defect> defects) {
		int rowNum = 1;
		for(Defect defect:defects) {
			HSSFRow row = sheet.createRow(rowNum);
			
			short cellNum = 0;
			
			HSSFCell cell = row.createCell(cellNum);
			cell.setCellValue(defect.getDefectId());
			cellNum++;
			
			cell = row.createCell(cellNum);
			cell.setCellValue(defect.getDefectDesc());
			cellNum++;
			
			cell = row.createCell(cellNum);
			cell.setCellValue(defect.getPriority());
			cellNum++;
			
			cell = row.createCell(cellNum);
			cell.setCellValue(defect.getLastUpdateDateOriginal());
			cellNum++;
			
			rowNum++;
		}
	}
}
