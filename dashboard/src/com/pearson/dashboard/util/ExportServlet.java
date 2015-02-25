package com.pearson.dashboard.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.pearson.dashboard.form.DashboardForm;
import com.pearson.dashboard.vo.Defect;

/**
 * 
 * @author Mohammed Saquib (mohammed.saquib)
 *
 */
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
	    	short columnIndex = 1;
	    	short columnWidth = 10000;
	    	
	    	List<Defect> submittedDefects = dashboardForm.getSubmittedDefects();
	    	HSSFSheet sheet = wb.createSheet();
	    	sheet.setColumnWidth(columnIndex, columnWidth);
	    	wb.setSheetName(0, "Submitted");
	    	createSheetHeader(sheet, wb);
	    	createSheetData(sheet, submittedDefects, wb);
	    	
	    	List<Defect> openDefects = dashboardForm.getOpenDefects();
	    	sheet = wb.createSheet();
	    	sheet.setColumnWidth(columnIndex, columnWidth);
	    	wb.setSheetName(1, "Open");
	    	createSheetHeader(sheet, wb);
	    	createSheetData(sheet, openDefects, wb);
	    	
	    	List<Defect> fixedDefects = dashboardForm.getFixedDefects();
	    	sheet = wb.createSheet();
	    	sheet.setColumnWidth(columnIndex, columnWidth);
	    	wb.setSheetName(2, "Fixed");
	    	createSheetHeader(sheet, wb);
	    	createSheetData(sheet, fixedDefects, wb);
	    	
	    	List<Defect> closedDefects = dashboardForm.getClosedDefects();
	    	sheet = wb.createSheet();
	    	sheet.setColumnWidth(columnIndex, columnWidth);
	    	wb.setSheetName(3, "Closed");
	    	createSheetHeader(sheet, wb);
	    	createSheetData(sheet, closedDefects, wb);
	    	
	    	wb.write(out);
	    } catch (Exception e) {
	    	throw new ServletException("Exception in Excel Sample Servlet", e);
	    } finally {
	    	if (out != null)
	    		out.close();
	    }
	}

	private void createSheetHeader(HSSFSheet sheet, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(0);
		
		HSSFFont font = wb.createFont();
		font.setFontName("Veranda");
		HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.AQUA.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        
        short cellNum = 0;
		
		HSSFCell cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue("Defect Id");
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue("Description");
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue("Priority");
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue("Project");
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue("Platform");
		cellNum++;
	}
	
	private void createSheetData(HSSFSheet sheet, List<Defect> defects, HSSFWorkbook wb) {
		int rowNum = 1;
		
		HSSFFont font = wb.createFont();
		font.setFontName("Veranda");
		HSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setWrapText(true);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        
		for(Defect defect:defects) {
			HSSFRow row = sheet.createRow(rowNum);
			
			short cellNum = 0;
			
			HSSFCell cell = row.createCell(cellNum);
			cell.setCellStyle(style);
			cell.setCellValue(defect.getDefectId());
			cellNum++;
			
			cell = row.createCell(cellNum);
			cell.setCellStyle(style);
			cell.setCellValue(defect.getDefectDesc());
			cellNum++;
			
			cell = row.createCell(cellNum);
			cell.setCellStyle(style);
			cell.setCellValue(defect.getPriority());
			cellNum++;
			
			cell = row.createCell(cellNum);
			cell.setCellStyle(style);
			cell.setCellValue(defect.getProject());
			cellNum++;
			
			cell = row.createCell(cellNum);
			cell.setCellStyle(style);
			cell.setCellValue(defect.getPlatform());
			cellNum++;
			
			rowNum++;
		}
	}
}
