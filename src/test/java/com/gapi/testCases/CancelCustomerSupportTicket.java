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

public class CancelCustomerSupportTicket extends BaseClass {

	public static String path = "/customer-support-tickets";
	static TestUtilities tc = new TestUtilities();
	CreateCustomerSupportTicket createCSTicket= new CreateCustomerSupportTicket();
	static String date = tc.getCurrentDateAndTime();

	//Canceling the Customer Support ticket
	//GAPI-3942-CS - Cancel Customer Support Ticket in Status - New   (with mandatory fields ID and Cancel Reason as 'Request no longer needed')
	//GAPI-3941-CS - Cancel Customer Support Ticket in Status - New   (with mandatory fields ID and Cancel Reason as 'Request entered in error')
	//GAPI-3851-CS - Cancel Customer Support ticket without specifying Mandatory fields ID or Cancel Reason
	//GAPI-3836	CS - Add Comment to Existing CS Tickets without using both Mandatory fields RequestID and Comment

	@Test(groups ={"All"},dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCancelCustomerSupportTicket(Map<String, String> data) throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName"));
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createCSTicket.createCustomerSupportTicket(Token, body);
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
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "CS_ID " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Customer Support Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}
		
		String cancelReason = data.get("cancelReason");
		body = "{\r\n  \"cancelReason\": \""+cancelReason+"\"\r\n}";
		System.out.println("Cancel PayLoad is : " + body);
		log.info("Created pay load with Cancel Reason");
		logger.log(Status.PASS, "Created pay load with Cancel Reason");
		Response cancelResponse = cancelCustomerSupportTicket(Token, body, WONumber);
		log.info("Cancelled the Customer Support ticket with CancelReason "+cancelReason);
		logger.log(Status.PASS, "Cancelled the Customer Support ticket with CancelReason "+cancelReason);
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + cancelResponse.statusLine());
		System.out.println("Response is : " + cancelResponse.asString());
		String message = "ServiceTicket '" +WONumber+ "' has been Cancelled";

		Assert.assertEquals(cancelResponse.jsonPath().get("message"), message);
		actualStatusCode = cancelResponse.getStatusCode();
		expectedStatusCode = "200";
		if (cancelResponse.getStatusCode() == 200) {
			logger.log(Status.PASS, "Successfully Cancelled Customer Support- " + WONumber);
			log.info("Successfully Cancelled Customer Support Ticket - " + WONumber);
		} else {
			logger.fail("Error Response message is " + response.asString());
			logger.log(Status.FAIL, "Not allowed to Cancel Customer Support Ticket");
		}
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		System.out.println("Response after Cancelling the ticket: " + cancelResponse.asString());
	}

	// Cancel the Customer Support ticket
	public Response cancelCustomerSupportTicket(String Token, String body, String WONumber) throws IOException {

		String apiURI=domain + path + "/" + WONumber + "/cancel";		
		Response response = tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
		
		return response;
	}

}
