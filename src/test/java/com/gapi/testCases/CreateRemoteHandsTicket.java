
package com.gapi.testCases;

import org.json.JSONObject;

/** CreateRemoteHandRequest class is used to create REMOTE HANDS TICKET with the various sets of input data,
 * Validate the create REMOTE HANDS TICKET API response by using GET call 
 * and
 * verify the created REMOTE HANDS request information in service now application 
 *  @author rkasi
 *  @version 17
 *  @since 2022
 **/

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreateRemoteHandsTicket extends BaseClass {
	public static String path = "/remotehands-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();

	// create Remote Hands Ticket with all possible combinations
	//GAPI-3977-RH- Submit RH Request with Request Type Planned Work - Category-Customer premise cabling
	//GAPI-3963-RH- Submit RH Request with Optional parameters - Location
	//GAPI-3965-RH- Submit RH Request with Optional parameters - Customer Reference
	//GAPI-3966-RH- Submit RH Request with Optional parameters - reference Ticket
	//GAPI-3974-RH- Submit RH Request with Request Type Urgent Work - Category-'Other'
	//GAPI-3973-RH- Submit RH Request with Request Type Urgent Work - Category-PowerCycle (Reboot)
	//GAPI-3975-RH- Submit RH Request with Request Type Planned Work - category-Existing cross connect or connectivity testing
	//GAPI-3981-RH- Submit RH Request with Request Type Planned Work - Category-Equipment installation
	//GAPI-3976-RH- Submit RH Request with Request Type Planned Work - Category-Dedicated escort
	//GAPI-3962-RH- Submit RH Request with Mandatory parameters - (Site, title, request type, category and Description)
	//GAPI-3969-RH- Submit a RH Request - with invalid Notification Recipient email format
	//GAPI-3980-RH- Submit RH Request with Request Type Planned Work - Category-Equipment de-installation
	//GAPI-3983-RH- Submit RH Request with Request Type Urgent Work but WITHOUT A Category  (Verify Category Defaults to General Remote Hands Service)
	//GAPI-3984-RH- Submit RH Request with Request Type Planned Work but WITHOUT A Category  (Verify Category Defaults to General Remote Hands Service)
	//GAPI-3979-RH- Submit RH Request with Request Type Planned Work - Category-Tape swaps
	//GAPI-3972-RH- Submit RH Request with Request Type Urgent Work - Category-KVM (keyboard, video, mouse) assistance
	//GAPI-3978-RH- Submit RH Request with Request Type Planned Work - Category-Auditing
	//GAPI-3982-RH- Submit RH Request with Request Type Planned Work - Category-'Other'
	//GAPI-3967-RH- Submit RH Request with Optional parameters -  1 Notification Recipient
	//GAPI-3970-RH- Submit RH Request with Request Type Urgent Work - category-Existing cross connect or connectivity testing
	//GAPI-3971-RH- Submit RH Request with Request Type Urgent Work - Category-Equipment troubleshoot or replacement


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateRemoteHandsTicket(Map<String, String> data) throws IOException, InterruptedException {
		
		testData=data;
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsRequest(Token,body);
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		String requestType = data.get("requestType");
		String category = data.get("category");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode()==201) {
			String WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Remote Hands Ticket");
			logger.log(Status.PASS, "Created Remote Hands Ticket Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "RemoteHandsId_" + WONumber);
			Thread.sleep(5000);
			tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
			GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
			String getALLAPI = domain+"/remotehands-tickets?requestType="+requestType+"&category="+category;
			System.out.println(getALLAPI);
			obj.verifyGetAllWithAllRequestTypesAndCategoryValues(WONumber,getALLAPI);
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Remote Hands request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		
	}
	
	// verify remote hands end to end flow
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createRemoteHandsTicketAndVerifyEndToEndFlow(Map<String, String> data) throws IOException, InterruptedException{
		
		testData=data;
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsRequest(Token,body);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		log.info("Created Remote Hands Ticket");
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String WONumber = tc.getWONumberFromResponse(response);
		log.info("The WO Number Created is " + WONumber);
		logger.log(Status.PASS, "Created Remote Hands request Id  " + WONumber);
		tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "RemoteHandsId_" + WONumber);
		tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
		tc.addCommentToTheRequestAndVerify(WONumber,path);
		tc.addAttachmentToTheRequestAndVerify(WONumber,path);
		
	}
	
	//Create RH Ticket with Invalid user RBAC
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateRHTicketForInvalidUser(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		JSONObject remoteHandsdetails = new JSONObject();
		remoteHandsdetails.put("site", data.get("site"));
		remoteHandsdetails.put("location", data.get("locations"));
		remoteHandsdetails.put("title", data.get("title"));
		remoteHandsdetails.put("requestType", data.get("requestType"));
		remoteHandsdetails.put("category", data.get("category"));
		
		System.out.println("Body is------ :");
		System.out.println(remoteHandsdetails.toString());
		
		String apiURI = domain + path;
		System.out.println(apiURI);
		String jsonbody = remoteHandsdetails.toString();
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + "abcde")
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(jsonbody).post(domain + path).then().extract().response();
		
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		Assert.assertEquals(401, response.getStatusCode());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		
		if (response.getStatusCode()==200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created RH Ticket");
			logger.log(Status.PASS, "Created RH Ticket  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "rh_" + WONumber);
			//tc.retrieveFAGETResponseAndRequestParameters(WONumber, jsonbody, "service-tickets?id=");			
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create RH Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
		
}
}
	

	// pavan on 08-03-2023 create RH ticket with Invalid and Multiple notification Recipients 
	//GAPI-3969-RH- Submit a RH Request - with invalid Notification Recipient email format
	//GAPI-3968-RH - Submit a RH Request - with multiple Notification Recipients (comma separated email addresses)

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createRemoteHandsTicketwithNotificationRecipient(Map<String, String> data) throws IOException, InterruptedException {
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsRequest(Token,body);
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		String requestType = data.get("requestType");
		String category = data.get("category");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode()==201) {
			String WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Remote Hands Ticket");
			logger.log(Status.PASS, "Created Remote Hands Ticket Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "RemoteHandsId_" + WONumber);
			tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
			GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
			String getALLAPI = domain+"/remotehands-tickets?requestType="+requestType+"&category="+category;
			System.out.println(getALLAPI);
			obj.verifyGetAllWithAllRequestTypesAndCategoryValues(WONumber,getALLAPI);
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Remote Hands request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
	}

	

	
	// Re-usable methods for Create Remote Hands Ticket
	// *************************************************************************************

	public static Response createRemoteHandsRequest(String Token, String body) throws IOException {
		System.out.println("Body is : " + body);
		
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
				.body(body)
				.post(domain + path)
				.then().extract().response();
		
		return response;
	}

}
