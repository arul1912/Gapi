package com.gapi.testCases;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.mail.internet.ContentDisposition;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AddAttachmentsToTheRemoteHandsTicket extends BaseClass {

	String path = "/remotehands-tickets";
	CreateRemoteHandsTicket createRemoteHandsTicket = new CreateRemoteHandsTicket();
	static TestUtilities tc = new TestUtilities();

	// Re-usable Method to Add Attachment to the Existing valid Tickets
	// ********************************************************************************************

	public String uploadAttachmentToTheExistingTicket(String Id) throws IOException {

		logger.createNode("uploadAttachmentToTheExistingTicket");
		File testUploadFile = new File(
				System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Attachment.txt");

		String apiURI = domain + "/attachments?file_name=test&requestId=" + Id;
		System.out.println(apiURI);
		Response response = tc.postAttachmentRequest(Token, masterAccountID, accountID, apiURI, testUploadFile);
		String responseBody = response.getBody().asString();
		System.out.println("Response is:" + responseBody);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.asString().contains("filename"));
		String actualFileName = getAttachmentFileInfoFromResponse(response.asString());
		Assert.assertEquals(actualFileName, "test");
		JSONObject object = new JSONObject(responseBody);
		String id = object.getString("id");
		String hash = object.getString("hash");
		String createdBy = object.getString("hash");
		String createdOn = object.getString("hash");
		Assert.assertTrue(!hash.isEmpty());
		Assert.assertTrue(!id.isEmpty());
		Assert.assertTrue(!createdBy.isEmpty());
		Assert.assertTrue(!createdOn.isEmpty());
		log.info("Successfully Posted attachment to the Existing Ticket ");
		logger.log(Status.INFO, "Posted Attachment to the Existing Ticket");

		return responseBody;

	}

	public static String getAttachmentFileInfoFromResponse(String responseBody) {

		JSONObject object = new JSONObject(responseBody);
		String ExpectedFilename = object.getString("filename");
		return ExpectedFilename;

	}

	public String getAttachmentDetailsByRequestId(String Id) {

		logger.createNode("getAttachmentInfoByRequestId");
		String apiURI = domain + "/attachments/" + Id;
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "msirikonda@digitalrealty.com").get(apiURI);

		Assert.assertEquals(response.getStatusCode(), 200);
		// Assert.assertTrue(response.statusLine().contains("OK"));
		String allResponsebody = response.getBody().asString();

		log.info("Successfully extracted the Attachments Ticket Info ");
		logger.log(Status.PASS, "Successfully extracted theAttachments Ticket Info for " + Id);
		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response.getStatusLine());
		;

		return allResponsebody;

	}

	// REMOTE HANDS ATTACHMENTS TESTCASES
	// *********************************************************************************************

	// Add Attachment to the Existing valid Remote Hands Ticket
	//GAPI-4084-RH- Submit Remote Hands Request with 1 attachment (with mandatory parameters RequestID and filename)

			@Test
			public void uploadAttachmentToTheExistingRemoteHandsTicket() throws IOException, InterruptedException {

				logger = extent.createTest("upload Attachment to the Existing Remote Hands Ticket and verify attachment info");
				String body = "{\r\n" + "    \"site\": \"PHX015\",\r\n" + "    \"requestType\": \"Urgent Work\",\r\n"
						+ "    \"accountName\": \"TelX Group, Inc.\",\r\n" + "    \"category\": \"Other\",\r\n"
						+ "    \"title\": \"title sample\",\r\n"
						+ "    \"detailedInstruction\": \"API Test 03052021. Please locate the Cisco UCS server in rack1 and connect to the KVM. Please provide screenshots once you connect to the KVM.\"\r\n"
						+ "}";

				String apiURI = domain + path;
				Response crResponse = createRemoteHandsTicket.createRemoteHandsRequest(Token,body);
				System.out.println(crResponse.asPrettyString());
				System.out.println(crResponse.getStatusCode());
				Assert.assertEquals(crResponse.getStatusCode(), 201);
				String WONumber = tc.getWONumberFromResponse(crResponse);
				System.out.println(apiURI);
				tc.addAttachmentToTheRequestAndVerify(WONumber,path);
			}

			/*
			 * File testUploadFile = new File( System.getProperty("user.dir") +
			 * "\\src\\test\\java\\com\\gapi\\testData\\Attachment.txt"); String
			 * attachmentURI = domain + "/v1/attachments?filename=test&requestId=" +
			 * WONumber; System.out.println(apiURI); Response response =
			 * RestAssured.given().relaxedHTTPSValidation().header("Authorization",
			 * "Bearer " + Token)
			 * .contentType(ContentType.BINARY).header("Master-Account-Id", 10028)
			 * .header("Account-Id",
			 * "0013000000GbqerAAB").body(testUploadFile).post(attachmentURI).then().extract
			 * () .response();
			 * 
			 * String responseBody = response.getBody().asString();
			 * Assert.assertEquals(response.getStatusCode(), 201);
			 * Assert.assertTrue(response.asString().contains("filename")); String
			 * actualFileName = getAttachmentFileInfoFromResponse(response.asString());
			 * Assert.assertEquals(actualFileName, "test"); JSONObject object = new
			 * JSONObject(responseBody); String id = object.getString("id"); String hash =
			 * object.getString("hash"); String createdBy = object.getString("hash"); String
			 * createdOn = object.getString("hash"); Assert.assertTrue(!hash.isEmpty());
			 * Assert.assertTrue(!id.isEmpty()); Assert.assertTrue(!createdBy.isEmpty());
			 * Assert.assertTrue(!createdOn.isEmpty());
			 * log.info("Successfully Posted attachment to the Existing Ticket ");
			 * logger.log(Status.INFO, "Posted Attachment to the Existing Ticket");
			 * getAttachmentDetailsByAttachmentId(id); }
			 */

	// get Attachment info by using Attachment id
	public void getAttachmentDetailsByAttachmentId(String Id) {

		logger.createNode("getAttachmentInfoByAttachmentId");
		String apiURI = domain + "/attachments/file/" + Id;
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", 10028)
				.header("Account-Id", "0013000000GbqerAAB").get(apiURI);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		String allResponsebody = response.getBody().asString();
		Assert.assertTrue(allResponsebody.contains("SampleText Document"));

		log.info("Successfully extracted the Attachments Ticket Info by using AID ");
		logger.log(Status.PASS, "Successfully extracted theAttachments Ticket Info for " + Id);
		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response.getStatusLine());

	}

	// Verify adding empty text document to the remote hands request and verify the
	// error message
	// Verify adding empty text document to the remote hands request and verify the
		// error message
		@Test
		public void uploadEmptyTextDocumentToTheValidRemoteHandsRequest() throws IOException {

			logger = extent
					.createTest("upload empty Attachment to the Existing Remote Hands Ticket and verify Error Message");
			String body = "{\r\n" + "    \"site\": \"PHX015\",\r\n" + "    \"requestType\": \"Urgent Work\",\r\n"
					+ "    \"accountName\": \"TelX Group, Inc.\",\r\n" + "    \"category\": \"Other\",\r\n"
					+ "    \"title\": \"title sample\",\r\n"
					+ "    \"detailedInstruction\": \"API Test 03052021. Please locate the Cisco UCS server in rack1 and connect to the KVM. Please provide screenshots once you connect to the KVM.\"\r\n"
					+ "}";

			String apiURI = domain + path;
			Response crResponse = tc.createRequest(Token, body, apiURI);
			System.out.println(crResponse.asPrettyString());
			Assert.assertEquals(crResponse.getStatusCode(), 201);
			String WONumber = tc.getWONumberFromResponse(crResponse);

			File testUploadFile = new File(
					System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\emptyAttachment.txt");
			String attachmentURI = domain + "/attachments?file_name=test&requestId=" + WONumber;
			System.out.println(attachmentURI);
//			Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", Token)
//					.contentType(ContentType.BINARY).body(testUploadFile).post(attachmentURI).then().extract().response();
			
			Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
					.contentType("multipart/form-data").multiPart("file", testUploadFile)
					.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID)
					.header("User-Email", "msirikonda@digitalrealty.com").body(testUploadFile).post(attachmentURI).then().extract()
					.response();

			String responseBody = response.getBody().asString();
			System.out.println(responseBody);
			Assert.assertEquals(response.getStatusCode(), 500);
			Assert.assertTrue(response.getStatusLine().contains("Internal Server Error"));
			Assert.assertTrue(responseBody.contains("org.mozilla.javascript.Undefined@0"));
		}


	// Add attachment to Remote hands ticket based on file size
	static String path2 = "/attachments";
	//GAPI-4085-RH-Submit Remote Hands Request with Various file types/formats
	//GAPI-3992-RH- Submit Remote Hands Request with file size 1024 MB
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void AddAttachmentstoRemoteHandsTicket(Map<String, String> data) throws IOException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsTicket.createRemoteHandsRequest(Token, body);
		System.out.println("Response is: " + response.asString());
		String WONumber = tc.getWONumberFromResponse(response);
		String attachmentfilter = data.get("filters");
		String apiURI = domain + path2 + attachmentfilter + WONumber;
		System.out.println("API URL by ID: " + apiURI);
		File testUploadFile = new File(
				System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
		Response responsepst = tc.postAttachmentRequest(Token, masterAccountID, accountID, apiURI, testUploadFile);
		log.info("Attached file to the Remote Hands ticket");
		logger.log(Status.PASS, "Successfully attached attachment to the Remote Hands ticket");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
		System.out.println("Response is : " + responsepst.asString());

	}

	// Shrikanth changes on 10-03-2023
	// addAttachmentstoRemoteHandsTicketWithOutRequestIDAndfilename
	//GAPI-4087-RH-Submit Remote hand without mandatory fields RequestID and filename
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void addAttachmentstoRemoteHandsTicketWithOutRequestIDAndfilename(Map<String, String> data)
			throws IOException, InterruptedException {
		String path = "/remotehands-tickets";
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

	// *******************************Get a single Attachment related to a Remote
	// Hands ticket  (by attachment ID)***********************
	//GAPI-3993-RH-Get a single Attachment related to a Remote Hands ticket  (by attachment ID)
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getAttachmentOfRemoteHandsTicketByID(Map<String, String> data) throws IOException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsTicket.createRemoteHandsRequest(Token, body);
		System.out.println("Response is: " + response.asString());
		String WONumber = tc.getWONumberFromResponse(response);
		String attachmentfilter = data.get("filters");
		String apiURI = domain + path2 + attachmentfilter + WONumber;
		System.out.println("API URL by ID: " + apiURI);
		File testUploadFile = new File(
				System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
		Response responsepst = tc.postAttachmentRequest(Token, masterAccountID, accountID, apiURI, testUploadFile);
		log.info("Attached file to the Remote Hands ticket");
		logger.log(Status.PASS, "Successfully attached attachment to the Remote Hands ticket");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
		System.out.println("Response is : " + responsepst.asString());
		String id = tc.getIdFromResponse(responsepst);
		System.out.println("Attachment ID is : " + id);
		//String apiURI1 = domain + "/attachments/file/" + id;
		String apiURI1 = domain + "/attachments/"+WONumber+"/file/" + id+"/";
		Response responseOfAttachment = tc.getRequest(Token, masterAccountID, accountID, apiURI1);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = responseOfAttachment.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(responseOfAttachment.statusLine().contains(expectedStatusMessage));
		String allResponsebody = responseOfAttachment.getBody().asString();
	//	System.out.println(allResponsebody);
		log.info("Successfully extracted the Attachments Ticket Info by using AID ");
		logger.log(Status.PASS, "Successfully extracted theAttachments Ticket Info for " + id);
	}
}
