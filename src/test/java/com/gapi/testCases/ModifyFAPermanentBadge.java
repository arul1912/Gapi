package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ModifyFAPermanentBadge extends BaseClass {
	public static String path = "/facility-access/perm-badges/modify";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String accessStartDate = tc.getCurrentDateAndTime();
	static String accessEndDate = tc.getCurrentDateTime();

	// GAPI-6840 FA-Permanent Badge - Modify Badge- Add sites
	// MOdify Visitor Access Ticket with all possible combinations
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyModifyPermanentBadgeRequest(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));

		String body = "{\r\n    \"firstName\": \"" + data.get("visitorFirstName") + "\"," + "\r\n  \"lastName\": \""
				+ data.get("visitorLastName") + "\"," + "\r\n  \"email\": \"" + data.get("visitorEmail") + "\","
				+ "\r\n  \"badgeStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"badgeExpirationDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"emailNotifications\": \"" + data.get("emailNotifications") + "\","
				+ "\r\n  \"phone\": \"" + data.get("visitorPhone") + "\"," + "\r\n  \"company\": \""
				+ data.get("company") + "\"," + "\r\n  \"addSites\": [\r\n  {\r\n \"site\": \"" + data.get("site")
				+ "\"," + "\r\n  \"locations\": [\r\n  {\r\n  \"location\": \"" + data.get("locations") + "\","
				+ "\r\n  \"accessStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"accessEndDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"accessStartTime\": \"06:22:00\","
				+ "\r\n  \"accessEndTime\": \"06:22:00\"\r\n }\r\n  ]\r\n   }\r\n    ]\r\n}";

		System.out.println("Body is : " + body);

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com").body(body)
				.put(domain + path).then().extract().response();
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());

		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		if (response.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Modified Permanent Badge request");
			logger.log(Status.PASS, "Modified Permanent Badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Modify_Permanentbadge_" + WONumber);
		} else {
			// logger.fail("Error Response message is " + response.asString());
			logger.info("Error Response message is " + response.asString());
			// logger.log(Status.FAIL, "Not allowed to Permanent Badge request");
			logger.log(Status.PASS, "Not allowed to Permanent Badge request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
		}
	}

	// GAPI-8989-FA-Permanent Badge - Modify Ticket -Get Service Ticket
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyModifyPermanentBadgeServiceTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));

		String body = "{\r\n    \"firstName\": \"" + data.get("visitorFirstName") + "\"," + "\r\n  \"lastName\": \""
				+ data.get("visitorLastName") + "\"," + "\r\n  \"email\": \"" + data.get("visitorEmail") + "\","
				+ "\r\n  \"badgeStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"badgeExpirationDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"emailNotifications\": \"" + data.get("emailNotifications") + "\","
				+ "\r\n  \"phone\": \"" + data.get("visitorPhone") + "\"," + "\r\n  \"company\": \""
				+ data.get("company") + "\"," + "\r\n  \"addSites\": [\r\n  {\r\n \"site\": \"" + data.get("site")
				+ "\"," + "\r\n  \"locations\": [\r\n  {\r\n  \"location\": \"" + data.get("locations") + "\","
				+ "\r\n  \"accessStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"accessEndDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"accessStartTime\": \"06:22:00\","
				+ "\r\n  \"accessEndTime\": \"06:22:00\"\r\n }\r\n  ]\r\n   }\r\n    ]\r\n}";

		System.out.println("Body is : " + body);

		Response getResponse = tc.postRequest(Token, body, masterAccountID, accountID, domain + path);
		System.out.println("Response is : " + getResponse.asString());
		System.out.println("Status Code is : " + getResponse.getStatusCode());

		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = getResponse.getStatusCode();
		// String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + getResponse.statusCode() + getResponse.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + getResponse.statusLine());

		if (getResponse.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(getResponse);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Modified Permanent Badge request");
			logger.log(Status.PASS, "Modified Permanent Badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Modify_Permanentbadge_" + WONumber);
		} else {
			logger.info("Error Response message is " + getResponse.asString());
			logger.log(Status.PASS, "Not allowed to Permanent Badge request");

		}

		String WOnumber = tc.getAttributeFromArrayResponse1(getResponse.asString(), "workOrder", "workOrderId");

		String path = "/service-tickets";
		String apiURI2 = domain + path + "?id=" + WOnumber;
		String title = data.get("title");
		String description = data.get("description");
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).get(apiURI2);

		JsonPath responseJsonPath = response1.jsonPath();
		Assert.assertEquals(responseJsonPath.get("content[0].title"), "Request for Modify Permanent badge");
		Assert.assertEquals(responseJsonPath.get("content[0].description"), "Request for Modify Permanent badge");
		Assert.assertEquals(title, title);
		Assert.assertEquals(description, description);
		Assert.assertEquals(response1.getStatusCode(), 200);
	}

	// GAPI-8986-FA-Permanent Badge - Modify Ticket -Subject Enhancement

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyModifyPermanentBadgeSubjectEnhancement(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));

		String body = "{\r\n    \"firstName\": \"" + data.get("visitorFirstName") + "\"," + "\r\n  \"lastName\": \""
				+ data.get("visitorLastName") + "\"," + "\r\n  \"email\": \"" + data.get("visitorEmail") + "\","
				+ "\r\n  \"badgeStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"badgeExpirationDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"emailNotifications\": \"" + data.get("emailNotifications") + "\","
				+ "\r\n  \"phone\": \"" + data.get("visitorPhone") + "\"," + "\r\n  \"company\": \""
				+ data.get("company") + "\"," + "\r\n  \"addSites\": [\r\n  {\r\n \"site\": \"" + data.get("site")
				+ "\"," + "\r\n  \"locations\": [\r\n  {\r\n  \"location\": \"" + data.get("locations") + "\","
				+ "\r\n  \"accessStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"accessEndDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"accessStartTime\": \"06:22:00\","
				+ "\r\n  \"accessEndTime\": \"06:22:00\"\r\n }\r\n  ]\r\n   }\r\n    ]\r\n}";

		System.out.println("Body is : " + body);

		Response getResponse = tc.postRequest(Token, body, masterAccountID, accountID, domain + path);
		System.out.println("Response is : " + getResponse.asString());
		System.out.println("Status Code is : " + getResponse.getStatusCode());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = getResponse.getStatusCode();
		System.out.println("Statusmessage & status code is:" + getResponse.statusCode() + getResponse.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + getResponse.statusLine());
		String WOnumber = tc.getAttributeFromArrayResponse1(getResponse.asString(), "workOrder", "workOrderId");
		String path1 = "/facility-access/access-tickets/";
		String apiURI1 = domain + path1 + WOnumber;
		String contactType = data.get("contactType");
		String description = data.get("description");
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "gpuat22-uat3@yahoo.com").get(apiURI1);

		String getcontacttype = tc.getAttributeFromJsonResponse(response1.asString(), "contactType");
		String getdescription = tc.getAttributeFromJsonResponse(response1.asString(), "description");
		Assert.assertEquals(contactType, getcontacttype);
		Assert.assertEquals(description, getdescription);
		Assert.assertEquals(response1.getStatusCode(), 200);
	}

	GetServiceTicketDetails getservice = new GetServiceTicketDetails();

	String accpath = "/facility-access/access-tickets";

	// GAPI-9005-Modify Perm badge (Add Site) and verify the weather the category
	// attribute value as (Modify Badge) from Service Ticket Get call response

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyAttributeModifyPermanentBadgeRequest(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		String body = modifyPermanentBadgePayLoad(data);
		System.out.println(body);
		Response response = modifyPermanentBadgeRequest(Token, body);
		System.out.println("exam: " + response.asString());
		String WONumber = tc.getAttributeFromArrayResponse(response.asString(), "workOrder", "workOrderId");
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String apiURI1 = domain + accpath + "/" + WONumber;
		Response getresponse = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "gpuat22-uat3@yahoo.com").get(apiURI1);

		System.out.println("===>" + getresponse.asString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		logger.log(Status.INFO, "Successfully extracted Permanent Badge Info");
		String category = data.get("category");
		String getcategory = tc.getAttributeFromJsonResponse(getresponse.asString(), "ticketCategory");
		Response serviceresponse = getservice.getAllServiceTicketDetailsinfo();
		Assert.assertEquals(serviceresponse.getStatusCode(), 200);
		String servicecategory = tc.getAttributeFromArrayResponse(serviceresponse.asString(), "content", "category");
		Assert.assertEquals(category, getcategory);
		Assert.assertEquals(category, servicecategory);
		logger.log(Status.PASS, "Successfully extracted service Ticket info and verified");
	}

	// Re-usable methods
	public String modifyPermanentBadgePayLoad(Map<String, String> data) {
		String body = "{\r\n    \"firstName\": \"" + data.get("visitorFirstName") + "\"," + "\r\n  \"lastName\": \""
				+ data.get("visitorLastName") + "\"," + "\r\n  \"email\": \"" + data.get("visitorEmail") + "\","
				+ "\r\n  \"badgeStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"badgeExpirationDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"emailNotifications\": \"" + data.get("emailNotifications") + "\","
				+ "\r\n  \"phone\": \"" + data.get("visitorPhone") + "\"," + "\r\n  \"company\": \""
				+ data.get("company") + "\"," + "\r\n  \"addSites\": [\r\n  {\r\n \"site\": \"" + data.get("site")
				+ "\"," + "\r\n  \"locations\": [\r\n  {\r\n  \"location\": \"" + data.get("locations") + "\","
				+ "\r\n  \"accessStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"accessEndDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"accessStartTime\": \"06:22:00\","
				+ "\r\n  \"accessEndTime\": \"06:22:00\"\r\n }\r\n  ]\r\n   }\r\n    ]\r\n}";
		return body;
	}

	// GAPI-9007-Modify Perm badge (Add Site) and verify the weather the Description
	// attribute value as (Request for Modify Permanent Badge) from Service Ticket
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyModifyPermanentBadgeRequestAddSite(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));

		String body = "{\r\n    \"firstName\": \"" + data.get("visitorFirstName") + "\"," + "\r\n  \"lastName\": \""
				+ data.get("visitorLastName") + "\"," + "\r\n  \"email\": \"" + data.get("visitorEmail") + "\","
				+ "\r\n  \"badgeStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"badgeExpirationDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"emailNotifications\": \"" + data.get("emailNotifications") + "\","
				+ "\r\n  \"phone\": \"" + data.get("visitorPhone") + "\"," + "\r\n  \"company\": \""
				+ data.get("company") + "\"," + "\r\n  \"addSites\": [\r\n  {\r\n \"site\": \"" + data.get("site")
				+ "\"," + "\r\n  \"locations\": [\r\n  {\r\n  \"location\": \"" + data.get("locations") + "\","
				+ "\r\n  \"accessStartDate\": \"" + accessStartDate + "\"," + "\r\n  \"accessEndDate\": \""
				+ accessEndDate + "\"," + "\r\n  \"accessStartTime\": \"06:22:00\","
				+ "\r\n  \"accessEndTime\": \"06:22:00\"\r\n }\r\n  ]\r\n   }\r\n    ]\r\n}";

		System.out.println("Body is : " + body);

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com").body(body)
				.put(domain + path).then().extract().response();
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());

		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String WONumber = tc.getAttributeFromArrayResponse1(response.asString(), "workOrder", "workOrderId");

		String path = "/facility-access/access-tickets/";
		// https://api-tst.digitalrealty.com/v1/service-tickets?id=WO8719465
		// https://api-tst.digitalrealty.com/v1/facility-access/access-tickets/WO8706357
		String apiURI2 = domain + path + WONumber;

		String description = data.get("description");
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).get(apiURI2);

		JsonPath responseJsonPath = response1.jsonPath();

		// String getTitle =
		// tc.getAttributeFromArrayResponse1(response1.asString(),"content","title");
		String getdescription = tc.getAttributeFromJsonResponse(response1.asString(), "description");

//			Assert.assertEquals(title,getTitle);
		// Assert.assertEquals(description,getdescription)
		// Assert.assertEquals(responseJsonPath.get("content[0].description"),"description");
		// String getTitle =
		// tc.getAttributeFromJsonResponse(response1.asString(),"title");

		Assert.assertEquals(response1.getStatusCode(), 200);
	}

	public Response modifyPermanentBadgeRequest(String Token, String body) {
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com").body(body)
				.put(domain + path).then().extract().response();
		return response;
	}

}
