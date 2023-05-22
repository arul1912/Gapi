package com.gapi.testCases;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetCampus extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/sites/campuses";	
		
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getCampusInformation(Map<String, String> data) throws InterruptedException, IOException {
			
		logger = extent.createTest(data.get("TestCaseName"));
		String getCampusessFilters = data.get("filters");
		System.out.println(getCampusessFilters);
		String apiURI = idpdomain+ path +getCampusessFilters;
			System.out.println(apiURI);
			Response response = tc.getRequest(Tokenidp,masterAccountID,accountID,apiURI);
			System.out.println(response.getBody().asPrettyString());
			logger.pass("Displayed Campuses are" + response.getBody().asPrettyString());
			logger.log(Status.INFO, response.statusLine());
			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
			Assert.assertTrue(response.asString().contains("campus"));
	 		logger.log(Status.INFO, "Successfully extracted Campus Info" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}
	      //Retrieve Campus Information based on different filters
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void getCampusInformationUsingDifferentFilters(Map<String, String> data) throws InterruptedException, IOException {

				logger = extent.createTest(data.get("TestCaseName"));
				String campusessFilters = data.get("filters");
				System.out.println(campusessFilters);
				String apiURI = idpdomain + path + campusessFilters;
				System.out.println(apiURI);
				Response response = tc.getRequest(Tokenidp,masterAccountID,accountID,apiURI);
				HashMap<String, String> filtersMap = new HashMap<>();
				if (campusessFilters.contains("&")) {
					String[] values = campusessFilters.split("&");
					for (int j = 0; j < values.length; j++) {
						String[] keyPair = values[j].split("=");
						filtersMap.put(keyPair[0], keyPair[1]);
					}
				} else {
					String[] keyPair = campusessFilters.split("=");
					filtersMap.put(keyPair[0], keyPair[1]);
				}
				System.out.println(response.getBody().asPrettyString());
				logger.pass("Displayed Campuses are" + response.getBody().asPrettyString());
				log.info("Displayed Campuses are" + response.getBody().asPrettyString());
				Assert.assertEquals(response.getStatusCode(), 200);
				JSONArray jsonArray = new JSONArray(response.asString());
				Assert.assertTrue(response.asString().contains("campus"));
				boolean status = false;

				if (campusessFilters.contains("&")) {
					String[] values = campusessFilters.split("&");
					for (int j = 0; j < values.length; j++) {
						String[] keyPair = values[j].split("=");
						filtersMap.put(keyPair[0], keyPair[1]);
					}
				} else {
					String[] keyPair = campusessFilters.split("=");
					filtersMap.put(keyPair[0], keyPair[1]);
				}
				for (int index = 0; index < jsonArray.length(); index++) {
					if (campusessFilters.contains("market")) {
						Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("market"), filtersMap.get("market"));
						status = true;
					}
					if (campusessFilters.contains("city")) {
						Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("city"), filtersMap.get("city"));
						status = true;
					}
					if (campusessFilters.contains("country")) {
						Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("country"), filtersMap.get("country"));
						status = true;
					}
					if (campusessFilters.contains("region")) {
						Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("region"), filtersMap.get("region"));
						status = true;
					}
				}

				logger.log(Status.INFO, "Successfully extracted Campus Info");
				logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
				if (status == true) {
					log.info("Validated Campus Filters Successfully");
					logger.log(Status.PASS, "Validated Campus Filters Successfully ");
				} else {
					logger.log(Status.FAIL, "Validation is not Successfull with Campus Filters ");
					log.info("Validation is not Successfull with Campus Filters");
				}
			}
}
