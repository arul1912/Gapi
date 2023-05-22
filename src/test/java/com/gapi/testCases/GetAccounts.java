package com.gapi.testCases;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetAccounts extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/accounts";

	//GAPI-6198	A- Verify the accounts by valid Legal entity Key
	//GAPI-5756	U- Account Search using accountName


		
	    @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getAccountsInformation(Map<String, String> data) throws InterruptedException {
	    	
			
			logger = extent.createTest("getAccountsInformation");
			/*
			 * String assetFilters = data.get("filters"); System.out.println(assetFilters);
			 */
			String apiURI = domain+ path ;
			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization","Bearer "+Token)
	 				.header("Content-Type", "application/json")
	 				.header("Master-Account-Id",masterAccountID)
	 				.header("Account-Id",accountID)
	 				.header("User-Email","msirikonda@digitalrealty.com")
	 				.get(apiURI);  
			logger.log(Status.INFO, response.statusLine());
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode  = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			//Assert.assertEquals(response.getStatusCode(), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains("OK"));
			
			log.info("Successfully extracted all Accounts Info ");
			logger.log(Status.INFO, "Successfully extracted all Accounts Info");
			logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}

}