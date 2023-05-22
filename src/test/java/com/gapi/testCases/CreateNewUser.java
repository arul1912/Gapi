package com.gapi.testCases;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.json.XML;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aventstack.extentreports.Status;

import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreateNewUser extends BaseClass{
	
	public static String path = "/users";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	
	//GAPI-5735-U- New User Creation using 'Position' optional parmaters
	//GAPI-5733-U- Create New user using mandatory parameters
	//GAPI-5738-U- New User Creation without First name mandatory parametrs
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateNewUser(Map<String, String> data) throws IOException, InterruptedException, ParserConfigurationException, SAXException{
		
		String body =tc.getRequestBodyWithDynamicData(data);
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		//String updateResponse1 = updateUserDetails(Token,"test123");
		Response response = createNewUser(Tokenidp,body);
		System.out.println("Response is: "+response.asString());
		JSONObject json = XML.toJSONObject(response.asString());
		String abc=response.asString();
		System.out.println("Response is: "+abc);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src= new InputSource();
		src.setCharacterStream(new StringReader(abc));
		
		//Document doc=(Document) builder.parse(src);
		
		//System.out.println("Positoon is: "+xyz);
		
		
		System.out.println("Response is: "+json);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		log.info("Create new users");
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode()==201) {
			String userId = tc.getUserIdFromResponse(response);
		//	String userId = tc.getIdFromResponse(response);
			log.info("The created userId is " + userId);
			logger.log(Status.PASS, "Created userId is  " + userId);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "UserId_" + userId);
			/*Below function retrieves the response of Get Call and 
			compares those parameters are present in the request of Post*/
			//tc.retrieveUserGETCallResponseAndCompareWithRequestParameters(userId, body, path);
			//String updateResponse = updateUserDetails(Token,userId);
			//tc.retrieveUserGETCallResponseAndCompareWithRequestParameters(userId, updateResponse, path);
			
		}
		else {
			
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create New User request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			//tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		
	}
	
	// Re-usable methods for Create User
	// *************************************************************************************

	public static Response createNewUser(String Token, String body) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Tokenidp)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.body(body)
				.post(idpdomain + path)
				.then().extract().response();
		
		return response;
	}

	public static String updateUserDetails(String Token, String userId) throws IOException {
		Faker faker = new Faker();
		String firstName1 = faker.lorem().characters(8, 16);
		String lastName1 = faker.lorem().characters(8, 16);
		String email1=faker.bothify("????????##@digitalrealty.com");
		String body = "{\r\n"
				+ "  \"firstName\": \""+firstName1+"\",\r\n"
				+ "  \"lastName\": \""+lastName1+"\",\r\n"
				+ "  \"email\": \"jdoe@digitalrealy.com\",\r\n"
				+ "  \"phone\": \"999-999-9999\",\r\n"
				+ "  \"position\": \"Director of Marketing\"\r\n"
				+ "}";
		System.out.println("The body is++++++++ "+body);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Tokenidp)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.body(body)
				.put(idpdomain + path+"/"+userId)
				.then().extract().response();
		
		String responseString = response.asString();
		return responseString;
	}
	 //GAPI-8920-U - Verify weather the user details like first name, Last name & Mobile numbers coming after User Role & account Association
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateNewUserDetails(Map<String, String> data) throws IOException, InterruptedException, ParserConfigurationException, SAXException{
		
		logger = extent.createTest(data.get("TestCaseName"));
		String body =tc.getRequestBodyWithDynamicData(data);
		System.out.println("Request Payload data is  \n" + body);
		JSONObject json = new JSONObject(body);
		String firstName = json.getString("firstName");
		String lastName = json.getString("lastName");
		String phone = json.getString("phone");

		Response response = createNewUser(Tokenidp,body);
		System.out.println("Response is: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String userId = tc.getUserIdFromResponse(response);
//		String userId = tc.getIdFromResponse(response);
		System.out.println("userId is :"+userId);
		String apiuri = idpdomain + path+"/"+userId+"/assignments";
		String assignbody = "{\r\n    \"firstName\": \"" + firstName + "\",\r\n " + "\"lastName\": \""
				+ lastName + "\",\r\n " + "\"phone\": \"" + phone + "\",\r\n  "
				+ "\"userAccounts\": [\r\n {\r\n " + " \"legalEntityKey\": \"" + data.get("legalEntityKey") + "\",\r\n "
				+ " \"associations\": [\r\n{\r\n " + "\"role\": \"" + data.get("role") + "\",\r\n"
				+ "\"assets\": [\r\n{\r\n" + "\"sitepath\": \"" + data.get("site") + "\"\r\n}\r\n ]\r\n}\r\n]\r\n}\r\n]\r\n}";
		System.out.println(assignbody);
		Response getresponse = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Tokenidp)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.body(assignbody)
				.post(apiuri)
				.then().extract().response();
		System.out.println(getresponse.asString());
		Assert.assertEquals(getresponse.getStatusCode(), 200);
		JSONObject getjson = new JSONObject(getresponse.asString());
		String getfirstName = getjson.getString("firstName");
		String getlastName = getjson.getString("lastName");
		String getphone = getjson.getString("phone");
		Assert.assertEquals(firstName, getfirstName);
		Assert.assertEquals(lastName, getlastName);
		Assert.assertEquals(phone, getphone);
		logger.log(Status.INFO, "Successfully extracted user Info");
		System.out.println("firstName ,lastName and phone are extracted and verified from Response");
		logger.log(Status.PASS, "firstName ,lastName and phone are extracted and verified from Response");
	
	}
	// Reusable Method to extract User Details
	public static Response retrieveUserdetails(String Token, String URI) throws IOException {
		Response allresponse = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + Tokenidp).header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID).get(URI).then()
				.extract().response();
		return allresponse;
	}
}
