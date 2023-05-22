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

public class EditRemoteHandsTicket extends BaseClass {

	public String path = "/remotehands-tickets";
	static TestUtilities tc = new TestUtilities();
	CreateRemoteHandsTicket createRemoteHandsTicket = new CreateRemoteHandsTicket();
	static String date = tc.getCurrentDateAndTime();

	// Editing the Remote Hands ticket with Notification Recipients
	//GAPI-4002-RH- Edit the Notification Recipients of an Existing RH Ticket in New Status  (add multiple recipients)

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyEditNotificationRecipientsOfAnExistingRemoteHandsTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsTicket.createRemoteHandsRequest(Token, body);
		System.out.println("Response is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String WONumber = tc.getWONumberFromResponse(response);

		if (response.getStatusCode() == 201) {
			WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Remote Hands Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Remote_Hands_ID " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Remote Hands Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String notificationRecipients = "updatedNotification@test.com,updatedNotification2@test.com";
		body = "{\r\n  \"notificationRecipients\": \"" + notificationRecipients + "\"\r\n}";

		log.info("Created pay load with updated Notification Recipients");
		logger.log(Status.PASS, "Created pay load with updated Notification Recipients");
		Response editResponse = editRemoteHandsTicket(Token, body, WONumber);

		if (editResponse.getStatusCode() == 200) {
			log.info("Edited the Remote Hands ticket with updated Notification Recipients");
			logger.log(Status.PASS, "Edited the Remote Hands ticket with updated Notification Recipients");
			logger.log(Status.PASS,
					"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
			Assert.assertEquals(editResponse.jsonPath().get("notificationRecipients"), notificationRecipients);
			actualStatusCode = editResponse.getStatusCode();
			expectedStatusCode = "200";
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			System.out.println("Response after updating: " + editResponse.asString());
			log.info("Updated Notification Recipients is reflecting Correctly.");
			logger.log(Status.PASS, "Updated Notification Recipients is reflecting Correctly.");
		}
		else
		{
			logger.log(Status.FAIL, "Not allowed to Edit Remote Hands Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

	}

	// Editing the Customer Support ticket with Customer Reference
	//GAPI-4001-RH- Edit the Customer Reference of an Existing RH Ticket in New Status

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyEditCustomerReferenceOfAnExistingRemoteHandsTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsTicket.createRemoteHandsRequest(Token, body);
		System.out.println("Response is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String WONumber = tc.getWONumberFromResponse(response);

		if (response.getStatusCode() == 201) {
			WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Remote Hands Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Remote_Hands_ID " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Remote Hands Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String customerReference = "nr. RF00001245";
		body = "{\r\n  \"customerReference\": \"" + customerReference + "\"\r\n}";
		System.out.println("Request Edit payload is \n" + body);
		log.info("Created pay load with updated Customer Reference");
		logger.log(Status.PASS, "Created pay load with updated Customer Reference");
		Response editResponse = editRemoteHandsTicket(Token, body, WONumber);
		if (editResponse.getStatusCode() == 200) {
		log.info("Edited the Remote Hands ticket with updated Customer Reference");
		logger.log(Status.PASS, "Edited the Remote Hands ticket with updated Customer Reference");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
		Assert.assertEquals(editResponse.jsonPath().get("customerReference"), customerReference);
		actualStatusCode = editResponse.getStatusCode();
		expectedStatusCode = "200";
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		System.out.println("Response after updating: " + editResponse.asString());
		log.info("Updated Customer Reference is reflecting Correctly.");
		logger.log(Status.PASS, "Updated Customer Reference is reflecting Correctly.");
		}
		else
		{
			logger.log(Status.FAIL, "Not allowed to Edit Remote Hands Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

	}

	// Edit the RemoteHands ticket
	public Response editRemoteHandsTicket(String Token, String body, String WONumber) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(body).put(domain + path + "/" + WONumber).then()
				.extract().response();

		return response;
	}

}
