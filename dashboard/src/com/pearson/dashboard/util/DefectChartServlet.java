package com.pearson.dashboard.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class DefectChartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String query = URLDecoder.decode(request.getParameter("query"));
		String type = URLDecoder.decode(request.getParameter("type"));
		DefaultPieDataset dataset = new DefaultPieDataset();

		int total = populateDataSet(query, dataset);
		
		boolean legend = true;
		boolean tooltips = true;
		if(type.equalsIgnoreCase("Submitted and Open Defects")) {
			tooltips = false;
		}
		boolean urls = false;
		JFreeChart chart = ChartFactory.createPieChart(type+"["+total+"]", dataset, legend, tooltips, urls);
		PiePlot plot = (PiePlot) chart.getPlot();
		
		int width = 800;
		int height = 400;
		if(type.equalsIgnoreCase("Submitted and Open Defects")) {
			width = 610;
			height = 610;
		}
		BufferedImage bi = chart.createBufferedImage(width, height);
		OutputStream out = response.getOutputStream();
		ImageIO.write(bi, "png", out);
		out.close();
	}
	
	private int populateDataSet(String query, DefaultPieDataset dataset) {
		int total = 0;
		String data[] = query.split("////");
		if(null != data) {
			for(int i=0; i<data.length; i++) {
				String params[] = data[i].trim().split("~");
				if(params.length > 1) {
					total = total + Integer.parseInt(params[1]);
					dataset.setValue(params[0]+"["+Integer.parseInt(params[1])+"]", Integer.parseInt(params[1]));
				}
			}
		}
		return total;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
