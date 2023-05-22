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

public class CancelRemoteHandsTicket extends BaseClass {

	public String path = "/remotehands-tickets";
	static TestUtilities tc = new TestUtilities();
	CreateRemoteHandsTicket createRemoteHandsTicket = new CreateRemoteHandsTicket();
	static String date = tc.getCurrentDateAndTime();

	// Editing the Remote Hands ticket with Notification Recipients
	//GAPI-3994-RH - Cancel Remote Hands Ticket in Status - New   ( with mandatory fields ID and Cancel Reason)


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCancelRemoteHandsTicket(Map<String, String> data) throws IOException, InterruptedException {

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
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "RH_ID " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Remote Hands Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String cancelReason = "Duplicate request";
		body = "{\r\n  \"cancelReason\": \""+cancelReason+"\"\r\n}";
		System.out.println("Cancel PayLoad is : " + body);
		log.info("Created pay load with Cancel Reason");
		logger.log(Status.PASS, "Created pay load with Cancel Reason");
		Response cancelResponse = cancelRemoteHandsTicket(Token, body, WONumber);
		log.info("Cancelled the Remote Hands ticket");
		logger.log(Status.PASS, "Cancelled the Remote Hands ticket");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + cancelResponse.statusLine());
		System.out.println("Response is : " + cancelResponse.asString());
		String message = "We have received your request for cancellation of workorder '" +WONumber
				+ "'. We will be in contact shortly. Please be aware that fees might still apply to this request if work has already started.";

		Assert.assertEquals(cancelResponse.jsonPath().get("message"), message);
		actualStatusCode = cancelResponse.getStatusCode();
		expectedStatusCode = "200";
		if (cancelResponse.getStatusCode() == 200) {
			logger.log(Status.PASS, "Successfully Cancelled Remote Hands Ticket- " + WONumber);
			log.info("Successfully Cancelled Remote Hands Ticket - " + WONumber);
		} else {
			logger.fail("Error Response message is " + response.asString());
			logger.log(Status.FAIL, "Not allowed to Cancel Remote Hands Ticket");
		}
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		System.out.println("Response after Cancelling the ticket: " + cancelResponse.asString());
	}
	
	  //Cancel Remote Hands ticket without specifying Mandatory fields ID and/or Cancel Reason
	//GAPI-3995-RH - Cancel Remote Hands ticket without specifying Mandatory fields ID and/or Cancel Reason
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyCancelRemoteHandsTicketwithoutID(Map<String, String> data) throws IOException, InterruptedException {

				logger = extent.createTest(data.get("TestCaseName"));
				System.out.println(data.get("TestCaseName") + "\n");
				String cancelReason = "Duplicate request";
				String body = "{\r\n  \"cancelReason\": \""+cancelReason+"\"\r\n}";
				System.out.println("Cancel PayLoad is : " + body);
				log.info("Created pay load with Cancel Reason");
				logger.log(Status.PASS, "Created pay load with Cancel Reason");
				String WONumber="123";
				Response cancelResponse = cancelRemoteHandsTicket(Token, body, WONumber);
				log.info("Cancelled the Remote Hands ticket");
				logger.log(Status.PASS, "Cancelled the Remote Hands ticket");
				logger.log(Status.PASS,
						"Response Status Code and Status Message is after editing the ticket" + cancelResponse.statusLine());
				System.out.println("Response is : " + cancelResponse.asString());
				int actualStatusCode = cancelResponse.getStatusCode();
				String expectedStatusCode = data.get("expectedStatusCode");
				Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");
				tc.verifyErrorResponseMessage(cancelResponse.asString(),expectedErrorMessageType,expectedErrorMessage);
				
				if (cancelResponse.getStatusCode() == 404) {
					logger.log(Status.PASS, "Mandatory field ID was missing and Not Allowed to Cancel" );
					logger.log(Status.PASS, "Mandatory field ID was missing and Not Allowed to Cancel" );
					log.info("Mandatory field ID was missing and Not Allowed to Cancel");
					logger.log(Status.PASS, "Status Code and Status Message is" + cancelResponse.getStatusLine());
				} else {
					logger.fail("Error Response message is " + cancelResponse.asString());
					logger.log(Status.FAIL, "Not allowed to Cancel Remote Hands Ticket");
				}
			}
		


			// Cancel the RemoteHands ticket
			public Response cancelRemoteHandsTicket(String Token, String body, String WONumber) throws IOException {

				Response response = RestAssured.given().relaxedHTTPSValidation()
						.header("Authorization", "Bearer " + Token)
						.header("Content-Type", "application/json")
						.header("Master-Account-Id", masterAccountID)
						.header("Account-Id", accountID)
						.body(body).put(domain + path + "/" + WONumber + "/cancel")
						.then().extract().response();
					 
				return response;
			}

}
