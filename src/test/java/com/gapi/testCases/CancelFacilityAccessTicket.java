package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CancelFacilityAccessTicket extends BaseClass {

	public static String cancelPath = "/facility-access/access-tickets/";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String date1 = tc.getCurrentDateTime();
	CreateVisitorAccessRequest cvar = new CreateVisitorAccessRequest();

	// Cancel Facility Access Ticket
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCancelFaclityAccessTicket(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		String visitorAccessPayLoad = createVisitorAccessPayLoad(data);
		System.out.println(visitorAccessPayLoad);
		Response response = cvar.createVisitorAccessTicket(Token, visitorAccessPayLoad);
		System.out.println("Response is : " + response.asString());
		String WONumber = tc.getWOIDFromResponse(response);
		String cancelPayLoad = "{\r\n\"cancelReason\":\"\"\r\n}";

		String cancelAPIURI = domain + cancelPath + WONumber + "/cancel";

		response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(cancelPayLoad)
				.put(cancelAPIURI).then().extract().response();

		logger.log(Status.PASS, "Fired Cancel Faclility Access Request API");
		log.info("Fired Cancel Faclility Access Request API Successfully");
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());

		String expectedstatus = "Work Order " + WONumber + " has been cancelled.";
		//Assert.assertTrue(response.asString().contains(expectedstatus));
		Assert.assertEquals(response.jsonPath().get("result.status"), expectedstatus);
		logger.log(Status.PASS, "Response Status Code and Status Message is" + response.statusLine());

		Assert.assertEquals(200, response.getStatusCode());
		if (response.getStatusCode() == 200) {
			logger.log(Status.PASS, "Successfully Cancelled Visitor Access Ticket- " + WONumber);
			log.info("Successfully Cancelled Visitor Access Ticket - " + WONumber);
		} else {
			logger.fail("Error Response message is " + response.asString());
			logger.log(Status.FAIL, "Not allowed to Cancel Visitor Access Ticket");
		}
	}

	// Get Cancelled Facility Access Ticket
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCancelledFaclityAccessTicket(Map<String, String> data)
			throws IOException, InterruptedException {
		logger = extent.createTest(data.get("TestCaseName"));
		String visitorAccessPayLoad = createVisitorAccessPayLoad(data);
		System.out.println(visitorAccessPayLoad);
		Response response = cvar.createVisitorAccessTicket(Token, visitorAccessPayLoad);
		System.out.println("Response is : " + response.asString());
		String WONumber = tc.getWOIDFromResponse(response);
		String cancelPayLoad = "{\r\n\"cancelReason\":\"\"\r\n}";

		String apiURI = domain + cancelPath + WONumber + "/cancel";
		// response=fireCancelFacilityAccessTicket(TokenVuat, apiURI, cancelPayLoad);

		
		  response = RestAssured.given().relaxedHTTPSValidation().header("Authorization",
		  "Bearer " + Token) .header("Content-Type",
		  "application/json").header("Master-Account-Id", masterAccountID )
		  .header("Account-Id",accountID ).header("User-Email",
		  "vigneswarareddy@digitalrealty.com")
		  .body(cancelPayLoad).put(apiURI).then().extract().response();
		  
		 
		String getCancelWOURI = domain + cancelPath + WONumber;
		// response=getFacilityAccessTicket(TokenVuat,getCancelWOURI);
		response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id",masterAccountID )
				.header("Account-Id",accountID ).header("User-Email", "vigneswarareddy@digitalrealty.com")
				.get(getCancelWOURI).then().extract().response();

		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		logger.log(Status.PASS, "Fired Get Facility Accesss ticket API");
		log.info("Fired Get Facility Accesss ticket API");
		String expectedstatus = "Cancelled";
		Assert.assertEquals(response.jsonPath().get("ticketId"), WONumber);
		Assert.assertEquals(response.jsonPath().get("ticketStatus"), expectedstatus);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		Assert.assertEquals(200, response.getStatusCode());
		if (response.getStatusCode() == 200) {
			logger.log(Status.PASS, "Successfully ticket status was updated to Cancelled");
			log.info("Successfully ticket status was updated to Cancelled");
		} else {
			logger.fail("Error Response message is " + response.asString());
			logger.log(Status.FAIL, "Not allowed to Cancel Faclility Access Ticket");
		}
	}

	public String createVisitorAccessPayLoad(Map<String, String> data) {
		String visitorAccessPayLoad = "[\r\n" + "  {\r\n" + "\"visitors\": [  {\"visitorFirstName\": \""
				+ data.get("visitorFirstName") + "\"," + "\"visitorLastName\": \"" + data.get("visitorLastName") + "\","
				+ "\"visitorEmail\": \"" + data.get("visitorEmail") + "\"," + "\"visitorPhone\": \""
				+ data.get("visitorPhone") + "\"," + "\"company\":\"\"  }]," + "\"visitorType\": \""
				+ data.get("visitorType") + "\"," + "\"escortFirstName\": \"" + data.get("escortFirstName") + "\","
				+ "\"escortLastName\": \"" + data.get("escortLastName") + "\"," + "\"visitorHostFirstName\": \""
				+ data.get("visitorHostFirstName") + "\"," + "\"visitorHostLastName\": \""
				+ data.get("visitorHostLastName") + "\"," + "\"visitorHostCompany\": \""
				+ data.get("visitorHostCompany") + "\"," + "\"visitorBadgeStartDate\": \"" + date + "\","
				+ "\"visitorBadgeEndDate\": \"" + date1 + "\"," + "\"company\": \"" + data.get("company") + "\","
				+ "\"notes\": \"" + data.get("notes") + "\"," + "\"emailNotifications\": \""
				+ data.get("emailNotifications") + "\"," + "\"isExtendedVisitorBadge\": false,"
				+ "\"standardVisitorBadgeStr\": \"" + data.get("standardVisitorBadgeStr") + "\","
				+ "\"extendedVisitorBadgeStr\": \"" + data.get("extendedVisitorBadgeStr") + "\","
				+ "\"sites\": [  {\"site\": \"" + data.get("site") + "\"," + "\"locations\": [  {\"location\": \""
				+ data.get("locations") + "\"," + "\"accessStartDate\": \"" + date + "\"," + "\"accessEndDate\": \""
				+ date1 + "\"," + "\"accessStartTime\": \"06:22:00\"," + "\"accessEndTime\": \"06:22:00\"  }]  " + "}],"
				+ "\"isBulkRequest\":false  }]";
		return visitorAccessPayLoad;

	}

	
}
