package com.gapi.testCases;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetCustomerSupportTicketDetails extends BaseClass{
	
	String path = "/customer-support-tickets/";
	static TestUtilities tc = new TestUtilities();
	
	// CUSTOMER SUPPORT GET CALLS TESTCASES
	//*********************************************************************************  
		
	    
		public void getAllCustomerSupportTicketsInfo() throws InterruptedException {
			
	    	logger = extent.createTest("Get All Customer Support Tickets Information");
			String apiURI = domain+ path;
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID) 				
	 				.get(apiURI);      		     	

			logger.log(Status.INFO, response.statusLine());
			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
	 		
	 		log.info("Successfully extracted all Customer Support Ticket Info ");
	 		logger.log(Status.INFO, "Successfully extracted Customer Support Ticket Info" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}
	    
		// verify all request types category values

	    @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getCategoryListForCustomerSupportRequestType(Map<String, String> data)  {

			logger = extent.createTest("Get Category List For Customer Support Request Type");
			String requestType = data.get("requestType");
			String apiURI = domain + path + "categories?" + requestType;
			System.out.println("URI is: " + apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id",accountID)
					.header("User-Email", "msirikonda@digitalrealty.com")
					.get(apiURI);
			System.out.println("Response is:" + response.asString());

			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			String allResponsebody = response.getBody().asString();
			logger.log(Status.INFO, "API -" + apiURI);
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.getStatusLine());
			logger.log(Status.PASS, allResponsebody);
			if (actualStatusCode == 200) {
				JSONObject object = new JSONObject(allResponsebody);
				JSONArray array = object.getJSONArray("result");
				JSONObject object1 = array.getJSONObject(0);
				JSONObject requestTypeObject = object1.getJSONObject("requestType");
				String actualrequestTypeName = requestTypeObject.getString("name");
				Assert.assertTrue(requestType.contains(actualrequestTypeName));
				JSONArray categoryArray = requestTypeObject.getJSONArray("categories");
				String categoryName;
				for (int i = 0; i < categoryArray.length(); i++) {
					JSONObject rec = categoryArray.getJSONObject(i);
					categoryName = rec.getString("name");
					if (requestType.contains("Account Management")) {
						Assert.assertTrue(categoryName.equalsIgnoreCase("User Account Inquiry"));
					}else if (requestType.contains("Accounts Receivable")) {
						Assert.assertTrue(categoryName.equalsIgnoreCase("General Billing Inquiry"));
					} else if (requestType.contains("Facilities Request")) {
						Assert.assertTrue(categoryName.equalsIgnoreCase("Cooling - Datacenter")
								|| categoryName.equalsIgnoreCase("Cooling - Office")
								|| categoryName.equalsIgnoreCase("Electrical")
								|| categoryName.equalsIgnoreCase("Fire/Safety")
								|| categoryName.equalsIgnoreCase("Planned Maintenance"));
					} else if (requestType.contains("Cross Connect")) {
						Assert.assertTrue(categoryName.equalsIgnoreCase("New Install")
								|| categoryName.equalsIgnoreCase("Change (Hot Cut)")
								|| categoryName.equalsIgnoreCase("Disconnect"));							
								
					} else {

						Assert.assertTrue(categoryName.equalsIgnoreCase("Elevator")
								|| categoryName.equalsIgnoreCase("Loading Dock")
								|| categoryName.equalsIgnoreCase("Parking")
								|| categoryName.equalsIgnoreCase("General Inquiry"));

					}
				}
				log.info("Successfully extracted the Remote Hands Category List Info ");
				logger.log(Status.PASS, "Successfully extracted the Remote Hands Category List Info");

			} else {
				logger.pass("Error Response message is " + response.asString());
				logger.log(Status.PASS, "Not allowed to retrieve Remote Hands categoryList");
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");
				tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
			}
		}
	    
	  //GAPI-9148-CS - Retrieve all Customer Support Request - filter - Request Type - Cross Connect
	  //GAPI-9149-CS - Retrieve all Customer Support Request - filter - Request Type - Cross Connect and Category New Install
      //GAPI-9150-CS - Retrieve all Customer Support Request - filter - Request Type - Cross Connect and Category Change (Hot Cut)
      //GAPI-9151-CS - Retrieve all Customer Support Request - filter - Request Type - Cross Connect and Category Disconnect
		
	    //Verify retrieve all CustomerSupportTicket request details using various filters		
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyGetAllWithCustomerSupportTicketFilters(Map<String, String> data) {

			logger = extent.createTest(data.get("TestCaseName"));
			String filters = data.get("filters");
			String apiURI = domain + "/customer-support-tickets?" + filters;
		
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "msirikonda@digitalrealty.com")
					.get(apiURI);
			System.out.println(apiURI);
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			//Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			String allResponsebody = response.asString();
			logger.log(Status.INFO, "API -" + apiURI);
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.getStatusLine());
			logger.log(Status.PASS, allResponsebody);
			JSONObject object = new JSONObject(allResponsebody);
			JSONArray requestArray = object.getJSONArray("content");
			Assert.assertTrue(requestArray.length() >= 0);
			//Assert.assertTrue(requestArray.isEmpty());
			Map<String, String> objMap = new HashMap<String, String>();
			if (filters.contains("&")) {
				String[] values = filters.split("&");//site=DFW10&createdBy=John Johnson

				for (int j = 0; j < values.length; j++) {
					String[] keyPair = values[j].split("=");
					objMap.put(keyPair[0], keyPair[1]);
				}
			} else {
				String[] keyPair = filters.split("=");
				objMap.put(keyPair[0], keyPair[1]);
			}
			if (!filters.contains("size") && !filters.contains("page")) {
				for (int i = 0; i < requestArray.length(); i++) {
					JSONObject individualRequestInfo = requestArray.getJSONObject(i);
					for (Entry<String, String> m : objMap.entrySet()) {
						String actualValue = individualRequestInfo.getString(m.getKey());
						Assert.assertEquals(actualValue, m.getValue());
					}
					String status = individualRequestInfo.getString("status");
					if(!status.equals("Completed")) {
					String actualRequestType = individualRequestInfo.getString("requestType");
					Assert.assertTrue(actualRequestType.equals("Account Management") || actualRequestType.equals("Accounts Receivable") || actualRequestType.equals("Facilities Request") || actualRequestType.equals("Amenities")|| actualRequestType.equals("Cross Connect"));
					String actualCategoryValue = individualRequestInfo.getString("category");
					verifyCategoryValue(actualRequestType, actualCategoryValue);
					logger.log(Status.PASS, "Response details Matched with the given filter criteria");
					}

				}
			} else if (filters.contains("size") || filters.contains("page")) {
				String keyName, keyValue;
				for (int i = 0; i < requestArray.length(); i++) {
					JSONObject individualRequestInfo = requestArray.getJSONObject(i);
					for (Entry<String, String> m : objMap.entrySet()) {
						keyName = m.getKey();
						keyValue = m.getValue();
						
						if (keyName.equals("size")) {
							int objectsLength = requestArray.length();
						//Added by Santosh 
						if(objectsLength<=Integer.parseInt(keyValue))
							{
								Assert.assertTrue(true);
							}
							else
							{
								Assert.assertTrue(false);
							}
							
						//Assert.assertEquals(String.valueOf(objectsLength), keyValue);
						} else if (keyName.equals("page")) {
							int objectsLength = requestArray.length();
							Assert.assertTrue(objectsLength>0 || objectsLength==25);

						} else {
							String actualValue = individualRequestInfo.getString(keyName);
							Assert.assertEquals(actualValue, keyValue);
						}
					}
					String status = individualRequestInfo.getString("status");
					if (!status.equals("Completed")) {
						String actualRequestType = individualRequestInfo.getString("requestType");
						Assert.assertTrue(actualRequestType.equals("Account Management") || actualRequestType.equals("Accounts Receivable") || actualRequestType.equals("Facilities Request") || actualRequestType.equals("Amenities")|| actualRequestType.equals("Cross Connect"));
						String actualCategoryValue = individualRequestInfo.getString("category");
						verifyCategoryValue(actualRequestType, actualCategoryValue);
						logger.log(Status.PASS, "Response details Matched with the given filter criteria");

					}
				}
			}
		}
		
		public void verifyCategoryValue(String actualRequestType, String actualCategoryValue) {

			if (actualRequestType.equals("Account Management")) {
				Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("User Account Inquiry"));
			
			} else if(actualRequestType.equals("Accounts Receivable")) {
				Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("General Billing Inquiry"));
				
			} else if(actualRequestType.equals("Facilities Request")) {
				Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Cooling - Datacenter")
				|| actualCategoryValue.equalsIgnoreCase("Cooling - Office")
				|| actualCategoryValue.equalsIgnoreCase("Electrical")
				|| actualCategoryValue.equalsIgnoreCase("Fire/Safety")
				|| actualCategoryValue.equalsIgnoreCase("Planned Maintenance"));
			} else if (actualRequestType.contains("Cross Connect")) {
				Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("New Install")
						|| actualCategoryValue.equalsIgnoreCase("Change (Hot Cut)")
						|| actualCategoryValue.equalsIgnoreCase("Disconnect"));	
									
			} else {

				    Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Elevator")
						|| actualCategoryValue.equalsIgnoreCase("Loading Dock")
						|| actualCategoryValue.equalsIgnoreCase("Parking")
						|| actualCategoryValue.equalsIgnoreCase("Shipping / Receiving")
						|| actualCategoryValue.equalsIgnoreCase("General Inquiry"));
			}
		}

		
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyCustomerSupportTicketInfoByInvalidId(Map<String, String> data) {
			
			logger = extent.createTest(data.get("TestCaseName"));
			String apiURI = domain+"/customer-support-tickets/"+"WO7568";
			System.out.println("Request is :"+apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "msirikonda@digitalrealty.com")				
	  				.get(apiURI);      		     	
			System.out.println(response.asString());
					Assert.assertEquals(response.getStatusCode(), 404);
			Assert.assertTrue(response.statusLine().contains("Not Found"));
			logger.pass("Response Status Code and Message Is " + response.getStatusLine());
			logger.pass("Error Response Message Is" + response.asString());
			tc.verifyErrorResponseMessage(response.asString(), "client", "Resource not found.");
			logger.pass("Not Allowed to Retrieve Request Details With Invalid Id");
			
		}
		

}
