package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UsersRBAC extends BaseClass {

	public static String path = "/users/f0798e65-4425-452e-af9e-ec204764a087/assignments";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static CreateNewUser details = new CreateNewUser();
	CreateRemoteHandsTicket createRemoteHandsTicket = new CreateRemoteHandsTicket();

	// RBAC Assign user with Asset ID's in two different countries

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)

	public void userAccountAssociationRBAC(Map<String, String> data) throws IOException, InterruptedException {

		String body = "{\r\n    \"firstName\": \"" + data.get("firstName") + "\",\r\n " + "\"lastName\": \""
				+ data.get("lastName") + "\",\r\n " + "\"phone\": \"" + data.get("phone") + "\",\r\n  "
				+ "\"userAccounts\": [\r\n {\r\n " + " \"legalEntityKey\": \"" + data.get("legalEntityKey") + "\",\r\n "
				+ " \"associations\": [\r\n{\r\n " + "\"role\": \"" + data.get("role") + "\",\r\n"
				+ "\"assets\": [\r\n{\r\n" + "\"assetId\": \"" + data.get("assetId1") + "\"\r\n},\r\n{\r\n"
				+ "\"assetId\": \"" + data.get("assetId2") + "\"\r\n }\r\n ]\r\n}\r\n]\r\n}\r\n]\r\n}";

		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = userAccountAssociation(Tokenidp, body);
		System.out.println(response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		System.out.println(expectedStatusCode);
		int actualStatusCode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		System.out.println("===>" + response.asString());

	}

	// RBAC Multiple Asset ID's User Association
	//GAPI-6895-Assign a user with a single Asset id's
	//GAPI-6896-Assign a user with multiple Asset id's

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)

	public void multipleAssetsAssociationRBAC(Map<String, String> data) throws IOException, InterruptedException {

		String body = "{\r\n \"firstName\": \"" + data.get("firstName") + "\",\r\n" + "\"lastName\": \""
				+ data.get("lastName") + "\",\r\n " + "\"phone\": \"" + data.get("phone") + "\",\r\n "
				+ "\"userAccounts\": [\r\n{\r\n " + "\"legalEntityKey\": \"" + data.get("legalEntityKey") + "\",\r\n "
				+ "\"associations\": [\r\n{\r\n" + "\"role\": \"" + data.get("role") + "\",\r\n"
				+ "\"assets\": [\r\n{\r\n" + "\"assetId\": \"" + data.get("assetId1") + "\"\r\n },\r\n{\r\n "
				+ "\"assetId\": \"" + data.get("assetId2") + "\"\r\n},\r\n{\r\n " + "\"assetId\": \""
				+ data.get("assetId3") + "\"\r\n},\r\n {\r\n" + "\"assetId\": \"" + data.get("assetId4")
				+ "\"\r\n }\r\n ]\r\n }\r\n ]\r\n }\r\n]\r\n}";

		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = userAccountAssociation(Tokenidp, body);
		System.out.println(response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		System.out.println(expectedStatusCode);
		int actualStatusCode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		System.out.println("===>" + response.asString());
	}

	// RBAC users site path Association
	//GAPI-6884-Assign a user with all the sites present in one region
	//GAPI-6891-Assign a User with  one country and one single city in another country
	//GAPI-6893-Assign a User with  two Sites
	//GAPI-6890-Assign a User with  one region and one single country in another region
	//GAPI-6894-Assign a User with  one city Atlanta and one site in another city
	//GAPI-6892-Assign a User with  only one Site
	//GAPI-6889-Assign a user with all the sites present in two cities
	//GAPI-6888-Assign a user with all the sites present in a city
	//GAPI-6886-Assign a user with all the sites present in a country
	//GAPI-6887-Assign a user with all the sites present in two countries
	//GAPI-6885-Assign a user with all the sites present in two regions

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)

	public void sitepathAssociationRBAC(Map<String, String> data) throws IOException, InterruptedException {

		String body = "{\r\n    \"firstName\": \"" + data.get("firstName") + "\",\r\n " + "\"lastName\": \""
				+ data.get("lastName") + "\",\r\n " + "\"phone\": \"" + data.get("phone") + "\",\r\n  "
				+ "\"userAccounts\": [\r\n {\r\n " + " \"legalEntityKey\": \"" + data.get("legalEntityKey") + "\",\r\n "
				+ " \"associations\": [\r\n{\r\n " + "\"role\": \"" + data.get("role") + "\",\r\n"
				+ "\"assets\": [\r\n{\r\n" + "\"sitepath\": \"" + data.get("assetId1") + "\"\r\n},\r\n{\r\n"
				+ "\"sitepath\": \"" + data.get("assetId2") + "\"\r\n }\r\n ]\r\n}\r\n]\r\n}\r\n]\r\n}";

		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = userAccountAssociation(Tokenidp, body);
		System.out.println(response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		System.out.println(expectedStatusCode);
		int actualStatusCode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		System.out.println("===>" + response.asString());

	}

	// RBAC retrieve user account association
	//GAPI-6897-Assign a user with one asset id in one city and another Asset id in another city
	//GAPI-6898-Assign a user with one asset id in one country  and another Asset id in another country
	//GAPI-5751-U- Retrieve list of Associations of user by ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	
	public void retrieveUserAccountAssociationRBAC(Map<String, String> data) throws IOException, InterruptedException {

		System.out.println(data.get("TestCaseName"));
		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = idpdomain + path;
		System.out.println(apiURI);

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).get(apiURI).then().extract().response();

		System.out.println("Response is: " + response.asString());
		System.out.println("Status Message is: " + response.statusLine());
		logger.log(Status.INFO, response.statusLine());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualstatuscode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
		logger.log(Status.INFO, "Successfully extracted user Info");

	}

