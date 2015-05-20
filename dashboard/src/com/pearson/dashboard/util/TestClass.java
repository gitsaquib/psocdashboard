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
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

public class TestClass {
    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {

    	RallyRestApi restApi = loginRally(); 
    	//updateTestSet(restApi);
    	//updateTestCase(restApi, "TC44627,TC45043");
    	//retrieveTestSets(restApi);
    	retrieveTestSetsResult(restApi);
    	//retrieveTestCases(restApi);
    	//retrieveDefects(restApi);
    	//retrieveTestFolder(restApi);
    	restApi.close();
    	//postJenkinsJob();
    }
    
    private static void updateTestSet(RallyRestApi restApi) throws IOException {
    	QueryRequest testCaseRequest = new QueryRequest("TestCase");
        testCaseRequest.setFetch(new Fetch("FormattedID","Name"));
        String testcasesids = "TC25478,TC26223,TC29984,TC24402,TC24403,TC24361,TC24304,TC24295,TC27061,TC24401,TC19548,TC19550,TC19924,TC19925,TC19926,TC24429,TC30104,TC19571,TC19554,TC30102,TC26288,TC19648,TC30009,TC24425,TC31834,TC44181,TC24175,TC22859,TC29862,TC19573,TC20419,TC20418,TC30048,TC30046,TC30007,TC29992,TC29989,TC20482,TC23890,TC31645,TC25165,TC43479,TC31593,TC30049,TC29985,TC27025,TC30052,TC24413,TC28365,TC26287,TC31591,TC27059,TC29979,TC31592,TC29961,TC43463,TC21662,TC28363,TC21671,TC27060,TC20721,TC23287,TC31833,TC19549,TC25167,TC23883,TC27011,TC31835,TC30028,TC20803,TC21667,TC20805,TC31812,TC30047,TC43808,TC43809,TC27155,TC31613,TC26284,TC23129,TC31611,TC22759,TC22303,TC19556,TC24343,TC24399,TC23199,TC23201,TC31751,TC23309,TC43481,TC23363,TC23365,TC23364,TC43462";
        String[] tcids = testcasesids.split(",");
        
        QueryFilter query = new QueryFilter("FormattedID", "=", "TC43480");
        
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
        
        testSetRequest.setQueryFilter(new QueryFilter("FormattedID", "=", "TS681"));
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
            UpdateRequest request = new UpdateRequest("/testset/35178559772", object);
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
        userRequest.setQueryFilter(new QueryFilter("UserName", "=", "namrita.agarwal@pearson.com"));
        QueryResponse userQueryResponse = restApi.query(userRequest);
        JsonArray userQueryResults = userQueryResponse.getResults();
        JsonElement userQueryElement = userQueryResults.get(0);
        JsonObject userQueryObject = userQueryElement.getAsJsonObject();
        String userRef = userQueryObject.get("_ref").getAsString();  
        
        //String wsapiVersion = "1.43";
        //restApi.setWsapiVersion(wsapiVersion);
        String testSets[] = "TS722,TS723,TS724,TS725,TS726,TS727,TS728,TS730,TS731,TS732".split(",");
        for(String testSet:testSets) {
	        QueryRequest testSetRequest = new QueryRequest("TestSet");
	        testSetRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testSet));
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
			        newTestCaseResult.addProperty("Date", "2015-05-19T17:00:00.000Z");
			        newTestCaseResult.addProperty("Build", "1.6.0.715");
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
    }
    
    private static void retrieveTestSets(RallyRestApi restApi)
			throws IOException, URISyntaxException, ParseException {

        QueryRequest testSetRequest = new QueryRequest("TestSet");
        
        //testSetRequest.setProject("/project/21028059357"); //2-12
        testSetRequest.setProject("/project/23240411122"); //K1
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);

        testSetRequest.setFetch(new Fetch(new String[] {"Name", "Description", "TestCases", "Results", "FormattedID", "LastVerdict", "LastBuild", "LastRun", "Priority", "Method"}));
        String testSetsString = "TS722,TS723,TS724,TS725,TS726,TS727,TS728,TS729,TS730,TS731,TS732";
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
                	  	JsonArray results = jsonObject.get("Results").getAsJsonArray();
                	  	//testSetResultExists(restApi, testSetsString, results);
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
    
    private static void retrieveTestFolder(RallyRestApi restApi) throws IOException {
		QueryFilter queryFilter = new QueryFilter("FormattedID", "=", "TC49355");
    	QueryRequest defectRequest = new QueryRequest("testcases");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("FormattedID", "TestFolder"));
    	defectRequest.setProject("/project/11052443367"); 
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(10000);
    	QueryResponse projectDefects = restApi.query(defectRequest);
    	JsonArray defectsArray = projectDefects.getResults();
    
    	for(int i=0; i<defectsArray.size(); i++) {
    		JsonElement elements =  defectsArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
            System.out.println(i+" "+object);
            JsonObject testSets = object.get("TestFolder").getAsJsonObject();
            System.out.println(testSets.get("_ref"));
    	}
	}
    
    private static void retrieveTestSetsResult(RallyRestApi restApi)
			throws IOException, URISyntaxException, ParseException {

        QueryRequest testSetRequest = new QueryRequest("TestSet");
        
        //testSetRequest.setProject("/project/21028059357"); //2-12
        testSetRequest.setProject("/project/23240411122"); //K1
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);

        testSetRequest.setFetch(new Fetch(new String[] {"Name", "TestCases", "Results", "FormattedID", "LastVerdict", "LastBuild", "LastRun", "Priority", "Method"}));
        String testSetsString = "TS722";
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
                	  	JsonArray results = jsonObject.get("Results").getAsJsonArray();
                	  	String verdict = testSetResult(restApi, jsonObject.get("FormattedID").getAsString(), testSetJsonObject.get("FormattedID").getAsString());
                	  	if(verdict != null) {
                	  		System.out.println((ij)+"\t"+ jsonObject.get("FormattedID") +"\t" + verdict +"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Method"));
                	  	} else {
                	  		System.out.println((ij)+"\t"+ jsonObject.get("FormattedID") +"\t" + "" +"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Method"));
                	  	}
                	  	ij++;
                 }
            }
        }

	}
    
    private static String testSetResult(RallyRestApi restApi, String testCaseId, String testSetId) throws IOException {
    	QueryRequest testCaseResultsRequest = new QueryRequest("TestCaseResult");
        testCaseResultsRequest.setFetch(new Fetch("Build","TestCase","TestSet", "Verdict","FormattedID"));
        testCaseResultsRequest.setQueryFilter(new QueryFilter("TestCase.FormattedID", "=", testCaseId).and(
                new QueryFilter("TestSet.FormattedID", "=", testSetId)));
        QueryResponse testCaseResultResponse = restApi.query(testCaseResultsRequest);
        int numberTestCaseResults = testCaseResultResponse.getTotalResultCount();
        if(numberTestCaseResults >0)
            return testCaseResultResponse.getResults().get(0).getAsJsonObject().get("Verdict").getAsString();
        else
            return null;
    }
    
    private static String testSetResultExists(RallyRestApi restApi, String testSetName, JsonArray results) throws IOException {
    	int numberOfTestCaseResults = results.size();
    	for (int j=0; j<numberOfTestCaseResults; j++){
    		JsonObject testResult = results.get(j).getAsJsonObject();
    		String ref = testResult.get("_ref").getAsString();
    		GetRequest testCaseResultRequest = new GetRequest(ref.substring(ref.indexOf("/testcaseresult/")));
    	    GetResponse testCaseResultResponse = restApi.get(testCaseResultRequest);
    	    JsonObject testCaseResultObj = testCaseResultResponse.getObject();
    	    if(!testCaseResultObj.get("TestSet").isJsonNull()) {
    	    	JsonObject testSetInResult = testCaseResultObj.get("TestSet").getAsJsonObject();
    	    	if(testSetInResult.get("_refObjectName").getAsString().equalsIgnoreCase(testSetName))
    	    	{
    	    		return testCaseResultObj.get("Verdict").getAsString();
    	    	}
    	    }
    	}
    	return null;
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

