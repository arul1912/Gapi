package com.gapi.testCases;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetCrossConnectDetails extends BaseClass {

	String authToken;
	String path = "/cross-connect-inventory";
	// String audience="https://api-uat.digitalrealty.com";
	static TestUtilities tc = new TestUtilities();

	// verify retrieve Cross Connect Details using Get Calls
	//GAPI-4529-CC - Retrieve the Cross Connect details by exceeds the Max Limit value(Max limit=1000)
	//GAPI-4508-CC- Retrieve Cross Connet Requests by Iinterconnection Panel
	//GAPI-4664-CC - Retrieve the cross connect details by  providing the Limit values and Not specifying the offset value
	//GAPI-4505-CC- Retrieve Cross Connect Requests by Interconnection Port Status  -Available
	//GAPI-4506-CC- Retrieve Cross Connet Requests by Interconnection PortStatus  -In-Service
	//GAPI-4507-CC- Retrieve Cross Connect Requests by Interconnection Port Status  -Pre-Wired
	//GAPI-4504-CC- Retrieve Cross Connect Requests by Billing Account Number
	//GAPI-4510-CC- Retrieve Cross-Connect Requests by Offset
	//GAPI-4509-CC- Retrieve all Cross-Connect Request - filter - Limit(Min 25 to Max 1000)
	//GAPI-4516-CC - Retrieve the Cross Connect Details by valid Limit & Offset values
	//GAPI-4663-CC - Retreive the cross connect details by NOT providing the offset&Limit values
	//GAPI-4673-CC - Retreive the cross connect details by providing the offset value greater than all available records
	//GAPI-4669-CC - Retrive the Cross connect data by providing Same Limit & Offset values

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCrossConnectDetailsUsingDifferentFilters(Map<String, String> data) {

		logger = extent.createTest(data.get("TestCaseName"));
		String crossconnectfilters = data.get("filters");
		System.out.println("CC: " + crossconnectfilters);
		String apiURI = CCdomain + path + crossconnectfilters;
		System.out.println("URI is: " + apiURI);
		Response response = RestAssured.given().header("Authorization", "Bearer " + TokenCC)
				.header("Content-Type", "application/json").header("Master-Account-Id", cmasterAccountID)
				.header("Account-Id", caccountID).header("User-Email", "phantom.aao.dlr@gmail.com").get(apiURI);

		System.out.println("Response is: " + response.prettyPrint());
		// String allResponsebody = response.asString();
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		String errorMessage = data.get("errorMessageType");
		logger.log(Status.PASS, "API URI :" + apiURI);
		logger.log(Status.PASS, "Response :" + response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String allResponsebody = response.asString();

		if (actualStatusCode == 200) {
			JSONObject object = new JSONObject(allResponsebody);
			JSONArray requestArray = object.getJSONArray("content");
			Assert.assertTrue(requestArray.length() >= 0);
			Map<String, String> objMap = new HashMap<String, String>();
			crossconnectfilters = crossconnectfilters.replace("?", "");
			if (crossconnectfilters.contains("&")) {
				String[] values = crossconnectfilters.split("&");
				for (int j = 0; j < values.length; j++) {
					String[] keyPair = values[j].split("=");
					objMap.put(keyPair[0], keyPair[1]);
				}
			} else if (!(crossconnectfilters == "")) {
				String[] keyPair = crossconnectfilters.split("=");
				objMap.put(keyPair[0], keyPair[1]);
			}
			if (!crossconnectfilters.contains("limit") && !crossconnectfilters.contains("offset")) {
				for (int i = 0; i < requestArray.length(); i++) {
					JSONObject individualRequestInfo = requestArray.getJSONObject(i);
					for (int index = 0; index < requestArray.length(); index++) {

						Assert.assertEquals(response.getBody().jsonPath().getString("content[" + i + "].accountId"),
								caccountID);

					}
					if (crossconnectfilters.contains("interconnectionPanel")) {
						Assert.assertEquals(
								response.getBody().jsonPath().getString("content[" + i + "].interconnectionPanel"),
								objMap.get("interconnectionPanel"));

					}
					if (crossconnectfilters.contains("interconnectionPortStatus")) {
						Assert.assertEquals(
								response.getBody().jsonPath().getString("content[" + i + "].interconnectionPortStatus"),
								objMap.get("interconnectionPortStatus"));

					}
				}

			} else if (crossconnectfilters.contains("limit") || crossconnectfilters.contains("offset")) {
				String keyName, keyValue;
				/*
				 * if(crossconnectfilters.contains("limit")) {
				 * Assert.assertEquals(String.valueOf(requestArray.length()),objMap.get("limit")
				 * ); } if(crossconnectfilters.contains("offset")) {
				 * Assert.assertEquals(String.valueOf(requestArray.length()),objMap.get("offset"
				 * )); }
				 */
				// for (int i = 0; i < requestArray.length(); i++) {
				// JSONObject individualRequestInfo = requestArray.getJSONObject(i);
				for (Entry<String, String> m : objMap.entrySet()) {
					keyName = m.getKey();
					keyValue = m.getValue();
					if (keyName.equals("limit")) {
						int objectsLength = requestArray.length();
						Assert.assertEquals(String.valueOf(objectsLength), keyValue);
					} else if (keyName.equals("offset")) {
						int objectsLength = requestArray.length();
						Assert.assertTrue(objectsLength > 0 || objectsLength == 25);

					} /*
						 * else { String actualValue = individualRequestInfo.getString(keyName);
						 * Assert.assertEquals(actualValue, keyValue); }
						 */
				}

				// }
				logger.log(Status.PASS, "Response details Matched with the given filter criteria");
			} else {
				logger.pass("Error Response message is " + response.asString());
				logger.log(Status.PASS, "Not allowed to create Remote Hands request");
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");
				tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
			}
		} else if (actualStatusCode == 400) {
			tc.verifyErrorResponseMessage(response.asString(), "client", errorMessage);
		}
	}

	// verify retrieve cross connect details by valid id
	//GAPI-4531-CC- Retrieve Cross Connect Inventory Details by ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class, groups = { "All" })
	public void verifyGetCrossConnectDetailsByValidId(Map<String, String> data) {

		logger = extent.createTest("verifyGetCrossConnectDetailsByValidId");
		String apiURI = CCdomain + "/cross-connect-inventory/" + "X17XY33OWFJKJ0";
		System.out.println("URI :" + apiURI);
		Response response = RestAssured.given().header("Authorization", "Bearer " + TokenCC)
				.header("Content-Type", "application/json").header("Master-Account-Id", cmasterAccountID)
				.header("Account-Id", caccountID).header("User-Email", "phantom.aao.dlr@gmail.com").get(apiURI);
		System.err.println("Response is: " + response.asString());

		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String actualmessage = response.getStatusLine();
		System.out.println("code: " + actualStatusCode);
		System.out.println("message: " + actualmessage);
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertEquals(response.statusLine().trim(), "HTTP/1.1 200 OK");

		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Response Message Is" + response.asString());
		logger.pass("Allowed to Retrieve Request Details With valid Id");

	}

	// verify retrieve cross connect details by invalid id
	//GAPI-4532-CC- Retrieve Cross Connect Inventory by Invalid ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsByInValidId(Map<String, String> data) {

		logger = extent.createTest("verifyGetCrossConnectDetailsByInValidId");
		String apiURI = CCdomain + "/cross-connect-inventory/" + "ZYJWVGVBAYE";
		System.out.println("URI :" + apiURI);
		Response response = RestAssured.given().header("Authorization", "Bearer " + TokenCC)
				.header("Content-Type", "application/json").header("Master-Account-Id", "0012E00002S4aOtQAJ")
				.header("Account-Id", "0012E00002dzucpQAA").header("User-Email", "phantom.aao.dlr@gmail.com")
				.get(apiURI);
		System.err.println("Response is: " + response.asString());

		String expectedErrorMessageType = data.get("errorMessageType");
		String expectedErrorMessage = data.get("errorMessage");
		System.out.println("Message:" + expectedErrorMessageType);
		System.out.println("message1 :" + expectedErrorMessage);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With Invalid Id");

	}

	// verify retrieve cross connect details by Empty Billing Account Number
	//GAPI-4511-CC- Retrieve Cross Connect Requests by Invalid Accounnt Id

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsByInvalidBillingAccountNumber(Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String crossconnectfilters = data.get("filters");
		String apiURI = CCdomain + path + crossconnectfilters;
		System.out.println("URI :" + apiURI);
		Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.asString().contains(" \"content\": []"));
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With Empty Billing Account Number");

	}

	// verify retrieve cross connect details by In-valid Interconnection PortStatus
	//GAPI-4512-CC- Retrieve Cross Connet Requests by In-valid  Interconnection PortStatus

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsByInvalidInterconnectionPortStatus(Map<String, String> data)
			throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + "/cross-connect-inventory?" + data.get("filters");
		System.out.println("URI :" + apiURI);
		Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		String expectedErrorMessageType = "client";
		String expectedErrorMessage = "Query parameter interconnectionPortStatus can only have one of the following values: 'Pre-Wired', 'In-Service', 'Available'";
		System.out.println("Message:" + expectedErrorMessageType);
		System.out.println("message1 :" + expectedErrorMessage);
		Assert.assertEquals(response.getStatusCode(), 400);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With In-valid  Interconnection PortStatus");

	}

	// verify retrieve cross connect details by valid Inter connection Port Status &
	// Invalid inter connect Panel
	//GAPI-4515-CC - Retrieve the Cross Connect Details by valid Inter connection  Port Status & Invalid inter connect Panel

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsByValidInterconnectionPortStatusAndInvalidInterConnectPanel(
			Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + "/cross-connect-inventory?" + data.get("filters");
		System.out.println("URI :" + apiURI);
		Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.asString().contains("\"content\": []"));
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass(
				"Not Allowed to Retrieve Request Details With valid Inter connection  Port Status & Invalid inter connect Panel");

	}

	// Retrieve the Cross connect details by Invalid Bearer Token
	//GAPI-4533-CC- Retrieve Cross Connect Inventory details by In-Valid Bearer Token

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsByInvalidBearerToken(Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + "/cross-connect-inventory?" + data.get("filters");
		System.out.println("URI :" + apiURI);
		String invalidToken = "123444";
		Response response = tc.getRequest(invalidToken, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		String expectedErrorMessageType = "client";
		String expectedErrorMessage = "Unauthorized. Access token is missing or invalid.";
		System.out.println("Message:" + expectedErrorMessageType);
		System.out.println("message1 :" + expectedErrorMessage);
		Assert.assertEquals(response.getStatusCode(), 401);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With In-valid  Interconnection PortStatus");

	}

	// Retrieve the Cross connect details by Invalid Limit value(limit=0)
	//GAPI-4528-CC - Retrieve the Cross connect details by Invalid Limit value(limit=0)

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsByInvalidLimitValue(Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + "/cross-connect-inventory?" + data.get("filters");
		System.out.println("URI :" + apiURI);
		Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		String expectedErrorMessageType = "client";
		String expectedErrorMessage = "Query parameter 'limit' value has to be between '1' and '1000'.";
		System.out.println("Message:" + expectedErrorMessageType);
		System.out.println("message1 :" + expectedErrorMessage);
		Assert.assertEquals(response.getStatusCode(), 400);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With Invalid Limit value");

	}

	// Retrieve the cross connect details by providing the Negative Offset values
	//GAPI-4530-CC - Retrieve the Cross connect details by NEGATIVE  Offset value(Offset=-100)

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsWithNegativeOffset(Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + path + data.get("filters");
		System.out.println("URI :" + apiURI);
		Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		String expectedErrorMessageType = "client";
		String expectedErrorMessage = "Query parameter 'offset' can't be a negative number.";
		System.out.println("Message:" + expectedErrorMessageType);
		System.out.println("message1 :" + expectedErrorMessage);
		Assert.assertEquals(response.getStatusCode(), 400);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With Negative Offset values");

	}

	// Retreive the cross connect details by  providing the Non-Interger Offset
	// values
	//GAPI-4667-CC - Retreive the cross connect details by  providing the Non-Interger Offset values

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsWithNonIntergerOffset(Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + path + data.get("filters");
		System.out.println("URI :" + apiURI);
		Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		String expectedErrorMessageType = "client";
		String expectedErrorMessage = "Query parameter 'offset' has to be formatted as 'integer'.";

		Assert.assertEquals(response.getStatusCode(), 400);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With Non-Interger Offset values");

	}

	// Retrieve the cross connect details by  providing the Non-Integer Limit values
	//GAPI-4666-CC - Retreive the cross connect details by  providing the Non-Interger Limit values

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsWithNonIntegerLimitValues(Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + path + data.get("filters");
		System.out.println("URI :" + apiURI);
		Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		String expectedErrorMessageType = "client";
		String expectedErrorMessage = "Query parameter 'limit' has to be formatted as 'integer'.";

		Assert.assertEquals(response.getStatusCode(), 400);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With Non-Integer Limit values");

	}

	// Retrieve the cross connect details by  providing the Negative Limit values
	//GAPI-4665-CC - Retreive the cross connect details by  providing the NEGATIVE Limit values

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsWithNegativeLimitValues(Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + path + data.get("filters");
		System.out.println("URI :" + apiURI);
		Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		String expectedErrorMessageType = "client";
		String expectedErrorMessage = "Query parameter 'limit' has to be formatted as 'integer'.";

		Assert.assertEquals(response.getStatusCode(), 400);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With Negative Limit values");

	}

	// Retrieve the Cross connect details using id by Invalid Bearer Token
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetCrossConnectDetailsusingIDByInvalidBearerToken(Map<String, String> data) throws IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = CCdomain + "/cross-connect-inventory/" + "ZYJWVGVBAYE";
		System.out.println("URI :" + apiURI);
		String invalidToken = "123444";
		Response response = tc.getRequest(invalidToken, cmasterAccountID, caccountID, apiURI);
		System.err.println("Response is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		// String expectedStatusMessage = data.get("ExpectedStatusMessage");
		String expectedErrorMessageType = data.get("errorMessageType");
		String expectedErrorMessage = data.get("errorMessage");
		System.out.println("Message:" + expectedErrorMessageType);
		System.out.println("message1 :" + expectedErrorMessage);
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		logger.pass("Not Allowed to Retrieve Request Details With In-valid bearer token");

	}
}
