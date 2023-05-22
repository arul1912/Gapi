package com.gapi.testCases;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;


public class AddAttachmentToTheCustomerSupportTicket extends BaseClass {
	
	
	CreateCustomerSupportTicket csTicket=new CreateCustomerSupportTicket();
	static TestUtilities tc = new TestUtilities();
	
	// CUSTOMER SUPPORT ATTACHMENT TESTCASES
	//************************************************************************
		
		public String uploadAttachmentToTheExistingCustomerSupportTicket(String Id, String Token) {
			
			logger.createNode("uploadAttachmentToTheExistingCustomerSupportTicket");
			File testUploadFile = new File(System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Attachment.txt");   
			String apiURI = domain+"/v1/customer-services/"+Id;
			Response response = RestAssured.given()
					.header("Authorization", Token)
					.header("Content-Type", "multipart/form-data")
					.multiPart("file", testUploadFile)
					.post(apiURI)
					.then().extract().response();     		     	

			String responseBody = response.getBody().asString();
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(responseBody.contains("successfully uploaded"));
	  		
	  		
	  		log.info("Successfully Posted attachment to the Existing Customer Support Ticket");
	  		logger.log(Status.INFO, "Posted Attachment to the Existing Customer Support Ticket" );
	  		
			return responseBody;	
			
			
		}
		
		//Adding Attachment to Existing Customer Support Request - with Various file types/formats
		//GAPI-3843-CS - Adding Attachment to Existing Customer Support Request -  with Various file types/formats
		
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void addAttachmentstoCustomerSupportTicket(Map<String, String> data) throws IOException {
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response=csTicket.createCustomerSupportTicket(Token, body);
		System.out.println("Response is: " + response.asString());
		String WONumber = tc.getWONumberFromResponse(response);
		String attachmentfilter = data.get("filters");
		String apiURI = domain+ "/attachments" +attachmentfilter+WONumber;
		System.out.println("API URL by ID: " + apiURI);
		File testUploadFile = new File(System.getProperty ("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
		Response responsepst = tc.postAttachmentRequest (Token,masterAccountID,accountID,apiURI,testUploadFile);
		log.info("Attached file to the Customer Support ticket");
		logger.log(Status.PASS, "Successfully attached attachment to the Customer Support ticket");
		logger.log(Status.PASS,	"Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
		System.out.println("Response is : " + responsepst.asString());
		
	}
		
		//Adding Attachment to Existing Customer Support Request - without mandatory fields RequestID and filename
		//GAPI-3846-CS - Adding Attachment to Existing Customer Support Request -  without mandatory fields RequestID and filename
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void addAttachmentstoCustomerSupportTicketWithOutRequestIDAndfilename (Map<String, String> data) throws IOException {
		 
		logger = extent.createTest(data.get("TestCaseName"));
		String attachmentfilter = data.get("filters");
		String apiURI = domain+ "/attachments" +attachmentfilter;
		System.out.println("API URL by ID: " + apiURI);
		File testUploadFile = new File(System.getProperty ("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
		Response responsepst = tc.postAttachmentRequest (Token,masterAccountID,accountID,apiURI,testUploadFile);
		log.info("Not allowed to attach attachment to the Customer Support ticket");
		logger.log(Status.PASS, "Not allowed to attach attachment to the Customer Support ticket");
		logger.log(Status.PASS,	"Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
		System.out.println("Response is : " + responsepst.asString());
		
	}
		//GAPI-3848-CS-Get a single Attachment related to a Customer Support ticket  (by attachment ID)
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getAttachmentOfCustomerSupportTicketByID(Map<String, String> data) throws IOException {

			String body = tc.getRequestBody(data);
			System.out.println(data.get("TestCaseName") + "\n");
			System.out.println("Request Payload data is  \n" + body);
			logger = extent.createTest(data.get("TestCaseName"));
			//Response response = createRemoteHandsTicket.createRemoteHandsRequest(Token, body);
			Response response=csTicket.createCustomerSupportTicket(Token, body);
			System.out.println("Response is: " + response.asString());
			String WONumber = tc.getWONumberFromResponse(response);
			String attachmentfilter = data.get("filters");
		//	String apiURI = domain + path2 + attachmentfilter + WONumber;
			String apiURI = domain+ "/attachments" +attachmentfilter+WONumber;
		//	https://api-tst.digitalrealty.com/v1/attachments?file_name=test&requestId=WO8697640
		//	https://api-tst.digitalrealty.com/v1/attachments/WO8697503/file/173c68151b7d2950e96a866ecc4bcbe0/
			System.out.println("API URL by ID: " + apiURI);
			File testUploadFile = new File(
					System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
			Response responsepst = tc.postAttachmentRequest(Token, masterAccountID, accountID, apiURI, testUploadFile);
			log.info("Attached file to the Remote Hands ticket");
			logger.log(Status.PASS, "Successfully attached attachment to the Customer Support ticket");
			logger.log(Status.PASS,
					"Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
			System.out.println("Response is : " + responsepst.asString());
			String id = tc.getIdFromResponse(responsepst);
			System.out.println("Attachment ID is : " + id);
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
