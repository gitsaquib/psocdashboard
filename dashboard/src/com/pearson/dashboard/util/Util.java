/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pearson.dashboard.form.DashboardForm;
import com.pearson.dashboard.vo.Configuration;
import com.pearson.dashboard.vo.Defect;
import com.pearson.dashboard.vo.Priority;
import com.pearson.dashboard.vo.Project;
import com.pearson.dashboard.vo.Release;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

/**
 *
 * @author Dell
 */
public class Util {
	
    public static List<Release> retrieveReleaseInfo(Configuration configuration) throws Exception {
    	RallyRestApi  restApi = loginRally(configuration);
    	List<Release> releases = retrieveRelease(restApi);
    	restApi.close();
    	return releases;
    }
    
    public static Map<String, List<Defect>> getDataFromRally(DashboardForm dashboard, Configuration configuration) throws Exception {
    	Map<String, List<Defect>> allDefects = new HashMap<String, List<Defect>>();
    	RallyRestApi  restApi = loginRally(configuration);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Open", dashboard.getSelectedRelease(), false);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Submitted", dashboard.getSelectedRelease(), false);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Fixed", dashboard.getSelectedRelease(), false);    	
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Closed", dashboard.getSelectedRelease(), false);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "ClosedY", dashboard.getSelectedRelease(), true);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "OpenY", dashboard.getSelectedRelease(), true);
    	restApi.close();
    	return allDefects;
    }

	private static void retrieveDefects(
			Map<String, List<Defect>> allDefects, RallyRestApi restApi,
			String projectId, String typeCategory, String releaseNum, boolean yesterdayDefects) throws IOException, ParseException {
		List<Defect> defects;
		QueryFilter queryFilter;
		QueryRequest defectRequest;
		QueryResponse projectDefects;
		JsonArray defectsArray;
		defects = new ArrayList<Defect>();
    	if(yesterdayDefects) {
    		String today = getDate("today");
    		String yesterday = getDate("yesterday");
    		if(typeCategory.equalsIgnoreCase("ClosedY")) {
    			queryFilter = new QueryFilter("State", "=", "Closed").and(new QueryFilter("Release.Name", "=", releaseNum)).and(new QueryFilter("ClosedDate", "<", today)).and(new QueryFilter("ClosedDate", ">=", yesterday));
    		} else {
    			queryFilter = new QueryFilter("Release.Name", "=", releaseNum).and(new QueryFilter("CreationDate", "<", today)).and(new QueryFilter("CreationDate", ">=", yesterday));
    		}
    	} else {
    		queryFilter = new QueryFilter("State", "=", typeCategory).and(new QueryFilter("Release.Name", "=", releaseNum));
    	}
    	defectRequest = new QueryRequest("defects");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("State", "Release", "Name", "FormattedID", "Environment", "Priority", "LastUpdateDate", "SubmittedBy", "Owner", "Project", "ClosedDate"));
    	defectRequest.setProject("/project/"+projectId);  
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(1000);
    	projectDefects = restApi.query(defectRequest);
    	defectsArray = projectDefects.getResults();
    	for(int i=0; i<defectsArray.size(); i++) {
            JsonElement elements =  defectsArray.get(i);
            JsonObject object = elements.getAsJsonObject();
            if(!object.get("Release").isJsonNull()) {
	            Defect defect =  new Defect();
	            defect.setDefectId(object.get("FormattedID").getAsString());

	            String strFormat1 = object.get("LastUpdateDate").getAsString().substring(0, 10);
	            DateFormat formatter1 = new SimpleDateFormat("yyyy-dd-MM"); 
	            Date date = (Date) formatter1.parse(strFormat1);
	            DateFormat formatter2 = new SimpleDateFormat("MMM dd");
	            defect.setLastUpdateDate(formatter2.format(date));
	            
	            defect.setLastUpdateDateOriginal(object.get("LastUpdateDate").getAsString().substring(0, 10));
	            
	            if(null != object.get("Priority") && !object.get("Priority").toString().isEmpty()) {
	                defect.setPriority("P"+object.get("Priority").getAsString().charAt(0));
	            } else {
	                defect.setPriority("TBD");
	            }
	            defect.setState(object.get("State").getAsString());
	            JsonObject project = object.get("Project").getAsJsonObject();
	            defect.setProject(project.get("_refObjectName").getAsString().charAt(0)+"");
	            defect.setDefectDesc(object.get("Name").getAsString());
	            defects.add(defect);
            }
        }
    	allDefects.put(typeCategory, defects);
	}
    
    public static Project getSelectedProject(String projectKey, Configuration configuration) {
    	if(null == projectKey) {
    		projectKey = "21028059357";
    	}
    	List<Project> projects = configuration.getProjects();
    	for(Project project:projects) {
    		if(project.getProjectId().equalsIgnoreCase(projectKey)) {
    			return project;
    		}
    	}
    	return null;
    }
    
    private static boolean isReleaseAlreadyAdded(List<Release> releases, String releaseName) {
    	for(Release release:releases) {
    		if(release.getReleaseName().equals(releaseName)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private static List<Release> retrieveRelease(RallyRestApi restApi) throws Exception{
    	List<Release> releases = new ArrayList<Release>();
    	QueryRequest query = new QueryRequest("Release");
    	QueryFilter queryFilter = new QueryFilter("State", "=", "Active").or(new QueryFilter("State", "=", "Planning"));
    	query.setQueryFilter(queryFilter);
    	QueryResponse projectDefects = restApi.query(query);
    	
    	JsonArray releasesArray = projectDefects.getResults();
    	for(int i=0; i<releasesArray.size(); i++) {
    		Release release = new Release();
            JsonElement elements =  releasesArray.get(i);
            JsonObject object = elements.getAsJsonObject();
            if(!isReleaseAlreadyAdded(releases, object.get("_refObjectName").getAsString())) {
	            release.setReleaseEndDate(object.get("ReleaseDate").getAsString());
	            release.setReleaseName(object.get("_refObjectName").getAsString());
	            release.setReleaseStartDate(object.get("ReleaseStartDate").getAsString());
	            releases.add(release);
            }
        }
    	return releases;
    }
    
    private static RallyRestApi loginRally(Configuration configuration) throws URISyntaxException {
    	return new RallyRestApi(new URI(configuration.getRallyURL()), configuration.getRallyUser(), configuration.getRallyPassword());
    }
    
    public static Configuration readConfigFile(){
    	Properties prop = new Properties();
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();           
    	InputStream stream = loader.getResourceAsStream("/config.properties");
    	try {
    		Configuration configuration = new Configuration();
			prop.load(stream);
			configuration.setRallyURL(prop.getProperty("rallyURL"));
			configuration.setRallyUser(prop.getProperty("rallyUser"));
			configuration.setRallyPassword(prop.getProperty("rallyPassword"));
			
			stream = loader.getResourceAsStream("/project.properties");
			prop.load(stream);
			
			List<Project> projectList = new ArrayList<Project>();  
			String projectsStr = prop.getProperty("projects");
			if(null != projectsStr) {
				String[] projects = projectsStr.split(";");
				if(null != projects) {
					for(int i=0; i<projects.length; i++) {
						String params = projects[i];
						if(null != params) {
							String param[] = params.split(":");
							Project project = new Project();
							project.setTabIndex(Integer.parseInt(param[0]));
							project.setProjectKey(param[1]);
							project.setProjectId(param[2]);
							project.setRelease(param[3]);
							projectList.add(project);
						}
					}
				}
			}
			configuration.setProjects(projectList);
			
			return configuration;
		} catch (IOException e) {
			return null;
		}
    }
    
    public static void populateDefectData(DashboardForm dashboardForm, Configuration configuration) throws Exception {
		Map<String, List<Defect>> allDefects = Util.getDataFromRally(dashboardForm, configuration);
		
		//Open defects
		List<Defect> openDefects = allDefects.get("Open");
		List<Priority> priorities = new ArrayList<Priority>();
		Priority tbd1 = new Priority();
		tbd1.setPriorityName("TBD");
		Priority priority1 = new Priority();
		priority1.setPriorityName("P1");
		Priority priority2 = new Priority();
		priority2.setPriorityName("P2");
		Priority priority3 = new Priority();
		priority3.setPriorityName("P3");
		Priority priority4 = new Priority();
		priority4.setPriorityName("P4");
		for(Defect defect:openDefects) {
		    if(defect.getPriority().equalsIgnoreCase("TBD")) {
		        tbd1.setPriorityCount(tbd1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P1")) {
		        priority1.setPriorityCount(priority1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P2")) {
		        priority2.setPriorityCount(priority2.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P3")) {
		        priority3.setPriorityCount(priority3.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P4")) {
		        priority4.setPriorityCount(priority4.getPriorityCount()+1);
		    }
		}
		
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(tbd1.getPriorityCount());
		arrayList.add(priority1.getPriorityCount());
		arrayList.add(priority2.getPriorityCount());
		arrayList.add(priority3.getPriorityCount());
		arrayList.add(priority4.getPriorityCount());
		Integer maximumCount = Collections.max(arrayList);
		if(maximumCount <= 0) {
			tbd1.setPxSize("0");
		    priority1.setPxSize("0");
		    priority2.setPxSize("0");
		    priority3.setPxSize("0");
		    priority4.setPxSize("0");
		} else {
		    tbd1.setPxSize(Math.round((100*tbd1.getPriorityCount())/maximumCount)+"");
		    priority1.setPxSize(Math.round((100*priority1.getPriorityCount())/maximumCount)+"");
		    priority2.setPxSize(Math.round((100*priority2.getPriorityCount())/maximumCount)+"");
		    priority3.setPxSize(Math.round((100*priority3.getPriorityCount())/maximumCount)+"");
		    priority4.setPxSize(Math.round((100*priority4.getPriorityCount())/maximumCount)+"");
		}
		priorities.add(tbd1);
		priorities.add(priority1);
		priorities.add(priority2);
		priorities.add(priority3);
		priorities.add(priority4);
		dashboardForm.setOpenDefects(openDefects);
		dashboardForm.setOpenPriorities(priorities);
		dashboardForm.setOpenDefectCount(openDefects.size());
		int p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setOpenP1AndP2Count(p1np2cnt);
		
		//Submitted defects
		List<Defect> submittedDefects = allDefects.get("Submitted");
		priorities = new ArrayList<Priority>();
		tbd1 = new Priority();
		tbd1.setPriorityName("TBD");
		priority1 = new Priority();
		priority1.setPriorityName("P1");
		priority2 = new Priority();
		priority2.setPriorityName("P2");
		priority3 = new Priority();
		priority3.setPriorityName("P3");
		priority4 = new Priority();
		priority4.setPriorityName("P4");
		for(Defect defect:submittedDefects) {
		    if(defect.getPriority().equalsIgnoreCase("TBD")) {
		        tbd1.setPriorityCount(tbd1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P1")) {
		        priority1.setPriorityCount(priority1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P2")) {
		        priority2.setPriorityCount(priority2.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P3")) {
		        priority3.setPriorityCount(priority3.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P4")) {
		        priority4.setPriorityCount(priority4.getPriorityCount()+1);
		    }
		}
		
		arrayList = new ArrayList<Integer>();
		arrayList.add(tbd1.getPriorityCount());
		arrayList.add(priority1.getPriorityCount());
		arrayList.add(priority2.getPriorityCount());
		arrayList.add(priority3.getPriorityCount());
		arrayList.add(priority4.getPriorityCount());
		maximumCount = Collections.max(arrayList);
		if(maximumCount <= 0) {
			tbd1.setPxSize("0");
		    priority1.setPxSize("0");
		    priority2.setPxSize("0");
		    priority3.setPxSize("0");
		    priority4.setPxSize("0");
		} else {
		    tbd1.setPxSize(Math.round((100*tbd1.getPriorityCount())/maximumCount)+"");
		    priority1.setPxSize(Math.round((100*priority1.getPriorityCount())/maximumCount)+"");
		    priority2.setPxSize(Math.round((100*priority2.getPriorityCount())/maximumCount)+"");
		    priority3.setPxSize(Math.round((100*priority3.getPriorityCount())/maximumCount)+"");
		    priority4.setPxSize(Math.round((100*priority4.getPriorityCount())/maximumCount)+"");
		}
		priorities.add(tbd1);
		priorities.add(priority1);
		priorities.add(priority2);
		priorities.add(priority3);
		priorities.add(priority4);
		dashboardForm.setSubmittedDefects(submittedDefects);
		dashboardForm.setSubmittedPriorities(priorities);
		dashboardForm.setSubmittedDefectCount(submittedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setSubmittedP1AndP2Count(p1np2cnt);
		
		//Fixed defects
		List<Defect> fixedDefects = allDefects.get("Fixed");
		priorities = new ArrayList<Priority>();
		tbd1 = new Priority();
		tbd1.setPriorityName("TBD");
		priority1 = new Priority();
		priority1.setPriorityName("P1");
		priority2 = new Priority();
		priority2.setPriorityName("P2");
		priority3 = new Priority();
		priority3.setPriorityName("P3");
		priority4 = new Priority();
		priority4.setPriorityName("P4");
		for(Defect defect:fixedDefects) {
		    if(defect.getPriority().equalsIgnoreCase("TBD")) {
		        tbd1.setPriorityCount(tbd1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P1")) {
		        priority1.setPriorityCount(priority1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P2")) {
		        priority2.setPriorityCount(priority2.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P3")) {
		        priority3.setPriorityCount(priority3.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P4")) {
		        priority4.setPriorityCount(priority4.getPriorityCount()+1);
		    }
		}
		
		arrayList = new ArrayList<Integer>();
		arrayList.add(tbd1.getPriorityCount());
		arrayList.add(priority1.getPriorityCount());
		arrayList.add(priority2.getPriorityCount());
		arrayList.add(priority3.getPriorityCount());
		arrayList.add(priority4.getPriorityCount());
		maximumCount = Collections.max(arrayList);
		if(maximumCount <= 0) {
			tbd1.setPxSize("0");
		    priority1.setPxSize("0");
		    priority2.setPxSize("0");
		    priority3.setPxSize("0");
		    priority4.setPxSize("0");
		} else {
		    tbd1.setPxSize(Math.round((100*tbd1.getPriorityCount())/maximumCount)+"");
		    priority1.setPxSize(Math.round((100*priority1.getPriorityCount())/maximumCount)+"");
		    priority2.setPxSize(Math.round((100*priority2.getPriorityCount())/maximumCount)+"");
		    priority3.setPxSize(Math.round((100*priority3.getPriorityCount())/maximumCount)+"");
		    priority4.setPxSize(Math.round((100*priority4.getPriorityCount())/maximumCount)+"");
		}
		priorities.add(tbd1);
		priorities.add(priority1);
		priorities.add(priority2);
		priorities.add(priority3);
		priorities.add(priority4);
		dashboardForm.setFixedDefects(fixedDefects);
		dashboardForm.setFixedPriorities(priorities);
		dashboardForm.setFixedDefectCount(fixedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setFixedP1AndP2Count(p1np2cnt);
		
		//Closed defects
		List<Defect> closedDefects = allDefects.get("Closed");
		priorities = new ArrayList<Priority>();
		tbd1 = new Priority();
		tbd1.setPriorityName("TBD");
		priority1 = new Priority();
		priority1.setPriorityName("P1");
		priority2 = new Priority();
		priority2.setPriorityName("P2");
		priority3 = new Priority();
		priority3.setPriorityName("P3");
		priority4 = new Priority();
		priority4.setPriorityName("P4");
		for(Defect defect:closedDefects) {
		    if(defect.getPriority().equalsIgnoreCase("TBD")) {
		        tbd1.setPriorityCount(tbd1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P1")) {
		        priority1.setPriorityCount(priority1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P2")) {
		        priority2.setPriorityCount(priority2.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P3")) {
		        priority3.setPriorityCount(priority3.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P4")) {
		        priority4.setPriorityCount(priority4.getPriorityCount()+1);
		    }
		}
		
		arrayList = new ArrayList<Integer>();
		arrayList.add(tbd1.getPriorityCount());
		arrayList.add(priority1.getPriorityCount());
		arrayList.add(priority2.getPriorityCount());
		arrayList.add(priority3.getPriorityCount());
		arrayList.add(priority4.getPriorityCount());
		maximumCount = Collections.max(arrayList);
		if(maximumCount <= 0) {
			tbd1.setPxSize("0");
		    priority1.setPxSize("0");
		    priority2.setPxSize("0");
		    priority3.setPxSize("0");
		    priority4.setPxSize("0");
		} else {
		    tbd1.setPxSize(Math.round((100*tbd1.getPriorityCount())/maximumCount)+"");
		    priority1.setPxSize(Math.round((100*priority1.getPriorityCount())/maximumCount)+"");
		    priority2.setPxSize(Math.round((100*priority2.getPriorityCount())/maximumCount)+"");
		    priority3.setPxSize(Math.round((100*priority3.getPriorityCount())/maximumCount)+"");
		    priority4.setPxSize(Math.round((100*priority4.getPriorityCount())/maximumCount)+"");
		}
		priorities.add(tbd1);
		priorities.add(priority1);
		priorities.add(priority2);
		priorities.add(priority3);
		priorities.add(priority4);
		dashboardForm.setClosedDefects(closedDefects);
		dashboardForm.setClosedPriorities(priorities);
		dashboardForm.setClosedDefectCount(closedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setClosedP1AndP2Count(p1np2cnt);
		
		//Closed Yesterday
		List<Defect> closedYesterdayDefects = allDefects.get("ClosedY");
		priorities = new ArrayList<Priority>();
		tbd1 = new Priority();
		tbd1.setPriorityName("TBD");
		priority1 = new Priority();
		priority1.setPriorityName("P1");
		priority2 = new Priority();
		priority2.setPriorityName("P2");
		priority3 = new Priority();
		priority3.setPriorityName("P3");
		priority4 = new Priority();
		priority4.setPriorityName("P4");
		for(Defect defect:closedYesterdayDefects) {
		    if(defect.getPriority().equalsIgnoreCase("TBD")) {
		        tbd1.setPriorityCount(tbd1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P1")) {
		        priority1.setPriorityCount(priority1.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P2")) {
		        priority2.setPriorityCount(priority2.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P3")) {
		        priority3.setPriorityCount(priority3.getPriorityCount()+1);
		    }
		    if(defect.getPriority().equalsIgnoreCase("P4")) {
		        priority4.setPriorityCount(priority4.getPriorityCount()+1);
		    }
		}
		
		arrayList = new ArrayList<Integer>();
		arrayList.add(tbd1.getPriorityCount());
		arrayList.add(priority1.getPriorityCount());
		arrayList.add(priority2.getPriorityCount());
		arrayList.add(priority3.getPriorityCount());
		arrayList.add(priority4.getPriorityCount());
		maximumCount = Collections.max(arrayList);
		if(maximumCount <= 0) {
			tbd1.setPxSize("0");
		    priority1.setPxSize("0");
		    priority2.setPxSize("0");
		    priority3.setPxSize("0");
		    priority4.setPxSize("0");
		} else {
		    tbd1.setPxSize(Math.round((100*tbd1.getPriorityCount())/maximumCount)+"");
		    priority1.setPxSize(Math.round((100*priority1.getPriorityCount())/maximumCount)+"");
		    priority2.setPxSize(Math.round((100*priority2.getPriorityCount())/maximumCount)+"");
		    priority3.setPxSize(Math.round((100*priority3.getPriorityCount())/maximumCount)+"");
		    priority4.setPxSize(Math.round((100*priority4.getPriorityCount())/maximumCount)+"");
		}
		priorities.add(tbd1);
		priorities.add(priority1);
		priorities.add(priority2);
		priorities.add(priority3);
		priorities.add(priority4);
		dashboardForm.setClosedYesterdayDefects(closedYesterdayDefects);
		dashboardForm.setClosedYesterdayPriorities(priorities);
		dashboardForm.setClosedYesterdayDefectCount(closedYesterdayDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setClosedYesterdayP1AndP2Count(p1np2cnt);
		
		//Open Yesterday
		List<Defect> openYesterdayDefects = allDefects.get("OpenY");
		priorities = new ArrayList<Priority>();
		tbd1 = new Priority();
		tbd1.setPriorityName("TBD");
		priority1 = new Priority();
		priority1.setPriorityName("P1");
		priority2 = new Priority();
		priority2.setPriorityName("P2");
		priority3 = new Priority();
		priority3.setPriorityName("P3");
		priority4 = new Priority();
		priority4.setPriorityName("P4");
		for(Defect defect:openYesterdayDefects) {
			if(defect.getPriority().equalsIgnoreCase("TBD")) {
				tbd1.setPriorityCount(tbd1.getPriorityCount()+1);
			}
			if(defect.getPriority().equalsIgnoreCase("P1")) {
				priority1.setPriorityCount(priority1.getPriorityCount()+1);
			}
			if(defect.getPriority().equalsIgnoreCase("P2")) {
				priority2.setPriorityCount(priority2.getPriorityCount()+1);
			}
			if(defect.getPriority().equalsIgnoreCase("P3")) {
				priority3.setPriorityCount(priority3.getPriorityCount()+1);
			}
			if(defect.getPriority().equalsIgnoreCase("P4")) {
				priority4.setPriorityCount(priority4.getPriorityCount()+1);
			}
		}
		
		arrayList = new ArrayList<Integer>();
		arrayList.add(tbd1.getPriorityCount());
		arrayList.add(priority1.getPriorityCount());
		arrayList.add(priority2.getPriorityCount());
		arrayList.add(priority3.getPriorityCount());
		arrayList.add(priority4.getPriorityCount());
		maximumCount = Collections.max(arrayList);
		if(maximumCount <= 0) {
			tbd1.setPxSize("0");
			priority1.setPxSize("0");
			priority2.setPxSize("0");
			priority3.setPxSize("0");
			priority4.setPxSize("0");
		} else {
			tbd1.setPxSize(Math.round((100*tbd1.getPriorityCount())/maximumCount)+"");
			priority1.setPxSize(Math.round((100*priority1.getPriorityCount())/maximumCount)+"");
			priority2.setPxSize(Math.round((100*priority2.getPriorityCount())/maximumCount)+"");
			priority3.setPxSize(Math.round((100*priority3.getPriorityCount())/maximumCount)+"");
			priority4.setPxSize(Math.round((100*priority4.getPriorityCount())/maximumCount)+"");
		}
		priorities.add(tbd1);
		priorities.add(priority1);
		priorities.add(priority2);
		priorities.add(priority3);
		priorities.add(priority4);
		dashboardForm.setOpenYesterdayDefects(openYesterdayDefects);
		dashboardForm.setOpenYesterdayPriorities(priorities);
		dashboardForm.setOpenYesterdayDefectCount(openYesterdayDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setOpenYesterdayP1AndP2Count(p1np2cnt);
		
		Project project = Util.getSelectedProject(dashboardForm.getProjectId(), configuration);
		dashboardForm.setProjectId(project.getProjectId());
		dashboardForm.setProjectName(project.getProjectKey());
	}
    
    public static String getProjectAttribute(Configuration configuration, String key, int tab) {
    	List<Project> projects = configuration.getProjects();
    	for(Project project:projects) {
    		if(tab == project.getTabIndex()) {
    			if(key.equals("release")) {
    				return project.getRelease();
    			} else {
    				return project.getProjectId();
    			}
    		}
    	}
    	return null;
    }
    
    private static String getDate(String day) {
    	Calendar cal = Calendar.getInstance();
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	if(day.equalsIgnoreCase("today")) {
    		;
    	} else {
    		cal.add(Calendar.DATE, -1);
    	}
    	return dateFormat.format(cal.getTime());
    }
    
    public static String readUserFile(String username){
    	Properties prop = new Properties();
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();           
    	InputStream stream = loader.getResourceAsStream("/users.properties");
    	try {
    		prop.load(stream);
			return prop.getProperty(username);
    	} catch (IOException e) {
			return null;
		}
    }
    
}
