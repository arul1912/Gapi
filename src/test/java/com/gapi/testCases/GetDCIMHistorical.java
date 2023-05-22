package com.gapi.testCases;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetDCIMHistorical extends BaseClass{
	static TestUtilities tc = new TestUtilities();


	
	
		
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getDCIMDataPointValues(Map<String, String> data) throws InterruptedException {
			
		logger = extent.createTest(data.get("TestCaseName"));
		String getFiltersData = data.get("filters");
		System.out.println(getFiltersData);

//		String sysparm_fields = data.get("sysparm_fields");
//		String sysparm_query=data.get("sysparm_query");
//		System.out.println(sysparm_fields);
//		System.out.println(sysparm_query);
		String apiURI = domain  +"?"+getFiltersData;
		System.out.println(apiURI);
		Response response = RestAssured.given()
				.header("Authorization",Token)
 				.header("Content-Type", "application/json")
 				.header("Master-Account-Id","0012E00002dn2USQAT")
 				.header("Account-Id","66")
 				.header("User-Email","phantom.aao.dlr@gmail.com")
	 				.get(apiURI);  
		System.out.println(response.asString());
		logger.pass("Displayed DCIM DataPoint Values::" + response.asString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
	 	logger.log(Status.INFO, "Successfully extracted DCIM DataPoint Values Data" );
	 	logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}

}
