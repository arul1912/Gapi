package com.gapi.testCases;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetMarkets extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/sites/markets";

	
		
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getMarketsInformation(Map<String, String> data) throws InterruptedException {
			
		
		logger = extent.createTest(data.get("TestCaseName"));
		String getMarketsFilters = data.get("filters");
		System.out.println(getMarketsFilters);
		String apiURI = idpdomain+ path +getMarketsFilters;

//	    	logger = extent.createTest("getMarketsInformation");
//			String apiURI = domain+"/v1/sites/markets?accountId=0012E00002dmMWbQAM";
			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Tokenidp)
	 				.header("Content-Type", "application/json")
	 				.header("Master-Account-Id", masterAccountID)
	                .header("Account-Id", accountID)
	 				.header("User-Email","phantom.aao.dlr@gmail.com")
	 				.get(apiURI);  
			System.out.println(response.asString());
			logger.pass("Displayed Markets are" + response.asString());
			logger.log(Status.INFO, response.statusLine());
			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
			Assert.assertTrue(response.asString().contains("market"));
	 		logger.log(Status.INFO, "Successfully extracted Markets Info" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}

}
