package com.pearson.dashboard.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pearson.dashboard.vo.Release;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

public class TestClass {
    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {

    	RallyRestApi restApi = loginRally(); 
    	//updateTestSet(restApi);
    	updateTestCase(restApi, "TC24408,TC43971,TC44280,TC26310,TC31609,TC24407,TC27034,TC15944,TC15999,TC15955,TC23138,TC23265,TC23923,TC23163,TC23271,TC23164,TC22307,TC23167,TC15888,TC19059,TC18573,TC17673,TC15875,TC19362,TC20078,TC16193,TC20030,TC15876,TC19372,TC15883,TC15880,TC45485,TC15882,TC17672,TC16114,TC20077,TC17667,TC22044,TC17666,TC17676,TC17675,TC17669,TC30109,TC30108,TC30110,TC17684,TC17681,TC19398,TC34064,TC16047,TC19380,TC15865,TC16042,TC20151,TC16011,TC18632,TC20448,TC18627,TC16007,TC22423,TC20519,TC16214,TC22555,TC20431,TC20449,TC20378,TC16002,TC20388,TC23168,TC23257,TC16216,TC20387,TC16012,TC18625,TC23166,TC20508,TC20430,TC16003,TC20450,TC20390,TC22465,TC16018,TC22639,TC15964,TC20511,TC22242,TC23273,TC20517,TC16210,TC22627,TC16009,TC16001,TC20465,TC22625,TC22138,TC18622,TC18616,TC20518,TC22626,TC23274,TC22549,TC22524,TC16023,TC20514,TC23258,TC16010,TC20422,TC16017,TC19058,TC15860,TC34062,TC16028");
    	//retrieveTestSets(restApi);
    	//retrieveTestCases(restApi);
    	//retrieveDefects(restApi);
    	restApi.close();
    	//postJenkinsJob();
    }
    
    private static void updateTestSet(RallyRestApi restApi) throws IOException {
    	QueryRequest testCaseRequest = new QueryRequest("TestCase");
        testCaseRequest.setFetch(new Fetch("FormattedID","Name"));
        String testcasesids = "TC15859,TC15874,TC15880,TC15881,TC15882,TC15886,TC15887,TC15888,TC15909,TC15967,TC15969,TC15970,TC15971,TC15972,TC15974,TC15980,TC16087,TC16091,TC16092,TC16108,TC16109,TC16114,TC17664,TC17665,TC17669,TC17670,TC17675,TC17676,TC17679,TC17696,TC18572,TC19372,TC19373,TC19374,TC20039,TC20098,TC15877,TC15921,TC17673,TC19058,TC19384,TC26212 ,TC26213,TC26215,TC16019,TC22307,TC23154,TC23923,TC24163,TC24164,TC24165,TC24166,TC24356,TC25097,TC25112,TC25115,TC25116,TC25117,TC25118,TC25119,TC25121,TC25123,TC25125,TC25129,TC25135,TC25140,TC25168,TC25472,TC25818,TC25821,TC26310,TC26311,TC27034,TC27139,TC27142,TC27145,TC27213,TC27214,TC28346,TC14961,TC29823,TC27473,TC27450,TC28364,TC27470,TC27224,TC30109,TC27219,TC30108,TC20269,TC20270,TC23163,TC23169,TC23164,TC23167,TC20376,TC22054,TC23165,TC22044,TC22138,TC23265,TC23271,TC43978,TC44280,TC44276,TC43971,TC44514,TC43948";
        String[] tcids = testcasesids.split(",");
        
        QueryFilter query = new QueryFilter("FormattedID", "=", "TC15944");
        
        for(String tc:tcids) {
        	query = query.or(new QueryFilter("FormattedID", "=", tc));
        }
        	
        testCaseRequest.setQueryFilter(query);
        QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
        JsonArray testCases = new JsonArray();
        JsonArray testCasesArray = testCaseQueryResponse.getResults();
        
        for(int i=0; i<testCasesArray.size(); i++) {
    		JsonElement elements =  testCasesArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
    		testCases.add(object);
    	}
        
        QueryRequest testSetRequest = new QueryRequest("TestSet");
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        
        testSetRequest.setQueryFilter(new QueryFilter("FormattedID", "=", "TS569"));
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        JsonArray testSetArray = testSetQueryResponse.getResults();
        
        for(int i=0; i<testSetArray.size(); i++) {
    		JsonElement elements =  testSetArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
    		System.out.println(i+" "+object);
            System.out.println(i+" "+object.get("_ref"));
            JsonArray exTCs = object.get("TestCases").getAsJsonArray();
            exTCs.addAll(testCases);
            object.add("TestCases", exTCs);
            UpdateRequest request = new UpdateRequest("/testset/33616967261", object);
            UpdateResponse response = restApi.update(request);
            System.out.println(response);
    	}
    }
    
