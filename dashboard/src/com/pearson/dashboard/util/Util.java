/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import org.apache.http.conn.HttpHostConnectException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pearson.dashboard.form.DashboardForm;
import com.pearson.dashboard.vo.Configuration;
import com.pearson.dashboard.vo.Defect;
import com.pearson.dashboard.vo.Priority;
import com.pearson.dashboard.vo.RegressionData;
import com.pearson.dashboard.vo.Release;
import com.pearson.dashboard.vo.Tab;
import com.pearson.dashboard.vo.TestCase;
import com.pearson.dashboard.vo.TestResult;
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
    	List<Release> releases = retrieveRelease(restApi, configuration);
    	restApi.close();
    	return releases;
    }
    
    public static Map<String, List<Defect>> getDataFromRally(DashboardForm dashboard, Configuration configuration) throws Exception {
    	Map<String, List<Defect>> allDefects = new HashMap<String, List<Defect>>();
    	RallyRestApi  restApi = loginRally(configuration);
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Open", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), false, configuration, dashboard.getOperatingSystem(), dashboard.getTag());
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Submitted", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), false, configuration, dashboard.getOperatingSystem(), dashboard.getTag());
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Fixed", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), false, configuration, dashboard.getOperatingSystem(), dashboard.getTag());    	
    	//retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "Closed", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), false, configuration, dashboard.getOperatingSystem(), dashboard.getTag());
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "ClosedY", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), true, configuration, dashboard.getOperatingSystem(), dashboard.getTag());
    	retrieveDefects(allDefects, restApi, dashboard.getProjectId(), "OpenY", dashboard.getSelectedRelease(), dashboard.getCutoffDate(), true, configuration, dashboard.getOperatingSystem(), dashboard.getTag());
    	restApi.close();
    	return allDefects;
    }

	private static void retrieveDefects(
			Map<String, List<Defect>> allDefects, RallyRestApi restApi,
			String projectId, String typeCategory, String releaseNum, String cutoffDate, boolean yesterdayDefects, Configuration configuration, 
			String operatingSystem, String tag) throws IOException, ParseException {
		List<Defect> defects;
		QueryFilter queryFilter;
		QueryRequest defectRequest;
		QueryResponse projectDefects;
		JsonArray defectsArray;
		defects = new ArrayList<Defect>();
		
		QueryFilter releaseQueryFilter = null;
		if(null != releaseNum) {
			String releases[] =  releaseNum.split(",");
			releaseQueryFilter = new QueryFilter("Release.Name", "=", releases[0]); 
			for(int r=1; r<releases.length; r++) {
				releaseQueryFilter = releaseQueryFilter.or(new QueryFilter("Release.Name", "=", releases[r]));
			}
		}
    	if(yesterdayDefects) {
    		String today = getDate("today");
    		String yesterday = getDate("yesterday");
    		if(typeCategory.equalsIgnoreCase("ClosedY")) {
    			if(null != releaseQueryFilter) {
    				queryFilter = new QueryFilter("State", "=", "Closed").and(releaseQueryFilter).and(new QueryFilter("ClosedDate", "<", today)).and(new QueryFilter("ClosedDate", ">=", yesterday));	
    			} else {
    				queryFilter = new QueryFilter("State", "=", "Closed").and(new QueryFilter("ClosedDate", "<", today)).and(new QueryFilter("ClosedDate", ">=", yesterday));
    			}
    		} else {
    			if(null != releaseQueryFilter) {
    				queryFilter = releaseQueryFilter.and(new QueryFilter("CreationDate", "<", today)).and(new QueryFilter("CreationDate", ">=", yesterday));
    			} else {
    				queryFilter = new QueryFilter("CreationDate", "<", today).and(new QueryFilter("CreationDate", ">=", yesterday));
    			}
    		}
    	} else {
    		if(null != releaseQueryFilter) {
    			queryFilter = new QueryFilter("State", "=", typeCategory).and(releaseQueryFilter);
    		} else {
    			queryFilter = new QueryFilter("State", "=", typeCategory);
    		}
			if(null != cutoffDate) {
				queryFilter = queryFilter.and(new QueryFilter("CreationDate", ">=", cutoffDate));
			}
    	}
    	
    	
    	defectRequest = new QueryRequest("defects");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("State", "Release", "Tags", "Name", "FormattedID", "Platform", "Priority", "LastUpdateDate", "SubmittedBy", "Owner", "Project", "ClosedDate"));
    	defectRequest.setProject("/project/"+projectId);  
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(4000);
    	defectRequest.setOrder("FormattedID desc");
    	boolean dataNotReceived = true;
    	while(dataNotReceived){
	    	try {
	    		projectDefects = restApi.query(defectRequest);
	    		dataNotReceived = false;
	    		defectsArray = projectDefects.getResults();
	        	for(int i=0; i<defectsArray.size(); i++) {
	                JsonElement elements =  defectsArray.get(i);
	                JsonObject object = elements.getAsJsonObject();
	                boolean isTag = true;
	                if(null != tag) {
	                	isTag = false;
	                }
	                
	                if(null != tag && null != object.get("Tags") && !object.get("Tags").isJsonNull()) {
	                	JsonObject jsonObject = object.get("Tags").getAsJsonObject();
	                	int numberOfTestCases = jsonObject.get("_tagsNameArray").getAsJsonArray().size();
	                    if(numberOfTestCases>0){
	                          for (int j=0;j<numberOfTestCases;j++){
	    	            	  	JsonObject jsonObj = jsonObject.get("_tagsNameArray").getAsJsonArray().get(j).getAsJsonObject();
	    	            	  	if(jsonObj.get("Name").getAsString().equals(tag)){
	    	            	  		isTag = true;
	    	            	  	}
	                         }
	                    }
	                }
	                
	                if(isTag) {
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
	    	            defect.setProject(project.get("_refObjectName").getAsString());
	    	            defect.setDefectDesc(object.get("Name").getAsString());
	    	            String platform = "iOS";
	    	            if(null != object.get("c_Platform")) {
	    	            	if(object.get("c_Platform").getAsString().contains("iOS") && object.get("c_Platform").getAsString().contains("Win")) {
	    	            		platform  = "iOS";
	    	            	} else if(object.get("c_Platform").getAsString().contains("iOS")) {
	    	            		platform = "iOS";
	    	            	} else if(object.get("c_Platform").getAsString().contains("Win")) {
	    	            		platform = "Windows";
	    	            	}
	    	            }
	    	            defect.setPlatform(platform);
	    	            if(platform.equalsIgnoreCase(operatingSystem) || operatingSystem.equalsIgnoreCase("All")) {
	    	            	defects.add(defect);	
	    	            }
	                } else {
	                	System.out.println("aplha");
	                }
	            }
	        	Collections.sort(defects);
	        	allDefects.put(typeCategory, defects);
	    	} catch(HttpHostConnectException connectException) {
	    		if(restApi != null) {
	    			restApi.close();
	    		}
	    		try {
					restApi = loginRally(configuration);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
	    	}	
    	}
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
    
    private static List<Release> retrieveRelease(RallyRestApi restApi, Configuration configuration) throws Exception{
    	List<Release> releases = new ArrayList<Release>();
    	QueryRequest query = new QueryRequest("Release");
    	QueryFilter queryFilter = new QueryFilter("State", "=", "Active").or(new QueryFilter("State", "=", "Planning"));
    	query.setQueryFilter(queryFilter);
    	QueryResponse projectDefects = null;
    	boolean dataNotReceived = true;
    	while(dataNotReceived){
	    	try {
	    		projectDefects = restApi.query(query);
	    		dataNotReceived = false;
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
	    	} catch(HttpHostConnectException connectException) {
	    		if(restApi != null) {
	    			restApi.close();
	    		}
	    		try {
					restApi = loginRally(configuration);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
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
			configuration.setUseRegressionInputFile(prop.getProperty("useRegressionInputFile"));
			
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
					if(param[3].equals("null")) {
						tab.setRelease(null);
					} else {
						tab.setRelease(param[3]);
					}
					if(null != param[4] && !"null".equals(param[4])) {
						tab.setCutoffDate(param[4]);
					}
					if(null != param[5] && "true".equals(param[5])) {
						tab.setRegressionData(true);
					} else {
						tab.setRegressionData(false);
					}
					tab.setTabType("Parent");
					tab.setInformation(param[6]);
					if(param[7].equals("null")) {
						tab.setTag(null);
					} else {
						tab.setTag(param[7]);
					}
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
		
		
		if(dashboardForm.getTabName().startsWith("Other") || dashboardForm.getTabName().startsWith("Author")) {
			allDefects = Util.getMultiProjectDataFromRally(dashboardForm, configuration);
		} else {
			allDefects = Util.getDataFromRally(dashboardForm, configuration);
		}
		
		int winCount = 0;
		int iOSCount = 0;
		
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
			if(defect.getPlatform().equalsIgnoreCase("iOS")) {
		    	iOSCount++;
		    } else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
		    	winCount++;
		    }
		}
		
		dashboardForm.setOpenDefects(openDefects);
		dashboardForm.setOpenPriorities(priorities);
		dashboardForm.setOpenDefectCount(openDefects.size());
		int p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setOpenP1AndP2Count(p1np2cnt);
		dashboardForm.setOpenMsg("iOS: "+iOSCount+", Windows: "+winCount);
		
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
		for(Defect defect:submittedDefects) {
			if(defect.getPlatform().equalsIgnoreCase("iOS")) {
		    	iOSCount++;
		    } else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
		    	winCount++;
		    }
		}
		
		dashboardForm.setSubmittedDefects(submittedDefects);
		dashboardForm.setSubmittedPriorities(priorities);
		dashboardForm.setSubmittedDefectCount(submittedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setSubmittedP1AndP2Count(p1np2cnt);
		dashboardForm.setSubmittedMsg("iOS: "+iOSCount+", Windows: "+winCount);
		
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
		for(Defect defect:fixedDefects) {
			if(defect.getPlatform().equalsIgnoreCase("iOS")) {
				iOSCount++;
			} else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
				winCount++;
			}
		}
		
		dashboardForm.setFixedDefects(fixedDefects);
		dashboardForm.setFixedPriorities(priorities);
		dashboardForm.setFixedDefectCount(fixedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setFixedMsg("iOS: "+iOSCount+", Windows: "+winCount);
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
		if(null == closedDefects){
			closedDefects = new ArrayList<Defect>();
		}
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
		
		for(Defect defect:closedDefects) {
			if(defect.getPlatform().equalsIgnoreCase("iOS")) {
				iOSCount++;
			} else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
				winCount++;
			}
		}
		
		dashboardForm.setClosedDefects(closedDefects);
		dashboardForm.setClosedPriorities(priorities);
		dashboardForm.setClosedDefectCount(closedDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setClosedMsg("iOS: "+iOSCount+", Windows: "+winCount);
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
		if(null != closedYesterdayDefects) {
			for(Defect defect:closedYesterdayDefects) {
				if(defect.getPlatform().equalsIgnoreCase("iOS")) {
					iOSCount++;
				} else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
					winCount++;
				}
			}
		}
		dashboardForm.setClosedYesterdayDefects(closedYesterdayDefects);
		dashboardForm.setClosedYesterdayPriorities(priorities);
		dashboardForm.setClosedYesterdayDefectCount(null == closedYesterdayDefects ? 0 : closedYesterdayDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setClosedYMsg("iOS: "+iOSCount+", Windows: "+winCount);
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
		if(null != openYesterdayDefects) {
			for(Defect defect:openYesterdayDefects) {
				if(defect.getPlatform().equalsIgnoreCase("iOS")) {
					iOSCount++;
				} else if(defect.getPlatform().equalsIgnoreCase("Windows")) {
					winCount++;
				}
			}
		}
		dashboardForm.setOpenYesterdayDefects(openYesterdayDefects);
		dashboardForm.setOpenYesterdayPriorities(priorities);
		dashboardForm.setOpenYesterdayDefectCount(null == openYesterdayDefects ? 0 : openYesterdayDefects.size());
		p1np2cnt = priority1.getPriorityCount() + priority2.getPriorityCount();
		dashboardForm.setOpenYMsg("iOS: "+iOSCount+", Windows: "+winCount);
		dashboardForm.setOpenYesterdayP1AndP2Count(p1np2cnt);
		
		Tab tab = Util.getSelectedProject(Integer.parseInt(dashboardForm.getTabIndex()), Integer.parseInt(dashboardForm.getSubProject()), configuration);
		dashboardForm.setProjectId(tab.getTabUniqueId());
		dashboardForm.setProjectName(tab.getTabDisplayName());
		dashboardForm.setRegressionData(tab.isRegressionData());
		dashboardForm.setFilterInfo(tab.getInformation());
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
		} else if(key.equals("tag")) {
			return selectedTab.getTag();
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
    	
    	if(dashboard.getTabName().startsWith("Other")) {
    		
	    	String projectId = getTabAttribute(configuration, "project", 6, 25);
	    	String cutoffDate = getTabAttribute(configuration, "cutoffdate", 6, 25);    	
	    	retrieveDefects(allDefects, restApi, projectId, "Open", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	retrieveDefects(allDefects, restApi, projectId, "Submitted", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	retrieveDefects(allDefects, restApi, projectId, "Fixed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());    	
	    	//retrieveDefects(allDefects, restApi, projectId, "Closed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	
	    	projectId = getTabAttribute(configuration, "project", 6, 26);
	    	cutoffDate = getTabAttribute(configuration, "cutoffdate", 6, 26);   
	    	retrieveDefects(allDefects, restApi, projectId, "Open", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	retrieveDefects(allDefects, restApi, projectId, "Submitted", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	retrieveDefects(allDefects, restApi, projectId, "Fixed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());  	
	    	//retrieveDefects(allDefects, restApi, projectId, "Closed", dashboard.getSelectedRelease(), null, ">=", configuration, dashboard.getOperatingSystem());
    	}	
    	if(dashboard.getTabName().startsWith("Author")) {
    		
	    	String projectId = getTabAttribute(configuration, "project", 3, 15);
	    	String cutoffDate = getTabAttribute(configuration, "cutoffdate", 3, 15);    	
	    	retrieveDefects(allDefects, restApi, projectId, "Open", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	retrieveDefects(allDefects, restApi, projectId, "Submitted", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	retrieveDefects(allDefects, restApi, projectId, "Fixed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());    	
	    	//retrieveDefects(allDefects, restApi, projectId, "Closed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	
	    	projectId = getTabAttribute(configuration, "project", 3, 18);
	    	cutoffDate = getTabAttribute(configuration, "cutoffdate", 3, 18);   
	    	retrieveDefects(allDefects, restApi, projectId, "Open", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	retrieveDefects(allDefects, restApi, projectId, "Submitted", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());
	    	retrieveDefects(allDefects, restApi, projectId, "Fixed", dashboard.getSelectedRelease(), cutoffDate, ">=", configuration, dashboard.getOperatingSystem());  	
	    	//retrieveDefects(allDefects, restApi, projectId, "Closed", dashboard.getSelectedRelease(), null, ">=", configuration, dashboard.getOperatingSystem());
    	}	
    	restApi.close();
    	return allDefects;
    }

	private static void retrieveDefects(
			Map<String, List<Defect>> allDefects, RallyRestApi restApi,
			String projectId, String typeCategory, String releaseNum, String cutoffDate, String comparisonOperator, Configuration configuration, String operatingSystem) throws IOException, ParseException {
		List<Defect> defects;
		QueryFilter queryFilter;
		QueryRequest defectRequest;
		QueryResponse projectDefects;
		JsonArray defectsArray;
		defects = new ArrayList<Defect>();
		
		QueryFilter releaseQueryFilter  = null;
		if(null != releaseNum) {
			String releases[] =  releaseNum.split(",");
			releaseQueryFilter = new QueryFilter("Release.Name", "=", releases[0]); 
			for(int r=1; r<releases.length; r++) {
				releaseQueryFilter = releaseQueryFilter.or(new QueryFilter("Release.Name", "=", releases[r]));
			}
		}
		if(null != releaseQueryFilter) {
			queryFilter = new QueryFilter("State", "=", typeCategory).and(releaseQueryFilter);
		} else {
			queryFilter = new QueryFilter("State", "=", typeCategory);
		}
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
    	boolean dataNotReceived = true;
    	while(dataNotReceived){
	    	try {
		    	projectDefects = restApi.query(defectRequest);
		    	dataNotReceived = false;
		    	defectsArray = projectDefects.getResults();
		    	for(int i=0; i<defectsArray.size(); i++) {
		            JsonElement elements =  defectsArray.get(i);
		            JsonObject object = elements.getAsJsonObject();
		            //if(!object.get("Release").isJsonNull()) 
		            {
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
			            defect.setProject(project.get("_refObjectName").getAsString());
			            defect.setDefectDesc(object.get("Name").getAsString());
			            String platform = "iOS";
			            if(null != object.get("c_Platform")) {
			            	if(object.get("c_Platform").getAsString().contains("iOS") && object.get("c_Platform").getAsString().contains("Win")) {
			            		platform  = "iOS";
			            	} else if(object.get("c_Platform").getAsString().startsWith("iOS")) {
			            		platform = "iOS";
			            	} else if(object.get("c_Platform").getAsString().startsWith("Win")) {
			            		platform = "Windows";
			            	}
			            }
			            defect.setPlatform(platform);
			            
			            if(platform.equalsIgnoreCase(operatingSystem) || operatingSystem.equalsIgnoreCase("All")) {
			            	defects.add(defect);	
			            }
		            }
		        }
		    	List<Defect> olderDefects = allDefects.get(typeCategory);
		    	if(null == olderDefects) {
		    		olderDefects = new ArrayList<Defect>();
		    	}
		    	olderDefects.addAll(defects);
		    	Collections.sort(olderDefects);
		    	allDefects.put(typeCategory, olderDefects);
	    	} catch(HttpHostConnectException connectException){
	    		if(null != restApi) {
	    			restApi.close();
	    		}
	    		try {
					restApi = loginRally(configuration);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
	    	}
    	}
	}
    
	public static void retrieveTestCases(DashboardForm dashboardForm, Configuration configuration, String cutoffDate)
			throws IOException, URISyntaxException {
		RallyRestApi  restApi = loginRally(configuration);
		List<TestCase> testCases = new ArrayList<TestCase>();
		QueryFilter queryFilter = new QueryFilter("CreationDate", ">=", cutoffDate).and(new QueryFilter("Type", "=", "Regression"));
    	QueryRequest defectRequest = new QueryRequest("testcases");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("FormattedID", "LastVerdict", "Name", "Description", "LastRun", "LastBuild", "Priority"));
    	defectRequest.setProject("/project/"+dashboardForm.getProjectId()); 
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(10000);
    	defectRequest.setOrder("FormattedID desc");
    	boolean dataNotReceived = true;
    	while(dataNotReceived){
	    	try {
		    	QueryResponse projectDefects = restApi.query(defectRequest);
		    	JsonArray defectsArray = projectDefects.getResults();
		    	dataNotReceived = false;
		    
		    	for(int i=0; i<defectsArray.size(); i++) {
		    		TestCase testCase = new TestCase();
		    		JsonElement elements =  defectsArray.get(i);
		            JsonObject object = elements.getAsJsonObject();
		            testCase.setTestCaseId(object.get("FormattedID").getAsString());
		            testCase.setLastVerdict(object.get("LastVerdict")==null || object.get("LastVerdict").isJsonNull() ?"":object.get("LastVerdict").getAsString());
		            testCase.setName(object.get("Name")==null || object.get("Name").isJsonNull() ?"":object.get("Name").getAsString());
		            testCase.setDescription(object.get("Description")==null || object.get("Description").isJsonNull() ?"":object.get("Description").getAsString());
		            testCase.setLastRun(object.get("LastRun")==null || object.get("LastRun").isJsonNull() ?"":object.get("LastRun").getAsString());
		            testCase.setLastBuild(object.get("LastBuild")==null || object.get("LastBuild").isJsonNull() ?"":object.get("LastBuild").getAsString());
		            testCase.setPriority(object.get("Priority")==null || object.get("Priority").isJsonNull() ?"":object.get("Priority").getAsString());
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
	    	} catch(HttpHostConnectException connectException) {
	    		if(restApi != null) {
	    			restApi.close();
	    		}
	    		try {
					restApi = loginRally(configuration);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
	    	}
    	}
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
					if(paramSub[3].equals("null")) {
						subTtab.setRelease(null);
					} else {
						subTtab.setRelease(paramSub[3]);
					}
					if(null != paramSub[4] && !"null".equals(paramSub[4])) {
						subTtab.setCutoffDate(paramSub[4]);
					}
					if(null != paramSub[5] && "true".equals(paramSub[5])) {
						subTtab.setRegressionData(true);
					} else {
						subTtab.setRegressionData(false);
					}
					subTtab.setTabType("Child");
					subTtab.setInformation(paramSub[7]);
					if(paramSub[8].equals("null")) {
						subTtab.setTag(null);
					} else {
						subTtab.setTag(paramSub[8]);
					}
					subTabs.add(subTtab);
				}
			}
		}
		Collections.sort(subTabs);
		return subTabs;
	}
	
	public void configureTestSet() {
	    try {
	        Properties props = new Properties();
	        props.setProperty("ServerAddress", "");
	        props.setProperty("ServerPort", ""+"");
	        props.setProperty("ThreadCount", ""+"");
	        File f = new File("server.properties");
	        OutputStream out = new FileOutputStream( f );
	        props.store(out, "This is an optional header comment string");
	    }
	    catch (Exception e ) {
	        e.printStackTrace();
	    }
	}
	
	public static void setOperatingSystems(DashboardForm dashboardForm) {
		List<String> operatingSystems = new ArrayList<String>();
		operatingSystems.add("iOS");
		operatingSystems.add("Windows");
		operatingSystems.add("All");
		dashboardForm.setOperatingSystems(operatingSystems);
	}
	
	public static void retrieveTestCasesUsingSets(DashboardForm dashboardForm, Configuration configuration, String cutoffDateStr, List<String> testSets)
			throws IOException, URISyntaxException, ParseException {
		RallyRestApi  restApi = loginRally(configuration);
        QueryRequest testSetRequest = new QueryRequest("TestSet");
        testSetRequest.setProject("/project/"+dashboardForm.getProjectId()); 
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        
        testSetRequest.setFetch(new Fetch(new String[] {"Name", "Priority",  "Description", "TestCases", "FormattedID", "LastVerdict", "LastBuild","LastRun"}));
        QueryFilter queryFilter = new QueryFilter("FormattedID", "=", testSets.get(0));
        int q = 1;
        while(testSets.size() > q) {
        	queryFilter = queryFilter.or(new QueryFilter("FormattedID", "=", testSets.get(q)));
        	q++;
        }
        testSetRequest.setQueryFilter(queryFilter);
        boolean dataNotReceived = true;
    	while(dataNotReceived){
	    	try {
		        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
		        dataNotReceived = false;
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
		        
				int testCasesCount = 0;
				List<TestCase> testCases = new ArrayList<TestCase>();
				for (int i=0; i<testSetQueryResponse.getResults().size();i++){
		            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
		            int numberOfTestCases = testSetJsonObject.get("TestCases").getAsJsonArray().size();
		            if(numberOfTestCases>0){
		            		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd"); 
		            		Date cutoffDate = (Date) formatter1.parse(cutoffDateStr);
		            		
		                  	for (int j=0;j<numberOfTestCases;j++){
		                  		JsonObject jsonObject = testSetJsonObject.get("TestCases").getAsJsonArray().get(j).getAsJsonObject();
		                  		TestCase testCase = new TestCase();
		                  		testCase.setTestCaseId(jsonObject.get("FormattedID").getAsString());
		                	  	Date lastVerdictDate = null;
		                	  	if(null != jsonObject.get("LastRun") && !jsonObject.get("LastRun").isJsonNull()) {
		                	  		lastVerdictDate = (Date) formatter1.parse(jsonObject.get("LastRun").getAsString());
		                	  	}
		                	  	String lastVerdict = "";
		                	  	if(!jsonObject.get("LastVerdict").isJsonNull()) {
		                	  		lastVerdict = jsonObject.get("LastVerdict").getAsString();
		                	  	}
		                	  	if(null != lastVerdictDate && lastVerdictDate.compareTo(cutoffDate) >= 0) {
			                	  	if(lastVerdict.equalsIgnoreCase("Pass")) {
			        			    	priority0.setPriorityCount(priority0.getPriorityCount()+1);
			        			    }
			        			    if(lastVerdict.equalsIgnoreCase("Blocked")) {
			        			    	priority1.setPriorityCount(priority1.getPriorityCount()+1);
			        			    }
			        			    if(lastVerdict.equalsIgnoreCase("Error")) {
			        			    	priority2.setPriorityCount(priority2.getPriorityCount()+1);
			        			    }
			        			    if(lastVerdict.equalsIgnoreCase("Fail")) {
			        			    	priority3.setPriorityCount(priority3.getPriorityCount()+1);
			        			    }
			        			    if(lastVerdict.equalsIgnoreCase("Inconclusive")) {
			        			    	priority4.setPriorityCount(priority4.getPriorityCount()+1);
			        			    }
			        			    if(lastVerdict.equalsIgnoreCase("")) {
			        			    	priority5.setPriorityCount(priority5.getPriorityCount()+1);
			        			    }
			        			    
		                	  	} else {
		                	  		priority5.setPriorityCount(priority5.getPriorityCount()+1);
		                	  	}
		                	  	testCasesCount++;
		                	  	testCase.setLastVerdict(jsonObject.get("LastVerdict")==null || jsonObject.get("LastVerdict").isJsonNull() ?"":jsonObject.get("LastVerdict").getAsString());
		        			    testCase.setName(jsonObject.get("Name")==null || jsonObject.get("Name").isJsonNull() ?"":jsonObject.get("Name").getAsString());
		        	            testCase.setDescription(jsonObject.get("Description")==null || jsonObject.get("Description").isJsonNull() ?"":jsonObject.get("Description").getAsString());
		        	            testCase.setLastRun(jsonObject.get("LastRun")==null || jsonObject.get("LastRun").isJsonNull() ?"":jsonObject.get("LastRun").getAsString());
		        	            testCase.setLastBuild(jsonObject.get("LastBuild")==null || jsonObject.get("LastBuild").isJsonNull() ?"":jsonObject.get("LastBuild").getAsString());
		        	            testCase.setPriority(jsonObject.get("Priority")==null || jsonObject.get("Priority").isJsonNull() ?"":jsonObject.get("Priority").getAsString());
		        	            testCases.add(testCase);
		                 }
		            }
		        }
				dashboardForm.setTestCases(testCases);        
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
				
				dashboardForm.setTestCasesCount(testCasesCount);
				dashboardForm.setTestCasesPriorities(priorities);
	    	} catch(HttpHostConnectException connectException) {
	    		if(restApi != null) {
	    			restApi.close();
	    		}
	    		try {
					restApi = loginRally(configuration);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
	    	}
    	}
	}
	
	public static void retrieveTestResults(DashboardForm dashboardForm, Configuration configuration, String cutoffDateStr, List<String> testSets)
			throws IOException, URISyntaxException, ParseException {
		RallyRestApi  restApi = loginRally(configuration);
        QueryRequest testCaseResultsRequest = new QueryRequest("TestCaseResult");
        testCaseResultsRequest.setFetch(new Fetch("Build","TestCase","TestSet", "Verdict","FormattedID","Date", "TestCaseCount"));
        if(testSets == null ||  testSets.isEmpty()){
        	testSets = new ArrayList<String>();
        	testSets.add("TS0");
        }
        QueryFilter queryFilter = new QueryFilter("TestSet.FormattedID", "=", testSets.get(0));
        int q = 1;
        while(testSets.size() > q) {
        	queryFilter = queryFilter.or(new QueryFilter("TestSet.FormattedID", "=", testSets.get(q)));
        	q++;
        }
        testCaseResultsRequest.setLimit(3000);
        testCaseResultsRequest.setQueryFilter(queryFilter);
        boolean dataNotReceived = true;
    	while(dataNotReceived){
	    	try {
		        QueryResponse testCaseResultResponse = restApi.query(testCaseResultsRequest);
		        JsonArray array = testCaseResultResponse.getResults();
		        int numberTestCaseResults = array.size();
		        dataNotReceived = false;
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
				List<TestCase> testCases = new ArrayList<TestCase>();
				List<TestResult> testResults = new ArrayList<TestResult>();
				if(numberTestCaseResults >0) {
		        	for(int i=0; i<numberTestCaseResults; i++) {
		        		TestResult testResult = new TestResult();
		        		TestCase testCase = new TestCase();
		        		String build = array.get(i).getAsJsonObject().get("Build").getAsString();
		        		String verdict = array.get(i).getAsJsonObject().get("Verdict").getAsString();
		        		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		        		String strDate = array.get(i).getAsJsonObject().get("Date").getAsString().substring(0, 10);
		        		int hour = Integer.parseInt(array.get(i).getAsJsonObject().get("Date").getAsString().substring(11, 13));
		        		int min = Integer.parseInt(array.get(i).getAsJsonObject().get("Date").getAsString().substring(14, 16));
		                Date date = (Date) formatter1.parse(strDate);
		                date.setHours(hour);
		                date.setMinutes(min);
		        		JsonObject testSetJsonObj = array.get(i).getAsJsonObject().get("TestSet").getAsJsonObject();
		        		JsonObject testCaseJsonObj = array.get(i).getAsJsonObject().get("TestCase").getAsJsonObject();
		        		String testSet = testSetJsonObj.get("FormattedID").getAsString();
		        		String testCaseId = testCaseJsonObj.get("FormattedID").getAsString();
		        		int resultExists = testResultExists(testSet, testCaseId, date, testResults, testCases); 
		        		if(resultExists != 0) {
		        			testResult.setDate(date);
		        			testResult.setStatus(verdict);
		        			testResult.setTestCase(testCaseId);
		        			testResult.setTestSet(testSet);
		        			testResults.add(testResult);
		        			testCase.setTestCaseId(testCaseId);
	                	  	testCase.setLastVerdict(verdict);
	        			    testCase.setName(testSet);
	        	            testCase.setDescription("");
	        	            testCase.setLastRun(strDate);
	        	            testCase.setLastBuild(build);
	        	            testCase.setPriority("");
	        	            testCases.add(testCase);
		        		}
	                 }
	            }
				for(TestResult result:testResults) {
					String verdict = result.getStatus();
	        		if(verdict.equalsIgnoreCase("error")) {
	        			priority2.setPriorityCount(priority2.getPriorityCount()+1);
	        		} else if(verdict.equalsIgnoreCase("pass")) {
	        			priority0.setPriorityCount(priority0.getPriorityCount()+1);
	        		} else if(verdict.equalsIgnoreCase("fail")) {
	        			priority3.setPriorityCount(priority3.getPriorityCount()+1);
	        		} else if(verdict.equalsIgnoreCase("inconclusive")) {
	        			priority4.setPriorityCount(priority4.getPriorityCount()+1);
	        		} else if(verdict.equalsIgnoreCase("blocked")) {
	        			priority1.setPriorityCount(priority1.getPriorityCount()+1);
	        		}
    			}
        		
				dashboardForm.setTestCases(testCases); 
				
				
				QueryRequest testCaseCountReq = new QueryRequest("TestSet");
				testCaseCountReq.setFetch(new Fetch("FormattedID", "Name", "TestCaseCount"));
				queryFilter = new QueryFilter("FormattedID", "=", testSets.get(0));
		        q = 1;
		        while(testSets.size() > q) {
		        	queryFilter = queryFilter.or(new QueryFilter("FormattedID", "=", testSets.get(q)));
		        	q++;
		        }
		        testCaseCountReq.setQueryFilter(queryFilter);
		        QueryResponse testCaseResponse = restApi.query(testCaseCountReq);
		        int testCaseCount = 0;
		        for(int i=0; i<testCaseResponse.getResults().size(); i++) {
		        	testCaseCount = testCaseCount + testCaseResponse.getResults().get(i).getAsJsonObject().get("TestCaseCount").getAsInt();
		        }
		        
		        int unAttempted = testCaseCount - priority0.getPriorityCount() - priority1.getPriorityCount() - priority2.getPriorityCount() - priority3.getPriorityCount() - priority4.getPriorityCount();
		        priority5.setPriorityCount(unAttempted);
		        
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
				
				dashboardForm.setTestCasesCount(testCaseCount);
				dashboardForm.setTestCasesPriorities(priorities);
	    	} catch(HttpHostConnectException connectException) {
	    		if(restApi != null) {
	    			restApi.close();
	    		}
	    		try {
					restApi = loginRally(configuration);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
	    	}
    	}
	}

	public static RegressionData getRegressionSetDetails(String tabUniqueId) throws IOException {
		File file = new File(System.getProperty("user.home"), "config/regression.properties");
    	InputStream streamProject = new FileInputStream(file);
    	Properties regressionProperties = new Properties();
		regressionProperties.load(streamProject);
		for(Enumeration<String> en = (Enumeration<String>) regressionProperties.propertyNames();en.hasMoreElements();) {
			String key = (String) en.nextElement();
			String params = regressionProperties.getProperty(key);
			if(null != params) {
				String param[] = params.split(":");
				if(param[0].equalsIgnoreCase(tabUniqueId)) {
					RegressionData regressionData = new RegressionData();
					
					regressionData.setCutoffDate(param[1]);
					
					String iossets[] = param[2].split("~");
					String sets[] = iossets[1].split(",");
					List<String> iostestSets = new ArrayList<String>();
					for(String set:sets) {
						iostestSets.add(set);
					}
					regressionData.setIosTestSetsIds(iostestSets);
					
					String winsets[] = param[3].split("~");
					List<String> wintestSets = new ArrayList<String>();
					if(winsets.length > 1) {
						sets = winsets[1].split(",");
						for(String set:sets) {
							wintestSets.add(set);
						}
					}
					regressionData.setWinTestSetsIds(wintestSets);
					
					return regressionData;
				}
			}
		}
		return null;
	}
	
	public static Map<String, List<String>> getIteration(Configuration configuration, String projectId) throws IOException, ParseException, URISyntaxException {
		RallyRestApi  restApi = loginRally(configuration);
		Map<String, List<String>> testSetsInformation = new HashMap<String, List<String>>();
    	QueryRequest iterationRequest = new QueryRequest("Iteration");
    	iterationRequest.setProject("/project/"+projectId);
    	iterationRequest.setLimit(2000);
        QueryResponse iterationResponse = restApi.query(iterationRequest);
        JsonArray iterationArray = iterationResponse.getResults();
        QueryFilter queryFilter = null;
        for(int i=0; i<iterationArray.size(); i++) {
    		JsonElement elements =  iterationArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
    		String ref = object.get("_ref").getAsString();
    		ref = ref.substring(ref.indexOf("/iteration/"));
    		ref = ref.replace(".js", "");
    		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
    		String strStartDate = object.get("StartDate").getAsString().substring(0, 10);
            Date startDate = (Date) formatter1.parse(strStartDate);
            String strEndDate = object.get("EndDate").getAsString().substring(0, 10);
            Date endDate = (Date) formatter1.parse(strEndDate);
    		Date dateNow = new Date();
    		if(dateNow.after(startDate) && dateNow.before(endDate)) {
    			if(queryFilter == null) {
    				queryFilter = new QueryFilter("Iteration", "=", ref);
    			} else {
    				queryFilter = queryFilter.or(new QueryFilter("Iteration", "=", ref));
    			}
    		}
    	}
        if(queryFilter != null) {
	        QueryRequest testSetRequest = new QueryRequest("TestSet");
	        testSetRequest.setQueryFilter(queryFilter);
	        String wsapiVersion = "1.43";
	        restApi.setWsapiVersion(wsapiVersion);
	        testSetRequest.setProject("/project/"+projectId);
	        testSetRequest.setScopedDown(true);
	    	QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
	        List<String> iosTestSetList = new ArrayList<String>();
	        List<String> winTestSetList = new ArrayList<String>();
	        for (int i=0; i<testSetQueryResponse.getResults().size();i++){
	            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
	            String testSetId = testSetJsonObject.get("FormattedID").getAsString();
	            String testSetName = testSetJsonObject.get("Name").getAsString();
	            if(testSetName.toUpperCase().contains("EOS")) {
	            	if(testSetName.toUpperCase().contains("WIN")) {
	            		winTestSetList.add(testSetId);
	            	} else {
	            		iosTestSetList.add(testSetId);
	            	}
	            }
	            
	        }
	        testSetsInformation.put("IOS", iosTestSetList);
	        testSetsInformation.put("WIN", winTestSetList);
        }
        return testSetsInformation;
    }
	
	private static int testResultExists(String testSet, String testCase, Date date, List<TestResult> testResults, List<TestCase> testCases) {
    	int index = 0;
    	for(TestResult testResult:testResults) {
    		if(testResult.getTestCase().equalsIgnoreCase(testCase) && testResult.getTestSet().equalsIgnoreCase(testSet)){
    			if( testResult.getDate().after(date)) {
    				return 0;
    			} else {
    				testResults.remove(index);
    				testCases.remove(index);
    				return 1;
    			}
    		} 
    		index++;
    	}
    	return -1;
    }
}
