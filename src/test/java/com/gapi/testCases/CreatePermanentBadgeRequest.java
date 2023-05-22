package com.gapi.testCases;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CreatePermanentBadgeRequest extends BaseClass {
	
	public static String path="/facility-access/perm-badges";
	//public static String accpath="/facility-access/access-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String accessStartDate = tc.getCurrentDateAndTime();
	static String accessEndDate = tc.getCurrentDateTime();
	HashMap<String, String> permanentBadgeTicketsHashMap = new HashMap<String, String>();
	boolean ticketsCreatedStatusForSingleLocation = false;
	boolean ticketsCreatedStatusForMultipleLocations = false;
	
	
	// create Visitor Access Ticket with all possible combinations
	//GAPI-6839-FA-Permanent Badge - Create New Badge

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreatePermanentBadgeRequest(Map<String, String> data) throws IOException, InterruptedException {
		
		
		//logger = extent.createTest("verifyCreatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));
			
		String body = createPermanentBadgePayLoad(data);
				
		String apiURI = domain + path;
		System.out.println(body);
		//String body =tc.getRequestBody(data);
		//System.out.println(body);
		Response response = createPermanentBadgeRequest(Token,body);
		
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
			logger.log(Status.PASS, "Successfully Created Permanent Badge request");
			logger.log(Status.PASS, "Created Permanent Badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "PermanentBadge_" + WONumber);
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
	
	//Create Permanent Badge Request For Invalid Token	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreatePermanentBadgeRequestForInvalidToken(Map<String, String> data) throws IOException, InterruptedException {
		
		
		logger = extent.createTest(data.get("TestCaseName"));
			
		String body = createPermanentBadgePayLoad(data);
				
		String apiURI = domain + path;
		System.out.println(body);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+"abcde")
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body)
				.post(domain + path)
				.then().extract().response();
		log.info("Provided Invalid Token");
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		
		Assert.assertEquals(response.getStatusCode(), 401);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 401"));
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		log.info("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		tc.verifyErrorResponseMessage(response.asString(), "client", "Unauthorized. Access token is missing or invalid.");
		logger.pass("Not Allowed to Create a Permanent Badge Request With Invalid token");		 
		log.info("Not Allowed to Create a Permanent Badge Request With Invalid token");
		
	}
	
	//Create Permanent Badge Request For Invalid Legal Entity	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreatePermanentBadgeRequestForInvalidLegalentity(Map<String, String> data) throws IOException, InterruptedException {				
		logger = extent.createTest(data.get("TestCaseName"));
		String body = createPermanentBadgePayLoad(data);
				
		String apiURI = domain + path;
		System.out.println(body);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id",masterAccountID)
				.header("Account-Id", "abcde")
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body)
				.post(domain + path)
				.then().extract().response();
		log.info("Provided Invalid Legal Entity");
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		
		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 404"));
		log.info("Response Status Code and Message Is  "+response.getStatusLine());
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
	//	tc.verifyErrorResponseMessage(response.asString(), "client", "Bad request was submitted.");
		tc.verifyErrorResponseMessage(response.asString(), "client", "Resource not found.");
		logger.pass("Not Allowed to Create a Permanent Badge Request With Invalid Legal Entity");		 
		log.info("Not Allowed to Create a Permanent Badge Request With Invalid Legal Entity"); 
		
	}


	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyUploadAttachmentToPermanentBadgeRequest(Map<String, String> data) throws IOException, InterruptedException {
		
		
		//logger = extent.createTest("verifyCreatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));			
		String body = createPermanentBadgePayLoad(data);		
		String apiURI = domain + path;
		System.out.println(body);
		//String body =tc.getRequestBody(data);
		//System.out.println(body);
		String WONumber=null;
		Response response = createPermanentBadgeRequest(Token,body);
		
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		
		if (response.getStatusCode()==200) {
			WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Permanent Badge request");
			logger.log(Status.PASS, "Created Permanent Badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "PermanentBadge_" + WONumber);
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
	
		public String createPermanentBadgePayLoad(Map<String, String> data)
		{
			String body = "{\r\n"
					+ "\"firstName\":\""+data.get("visitorFirstName")+"\","
					+ "\r\n\"lastName\":\""+data.get("visitorLastName")+"\","
					+ "\r\n\"email\":\""+data.get("visitorEmail")+"\","
					+ "\r\n\r\n\"company\":\""+data.get("company")+"\","
					+ "\r\n\"badgeStartDate\": \""+date+"\","
					+ "\r\n\"phone\": \""+data.get("visitorPhone")+"\","
					+ "\r\n \"emailNotifications\": \""+data.get("emailNotifications")+"\","
					+ "\r\n\r\n\"sites\":[\r\n      {\r\n \"site\": \""+data.get("site")+"\","
					+ "\r\n        \"locations\": [ {\r\n \"location\": \""+data.get("locations")+"\","
					+ "\r\n  \"accessStartDate\": \""+accessStartDate+"\","
					+ "\r\n \"accessEndDate\": \""+accessEndDate+"\","
					+ "\r\n \"accessStartTime\": \"06:22:00\","
					+ "\r\n \"accessEndTime\": \"06:22:00\"\r\n }"
					+ "\r\n        ]      }    ]\r\n}";
			
			return body;
		}
		
		GetServiceTicketDetails getservice = new GetServiceTicketDetails();

		String path1 = "/service-tickets";
			//GAPI-8988-FA-Permanent Badge - Create New Badge - Get Service ticket

			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyCreatePermanentBadgeRequestGetService(Map<String, String> data) throws IOException, InterruptedException {
				logger = extent.createTest(data.get("TestCaseName"));
				
				String body = createPermanentBadgePayLoad(data);				
				String apiURI = domain + path;
				System.out.println(body);
				Response response = createPermanentBadgeRequest(Token,body);
				System.out.println("exam: "+response.asString());
				String WONumber = tc.getAttributeFromArrayResponse(response.asString(),"workOrder","workOrderId");
				String expectedStatusCode = data.get("expectedStatusCode");
				int actualStatusCode  = response.getStatusCode();
				System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
				Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
				logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
				String apiURI1 = domain + path1 +"?"+"id="+ WONumber;
				System.out.println("Request is: "+apiURI1);
				Response response1 = RestAssured.given().relaxedHTTPSValidation()
						.header("Authorization", "Bearer "+Token)
						.header("Content-Type", "application/json")
						.header("Master-Account-Id", masterAccountID)
						.header("Account-Id", accountID)
						.header("User-Email", "msirikonda@digitalrealty.com")
						.get(apiURI1);
				System.out.println(response1.asPrettyString());
				Assert.assertEquals(response1.getStatusCode(), 200);
				logger.log(Status.PASS, "Successfully extracted service Ticket info ");
				String gettitle = tc.getAttributeFromArrayResponse(response1.asString(), "content", "title").trim();
				String getdescription = tc.getAttributeFromArrayResponse(response1.asString(), "content", "description").trim();
				String titledata = "Request for New Permanent badge";
				Assert.assertEquals(titledata, gettitle);
				Assert.assertEquals(titledata, getdescription);
				logger.log(Status.PASS, "Both Title and Description are Successfully Verified");
			}
			
			String accpath="/facility-access/access-tickets";
			//GAPI-9004-Create Permanent badge request in Test for Telx Group and verify weather the category attribute value as (New Badge) from Service ticket Get Call Response

				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyAttributeCreatePermanentBadgeRequest(Map<String, String> data) throws IOException, InterruptedException {
					logger = extent.createTest(data.get("TestCaseName"));
					
					String body = createPermanentBadgePayLoad(data);				
					String apiURI = domain + path;
					System.out.println(body);
					Response response = createPermanentBadgeRequest(Token,body);
					System.out.println("exam: "+response.asString());
					String WONumber = tc.getAttributeFromArrayResponse(response.asString(),"workOrder","workOrderId");
					String expectedStatusCode = data.get("expectedStatusCode");
					int actualStatusCode  = response.getStatusCode();
					System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
					String apiURI1 = domain+ accpath+"/"+WONumber;
					Response getresponse = RestAssured.given().relaxedHTTPSValidation()
							.header("Authorization", "Bearer "+Token)
							.header("Content-Type", "application/json")
							.header("Master-Account-Id", masterAccountID)
							.header("Account-Id", accountID) 	
							.header("User-Email", "gpuat22-uat3@yahoo.com")
			 				.get(apiURI1);      		     	

					System.out.println("===>"+getresponse.asString());
					logger.log(Status.INFO, response.statusLine());
					Assert.assertEquals(response.getStatusCode(), 200);
					Assert.assertTrue(response.statusLine().contains("OK"));
					logger.log(Status.INFO, "Successfully extracted Permanent Badge Info and verified" );
					String category = data.get("category");
					String getcategory = tc.getAttributeFromJsonResponse(getresponse.asString(),"ticketCategory");
					Response serviceresponse = getservice.getAllServiceTicketDetailsinfo();
					Assert.assertEquals(serviceresponse.getStatusCode(), 200);
					String servicecategory = tc.getAttributeFromArrayResponse(serviceresponse.asString(), "content","category");
					Assert.assertEquals(category, getcategory);
					Assert.assertEquals(category, servicecategory);
					logger.log(Status.PASS, "Successfully extracted service Ticket info and verified");
				}
				//GAPI-8985-FA-Permanent Badge - Create New Badge -Subject Enhancement
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyCreatePermanentBadgeSubjectEnhancement(Map<String, String> data) throws IOException, InterruptedException {
					
					logger = extent.createTest(data.get("TestCaseName"));
					String body = createPermanentBadgePayLoad(data);
					System.out.println("Body is : " + body);
					
					Response getResponse = tc.postRequest(Token, body, masterAccountID, accountID, domain+path);
					System.out.println("Response is : " + getResponse.asString());
					System.out.println("Status Code is : " + getResponse.getStatusCode());
						String expectedStatusCode = data.get("expectedStatusCode");
						int actualStatusCode  = getResponse.getStatusCode();
						System.out.println("Statusmessage & status code is:"+getResponse.statusCode()+getResponse.statusLine());
						Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
						logger.log(Status.PASS, "Response Status Code and Status Message is " + getResponse.statusLine());
						String WOnumber = tc.getAttributeFromArrayResponse1(getResponse.asString(),"workOrder","workOrderId");
							String path1="/facility-access/access-tickets/";
							String apiURI1 = domain+ path1+ WOnumber;
							String contactType = data.get("contactType");
							String description = data.get("description");
							Response response1 = RestAssured.given().relaxedHTTPSValidation()
								.header("Authorization", "Bearer "+Token)
								.header("Content-Type", "application/json")
								.header("Master-Account-Id", masterAccountID)
								.header("Account-Id", accountID) 	
								.header("User-Email", "gpuat22-uat3@yahoo.com")
				 				.get(apiURI1);
						
							String getcontacttype = tc.getAttributeFromJsonResponse(response1.asString(),"contactType");
							String getdescription = tc.getAttributeFromJsonResponse(response1.asString(),"description");
							Assert.assertEquals(contactType,getcontacttype);
							Assert.assertEquals(description,getdescription);
							Assert.assertEquals(response1.getStatusCode(), 200);
					}
				
				static String CurrentDateNextYear = tc.getCurrentDateNextYear();

				//GAPI-6259-Permanent badge - Verify that a badge with an expiration date greater than one year from today's date was created or not

					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyCreatePermanentBadgeRequestForExpiryDate(Map<String, String> data) throws IOException, InterruptedException {	
						
						logger = extent.createTest(data.get("TestCaseName"));
						String body = "{\r\n"
								+ "\"firstName\":\""+data.get("visitorFirstName")+"\","
								+ "\r\n\"lastName\":\""+data.get("visitorLastName")+"\","
								+ "\r\n\"email\":\""+data.get("visitorEmail")+"\","
								+ "\r\n\r\n\"company\":\""+data.get("company")+"\","
								+ "\r\n\"badgeStartDate\": \""+date+"\","
								+ "\r\n\"badgeExpirationDate\": \""+CurrentDateNextYear+"\","
								+ "\r\n\"phone\": \""+data.get("visitorPhone")+"\","
								+ "\r\n \"emailNotifications\": \""+data.get("emailNotifications")+"\","
								+ "\r\n\r\n\"sites\":[\r\n      {\r\n \"site\": \""+data.get("site")+"\","
								+ "\r\n        \"locations\": [ {\r\n \"location\": \""+data.get("locations")+"\","
								+ "\r\n  \"accessStartDate\": \""+accessStartDate+"\","
								+ "\r\n \"accessEndDate\": \""+accessEndDate+"\","
								+ "\r\n \"accessStartTime\": \"06:22:00\","
								+ "\r\n \"accessEndTime\": \"06:22:00\"\r\n }"
								+ "\r\n        ]      }    ]\r\n}";
								
						System.out.println(body);
						Response response = RestAssured.given().relaxedHTTPSValidation()
								.header("Authorization", "Bearer "+Token)
								.header("Content-Type", "application/json")
								.header("Master-Account-Id",masterAccountID)
								.header("Account-Id", accountID)
								.header("User-Email", "vigneswarareddy@digitalrealty.com")
								.body(body)
								.post(domain + path)
								.then().extract().response();
						
						System.out.println("Response is : " + response.asString());
						System.out.println("Status Code is : " + response.getStatusCode());
						Assert.assertEquals(response.getStatusCode(), 200);
						Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
						log.info("Response Status Code and Message Is  "+response.getStatusLine());
						logger.pass("Response Status Code and Message Is " + response.getStatusLine());
						
					}
					//GAPI-8916-Create Permanent badge request in Test for Telx Group and verify weather the Description attribute value as (Request for New Permanent Badge)
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyCreatePermanentBadgeRequestTelxGroup(Map<String, String> data) throws IOException, InterruptedException {
						
						
						//logger = extent.createTest("verifyCreatePermanentBadgeRequest");
						logger = extent.createTest(data.get("TestCaseName"));
							
						String body = createPermanentBadgePayLoad(data);
								
						String apiURI = domain + path;
						System.out.println(body);
						//String body =tc.getRequestBody(data);
						//System.out.println(body);
						Response response = createPermanentBadgeRequest(Token,body);
						
						System.out.println("exam: "+response.asString());
						String expectedStatusCode = data.get("expectedStatusCode");
						int actualStatusCode  = response.getStatusCode();
						String expectedStatusMessage = data.get("ExpectedStatusMessage");
						System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
						Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					//Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
						logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
						String WONumber = tc.getAttributeFromArrayResponse1(response.asString(),"workOrder","workOrderId");
						
						String path = "/facility-access/access-tickets/";
						//api-tst.digitalrealty.com/v1/facility-access/access-tickets/WO8706357
						//https://api-tst.digitalrealty.com/v1/service-tickets?id=WO8719465
						String apiURI2 = domain+ path+WONumber;
						//String title = data.get("title");
						String description = data.get("description");
						Response response1 = RestAssured.given().relaxedHTTPSValidation()
								.header("Authorization", "Bearer " + Token)
								.header("Content-Type", "application/json")
								.header("Master-Account-Id", masterAccountID)
								.header("Account-Id", accountID)
								.get(apiURI2);
						
						JsonPath responseJsonPath = response1.jsonPath();

						
						//String getTitle = tc.getAttributeFromArrayResponse1(response1.asString(),"content","title");
						String getdescription = tc.getAttributeFromJsonResponse(response1.asString(),"description");
					
						//Assert.assertEquals(responseJsonPath.get("content[0].title"),"title");
					//	Assert.assertEquals(responseJsonPath.get("content[0].description"),"description");
					//	Assert.assertEquals(title,getTitle);
						Assert.assertEquals(description,getdescription);
						//Assert.assertEquals(responseJsonPath.get("content[0].description"),"description");
						//String getTitle = tc.getAttributeFromJsonResponse(response1.asString(),"title");
					//String getDescription = tc.getAttributeFromJsonResponse(response1.asString(),"description");				
						Assert.assertEquals(response1.getStatusCode(), 200);
						
					}
	
					//GAPI-8973	FA-Permanent Badge -Create ticket with multiple Locations and verify the Ticket Status when one location is Approved and other one is pending for Approval
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyPermanentBadgeAprovedAndPendingApprovalForMultipleLocations(Map<String, String> data) throws IOException, InterruptedException
					{
						logger = extent.createTest(data.get("TestCaseName"));
						if (ticketsCreatedStatusForMultipleLocations == false) {
							createPermanentBadgeTicketsForMultipleLocations(data);
						}
						String WONumber = permanentBadgeTicketsHashMap.get("MultipleLoctionsTicket1");
						logger.log(Status.PASS, WONumber + " Ticket created Successfully");
						List<String> sysIDs = tc.getSysID(WONumber);
						tc.approveSysID(sysIDs.get(0));
						Assert.assertEquals("Pending Approval", tc.getFATicketStatus(WONumber));
						logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
						
					}
					
					//GAPI-8974	FA- Permanent badge -Create ticket for Multiple locations and verify the Ticket Status when one location is Denied and other one is pending for Approval
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyPermanentBadgeDeniedAndPendingApprovalForMultipleLocations(Map<String, String> data) throws IOException, InterruptedException
					{
						logger = extent.createTest(data.get("TestCaseName"));
						if (ticketsCreatedStatusForMultipleLocations == false) {
							createPermanentBadgeTicketsForMultipleLocations(data);
						}
						String WONumber = permanentBadgeTicketsHashMap.get("MultipleLoctionsTicket2");
						logger.log(Status.PASS, WONumber + " Ticket created Successfully");
						List<String> sysIDs = tc.getSysID(WONumber);
						tc.approveSysID(sysIDs.get(0));
						Assert.assertEquals("Pending Approval", tc.getFATicketStatus(WONumber));
						logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
						
					}
					//GAPI-8976	Facility Access API	FA- Permanent badge -Create ticket for Multiple locations and verify the Ticket Status when both locations are Approved
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyPermanentBadgeApprovedForMultipleLocations(Map<String, String> data) throws IOException, InterruptedException
					{
						logger = extent.createTest(data.get("TestCaseName"));
						if (ticketsCreatedStatusForMultipleLocations == false) {
							createPermanentBadgeTicketsForMultipleLocations(data);
						}
						String WONumber = permanentBadgeTicketsHashMap.get("MultipleLoctionsTicket3");
						logger.log(Status.PASS, WONumber + " Ticket created Successfully");
						List<String> sysIDs = tc.getSysID(WONumber);
						tc.approveSysID(sysIDs.get(0));
						tc.approveSysID(sysIDs.get(1));
						Assert.assertEquals("Approved", tc.getFATicketStatus(WONumber));
						logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
						
					}
					
					//GAPI-8977	Facility Access API	FA- Permanent badge -Create ticket for Multiple locations and  verify the Ticket Status when both locations are Denied
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyPermanentBadgeDeniedForMultipleLocations(Map<String, String> data) throws IOException, InterruptedException
					{
						logger = extent.createTest(data.get("TestCaseName"));
						if (ticketsCreatedStatusForMultipleLocations == false) {
							createPermanentBadgeTicketsForMultipleLocations(data);
						}
						String WONumber = permanentBadgeTicketsHashMap.get("MultipleLoctionsTicket4");
						logger.log(Status.PASS, WONumber + " Ticket created Successfully");
						List<String> sysIDs = tc.getSysID(WONumber);
						tc.rejectSysID(sysIDs.get(0));
						tc.rejectSysID(sysIDs.get(1));
						Assert.assertEquals("Approval Denied", tc.getFATicketStatus(WONumber));
						logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
						
					}
					//GAPI-8975-FA- Permanent badge -Create ticket for Multiple locations and verify the Ticket Status when one location is Approved and other one is Denied
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyPermanentBadgeApprovedAndDeniedForMultipleLocations(Map<String, String> data) throws IOException, InterruptedException
					{
						logger = extent.createTest(data.get("TestCaseName"));
						if (ticketsCreatedStatusForMultipleLocations == false) {
							createPermanentBadgeTicketsForMultipleLocations(data);
						}
						String WONumber = permanentBadgeTicketsHashMap.get("MultipleLoctionsTicket5");
						logger.log(Status.PASS, WONumber + " Ticket created Successfully");
						List<String> sysIDs = tc.getSysID(WONumber);
						tc.approveSysID(sysIDs.get(0));
						tc.rejectSysID(sysIDs.get(1));
						Assert.assertEquals("Approved", tc.getFATicketStatus(WONumber));
						logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
						
					}
					
					//GAPI-8979-FA- Permanent badge -Create ticket for single Location and verify the Ticket Status when the location status is Denied
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyPermanentBadgeApprovedForSingleLocation(Map<String, String> data) throws IOException, InterruptedException
					{
						logger = extent.createTest(data.get("TestCaseName"));
						if (ticketsCreatedStatusForSingleLocation == false) {
							createPermanentBadgeTicketsForSingleLocation(data);
						}
						String WONumber = permanentBadgeTicketsHashMap.get("SingleLoctionTicket1");
						logger.log(Status.PASS, WONumber + " Ticket created Successfully");
						List<String> sysIDs = tc.getSysID(WONumber);
						tc.approveSysID(sysIDs.get(0));
						Assert.assertEquals("Approved", tc.getFATicketStatus(WONumber));
						logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
						
					}
					//GAPI-8979-FA- Permanent badge -Create ticket for single Location and verify the Ticket Status when the location status is Denied
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyPermanentBadgeDeniedForSingleLocation(Map<String, String> data) throws IOException, InterruptedException
					{
						logger = extent.createTest(data.get("TestCaseName"));
						if (ticketsCreatedStatusForSingleLocation == false) {
							createPermanentBadgeTicketsForSingleLocation(data);
						}
						String WONumber = permanentBadgeTicketsHashMap.get("SingleLoctionTicket2");
						logger.log(Status.PASS, WONumber + " Ticket created Successfully");
						List<String> sysIDs = tc.getSysID(WONumber);
						tc.rejectSysID(sysIDs.get(0));
						Assert.assertEquals("Approval Denied", tc.getFATicketStatus(WONumber));
						logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
						
					}
					//GAPI-8981-FA- Permanent badge -Create ticket for single Location and  verify the Ticket Status when the location status is Pending for Approval
					@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
					public void verifyPermanentBadgePendingApprovalForSingleLocation(Map<String, String> data) throws IOException, InterruptedException
					{
						logger = extent.createTest(data.get("TestCaseName"));
						if (ticketsCreatedStatusForSingleLocation == false) {
							createPermanentBadgeTicketsForSingleLocation(data);
						}
						String WONumber = permanentBadgeTicketsHashMap.get("SingleLoctionTicket3");
						logger.log(Status.PASS, WONumber + " Ticket created Successfully");
						Assert.assertEquals("Pending Approval", tc.getFATicketStatus(WONumber));
						logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
						
					}
					
					// Reusable Methods to create visitor Access tickets
					public void createPermanentBadgeTicketsForSingleLocation(Map<String, String> data)
							throws IOException, InterruptedException {

						for (int i = 1; i < 6; i++) {
							String body = createPermanentBadgePayLoad(data);
							Response response = createPermanentBadgeRequest(createToken, body);
							String WONumber = tc.getAttributeFromArrayResponse(response.asString(), "workOrder", "workOrderId");
							permanentBadgeTicketsHashMap.put("SingleLoctionTicket" + i, WONumber);
						}
						Thread.sleep(60000);
						ticketsCreatedStatusForSingleLocation = true;
		
					}

					// Reusable Methods to create visitor Access tickets
					public void createPermanentBadgeTicketsForMultipleLocations(Map<String, String> data)
							throws IOException, InterruptedException {

						for (int i = 1; i < 6; i++) {
							String body = createPayLoadForPermanentBadgeMultipleLocations(data);
							Response response = createPermanentBadgeRequest(createToken, body);
							String WONumber = tc.getAttributeFromArrayResponse(response.asString(), "workOrder", "workOrderId");
							permanentBadgeTicketsHashMap.put("MultipleLoctionsTicket" + i, WONumber);
						}
						Thread.sleep(60000);
						ticketsCreatedStatusForMultipleLocations = true;
						// return visitorTicketsHashMap;
					}
					public String createPayLoadForPermanentBadgeMultipleLocations(Map<String, String> data)
					{
						String body = "{\r\n"
								+ "\"firstName\":\""+data.get("visitorFirstName")+"\","
								+ "\r\n\"lastName\":\""+data.get("visitorLastName")+"\","
								+ "\r\n\"email\":\""+data.get("visitorEmail")+"\","
								+ "\r\n\r\n\"company\":\""+data.get("company")+"\","
								+ "\r\n\"badgeStartDate\": \""+date+"\","
								+ "\r\n\"phone\": \""+data.get("visitorPhone")+"\","
								+ "\r\n \"emailNotifications\": \""+data.get("emailNotifications")+"\","
								+ "\r\n\r\n\"sites\":[\r\n      {\r\n \"site\": \""+data.get("site")+"\","
								+ "\r\n        \"locations\": [ {\r\n \"location\": \""+data.get("locations")+"\","
								+ "\r\n  \"accessStartDate\": \""+accessStartDate+"\","
								+ "\r\n \"accessEndDate\": \""+accessEndDate+"\","
								+ "\r\n \"accessStartTime\": \"06:22:00\","
								+ "\r\n \"accessEndTime\": \"06:22:00\"\r\n },\r\n          {"
								+ "\r\n           \"location\": \""+data.get("locations2")+"\","
								+ "\r\n            \"accessStartDate\": \""+accessStartDate+"\","
								+ "\r\n            \"accessEndDate\": \""+accessEndDate+"\","
								+ "\r\n           \"accessStartTime\": \"06:22:00\","
								+ "\r\n            \"accessEndTime\": \"06:22:00\"\r\n          }"
								+ "\r\n        ]\r\n      \r\n      }\r\n    ]\r\n}";
						
						return body;
					}
	// Re-usable methods for Create FA Ticket
		// *************************************************************************************

		public static Response createPermanentBadgeRequest(String Token, String body) throws IOException {

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
