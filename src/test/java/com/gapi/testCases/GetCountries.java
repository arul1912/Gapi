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

public class GetCountries extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/sites/countries";

	
	
		
    	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getCountriesInformation(Map<String, String> data) throws InterruptedException {
			
    		
    		logger = extent.createTest(data.get("TestCaseName"));
			String getCountriesFilters = data.get("filters");
			System.out.println(getCountriesFilters);
			String apiURI = idpdomain+ path +getCountriesFilters;
			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Tokenidp)
	 				.header("Content-Type", "application/json")
	 				.header("Master-Account-Id", masterAccountID)
	                .header("Account-Id", accountID)
	 				.header("User-Email","phantom.aao.dlr@gmail.com")
	 				.get(apiURI);  
			System.out.println(response.asString());
			logger.pass("Displayed Countries are" + response.asString());
			logger.log(Status.INFO, response.statusLine());
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			
			 	
			 Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	 		logger.log(Status.INFO, "Successfully extracted Countries Information" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}
    	
    	//Countrie info using different filters
    	//GAPI-5381-Retrieve Locations for Customer - Countries using account id &Region

    	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getCountriesInformationusingfilters(Map<String, String> data) throws InterruptedException {
			
    		
    		logger = extent.createTest(data.get("TestCaseName"));
			String CountriesFilters = data.get("filters");
			String apiURI = idpdomain+ path +CountriesFilters;
			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Tokenidp)
	 				.header("Content-Type", "application/json")
	 				.header("Master-Account-Id", masterAccountID)
	                .header("Account-Id", accountID)
	 				.header("User-Email","phantom.aao.dlr@gmail.com")
	 				.get(apiURI);  
			
			HashMap<String, String> filtersMap = new HashMap<>();
			if (CountriesFilters.contains("&")) {
				String[] values = CountriesFilters.split("&");
				for (int j = 0; j < values.length; j++) {
					String[] keyPair = values[j].split("=");
					filtersMap.put(keyPair[0], keyPair[1]);
				}
			} else {
				String[] keyPair = CountriesFilters.split("=");
				filtersMap.put(keyPair[0], keyPair[1]);
			}
			System.out.println(response.getBody().asPrettyString());
			logger.pass("Displayed Country are" + response.getBody().asPrettyString());
			log.info("Displayed Country are" + response.getBody().asPrettyString());
			Assert.assertEquals(response.getStatusCode(), 200);
			JSONArray jsonArray = new JSONArray(response.asString());
			Assert.assertTrue(response.asString().contains("country"));
			boolean status = false;
			Map<String, String> objMap = new HashMap<String, String>();
			if (CountriesFilters.contains("&")) {
				String[] values = CountriesFilters.split("&");
				for (int j = 0; j < values.length; j++) {
					String[] keyPair = values[j].split("=");
					filtersMap.put(keyPair[0], keyPair[1]);
				}
			} else {
				String[] keyPair = CountriesFilters.split("=");
				filtersMap.put(keyPair[0], keyPair[1]);
			}
			for (int index = 0; index < jsonArray.length(); index++) {
				
				if (CountriesFilters.contains("region")) {
					Assert.assertEquals(response.getBody().jsonPath().getString("content[" + index + "].region"),
							objMap.get("region"));
				}
			}
				logger.log(Status.INFO, "Successfully extracted Countries Info");
				logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

				logger.log(Status.PASS, "Response details Matched with the given filter criteria");
			}

}

