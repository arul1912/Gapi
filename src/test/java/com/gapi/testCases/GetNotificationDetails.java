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

public class GetNotificationDetails extends BaseClass {

	static String path = "/notifications";
	static TestUtilities tc = new TestUtilities();

	// verify retrieve Notification by invalid id
	//GAPI-5645	N - Retrieve Notification details by - Invalid NotificationID

	@Test
	public void verifyGetNotificationInfoByInvalidId() {

		logger = extent.createTest("verifyGetNotificationInfoByInvalidId");
		String apiURI = domain + path + "/WO8478020__959f5bb5";
		System.out.println("URI is: " + apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).get(apiURI).then().extract().response();
		System.out.println("Response is:" + response.asString());

		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 404"));
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		tc.verifyErrorResponseMessage(response.asString(), "client", "Resource not found.");
		logger.pass("Not Allowed to Retrieve Request Details With Invalid Id");

	}
	
	//GAPI-5631	N-Emergency Repairs - Detailed view
	//GAPI-5622	N-Planned Site Maintenance- Summary View
    //GAPI-8691	N-Verify the attributes in Detailed view notification(GET-ID) response payload



	@Test
	public void getAllNotificationDetailsInfo() throws InterruptedException, IOException {

		logger = extent.createTest("getAllNotificationDetailsInfo");
		String apiURI = domain + path;
		Response response = notifications(Token, apiURI);
		System.out.println("===>" + apiURI);

		System.out.println("Response is:" + response.asPrettyString());
		String NotifID = tc.getNotificationIDFromResponse(response);
		System.out.println("ID:===>" + NotifID);

		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));

		log.info("Successfully extracted all get Notification Details Info ");
		logger.log(Status.INFO, "Successfully extracted get Notification Details Info");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

	}
	//GAPI-5644-N - Retrieve Notification details by - Valid NotificationID

	@Test
	public void getNotificationDetailsById() throws IOException {

		logger = extent.createTest("getNotificationDetailsById");
		String apiURI = domain + path;
		Response response = notifications(Token, apiURI);

		String NotifID = tc.getNotificationIDFromResponse(response);
		apiURI = domain + path + "/" + NotifID;
		System.out.println("API URL by ID: " + apiURI);
		Response response1 = notifications(Token, apiURI);

		Assert.assertEquals(response1.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		String allResponsebody = response1.getBody().asString();
		System.out.println("===>" + allResponsebody);

		log.info("Successfully extracted the Notification ID Info ");
		// logger.log(Status.PASS, "Successfully extracted the created WoNumber" +
		// NotifID + "Info");
		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response1.getStatusLine());

	}

	//GAPI-5646-N - Update a RedFlag attribute of an existing Notification using valid NotificationId
	//GAPI-5647-N - Update a DeleteFlag attribute of an existing Notification using valid NotificationId
	//GAPI-5649-N - Update a All attribute of an existing Notification using valid NotificationId

	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void updateNotificationById(Map<String, String> data) throws IOException {
		System.out.println(data.get("TestCaseName") + "\n");
		String readFlag = data.get("readFlag");
		String deleteFlag = data.get("deleteFlag");
		logger = extent.createTest(data.get("TestCaseName"));
		logger.log(Status.INFO,
				"Values to be Updated : readFlag is: " + readFlag + "  and  " + "deleteFlag is: " + deleteFlag);
		String apiURI = domain + path;
		Response response = notifications(Token, apiURI);
		String NotifID = tc.getNotificationIDFromResponse(response);
		boolean readflagBefore = getflagvalueResponseElement(response.asString(), "readFlag");
		boolean deleteflagBefore = getflagvalueResponseElement(response.asString(), "deleteFlag");

		System.out.println("Response before updation: " + response.getBody().asString());
		String body = "{\r\n    \"readFlag\": " + readFlag + ",\r\n    \"deleteFlag\": " + deleteFlag
				+ ",\r\n    \"isDcimNotification\": false\r\n}";
		System.out.println(body);
		String apiURI1 = domain + path + "/" + NotifID;
		System.out.println("API URL by ID: " + apiURI);
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body.trim()).put(apiURI1).then().extract().response();
		String allResponsebody = response1.getBody().asString();
		System.out.println("===>" + allResponsebody);
		logger.log(Status.INFO, response.statusLine());
		logger.log(Status.PASS, "Response Status Code is " + response.statusCode());
		logger.log(Status.PASS, "Notification Id : " + NotifID);
		String expectedStatusCode = data.get("expectedStatusCode");
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		int actualstatuscode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));

		String apiURI2 = domain + path;
		Response response2 = notifications(Token, apiURI2);
		boolean readflagafter = getflagvalueResponseElement(response2.asString(), "readFlag");
		boolean deleteflagafter = getflagvalueResponseElement(response2.asString(), "deleteFlag");
		logger.log(Status.PASS, "readflag before: " + readflagBefore + " , " + "readflag After: " + readflagafter);
		logger.log(Status.PASS,
				"deleteflag before: " + deleteflagBefore + " , " + "deleteflag After: " + deleteflagafter);
		System.out.println("readflag before: " + readflagBefore + " , " + "readflag After: " + readflagafter);
		System.out.println("deleteflag before: " + deleteflagBefore + " , " + "deleteflag After: " + deleteflagafter);
	}

	// updateNotificationByInvalidId
	//GAPI-5650-N - Update a All attribute of an existing Notification using valid NotificationId
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void updateNotificationByInvalidId(Map<String, String> data) throws IOException {
		System.out.println(data.get("TestCaseName") + "\n");
		logger = extent.createTest(data.get("TestCaseName"));
		String readFlag = data.get("readFlag");
		String deleteFlag = data.get("deleteFlag");
		String NotifID = "1238478020__959f5bb5";
		logger.info("Invalid Id is " + NotifID);
		logger.info("Values to be Updated : readFlag is: " + readFlag + "  and  " + "deleteFlag is: " + deleteFlag);
		String body = "{\r\n    \"readFlag\": " + readFlag + ",\r\n    \"deleteFlag\": " + deleteFlag
				+ ",\r\n    \"isDcimNotification\": false\r\n}";
		System.out.println(body);
		String apiURI = domain + path + "/" + NotifID;
		System.out.println("API URL by ID: " + apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body.trim()).put(apiURI).then().extract().response();
		String allResponsebody = response.getBody().asString();
		System.out.println("===>" + allResponsebody);
		System.out.println(response.statusLine());
		String expectedStatusCode = data.get("expectedStatusCode");
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		int actualstatuscode = response.getStatusCode();

		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
	}

