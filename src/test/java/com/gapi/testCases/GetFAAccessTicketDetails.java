package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GetFAAccessTicketDetails extends BaseClass {

	String path = "/facility-access/visitors";
	static TestUtilities tc = new TestUtilities();

	// this method is used to retrieve WO information by using ID and validate the
	// response


		@Test
		public void getAllVisitorAccessTicketsInfo() throws InterruptedException {

			logger = extent.createTest("getAllVistorAccessTicketDetails");
			String apiURI = domain + "/facility-access/osps";
			Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
					.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID).get(apiURI);

			logger.log(Status.INFO, response.statusLine());
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("OK"));

			log.info("Successfully extracted all Visitor Access Ticket Info ");
			logger.log(Status.INFO, "Status Code and Status Message is " + response.getStatusLine());
			logger.log(Status.INFO, "Successfully extracted Visitor Access Ticket Info");

		}


	// Retrieve POP/POE ticket details - Get By ID
	//GAPI-6277-Retrieve POP/POE ticket details - Get By ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetPOPPOERequestByID(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		CreatePOPPOERequest poppoeRequest = new CreatePOPPOERequest();
		String body = poppoeRequest.createPOPOEPayLoad(data);
		System.out.println(body);
		Response response = poppoeRequest.createPOPPOERequest(Token, body);
		String WONumber = tc.getWOIDFromResponse(response);
		System.out.println("Response: " + response.asString());
		System.out.println("WONumber: " + WONumber);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String date = tc.getCurrentDateAndTime();
		if (response.getStatusCode() == 200) {

			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Visitor access request");
			logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "poppoe_" + WONumber);

		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String getAPIURI = domain + "/facility-access/access-tickets/" + WONumber;
		Response getResponse = tc.getRequest(Token, masterAccountID, accountID, getAPIURI);
		System.out.println(getAPIURI);
		System.out.println("Response: " + getResponse.asString());
		JsonPath responseJsonPath = getResponse.jsonPath();
		String accessStartDate = tc.getCurrentDateAndTime();
		String accessEndDate = tc.getCurrentDateTime();
		String visitorType = "Escorted visitor";
		if (data.get(visitorType) == "false") {
			visitorType = "Unescorted visitor";
		}

		Assert.assertEquals(responseJsonPath.get("emailNotifications"), data.get("emailNotifications"));
		Assert.assertEquals(responseJsonPath.get("ticketId"), WONumber);
		Assert.assertEquals(responseJsonPath.get("ticketType"), "Security");
		Assert.assertEquals(responseJsonPath.get("requestType"), "PoP/POE Room Access");
		Assert.assertEquals(responseJsonPath.get("contactFirstName"), data.get("visitorFirstName"));
		Assert.assertEquals(responseJsonPath.get("contactLastName"), data.get("visitorLastName"));
		Assert.assertEquals(responseJsonPath.get("contactEmail"), data.get("visitorEmail"));
		Assert.assertEquals(responseJsonPath.get("contactPhone"), data.get("visitorPhone"));
		Assert.assertEquals(responseJsonPath.get("representingCompany"), data.get("company"));
		Assert.assertEquals(responseJsonPath.get("visitorBadgeStartDate"), accessStartDate);
		Assert.assertEquals(responseJsonPath.get("visitorBadgeEndDate"), accessEndDate);
		Assert.assertEquals(responseJsonPath.get("visitorType"), visitorType);
		Assert.assertEquals(responseJsonPath.get("visitorHostFirstName"), data.get("visitorHostFirstName"));
		Assert.assertEquals(responseJsonPath.get("visitorHostLastName"), data.get("visitorHostLastName"));
		Assert.assertEquals(responseJsonPath.get("hostCompany"), data.get("visitorHostCompany"));
		Assert.assertEquals(responseJsonPath.get("notes"), data.get("notes"));
		Assert.assertEquals(responseJsonPath.get("visitorBadgeStartDate"), accessStartDate);
		Assert.assertEquals(responseJsonPath.get("visitorBadgeEndDate"), accessEndDate);
		Assert.assertEquals(responseJsonPath.get("bulkRequest").toString(), data.get("isBulkRequest"));
		Assert.assertEquals(responseJsonPath.get("site[0].site"), data.get("site"));
		Assert.assertEquals(responseJsonPath.get("site[0].locations[0].location"), data.get("locations"));
		Assert.assertEquals(responseJsonPath.get("escortFirstName"), data.get("escortFirstName"));
		Assert.assertEquals(responseJsonPath.get("escortLastName"), data.get("escortLastName"));

		logger.log(Status.PASS, "Retrieved all the POP POE  Access ticket details - Get By ID");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after Retrieving all the POP POE  Access ticket details by ID"
						+ getResponse.statusLine());
	}

	// Retrieve OSP ticket details - Get By ID
	//GAPI-6276-Retrieve OSP ticket details - Get By ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetOSPAccessRequestByID(Map<String, String> data) throws IOException, InterruptedException {

		// logger = extent.createTest("verifyCreateVisitorAccessRequest");
		logger = extent.createTest(data.get("TestCaseName"));
		CreateFAOSPRequest ospticket = new CreateFAOSPRequest();
		JSONObject jobject = ospticket.createPayloadForOSPTicket(data);
		String body = jobject.toString();
		// String apiURI = domain + path;
		System.out.println(body);
		String apiURI = domain + "/facility-access/osps/access-requests";
		Response response = tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
		String WONumber = tc.getWOIDFromResponse(response);
		System.out.println("Response: " + response.asString());
		System.out.println("WONumber: " + WONumber);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String date = tc.getCurrentDateAndTime();
		if (response.getStatusCode() == 200) {

			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Visitor access request");
			logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "poppoe_" + WONumber);

		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String getAPIURI = domain + "/facility-access/access-tickets/" + WONumber;
		Response getResponse = tc.getRequest(Token, masterAccountID, accountID, getAPIURI);
		System.out.println("Response: " + getResponse.asString());
		JsonPath responseJsonPath = getResponse.jsonPath();
		String accessStartDate = tc.getCurrentDateAndTime();
		String accessEndDate = tc.getCurrentDateTime();
		String visitorType = "Escorted visitor";
		if (data.get(visitorType) == "false") {
			visitorType = "Unescorted visitor";
		}

		Assert.assertEquals(responseJsonPath.get("emailNotifications"), data.get("emailNotifications"));
		Assert.assertEquals(responseJsonPath.get("ticketId"), WONumber);
		Assert.assertEquals(responseJsonPath.get("ticketType"), "Security");
		Assert.assertEquals(responseJsonPath.get("requestType"), "OSP Access");
		Assert.assertEquals(responseJsonPath.get("contactFirstName"), data.get("visitorFirstName"));
		Assert.assertEquals(responseJsonPath.get("contactLastName"), data.get("visitorLastName"));
		Assert.assertEquals(responseJsonPath.get("contactEmail"), data.get("visitorEmail"));
		Assert.assertEquals(responseJsonPath.get("contactPhone"), data.get("visitorPhone"));
		// Assert.assertEquals(responseJsonPath.get("representingCompany"),
		// data.get("company"));
		Assert.assertEquals(responseJsonPath.get("visitorBadgeStartDate"), accessStartDate);
		Assert.assertEquals(responseJsonPath.get("visitorBadgeEndDate"), accessEndDate);
		Assert.assertEquals(responseJsonPath.get("visitorType"), visitorType);
		Assert.assertEquals(responseJsonPath.get("visitorHostFirstName"), data.get("visitorHostFirstName"));
		Assert.assertEquals(responseJsonPath.get("visitorHostLastName"), data.get("visitorHostLastName"));
		Assert.assertEquals(responseJsonPath.get("hostCompany"), data.get("visitorHostCompany"));
		// Assert.assertEquals(responseJsonPath.get("notes"), data.get("notes"));
		Assert.assertEquals(responseJsonPath.get("visitorBadgeStartDate"), accessStartDate);
		Assert.assertEquals(responseJsonPath.get("visitorBadgeEndDate"), accessEndDate);
		Assert.assertEquals(responseJsonPath.get("bulkRequest").toString(), data.get("isBulkRequest"));
		Assert.assertEquals(responseJsonPath.get("site[0].site"), data.get("site"));
		Assert.assertEquals(responseJsonPath.get("site[0].locations[0].location"), data.get("locations"));
		Assert.assertEquals(responseJsonPath.get("escortFirstName"), data.get("escortFirstName"));
		Assert.assertEquals(responseJsonPath.get("escortLastName"), data.get("escortLastName"));

		logger.log(Status.PASS, "Retrieved all the OSP   Access ticket details - Get By ID");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after Retrieving all the OSP Accessticket details by ID"
						+ getResponse.statusLine());
	}

	// Retrieve PermanentBadge details - Get By ID
	//GAPI-6275-Retrieve Permanent Badge ticket details - Get By ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetPermanentBadgeRequestByID(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		CreatePermanentBadgeRequest pbrequest = new CreatePermanentBadgeRequest();
		String body = pbrequest.createPermanentBadgePayLoad(data);
		System.out.println(body);
		String apiURI = domain + "/facility-access/perm-badges";
		Response response = tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
		String WONumber = tc.getWOIDFromResponse(response);
		System.out.println("Response: " + response.asString());
		System.out.println("WONumber: " + WONumber);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String date = tc.getCurrentDateAndTime();
		if (response.getStatusCode() == 200) {

			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Visitor access request");
			logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "poppoe_" + WONumber);

		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String getAPIURI = domain + "/facility-access/access-tickets/" + WONumber;
		Response getResponse = tc.getRequest(Token, masterAccountID, accountID, getAPIURI);
		System.out.println("Response: " + getResponse.asString());
		JsonPath responseJsonPath = getResponse.jsonPath();
		String accessStartDate = tc.getCurrentDateAndTime();
		String accessEndDate = tc.getCurrentDateTime();
		String visitorType = "Escorted visitor";
		if (data.get(visitorType) == "false") {
			visitorType = "Unescorted visitor";
		}

		Assert.assertEquals(responseJsonPath.get("emailNotifications"), data.get("emailNotifications"));
		Assert.assertEquals(responseJsonPath.get("ticketId"), WONumber);
		Assert.assertEquals(responseJsonPath.get("ticketType"), "Security");
		// Assert.assertEquals(responseJsonPath.get("requestType"), "Visitor Access");
		Assert.assertEquals(responseJsonPath.get("contactFirstName"), data.get("visitorFirstName"));
		Assert.assertEquals(responseJsonPath.get("contactLastName"), data.get("visitorLastName"));
		Assert.assertEquals(responseJsonPath.get("contactEmail"), data.get("visitorEmail"));
		Assert.assertEquals(responseJsonPath.get("contactPhone"), data.get("visitorPhone"));
		// Assert.assertEquals(responseJsonPath.get("representingCompany"),
		// data.get("company"));
		Assert.assertEquals(responseJsonPath.get("visitorBadgeStartDate"), accessStartDate);
		Assert.assertEquals(responseJsonPath.get("visitorBadgeEndDate"), accessEndDate);
		Assert.assertEquals(responseJsonPath.get("visitorType"), visitorType);
		Assert.assertEquals(responseJsonPath.get("notes"), data.get("notes"));
		Assert.assertEquals(responseJsonPath.get("site[0].site"), data.get("site"));
		Assert.assertEquals(responseJsonPath.get("site[0].locations[0].location"), data.get("locations"));

		logger.log(Status.PASS, "Retrieved all the POEP POE  Access ticket details - Get By ID");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after Retrieving all the Permanent Badge  Access ticket details by ID"
						+ getResponse.statusLine());
	}
}
