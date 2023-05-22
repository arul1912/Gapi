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

public class EditCustomerSupportTicket extends BaseClass {

	public String path = "/customer-support-tickets";
	static TestUtilities tc = new TestUtilities();
	CreateCustomerSupportTicket createCustomerSupportTicket = new CreateCustomerSupportTicket();
	static String date = tc.getCurrentDateAndTime();

	// Editing the Customer Support ticket with Notification Recipients
	//GAPI-3858-CS- Edit the Notification Recipients of an Existing Customer Support Ticket in New Status
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyEditNotificationRecipientsOfAnExistingCustomerSupportTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createCustomerSupportTicket.createCustomerSupportTicket(Token, body);
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
			logger.log(Status.PASS, "Successfully Created Customer Support Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Customer Support_ID " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Customer Support Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String notificationRecipients = "updatedNotification@test.com";

		body = "{\r\n  \"notificationRecipients\": \"" + notificationRecipients + "\"\r\n}";

		log.info("Created pay load with updated Notification Recipients");
		logger.log(Status.PASS, "Created pay load with updated Notification Recipients");
		Response editResponse = editCustomerSupportTicket(Token, body, WONumber);
		log.info("Edited the Customer Support ticket with updated Notification Recipients");
		logger.log(Status.PASS, "Edited the Customer Support ticket with updated Notification Recipients");
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

	// Editing the Customer Support ticket with Customer Reference
	//GAPI-3857-CS- Edit the Customer Reference of an Existing Customer Support Ticket in New Status
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyEditCustomerReferenceOfAnExistingCustomerSupportTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createCustomerSupportTicket.createCustomerSupportTicket(Token, body);
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
			logger.log(Status.PASS, "Successfully Created Customer Support Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Customer Support_ID " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Customer Support Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String customerReference = "nr. RF00001245";

		body = "{\r\n  \"customerReference\": \"" + customerReference + "\"\r\n}";

		log.info("Created pay load with updated Customer Reference");
		logger.log(Status.PASS, "Created pay load with updated Customer Reference");
		Response editResponse = editCustomerSupportTicket(Token, body, WONumber);
		log.info("Edited the Customer Support ticket with updated Customer Reference");
		logger.log(Status.PASS, "Edited the Customer Support ticket with updated Customer Reference");
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

	// Editing the Customer Support ticket with Description
	//GAPI-3856-CS- Edit the Description of an Existing Customer Support Ticket in New Status
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyEditDescriptionOfAnExistingCustomerSupportTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createCustomerSupportTicket.createCustomerSupportTicket(Token, body);
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
			logger.log(Status.PASS, "Successfully Created Customer Support Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Customer Support_ID " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Customer Support Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String description = "Updated Customer Description";

		body = "{\r\n  \"description\": \"" + description + "\"\r\n}";

		log.info("Created pay load with updated Description");
		logger.log(Status.PASS, "Created pay load with updated Description");
		Response editResponse = editCustomerSupportTicket(Token, body, WONumber);
		log.info("Edited the Customer Support ticket with updated Description");
		logger.log(Status.PASS, "Edited the Customer Support ticket with updated Description ");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
		Assert.assertEquals(editResponse.jsonPath().get("description"), description);
		actualStatusCode = editResponse.getStatusCode();
		expectedStatusCode = "200";
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		System.out.println("Response after updating: " + editResponse.asString());
		log.info("Updated Description  is reflecting Correctly.");
		logger.log(Status.PASS, "Updated Description is reflecting Correctly.");

	}
	
	// Editing the Customer Support ticket with Title
	//GAPI-3855-CS- Edit the Title of an Existing Customer Support Ticket in New Status
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyEditTitleOfAnExistingCustomerSupportTicket(Map<String, String> data)
					throws IOException, InterruptedException {

				String body = tc.getRequestBody(data);
				System.out.println(data.get("TestCaseName") + "\n");
				System.out.println("Request payload is \n" + body);
				logger = extent.createTest(data.get("TestCaseName"));
				Response response = createCustomerSupportTicket.createCustomerSupportTicket(Token, body);
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
					logger.log(Status.PASS, "Successfully Created Customer Support Ticket");
					tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Customer Support_ID " + WONumber);
				} else {
					logger.log(Status.PASS, "Not allowed to create Customer Support Ticket");
					String expectedErrorMessageType = data.get("errorMessageType");
					String expectedErrorMessage = data.get("errorMessage");
					tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
				}

				String title = "updatedTitle";
			
				body = "{\r\n  \"title\": \"" + title + "\"\r\n}";
				
				
				log.info("Created pay load with updated title");
				logger.log(Status.PASS, "Created pay load with updated title");
				Response editResponse = editCustomerSupportTicket(Token, body, WONumber);
				log.info("Edited the Customer Support ticket with updated title");
				logger.log(Status.PASS, "Edited the Customer Support ticket with updated title");
				logger.log(Status.PASS,
						"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
				Assert.assertEquals(editResponse.jsonPath().get("title"), title);
				actualStatusCode = editResponse.getStatusCode();
				expectedStatusCode = "200";
				Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
				Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
				System.out.println("Response after updating: " + editResponse.asString());
				log.info("Updated title is reflecting Correctly.");
				logger.log(Status.PASS, "Updated title is reflecting Correctly.");

			}

			
	// Edit the Customer Support ticket
	public Response editCustomerSupportTicket(String Token, String body, String WONumber) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(body).put(domain + path + "/" + WONumber).then()
				.extract().response();

		return response;
	}

}
