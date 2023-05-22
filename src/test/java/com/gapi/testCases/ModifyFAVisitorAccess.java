package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;


import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ModifyFAVisitorAccess extends BaseClass {
	public static String path="/facility-access/visitors/";
	static TestUtilities tc = new TestUtilities();
	static String visitorBadgeStartDate = tc.getCurrentDateAndTime();
	static String visitorBadgeEndDate = tc.getCurrentDateTime();
	String date=visitorBadgeStartDate;
	// MOdify Visitor Access Ticket with all possible combinations	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyModifyVisitorAccessRequest(Map<String, String> data) throws IOException, InterruptedException {
		
		logger = extent.createTest(data.get("TestCaseName"));
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

		
		System.out.println("Body is : " + body);
				
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body).put(domain+path)
				.then().extract().response();
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		if (response.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Modified Visitor access request");
			logger.log(Status.PASS, "Modified Visitor access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Modify_visitor_" + WONumber);
		} else {
			logger.info("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to Modify Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
		}
	}

	GetServiceTicketDetails getservice = new GetServiceTicketDetails();

	String path2 = "/facility-access/access-tickets/";
		//GAPI-9001-Modify Visitor Access (Add Site) and verify the weather the category attribute value as (Modify Visitor Access) from Service Ticket Get call response
		//GAPI-9010-Modify Visitor Access (Add Site) and verify the weather the Description attribute value as (Modify Visitor Access) from Service Ticket Get call response

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyAttributeModifyVisitorAccessRequest(Map<String, String> data) throws IOException, InterruptedException {
			
			logger = extent.createTest(data.get("TestCaseName"));
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

			
			System.out.println("Body is : " + body);		
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "vigneswarareddy@digitalrealty.com")
					.body(body).put(domain+path)
					.then().extract().response();
			System.out.println("Response is : " + response.asString());
			System.out.println("Status Code is : " + response.getStatusCode());
			String WONumber = tc.getAttributeFromArrayResponse(response.asString(),"workOrder","workOrderId");
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode  = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
			String apiURI = domain+ path2+WONumber;
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
				String getdescription = tc.getAttributeFromJsonResponse(getresponse.asString(),"description");
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
}

