package com.gapi.testCases;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/** CreateRemoteHandRequest class is used to create REMOTE HANDS TICKET with the various sets of input data,**/

public class CreateVisitorAccessRequest extends BaseClass {
	
	public static String path="/facility-access/visitors/access-requests";
	public static String accpath="/facility-access/access-tickets";

	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String accessStartDate = tc.getCurrentDateAndTime();
	static String accessEndDate = tc.getCurrentDateTime();
	static String visitorBadgeStartDate = tc.getCurrentDateAndTime();
	static String visitorBadgeEndDate = tc.getCurrentDateTime();
	HashMap<String, String> visitorTicketsHashMap = new HashMap<String, String>();
	boolean ticketsCreatedStatusForSingleLocation = false;
	boolean ticketsCreatedStatusForMultipleLocations = false;
	
	// create Visitor Access Ticket with all possible combinations
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateVisitorAccessRequest(Map<String, String> data) throws IOException, InterruptedException {

		//logger = extent.createTest("verifyCreateVisitorAccessRequest");
		logger = extent.createTest(data.get("TestCaseName"));
			
		String body = createVisitorAccessPayLoad(data);
		String apiURI = domain + path;
		System.out.println(body);
		//String body =tc.getRequestBody(data);
		//System.out.println(body);
		Response response = createVisitorAccessTicket(Token,body);
		
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
			logger.log(Status.PASS, "Successfully Created Visitor access request");
			logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Visitoraccess_" + WONumber);
	//		tc.retrieveFAGETResponseAndRequestParameters(WONumber, body, accpath);
			//GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
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
			String expectedReasonCode = data.get("errorMessageType");
			String expectedDescription = data.get("errorMessage");
			System.out.println();
			tc.verifyErrorResponseMessage(response.asString(), expectedReasonCode, expectedDescription);
			//tc.verifyErrorMessagefromResponse(response.asString(), expectedErrorMessageType,expectedErrorMessage);
		}
		 
		
	}
	
	// Re-usable methods for Create FA Ticket
		// *************************************************************************************

		public static Response createVisitorAccessTicket(String Token, String body) throws IOException {

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



        //Create Visitor Access Ticket for Invalid token	
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyCreateVisitorAccessRequestForInvalidToken(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			String body = createVisitorAccessPayLoad(data);
			String apiURI = domain + path;
			System.out.println(body);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer " + Tokenidp).header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID)
					.header("User-Email", "vigneswarareddy@digitalrealty.com").body(body).post(domain + path).then()
					.extract().response();
			System.out.println("Error Response Message Is" + response.asString());
			Assert.assertEquals(response.getStatusCode(), 401);
			Assert.assertTrue(response.statusLine().contains("HTTP/1.1 401"));
			logger.pass("Response Status Code and Message Is " + response.getStatusLine());
			logger.pass("Error Response Message Is" + response.asString());
			tc.verifyErrorResponseMessage(response.asString(), "client",
					"Unauthorized. Access token is missing or invalid.");
			logger.pass("Not Allowed to Create a Visitor Access Request Details With Invalid token");

		}

       //create Visitor Access Ticket for For Invalid Legal Entity
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyCreateVisitorAccessRequestForInvalidLegalentity(Map<String, String> data) throws IOException, InterruptedException {
			
			
			logger = extent.createTest(data.get("TestCaseName"));				
			String body = createVisitorAccessPayLoad(data);
			String apiURI = domain + path;
			System.out.println(body);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", "abcde")
					.header("User-Email", "vigneswarareddy@digitalrealty.com")
					.body(body)
					.post(domain + path)
					.then().extract().response();
			
			log.info("Provided Invalid Legal Entity");
			logger.pass("Created Body with Invalid Legal Entity and Fired API");
			System.out.println("Response is : " + response.asString());
			System.out.println("Status Code is : " + response.getStatusCode());
			
			Assert.assertEquals(response.getStatusCode(), 404);
			Assert.assertTrue(response.statusLine().contains("HTTP/1.1 404"));
			log.info("Response Status Code and Message Is  "+response.getStatusLine());
			logger.pass("Response Status Code and Message Is " + response.getStatusLine());
			logger.pass("Error Response Message Is" + response.asString());
	//		tc.verifyErrorResponseMessage(response.asString(), "client", "Bad request was submitted.");
			tc.verifyErrorResponseMessage(response.asString(), "client", "Resource not found.");
			//tc.verifyErrorResponseMessage(response.asString(), "ErrorWrapper.Errors.Error.Error.Description", "Bad request was submitted.");
			logger.pass("Not Allowed to Create a Visitor Access Request  With Invalid Legal Entity");		 
			log.info("Not Allowed to Create a  Visitor Access Request With Invalid Legal Entity");
			 
			
		}
		
		/*
		 * //Create Visitor Access Ticket for Invalid GlobalUltimate
		 * 
		 * @Test(dataProvider = "testCasesData", dataProviderClass =
		 * DataProviderUtility.class) public void
		 * verifyCreateVisitorAccessRequestForInvalidGlobalUltimate(Map<String, String>
		 * data) throws IOException, InterruptedException {
		 * 
		 * logger = extent.createTest(data.get("TestCaseName")); String body
		 * =createVisitorAccessPayLoad(data); String apiURI = Vuatdomain + path;
		 * System.out.println(body); Response response =
		 * RestAssured.given().relaxedHTTPSValidation() .header("Authorization",
		 * "Bearer "+TokenVuat) .header("Content-Type", "application/json")
		 * .header("Master-Account-Id", 12345678) .header("Account-Id",
		 * "0012E00002dmMY7QAM") .header("User-Email",
		 * "vigneswarareddy@digitalrealty.com") .body(body) .post(Vuatdomain + path)
		 * .then().extract().response();
		 * log.info("Created Body with Invalid Global Ultimate and Fired API");
		 * logger.pass("Created Body with Global Ultimate and Fired API");
		 * System.out.println("Error Response Message Is" + response.asString());
		 * Assert.assertEquals(response.getStatusCode(), 400);
		 * Assert.assertTrue(response.statusLine().contains("HTTP/1.1 400"));
		 * logger.pass("Response Status Code and Message Is " +
		 * response.getStatusLine()); logger.pass("Error Response Message Is" +
		 * response.asString()); tc.verifyErrorResponseMessage(response.asString(),
		 * "client", "Bad request was submitted."); logger.
		 * pass("Not Allowed to Create a Visitor Access Request Details With Invalid Global Ultimate"
		 * );
		 * 
		 * }
		 */
		
		// Add Attachment to Visitor Access Ticket
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyUploadAttachmentToVisitorAccessRequest(Map<String, String> data) throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			String body =createVisitorAccessPayLoad(data);
			String apiURI = domain + path;
			System.out.println(body);
			Response response = createVisitorAccessTicket(Token, body);
			System.out.println("exam: " + response.asString());
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
			String WONumber = null;
			if (response.getStatusCode() == 200) {
				WONumber = tc.getWOIDFromResponse(response);
				log.info("The WO Number Created is " + WONumber);
				logger.log(Status.PASS, "Successfully Created Visitor access request");
				logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
				tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Visitoraccess_" + WONumber);
				
			} else {
				logger.pass("Error Response message is " + response.asString());
				logger.log(Status.PASS, "Not allowed to create Visitor access request");
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");
				tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
			}
			
			tc.addAttachmentToTheRequestAndVerify(WONumber,path,Token);

		}
		
		// Retrieve Visitor Access ticket details - Get By ID
		
		//GAPI-6274-Retrieve Visitor Access ticket details - Get By ID

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getVisitorAccessRequestByID(Map<String, String> data) throws IOException, InterruptedException {
			// logger = extent.createTest("verifyCreateVisitorAccessRequest");
			logger = extent.createTest(data.get("TestCaseName"));
			CreateVisitorAccessRequest visitorAccessTicket = new CreateVisitorAccessRequest();
			String body = visitorAccessTicket.createVisitorAccessPayLoad(data);
			String apiURI = domain + path;
			System.out.println(body);
			Response response = tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
			String WONumber = tc.getWOIDFromResponse(response);
			System.out.println("Response: " + response.asString());
			System.out.println("WONumber: " + WONumber);
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

			if (response.getStatusCode() == 200) {

				log.info("The WO Number Created is " + WONumber);
				logger.log(Status.PASS, "Successfully Created Visitor access request");
				logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
				tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Visitoraccess_" + WONumber);

			} else {
				logger.pass("Error Response message is " + response.asString());
				logger.log(Status.PASS, "Not allowed to create Visitor access request");
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");
				tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
			}

			String getAPIURI = domain + "/facility-access/access-tickets/" + WONumber;
			Response getResponse = tc.getRequest(Token, masterAccountID, accountID, getAPIURI);
			System.out.println("Response: " + getResponse.asString());
			JsonPath responseJsonPath = getResponse.jsonPath();
			accessStartDate = tc.getCurrentDateAndTime();
			accessEndDate = tc.getCurrentDateTime();
			String visitorType = "Escorted visitor";
			if (data.get(visitorType) == "false") {
				visitorType = "Unescorted visitor";
			}

			Assert.assertEquals(responseJsonPath.get("emailNotifications"), data.get("emailNotifications"));
			Assert.assertEquals(responseJsonPath.get("ticketId"), WONumber);
			Assert.assertEquals(responseJsonPath.get("ticketType"), "Security");
			Assert.assertEquals(responseJsonPath.get("requestType"), "Visitor Access");
			Assert.assertEquals(responseJsonPath.get("contactFirstName"), data.get("visitorFirstName"));
			Assert.assertEquals(responseJsonPath.get("contactLastName"), data.get("visitorLastName"));
			Assert.assertEquals(responseJsonPath.get("contactEmail"), data.get("visitorEmail"));
			Assert.assertEquals(responseJsonPath.get("contactPhone"), data.get("visitorPhone"));
			Assert.assertEquals(responseJsonPath.get("representingCompany"), data.get("company"));
			Assert.assertEquals(responseJsonPath.get("visitorBadgeStartDate"), accessStartDate);
			Assert.assertEquals(responseJsonPath.get("visitorBadgeEndDate"), accessEndDate);
			Assert.assertEquals(responseJsonPath.get("visitorType"), visitorType);
			Assert.assertEquals(responseJsonPath.get("visitorHostFirstName"), data.get("visitorHostFirstName"));
			Assert.assertEquals(responseJsonPath.get("visitorHostLastName"), data.get("visitorHostLastName"));
			Assert.assertEquals(responseJsonPath.get("hostCompany"), data.get("visitorHostCompany"));
			Assert.assertEquals(responseJsonPath.get("notes"), data.get("notes"));
			Assert.assertEquals(responseJsonPath.get("visitorBadgeStartDate"), accessStartDate);
			Assert.assertEquals(responseJsonPath.get("visitorBadgeEndDate"), accessEndDate);
			Assert.assertEquals(responseJsonPath.get("bulkRequest").toString(), data.get("isBulkRequest"));
			Assert.assertEquals(responseJsonPath.get("site[0].site"), data.get("site"));
			Assert.assertEquals(responseJsonPath.get("site[0].locations[0].location"), data.get("locations"));
			Assert.assertEquals(responseJsonPath.get("escortFirstName"), data.get("escortFirstName"));
			Assert.assertEquals(responseJsonPath.get("escortLastName"), data.get("escortLastName"));

			logger.log(Status.PASS, "Retrieved all the Visitor Access ticket details - Get By ID");
			logger.log(Status.PASS,
					"Response Status Code and Status Message is after Retrieving all the Visitor Access ticket details by ID"
							+ getResponse.statusLine());
		}
		
		// Arul changes on 13-03-2023 Creation of Bulk Visitor Access request
		//GAPI-6818-FA-Creation of Bulk Visitor Access request

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void bulkVisitorAccessRequestInfo(Map<String, String> data) throws IOException, InterruptedException {
			
			String path = "/facility-access/visitors/access-requests";
			logger = extent.createTest(data.get("TestCaseName"));
			String body = "[\r\n    {\r\n        \"visitors\": [\r\n            {\r\n                \"visitorFirstName\": \"john\","
					+ "\r\n                \"visitorLastName\": \"test\","
					+ "\r\n                \"visitorEmail\": \"test@test.com\"\r\n                 \r\n            }\r\n           \r\n        ],"
					+ "\r\n        \"visitorType\": \"false\",\r\n        \"escortFirstName\": \"test5\",\r\n        \"escortLastName\": \"test6\","
					+ "\r\n        \"visitorHostFirstName\": \"hello\",\r\n        \"visitorHostLastName\": \"test8\","
					+ "\r\n        \"visitorHostCompany\": \"civil\",\r\n        \"visitorBadgeStartDate\": \""+accessStartDate+"\","
					+ "\r\n        \"visitorBadgeEndDate\": \""+accessEndDate+"\",\r\n        \"company\": \"TelX Group, Inc.\","
					+ "\r\n        \"notes\": \"unknow test\",\r\n        \"emailNotifications\": \"test35@forvisitor.com\","
					+ "\r\n        \"isExtendedVisitorBadge\": false,\r\n        \"standardVisitorBadgeStr\": \"Visitor Access (Escorted)\","
					+ "\r\n        \"extendedVisitorBadgeStr\": \"Visitor Access (Escorted)\","
					+ "\r\n        \"sites\": [\r\n            {\r\n                \"site\": \"PHX015\","
					+ "\r\n                \"locations\": [\r\n                    {\r\n                        \"location\": \"COLO 8 Starbucks Cage 8.06\","
					+ "\r\n                        \"accessStartDate\": \""+accessStartDate+"\",\r\n                        \"accessEndDate\": \""+accessEndDate+"\","
					+ "\r\n                        \"accessStartTime\": \"06:22:00\","
					+ "\r\n                        \"accessEndTime\": \"06:22:00\"\r\n                    }\r\n                ]\r\n            }\r\n        ],"
					+ "\r\n        \"isBulkRequest\": true\r\n    }\r\n]";
			String apiURI = domain + path;
			System.out.println(body);
			Response response = tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
			System.out.println(response.asString());
			String WONumber = tc.getWOIDFromResponse(response);
			System.out.println("Response: " + response.asString());
			System.out.println("WONumber: " + WONumber);
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

			if (response.getStatusCode() == 200) {

				log.info("The WO Number Created is " + WONumber);
				logger.log(Status.PASS, "Successfully Created Visitor access request");
				logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
				tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Visitoraccess_" + WONumber);

			} else {
				logger.pass("Error Response message is " + response.asString());
				logger.log(Status.PASS, "Not allowed to create Visitor access request");
				
			}
			
			 String attachmentfilter = data.get("filters");
			String getAPIURI = domain + "/attachments" +attachmentfilter+ WONumber;
			Response getResponse = tc.postRequest(Token, body, masterAccountID, accountID, getAPIURI);
			System.out.println("Response: " + getResponse.asString());
		System.out.println("API URL by ID: " + apiURI);
		 File testUploadFile = new File(System.getProperty ("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
		 Response responsepst = tc.postAttachmentRequest (Token,masterAccountID,accountID,apiURI,testUploadFile);
		log.info("Attached file to the visitors access ticket");
		 logger.log(Status.PASS, "Successfully attached attachment to the visitors access ticket");
		 logger.log(Status.PASS, "Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
		 System.out.println("Response is : " + responsepst.asString());
			}

		GetServiceTicketDetails getservice = new GetServiceTicketDetails();

			//String accpath="/facility-access/access-tickets";

			//GAPI-8917-Create Visitor access ticket and verify weather the category attribute value as (New Visitor Access) from Service ticket Get Call Response
			//GAPI-9009-Create Visitor access request in Test for Telx Group and verify weather the Description attribute value as (New Visitor Access) from Service ticket Get Call Response

			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyAttributeCreateVisitorAccessRequest(Map<String, String> data) throws IOException, InterruptedException {
				
				logger = extent.createTest(data.get("TestCaseName"));
				String body = createVisitorAccessPayLoad(data);				
				System.out.println(body);
				Response response = createVisitorAccessTicket(Token,body);
				System.out.println("exam: "+response.asString());
				String WONumber = tc.getAttributeFromArrayResponse(response.asString(),"workOrder","workOrderId");
				String expectedStatusCode = data.get("expectedStatusCode");
				int actualStatusCode  = response.getStatusCode();
				System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
				Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
				logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
				String apiURI = domain+ accpath+"/"+WONumber;
				Response getresponse = RestAssured.given().relaxedHTTPSValidation()
						.header("Authorization", "Bearer "+Token)
						.header("Content-Type", "application/json")
						.header("Master-Account-Id", masterAccountID)
						.header("Account-Id", accountID) 	
						.header("User-Email", "gpuat22-uat3@yahoo.com")
		 				.get(apiURI);      		     	

				System.out.println("===>"+getresponse.asString());
				logger.log(Status.INFO, response.statusLine());
				Assert.assertEquals(response.getStatusCode(), 200);
				Assert.assertTrue(response.statusLine().contains("OK"));
				logger.log(Status.INFO, "Successfully extracted Permanent Badge Info and verified" );
				String attribute = data.get("filters");
				if(attribute.equalsIgnoreCase("category")) {
					String category = data.get("category");
					String getcategory = tc.getAttributeFromJsonResponse(getresponse.asString(),"ticketCategory");
					Response serviceresponse = getservice.getAllServiceTicketDetailsinfo();
					Assert.assertEquals(serviceresponse.getStatusCode(), 200);
					String servicecategory = tc.getAttributeFromArrayResponse(serviceresponse.asString(), "content","category");
					Assert.assertEquals(category, getcategory);
					Assert.assertEquals(category, servicecategory);
					logger.log(Status.PASS, "Successfully extracted service Ticket info and verified");
				}
				else if(attribute.equalsIgnoreCase("description")) {
					String description = data.get("description");
					String getdescription = tc.getAttributeFromJsonResponse(getresponse.asString(),"ticketCategory");
					Response serviceresponse = getservice.getAllServiceTicketDetailsinfo();
					Assert.assertEquals(serviceresponse.getStatusCode(), 200);
					String servicedescription = tc.getAttributeFromArrayResponse(serviceresponse.asString(), "content","description");
					Assert.assertEquals(description, getdescription);
					Assert.assertEquals(description, servicedescription);
					logger.log(Status.PASS, "Successfully extracted service Ticket info and verified");
				}else if(attribute.equalsIgnoreCase("null"))
				{
					Assert.fail("The Attribute is null");
				}	
			}
		
		
		
		
		static String lessaccessStartDate = tc.getLessThanCurrentDate();

		// GAPI-8948-FA Visitor Access - Create with Location Start Date < Visitor Badge
		// Start Date

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void CreateVisitorAccessRequestLessLocationStartDate(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));

			String body = "[\r\n" + "  {\r\n" + "\"visitors\": [  {\"visitorFirstName\": \"" + data.get("visitorFirstName")
					+ "\"," + "\"visitorLastName\": \"" + data.get("visitorLastName") + "\"," + "\"visitorEmail\": \""
					+ data.get("visitorEmail") + "\"," + "\"visitorPhone\": \"" + data.get("visitorPhone") + "\","
					+ "\"company\":\"\"  }]," + "\"visitorType\": \"" + data.get("visitorType") + "\","
					+ "\"escortFirstName\": \"" + data.get("escortFirstName") + "\"," + "\"escortLastName\": \""
					+ data.get("escortLastName") + "\"," + "\"visitorHostFirstName\": \"" + data.get("visitorHostFirstName")
					+ "\"," + "\"visitorHostLastName\": \"" + data.get("visitorHostLastName") + "\","
					+ "\"visitorHostCompany\": \"" + data.get("visitorHostCompany") + "\","
					+ "\"visitorBadgeStartDate\": \"" + accessStartDate + "\"," + "\"visitorBadgeEndDate\": \""
					+ accessEndDate + "\"," + "\"company\": \"" + data.get("company") + "\"," + "\"notes\": \""
					+ data.get("notes") + "\"," + "\"emailNotifications\": \"" + data.get("emailNotifications") + "\","
					+ "\"isExtendedVisitorBadge\": false," + "\"standardVisitorBadgeStr\": \""
					+ data.get("standardVisitorBadgeStr") + "\"," + "\"extendedVisitorBadgeStr\": \""
					+ data.get("extendedVisitorBadgeStr") + "\"," + "\"sites\": [  {\"site\": \"" + data.get("site") + "\","
					+ "\"locations\": [  {\"location\": \"" + data.get("locations") + "\"," + "\"accessStartDate\": \""
					+ lessaccessStartDate + "\"," + "\"accessEndDate\": \"" + accessEndDate + "\","
					+ "\"accessStartTime\": \"06:22:00\"," + "\"accessEndTime\": \"06:22:00\"  }]  " + "}],"
					+ "\"isBulkRequest\":" + data.get("isBulkRequest") + "  }]";

			System.out.println(body);
			Response response = createVisitorAccessTicket(Token, body);
			System.out.println("exam: " + response.asString());
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
			logger.log(Status.PASS, "Error message And Error messageType verified");
		}

		// GAPI-8903-FA Visitor Access - Create with Location Start Date >=Visitor Badge
		// Start Dates
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void CreateVisitorAccessRequestGreaterLocationStartDate(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));

			String body = "[\r\n" + "  {\r\n" + "\"visitors\": [  {\"visitorFirstName\": \"" + data.get("visitorFirstName")
					+ "\"," + "\"visitorLastName\": \"" + data.get("visitorLastName") + "\"," + "\"visitorEmail\": \""
					+ data.get("visitorEmail") + "\"," + "\"visitorPhone\": \"" + data.get("visitorPhone") + "\","
					+ "\"company\":\"\"  }]," + "\"visitorType\": \"" + data.get("visitorType") + "\","
					+ "\"escortFirstName\": \"" + data.get("escortFirstName") + "\"," + "\"escortLastName\": \""
					+ data.get("escortLastName") + "\"," + "\"visitorHostFirstName\": \"" + data.get("visitorHostFirstName")
					+ "\"," + "\"visitorHostLastName\": \"" + data.get("visitorHostLastName") + "\","
					+ "\"visitorHostCompany\": \"" + data.get("visitorHostCompany") + "\","
					+ "\"visitorBadgeStartDate\": \"" + accessStartDate + "\"," + "\"visitorBadgeEndDate\": \""
					+ accessEndDate + "\"," + "\"company\": \"" + data.get("company") + "\"," + "\"notes\": \""
					+ data.get("notes") + "\"," + "\"emailNotifications\": \"" + data.get("emailNotifications") + "\","
					+ "\"isExtendedVisitorBadge\": false," + "\"standardVisitorBadgeStr\": \""
					+ data.get("standardVisitorBadgeStr") + "\"," + "\"extendedVisitorBadgeStr\": \""
					+ data.get("extendedVisitorBadgeStr") + "\"," + "\"sites\": [  {\"site\": \"" + data.get("site") + "\","
					+ "\"locations\": [  {\"location\": \"" + data.get("locations") + "\"," + "\"accessStartDate\": \""
					+ accessEndDate + "\"," + "\"accessEndDate\": \"" + accessEndDate + "\","
					+ "\"accessStartTime\": \"06:22:00\"," + "\"accessEndTime\": \"06:22:00\"  }]  " + "}],"
					+ "\"isBulkRequest\":" + data.get("isBulkRequest") + "  }]";

			System.out.println(body);
			Response response = createVisitorAccessTicket(Token, body);
			System.out.println("exam: " + response.asString());
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
			logger.log(Status.PASS, "Error message And Error messageType verified");
		}

		// GAPI-8999 - FA-visitor access- Create ticket for one location and verify the
		// Ticket Status when the location status is Approved
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyVisitorAccessApprovedForSingleLocation(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			if (ticketsCreatedStatusForSingleLocation == false) {
				createVisitorAccessTicketsForSingleLocation(data);
			}
			String WONumber = visitorTicketsHashMap.get("SingleLoctionTicket1");
			List<String> sysIDs = tc.getSysID(WONumber);
			tc.approveSysID(sysIDs.get(0));
			logger.log(Status.PASS, WONumber + " Ticket Approved Successfully");
			Assert.assertEquals("Approved", tc.getFATicketStatus(WONumber));
		}

		// GAPI-8902-FA-Ticket status Approval denied
		// GAPI-9000 - FA-Visitor Access-Create ticket for one location and verify the
		// Ticket Status when the location status is Denied
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyVisitorAccessDeniedForSingleLocation(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			if (ticketsCreatedStatusForSingleLocation == false) {
				createVisitorAccessTicketsForSingleLocation(data);
			}
			String WONumber = visitorTicketsHashMap.get("SingleLoctionTicket2");
			logger.log(Status.PASS, WONumber + " Ticket created Successfully");
			List<String> sysIDs = tc.getSysID(WONumber);
			tc.rejectSysID(sysIDs.get(0));
			Assert.assertEquals("Approval Denied", tc.getFATicketStatus(WONumber));
			logger.log(Status.PASS, WONumber + " Ticket Approval Denied Successfully");
		}
		//GAPI-8972-FA-visitor access- Create ticket for one location and verify the Ticket Status when the location status is Pending for Approval
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyVisitorAccessPendingForApprovalForSingleLocation(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			if (ticketsCreatedStatusForSingleLocation == false) {
				createVisitorAccessTicketsForSingleLocation(data);
			}
			String WONumber = visitorTicketsHashMap.get("SingleLoctionTicket3");
			logger.log(Status.PASS, WONumber + " Ticket created Successfully");
			Assert.assertEquals("Pending Approval", tc.getFATicketStatus(WONumber));
			logger.log(Status.PASS, WONumber + " Ticket Status Validated Successfully");
		}
		// GAPI-8998-FA-visitor access- Create ticket for multiple locations and verify
		// the Ticket Status when both location statuses is Denied
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyVisitorAccessDeniedForMultipleLocations(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			if (ticketsCreatedStatusForMultipleLocations == false) {
				createVisitorAccessTicketsForMultipleLocations(data);
			}
			String WONumber = visitorTicketsHashMap.get("MultipleLoctionsTicket3");
			logger.log(Status.PASS, WONumber + " Ticket created Successfully");
			List<String> sysIDs = tc.getSysID(WONumber);
			tc.rejectSysID(sysIDs.get(0));
			tc.rejectSysID(sysIDs.get(1));
			Assert.assertEquals("Approval Denied", tc.getFATicketStatus(WONumber));
			logger.log(Status.PASS, WONumber + " Ticket Status Validated Successfully");
		}

		// GAPI-8995 FA-visitor access- Create ticket for multiple locations and verify
		// the Ticket Status when one location status is Denied and other Location is
		// Pending for Approval
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyVisitorAccessDeniedPendingApprovalForMultipleLocations(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			if (ticketsCreatedStatusForMultipleLocations == false) {
				createVisitorAccessTicketsForMultipleLocations(data);
			}
			String WONumber = visitorTicketsHashMap.get("MultipleLoctionsTicket4");
			logger.log(Status.PASS, WONumber + " Ticket created Successfully");
			List<String> sysIDs = tc.getSysID(WONumber);
			tc.rejectSysID(sysIDs.get(0));
			Assert.assertEquals("Pending Approval", tc.getFATicketStatus(WONumber));
			logger.log(Status.PASS, WONumber + " Ticket Status was Pending Approval");
		}

		// GAPI-8996 FA-visitor access- Create ticket for multiple locations and verify
		// the Ticket Status when one location status is Denied and other Location is
		// Pending for Approval
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class, priority = 1)
		public void verifyVisitorAccessDeniedApprovedForMultipleLocations(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			/*
			 * String body = createPayLoadForVisitorAccessMultipleLocations(data);
			 * System.out.println(body); Response response =
			 * createVisitorAccessTicket(createToken, body); System.out.println("exam: " +
			 * response.asString());
			 */

			if (ticketsCreatedStatusForMultipleLocations == false) {
				createVisitorAccessTicketsForMultipleLocations(data);
			}
			String WONumber = visitorTicketsHashMap.get("MultipleLoctionsTicket2");
			logger.log(Status.PASS, WONumber + " Ticket created Successfully");
			List<String> sysIDs = tc.getSysID(WONumber);
			tc.rejectSysID(sysIDs.get(0));
			tc.approveSysID(sysIDs.get(1));
			Assert.assertEquals("Approved", tc.getFATicketStatus(WONumber));
			logger.log(Status.PASS, WONumber + " Ticket Status was Vlidated Successfully");
		}
		// GAPI-8994-FA-visitor access- Create ticket for multiple locations and verify
		// the Ticket Status when one location status is Approved and other Location is
		// Pending for Approval

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyVisitorAccessApprovedPendingApprovalDoubleLocation(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));

			if (ticketsCreatedStatusForMultipleLocations == false) {
				createVisitorAccessTicketsForMultipleLocations(data);
			}
			String WONumber = visitorTicketsHashMap.get("MultipleLoctionsTicket5");
			logger.log(Status.PASS, WONumber + " Ticket Status created Successfully");
			List<String> sysIDs = tc.getSysID(WONumber);
			tc.approveSysID(sysIDs.get(0));
			Assert.assertEquals("Pending Approval", tc.getFATicketStatus(WONumber));
			logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
		}

		// GAPI-8993-FA-visitor access- Create ticket for multiple locations and verify the Ticket Status when the location status is Pending for Approval for user with no permissions

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyVisitorAccessPendingApprovalForMultipleLocations(Map<String, String> data)
				throws IOException, InterruptedException {

			logger = extent.createTest(data.get("TestCaseName"));
			if (ticketsCreatedStatusForMultipleLocations == false) {
				createVisitorAccessTicketsForMultipleLocations(data);
			}
			String WONumber = visitorTicketsHashMap.get("MultipleLoctionsTicket6");
			logger.log(Status.PASS, WONumber + " Ticket created Successfully");
			Assert.assertEquals("Pending Approval", tc.getFATicketStatus(WONumber));
			logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
		}

		// GAPI-8997 FA-visitor access- Create ticket for multiple locations and verify
				// the Ticket Status when both location statuses is Approved
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyVisitorAccessApprovedForMultipleLocations(Map<String, String> data)
						throws IOException, InterruptedException {

					logger = extent.createTest(data.get("TestCaseName"));
					if (ticketsCreatedStatusForMultipleLocations == false) {
						createVisitorAccessTicketsForMultipleLocations(data);
					}
					String WONumber = visitorTicketsHashMap.get("MultipleLoctionsTicket1");
					logger.log(Status.PASS, WONumber + " Ticket created Successfully");
					List<String> sysIDs = tc.getSysID(WONumber);
					tc.approveSysID(sysIDs.get(0));
					tc.approveSysID(sysIDs.get(1));
					Assert.assertEquals("Approved", tc.getFATicketStatus(WONumber));
					logger.log(Status.PASS, WONumber + " Ticket Status was Validated Successfully");
				}
		// Reusable method to create Pay load for Single Location.
		public String createVisitorAccessPayLoad(Map<String, String> data) {
			String body = "[\r\n" + "  {\r\n" + "\"visitors\": [  {\"visitorFirstName\": \"" + data.get("visitorFirstName")
					+ "\"," + "\"visitorLastName\": \"" + data.get("visitorLastName") + "\"," + "\"visitorEmail\": \""
					+ data.get("visitorEmail") + "\"," + "\"visitorPhone\": \"" + data.get("visitorPhone") + "\","
					+ "\"company\":\"\"  }]," + "\"visitorType\": \"" + data.get("visitorType") + "\","
					+ "\"escortFirstName\": \"" + data.get("escortFirstName") + "\"," + "\"escortLastName\": \""
					+ data.get("escortLastName") + "\"," + "\"visitorHostFirstName\": \"" + data.get("visitorHostFirstName")
					+ "\"," + "\"visitorHostLastName\": \"" + data.get("visitorHostLastName") + "\","
					+ "\"visitorHostCompany\": \"" + data.get("visitorHostCompany") + "\","
					+ "\"visitorBadgeStartDate\": \"" + accessStartDate + "\"," + "\"visitorBadgeEndDate\": \""
					+ accessEndDate + "\"," + "\"company\": \"" + data.get("company") + "\"," + "\"notes\": \""
					+ data.get("notes") + "\"," + "\"emailNotifications\": \"" + data.get("emailNotifications") + "\","
					+ "\"isExtendedVisitorBadge\": false," + "\"standardVisitorBadgeStr\": \""
					+ data.get("standardVisitorBadgeStr") + "\"," + "\"extendedVisitorBadgeStr\": \""
					+ data.get("extendedVisitorBadgeStr") + "\"," + "\"sites\": [  {\"site\": \"" + data.get("site") + "\","
					+ "\"locations\": [  {\"location\": \"" + data.get("locations") + "\"," + "\"accessStartDate\": \""
					+ accessStartDate + "\"," + "\"accessEndDate\": \"" + accessEndDate + "\","
					+ "\"accessStartTime\": \"06:22:00\"," + "\"accessEndTime\": \"06:22:00\"  }]  " + "}],"
					+ "\"isBulkRequest\":" + data.get("isBulkRequest") + "  }]";
			return body;

		}

		// Reusable Method to create a Pay Load with Multiple Locations
		public String createPayLoadForVisitorAccessMultipleLocations(Map<String, String> data)

		{
			String body = "[\r\n    {\r\n\"visitors\": [\r\n{\r\n                \"" + "visitorFirstName\": \""
					+ data.get("visitorFirstName") + "\",\r\n                \"" + "visitorLastName\": \""
					+ data.get("visitorLastName") + "\",\r\n                \"" + "visitorEmail\": \""
					+ data.get("visitorEmail") + "\",\r\n                \"" + "visitorPhone\": \""
					+ data.get("visitorPhone") + "\",\r\n                \""
					+ "company\": \"\"\r\n            }\r\n        ],\r\n        \"" + "visitorType\": \""
					+ data.get("visitorType") + "\",\r\n        \"" + "escortFirstName\": \"" + data.get("escortFirstName")
					+ "\",\r\n        \"" + "escortLastName\": \"" + data.get("escortLastName") + "\",\r\n        \""
					+ "visitorHostFirstName\": \"hello\",\r\n        \"" + "visitorHostLastName\": \"test8\",\r\n        \""
					+ "visitorHostCompany\": \"civil\",\r\n        \"" + "visitorBadgeStartDate\": \""
					+ visitorBadgeStartDate + "\",\r\n        \"" + "visitorBadgeEndDate\": \"" + visitorBadgeEndDate
					+ "\",\r\n        \"" + "company\": \"Zayo Group, LLC\",\r\n        \""
					+ "notes\": \"unknow test\",\r\n        \""
					+ "emailNotifications\": \"test1@forvisitor.com\",\r\n        \""
					+ "isExtendedVisitorBadge\": false,\r\n        \""
					+ "standardVisitorBadgeStr\": \"Visitor Access (Escorted)\",\r\n        \""
					+ "extendedVisitorBadgeStr\": \"Visitor Access (Escorted)\",\r\n        \"" + "sites\": [\r\n{\r\n  \""
					+ "site\": \"" + data.get("site") + "\",\r\n                \"" + "locations\": [\r\n{\r\n  \""
					+ "location\": \"" + data.get("locations") + "\",\r\n                        \""
					+ "accessStartDate\": \"" + visitorBadgeStartDate + "\",\r\n                        \""
					+ "accessEndDate\": \"" + visitorBadgeEndDate + "\",\r\n                        \""
					+ "accessStartTime\": \"06:22:00\",\r\n                        \""
					+ "accessEndTime\": \"06:22:00\"\r\n  },\r\n  {\r\n \"" + "location\": \"" + data.get("locations2")
					+ "\",\r\n                        \"" + "accessStartDate\": \"" + visitorBadgeStartDate
					+ "\",\r\n                        \"" + "accessEndDate\": \"" + visitorBadgeEndDate
					+ "\",\r\n                        \"" + "accessStartTime\": \"06:22:00\",\r\n                        \""
					+ "accessEndTime\": \"06:22:00\"\r\n }\r\n ]\r\n            }\r\n        ],\r\n        \""
					+ "isBulkRequest\": false\r\n    }\r\n]";

			return body;

		}

		// Reusable Methods to create visitor Access tickets
		public void createVisitorAccessTicketsForSingleLocation(Map<String, String> data)
				throws IOException, InterruptedException {

			for (int i = 1; i < 6; i++) {
				String body = createVisitorAccessPayLoad(data);
				Response response = createVisitorAccessTicket(createToken, body);
				String WONumber = tc.getAttributeFromArrayResponse(response.asString(), "workOrder", "workOrderId");
				visitorTicketsHashMap.put("SingleLoctionTicket" + i, WONumber);
			}
			Thread.sleep(60000);
			ticketsCreatedStatusForSingleLocation = true;
			// return visitorTicketsHashMap;
		}

		// Reusable Methods to create visitor Access tickets
		public void createVisitorAccessTicketsForMultipleLocations(Map<String, String> data)
				throws IOException, InterruptedException {

			for (int i = 1; i < 8; i++) {
				String body = createPayLoadForVisitorAccessMultipleLocations(data);
				Response response = createVisitorAccessTicket(createToken, body);
				String WONumber = tc.getAttributeFromArrayResponse(response.asString(), "workOrder", "workOrderId");
				visitorTicketsHashMap.put("MultipleLoctionsTicket" + i, WONumber);
			}
			Thread.sleep(60000);
			ticketsCreatedStatusForMultipleLocations = true;
			// return visitorTicketsHashMap;
		}



}
