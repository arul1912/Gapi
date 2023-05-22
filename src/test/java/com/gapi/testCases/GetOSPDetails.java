package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetOSPDetails extends BaseClass {
	
	String path = "/facility-access/osps/access-requests";
	static TestUtilities tc = new TestUtilities();
	
		@Test()
		public void getOSPDetailsInfo() throws InterruptedException {
			
	    	logger = extent.createTest("Get OSP Ticket details Information");
			String apiURI = idpdomain+ path;
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Tokenidp)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID) 	
					.header("User-Email", "gpuat22-uat3@yahoo.com")
	 				.get(apiURI);      		     	

			System.out.println("===>"+response.asString());
			
			logger.log(Status.INFO, response.statusLine());
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
//			Assert.assertTrue(response.statusLine().contains("OK"));
	 		
	 		log.info("Successfully extracted all get OSP Details Info ");
	 		logger.log(Status.INFO, "Successfully extracted get OSP Details Info" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}
	    
		  
				@Test
				public void getOSPDetailsInfoWithInvalidToken() throws InterruptedException {
					
			    	logger = extent.createTest("getOSPDetailsInfoWithInvalidToken");
					String apiURI = idpdomain+ path;
					Response response = RestAssured.given().relaxedHTTPSValidation()
							.header("Authorization", "Bearer "+"abcde")
							.header("Content-Type", "application/json")
							.header("Master-Account-Id", masterAccountID)
							.header("Account-Id", accountID) 	
							.header("User-Email", "gpuat22-uat3@yahoo.com")
			 				.get(apiURI);      		     	

					System.out.println("===>"+response.asString());
					
					logger.log(Status.INFO, response.statusLine());	
					Assert.assertEquals(response.getStatusCode(), 500);
					Assert.assertTrue(response.statusLine().contains("HTTP/1.1 500"));
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					tc.verify500ErrorResponseMessage(response.asString(), "Internal Server Error", "The token was expected to have 3 parts, but got 1.");
					logger.pass("Not Allowed to extracted OSP Details Info With Invalid token");
					
				
				}
				
				@Test
				public void getOSPDetailsInfoWithInvalidGlobalUltimate() throws InterruptedException {

					logger = extent.createTest("Get OSP Details Information With Invalid Global Ultimate");
					String apiURI = idpdomain + path;
					Response response = RestAssured.given().relaxedHTTPSValidation()
							.header("Authorization", "Bearer " + Tokenidp)
							.header("Content-Type", "application/json")
							.header("Master-Account-Id", "abcde")
							.header("Account-Id", accountID)
							.header("User-Email", "gpuat22-uat3@yahoo.com")
							.get(apiURI);

					System.out.println("===>" + response.asString());

					logger.log(Status.INFO, response.statusLine());
					Assert.assertEquals(response.getStatusCode(), 400);
					Assert.assertTrue(response.statusLine().contains("HTTP/1.1 400"));
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					tc.verifyErrorMessagefromResponse(response.asString(), "BAD_REQUEST", "Bad request was submitted.");
					logger.pass("Not Allowed to extracted OSP Details Info With Invalid Global Ultimate");
				}

			
		
	

}
