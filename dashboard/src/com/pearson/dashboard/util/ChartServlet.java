package com.pearson.dashboard.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
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
		JFreeChart chart = ChartFactory.createPieChart("", dataset, legend, tooltips, urls);
		int width = 500;
		int height = 350;
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
