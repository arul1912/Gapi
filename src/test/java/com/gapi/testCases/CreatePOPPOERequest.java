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

public class CreatePOPPOERequest extends BaseClass {
	
	public static String path="/facility-access/pop-poes/access-requests";
	//public static String accpath="/facility-access/access-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String accessStartDate = tc.getCurrentDateAndTime();
	static String accessEndDate = tc.getCurrentDateTime();
	
	// create Visitor Access Ticket with all possible combinations
	//GAPI-6265-PoP/POE Room Access

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreatePOPPOERequest(Map<String, String> data) throws IOException, InterruptedException {
		
		
		//logger = extent.createTest("verifyCreatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));
			
		String body = createPOPOEPayLoad(data);
				
		
		String apiURI = domain + path;
		System.out.println(body);
		//String body =tc.getRequestBody(data);
		System.out.println(apiURI);
		Response response = createPOPPOERequest(Token,body);
		
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	//Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		
		if (response.getStatusCode()==200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created POP/POE request");
			logger.log(Status.PASS, "Created POP/POE request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "POPPOERequest_" + WONumber);
			//tc.retrieveFAGETResponseAndRequestParameters(WONumber, body, accpath);
			GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
			/*
			 * String getALLAPI =
			 * domain+"/remotehands-tickets?requestType="+requestType+"&category="+category;
			 * System.out.println(getALLAPI);
			 * obj.verifyGetAllWithAllRequestTypesAndCategoryValues(WONumber,getALLAPI);
			 */
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		 
		
	}
	
	 //Create POP/POE Request with Invalid Legal  Entity 
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreatePOPPOERequestForInvalidLegalEntity(Map<String, String> data) throws IOException, InterruptedException {
		logger = extent.createTest(data.get("TestCaseName"));			
		String body = createPOPOEPayLoad(data);
				
		
		String apiURI = domain + path;
		System.out.println(body);
		//String body =tc.getRequestBody(data);
		System.out.println(apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id","abcdcc")
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body)
				.post(domain + path)
				.then().extract().response();
		log.info("Provided Invalid Legal Entity");
		logger.pass("Pay load Body was created with Invalid Legal Entity");
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		
		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 404"));
		log.info("Response Status Code and Message Is  "+response.getStatusLine());
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
	//	tc.verifyErrorResponseMessage(response.asString(),"client", "Bad request was submitted.");
		tc.verifyErrorResponseMessage(response.asString(), "client", "Resource not found.");
		//tc.verifyErrorResponseMessage(response.asString(), "BAD_REQUEST", "Bad request was submitted.");
		logger.pass("Not Allowed to Create a POP/POE Request Request With Invalid Legal Entity");		 
		log.info("Not Allowed to Create a POP/POE Request With Invalid Legal Entity");
	}
	
	// Upload Attachment To POP/POE Request
	//GAPI-6847-FA-POP/POE-  Attachments

			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyUploadAttachmentToPOPPOERequest(Map<String, String> data) throws IOException, InterruptedException {	
				
				//logger = extent.createTest("verifyCreatePermanentBadgeRequest");
				logger = extent.createTest(data.get("TestCaseName"));
					
				String body = createPOPOEPayLoad(data);			
				String apiURI = domain + path;
				System.out.println(body);
				//String body =tc.getRequestBody(data);
				System.out.println(apiURI);
				Response response = createPOPPOERequest(Token,body);
				
				System.out.println("exam: "+response.asString());
				String expectedStatusCode = data.get("expectedStatusCode");
				int actualStatusCode  = response.getStatusCode();
				String expectedStatusMessage = data.get("ExpectedStatusMessage");
				System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
				Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
				//Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
				logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
				String WONumber=null;
				if (response.getStatusCode()==200) {
					WONumber = tc.getWOIDFromResponse(response);
					log.info("The WO Number Created is " + WONumber);
					logger.log(Status.PASS, "Successfully Created POP/POE request");
					logger.log(Status.PASS, "Created POP/POE request Id  " + WONumber);
					tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "POPPOERequest_" + WONumber);
					GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
					
				}
				else {
					logger.pass("Error Response message is " + response.asString());
					logger.log(Status.PASS, "Not allowed to create Visitor access request");
					String expectedErrorMessageType = data.get("errorMessageType");
					String expectedErrorMessage = data.get("errorMessage");
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
				}
				 
				tc.addAttachmentToTheRequestAndVerify(WONumber,path,Token);
			}
			public String createPOPOEPayLoad(Map<String, String> data)
			{
				String body = "{\r\n"
						+ "\"visitors\": [   {\"company\": \""+data.get("company")+"\","
						+ "\"contactType\": \""+data.get("contactType")+"\","
						+ "\"visitorFirstName\": \""+data.get("visitorFirstName")+"\","
						+ "\"visitorLastName\": \""+data.get("visitorLastName")+"\","
						+ "\"visitorEmail\": \""+data.get("visitorEmail")+"\","
						+ "\"visitorPhone\": \""+data.get("visitorPhone")+"\"    }],"
						+ "\"visitorType\": \"true\","
						+ "\"escortFirstName\": \""+data.get("escortFirstName")+"\","
						+ "\"escortLastName\": \""+data.get("escortLastName")+"\","
						+ "\"visitorHostFirstName\": \""+data.get("visitorHostFirstName")+"\","
						+ "\"visitorHostLastName\": \""+data.get("visitorHostLastName")+"\","
						+ "\"visitorHostCompany\": \""+data.get("visitorHostCompany")+"\","
						+ "\"sites\": [  {\"site\": \""+data.get("site")+"\","
						+ "\"locations\": [  {\"location\": \""+data.get("locations")+"\","
						+ "\"accessStartDate\": \""+accessStartDate+"\","
						+ "\"accessEndDate\": \""+accessEndDate+"\","
						+ "\"accessStartTime\": \"06:22:00\","
						+ "\"accessEndTime\": \"06:22:00\"   }]"
						+ "}],"
						+ "\"customerExternalReference\": \""+data.get("customerExternalReference")+"\","
						+ "\"emailNotifications\": \""+data.get("emailNotifications")+"\","
						+ "\"details\": \""+data.get("details")+"\""
						+ "}";
				return body;
						
			}

	
	// Re-usable methods for Create FA Ticket
		// *************************************************************************************

		public static Response createPOPPOERequest(String Token, String body) throws IOException {

			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "vigneswarareddy@digitalrealty.com")
					.body(body)
					.post(domain + path)
					.then().extract().response();
			
			return response;
		}
}
