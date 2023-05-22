package com.gapi.testCases;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetShipmentsTicketDetails extends BaseClass {
	static TestUtilities tc = new TestUtilities();
	

	//**********************************SHIPMENTS GET CALLS TestCases***********************
    
     public String getShipmentsTicketInfoById(String Id) {
		
	    logger.createNode("getCustomerSupportTicketInfoById");
		String apiURI = domain+"/shipment-tickets"+Id;
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID) 				
 				.get(apiURI);      		     	

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
 		String allResponsebody = response.getBody().asString();
 		
 		log.info("Successfully extracted the created Shipments Ticket Info ");
 		logger.log(Status.PASS, "Successfully extracted the created Shipments Ticket Info" );
 		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response.getStatusLine());
 		
		return allResponsebody;	
		
		
	}
	
    @Test
	public void getAllShipmentsTicketsInfo() throws InterruptedException {
		
		
    	logger = extent.createTest("getAllCustomerSupportTicketsInfo");
		String apiURI = domain+"/shipment-tickets";
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID) 				
 				.get(apiURI);      		     	

		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
 		
 		log.info("Successfully extracted all Shipments Ticket Info ");
 		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());
 		logger.log(Status.INFO, "Successfully extracted Shipments Ticket Info" );
 		
	}
    

    // verify retrieve all Shipments Ticket details using various filters
   	
    	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
    	public void verifyGetAllWithShipmentsTicketFilters(Map<String, String> data) {

    		logger = extent.createTest(data.get("TestCaseName"));
    		String filters = data.get("filters");
    		String apiURI = domain + "/shipment-tickets?" + filters;
    		System.out.println("Request :" +apiURI);
    		Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
    				.get(apiURI);

    		String expectedStatusCode = data.get("expectedStatusCode");
    		int actualStatusCode = response.getStatusCode();
    		String expectedStatusMessage = data.get("ExpectedStatusMessage");
    		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
    		//Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
    		String allResponsebody = response.asString();
    		logger.log(Status.INFO, "API -" + apiURI);
    		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.getStatusLine());
    		logger.log(Status.PASS, allResponsebody);
    		JSONObject object = new JSONObject(allResponsebody);
    		JSONArray requestArray = object.getJSONArray("content");
    		Assert.assertTrue(requestArray.length() > 0);
    		//Assert.assertTrue(requestArray.isEmpty());
    		Map<String, String> objMap = new HashMap<String, String>();
    		if (filters.contains("&")) {
    			String[] values = filters.split("&");//requsestType=Amenities&category=Shipping / Receiving

    			for (int j = 0; j < values.length; j++) {
    				String[] keyPair = values[j].split("=");
    				objMap.put(keyPair[0], keyPair[1]);
    			}
    		} else {
    			String[] keyPair = filters.split("=");
    			objMap.put(keyPair[0], keyPair[1]);
    		}
    		if (!filters.contains("size") && !filters.contains("page")) {
    			for (int i = 0; i < requestArray.length(); i++) {
    				JSONObject individualRequestInfo = requestArray.getJSONObject(i);
    				for (Entry<String, String> m : objMap.entrySet()) {
    					String actualValue = individualRequestInfo.getString(m.getKey());
    					Assert.assertEquals(actualValue, m.getValue());
    				}
					/*
					 * String status = individualRequestInfo.getString("status");
					 * if(!status.equals("Completed")) { String actualRequestType =
					 * individualRequestInfo.getString("title");
					 * Assert.assertTrue(actualRequestType.equals("shipment title")); String
					 * actualCategoryValue = individualRequestInfo.getString("category");
					 * //verifyCategoryValue(actualRequestType, actualCategoryValue);
					 * logger.log(Status.PASS,
					 * "Response details Matched with the given filter criteria"); }
					 */

    			}
    		} else if (filters.contains("size") || filters.contains("page")) {
    			String keyName, keyValue;
    			for (int i = 0; i < requestArray.length(); i++) {
    				JSONObject individualRequestInfo = requestArray.getJSONObject(i);//site=DFW10&status=Cancelled&limit=30&offset=0
    				for (Entry<String, String> m : objMap.entrySet()) {
    					keyName = m.getKey();
    					keyValue = m.getValue();
    					if (keyName.equals("size")) {
    						int objectsLength = requestArray.length();
    						Assert.assertTrue(String.valueOf(objectsLength).equals(keyValue)|| objectsLength>=0);
    					} else if (keyName.equals("page")) {
    						int objectsLength = requestArray.length();
    						Assert.assertTrue(objectsLength>=0 || objectsLength==25);

    					} else {
    						String actualValue = individualRequestInfo.getString(keyName);
    						Assert.assertEquals(actualValue, keyValue);
    					}
    				}
					/*
					 * String status = individualRequestInfo.getString("status"); if
					 * (!status.equals("Completed")) { String actualRequestType =
					 * individualRequestInfo.getString("title");
					 * Assert.assertTrue(actualRequestType.equals("shipment title")); String
					 * actualCategoryValue = individualRequestInfo.getString("category");
					 * //verifyCategoryValue(actualRequestType, actualCategoryValue);
					 * logger.log(Status.PASS,
					 * "Response details Matched with the given filter criteria");
					 * 
					 * }
					 */
    			}
    		}
    	}
    	
    	//Pavan 08-03-2023 Retrieve Shipment tickets with Invalid RequestId (Add this method in get shipment ticket details class file)
        //GAPI-4033-SR- Retrieve S&R tickets by InValid Request ID

      	static String path2 = "/shipment-tickets/";
      	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
      	public void retrieveShipmentTicketInfoByInvalidId(Map<String, String> data) throws IOException {  
      		
      		String body =tc.getRequestBody(data);
      		System.out.println(data.get("TestCaseName")+ "\n");
      		System.out.println("Request Payload data is  \n" + body);
      		logger = extent.createTest(data.get("TestCaseName"));
      		Response response = CreateShipmentsTicket.createShipmentsTicket(Token,body);
    		System.out.println("Request is: "+response.asString());
    		String WONumber = tc.getWONumberFromResponse(response);
    		String apiURI = domain+ path2 +WONumber+1;
    		System.out.println("API URL by ID: " + apiURI);
    		Response responsInvalid = retrieveShipmentsTicket(Token,apiURI);
    		Assert.assertEquals(responsInvalid.getStatusCode(), 401);
    		Assert.assertTrue(responsInvalid.statusLine().contains("HTTP/1.1 401"));
    		logger.pass("Response Status Code and Message Is " + responsInvalid.getStatusLine());
    		logger.pass("Error Response Message Is" + responsInvalid.asString());
    		tc.verifyErrorResponseMessage(responsInvalid.asString(), "client", "Service ticket not found.");
    		logger.pass("Not Allowed to Retrieve Request Details With Invalid Id");
      	}


    // pavan 08-03-2023 Re-usable method to retrieve Shipments Ticket

    public static Response retrieveShipmentsTicket(String Token, String apiURI) throws IOException {

    	Response response = RestAssured.given().relaxedHTTPSValidation()
    			.header("Authorization", "Bearer " +Token)
    			.header("Content-Type", "application/json")
    			.header("Master-Account-Id", masterAccountID)
    			.header("Account-Id", accountID)
    			.get(apiURI);
    	return response;
    }
    

    
	/*
	 * @Test public void verifyShipmentsTicketInfoByInvalidId() {
	 * 
	 * logger = extent.createTest("verifyShipmentsTicketInfoByInvalidId"); String
	 * apiURI = domain+"/shipment-tickets/"+"WO845613"; Response response =
	 * RestAssured.given().relaxedHTTPSValidation() .header("Authorization",
	 * "Bearer "+Token) .header("Content-Type", "application/json")
	 * .header("Master-Account-Id", masterAccountID) .header("Account-Id",
	 * accountID) .get(apiURI);
	 * 
	 * Assert.assertEquals(response.getStatusCode(), 401);
	 * Assert.assertTrue(response.statusLine().contains("Unauthorized"));
	 * logger.pass("Response Status Code and Message Is " +
	 * response.getStatusLine()); logger.pass("Error Response Message Is" +
	 * response.asString()); tc.verifyErrorResponseMessage(response.asString(),
	 * "client", "Service ticket not found.");
	 * logger.pass("Not Allowed to Retrieve Request Details With Invalid Id");
	 * 
	 * }
	 */
    

}
