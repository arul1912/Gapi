package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AddCommentsToTheRemoteHandsTicket extends BaseClass {

	static String path = "/comments";
	CreateRemoteHandsTicket createRemoteHandsTicket = new CreateRemoteHandsTicket();
	static TestUtilities tc = new TestUtilities();


	
	// REMOTE HANDS ADD COMMENT TESTCASES
	//***********************************************************************
	
	public String addCommentsToTheExistingRemoteHandsTicket(String Id) throws InterruptedException {

		logger.createNode("addCommentsToTheExistingRemoteHandsTicket");
		Faker faker = new Faker();
		String comment = faker.lorem().characters(8, 16);
		String body = "{\r\n"
				+ "    \"comment\": \""+comment+"\"\r\n"
				+ "}";
		
		String apiURI = domain + path + Id;

		Response response = RestAssured.given()
				.header("Authorization", Token)
				.header("Content-Type", "application/json")
				.body(body)
				.patch(apiURI)
				.then().extract().response();

		String responseBody = response.getBody().asString();
        System.out.println(responseBody);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		String expectedComment = getCommentInfoFromResponse(responseBody);
        Assert.assertEquals(comment, expectedComment);
		log.info("Successfully Posted comment to the Existing Remote Hands Ticket ");
		logger.log(Status.INFO, "Posted comment to the Existing Remote Hands Ticket");
		logger.log(Status.INFO, "Status code and Status Message is" + response.getStatusLine());

		return responseBody;
	}
	
	// Re-Usable Method to Add Comment to the Existing Valid Ticket
	//*******************************************************************************
	@Test
	public String addCommentsToTheExistingTicket(String Id,String pathParam) throws InterruptedException {
		//Id="WO7617810";
        logger.createNode("addCommentsToTheExistingTicket");
		Faker faker = new Faker();
		String comment = faker.lorem().characters(8, 16);
		String body = "{\r\n"
				+ "    \"requestId\": \""+Id+"\",\r\n"
				
				+ "    \"comment\": \""+comment+"\"\r\n"
				+ "}";

		System.out.println("Request Body is:"+body);
		String apiURI = domain + path;
		System.out.println("Request URL is: "+apiURI);

		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
				.body(body)
				.post(apiURI)
				.then().extract().response();

		String responseBody = response.getBody().asString();
        System.out.println(responseBody);
		Assert.assertEquals(response.getStatusCode(), 200);
		System.out.println("Status message is: "+response.statusLine());
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
		

		String expectedComment = getCommentInfoFromResponse(responseBody);
        Assert.assertEquals(comment, expectedComment);
        JSONObject object = new JSONObject(responseBody);
        String commentByValue = object.getString("commentedBy");
        String commentOnValue = object.getString("commentedOn");
        Assert.assertTrue(!commentByValue.isEmpty());
        Assert.assertTrue(!commentOnValue.isEmpty());
		log.info("Successfully Posted comment to the Existing Ticket ");
		logger.log(Status.INFO, "Posted comment to the Existing Ticket");
		logger.log(Status.INFO, "Status code and Status Message is" + response.getStatusLine());

		return responseBody;
	}
	@Test
	public String getCommentInfoFromResponse(String responseBody) {
		
		JSONObject object = new JSONObject(responseBody);
	   // JSONArray  array = object.getJSONArray("comments");
	    String expectedComment = object.getString("comment");
		return expectedComment;
		
	}
	
	//Pavan changes on 06-03-2023 Re-Usable Method to Add Comment to the Existing Valid RemoteHands Ticket
		//GAPI-4079-RH - Add Comment to Existing RH Tickets in Status -New  - Comment greater than Max size of 1024
		//GAPI-4077-RH - Add Comment to Existing RH Tickets in Status -New (up to max size 1024)
		
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void AddUsercommentsRemoteHandsTicket(Map<String, String> data) throws IOException {
			
			String body =tc.getRequestBody(data);
			System.out.println(data.get("TestCaseName")+ "\n");
			System.out.println("Request Payload data is  \n" + body);
			logger = extent.createTest(data.get("TestCaseName"));
			Response response = createRemoteHandsTicket.createRemoteHandsRequest(Token,body);
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			String WONumber = tc.getWONumberFromResponse(response);
			String apicommentsURI = domain + path;
			System.out.println("API URL by ID: " + apicommentsURI);
			Faker faker = new Faker();
			Random rand = new Random();
			String lengths = data.get("filters");
			int length=Integer.parseInt(lengths);  
			int rand_int1 = rand.nextInt(1000000000);
			String comments = faker.lorem().characters(length);
			String samplecomment = comments + rand_int1;
			System.out.println(samplecomment.length());
			String commentsbody = "{\r\n    \"requestId\": \""+WONumber+"\",\r\n\r\n"
					+ "\"comment\": \""+samplecomment+"\"\r\n}";
			System.out.println("Cancel PayLoad is : " + commentsbody);
			log.info("Created pay load with comments added");
			logger.log(Status.PASS, "Created pay load with comments added");

			Response responsecmnts = commentRemoteHandsTicket(Token, commentsbody, apicommentsURI);
			log.info("Commented the Remote Hands ticket");
			logger.log(Status.PASS, "commented the Remote Hands ticket");
			logger.log(Status.PASS,	"Response Status Code and Status Message is after editing the ticket" + responsecmnts.statusLine());
			System.out.println("Response is : " + responsecmnts.asString());
					
			
		}
		
		//  Add Comment to the Existing Valid RemoteHands Ticket without RequestID and Comment(10-03)
		
			//GAPI-4080 RH - Add Comment to Existing RH Tickets without using both Mandatory fields RequestID and Comment
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void AddUsercommentsRemoteHandsTicketwithoutMandatoryParameters(Map<String, String> data) throws IOException {
			
			String body =tc.getRequestBody(data);
			System.out.println(data.get("TestCaseName")+ "\n");
			System.out.println("Request Payload data is  \n" + body);
			logger = extent.createTest(data.get("TestCaseName"));
			Response response = createRemoteHandsTicket.createRemoteHandsRequest(Token,body);
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			String apicommentsURI = domain + path;
			System.out.println("API URL by ID: " + apicommentsURI);
			String commentsbody =  "{\r\n    \"requestId\": \""+data.get("filters")+",\r\n  \"comment\": \""+data.get("comments")+"\"\r\n";
			System.out.println("Cancel PayLoad is : " + commentsbody);
			log.info("Created pay load with comments added");
			logger.log(Status.PASS, "Created pay load with comments added");
			
			Response responsecmnts = commentRemoteHandsTicket(Token, commentsbody, apicommentsURI);
			 System.out.println("Response is: " + responsecmnts.asString());
		        System.out.println("Status Message is: " + responsecmnts.statusLine());
		        logger.log(Status.INFO, responsecmnts.statusLine());
		        Assert.assertEquals(responsecmnts.getStatusCode(), 400);
		        Assert.assertTrue(responsecmnts.statusLine().contains("HTTP/1.1 400"));
			log.info("cannot comment the Remote Hands ticket");
			logger.log(Status.PASS, "cannot comment the Remote Hands ticket");
			logger.log(Status.PASS,	"Response Status Code and Status Message is after editing the ticket" + responsecmnts.statusLine());
			
			}
					
			
		// Comment the RemoteHands ticket
			public Response commentRemoteHandsTicket(String Token, String commentsbody, String apicommentsURI) throws IOException {

				Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
				.body(commentsbody)
				.post(apicommentsURI)
				.then().extract().response();

				return response;
			}
	}

