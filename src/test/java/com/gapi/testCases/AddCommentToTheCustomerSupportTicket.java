package com.gapi.testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AddCommentToTheCustomerSupportTicket extends BaseClass {
	

       // SHIPMENTS COMMENT TESTCASES
	//********************************************************************
		
		public String addCommentsToTheExistingShipmentsTicket(String Id) {

			logger.createNode("addCommentsToTheExistingShipmentsTicket");
			Faker faker = new Faker();
			String comment = faker.lorem().characters(8, 16);

			String body = "{\r\n" + "  \"comment\": \"" + comment + "\"\r\n" + "}";

			String apiURI = domain + "/v1/shipments/" + Id;
			Response response = RestAssured.given()
					.header("Authorization", Token)
					.header("Content-Type", "application/json")
					.body(body)
					.patch(apiURI)
					.then().extract().response();

			String responseBody = response.getBody().asString();
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("OK"));

			log.info("Successfully Posted comment to the Existing Shipments Ticket ");
			logger.log(Status.INFO, "Posted comment to the Existing Shipments Ticket");
			logger.log(Status.INFO, "Status code and Status Message is" + response.getStatusLine());

			return responseBody;
		}

}
