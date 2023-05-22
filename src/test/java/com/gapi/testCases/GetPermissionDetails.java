package com.gapi.testCases;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetPermissionDetails extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	String path = "/permissions"; 
	
	 
	   public String getPermissionsInfoById(String id,String path) {
			
		    logger.createNode("getPermissionsInfoById");
			String apiURI = domain+path+"/" + id;
			System.out.println(apiURI);
			Response response = RestAssured.given()
					.header("Authorization",Token)
	 				.header("Content-Type", "application/json") 
	 				.header("Master-Account-Id",3)
					.header("Account-Id",3)
					.header("User-Email","admin@digitalrealty.com")
					.header("Correlation-Id","db1732a1-0bb9-4b61-b98a-0c893cabf3ba")
	 				.get(apiURI);      		     	

			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
	 		String allResponsebody = response.getBody().asString();
	 		
	 		log.info("Successfully extracted Permissions Details Info ");
	 		logger.log(Status.PASS, "Successfully extracted Permissions Details Info" );
	 		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response.getStatusLine());
	 		
				
			
			return allResponsebody;
		}
}