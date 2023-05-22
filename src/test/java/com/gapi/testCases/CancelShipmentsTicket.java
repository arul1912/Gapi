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

public class CancelShipmentsTicket extends BaseClass {
	

	public String path = "/shipment-tickets";
	static TestUtilities tc = new TestUtilities();
	CreateShipmentsTicket createShipmentsTicket = new CreateShipmentsTicket();
	static String date = tc.getCurrentDateAndTime();
	
	  //Cancel Shipments ticket specifying Mandatory fields ID and without Cancel Reason
	  //GAPI-4076-SR - Cancel S&R Ticket in Status - New ( specifying ID - WO Number only WITHOUT cancel reason)    (verify Cancel reason defaults to Request No longer needed)

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyCancelShipmentsTicketwithoutCancelReason(Map<String, String> data) throws IOException, InterruptedException {
		
		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
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
			logger.log(Status.PASS, "Successfully Created Shipments Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_ " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}
		String cancelReason = "Request no longer needed";
		body = "{\r\n  \"cancelReason\": \""+cancelReason+"\"\r\n}";
		System.out.println("Cancel PayLoad is : " + body);
		log.info("Created pay load with Cancel Reason");
		logger.log(Status.PASS, "Created pay load with Cancel Reason");
		Response cancelResponse = cancelShipmentsTicket(Token, body, WONumber);
		log.info("Cancelled the Shipments ticket");
		logger.log(Status.PASS, "Cancelled the Shipments ticket");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + cancelResponse.statusLine());
		System.out.println("Response is : " + cancelResponse.asString());
		String message = "Work Order "+WONumber+" has been cancelled.";

		Assert.assertEquals(cancelResponse.jsonPath().get("message"), message);
		actualStatusCode = cancelResponse.getStatusCode();
		expectedStatusCode = "200";
		if (cancelResponse.getStatusCode() == 200) {
			logger.log(Status.PASS, "Successfully Cancelled Remote Hands Ticket- " + WONumber);
			log.info("Successfully Cancelled Shipments Ticket - " + WONumber);
		} else {
			logger.fail("Error Response message is " + response.asString());
			logger.log(Status.FAIL, "Not allowed to Cancel Shipments Ticket");
		}
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		System.out.println("Response after Cancelling the ticket: " + cancelResponse.asString());
	}
	
	//GAPI-4075-SR - Cancel S&R Ticket in Status - New ( specifying ID - WO Number &cancel reason)    (verify Cancel reason-  "Duplicate request")
	//GAPI-4074-SR - Cancel S&R Ticket in Status - New ( specifying ID - WO Number & cancel reason)    (verify Cancel reason-  Request entered in error)

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCancelShipmentsTicketWithCancelReason(Map<String, String> data) throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName"));
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
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
			logger.log(Status.PASS, "Successfully Created Shipments Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "CS_ID " + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to Shipment Support Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String cancelReason = data.get("cancelReason");
		body = "{\r\n  \"cancelReason\": \""+cancelReason+"\"\r\n}";
		System.out.println("Cancel PayLoad is : " + body);
		log.info("Created pay load with Cancel Reason");
		logger.log(Status.PASS, "Created pay load with Cancel Reason");
		Response cancelResponse = cancelShipmentTicket(Token, body, WONumber);
		log.info("Cancelled the Shipment ticket with CancelReason "+cancelReason);
		logger.log(Status.PASS, "Cancelled the Shipment ticket with CancelReason "+cancelReason);
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + cancelResponse.statusLine());
		System.out.println("Response is : " + cancelResponse.asString());
		String message = "Work Order "+WONumber+" has been cancelled.";


		Assert.assertEquals(cancelResponse.jsonPath().get("message"), message);
		actualStatusCode = cancelResponse.getStatusCode();
		expectedStatusCode = "200";
		if (cancelResponse.getStatusCode() == 200) {
			logger.log(Status.PASS, "Successfully Cancelled Shipment- " + WONumber);
			log.info("Successfully Cancelled Shipment Ticket - " + WONumber);
		} else {
			logger.fail("Error Response message is " + response.asString());
			logger.log(Status.FAIL, "Not allowed to Cancel Shipment Ticket");
		}
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		System.out.println("Response after Cancelling the ticket: " + cancelResponse.asString());
	}

	// Cancel the Shipments ticket
	public Response cancelShipmentTicket(String Token, String body, String WONumber) throws IOException {

		String apiURI=domain + path + "/" + WONumber + "/cancel";		
		Response response = tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
		
		return response;
	}
	
	// Cancel the Shipments ticket
				public Response cancelShipmentsTicket(String Token, String body, String WONumber) throws IOException {

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