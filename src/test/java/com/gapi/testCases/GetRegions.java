package com.gapi.testCases;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetRegions extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/sites/regions";

		
    	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getRegionsInformation(Map<String, String> data) throws InterruptedException {
			
    		
    		logger = extent.createTest(data.get("TestCaseName"));
			String getRegionsFilters = data.get("filters");
			System.out.println(getRegionsFilters);
			String apiURI = idpdomain+ path +getRegionsFilters;
			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Tokenidp)
	 				.header("Content-Type", "application/json")
	 				.header("Master-Account-Id", masterAccountID)
	                .header("Account-Id", accountID)
	 				.header("User-Email","phantom.aao.dlr@gmail.com")
	 				.get(apiURI);  
			System.out.println(response.asString());
			logger.pass("Displayed Regions are" + response.asString());
			System.out.println(response.statusLine());
	 		Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
			Assert.assertTrue(response.asString().contains("region"));
			logger.log(Status.INFO, "Successfully extracted Regions Info" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}
		
		

}
