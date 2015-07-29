package com.pearson.dashboard.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class ChartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String query = request.getParameter("query");
		DefaultPieDataset dataset = new DefaultPieDataset();
		int count = getCount(query, "Pass");
		dataset.setValue("Pass["+count+"]", count);
		count = getCount(query, "Blocked");
		dataset.setValue("Blocked["+count+"]", count);
		count = getCount(query, "Error");
		dataset.setValue("Error["+count+"]", count);
		count = getCount(query, "Fail");
		dataset.setValue("Fail["+count+"]", count);
		count = getCount(query, "Inconclusive");
		dataset.setValue("Inconclusive["+count+"]", count);
		count = getCount(query, "NotAttempted");
		dataset.setValue("NotAttempted["+count+"]", count);
		boolean legend = true;
		boolean tooltips = true;
		boolean urls = false;
		JFreeChart chart = ChartFactory.createPieChart(""+getCount(query, "Total"), dataset, legend, tooltips, urls);
		PiePlot plot = (PiePlot) chart.getPlot();
		count = getCount(query, "Blocked");
		plot.setSectionPaint("Blocked["+count+"]", new Color(255,127,14));
		count = getCount(query, "NotAttempted");
		plot.setSectionPaint("NotAttempted["+count+"]", new Color(31,119,180));
		count = getCount(query, "Pass");
		plot.setSectionPaint("Pass["+count+"]", new Color(44,160,44));
		count = getCount(query, "Fail");
		plot.setSectionPaint("Fail["+count+"]", new Color(214,39,40));
		count = getCount(query, "Inconclusive");
		plot.setSectionPaint("Inconclusive["+count+"]", new Color(171,70,140));
		count = getCount(query, "Error");
		plot.setSectionPaint("Error["+count+"]", new Color(0,0,0));
		int width = 610;
		int height = 210;
		BufferedImage bi = chart.createBufferedImage(width, height);
		OutputStream out = response.getOutputStream();
		ImageIO.write(bi, "png", out);
		out.close();
	}
	
	private int getCount(String query, String type) {
		String data[] = query.split(",");
		if(null != data) {
			for(int i=0; i<data.length; i++) {
				String params[] = data[i].trim().split(":");
				if(params[0].equalsIgnoreCase(type)) {
					return Integer.parseInt(params[1].trim());
				}
			}
		}
		return 0;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
