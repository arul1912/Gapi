package com.gapi.testCases;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AddAttachmentToTheShipmentsTicket extends BaseClass {
	
	// SHIPMENTS ATTACHMENT TESTCASES
	//*****************************************************
		
		public String uploadAttachmentToTheExistingShipmentsTicket(String Id, String Token) {

			logger.createNode("uploadAttachmentToTheExistingShipmentsTicket");
			File testUploadFile = new File(
					System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Attachment.txt");
			String apiURI = domain + "/v1/customer-service/" + Id;
			Response response = RestAssured.given()
					.header("Authorization", Token)
					.header("Content-Type", "multipart/form-data")
					.multiPart("file", testUploadFile)
					.post(apiURI).then()
					.extract().response();

			String responseBody = response.getBody().asString();
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(responseBody.contains("successfully uploaded"));

			log.info("Successfully Posted attachment to the Existing Shipments Ticket");
			logger.log(Status.INFO, "Posted Attachment to the Existing Shipments Ticket");

			return responseBody;

		}


}