    private static void getUserStory(RallyRestApi restApi) throws IOException {
    	QueryRequest storyRequest = new QueryRequest("HierarchicalRequirement");
        //storyRequest.setWorkspace(workspaceRef);
        //restApi.setApplicationName(applicationName);  
        storyRequest.setFetch(new Fetch(new String[] {"Name", "FormattedID", "Tags", "Children"}));
        storyRequest.setLimit(1000);
        storyRequest.setScopedDown(false);
        storyRequest.setScopedUp(false);

        storyRequest.setQueryFilter((new QueryFilter("FormattedID", "=", "US6407")).and(new QueryFilter("DirectChildrenCount", ">", "0")));

        QueryResponse storyQueryResponse = restApi.query(storyRequest);
        System.out.println("Successful: " + storyQueryResponse.wasSuccessful());
        System.out.println("Size: " + storyQueryResponse.getTotalResultCount());

        for (int i=0; i<storyQueryResponse.getTotalResultCount();i++){
            JsonObject storyJsonObject = storyQueryResponse.getResults().get(i).getAsJsonObject();
            System.out.println("Name: " + storyJsonObject.get("Name") + " FormattedID: " + storyJsonObject.get("FormattedID"));
            QueryRequest childrenRequest = new QueryRequest(storyJsonObject.getAsJsonObject("Children"));
            childrenRequest.setFetch(new Fetch("Name","FormattedID"));
            int numberOfChildren = storyJsonObject.get("DirectChildrenCount").getAsInt();
            System.out.println(numberOfChildren);
            //load the collection
            JsonArray children = restApi.query(childrenRequest).getResults();
            for (int j=0;j<numberOfChildren;j++){
                System.out.println("Name: " + children.get(j).getAsJsonObject().get("Name") + children.get(j).getAsJsonObject().get("FormattedID").getAsString());
                System.out.println("Name: " + children.get(0).getAsJsonObject().get("Name") + children.get(0).getAsJsonObject().get("FormattedID").getAsString());
            }
        }
    }
    
    private static void updateTestCase(RallyRestApi restApi, String testcases) throws IOException {
    	
    	QueryRequest userRequest = new QueryRequest("User");
        userRequest.setFetch(new Fetch("UserName", "Subscription", "DisplayName", "SubscriptionAdmin"));
        userRequest.setQueryFilter(new QueryFilter("UserName", "=", "mohammed.saquib@pearson.com"));
        QueryResponse userQueryResponse = restApi.query(userRequest);
        JsonArray userQueryResults = userQueryResponse.getResults();
        JsonElement userQueryElement = userQueryResults.get(0);
        JsonObject userQueryObject = userQueryElement.getAsJsonObject();
        String userRef = userQueryObject.get("_ref").getAsString();  
        
        //String wsapiVersion = "1.43";
        //restApi.setWsapiVersion(wsapiVersion);
        QueryRequest testSetRequest = new QueryRequest("TestSet");
        testSetRequest.setQueryFilter(new QueryFilter("FormattedID", "=", "TS615"));
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        String testSetRef = testSetQueryResponse.getResults().get(0).getAsJsonObject().get("_ref").getAsString(); 
        
        String testCaseRef = "";
        String testCaseIds[] = testcases.split(",");
        for(String testCase: testCaseIds) {
            QueryRequest testCaseRequest = new QueryRequest("TestCase");
	        testCaseRequest.setFetch(new Fetch("FormattedID","Name"));
	        testCaseRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testCase));
	        QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
	        testCaseRef = testCaseQueryResponse.getResults().get(0).getAsJsonObject().get("_ref").getAsString(); 
	        
