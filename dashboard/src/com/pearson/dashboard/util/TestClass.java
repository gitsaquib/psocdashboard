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
    	//updateTestCase(restApi, "TC23269,TC23270,TC23272,TC15948,TC22133,TC15857,TC15873,TC15889,TC15892,TC15906,TC15908,TC15922,TC15981,TC15982,TC15983,TC15984,TC15985,TC16068,TC16111,TC16194,TC16195,TC17677,TC19114,TC19367,TC19370,TC19455,TC19456,TC19458,TC19459,TC19461,TC19462,TC19465,TC20021,TC20024,TC20026,TC20027,TC20028,TC20029,TC20073,TC20074,TC20075,TC20189,TC20192,TC20195,TC20202,TC20216,TC20477,TC20483,TC20515,TC21851,TC15859,TC15864,TC15874,TC15878,TC15881,TC15886,TC15887,TC15899,TC15900,TC15909,TC15966,TC15967,TC15968,TC15969,TC15970,TC15971,TC15972,TC15974,TC15980,TC16087,TC16090,TC16091,TC16092,TC16108,TC16109,TC16112,TC16113,TC17664,TC17665,TC17670,TC17674,TC17679,TC17682,TC17687,TC17689,TC17696,TC18572,TC19361,TC19373,TC19374,TC19460,TC20038,TC20039,TC20098,TC20440,TC21705,TC21706,TC21707,TC21708,TC21728,TC21732,TC21827,TC21840,TC21841,TC15877,TC15921,TC15932,TC17668,TC19384,TC21736,TC21737,TC21738,TC21760,TC15943,TC15954,TC16059,TC16062,TC16063,TC20271,TC20272,TC18604,TC18607,TC18673,TC19299,TC21759,TC23148,TC23149,TC23150,TC23151,TC19953,TC25148,TC25491,TC26212,TC26213,TC25838,TC26215,TC26216,TC26217,TC16019,TC16088,TC22477,TC22487,TC22760,TC22765,TC22766,TC22867,TC22868,TC23147,TC23153,TC23154,TC23924,TC23928,TC23936,TC23937,TC23938,TC23939,TC24163,TC24164,TC24165,TC24166,TC24356,TC25088,TC25091,TC25092,TC25093,TC25094,TC25096,TC25097,TC25112,TC25113,TC25114,TC25115,TC25116,TC25117,TC25118,TC25119,TC25121,TC25123,TC25125,TC25128,TC25129,TC25135,TC25140,TC25141,TC25142,TC25166,TC25168,TC25472,TC25818,TC25821,TC26299,TC26311,TC27064,TC29892,TC29893,TC29894,TC29895,TC29898,TC29899,TC29900,TC29902,TC29903,TC29904,TC29897,TC29901,TC29896,TC29936,TC29937,TC31230,TC31602,TC31603,TC27139,TC27142,TC27145,TC27213,TC27214,TC27215,TC27216,TC28346,TC31809,TC31810,TC27166,TC27175,TC27167,TC27165,TC14961,TC29823,TC27473,TC27450,TC28364,TC27470,TC27469,TC27224,TC29925,TC29924,TC31841,TC27222,TC28367,TC27472,TC27471,TC27449,TC27219,TC30112,TC31820,TC20269,TC20270,TC20267,TC23169,TC23261,TC20376,TC22054,TC23165,TC43978,TC44276,TC44514");
    	retrieveTestSets(restApi);
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
		        newTestCaseResult.addProperty("Date", "2015-05-01T17:00:00.000Z");
		        newTestCaseResult.addProperty("Build", "1.6.0.433");
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
        testSetRequest.setProject("/project/21028059357");
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

