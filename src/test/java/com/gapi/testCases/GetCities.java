package com.gapi.testCases;

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

public class GetCities extends BaseClass{
	
	static TestUtilities tc = new TestUtilities();
	public static String path = "/sites/cities";	
	
		
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getCitiesInformation(Map<String, String> data) throws InterruptedException {
			
		
		logger = extent.createTest(data.get("TestCaseName"));
		String getCitiesFilters = data.get("filters");
		System.out.println(getCitiesFilters);
		String apiURI = idpdomain+ path +getCitiesFilters;

			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Tokenidp)
	 				.header("Content-Type", "application/json")
	 				.header("Master-Account-Id", masterAccountID)
	                .header("Account-Id", accountID)
	 				.header("User-Email","phantom.aao.dlr@gmail.com")
	 				.get(apiURI);
	
			System.out.println(response.getBody().asPrettyString());
			logger.pass("Displayed Cities are" + response.getBody().asPrettyString());
			logger.log(Status.INFO, response.statusLine());
			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
			Assert.assertTrue(response.asString().contains("city"));
	 		logger.log(Status.INFO, "Successfully extracted Cities Info" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}
	
	//Retrieve Cities Information based on different filters
	//GAPI-5386-Retrieve Locations for Customer - Cities using account id,Market &Country
	//GAPI-5384-Retrieve Locations for Customer - Cities using account id,Region &Market
	//GAPI-5387-Retrieve Locations for Customer - Cities using account id,Region,Country &Market
	//GAPI-5383-Retrieve Locations for Customer - Cities using account id &Country
	//GAPI-5382-Retrieve Locations for Customer - Cities using account id &Region
	//GAPI-5385-Retrieve Locations for Customer - Cities using account id,Region&Country

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getCitiesInformationUsingDifferentFilters(Map<String, String> data) throws InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			String citiesFilters = data.get("filters");
			//System.out.println(citiesFilters);
			String apiURI = idpdomain + path + citiesFilters;
			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
					.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID).header("User-Email", "phantom.aao.dlr@gmail.com")
					.get(apiURI);

			HashMap<String, String> filtersMap = new HashMap<>();
			if (citiesFilters.contains("&")) {
				String[] values = citiesFilters.split("&");
				for (int j = 0; j < values.length; j++) {
					String[] keyPair = values[j].split("=");
					filtersMap.put(keyPair[0], keyPair[1]);
				}
			} else {
				String[] keyPair = citiesFilters.split("=");
				filtersMap.put(keyPair[0], keyPair[1]);
			}
			System.out.println(response.getBody().asPrettyString());
			logger.pass("Displayed Cities are" + response.getBody().asPrettyString());
			Assert.assertEquals(response.getStatusCode(), 200);
			JSONArray jsonArray = new JSONArray(response.asString());
			Assert.assertTrue(response.asString().contains("city"));
			boolean status = false;

			if (citiesFilters.contains("&")) {
				String[] values = citiesFilters.split("&");
				for (int j = 0; j < values.length; j++) {
					String[] keyPair = values[j].split("=");
					filtersMap.put(keyPair[0], keyPair[1]);
				}
			} else {
				String[] keyPair = citiesFilters.split("=");
				filtersMap.put(keyPair[0], keyPair[1]);
			}
			for (int index = 0; index < jsonArray.length(); index++) {
				if (citiesFilters.contains("market")) {
					Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("market"), filtersMap.get("market"));
					status = true;
				}
				
				if (citiesFilters.contains("country")) {
					Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("country"), filtersMap.get("country"));
					status = true;
				}
				if (citiesFilters.contains("region")) {
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
