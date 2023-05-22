package com.gapi.testCases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class CreateIdentityServerIDPToken extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/connect/token";

	static String date = tc.getCurrentDateAndTime();
	


//	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
//		public void CreateIdentityServerIDPToken(Map<String, String> data) throws InterruptedException, IOException {
//			
//		String body =tc.getRequestBodyWithDynamicData(data);
//		System.out.println("Request payload is \n" + body);
//		logger = extent.createTest(data.get("TestCaseName"));
//		Response response = createIdentityServerIDPTokenResponse(Token,body);
//		Token = response.jsonPath().get("access_token");
//		System.out.println(Token);
//		String expectedStatusCode = data.get("expectedStatusCode");
//		System.out.println(expectedStatusCode);
//		int actualStatusCode  = response.getStatusCode();
//		String expectedStatusMessage = data.get("ExpectedStatusMessage");
//		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
//	   // Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
//		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());	
//
//		}
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getAccountsIDP(Map<String, String> data) throws InterruptedException, IOException {
		
	logger = extent.createTest(data.get("TestCaseName"));
	String apiURI="https://gateway-service.tst-api.digitalrealtytrust.com/accounts";
	System.out.println(apiURI);
	String response = createIdentityServerIDPTokenResponse();
	System.out.println(response);
	Response response1 = RestAssured.given().relaxedHTTPSValidation()
			.header("Authorization", response)
			.header("Content-Type", "application/json")
			.header("Master-Account-Id","0")
			.header("Account-Id","0")
		    .get(apiURI);  
	System.out.println(response1.asString());
	logger.pass("Displayed DCIM DataPoint Values::" + response1.asString());
	logger.log(Status.INFO, response1.statusLine());
	Assert.assertEquals(response1.getStatusCode(), 200);
	Assert.assertTrue(response1.statusLine().contains("OK"));
 	logger.log(Status.INFO, "Successfully extracted DCIM DataPoint Values Data" );
 	logger.log(Status.PASS, "Status Code and Status Message is" + response1.getStatusLine());
	
	}
	public static String createIdentityServerIDPTokenResponse() throws IOException {

				Response getResponse = RestAssured.given()
						.header("Content-Type", "application/x-www-form-urlencoded")
						.header("cache-control", "no-cache")
						.formParam("client_id", "qa_telx_group")
						.formParam("client_secret", "952ef3014c0b495c99f7ad77572dd481")
						.formParam("grant_type", "client_credentials")
						.post("https://login-a1.interxion.com/connect/token");

				Token = getResponse.jsonPath().get("access_token");
				
			System.out.println(getResponse.asString());
			
			return Token;
		}
}
		