package com.gapi.testCases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.gherkin.model.Given;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class CreateAllFourInOne extends BaseClass {
	static TestUtilities tc = new TestUtilities();
	public static String path1 = "/permission-groups";
	public static String path2 = "/permissions";
	public static String path3 = "/roles";

	static String date = tc.getCurrentDateAndTime();
	public String permissionGroupId;
	public String permissionsId;

	//GAPI-5571-PG - Create a Permission Group using Mandatory parameters

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class, priority = 1)
	public void CreatePermissionGroup(Map<String, String> data) throws InterruptedException, IOException {

		/*
		 * String body =tc.getRequestBodyWithDynamicData(data);
		 * System.out.println("Request payload is \n" + body);
		 */
		logger = extent.createTest(data.get("TestCaseName"));
		String URI = idpdomain + path1;
		Response response = createPermissionGroupRequest(Tokenidp, URI);
		System.out.println(response.asString());
		logger.log(Status.INFO, response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		System.out.println(expectedStatusCode);
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertEquals(response.getStatusCode(), 200);
		// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		/*
		 * if (response.getStatusCode()==200) { JSONObject object = new
		 * JSONObject(response); JSONArray requestArray =
		 * object.getJSONArray("content"); String Id =
		 * requestArray.getJSONObject(0).getString("id"); // permissionGroupId =
		 * tc.getIdFromResponse(response);
		 */ System.out.println("created Permission GroupID::");
		log.info("The created Permission GroupId is ");
		logger.log(Status.PASS, "Created permissionGroupId is  ");
		logger.log(Status.PASS, "Successfully Created Permission Group");
		tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "permissionGroupId_");
		// tc.retrieveGETResponseAndRequestParameters(permissionGroupId, body, path1);

		logger.pass("Error Response message is " + response.asString());
		logger.log(Status.PASS, "Not allowed to create Permission Group");
		System.out.println(response.asString());
		String expectedErrorMessageType = data.get("errorMessageType");
		String expectedErrorMessage = data.get("errorMessage");
		// tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
	}

	//GAPI-5557-PM - Create a Permission using Mandatory parameters

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class, priority = 2)
	public void CreatePermissions(Map<String, String> data) throws InterruptedException, IOException {

		/*
		 * String body =tc.getRequestBodyWithDynamicData(data);
		 * System.out.println("Request payload is \n" + body); String
		 * finalBody=body.replaceAll("dummyPG", permissionGroupId);
		 * System.out.println("Request payload final body \n" + finalBody);
		 */
		logger = extent.createTest(data.get("TestCaseName"));
		String URI = idpdomain + path2;
		Response response = createPermissionsRequest(Tokenidp, URI);
		System.err.println(response.asPrettyString());
		logger.log(Status.INFO, response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		System.out.println(expectedStatusCode);
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode() == 200) {
			String permissionsId = getIdFromResponse(response);
			System.out.println("created permission ID::" + permissionsId);
			log.info("The created Permission Id is " + permissionsId);
			logger.log(Status.PASS, "Created permission Id is  " + permissionsId);
			logger.log(Status.PASS, "Successfully Created Permissions");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "permissionsId" + permissionsId);
			// tc.retrieveGETResponseAndRequestParameters(permissionsId, body, path2);
		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Permissions");
			System.out.println(response.asString());
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}
	}

	//GAPI-5766-U- Create Role- (EX: R1)- Using Mandatory Parmaters

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class, priority = 3)
	public void CreateRoles(Map<String, String> data) throws InterruptedException, IOException {
		/*
		 * String body="  {\r\n" + "\"name\": \""+data.get("name")+"\",\r\n" +
		 * " \"globalUltimateKey\": \""+data.get("globalUltimateKey")+"\",\r\n" +
		 * " \"permissionGroups\": [\r\n" + " \""+data.get("permissionGroups")+"\"\r\n"
		 * + "],\r\n" + " \"privileges\": [\r\n" + "    {\r\n" +
		 * "        \"id\": \""+data.get("PermissionGroupIDForprivileges")+"\",\r\n" +
		 * "        \"code\": \""+data.get("code")+"\",\r\n" +
		 * "        \"permissionId\": \""+data.get("permissionId")+"\"\r\n" +
		 * "    }\r\n" + "]" + "}\r\n" ;
		 * 

		 * System.out.println("Request payload is \n" + body); String
		 * roleName="automationRoleName"+tc.generateRandomDigits(3); String
		 * roleNameID=body.replaceAll("automationRoleName", roleName); String
		 * varPermissionGroupID=roleNameID.replaceAll(
		 * "automationPermissionGroupForRoles", permissionGroupId);
		 * System.out.println(varPermissionGroupID); String
		 * finalbody=varPermissionGroupID.replaceAll("automationPermissionForRoles",
		 * permissionsId); System.out.println(finalbody);
		 */
		logger = extent.createTest(data.get("TestCaseName"));
		String URI = idpdomain + path3;
		Response response = createRolesRequest(Tokenidp, URI);
		logger.log(Status.INFO, response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		System.out.println(expectedStatusCode);
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode() == 200) {
			String rolesId = getIdFromResponse(response);
			System.out.println("Created Role ID::" + rolesId);
			log.info("The created Role Id is " + rolesId);
			logger.log(Status.PASS, "Created Role Id is  " + rolesId);
			logger.log(Status.PASS, "Successfully Created Roles");
			// tc.saveRequestIdToExcelSheet(excelPath, "rolesId", 0, 1, date, "rolesId" +
			// rolesId);
			// tc.retrieveRolesGETResponseAndRequestParameters(rolesId, body, path3);
		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Roles");
			System.out.println(response.asString());
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}
	}

	public static Response createPermissionGroupRequest(String Token, String apiURI) throws IOException {

		Response getResponse = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("User-Email", "admin@digitalrealty.com")
				.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID).get(apiURI).then()
				.extract().response();

//						.header("Content-Type", "application/x-www-form-urlencoded")
//						.header("cache-control", "no-cache")
//						.formParam("client_id", "qa_telx_group")
//						.formParam("client_secret", "952ef3014c0b495c99f7ad77572dd481")
//						.formParam("grant_type", "client_credentials")
//						.post("https://login-a1.interxion.com/connect/token");
//
//				Token = getResponse.jsonPath().get("access_token");

		// System.out.println(getResponse.asString());

		return getResponse;
	}

	public static Response createPermissionsRequest(String Token, String APIuri) throws IOException {

		Response response = RestAssured.given().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("User-Email", "admin@digitalrealty.com")
				.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID).get(APIuri).then()
				.extract().response();

		return response;
	}

	public static Response createRolesRequest(String Token, String APIuri) throws IOException {

		Response response = RestAssured.given().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("User-Email", "admin@digitalrealty.com")
				.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID).get(APIuri).then()
				.extract().response();
		return response;

	}

//Re-uasble method to get Id
	public static String getIdFromResponse(Response response) {

		JSONObject object = new JSONObject(response.asString());
		JSONArray requestArray = object.getJSONArray("content");
		String Id = requestArray.getJSONObject(0).getString("id");

		return Id;
	}

}
