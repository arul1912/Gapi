package com.gapi.testCases;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreateShipmentsTicket extends BaseClass {

	public static String path = "/shipment-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();

	// Create shipments tickets
	//GAPI-4010-SR- Submit S&R tickets - enter an invalid Courier company   (DHL, FedEx, DPD, TNT, UPS, USPS, Other - are valid couriers)
	//GAPI-4015-SR- Submit  S&R tickets with Optional parameters - Customer Reference Number
	//GAPI-4012-SR- Submit S&R tickets with Optional parameters - Number of Packages (Package Count)
	//GAPI-4014-SR- Submit S&R tickets with Optional parameters - Tracking Number
	//GAPI-4009-SR- Submit S&R tickets with Mandatory parameters - Description, Title, Site, Courier Company
	//GAPI-4013-SR- Submit S&R tickets with Optional parameters - Estimated Delivery Date


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateShipmentsTicket(Map<String, String> data) throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createShipmentsTicket(Token, body);
		System.out.println("Request is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode() == 201) {
			String WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Shipments Ticket");
			logger.log(Status.PASS, "Created Customer Support Ticket Id Is " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
			tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
		} else {
			logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

	}

	//GAPI-4024	SR- Submit Shipping & Receiving Request with 1 Attachment (with mandatory parameters RequestID and filename)

	// verify shipments end to end flow
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createShipmentsTicketAndVerifyEndToEndFlow(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request Payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createShipmentsTicket(Token, body);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		log.info("Created Shipments Ticket");
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String WONumber = tc.getWONumberFromResponse(response);
		log.info("The WO Number Created is " + WONumber);
		logger.log(Status.PASS, "Created Shipments Ticket Id Is  " + WONumber);
		tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
		tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
		tc.addAttachmentToTheRequestAndVerify(WONumber, path);
		tc.addCommentToTheSHRequestAndVerify(WONumber, path);
		

	}

	// Create SH Ticket with Loading bay Required
	//GAPI-4016-SR- Submit  S&R tickets with Optional parameters - Loading Bay Required
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateSHTicketForWithLoadingBayRequired(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		JSONObject shipmentsTicketdetails = new JSONObject();
		shipmentsTicketdetails.put("site", data.get("site"));
		shipmentsTicketdetails.put("title", data.get("title"));
		shipmentsTicketdetails.put("description", data.get("description"));
		shipmentsTicketdetails.put("courier", data.get("courier"));
		shipmentsTicketdetails.put("customerReference", data.get("customerReference"));
		shipmentsTicketdetails.put("trackingNumber", data.get("trackingNumber"));
		shipmentsTicketdetails.put("isLoadingBayRequired", Boolean.valueOf(data.get("isLoadingBayRequired")));

		System.out.println("Body is------ :");
		System.out.println(shipmentsTicketdetails.toString());

		String apiURI = domain + path;
		System.out.println(apiURI);
		String body = shipmentsTicketdetails.toString();
		Response response = createShipmentsTicket(Token, body);

		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		Assert.assertEquals(201, response.getStatusCode());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode() == 201) {
			String WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Shipments Ticket");
			logger.log(Status.PASS, "Created Customer Support Ticket Id Is " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
			Assert.assertTrue(response.jsonPath().get("isLoadingBayRequired"));
		} else {
			logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

	}

	// Create SH Ticket with Invalid user RBAC
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateSHTicketForInvalidUser(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		JSONObject shipmentsTicketdetails = new JSONObject();
		shipmentsTicketdetails.put("site", data.get("site"));
		shipmentsTicketdetails.put("title", data.get("title"));
		shipmentsTicketdetails.put("description", data.get("description"));
		shipmentsTicketdetails.put("courier", data.get("courier"));

		System.out.println("Body is------ :");
		System.out.println(shipmentsTicketdetails.toString());

		String apiURI = domain + path;
		System.out.println(apiURI);
		String jsonbody = shipmentsTicketdetails.toString();
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + TokenCC)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(jsonbody).post(domain + path).then().extract().response();

		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		Assert.assertEquals(401, response.getStatusCode());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		if (response.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created RH Ticket");
			logger.log(Status.PASS, "Created RH Ticket  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "sh_" + WONumber);
			// tc.retrieveFAGETResponseAndRequestParameters(WONumber, jsonbody,
			// "service-tickets?id=");
		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create RH Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");

		}
	}

	// arul on 08-03-2023 invalid estimatedDeliveryDate format
	//GAPI-4011-SR- Submit  S&R tickets with invalid date format for Estimated delivery time

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void invalidEstimatedDeliveryDateFormatSHTicket(Map<String, String> data)
			throws IOException, InterruptedException {
		String path = "/shipment-tickets";
		logger = extent.createTest(data.get("TestCaseName"));
		String requestbody = "{\r\n  \"title\": \"" + data.get("title") + "\",\r\n  \"site\": \"" + data.get("site")
				+ "\"," + "\r\n  \"description\": \"" + data.get("description") + "\",\r\n  \"courier\": \""
				+ data.get("courier") + "\"," + "\r\n \"estimatedDeliveryDate\": \"" + data.get("estimatedDeliveryDate")
				+ "\"\r\n}";

		System.out.println("Body is: " + requestbody);
		System.out.println("Body is------ :");
		String apiURI = domain + path;
		System.out.println(apiURI);
		Response requestresponse = tc.postRequest(Token, requestbody, masterAccountID, accountID, apiURI);
		System.out.println("Response is: " + requestresponse.asString());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + requestresponse.statusLine());

		System.out.println("Response is: " + requestresponse.asString());
		System.out.println("Status Message is: " + requestresponse.statusLine());
		logger.log(Status.INFO, requestresponse.statusLine());
		Assert.assertEquals(requestresponse.getStatusCode(), 201);
		Assert.assertTrue(requestresponse.statusLine().contains("HTTP/1.1 201"));
		log.info("Successfully extracted all get Reports Info ");
		logger.log(Status.INFO, "Status Code and Status Message is" + requestresponse.getStatusLine());
		logger.log(Status.INFO, "Successfully extracted getreports Info");

	}

	// arul 08-03-2023 posting Attachment Of An Existing ShippingReceivingTicket
	//GAPI-4025-SR- Submit Shipping & Receiving Request with multiple Attachments  ( posting 1 attachment at a time)
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void postingAttachmentOfAnExistingShippingReceivingTicket(Map<String, String> data) throws IOException {
		String body = tc.getRequestBody(data);
		String path = "/attachments";
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request Payload data is� \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		String attachmentfilter = data.get("filters");
		String apiURI = domain + path + attachmentfilter + "WONumber";
		System.out.println("API URL by ID: " + apiURI);
		File testUploadFile = new File(
				System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
		Response responsepst = tc.postAttachmentRequest(Token, masterAccountID, accountID, apiURI, testUploadFile);
		log.info("Attached file to the Shipments ticket");
		logger.log(Status.PASS, "Successfully attached attachment to the Shipments ticket");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
		System.out.println("Response is : " + responsepst.asString());

	}

	// create shipments ticket with Invalid Notification recipient
	//GAPI-4022-SR- Submit a S&R tickets - with invalid Notification Recipient email format

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void CreateShipmentsTicketeithInvalidNotificationRecipient(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createShipmentsTicket(Token, body);
		System.out.println("Request is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode() == 201) {
			String WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Shipments Ticket");
			logger.log(Status.PASS, "Created Customer Support Ticket Id Is " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
			tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);

		}

		else {
			logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}
	}

	// without the mandatory fields
	//GAPI-4029-SR Submit Remote hand without mandatory fields RequestID and filename

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyRequestIdAndFileNameSHTicketForWithoutmandatoryfieldsRequired(Map<String, String> data)
			throws IOException, InterruptedException {
		String path = "/shipment-tickets";
		logger = extent.createTest(data.get("TestCaseName"));
		String requestbody = "{\r\n  \"title\": \"title sample1\"\r\n \r\n \r\n}";

		System.out.println("Body is: " + requestbody);
		System.out.println("Body is------ :");
		String apiURI = domain + path;
		System.out.println(apiURI);
		Response requestresponse = tc.postRequest(Token, requestbody, masterAccountID, accountID, apiURI);
		System.out.println("Response is: " + requestresponse.asString());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + requestresponse.statusLine());

		System.out.println("Response is: " + requestresponse.asString());
		System.out.println("Status Message is: " + requestresponse.statusLine());
		logger.log(Status.INFO, requestresponse.statusLine());
		Assert.assertEquals(requestresponse.getStatusCode(), 400);
		Assert.assertTrue(requestresponse.statusLine().contains("HTTP/1.1 400"));
		log.info("Successfully extracted all get Reports Info ");
		logger.log(Status.INFO, "Status Code and Status Message is" + requestresponse.getStatusLine());
		logger.log(Status.INFO, "Successfully extracted getreports Info");

	}

	// Shipping & Receiving Request with Various file types/formats
	//GAPI-4026-SR- Submit Shipping & Receiving Request with Various file types/formats

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void AddAttachmentstoShipmentsTicket(Map<String, String> data) throws IOException {
		String body = tc.getRequestBody(data);
		String path = "/attachments";
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		String attachmentfilter = data.get("filters");
		String apiURI = domain + path + attachmentfilter + "WONumber";
		System.out.println("API URL by ID: " + apiURI);
		File testUploadFile = new File(
				System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
		Response responsepst = tc.postAttachmentRequest(Token, masterAccountID, accountID, apiURI, testUploadFile);
		log.info("Attached file to the Shipments ticket");
		logger.log(Status.PASS, "Successfully attached attachment to the Shipments ticket");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
		System.out.println("Response is : " + responsepst.asString());
	}

	// *********************************Verify Max # of characters and data types
	// for each mandatory and optional parameters******************
	//GAPI-4023-SR - Verify Max # of characters and data types for each mandatory and optional parameters

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyMaxCharactersOfShipmentsParameters(Map<String, String> data)
			throws IOException, InterruptedException {

		System.out.println(data.get("TestCaseName") + "\n");
		logger = extent.createTest(data.get("TestCaseName"));
		Faker faker = new Faker();
		Random rand = new Random();
		String site = faker.lorem().characters(10);
		String title = faker.lorem().characters(160);
		String description = faker.lorem().characters(32000);
		String customerRef = faker.lorem().characters(40);
		int packageCount = rand.nextInt(40);
		String body = ("{\r\n    \"title\": \"" + title + "\",\r\n    \"site\": \"" + site
				+ "\",\r\n    \"description\": \"" + description + "\",\r\n    \"packageCount\": \"" + packageCount
				+ "\",\r\n    "
				+ "\"specialInstructions\": \"string\",\r\n    \"courier\": \"USP\",\r\n    \"customerReference\": \""
				+ customerRef + "\",\r\n    "
				+ "\"trackingNumber\": \"1\",\r\n    \"isLoadingBayRequired\": true,\r\n    \"needRemoteHandsTicket\": true,\r\n    "
				+ "\"estimatedDeliveryDate\": \"\",\r\n    \"notificationRecipients\": \"jane_doe@email.com\"\r\n}");
		log.info("Created Body within the maximum characters");
		Response response = createShipmentsTicket(Token, body);
		System.out.println("Request is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
	}

	// Re-usable method to create Shipments Ticket

	public static Response createShipmentsTicket(String Token, String body) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(body).post(domain + path).then().extract().response();

		return response;
	}

}