//
	// getNotificationDetailsByNegativeToken
	//GAPI-5628-N-Planned Site Maintenance - Retrieve without mandatory Headers( Token)_ Negative

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getNotificationDetailsByNegativeToken(Map<String, String> data) throws IOException {
		System.out.println(data.get("TestCaseName") + "\n");
		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = domain + path;
		Response response = notifications(Token, apiURI);

		String NotifID = tc.getNotificationIDFromResponse(response);
		String apiURI1 = domain + path + "/" + NotifID;
		System.out.println("API URL by ID: " + apiURI);
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com").get(apiURI1)
				.then().extract().response();

		String allResponsebody = response1.getBody().asString();
		System.out.println("===>" + allResponsebody);
		String expectedStatusCode = data.get("expectedStatusCode");
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		String expectedErrorMessageType = data.get("errorMessageType");
		String expectedErrorMessage = data.get("errorMessage");
		int actualstatuscode = response1.getStatusCode();

		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response1.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response1.statusLine());
		Assert.assertTrue(response1.asString().contains(expectedErrorMessageType));
		Assert.assertTrue(response1.asString().contains(expectedErrorMessage));
		logger.info("Error Response message is " + response1.asString());
		logger.log(Status.PASS, "Not allowed with Negative Token");
	}

	// Re-usable method

	public boolean getflagvalueResponseElement(String response, String keyName) {

		JSONArray object = new JSONArray(response);
		JSONObject Jsonvalue = (JSONObject) object.get(0);
		boolean flag = Jsonvalue.getBoolean(keyName);
		return flag;
	}

	// ****************************************************Update a All attribute
	// with wrong values of an existing Notification using
	// NotificationId*************
	//GAPI-5651-N - Update a All attribute with wrong values of an existing Notification using NotificationId

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void updateAttributesOfNotification(Map<String, String> data) throws IOException {

		System.out.println(data.get("TestCaseName") + "\n");
		String readFlag = data.get("readFlag");
		String deleteFlag = data.get("deleteFlag");
		logger = extent.createTest(data.get("TestCaseName"));
		logger.log(Status.INFO,
				"Values to be Updated : readFlag is: " + readFlag + "   and  " + "deleteFlag is: " + deleteFlag);
		String apiURI = domain + path;
		Response response = notifications(Token, apiURI);
		String NotifID = tc.getNotificationIDFromResponse(response);

		System.out.println("Response before updation: " + response.getBody().asString());
		String body = "{\r\n    \"readFlag\": " + readFlag + ",\r\n    \"deleteFlag\": " + deleteFlag
				+ ",\r\n    \"isDcimNotification\": false\r\n}";
		System.out.println(body);
		String apiURI1 = domain + path + "/" + NotifID;
		System.out.println("API URL by ID: " + apiURI);
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body.trim()).put(apiURI1).then().extract().response();
		String allResponsebody = response1.getBody().asString();
		System.out.println("===>" + allResponsebody);
		logger.log(Status.INFO, response1.statusLine());
		System.out.println(response1.statusLine());
		logger.log(Status.PASS, "Response Status Code is " + response1.statusCode());
		logger.log(Status.PASS, "Notification Id : " + NotifID);
		String expectedStatusCode = data.get("expectedStatusCode");
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		int actualstatuscode = response1.getStatusCode();
		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response1.statusLine().contains(expectedStatusMessage));
	}

	// Re-usable methods for Notification Retrieval
	// *************************************************************************************

	public static Response notifications(String Token, String apiURI) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com")
				.get(domain + path).then().extract().response();

		return response;
	}

}
