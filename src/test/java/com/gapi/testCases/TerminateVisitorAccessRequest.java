package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/* Terminate Visitor Access */

public class TerminateVisitorAccessRequest extends BaseClass {

	public static String path = "/facility-access/visitors/terminate";
	// public static String accpath="/facility-access/access-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String accessEndDate = tc.getCurrentDateTime();

	// GAPI-6258-Visitor Access - Terminate Visitor Access

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyTerminateVisitorAccessRequest(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));

		String body = "{\r\n  \"visitors\": [\r\n    " + "{\r\n      \"visitorFirstName\": \""
				+ data.get("visitorFirstName") + "\",\r\n     " + " \"visitorLastName\": \""
				+ data.get("visitorLastName") + "\",\r\n     " + " \"visitorEmail\": \"" + data.get("visitorEmail")
				+ "\",\r\n     " + " \"visitorPhone\": \"" + data.get("visitorPhone") + "\"\r\n    }\r\n  ],"
				+ "\r\n  \"visitorBadgeEndDate\": \"" + accessEndDate + "\"\r\n}";

		String apiURI = domain + path;
		System.out.println(body);
		Response response = TerminateCreatedVisitorAccessTicket(Token, body);
		System.out.println("Response is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		if (response.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Terminated Visitor access request");
			logger.log(Status.PASS, "Terminated Visitor access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "TerminateVisitoraccess_" + WONumber);

			GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();

		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to Terminate Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

	}

	// Arul on 09-03-2023 Terminate Visitor Access Request - With incorrect Email
	// GAPI-6817-FA-Terminate Visitor Access Request - With incorrect Email -
	// Validate error message

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void terminateVisitorAccessRequestWithIncorrectEmail(Map<String, String> data)
			throws InterruptedException, IOException {
		String path2 = "/facility-access/visitors/terminate";
		logger = extent.createTest(data.get("TestCaseName"));
		String body = "{\r\n " + "  \"visitors\": [\r\n        {\r\n            \"visitorFirstName\": \""
				+ data.get("visitorFirstName") + "\"," + "\r\n            \"visitorLastName\": \""
				+ data.get("visitorLastName") + "\",\r\n            \"visitorEmail\": \"" + data.get("visitorEmail")
				+ "\"," + "\r\n            \"visitorPhone\": \"" + data.get("visitorPhone")
				+ "\"\r\n        }\r\n    ]," + "\r\n    \"visitorBadgeEndDate\": \"" + accessEndDate + "\"\r\n}";

		System.out.println("Body is: " + body);
		String apiURI = domain + path2;
		System.out.println("Status Message is: " + apiURI);
		Response response1 = tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
		System.out.println("Response is: " + response1.asPrettyString());
		System.out.println("Status Message is: " + response1.statusLine());
		logger.log(Status.INFO, response1.statusLine());
		Assert.assertEquals(response1.getStatusCode(), 400);
		Assert.assertTrue(response1.statusLine().contains("HTTP/1.1 400"));
		log.info("Successfully extracted all get Reports Info ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response1.getStatusLine());
		logger.log(Status.INFO, "Successfully extracted getreports Info");
		System.out.println("===>" + response1.asString());

	}

	GetServiceTicketDetails getservice = new GetServiceTicketDetails();

	String accpath = "/facility-access/access-tickets";
	// GAPI-9011-Terminate a Visitor Access request and verify the weather the
	// Description attribute value as (Request for Terminate All Visitor badge) from
	// Service Ticket Get call response
	// GAPI-9003-Terminate a Visitor Access request and verify the weather the
	// category attribute value as (Terminate All Visitor Access) from Service
	// Ticket Get call response

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyAttributeTerminateVisitorAccessRequest(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		String body = "{\r\n  \"visitors\": [\r\n    " + "{\r\n      \"visitorFirstName\": \""
				+ data.get("visitorFirstName") + "\",\r\n     " + " \"visitorLastName\": \""
				+ data.get("visitorLastName") + "\",\r\n     " + " \"visitorEmail\": \"" + data.get("visitorEmail")
				+ "\",\r\n     " + " \"visitorPhone\": \"" + data.get("visitorPhone") + "\"\r\n    }\r\n  ],"
				+ "\r\n  \"visitorBadgeEndDate\": \"" + accessEndDate + "\"\r\n}";

		System.out.println(body);
		Response response = TerminateCreatedVisitorAccessTicket(Token, body);
		System.out.println("Response is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		String WONumber = tc.getAttributeFromArrayResponse(response.asString(), "workOrder", "workOrderId");
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String apiURI1 = domain + accpath + "/" + WONumber;
		Response getresponse = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "gpuat22-uat3@yahoo.com").get(apiURI1);

		System.out.println("===>" + getresponse.asString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		logger.log(Status.INFO, "Successfully extracted Permanent Badge Info and verified ");
		String attribute = data.get("filters");
		if (attribute.equalsIgnoreCase("category")) {
			String category = data.get("category");
			String getcategory = tc.getAttributeFromJsonResponse(getresponse.asString(), "ticketCategory");
			Response serviceresponse = getservice.getAllServiceTicketDetailsinfo();
			Assert.assertEquals(serviceresponse.getStatusCode(), 200);
			String servicecategory = tc.getAttributeFromArrayResponse(serviceresponse.asString(), "content",
					"category");
			Assert.assertEquals(category, getcategory);
			Assert.assertEquals(category, servicecategory);
			logger.log(Status.PASS, "Successfully extracted service Ticket info and verified");
		} else if (attribute.equalsIgnoreCase("description")) {
			String description = data.get("description");
			String getdescription = tc.getAttributeFromJsonResponse(getresponse.asString(), "description");
			Response serviceresponse = getservice.getAllServiceTicketDetailsinfo();
			Assert.assertEquals(serviceresponse.getStatusCode(), 200);
			String servicedescription = tc.getAttributeFromArrayResponse(serviceresponse.asString(), "content",
					"description");
			Assert.assertEquals(description, getdescription);
			Assert.assertEquals(description, servicedescription);
			logger.log(Status.PASS, "Successfully extracted service Ticket info and verified");
		} else if (attribute.equalsIgnoreCase("null") || attribute.equalsIgnoreCase("")) {
			Assert.fail("The Attribute is null");
		}
	}

//Re-usable methods for Terminate FA Ticket
	// *************************************************************************************

	public static Response TerminateCreatedVisitorAccessTicket(String Token, String body) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com").body(body)
				.post(domain + path).then().extract().response();

		return response;
	}

}