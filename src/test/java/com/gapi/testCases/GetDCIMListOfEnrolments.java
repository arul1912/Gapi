package com.gapi.testCases;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetDCIMListOfEnrolments extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String dynamicDomain = "/table/u_dcim_enrollment";


	
	
	//GAPI-6059-DCIMT- Retrieve List of Enrolments

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getDCIMListOfEnrolments(Map<String, String> data) throws InterruptedException {
			
		logger = extent.createTest(data.get("TestCaseName"));
		String sysparm_fields = data.get("sysparm_fields");
		String sysparm_query=data.get("sysparm_query");
		String apiURI = domain+dynamicDomain  +"?"+ sysparm_fields +sysparm_query;
		System.out.println(apiURI);
		Response response = RestAssured.given()
					.header("Authorization","Basic QXBpLlVzZXI6U04wVzIwMTYh")
	 				.header("Content-Type", "application/json")
	 				.get(apiURI);  
		System.out.println(response.asString());
		logger.pass("Displayed List Of Enrolment Data::" + response.getBody().asPrettyString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
	 	logger.log(Status.INFO, "Successfully extracted List Of Enrolment Data" );
	 	logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}

}
