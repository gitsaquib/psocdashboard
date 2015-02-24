/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Enumeration;
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
import com.pearson.dashboard.vo.Release;
import com.pearson.dashboard.vo.Tab;
import com.pearson.dashboard.vo.TestCase;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

/**
 *
 * @author Mohammed Saquib (mohammed.saquib)
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
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Open", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), false, configuration);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Submitted", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), false, configuration);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Fixed", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), false, configuration);    	
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Closed", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), false, configuration);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "ClosedY", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), true, configuration);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "OpenY", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), true, configuration);
    	restApi.close();
    	return allDefects;
    }

	private static void retrieveDefects(
			Map<String, List<Defect>> allDefects, RallyRestApi restApi,
			String projectId, String typeCategory, String releaseNum, String cutoffDate, boolean yesterdayDefects, Configuration configuration) throws IOException, ParseException {
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
			if(null != cutoffDate) {
				queryFilter = queryFilter.and(new QueryFilter("CreationDate", ">=", cutoffDate));
			}
    	}
    	defectRequest = new QueryRequest("defects");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("State", "Release", "Name", "FormattedID", "Platform", "Priority", "LastUpdateDate", "SubmittedBy", "Owner", "Project", "ClosedDate"));
    	defectRequest.setProject("/project/"+projectId);  
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(2000);
    	defectRequest.setOrder("FormattedID desc");
    	projectDefects = restApi.query(defectRequest);
    	defectsArray = projectDefects.getResults();
    	for(int i=0; i<defectsArray.size(); i++) {
            JsonElement elements =  defectsArray.get(i);
            JsonObject object = elements.getAsJsonObject();
            if(!object.get("Release").isJsonNull()) {
	            Defect defect =  new Defect();
	            defect.setDefectId(object.get("FormattedID").getAsString());

	            if(null != object.get("_ref")) {
	            	String defectRef = object.get("_ref").getAsString();
	            	if(null != defectRef) {
	            		String url = configuration.getRallyURL()+"#/"+projectId+"ud/detail/defect"+defectRef.substring(defectRef.lastIndexOf("/"));
	            		defect.setDefectUrl(url);
	            	}
	            }
	            
	            String strFormat1 = object.get("LastUpdateDate").getAsString().substring(0, 10);
	            DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd"); 
	            Date date = (Date) formatter1.parse(strFormat1);
	            DateFormat formatter2 = new SimpleDateFormat("MMM dd YY");
	            defect.setLastUpdateDate(formatter2.format(date));
	            
	            defect.setLastUpdateDateOriginal(object.get("LastUpdateDate").getAsString().substring(0, 10));
	            
	            if(null != object.get("Priority") && !object.get("Priority").getAsString().isEmpty() && !object.get("Priority").getAsString().startsWith("No")) {
	                defect.setPriority("P"+object.get("Priority").getAsString().charAt(0));
	            } else {
	                defect.setPriority("TBD");
	            }
	            defect.setState(object.get("State").getAsString());
	            JsonObject project = object.get("Project").getAsJsonObject();
	            defect.setProject(project.get("_refObjectName").getAsString().charAt(0)+"");
	            defect.setDefectDesc(object.get("Name").getAsString());
	            String platform = "Undefined";
	            if(null != object.get("c_Platform")) {
	            	if(object.get("c_Platform").getAsString().startsWith("iOS")) {
	            		platform = "Apple";
	            	} else if(object.get("c_Platform").getAsString().startsWith("Win")) {
	            		platform = "Windows";
	            	}
	            }
	            defect.setPlatform(platform);
	            defects.add(defect);
            }
        }
    	Collections.sort(defects);
    	allDefects.put(typeCategory, defects);
	}
    
    public static Tab getSelectedProject(int tabIndex, int subTabIndex, Configuration configuration) {
    	List<Tab> tabs = configuration.getTabs();
    	Tab selectedTab = new Tab();
    	for(Tab tab:tabs) {
    		if(tabIndex == tab.getTabIndex()) {
    			List<Tab> subTabs = tab.getSubTabs();
    			selectedTab = tab;
    			if(null != subTabs) {
    				for(Tab subTab:subTabs) {
    					if(subTab.getTabIndex() == subTabIndex) {
    						selectedTab = subTab;
    					}
    				}
    			}
    		}
    	}
    	return selectedTab;
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
    
    public static Configuration readConfigFile() throws FileNotFoundException{
    	Properties prop = new Properties();
    	
    	File file = new File(System.getProperty("user.home"), "config/config.properties");
    	InputStream stream = new FileInputStream(file);
    	
    	try {
    		Configuration configuration = new Configuration();
			prop.load(stream);
			configuration.setRallyURL(prop.getProperty("rallyURL"));
			configuration.setRallyUser(prop.getProperty("rallyUser"));
			configuration.setRallyPassword(prop.getProperty("rallyPassword"));
			
			file = new File(System.getProperty("user.home"), "config/tab.properties");
	    	InputStream streamProject = new FileInputStream(file);
			
			Properties tabProperties = new Properties();
			tabProperties.load(streamProject);
			
			List<Tab> tabs = new ArrayList<Tab>();  
			for(Enumeration<String> en = (Enumeration<String>) tabProperties.propertyNames();en.hasMoreElements();) {
				String key = (String) en.nextElement();
				String params = tabProperties.getProperty(key);
				if(null != params) {
					String param[] = params.split(":");
					Tab tab = new Tab();
					tab.setTabIndex(Integer.parseInt(param[0]));
					tab.setTabDisplayName(param[1]);
					tab.setTabUniqueId(param[2]);
					tab.setRelease(param[3]);
					if(null != param[4] && !"null".equals(param[4])) {
						tab.setCutoffDate(param[4]);
					}
					if(null != param[5] && "true".equals(param[5])) {
						tab.setRegressionData(true);
					} else {
						tab.setRegressionData(false);
					}
					tab.setTabType("Parent");
					tab.setSubTabs(getSubTabs(param[2]));
					tabs.add(tab);
				}
			}
			Collections.sort(tabs);
			configuration.setTabs(tabs);			
			return configuration;
		} catch (IOException e) {
			return null;
		}
    }
    
    public static void populateDefectData(DashboardForm dashboardForm, Configuration configuration) throws Exception {
		Map<String, List<Defect>> allDefects = null;
		
		if(dashboardForm.getTabName().startsWith("Older") || dashboardForm.getTabName().startsWith("Authering")) {
			allDefects = Util.getMultiProjectDataFromRally(dashboardForm, configuration);
		} else {
			allDefects = Util.getDataFromRally(dashboardForm, configuration);
		}
		
		int winCount = 0;
		int iOSCount = 0;
		int undefined = 0;
		
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
		
		for(Defect defect:openDefects) {
			if(defect.getPlatform().equalsIgnoreCase("Apple")) {
		    	iOSCount++;
		    } else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
		    	winCount++;
		    } else {
		    	undefined++;
		    }
		}
		
		dashboardForm.setOpenDefects(openDefects);
		dashboardForm.setOpenPriorities(priorities);
		dashboardForm.setOpenDefectCount(openDefects.size());
		int p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setOpenP1AndP2Count(p1np2cnt);
		dashboardForm.setOpenMsg("iOS: "+iOSCount+", Windows: "+winCount+" and "+undefined+" are undefined");
		
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
		
		iOSCount = 0;
		winCount = 0;
		undefined = 0;
		for(Defect defect:submittedDefects) {
			if(defect.getPlatform().equalsIgnoreCase("Apple")) {
		    	iOSCount++;
		    } else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
		    	winCount++;
		    } else {
		    	undefined++;
		    }
		}
		
		dashboardForm.setSubmittedDefects(submittedDefects);
		dashboardForm.setSubmittedPriorities(priorities);
		dashboardForm.setSubmittedDefectCount(submittedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setSubmittedP1AndP2Count(p1np2cnt);
		dashboardForm.setSubmittedMsg("iOS: "+iOSCount+", Windows: "+winCount+" and "+undefined+" are undefined");
		
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
		
		iOSCount = 0;
		winCount = 0;
		undefined = 0;
		for(Defect defect:fixedDefects) {
			if(defect.getPlatform().equalsIgnoreCase("Apple")) {
				iOSCount++;
			} else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
				winCount++;
			} else {
				undefined++;
			}
		}
		
		dashboardForm.setFixedDefects(fixedDefects);
		dashboardForm.setFixedPriorities(priorities);
		dashboardForm.setFixedDefectCount(fixedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setFixedMsg("iOS: "+iOSCount+", Windows: "+winCount+" and "+undefined+" are undefined");
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
		
		iOSCount = 0;
		winCount = 0;
		undefined = 0;
		for(Defect defect:closedDefects) {
			if(defect.getPlatform().equalsIgnoreCase("Apple")) {
				iOSCount++;
			} else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
				winCount++;
			} else {
				undefined++;
			}
		}
		
		dashboardForm.setClosedDefects(closedDefects);
		dashboardForm.setClosedPriorities(priorities);
		dashboardForm.setClosedDefectCount(closedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setClosedMsg("iOS: "+iOSCount+", Windows: "+winCount+" and "+undefined+" are undefined");
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
		if(null != closedYesterdayDefects) {
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
		
		iOSCount = 0;
		winCount = 0;
		undefined = 0;
		if(null != closedYesterdayDefects) {
			for(Defect defect:closedYesterdayDefects) {
				if(defect.getPlatform().equalsIgnoreCase("Apple")) {
					iOSCount++;
				} else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
					winCount++;
				} else {
					undefined++;
				}
			}
		}
		dashboardForm.setClosedYesterdayDefects(closedYesterdayDefects);
		dashboardForm.setClosedYesterdayPriorities(priorities);
		dashboardForm.setClosedYesterdayDefectCount(null == closedYesterdayDefects ? 0 : closedYesterdayDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setClosedYMsg("iOS: "+iOSCount+", Windows: "+winCount+" and "+undefined+" are undefined");
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
		if(null != openYesterdayDefects) {
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
		
		iOSCount = 0;
		winCount = 0;
		undefined = 0;
		if(null != openYesterdayDefects) {
			for(Defect defect:openYesterdayDefects) {
				if(defect.getPlatform().equalsIgnoreCase("Apple")) {
					iOSCount++;
				} else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
					winCount++;
				} else {
					undefined++;
				}
			}
		}
		dashboardForm.setOpenYesterdayDefects(openYesterdayDefects);
		dashboardForm.setOpenYesterdayPriorities(priorities);
		dashboardForm.setOpenYesterdayDefectCount(null == openYesterdayDefects ? 0 : openYesterdayDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setOpenYMsg("iOS: "+iOSCount+", Windows: "+winCount+" and "+undefined+" are undefined");
		dashboardForm.setOpenYesterdayP1AndP2Count(p1np2cnt);
		
		Tab tab = Util.getSelectedProject(Integer.parseInt(dashboardForm.getTabIndex()), Integer.parseInt(dashboardForm.getSubProject()), configuration);
		dashboardForm.setProjectId(tab.getTabUniqueId());
		dashboardForm.setProjectName(tab.getTabDisplayName());
		dashboardForm.setRegressionData(tab.isRegressionData());
	}
    
    public static String getTabAttribute(Configuration configuration, String key, int tabInt, int subTabInt) {
    	List<Tab> tabs = configuration.getTabs();
    	Tab selectedTab = new Tab();
    	for(Tab tab:tabs) {
    		if(tabInt == tab.getTabIndex()) {
    			selectedTab = tab;
    			List<Tab> subTabs = tab.getSubTabs();
    			if(null != subTabs) {
    				for(Tab subTab:subTabs) {
    					if(subTab.getTabIndex() == subTabInt) {
    						selectedTab = subTab;
    					}
    				}
    			}
    		}
    	}
    	if(key.equals("release")) {
			return selectedTab.getRelease();
		} else if(key.equals("cutoffdate")) {
			return selectedTab.getCutoffDate();
		} else if(key.equals("tabname")) {
			return selectedTab.getTabDisplayName();
		} else {
			return selectedTab.getTabUniqueId();
		}
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
    
    public static String readUserFile(String username) throws FileNotFoundException{
    	Properties prop = new Properties();
    	File file = new File(System.getProperty("user.home"), "config/users.properties");
    	InputStream stream = new FileInputStream(file);
    	try {
    		prop.load(stream);
			return prop.getProperty(username);
    	} catch (IOException e) {
			return null;
		}
    }
    
    public static Map<String, List<Defect>> getMultiProjectDataFromRally(DashboardForm dashboard, Configuration configuration) throws Exception {
    	Map<String, List<Defect>> allDefects = new HashMap<String, List<Defect>>();
    	RallyRestApi  restApi = loginRally(configuration);
    	
    	if(dashboard.getTabName().startsWith("Authering")) {
    		
    		//Retrieve V team data
	    	String projectId = getTabAttribute(configuration, "project", 4, 4);
	    	String cutoffDate = getTabAttribute(configuration, "cutoffdate", 4, 4);    	
	    	retrieveDefects(allDefects, restApi, projectId, "Open", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration);
	    	retrieveDefects(allDefects, restApi, projectId, "Submitted", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration);
	    	retrieveDefects(allDefects, restApi, projectId, "Fixed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration);    	
	    	retrieveDefects(allDefects, restApi, projectId, "Closed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration);
	    	
	    	//Retrieve A team data
	    	projectId = getTabAttribute(configuration, "project", 3, 3);
	    	cutoffDate = getTabAttribute(configuration, "cutoffdate", 3, 3);    
	    	retrieveDefects(allDefects, restApi, projectId, "Open", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration);
	    	retrieveDefects(allDefects, restApi, projectId, "Submitted", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration);
	    	retrieveDefects(allDefects, restApi, projectId, "Fixed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration);  	
	    	retrieveDefects(allDefects, restApi, projectId, "Closed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration);
	    	
    	} else {
	    	//Retrieve 2-12 old data
	    	String projectId = getTabAttribute(configuration, "project", 0, 0);
	    	String cutoffDate = getTabAttribute(configuration, "cutoffdate", 0, 0);    	
	    	retrieveDefects(allDefects, restApi, projectId, "Open", dashboard.getSelectedRelease(), cutoffDate, "<", configuration);
	    	retrieveDefects(allDefects, restApi, projectId, "Submitted", dashboard.getSelectedRelease(), cutoffDate, "<", configuration);
	    	retrieveDefects(allDefects, restApi, projectId, "Fixed", dashboard.getSelectedRelease(), cutoffDate, "<", configuration);    	
	    	retrieveDefects(allDefects, restApi, projectId, "Closed", dashboard.getSelectedRelease(), cutoffDate, "<", configuration);
	    	
	    	//Retrieve K1 old data
	    	projectId = getTabAttribute(configuration, "project", 1, 1);
	    	cutoffDate = getTabAttribute(configuration, "cutoffdate", 1, 1);    	
	    	retrieveDefects(allDefects, restApi, projectId, "Open", dashboard.getSelectedRelease(), cutoffDate, "<", configuration);
	    	retrieveDefects(allDefects, restApi, projectId, "Submitted", dashboard.getSelectedRelease(), cutoffDate, "<", configuration);
	    	retrieveDefects(allDefects, restApi, projectId, "Fixed", dashboard.getSelectedRelease(), cutoffDate, "<", configuration);    	
	    	retrieveDefects(allDefects, restApi, projectId, "Closed", dashboard.getSelectedRelease(), cutoffDate, "<", configuration);
    	}	
    	restApi.close();
    	return allDefects;
    }

	private static void retrieveDefects(
			Map<String, List<Defect>> allDefects, RallyRestApi restApi,
			String projectId, String typeCategory, String releaseNum, String cutoffDate, String comparisonOperator, Configuration configuration) throws IOException, ParseException {
		List<Defect> defects;
		QueryFilter queryFilter;
		QueryRequest defectRequest;
		QueryResponse projectDefects;
		JsonArray defectsArray;
		defects = new ArrayList<Defect>();
    	queryFilter = new QueryFilter("State", "=", typeCategory).and(new QueryFilter("Release.Name", "=", releaseNum));
		if(null != cutoffDate) {
			queryFilter = queryFilter.and(new QueryFilter("CreationDate", comparisonOperator, cutoffDate));
		}
    	defectRequest = new QueryRequest("defects");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("State", "Release", "Name", "FormattedID", "Platform", "Priority", "LastUpdateDate", "SubmittedBy", "Owner", "Project", "ClosedDate"));
    	defectRequest.setProject("/project/"+projectId);  
    	defectRequest.setScopedDown(true);
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(2000);
    	defectRequest.setOrder("FormattedID desc");
    	projectDefects = restApi.query(defectRequest);
    	defectsArray = projectDefects.getResults();
    	for(int i=0; i<defectsArray.size(); i++) {
            JsonElement elements =  defectsArray.get(i);
            JsonObject object = elements.getAsJsonObject();
            if(!object.get("Release").isJsonNull()) {
	            Defect defect =  new Defect();
	            defect.setDefectId(object.get("FormattedID").getAsString());
	            
	            if(null != object.get("_ref")) {
	            	String defectRef = object.get("_ref").getAsString();
	            	if(null != defectRef) {
	            		String url = configuration.getRallyURL()+"#/"+projectId+"ud/detail/defect"+defectRef.substring(defectRef.lastIndexOf("/"));
	            		defect.setDefectUrl(url);
	            	}
	            }
	            
	            String strFormat1 = object.get("LastUpdateDate").getAsString().substring(0, 10);
	            DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd"); 
	            Date date = (Date) formatter1.parse(strFormat1);
	            DateFormat formatter2 = new SimpleDateFormat("MMM dd YY");
	            defect.setLastUpdateDate(formatter2.format(date));
	            
	            defect.setLastUpdateDateOriginal(object.get("LastUpdateDate").getAsString().substring(0, 10));
	            
	            if(null != object.get("Priority") && !object.get("Priority").toString().isEmpty() && !object.get("Priority").getAsString().startsWith("No")) {
	                defect.setPriority("P"+object.get("Priority").getAsString().charAt(0));
	            } else {
	                defect.setPriority("TBD");
	            }
	            defect.setState(object.get("State").getAsString());
	            JsonObject project = object.get("Project").getAsJsonObject();
	            defect.setProject(project.get("_refObjectName").getAsString().charAt(0)+"");
	            defect.setDefectDesc(object.get("Name").getAsString());
	            String platform = "Undefined";
	            if(null != object.get("c_Platform")) {
	            	if(object.get("c_Platform").getAsString().startsWith("iOS")) {
	            		platform = "Apple";
	            	} else if(object.get("c_Platform").getAsString().startsWith("Win")) {
	            		platform = "Windows";
	            	}
	            }
	            defect.setPlatform(platform);
	            defects.add(defect);
            }
        }
    	List<Defect> olderDefects = allDefects.get(typeCategory);
    	if(null == olderDefects) {
    		olderDefects = new ArrayList<Defect>();
    	}
    	olderDefects.addAll(defects);
    	Collections.sort(olderDefects);
    	allDefects.put(typeCategory, olderDefects);
	}
    
	public static void retrieveTestCases(DashboardForm dashboardForm, Configuration configuration, String cutoffDate)
			throws IOException, URISyntaxException {
		RallyRestApi  restApi = loginRally(configuration);
		List<TestCase> testCases = new ArrayList<TestCase>();
		QueryFilter queryFilter = new QueryFilter("CreationDate", ">=", cutoffDate).and(new QueryFilter("Type", "=", "Regression"));
    	QueryRequest defectRequest = new QueryRequest("testcases");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("FormattedID", "LastVerdict"));
    	defectRequest.setProject("/project/"+dashboardForm.getProjectId()); 
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(10000);
    	defectRequest.setOrder("FormattedID desc");
    	QueryResponse projectDefects = restApi.query(defectRequest);
    	JsonArray defectsArray = projectDefects.getResults();
    
    	for(int i=0; i<defectsArray.size(); i++) {
    		TestCase testCase = new TestCase();
    		JsonElement elements =  defectsArray.get(i);
            JsonObject object = elements.getAsJsonObject();
            testCase.setTestCaseId(object.get("FormattedID").getAsString());
            testCase.setLastVerdict(object.get("LastVerdict")==null || object.get("LastVerdict").isJsonNull() ?"":object.get("LastVerdict").getAsString());
            testCases.add(testCase);
    	}
    	dashboardForm.setTestCases(testCases);
    	restApi.close();
    	
    	List<Priority> priorities = new ArrayList<Priority>();
    	Priority priority0 = new Priority();
    	priority0.setPriorityName("Pass");
		Priority priority1 = new Priority();
		priority1.setPriorityName("Blocked");
		Priority priority2 = new Priority();
		priority2.setPriorityName("Error");
		Priority priority3 = new Priority();
		priority3.setPriorityName("Fail");
		Priority priority4 = new Priority();
		priority4.setPriorityName("Inconclusive");
		Priority priority5 = new Priority();
		priority5.setPriorityName("NotAttempted");
		if(null != testCases) {
			for(TestCase testCase:testCases) {
			    if(testCase.getLastVerdict().equalsIgnoreCase("Pass")) {
			    	priority0.setPriorityCount(priority0.getPriorityCount()+1);
			    }
			    if(testCase.getLastVerdict().equalsIgnoreCase("Blocked")) {
			    	priority1.setPriorityCount(priority1.getPriorityCount()+1);
			    }
			    if(testCase.getLastVerdict().equalsIgnoreCase("Error")) {
			    	priority2.setPriorityCount(priority2.getPriorityCount()+1);
			    }
			    if(testCase.getLastVerdict().equalsIgnoreCase("Fail")) {
			    	priority3.setPriorityCount(priority3.getPriorityCount()+1);
			    }
			    if(testCase.getLastVerdict().equalsIgnoreCase("Inconclusive")) {
			    	priority4.setPriorityCount(priority4.getPriorityCount()+1);
			    }
			    if(testCase.getLastVerdict().equalsIgnoreCase("")) {
			    	priority5.setPriorityCount(priority5.getPriorityCount()+1);
			    }
			}
		}
		List<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(priority0.getPriorityCount());
		arrayList.add(priority1.getPriorityCount());
		arrayList.add(priority2.getPriorityCount());
		arrayList.add(priority3.getPriorityCount());
		arrayList.add(priority4.getPriorityCount());
		arrayList.add(priority5.getPriorityCount());
		Integer maximumCount = Collections.max(arrayList);
		if(maximumCount <= 0) {
			priority0.setPxSize("0");
		    priority1.setPxSize("0");
		    priority2.setPxSize("0");
		    priority3.setPxSize("0");
		    priority4.setPxSize("0");
		    priority5.setPxSize("0");
		} else {
			priority0.setPxSize(Math.round((100*priority0.getPriorityCount())/maximumCount)+"");
		    priority1.setPxSize(Math.round((100*priority1.getPriorityCount())/maximumCount)+"");
		    priority2.setPxSize(Math.round((100*priority2.getPriorityCount())/maximumCount)+"");
		    priority3.setPxSize(Math.round((100*priority3.getPriorityCount())/maximumCount)+"");
		    priority4.setPxSize(Math.round((100*priority4.getPriorityCount())/maximumCount)+"");
		    priority5.setPxSize(Math.round((100*priority5.getPriorityCount())/maximumCount)+"");
		}
		priorities.add(priority0);
		priorities.add(priority1);
		priorities.add(priority2);
		priorities.add(priority3);
		priorities.add(priority4);
		priorities.add(priority5);
		
		dashboardForm.setTestCasesCount(testCases.size());
		dashboardForm.setTestCasesPriorities(priorities);
	}
	
	private static List<Tab> getSubTabs(String tabUniqueId) throws IOException {
		File file = new File(System.getProperty("user.home"), "config/subtab.properties");
    	InputStream streamSubTab = new FileInputStream(file);
		
		Properties subTabProperties = new Properties();
		subTabProperties.load(streamSubTab);
		
		List<Tab> subTabs = new ArrayList<Tab>();  
		for(Enumeration<String> enSub = (Enumeration<String>) subTabProperties.propertyNames();enSub.hasMoreElements();) {
			String keySub = (String) enSub.nextElement();
			String paramsSub = subTabProperties.getProperty(keySub);
			if(null != paramsSub) {
				String paramSub[] = paramsSub.split(":");
				if(null != paramSub[6] && paramSub[6].equalsIgnoreCase(tabUniqueId)) {
					Tab subTtab = new Tab();
					subTtab.setTabIndex(Integer.parseInt(paramSub[0]));
					subTtab.setTabDisplayName(paramSub[1]);
					subTtab.setTabUniqueId(paramSub[2]);
					subTtab.setRelease(paramSub[3]);
					if(null != paramSub[4] && !"null".equals(paramSub[4])) {
						subTtab.setCutoffDate(paramSub[4]);
					}
					if(null != paramSub[5] && !"true".equals(paramSub[5])) {
						subTtab.setRegressionData(true);
					} else {
						subTtab.setRegressionData(false);
					}
					subTtab.setTabType("Child");
					subTabs.add(subTtab);
				}
			}
		}
		Collections.sort(subTabs);
		return subTabs;
	}
}
