package com.gapi.testCases;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetDCIMDataPointValues extends BaseClass{
	static TestUtilities tc = new TestUtilities();


	
	
		
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getDCIMDataPointValues(Map<String, String> data) throws InterruptedException {
			
		logger = extent.createTest(data.get("TestCaseName"));
		String getFiltersData = data.get("filters");
		System.out.println(getFiltersData);
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


//GAPI-8717-DCIM - Retrieve DDIDs information using valid profile & ddids
//GAPI-8718-DCIM - Retrieve DDIDs information using Invalid profile & valid ddids
//GAPI-8719-DCIM - Retrieve DDIDs information using valid profile & Invalid ddids
//GAPI-8846-DCIM -RBAC-  Retrieval of  DCIM Information Having Role/Permission to view DCIM enabled location data

@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getDCIMInformation(Map<String, String> data) throws InterruptedException {
	
	testData=data;
	logger = extent.createTest(data.get("TestCaseName"));
	String getFiltersData = data.get("filters");
	System.out.println(getFiltersData);
	String apiURI = CCdomain  +"/dcim?"+getFiltersData;
	System.out.println(apiURI);
	Response response = RestAssured.given()
			.header("Authorization",Token)
			.header("Content-Type", "application/json")
			.header("Master-Account-Id","0012E00002mgd1dQAA")
			.header("Account-Id","0012E00002dmMWbQAM")
			.header("User-Email","phantom.aao.dlr@gmail.com")
			.get(apiURI);  
	
	System.out.println(response.asString());
	if (response.getStatusCode()==200) {
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusCode = data.get("expectedStatusCode");
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		logger.pass("Displayed DCIM DataPoint Values::" + response.asString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
	 	logger.log(Status.INFO, "Successfully extracted DCIM Information" );
	 	logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	}else {
		logger.pass("Error Response message is " + response.asString());
		String expectedErrorMessageType = data.get("errorMessageType");
		String expectedErrorMessage = data.get("errorMessage");
		tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		logger.pass("Error Response message is Verified");
		}
	}

//GAPI-8922-DCIM - Retrieve DCIM info by using APIGEE Authentication

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getDCIMAPigeeToken(Map<String, String> data) throws InterruptedException {
	
		testData=data;
		logger = extent.createTest(data.get("TestCaseName"));
		String getFiltersData = data.get("filters");
		System.out.println(getFiltersData);
		String apiURI = CCdomain  +"/dcim?"+getFiltersData;
		System.out.println(apiURI);
		Response response = RestAssured.given()
			.header("Authorization",createApigeeToken)
			.header("Content-Type", "application/json")
			.header("Master-Account-Id","0012E00002mgd1dQAA")
			.header("Account-Id","0012E00002dmMWbQAM")
			.header("User-Email","phantom.aao.dlr@gmail.com")
			.get(apiURI);  
	
		System.out.println(response.asString());
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusCode = data.get("expectedStatusCode");
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		logger.pass("Displayed DCIM DataPoint Values::" + response.asString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
	 	logger.log(Status.INFO, "Successfully extracted DCIM Information" );
	 	logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	}

//GAPI-8722-DCIM - Retrieve DDIDs information using different subset values

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getDCIMSubset(Map<String, String> data) throws InterruptedException {
	
	testData=data;
	logger = extent.createTest(data.get("TestCaseName"));
	String getFiltersData = data.get("filters");
	System.out.println(getFiltersData);
	Response response=null;
	for(int i=0;i<=2;i++) {
	String apiURI = CCdomain  +"/dcim?"+getFiltersData+i;
	System.out.println(apiURI);
	response = RestAssured.given()
			.header("Authorization",Token)
			.header("Content-Type", "application/json")
			.header("Master-Account-Id","0012E00002mgd1dQAA")
			.header("Account-Id","0012E00002dmMWbQAM")
			.header("User-Email","phantom.aao.dlr@gmail.com")
			.get(apiURI);  
	System.out.println(response.asString());
	Assert.assertEquals(response.getStatusCode(), 200);
	Assert.assertTrue(response.statusLine().contains("OK"));
	}
	logger.pass("Displayed DCIM DataPoint Values::" + response.asString());
	logger.log(Status.INFO, response.statusLine());
	logger.log(Status.INFO, "Successfully extracted DCIM DataPoint Values Data" );
	logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	}

//GAPI-8721-DCIM -DCIM - Retrieve DDIDs information using invalid subset values.

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getDCIMInvalidSubset(Map<String, String> data) throws InterruptedException {
	
	testData=data;
	logger = extent.createTest(data.get("TestCaseName"));
	String getFiltersData = data.get("filters");
	System.out.println(getFiltersData);
	String apiURI = CCdomain  +"/dcim?"+getFiltersData;
	System.out.println(apiURI);
	Response response = RestAssured.given()
			.header("Authorization",Token)
			.header("Content-Type", "application/json")
			.header("Master-Account-Id","0012E00002mgd1dQAA")
			.header("Account-Id","0012E00002dmMWbQAM")
			.header("User-Email","phantom.aao.dlr@gmail.com")
			.get(apiURI);  
	
	System.out.println(response.asString());
	logger.log(Status.INFO, response.statusLine());
	Assert.assertEquals(response.getStatusCode(), 200);
	Assert.assertTrue(response.statusLine().contains("OK"));
	logger.log(Status.INFO, "Successfully extracted DCIM DataPoint Values Data" );
	logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	}
}