// RBAC get role details with valid ID
	//GAPI-5770-U - Get Roles For Customer Account
	//GAPI-5771-U -  Get Role Details by Role ID

	static String path2 = "/roles/";

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getallRolesinfoRBAC(Map<String, String> data) throws InterruptedException, IOException {
		System.out.println(data.get("TestCaseName"));
		logger = extent.createTest(data.get("TestCaseName"));
		String role = data.get("role");
		String apiURI = idpdomain + path2 + role;
		System.out.println(apiURI);

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).get(apiURI).then().extract().response();
		System.out.println("Response is: " + response.asString());
		System.out.println("Status Message is: " + response.statusLine());
		logger.log(Status.INFO, response.statusLine());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualstatuscode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
		logger.log(Status.INFO, "Successfully extracted Roles Info");

	}

	// RBAC get role details with valid ID
	//GAPI-5772-U -  Get Role Details by Invalid Role ID

	static String path4 = "/roles/";

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getallRolesinfoRBACinvalid(Map<String, String> data) throws InterruptedException, IOException {
		System.out.println(data.get("TestCaseName"));
		logger = extent.createTest(data.get("TestCaseName"));
		String role = data.get("role");
		String apiURI = idpdomain + path4 + role;
		System.out.println(apiURI);

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).get(apiURI).then().extract().response();
		System.out.println("Response is: " + response.asString());
		System.out.println("Status Message is: " + response.statusLine());
		logger.log(Status.INFO, response.statusLine());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualstatuscode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 400"));
		logger.log(Status.INFO, "role info not extracted");

	}

	// retrieve user details using ID
	//GAPI-5747-U- Retrieve user details by Invalid UserID
	//GAPI-5746-U- Retrieve user details by UserID

	static String path3 = "/users/";

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getUserdetailsRBAC(Map<String, String> data) throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String getfilters = data.get("filters");
		String URI = idpdomain + path3 + getfilters;
		Response response = details.retrieveUserdetails(Tokenidp, URI);
		System.out.println("Response is: " + response.getBody().asString());
	}

	// RBAC user creation without mandatory params
	//GAPI-5740-U- New User Creation without Email mandatory parametrs
	//GAPI-5732-U- New User Creation - Existing

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createUserwithoutmandatoryparametersRBAC(Map<String, String> data)
			throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String URI = idpdomain + path3;
		String payLoad = createmandUsersPayLoad(data);
		Response ftresponse = extractcreateUserinfo(Tokenidp, payLoad, URI);
		System.out.println("Response is: " + ftresponse.getBody().asString());
	}

	// RBAC user creation with mandatory params
	//GAPI-5734-U- New User Creation using 'Phone' optional parmaters
	//GAPI-5739-U- New User Creation without Last name mandatory parametrs

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createUserdetailsRBAC(Map<String, String> data) throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String URI = idpdomain + path3;
		String payLoad = createUsersPayLoad(data);
		Response frresponse = extractcreateUserinfo(Tokenidp, payLoad, URI);
		System.out.println("Response is: " + frresponse.getBody().asString());
	}

	// RBAC user creation without mandatory params

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createUserdetailswithoutmandparamRBAC(Map<String, String> data)
			throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String URI = idpdomain + path3;
		String payLoad = createUsersPayLoad(data);
		Response flresponse = extractcreateUserinfowithoutmandparam(Tokenidp, payLoad, URI);
		System.out.println("Response is: " + flresponse.getBody().asString());
	}

	// RBAC retrieve user name from comments
	//GAPI-7107-RBAC- Commented By -User name

	static String path5 = "/comments";

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void retrieveUsercommentsRemoteHandsTicketRBAC(Map<String, String> data) throws IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		Response response = createRemoteHandsTicket.createRemoteHandsRequest(Token, body);
		System.out.println("Response is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String WONumber = tc.getWONumberFromResponse(response);
//					String WONumber = data.get("filters");
		String apiURI = domain + path5 + "/" + WONumber;
		System.out.println("API URL by ID: " + apiURI);
		Response response1 = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "msirikonda@digitalrealty.com").get(apiURI).then()
				.extract().response();
		String cmntResponsebody = response1.getBody().asPrettyString();
		System.out.println("===>" + cmntResponsebody);

		logger.pass("commented by" + response1.getBody().asPrettyString());
		Assert.assertEquals(response1.getStatusCode(), 200);
		JSONArray jsonArray = new JSONArray(response1.asString());
		// Assert.assertTrue(cmresponse1.asString().contains("commentedBy"));
		boolean status = false;
		logger.log(Status.INFO, response1.statusLine());
		logger.log(Status.PASS, "Response Status Code is " + response1.statusCode());
		logger.log(Status.PASS, "commented by : " + WONumber);
	}

	// Re-usable method For user Account Association

	public static Response userAccountAssociation(String Token, String body) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(body).post(idpdomain + path).then().extract().response();

		return response;
	}

	// Re-usable method to create user Pay Load.
	public String createUsersPayLoad(Map<String, String> data) {

		Random rand = new Random();
		int rand_int1 = rand.nextInt(100000);
		Faker faker = new Faker();
		String samplemail = faker.lorem().characters(8, 16);
		String emailid = samplemail + rand_int1 + "@gmail.com";

		String userspayLoad = "{\r\n\"firstName\":\"" + data.get("firstName") + "\"," + "\r\n\"lastName\":\""
				+ data.get("lastName") + "\"," + "\r\n\"email\":\"" + emailid + "\"," + "\r\n\"phone\":\""
				+ data.get("phone") + "\"," + "\r\n\"position\":\"" + data.get("position") + "}";
		System.out.println("PayLoad is: " + userspayLoad);
		return userspayLoad;
	}

	// Re-usable method to create user without mandatory params Pay Load.

	public String createmandUsersPayLoad(Map<String, String> data) {

		String usersmandatorypayLoad = "{\r\n\"firstName\":\"" + data.get("firstName") + "\"," + "\r\n\"lastName\":\""
				+ data.get("lastName") + "\"," + "\r\n\"email\":\"" + data.get("email") + "\"," + "\r\n\"phone\":\""
				+ data.get("phone") + "\"," + "\r\n\"position\":\"" + data.get("position") + "}";
		System.out.println("PayLoad is: " + usersmandatorypayLoad);
		return usersmandatorypayLoad;

	}

	// reusable method to create user using reusable pay load body

	public static Response extractcreateUserinfo(String token, String userspayLoad, String URI) throws IOException {

		Response crresponse = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(userspayLoad).post(URI).then().extract().response();

		return crresponse;

	}
// reusable method to create user using reusable pay load body

	public static Response extractcreateUserinfowithoutmandparam(String token, String usersmandatorypayLoad, String URI)
			throws IOException {

		Response cr1response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(usersmandatorypayLoad).post(URI).then().extract().response();

		return cr1response;
	}

}