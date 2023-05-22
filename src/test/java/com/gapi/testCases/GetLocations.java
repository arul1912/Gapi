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
import io.restassured.http.Headers;
import io.restassured.response.Response;

public class GetLocations extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/locations";

	
	
	//GAPI-5388-Retrieve Locations for Customer - Markets using account id &Region

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getLocationsInformation(Map<String, String> data) throws InterruptedException {
		
	
	logger = extent.createTest(data.get("TestCaseName"));
	String getLocationsFilters = data.get("filters");
	System.out.println(getLocationsFilters);
	String apiURI = domain+ path +getLocationsFilters;
		System.out.println(apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
 				.header("Content-Type", "application/json")
 				.header("Master-Account-Id", masterAccountID)
                .header("Account-Id", accountID)
 				.header("User-Email","phantom.aao.dlr@gmail.com")
 				.get(apiURI);  
		System.out.println(response.asString());
		logger.pass("Displayed Locations are" + response.asString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		//Assert.assertTrue(response.statusLine().contains("OK"));
		Assert.assertTrue(response.asString().contains("id"));
		Assert.assertTrue(response.asString().contains("parentId"));
		Assert.assertTrue(response.asString().contains("location"));
		/*
		 * Assert.assertTrue(response.asString().contains("createTimestamp"));
		 * Assert.assertTrue(response.asString().contains("modifiedTimestamp"));
		 * Assert.assertTrue(response.asString().contains("assetName"));
		 * Assert.assertTrue(response.asString().contains("type"));
		 * Assert.assertTrue(response.asString().contains("legalEntityKey"));
		 * Assert.assertTrue(response.asString().contains("sitecode"));
		 * Assert.assertTrue(response.asString().contains("datasource"));
		 * Assert.assertTrue(response.asString().contains("status"));
		 * Assert.assertTrue(response.asString().contains("internalCompanyFlag"));
		 */
 		logger.log(Status.INFO, "Successfully extracted Locations Info" );
 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
}
	//Retrieve locations details with filter
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void getLocationswithFilter(Map<String, String> data) throws InterruptedException, IOException {

				logger = extent.createTest(data.get("TestCaseName"));
				String locationFilters = data.get("filters");
				String apiURI = domain + "/locations?" + locationFilters;
				System.out.println(apiURI);
				Headers headers = tc.sendMultipleHeadersforGetAllAssets();
				Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
						.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID)
						.header("User-Email", "phantom.aao.dlr@gmail.com").get(apiURI).then().extract().response();

				Assert.assertEquals(response.getStatusCode(), 200);
				System.out.println(response.asString());

				HashMap<String, String> filtersMap = new HashMap<>();
				locationFilters = locationFilters.replace("?", "");
				if (locationFilters.contains("&")) {
					String[] values = locationFilters.split("&");
					for (int j = 0; j < values.length; j++) {
						String[] keyPair = values[j].split("=");
						filtersMap.put(keyPair[0], keyPair[1]);
					}
				} else {
					String[] keyPair = locationFilters.split("=");
					filtersMap.put(keyPair[0], keyPair[1]);
				}
				 

				Assert.assertEquals(response.getStatusCode(), 200);
				JSONArray jsonArray = new JSONArray(response.asString());

				if (locationFilters.contains("&")) {
					String[] values = locationFilters.split("&");
					for (int j = 0; j < values.length; j++) {
						String[] keyPair = values[j].split("=");
						filtersMap.put(keyPair[0], keyPair[1]);
					}
				} else {
					String[] keyPair = locationFilters.split("=");
					filtersMap.put(keyPair[0], keyPair[1]);
				}

				if (!(data.get("TestCaseName").contains("Invalid"))) {
					for (int index = 0; index < jsonArray.length(); index++) {
						if (locationFilters.contains("sitecodes")) {
							Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("sitecode"),
									filtersMap.get("sitecodes"));
					}
					log.info("Successfully Retreieved Locations using Valid account id and Site Code ");
					logger.log(Status.PASS, "Successfully Retreieved Locations using Valid account id and Site Code");
					logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

				}
				}

				else {
//					Assert.assertTrue(response.asString().contains("[]"));
					log.info("Retreieval of Locations using Invalid account id or Site Code was Successfull");
					logger.log(Status.PASS, "Retreieval of Locations using Invalid account id or Site Code was Successfull");
					logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
				}
			}

	}





