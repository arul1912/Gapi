package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreationOfUser extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/users";
	static String date = tc.getCurrentDateAndTime();


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void CreateNewUser(Map<String, String> data) throws InterruptedException, IOException {
			
		String body =tc.getRequestBodyWithDynamicData(data);
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createNewUser(Tokenidp,body);
		logger.log(Status.INFO, response.asString());
		String expectedStatusCode1 = data.get("expectedStatusCode");
		String expectedStatusCode=expectedStatusCode1.replace(".0","");
		System.out.println(expectedStatusCode);
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());	
		if (response.getStatusCode()==201) {
			String userId = tc.getUserIdFromResponse(response);
			log.info("The created userId is " + userId);
			logger.log(Status.PASS, "Created userId is  " + userId);
			logger.log(Status.PASS, "Successfully Created New User");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "UserId_" + userId);
			}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create New User");
			//System.out.println(response.asString());
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}		
		}
	// Re-usable method

		public static Response createNewUser(String Token, String body) throws IOException {

			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Tokenidp)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.body(body)
					.post(domain + path)
					.then().extract().response();

			return response;
		}


}
