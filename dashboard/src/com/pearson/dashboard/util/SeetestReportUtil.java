package com.pearson.dashboard.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

public class SeetestReportUtil {

	public static void uploadSeetestReport(File seetestHtmlFile,
			String tabDelimitedFile, String testSetId) {
		File file = new File(tabDelimitedFile);
		try {
			file.createNewFile();
			OutputStream out = new FileOutputStream(file);
			Document doc = Jsoup.parse(seetestHtmlFile, "UTF-8");
			Elements tableElements = doc.select("table");
			Elements tableRowElements = tableElements.select(":not(thead) tr");
			String row = "";
			for (int i = 0; i < tableRowElements.size(); i++) {
				Element eRow = tableRowElements.get(i);
				Elements rowItems = eRow.select("td");
				if (rowItems.size() > 4) {
					String cellText = rowItems.get(1).text();
					System.out.println(cellText);
					int firstIndex = cellText.indexOf(":");
					String testCaseId = cellText.substring(0, firstIndex);
					if (testCaseId.contains("&")) {
						String testCasesIds[] = testCaseId.split("&");
						for (String testId : testCasesIds) {
							testId = testId.trim();
							if (testId.contains(",")) {
								String testds2[] = testId.split(",");
								for (String testId1 : testds2) {
									testId1 = testId1.trim();
									String testCaseStatus = rowItems.get(2)
											.text();
									if (testCaseStatus.startsWith("Pass")) {
										testCaseStatus = "Pass";
									} else {
										testCaseStatus = "Fail";
									}
									if (row.equals("")) {
										row = testSetId + "\t" + testId1 + "\t"
												+ testCaseStatus;
									} else {
										row += "\n" + testSetId + "\t"
												+ testId1 + "\t"
												+ testCaseStatus;
									}
								}
							} else {
								String testCaseStatus = rowItems.get(2).text();
								if (testCaseStatus.startsWith("Pass")) {
									testCaseStatus = "Pass";
								} else {
									testCaseStatus = "Fail";
								}
								if (row.equals("")) {
									row = testSetId + "\t" + testId + "\t"
											+ testCaseStatus;
								} else {
									row += "\n" + testSetId + "\t" + testId
											+ "\t" + testCaseStatus;
								}
							}
						}
					} else if (testCaseId.contains(",")) {
						String testCasesIds[] = testCaseId.split(",");
						for (String testId : testCasesIds) {
							testId = testId.trim();
							String testCaseStatus = rowItems.get(2).text();
							if (testCaseStatus.startsWith("Pass")) {
								testCaseStatus = "Pass";
							} else {
								testCaseStatus = "Fail";
							}
							if (row.equals("")) {
								row = testSetId + "\t" + testId + "\t"
										+ testCaseStatus;
							} else {
								row += "\n" + testSetId + "\t" + testId + "\t"
										+ testCaseStatus;
							}
						}
					} else {
						testCaseId = testCaseId.trim();
						String testCaseStatus = rowItems.get(2).text();
						if (testCaseStatus.startsWith("Pass")) {
							testCaseStatus = "Pass";
						} else {
							testCaseStatus = "Fail";
						}
						if (row.equals("")) {
							row = testSetId + "\t" + testCaseId + "\t"
									+ testCaseStatus;
						} else {
							row += "\n" + testSetId + "\t" + testCaseId + "\t"
									+ testCaseStatus;
						}
					}
				}
			}
			out.write(row.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void updateTestCaseResults(RallyRestApi restApi,
			String buildNumber, String userName, String path)
			throws IOException {
		File file = new File(path);
		Date curDate = new Date();
		Scanner sc = new Scanner(new FileReader(file));
		while (sc.hasNextLine()) {
			String words[] = sc.nextLine().split("\t");
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss" + ".000Z");
			String dateToStr = format.format(curDate);
			updateTestCase(restApi, words[0], words[1], words[2],
					dateToStr, buildNumber, userName);
		}
		sc.close();
		file.deleteOnExit();
	}

	private static void updateTestCase(RallyRestApi restApi, String testSet,
			String testCase, String status, String date, String build,
			String userId) throws IOException {
		QueryRequest userRequest = new QueryRequest("User");
		userRequest.setFetch(new Fetch("UserName", "Subscription",
				"DisplayName", "SubscriptionAdmin"));
		userRequest.setQueryFilter(new QueryFilter("UserName", "=", userId));
		QueryResponse userQueryResponse = restApi.query(userRequest);
		JsonArray userQueryResults = userQueryResponse.getResults();
		JsonElement userQueryElement = userQueryResults.get(0);
		JsonObject userQueryObject = userQueryElement.getAsJsonObject();
		String userRef = userQueryObject.get("_ref").getAsString();

		String wsapiVersion = "1.43";
		restApi.setWsapiVersion(wsapiVersion);

		QueryRequest testSetRequest = new QueryRequest("TestSet");
		testSetRequest.setQueryFilter(new QueryFilter("FormattedID", "=",
				testSet));
		QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
		String testSetRef = testSetQueryResponse.getResults().get(0)
				.getAsJsonObject().get("_ref").getAsString();

		String testCaseRef = "";
		QueryRequest testCaseRequest = new QueryRequest("TestCase");
		testCaseRequest.setFetch(new Fetch("FormattedID", "Name"));
		testCaseRequest.setQueryFilter(new QueryFilter("FormattedID", "=",
				testCase));
		QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
		if (null != testCaseQueryResponse.getResults()
				&& testCaseQueryResponse.getResults().size() > 0) {
			testCaseRef = testCaseQueryResponse.getResults().get(0)
					.getAsJsonObject().get("_ref").getAsString();

			if (null != testCaseRef && !testCaseRef.equals("")) {
				JsonObject newTestCaseResult = new JsonObject();
				newTestCaseResult.addProperty("Verdict", status);
				newTestCaseResult.addProperty("Date", date);
				newTestCaseResult.addProperty("Build", build);
				newTestCaseResult.addProperty("TestCase", testCaseRef);
				newTestCaseResult.addProperty("Tester", userRef);
				newTestCaseResult.addProperty("TestSet", testSetRef);

				CreateRequest createRequest = new CreateRequest(
						"testcaseresult", newTestCaseResult);
				CreateResponse createResponse = restApi.create(createRequest);
				if (createResponse.wasSuccessful()) {
					System.out.println(String.format("Created %s",
							createResponse.getObject().get("_ref")
									.getAsString()));
				} else {
					System.out
							.println("Error occurred creating Test Case Result: "
									+ createResponse.getErrors());
				}
			} else {
				System.out.println("Test Case doesn't exist");
			}
		} else {
			System.out.println("Test Case doesn't exist");
		}
	}
	
	public static RallyRestApi loginRally() throws URISyntaxException {
    	String rallyURL = "https://rally1.rallydev.com";
     	String myUserName = "mohammed.saquib@pearson.com";
     	String myUserPassword = "Rally@123";
     	return new RallyRestApi(new URI(rallyURL), myUserName, myUserPassword);
    }
}
