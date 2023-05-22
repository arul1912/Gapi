package com.gapi.testCases;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class GetVisitorAccessDetails extends BaseClass {

	String path = "/facility-access/visitors";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String visitorBadgeStartDate = tc.getCurrentDateAndTime();
	static String visitorBadgeEndDate = tc.getCurrentDateTime();

	//GAPI-6278-Create Visitor Access Ticket - Approve - Retrieve
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void GetVisitorAccessDetailsInfo(Map<String, String> data) throws InterruptedException, IOException {

		logger = extent.createTest("Get Visitor Access Details Info");
		String body="{\r\n    \"visitors\": [\r\n {\r\n\""
				+ "visitorFirstName\": \""+data.get("visitorFirstName")+"\",\r\n\""
				+ "visitorLastName\": \""+data.get("visitorLastName")+"\",\r\n\""
				+ "visitorEmail\": \""+data.get("visitorEmail")+"\"\r\n}\r\n    ],\r\n\""
				+ "visitorType\": \""+data.get("visitorType")+"\",\r\n    \""
				+ "escortFirstName\": \""+data.get("escortFirstName")+"\",\r\n    \""
				+ "escortLastName\": \""+data.get("escortLastName")+"\",\r\n    \""
				+ "visitorHostFirstName\": \""+data.get("visitorHostFirstName")+"\",\r\n    \""
				+ "visitorHostLastName\": \""+data.get("visitorHostFirstName")+"\",\r\n    \""
				+ "visitorHostCompany\": \""+data.get("visitorHostCompany")+"\",\r\n    \""
				+ "visitorBadgeStartDate\": \""+visitorBadgeStartDate+"\",\r\n    \""
				+ "visitorBadgeEndDate\": \""+visitorBadgeEndDate+"\",\r\n    \""
				+ "company\": \""+data.get("company")+"\",\r\n    \""
				+ "notes\": \""+data.get("notes")+" \",\r\n    \""
				+ "emailNotifications\": \""+data.get("emailNotifications")+"\",\r\n    \""
				+ "isExtendedVisitorBadge\": "+data.get("isExtendedVisitorBadge")+",\r\n    \""
				+ "standardVisitorBadgeStr\": \""+data.get("standardVisitorBadgeStr")+"\",\r\n    \""
				+ "extendedVisitorBadgeStr\": \""+data.get("extendedVisitorBadgeStr")+"\",\r\n    \""
				+ "addSites\": [\r\n        {\r\n            \""
				+ "site\": \""+data.get("site")+"\",\r\n            \""
				+ "locations\": [\r\n                {\r\n                   \""
				+ "location\": \""+data.get("locations")+"\",\r\n                    \""
				+ "accessStartDate\": \""+visitorBadgeStartDate+"\",\r\n                    \""
				+ "accessEndDate\": \""+visitorBadgeEndDate+"\",\r\n                    \""
				+ "accessStartTime\": \"06:22:00\",\r\n                    \""
				+ "accessEndTime\": \"06:22:00\"\r\n                }\r\n            ]\r\n        }\r\n    ]\r\n}";
		String apiURI = domain + path;
		System.out.println("body is :"+apiURI);
		System.out.println("body is :"+body);
		Response crResponse = tc.createFARequest(Token, body, apiURI);
		System.out.println("Response:"+crResponse.asString());
		Assert.assertEquals(crResponse.getStatusCode(), 200);
		String WONumber = tc.getWOIDFromResponse(crResponse);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.get(apiURI);
		System.out.println("Respo :"+response.asPrettyString());

		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		Assert.assertTrue(response.asString().contains(WONumber));
		Assert.assertTrue(response.asString().contains("Visitor Access"));
		log.info("Successfully extracted all Visito access Ticket Info ");
		logger.log(Status.INFO, "Successfully extracted all Visito access Ticket Info");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	}
	//GAPI-8905 -FA Visitor Access - Subject value enhancement for Single Location
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyVisitorAccessGetAccessTickerforSingleLocation(Map<String, String> data) throws InterruptedException, IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = domain + "/facility-access/visitors/access-requests";
		CreateVisitorAccessRequest visitorRequest = new CreateVisitorAccessRequest();
		String body = visitorRequest.createVisitorAccessPayLoad(data);
		System.out.println(body);
		Response response =tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
		System.out.println("Response: " + response.asString());
		String WONumber = tc.getWOIDFromResponse(response);
		System.out.println("WONumber: " + WONumber);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String date = tc.getCurrentDateAndTime();
		if (response.getStatusCode() == 200) {

			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Visitor access request");
			logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Visitor access request_" + WONumber);

		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String getAPIURI = domain + "/facility-access/access-tickets/" + WONumber;
		Response getResponse = tc.getRequest(Token, masterAccountID, accountID, getAPIURI);
		System.out.println(getAPIURI);
		System.out.println("Response: " + getResponse.asString());
		JsonPath responseJsonPath = getResponse.jsonPath();
		String currentDate = tc.getCurrentDate();
		String subject=data.get("visitorFirstName")+" "+data.get("visitorLastName")+" "+"going to visit "+ data.get("site")+" - "+data.get("locations")+" on "+currentDate;
		Assert.assertEquals(responseJsonPath.get("contactType"), subject);
		Assert.assertEquals(responseJsonPath.get("description"), subject);
		logger.log(Status.PASS, "Visitor Access contactType and Descriptions are mataching While retreieving the ticket using Get Access For Single Location");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after Retrieving all the POP POE  Access ticket details by ID"
						+ getResponse.statusLine());
	}
	
	       //GAPI-8906-FA Visitor Access - Subject value enhancement for Multiple Locations
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyVisitorTitleInGetAccessTicketsMultipleLocations(Map<String, String> data) throws InterruptedException, IOException {

				logger = extent.createTest(data.get("TestCaseName"));
				String apiURI = domain + "/facility-access/visitors/access-requests";
				CreateVisitorAccessRequest visitorRequest = new CreateVisitorAccessRequest();
				String body = createPayLoadForVisitorAccessMultipleLocations(data);
				System.out.println(body);
				Response response =tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
				System.out.println("Response: " + response.asString());
				String WONumber = tc.getWOIDFromResponse(response);
				System.out.println("WONumber: " + WONumber);
				String expectedStatusCode = data.get("expectedStatusCode");
				int actualStatusCode = response.getStatusCode();
				String expectedStatusMessage = data.get("ExpectedStatusMessage");
				System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
				Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
				logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
				String date = tc.getCurrentDateAndTime();
				if (response.getStatusCode() == 200) {

					log.info("The WO Number Created is " + WONumber);
					logger.log(Status.PASS, "Successfully Created Visitor access request");
					logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
					tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Visitor access request_" + WONumber);

				} else {
					logger.pass("Error Response message is " + response.asString());
					logger.log(Status.PASS, "Not allowed to create Visitor access request");
					String expectedErrorMessageType = data.get("errorMessageType");
					String expectedErrorMessage = data.get("errorMessage");
					tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
				}

				String getAPIURI = domain + "/service-tickets?id=" + WONumber;
				Response getResponse = tc.getRequest(Token, masterAccountID, accountID, getAPIURI);
				System.out.println(getAPIURI);
				System.out.println("Response: " + getResponse.asString());
				JsonPath responseJsonPath = getResponse.jsonPath();
				String currentDate = tc.getCurrentDate();
				String subject=data.get("visitorFirstName")+" "+data.get("visitorLastName")+" "+"going to visit "+ data.get("site")+" - "+"multiple locations"+" on "+currentDate;
				Assert.assertEquals(responseJsonPath.get("content[0].title"), subject);
		//		Assert.assertEquals(responseJsonPath.get("description"), subject);
				logger.log(Status.PASS, "Visitor Access contactType and Descriptions are mataching While retreieving the ticket using Get Access For Multiple Locations");
				logger.log(Status.PASS,
						"Response Status Code and Status Message is after Retrieving all Visitor  Access ticket details by ID"
								+ getResponse.statusLine());
			}
	
	//GAPI-8970-FA Visitor Access - Title attribute in get service tickets end point for single location
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyVisitorTitleInGetServiceTicketsSingleLocation(Map<String, String> data) throws InterruptedException, IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = domain + "/facility-access/visitors/access-requests";
		CreateVisitorAccessRequest visitorRequest = new CreateVisitorAccessRequest();
		String body = visitorRequest.createVisitorAccessPayLoad(data);
		System.out.println(body);
		Response response =tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
		System.out.println("Response: " + response.asString());
		String WONumber = tc.getWOIDFromResponse(response);
		System.out.println("WONumber: " + WONumber);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String date = tc.getCurrentDateAndTime();
		if (response.getStatusCode() == 200) {

			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Visitor access request");
			logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Visitor access request_" + WONumber);

		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		String getAPIURI = domain + "/service-tickets?id=" + WONumber;
		Response getResponse = tc.getRequest(Token, masterAccountID, accountID, getAPIURI);
		System.out.println(getAPIURI);
		System.out.println("Response: " + getResponse.asString());
		JsonPath responseJsonPath = getResponse.jsonPath();
		String currentDate = tc.getCurrentDate();
		String subject=data.get("visitorFirstName")+" "+data.get("visitorLastName")+" "+"going to visit "+ data.get("site")+" - "+data.get("locations")+" on "+currentDate;
		Assert.assertEquals(responseJsonPath.get("content[0].title"), subject);
		logger.log(Status.PASS, "Title is mataching as per the While retreieving the Visitor Access ticket using Get Service Tickets End point For Single Location");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after Retrieving all the POP POE  Access ticket details by ID"
						+ getResponse.statusLine());
	}
	
		//GAPI-8971-FA Visitor Access - Title attribute in Get Service tickets End point Response for Multiple Locations
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyVisitorTitleInGetServiceTicketsMultipleLocations(Map<String, String> data) throws InterruptedException, IOException {

			logger = extent.createTest(data.get("TestCaseName"));
			String apiURI = domain + "/facility-access/visitors/access-requests";
			CreateVisitorAccessRequest visitorRequest = new CreateVisitorAccessRequest();
			String body = createPayLoadForVisitorAccessMultipleLocations(data);
			System.out.println(body);
			Response response =tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
			System.out.println("Response: " + response.asString());
			String WONumber = tc.getWOIDFromResponse(response);
			System.out.println("WONumber: " + WONumber);
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			System.out.println("Statusmessage & status code is:" + response.statusCode() + response.statusLine());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
			String date = tc.getCurrentDateAndTime();
			if (response.getStatusCode() == 200) {

				log.info("The WO Number Created is " + WONumber);
				logger.log(Status.PASS, "Successfully Created Visitor access request");
				logger.log(Status.PASS, "Created Visitor access request Id  " + WONumber);
				tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Visitor access request_" + WONumber);

			} else {
				logger.pass("Error Response message is " + response.asString());
				logger.log(Status.PASS, "Not allowed to create Visitor access request");
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");
				tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
			}

			String getAPIURI = domain + "/service-tickets?id=" + WONumber;
			Response getResponse = tc.getRequest(Token, masterAccountID, accountID, getAPIURI);
			System.out.println(getAPIURI);
			System.out.println("Response: " + getResponse.asString());
			JsonPath responseJsonPath = getResponse.jsonPath();
			String currentDate = tc.getCurrentDate();
			String subject=data.get("visitorFirstName")+" "+data.get("visitorLastName")+" "+"going to visit "+ data.get("site")+" - "+"multiple locations"+" on "+currentDate;
			Assert.assertEquals(responseJsonPath.get("content[0].title"), subject);
			logger.log(Status.PASS, "Title is mataching as per the While retreieving the Visitor Access ticket using Get Service Tickets End point For Multiple Locations");
			logger.log(Status.PASS,
					"Response Status Code and Status Message is after Retrieving all the POP POE  Access ticket details by ID"
							+ getResponse.statusLine());
		}
	public String createPayLoadForVisitorAccessMultipleLocations(Map<String, String> data)
	
	{
		String body="[\r\n    {\r\n\"visitors\": [\r\n{\r\n                \""
				+ "visitorFirstName\": \""+data.get("visitorFirstName")+"\",\r\n                \""
				+ "visitorLastName\": \""+data.get("visitorLastName")+"\",\r\n                \""
				+ "visitorEmail\": \""+data.get("visitorEmail")+"\",\r\n                \""
				+ "visitorPhone\": \""+data.get("visitorPhone")+"\",\r\n                \""
				+ "company\": \"\"\r\n            }\r\n        ],\r\n        \""
				+ "visitorType\": \""+data.get("visitorType")+"\",\r\n        \""
				+ "escortFirstName\": \""+data.get("escortFirstName")+"\",\r\n        \""
				+ "escortLastName\": \""+data.get("escortLastName")+"\",\r\n        \""
				+ "visitorHostFirstName\": \"hello\",\r\n        \""
				+ "visitorHostLastName\": \"test8\",\r\n        \""
				+ "visitorHostCompany\": \"civil\",\r\n        \""
				+ "visitorBadgeStartDate\": \""+visitorBadgeStartDate+"\",\r\n        \""
				+ "visitorBadgeEndDate\": \""+visitorBadgeEndDate+"\",\r\n        \""
				+ "company\": \"Zayo Group, LLC\",\r\n        \""
				+ "notes\": \"unknow test\",\r\n        \""
				+ "emailNotifications\": \"test1@forvisitor.com\",\r\n        \""
				+ "isExtendedVisitorBadge\": false,\r\n        \""
				+ "standardVisitorBadgeStr\": \"Visitor Access (Escorted)\",\r\n        \""
				+ "extendedVisitorBadgeStr\": \"Visitor Access (Escorted)\",\r\n        \""
				+ "sites\": [\r\n{\r\n  \""
				+ "site\": \""+data.get("site")+"\",\r\n                \""
				+ "locations\": [\r\n{\r\n  \""
				+ "location\": \""+data.get("locations")+"\",\r\n                        \""
				+ "accessStartDate\": \""+visitorBadgeStartDate+"\",\r\n                        \""
				+ "accessEndDate\": \""+visitorBadgeEndDate+"\",\r\n                        \""
				+ "accessStartTime\": \"06:22:00\",\r\n                        \""
				+ "accessEndTime\": \"06:22:00\"\r\n  },\r\n  {\r\n \""
				+ "location\": \""+data.get("locations2")+"\",\r\n                        \""
				+ "accessStartDate\": \""+visitorBadgeStartDate+"\",\r\n                        \""
				+ "accessEndDate\": \""+visitorBadgeEndDate+"\",\r\n                        \""
				+ "accessStartTime\": \"06:22:00\",\r\n                        \""
				+ "accessEndTime\": \"06:22:00\"\r\n }\r\n ]\r\n            }\r\n        ],\r\n        \""
				+ "isBulkRequest\": true\r\n    }\r\n]";

		return body;
		
	}
}
