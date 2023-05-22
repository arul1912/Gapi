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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/* Terminate Visitor Access */

public class TerminatePermanentBadgeRequest extends BaseClass {

	public static String path = "/facility-access/perm-badges/terminate";
	// public static String accpath="/facility-access/access-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String date1 = tc.getCurrentDateTime();

	// GAPI-6263-Permanent badge - Terminate Permanent badge
	// GAPI-6844 FA-Permanent Badge - Terminate All Access

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyTerminatePermanentBadgeRequest(Map<String, String> data)
			throws IOException, InterruptedException {

//		logger = extent.createTest("verifyTerminatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));

		String body =

				"{\r\n\"firstName\":\"" + data.get("visitorFirstName") + "\"," + "\r\n\"lastName\":\""
						+ data.get("visitorLastName") + "\"," + "\r\n\"email\":\"" + data.get("visitorEmail") + "\","
						+ "\r\n\r\n\"badgeExpirationDate\": \"" + date1 + "\"," + "\r\n\r\n\"company\":\""
						+ data.get("company") + "\"\r\n}";

		System.out.println(body);
		Response response = TerminateCreatedPermBadgeTicket(Token, body);
		System.out.println("exam: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		if (response.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Terminated Perm Badge request");
			logger.log(Status.PASS, "Terminated Permanent badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "TerminatePermbadge_" + WONumber);
		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to Terminate Perm Badge request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

	}

	GetServiceTicketDetails getservice = new GetServiceTicketDetails();

	String path2 = "/facility-access/access-tickets/";
	// GAPI-9006-Terminate a Perm badge request and verify the weather the category
	// attribute value as (Terminate All Access) from Service Ticket Get call
	// response
	// GAPI-9008-Terminate a Perm badge request and verify the weather the
	// Description attribute value as (Request for Terminate Permanent badge) from
	// Service Ticket Get call response
	//GAPI-6844	FA-Permanent Badge - Terminate All Access


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyAttributeTerminatePermanentBadgeRequest(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		String body = "{\r\n\"firstName\":\"" + data.get("visitorFirstName") + "\"," + "\r\n\"lastName\":\""
				+ data.get("visitorLastName") + "\"," + "\r\n\"email\":\"" + data.get("visitorEmail") + "\","
				+ "\r\n\r\n\"badgeExpirationDate\": \"" + date1 + "\"," + "\r\n\r\n\"company\":\"" + data.get("company")
				+ "\"\r\n}";

		System.out.println(body);
		Response response = TerminateCreatedPermBadgeTicket(Token, body);
		System.out.println("exam: " + response.asString());
		String WOnumber = tc.getWONumberIdFromResponse(response);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String apiURI = domain + path2 + WOnumber;
		Response getresponse = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "gpuat22-uat3@yahoo.com").get(apiURI);

		System.out.println("===>" + getresponse.asString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		logger.log(Status.INFO, "Successfully extracted Permanent Badge Info and verified");
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
		} else if (attribute.equalsIgnoreCase("null")) {
			Assert.fail("The Attribute is null");
		}
	}

	// GAPI-8990-FA-Permanent Badge - Terminate -Get Service ticket
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyTerminatePermanentBadgeRequestGetServiceTicket(Map<String, String> data)
			throws IOException, InterruptedException {

//						logger = extent.createTest("verifyTerminatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));

		String body =

				"{\r\n\"firstName\":\"" + data.get("visitorFirstName") + "\"," + "\r\n\"lastName\":\""
						+ data.get("visitorLastName") + "\"," + "\r\n\"email\":\"" + data.get("visitorEmail") + "\","
						+ "\r\n\r\n\"badgeExpirationDate\": \"" + date1 + "\"," + "\r\n\r\n\"company\":\""
						+ data.get("company") + "\"\r\n}";

		System.out.println(body);
		Response response = TerminateCreatedPermBadgeTicket(Token, body);
		System.out.println("exam: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		// String WONumber =
		// tc.getAttributeFromArrayResponse1(response.asString(),"workOrder","workOrderId");
		String WOnumber = tc.getWONumberIdFromResponse(response);

		String path = "/service-tickets";
		// https://api-tst.digitalrealty.com/v1/service-tickets?id=WO8719465
		String apiURI2 = domain + path + "?id=" + WOnumber;
		String title = data.get("title");
		String description = data.get("description");
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).get(apiURI2);

		JsonPath responseJsonPath = response1.jsonPath();

		String getTitle = tc.getAttributeFromArrayResponse1(response1.asString(), "content", "title");
		String getdescription = tc.getAttributeFromArrayResponse1(response1.asString(), "content", "description");

		// Assert.assertEquals(responseJsonPath.get("content[0].title"),"title");
		// Assert.assertEquals(responseJsonPath.get("content[0].description"),"description");
		Assert.assertEquals(title, getTitle);
		Assert.assertEquals(description, getdescription);
		// Assert.assertEquals(responseJsonPath.get("content[0].description"),"description");
		// String getTitle =
		// tc.getAttributeFromJsonResponse(response1.asString(),"title");
		// String getDescription =
		// tc.getAttributeFromJsonResponse(response1.asString(),"description");
		Assert.assertEquals(response1.getStatusCode(), 200);
	}

	// GAPI-8987-FA-Permanent Badge - Terminate -Subject Enhancement
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyTerminatePermanentBadgeRequestSubjectEnhancement(Map<String, String> data)
			throws IOException, InterruptedException {

//						logger = extent.createTest("verifyTerminatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));

		String body =

				"{\r\n\"firstName\":\"" + data.get("visitorFirstName") + "\"," + "\r\n\"lastName\":\""
						+ data.get("visitorLastName") + "\"," + "\r\n\"email\":\"" + data.get("visitorEmail") + "\","
						+ "\r\n\r\n\"badgeExpirationDate\": \"" + date1 + "\"," + "\r\n\r\n\"company\":\""
						+ data.get("company") + "\"\r\n}";

		System.out.println(body);
		Response response = TerminateCreatedPermBadgeTicket(Token, body);
		System.out.println("exam: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		// String WONumber =
		// tc.getAttributeFromArrayResponse1(response.asString(),"workOrder","workOrderId");
		String WOnumber = tc.getWONumberIdFromResponse(response);

		// String getcategory =
		// tc.getAttributeFromJsonResponse(getresponse.asString(),"ticketCategory");

		// https://api-tst.digitalrealty.com/v1/facility-access/access-tickets/WO8719532
		String path1 = "/facility-access/access-tickets/";
		String apiURI1 = domain + path1 + WOnumber;
		String contactType = data.get("contactType");
		String description = data.get("description");
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "gpuat22-uat3@yahoo.com").get(apiURI1);
		String getcontacttype = tc.getAttributeFromJsonResponse(response1.asString(), "contactType");
		String getdiscription = tc.getAttributeFromJsonResponse(response1.asString(), "description");
		Assert.assertEquals(contactType, getcontacttype);
		Assert.assertEquals(description, getdiscription);
		Assert.assertEquals(response1.getStatusCode(), 200);
	}

	// Re-usable methods for Terminate FA Perm Badge Ticket
	// *************************************************************************************

	public static Response TerminateCreatedPermBadgeTicket(String Token, String body) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com").body(body)
				.post(domain + path).then().extract().response();

		return response;
	}

}
