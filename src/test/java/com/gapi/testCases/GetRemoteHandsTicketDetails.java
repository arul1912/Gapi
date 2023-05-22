package com.gapi.testCases;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.midi.Soundbank;

import org.json.JSONArray;
import org.json.JSONObject;

/** getRemoteHandsRequestDetailsById class is used to retrieve REMOTE HANDS TICKET details by using WO number
 * and verify the API response  
 *  @author rkasi
 *  @version 17
 *  @since 2022
 **/

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetRemoteHandsTicketDetails extends BaseClass {

	String path = "/remotehands-tickets/";
	static TestUtilities tc = new TestUtilities();

	

	// *********************REMOTE HANDS GET CALLS TC's****************************
	// ****************************************************************************

	// verify retrieve remote hands ticket by invalid id
	//GAPI-3946-RH- Retrieve Remote hands Requests by Invalid Request ID

	@Test
	public void verifyGetRemoteHandsTicketInfoByInvalidId() {

		logger = extent.createTest("verifyGetRemoteHandsTicketInfoByInvalidId");
		String apiURI = domain + path + "WO7568";
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.get(apiURI).then().extract().response();
		
		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 404"));
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		tc.verifyErrorResponseMessage(response.asString(), "client", "Resource not found.");
		logger.pass("Not Allowed to Retrieve Request Details With Invalid Id");

	}
	// verify retrieve GET ALL remote hands ticket info
	//GAPI-3945-RH- Retrieve Remote hands Requests by Valid Request ID

	@Test
	public void getAllRemoteHandsTicketsInfo() throws InterruptedException, IOException {
		

		String body = "{\r\n" + "    \"site\": \"DFW010\",\r\n" + "    \"requestType\": \"Urgent Work\",\r\n"
				+ "    \"accountName\": \"TelX Group, Inc.\",\r\n" + "    \"category\": \"Other\",\r\n"
				+ "    \"title\": \"title sample\",\r\n"
				+ "    \"detailedInstruction\": \"API�Test�03052021.�Please�locate�the�Cisco�UCS�server�in�rack1�and�connect�to�the�KVM.�Please�provide�screenshots�once�you�connect�to�the�KVM.\"\r\n"
				+ "}";

		String apiURI = domain + path;
		Response crResponse = tc.createRequest(Token, body, apiURI);
		Assert.assertEquals(crResponse.getStatusCode(), 201);
		String WONumber = tc.getWONumberFromResponse(crResponse);
		logger = extent.createTest("Get All RemoteHands Tickets Information");
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "admin@digitalrealty.com")
				.get(apiURI);

		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		Assert.assertTrue(response.asString().contains(WONumber));
		Assert.assertTrue(response.asString().contains("Planned Work"));
		log.info("Successfully extracted all Remote Hands Ticket Info ");
		logger.log(Status.INFO, "Successfully extracted all Remote Hands Ticket Info");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

	}

    // re-usable method to retrieve all remote hands ticket info using request type and category filter values
	
	public void verifyGetAllWithAllRequestTypesAndCategoryValues(String id, String apiURL) {

		logger.createNode("Verify Created WO using GET ALL API");
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
				.get(apiURL);

		System.out.println(response.asString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		Assert.assertTrue(response.asString().contains(id));
		Assert.assertTrue(response.asString().contains("Planned Work") || response.asString().contains("Urgent Work"));
		log.info("Successfully extracted all Remote Hands Ticket Info ");
		logger.log(Status.INFO, "Successfully extracted all Remote Hands Ticket Info");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	}

	
	// verify all request types category values
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getCategoryListForRemoteHandsRequestType(Map<String, String> data) {

		logger = extent.createTest(data.get("TestCaseName"));
		String requestType = data.get("requestType");
		String apiURI = domain + path + "categories?" + requestType;
		System.out.println("Request :"+apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization","Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
				.get(apiURI);
		System.out.println("API response is: "+response.asString());
		

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
				if (requestType.contains("Planned Work")) {
					Assert.assertTrue(categoryName.equalsIgnoreCase("Existing cross connect or connectivity testing")
							|| categoryName.equalsIgnoreCase("Customer premise cabling")
							|| categoryName.equalsIgnoreCase("Tape swaps")
							|| categoryName.equalsIgnoreCase("Equipment installation")
							|| categoryName.equalsIgnoreCase("Equipment de-installation")
							|| categoryName.equalsIgnoreCase("Dedicated escort")
							|| categoryName.equalsIgnoreCase("Auditing") || categoryName.equalsIgnoreCase("Other"));
				} else {

					Assert.assertTrue(categoryName.equalsIgnoreCase("Existing cross connect or connectivity testing")
							|| categoryName.equalsIgnoreCase("Equipment troubleshoot or replacement")
							|| categoryName.equalsIgnoreCase("KVM (keyboard, video, mouse) assistance")
							|| categoryName.equalsIgnoreCase("Power cycle or reboot")
							|| categoryName.equalsIgnoreCase("Other"));

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

	// verify retrieve all remote hands request details using various filters
	//GAPI-3953-RH- Retrieve Remote Hands Requests by Status - Cancellation Requested
	//GAPI-3951-RH- Retrieve Remote Hands Requests by Status - Completed
	//GAPI-3952-RH- Retrieve Remote Hands Requests by Status  -Cancelled
	//GAPI-3950-RH- Retrieve Remote Hands Requests by Status  -In Progress
	//GAPI-3954-RH- Retrieve Remote Hands Requests by Site
	//GAPI-3949-RH- Retrieve Remote Hands Requests by Status  -New
	//GAPI-3955-RH- Retrieve Remote Hands Requests by Request Type - Planned Work
	//GAPI-3957-RH- Retrieve Remote Hands Requests by Category - Existing cross connect or connectivity testing
	//GAPI-3956-RH- Retrieve Remote Hands Requests by Request Type - Urgent Work
	//GAPI-3958-RH- Retrieve Remote Hands Requests by Created By
	//GAPI-3961-RH- Retrieve Remote Hands Requests by SIZE


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetAllWithFilters(Map<String, String> data) {

		logger = extent.createTest(data.get("TestCaseName"));
		String filters = data.get("filters");
		String apiURI = domain + "/remotehands-tickets?" + filters;
		System.out.println("Request is: "+apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
				.get(apiURI);
	    
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
		Map<String, String> objMap = new HashMap<String, String>();
		if (filters.contains("&")) {
			String[] values = filters.split("&");
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
				String actualRequestType = individualRequestInfo.getString("requestType");
				Assert.assertTrue(actualRequestType.equals("Planned Work") || actualRequestType.equals("Urgent Work"));
				String actualCategoryValue = individualRequestInfo.getString("category");
				verifyCategoryValue(actualRequestType, actualCategoryValue);
				logger.log(Status.PASS, "Response details Matched with the given filter criteria");

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
						Assert.assertEquals(String.valueOf(objectsLength), keyValue);
					} else if (keyName.equals("page")) {
						int objectsLength = requestArray.length();
						Assert.assertTrue(objectsLength>0 || objectsLength==25);

					} else {
						String actualValue = individualRequestInfo.getString(keyName);
						Assert.assertEquals(actualValue, keyValue);
					}
				}
				String actualRequestType = individualRequestInfo.getString("requestType");
				Assert.assertTrue(actualRequestType.equals("Planned Work") || actualRequestType.equals("Urgent Work"));
				String actualCategoryValue = individualRequestInfo.getString("category");
				verifyCategoryValue(actualRequestType, actualCategoryValue);
				logger.log(Status.PASS, "Response details Matched with the given filter criteria");

			}
		}
	}

	public void verifyCategoryValue(String actualRequestType, String actualCategoryValue) {

		if (actualRequestType.equals("Planned Work")) {
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Existing cross connect or connectivity testing")
					|| actualCategoryValue.equalsIgnoreCase("Customer premise cabling")
					|| actualCategoryValue.equalsIgnoreCase("Tape swaps")
					|| actualCategoryValue.equalsIgnoreCase("Equipment installation")
					|| actualCategoryValue.equalsIgnoreCase("Equipment de-installation")
					|| actualCategoryValue.equalsIgnoreCase("Dedicated escort")
					|| actualCategoryValue.equalsIgnoreCase("Auditing")
					|| actualCategoryValue.equalsIgnoreCase("Other"));
		} else {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Existing cross connect or connectivity testing")
					|| actualCategoryValue.equalsIgnoreCase("Equipment troubleshoot or replacement")
					|| actualCategoryValue.equalsIgnoreCase("KVM (keyboard, video, mouse) assistance")
					|| actualCategoryValue.equalsIgnoreCase("Power cycle or reboot")
					|| actualCategoryValue.equalsIgnoreCase("Other"));
		}
	}
	
	// *****************Re-Usable Method for GET CALL************************

		// this method is used to retrieve WO information by using ID and validate the response
		
		public String getRequestDetailsById(String Id, String Path) {

			logger.createNode("getCreatedTicketInfoById");
			String apiURI = domain + Path + "/" + Id;
			System.out.println("API URL by ID: "+apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "msirikonda@digitalrealty.com")
					.get(apiURI);

			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
			String allResponsebody = response.getBody().asString();

			log.info("Successfully extracted the created Ticket Info ");
			logger.log(Status.PASS, "Successfully extracted the created WoNumber" + Id + "Info");
			logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response.getStatusLine());

 		 return allResponsebody;

		}
		
		
	
	/*
	 * //@Test(dataProvider = "testCasesData", dataProviderClass =
	 * DataProviderUtility.class) public void
	 * verifyGetAllFilterWithSingleParameter(Map<String, String> data) {
	 * 
	 * logger = extent.createTest(data.get("TestCaseName")); String filters =
	 * data.get("filters"); String apiURI = domain + "/v1/remote-hands?" + filters;
	 * Response response = RestAssured.given() .header("Authorization", Token)
	 * .header("Content-Type", "application/json") .get(apiURI);
	 * 
	 * String expectedStatusCode = data.get("expectedStatusCode"); int
	 * actualStatusCode = response.getStatusCode(); String expectedStatusMessage =
	 * data.get("ExpectedStatusMessage");
	 * Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	 * Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
	 * String allResponsebody = response.asString(); logger.log(Status.INFO,
	 * "API -"+ apiURI); logger.log(Status.PASS,
	 * "Response Status Code and Status Message is "+ response.getStatusLine());
	 * logger.log(Status.PASS, allResponsebody); JSONObject object = new
	 * JSONObject(allResponsebody); JSONArray requestArray =
	 * object.getJSONArray("result"); Assert.assertTrue(requestArray.length()>0);
	 * String[] values = filters.split("="); String keyName = values[0]; String
	 * expectedkeyValue = values[1]; if
	 * (!filters.contains("limit")&&!filters.contains("offset")) { for (int i = 0; i
	 * < requestArray.length(); i++) { JSONObject individualRequestInfo =
	 * requestArray.getJSONObject(i); String actualValue =
	 * individualRequestInfo.getString(keyName); Assert.assertEquals(actualValue,
	 * expectedkeyValue); String actualRequestType =
	 * individualRequestInfo.getString("requestType");
	 * Assert.assertTrue(actualRequestType.equals("Planned Work") ||
	 * actualRequestType.equals("Urgent Work")); String actualCategoryValue =
	 * individualRequestInfo.getString("category");
	 * verifyCategoryValue(actualRequestType,actualCategoryValue);
	 * logger.log(Status.PASS,
	 * "Response details Matched with the given filter criteria");
	 * 
	 * } } else if (keyName.contains("limit")||keyName.contains("offset")) {
	 * if(keyName.equals("limit")) { int objectsLength = requestArray.length();
	 * Assert.assertEquals(String.valueOf(objectsLength), expectedkeyValue); } else
	 * if (keyName.equals("offset")){ int objectsLength = requestArray.length();
	 * Assert.assertEquals(objectsLength, 25); } for (int i = 0; i <
	 * requestArray.length(); i++) { JSONObject individualRequestInfo =
	 * requestArray.getJSONObject(i); String actualRequestType =
	 * individualRequestInfo.getString("requestType");
	 * Assert.assertTrue(actualRequestType.equals("Planned Work") ||
	 * actualRequestType.equals("Urgent Work")); String actualCategoryValue =
	 * individualRequestInfo.getString("category");
	 * verifyCategoryValue(actualRequestType,actualCategoryValue);
	 * logger.log(Status.PASS,
	 * "Response details Matched with the given filter criteria"); } } }
	 */

}
