package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.response.Response;


public class AddCommentToTheShipmentsTicket extends BaseClass{
	
	CreateShipmentsTicket createShipmentsTicket = new CreateShipmentsTicket();
	AddCommentsToTheRemoteHandsTicket shippingticketcomments =new AddCommentsToTheRemoteHandsTicket();
	static TestUtilities tc = new TestUtilities();
	static String path = "/comments";
	
	     // CUSTOMER SUPPORT COMMENT TESTCASES
		//********************************************************************
	//GAPI-3867	CS - Add Comment to Existing CS Tickets in Status as New (with mandatory fields -  request ID and comment)

	    
		public String addCommentsToTheExistingCustomerSupportTicket(String Id) {

			logger.createNode("addCommentsToTheExistingCustomerSupportTicket");
			Faker faker = new Faker();
			String comment = faker.lorem().characters(8, 16);

			String body = "{\r\n"
					+ "    \"comment\": \""+comment+"\"\r\n"
					+ "}";

			String apiURI = domain + "/v1/customer-service/" + Id;
			Response response = RestAssured.given()
					.header("Authorization", Token)
					.header("Content-Type", "application/json")
					.body(body)
					.patch(apiURI)
					.then().extract().response();

			String responseBody = response.getBody().asString();
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("OK"));
			AddCommentsToTheRemoteHandsTicket obj = new AddCommentsToTheRemoteHandsTicket();
			String expectedComment =obj.getCommentInfoFromResponse(responseBody);
	        Assert.assertEquals(comment, expectedComment);

			log.info("Successfully Posted comment to the Existing Customer Support Ticket ");
			logger.log(Status.INFO, "Posted comment to the Existing Customer Support Ticket");
			logger.log(Status.INFO, "Status code and Status Message is" + response.getStatusLine());

			return responseBody;
		}
		
		//arul changes on 06-03-2023 without the mandatory fields Shipping ticket Add comment size max 4000 characters
		//GAPI-4059-SR- Add Comment to Existing S&R Ticket where comment size max 4000 characters

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyAddingsizeMaxSHTicketForWithmandatoryfieldsRequired(Map<String, String> data) throws IOException, InterruptedException {
			  String path = "/Comments";
			logger = extent.createTest(data.get("TestCaseName"));
			  String body = "{\r\n    \"requestId\": \""+data.get("Request_Id")+",\r\n  \"comment\": \""+data.get("comments")+"\"\r\n";
		     System.out.println("Body is: " + body);
			System.out.println("Body is------ :");
			String apiURI = domain + path;
			System.out.println(apiURI);
			Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
			  System.out.println("Response is: " + response.asString());
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
			
			  System.out.println("Response is: " + response.asString());
		        System.out.println("Status Message is: " + response.statusLine());
		        logger.log(Status.INFO, response.statusLine());
		        Assert.assertEquals(response.getStatusCode(), 200);
		        Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
		        log.info("Successfully extracted all get Reports Info ");
		        logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());
		        logger.log(Status.INFO, "Successfully extracted getreports Info");
				
		}
		
		// Re-Usable Method to Add Comment to the Existing Valid Shipping Ticket with max 4000 size length(10-03)
		
		//GAPI-4060-CS- Add Comment to Existing S&R Ticket where comment size greater than 4000 charecters

			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void AddUsercommentsShipmentsTicket(Map<String, String> data) throws IOException {
			
			String body =tc.getRequestBody(data);
			System.out.println(data.get("TestCaseName")+ "\n");
			System.out.println("Request Payload data is  \n" + body);
			logger = extent.createTest(data.get("TestCaseName"));
			Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			System.out.println("Request is: "+response.asString());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			String WONumber = tc.getWONumberFromResponse(response);
			String apicommentsURI = domain + path;
			System.out.println("API URL by ID: " + apicommentsURI);
			Faker faker = new Faker();
			Random rand = new Random();
			int rand_int1 = rand.nextInt(100000);
			String comments = faker.lorem().characters(4000);
			String samplecomment = comments + rand_int1;
			String commentsbody = "{\r\n    \"requestId\": \""+WONumber+"\",\r\n\r\n"
					+ "\"comment\": \""+samplecomment+"\"\r\n}";
			System.out.println("Cancel PayLoad is : " + commentsbody);
			log.info("Created pay load with comments added");
			logger.log(Status.PASS, "Created pay load with comments added");
			
			Response responsecomments = commentShipmentsTicket(Token, commentsbody, apicommentsURI);
			log.info("Commented the Shipments Ticket");
			logger.log(Status.PASS, "commented the Shipping and receiving ticket");
			logger.log(Status.PASS,	"Response Status Code and Status Message is after editing the ticket" + responsecomments.statusLine());
			System.out.println("Response is : " + responsecomments.asString());
			
			
			}
			// Comment the Shipments ticket
				public Response commentShipmentsTicket(String Token, String commentsbody, String apicommentsURI) throws IOException {

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