	        if(null != testCaseRef && !testCaseRef.equals("")){
		        JsonObject newTestCaseResult = new JsonObject();
		        newTestCaseResult.addProperty("Verdict", "Pass");
		        newTestCaseResult.addProperty("Date", "2015-04-28T18:40:00.000Z");
		        newTestCaseResult.addProperty("Build", "1.6.0.408");
		        newTestCaseResult.addProperty("TestCase", testCaseRef);
		        newTestCaseResult.addProperty("Tester", userRef);
		        newTestCaseResult.addProperty("TestSet", testSetRef);
		        
		        CreateRequest createRequest = new CreateRequest("testcaseresult", newTestCaseResult);
		        CreateResponse createResponse = restApi.create(createRequest);  
		        if (createResponse.wasSuccessful()) {
		            System.out.println(String.format("Created %s", createResponse.getObject().get("_ref").getAsString()));          
		        } else {
		            System.out.println("Error occurred creating Test Case Result: ");
		        }
	        } else {
	        	System.out.println("Error occurred creating Test Case Result: ");
	        }
        }
    }
    
    private static void retrieveTestSets(RallyRestApi restApi)
			throws IOException, URISyntaxException, ParseException {

        QueryRequest testSetRequest = new QueryRequest("TestSet");
        testSetRequest.setProject("/project/28521436640");
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);

        testSetRequest.setFetch(new Fetch(new String[] {"Name", "Description", "TestCases", "FormattedID", "LastVerdict", "LastBuild", "LastRun", "Priority", "Method"}));
        String testSetsString = "TS615";
        String[] testSets = testSetsString.split(",");
        QueryFilter query = new QueryFilter("FormattedID", "=", "TS0");
        for(String testSet:testSets) {
        	query = query.or(new QueryFilter("FormattedID", "=", testSet));
        }
        testSetRequest.setQueryFilter(query);
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        int ij=1;
        for (int i=0; i<testSetQueryResponse.getResults().size();i++){
            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
            int numberOfTestCases = testSetJsonObject.get("TestCases").getAsJsonArray().size();
            if(numberOfTestCases>0){
                  for (int j=0;j<numberOfTestCases;j++){
                	  	JsonObject jsonObject = testSetJsonObject.get("TestCases").getAsJsonArray().get(j).getAsJsonObject();
                	  	DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd"); 
                	  	if(null != jsonObject.get("LastRun") && !jsonObject.get("LastRun").isJsonNull()) {
                	  		Date date = (Date) formatter1.parse(jsonObject.get("LastRun").getAsString());
          	            	DateFormat formatter2 = new SimpleDateFormat("MMM dd YY");
          	            	String dateStr = formatter2.format(date);
          	            	System.out.println((ij)+"\t"+ jsonObject.get("FormattedID") +"\t" + jsonObject.get("LastVerdict")+"\t" + dateStr +"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Description").getAsString().replaceAll("\\<[^>]*>","")+"\t" + jsonObject.get("LastBuild")+"\t" + jsonObject.get("Priority")+"\t" + jsonObject.get("Method"));
                	  	} else {
                	  		System.out.println((ij)+"\t"+ jsonObject.get("FormattedID") +"\t" + jsonObject.get("LastVerdict")+"\t" + "" +"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Description").getAsString().replaceAll("\\<[^>]*>","")+"\t" + jsonObject.get("LastBuild")+"\t" + jsonObject.get("Priority")+"\t" + jsonObject.get("Method"));
                	  	}
                	  	ij++;
                 }
            }
        }

	}
    
    private static void retrieveTestCases(RallyRestApi restApi)
			throws IOException {
		
		QueryFilter queryFilter = new QueryFilter("CreationDate", ">=", "2014-09-16").and(new QueryFilter("Type", "=", "Regression")).and(new QueryFilter("TestSets.ObjectID", "=", "29426036743"));
    	QueryRequest defectRequest = new QueryRequest("testcases");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("FormattedID", "LastVerdict", "TestSets"));
    	defectRequest.setProject("/project/23240411122"); 
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(10000);
    	QueryResponse projectDefects = restApi.query(defectRequest);
    	JsonArray defectsArray = projectDefects.getResults();
    
    	for(int i=0; i<defectsArray.size(); i++) {
    		JsonElement elements =  defectsArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
            System.out.println(i+" "+object);
            JsonObject testSets = object.get("TestSets").getAsJsonObject();
            System.out.println(testSets.get("_ref"));
    	}
	}
    
    public static void postJenkinsJob() throws ClientProtocolException, IOException {
    	String username = "vsaqumo";
    	String password = "Welcome2";
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials(username, password));
		BasicScheme basicAuth = new BasicScheme();
		BasicHttpContext context = new BasicHttpContext();
		context.setAttribute("preemptive-auth", basicAuth);
		client.addRequestInterceptor(new PreemptiveAuth(), 0);
		String getUrl = "http://pjenkinsx.cloudapp.net:9090/view/212/job/212-iOS-1.6/lastBuild/";
		HttpGet get = new HttpGet(getUrl);
		try {
			HttpResponse response = client.execute(get, context);
    		HttpEntity entity = response.getEntity();
    		if (entity != null) {
	           String retSrc = EntityUtils.toString(entity); 
	           System.out.println(retSrc);
    		}
		}
    	catch (IOException e) {
			e.printStackTrace();
		}
    }

	private static void retrieveDefects(RallyRestApi restApi)
			throws IOException {
		
		QueryFilter queryFilter = new QueryFilter("FormattedID", "=", "DE8485");
    	QueryRequest defectRequest = new QueryRequest("defects");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("State", "Name", "Tags", "Platform", "Release", "FormattedID", "Environment", "Priority", "LastUpdateDate", "SubmittedBy", "Owner", "Project", "ClosedDate"));
    	defectRequest.setProject("/project/23240411122"); 
    	defectRequest.setScopedDown(true);
    	QueryResponse projectDefects = restApi.query(defectRequest);
    	JsonArray defectsArray = projectDefects.getResults();
    
    	for(int i=0; i<defectsArray.size(); i++) {
    		JsonElement elements =  defectsArray.get(i);
            JsonObject object = elements.getAsJsonObject();
            if(null != object.get("Tags") && !object.get("Tags").isJsonNull()) {
            	JsonObject jsonObject = object.get("Tags").getAsJsonObject();
            	
            	int numberOfTestCases = jsonObject.get("_tagsNameArray").getAsJsonArray().size();
                if(numberOfTestCases>0){
                      for (int j=0;j<numberOfTestCases;j++){
	            	  	JsonObject jsonObj = jsonObject.get("_tagsNameArray").getAsJsonArray().get(j).getAsJsonObject();
	            	  	System.out.println(jsonObj.get("Name"));
                     }
                }
            }
    	}
	}
    
	private static void retrieveTags(RallyRestApi restApi)
			throws IOException, ParseException {
		
		QueryRequest testSetRequest = new QueryRequest("Tags");
        testSetRequest.setProject("/project/23240411122");
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        
        testSetRequest.setFetch(new Fetch(new String[] {"Name", "Defects"}));
        testSetRequest.setQueryFilter(new QueryFilter("Name", "=", "Release 1.6 - CA Adoption"));
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        for (int i=0; i<testSetQueryResponse.getResults().size();i++){
            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
            int numberOfTestCases = testSetJsonObject.get("Defects").getAsJsonArray().size();
            if(numberOfTestCases>0){
                  for (int j=0;j<numberOfTestCases;j++){
                	  	JsonObject jsonObject = testSetJsonObject.get("Defects").getAsJsonArray().get(j).getAsJsonObject();
                	  	System.out.println(jsonObject.get("FormattedID"));
                 }
            }
        }
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
    
    private static RallyRestApi loginRally() throws URISyntaxException {
    	String rallyURL = "https://rally1.rallydev.com";
     	String myUserName = "mohammed.saquib@pearson.com";
     	String myUserPassword = "Rally@123";
     	return new RallyRestApi(new URI(rallyURL), myUserName, myUserPassword);
    }
    
    private static boolean isReleaseAlreadyAdded(List<Release> releases, String releaseName) {
    	for(Release release:releases) {
    		if(release.getReleaseName().equals(releaseName)) {
    			return true;
    		}
    	}
    	return false;
    }

    static class PreemptiveAuth implements HttpRequestInterceptor {
    	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
			if (authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null) {
					Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost
							.getPort()));
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}
		}
	}
}

