package com.gapi.testCases;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetAsset extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/assets";

	
		//GAPI-5317-Retrieve Asset list by Customer using Account id,ParentId,Site code&Type

	    @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getAssetInformation(Map<String, String> data) throws InterruptedException {
	    	
			logger = extent.createTest(data.get("TestCaseName"));
			String assetFilters = data.get("filters");
			System.out.println(assetFilters);
			String apiURI = idpdomain+ path +"?"+assetFilters;
			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer " + Tokenidp)
	 				.header("Content-Type", "application/json")
	 				.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
	 				.header("User-Email","phantom.aao.dlr@gmail.com")
	 				.get(apiURI);  
			System.out.println(response.asString());
			logger.pass("Get Asset Info" + response.asString());
			logger.log(Status.INFO, response.statusLine());
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("OK"));
			log.info("Successfully extracted Asset Info ");
	 		logger.log(Status.INFO, "Successfully extracted Asset Info" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}

}
