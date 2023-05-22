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

public class GetServiceTicketDetails extends BaseClass {

	String path = "/service-tickets";
	static TestUtilities tc = new TestUtilities();
	

	public Response getAllServiceTicketDetailsinfo() throws InterruptedException, IOException {
		
		String apiURI = domain + path;
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.get(apiURI);

		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		log.info("Successfully extracted all Service Ticket Info ");
		logger.log(Status.INFO, "Successfully extracted all Service Ticket Info");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
		return response;
	}

//verify retrieve all service ticket request details using various filters
	//GAPI-3768-ST - Retrieve all Tickets with  Request Type of  Facilities Request   (Ticket Type Customer Support)
	//GAPI-3767-ST - Retrieve all Tickets with Request Type of Accounts Receivable  (Ticket Type Customer Support)
	//GAPI-3766-ST - Retrieve all Tickets with  Request Type of Account Management   (Ticket Type Customer Support)
	//GAPI-3778-ST - Retrieve all Tickets -Verify filter - Size(Min 25 to Max 1000)
	//GAPI-3758-ST - Retrieve all tickets for a location/site
	//GAPI-3772-ST - Retrieve all Tickets with Request Type of Accounts Receivable, Category of General Billing Inquiry (Ticket Type Customer Support)
	//GAPI-3916-ST - Retrieve all Tickets with  Request Type of Planned Work and category Cabinet Power Strip Energizing    (Ticket Type Remote Hands)
	//GAPI-3763-ST - Retrieve all Tickets in Cancellation Requested Status
	//GAPI-3776-ST - Retrieve all Tickets with  Request Type of  Facilities Request, Category of Fire/Safety  (Ticket Type Customer Support)
	//GAPI-3762-ST - Retrieve all Tickets in Cancelled Status
	//GAPI-3919-ST - Retrieve all Tickets with  Request Type of Planned Work and category Dedicated escort (Ticket Type Remote Hands)
	//GAPI-3922-ST - Retrieve all Tickets with  Request Type of Planned Work and category Auditing (Ticket Type Remote Hands)
	//GAPI-3934-ST - Retrieve all Tickets with  Request Type of Urgent Work and category Equipment troubleshoot or replacement (Ticket Type Remote Hands)
	//GAPI-3923-ST - Retrieve all Tickets with  Request Type of Planned Work and category Other(Ticket Type Remote Hands)
	//GAPI-3933-ST - Retrieve all Tickets with  Request Type of Urgent Work and category KVM (keyboard, video, mouse) assistance(Ticket Type Remote Hands)
	//GAPI-3764-ST - Retrieve all Tickets with  Request Type of Planned Work     (Ticket Type Remote Hands)
	//GAPI-3931-ST - Retrieve all Tickets with  Request Type of Urgent Work and category Other(Ticket Type Remote Hands)
	//GAPI-3918-ST - Retrieve all Tickets with  Request Type of Planned Work and category Customer premise cabling (Ticket Type Remote Hands)
	//GAPI-3932-ST - Retrieve all Tickets with  Request Type of Urgent Work and category Power cycle or reboot(Ticket Type Remote Hands)
	//GAPI-3779-ST- Retrieve all Tickets - Verify Offset
	//GAPI-3777-ST - Retrieve all Tickets with  Request Type of  Facilities Request, Category of Planned Maintenance  (Ticket Type Customer Support)
	//GAPI-3771-ST - Retrieve all Tickets with  Request Type of Account Management, Category of User Account Inquiry   (Ticket Type Customer Support)
	//GAPI-3760-ST - Retrieve all Tickets in In Progress Status
	//GAPI-3774-ST - Retrieve all Tickets with  Request Type of  Facilities Request, Category of Cooling - Office  (Ticket Type Customer Support)
	//GAPI-3926-ST - Retrieve all Tickets with  Request Type of Planned Work and category Equipment de-installation(Ticket Type Remote Hands)
	//GAPI-3761-ST - Retrieve all Tickets in Completed Status
	//GAPI-3921-ST - Retrieve all Tickets with  Request Type of Planned Work and category Infrastructure Data Cabling (Ticket Type Remote Hands)
	//GAPI-3928-ST - Retrieve all Tickets with  Request Type of Planned Work and category Tape swaps(Ticket Type Remote Hands)
	//GAPI-3775-ST - Retrieve all Tickets with  Request Type of  Facilities Request, Category of  Electrical  (Ticket Type Customer Support)
	//GAPI-3935-ST - Retrieve all Tickets with  Request Type of Urgent Work and category Existing cross connect or connectivity testing(Ticket Type Remote Hands)
	//GAPI-3917-ST - Retrieve all Tickets with  Request Type of Planned Work and category Connectivity Circuit Testing (Ticket Type Remote Hands)
	//GAPI-3765-ST - Retrieve all Tickets with  Request Type of Urgent Work  (Ticket Type Remote Hands)
	//GAPI-3927-ST - Retrieve all Tickets with  Request Type of Planned Work and category Equipment installation(Ticket Type Remote Hands)
	//GAPI-3759-ST - Retrieve all Tickets in New Status
	//GAPI-3773-ST - Retrieve all Tickets with  Request Type of  Facilities Request, Category of Cooling - DataCenter  (Ticket Type Customer Support)

	//GAPI-9152-ST - Retrieve all Tickets with Request Type of Cross Connect and Category as New Install (Ticket Type Customer Support)
	//GAPI-9153-ST - Retrieve all Tickets with Request Type of Cross Connect and Category as Change (Hot Cut) (Ticket Type Customer Support)
	//GAPI-9154-ST - Retrieve all Tickets with Request Type of Cross Connect and Category as Disconnect  (Ticket Type Customer Support)

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetAllWithServiceTicketFilters(Map<String, String> data) throws InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		testData=data;
		String filters = data.get("filters");
		String apiURI = domain + path +"?"+ filters;
		System.out.println("Request is: "+apiURI);
		//Thread.sleep(1000);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
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
		Assert.assertTrue(requestArray.length() >= 0);
		Map<String, String> objMap = new HashMap<String, String>();
		//filters=filters.replace("KVM Assistance", "KVM (keyboard, video, mouse) assistance");
		if (filters.contains("&")) {
			String[] values = filters.split("&");
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
				if(!(individualRequestInfo.getString("ticketType").contains("Shipping and Receiving")))
				{
				String actualRequestType = individualRequestInfo.getString("requestType").trim();
				Assert.assertTrue(actualRequestType.equals("Planned Work") || actualRequestType.equals("Urgent Work") || actualRequestType.equals("Account Management")|| actualRequestType.equals("Accounts Receivable")|| actualRequestType.equals("Facilities Request")|| actualRequestType.equals("Amenities")||actualRequestType.equals("Visitor Access")||actualRequestType.equals("Permanent Badge")||actualRequestType.equals("OSP Access")||actualRequestType.equals("Cross Connect"));
				String actualCategoryValue = individualRequestInfo.getString("category");
				verifyCategoryValue(actualRequestType, actualCategoryValue);
				logger.log(Status.PASS, "Response details Matched with the given filter criteria");
				}
			}
		} else if (filters.contains("size") || filters.contains("page")) {
			String keyName, keyValue;
			for (int i = 0; i < requestArray.length(); i++) {
				JSONObject individualRequestInfo = requestArray.getJSONObject(i);
				for (Entry<String, String> m : objMap.entrySet()) {
					keyName = m.getKey();
					keyValue = m.getValue();
					if (keyName.equals("size")) {
						int objectsLength = requestArray.length();
						Assert.assertEquals(String.valueOf(objectsLength), keyValue);
					} else if (keyName.equals("page")) {
						int objectsLength = requestArray.length();
						Assert.assertTrue(objectsLength>0 || objectsLength==25);

					} else {
						String actualValue = individualRequestInfo.getString(keyName);
						Assert.assertEquals(actualValue, keyValue);
					}
				}
				if(!(individualRequestInfo.getString("ticketType").contains("Shipping and Receiving")))
				{
				String actualRequestType = individualRequestInfo.getString("requestType");
				Assert.assertTrue(actualRequestType.equals("Planned Work") || actualRequestType.equals("Urgent Work") || actualRequestType.equals("Account Management")|| actualRequestType.equals("Accounts Receivable")|| actualRequestType.equals("Facilities Request")|| actualRequestType.equals("Amenities")||actualRequestType.equals("Visitor Access")||actualRequestType.equals("Permanent Badge")||actualRequestType.equals("OSP Access")||actualRequestType.equals("PoP/POE Room Access")||actualRequestType.equals("Cross Connect"));
				String actualCategoryValue = individualRequestInfo.getString("category");
				verifyCategoryValue(actualRequestType, actualCategoryValue);
				logger.log(Status.PASS, "Response details Matched with the given filter criteria");
				}

			}
		}
	}
	
	public void verifyCategoryValue(String actualRequestType, String actualCategoryValue) {

		if (actualRequestType.equals("Planned Work")) {
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Existing cross connect or connectivity testing")
					|| actualCategoryValue.equalsIgnoreCase("Customer premise cabling")
					|| actualCategoryValue.equalsIgnoreCase("Tape swaps")
					|| actualCategoryValue.equalsIgnoreCase("Equipment installation")
					|| actualCategoryValue.equalsIgnoreCase("Equipment de-installation")
					|| actualCategoryValue.equalsIgnoreCase("Dedicated escort")
					|| actualCategoryValue.equalsIgnoreCase("Auditing")
					|| actualCategoryValue.equalsIgnoreCase("Other"));
		} else if(actualRequestType.equals("Urgent Work")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Existing cross connect or connectivity testing")
					|| actualCategoryValue.equalsIgnoreCase("Equipment troubleshoot or replacement")
					|| actualCategoryValue.equalsIgnoreCase("KVM (keyboard, video, mouse) assistance")
					|| actualCategoryValue.equalsIgnoreCase("Power cycle or reboot")
					|| actualCategoryValue.equalsIgnoreCase("Other"));
		}
		else if(actualRequestType.equals("Account Management")){
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("User Account Inquiry"));
		}
		else if(actualRequestType.equals("Accounts Receivable")){
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("General Billing Inquiry"));
		}
		else if(actualRequestType.equals("Facilities Request")){
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Cooling - Datacenter")
					|| actualCategoryValue.equalsIgnoreCase("Cooling - Office")
					|| actualCategoryValue.equalsIgnoreCase("Electrical")
					|| actualCategoryValue.equalsIgnoreCase("Fire/Safety")
					|| actualCategoryValue.equalsIgnoreCase("Planned Maintenance"));
		}
		else if(actualRequestType.equals("Cross Connect")){
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("New Install")
					|| actualCategoryValue.equalsIgnoreCase("Change (Hot Cut)")
					|| actualCategoryValue.equalsIgnoreCase("Disconnect"));
		}
		else if(actualRequestType.equals("Visitor Access")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("New Visitor Access")
					|| actualCategoryValue.equalsIgnoreCase("Modify Visitor Access")
					|| actualCategoryValue.equalsIgnoreCase("KVM (keyboard, video, mouse) assistance")
					|| actualCategoryValue.equalsIgnoreCase("Terminate All Visitor Access"));
		}
		else if(actualRequestType.equals("Permanent Badge")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("New Badge")
					|| actualCategoryValue.equalsIgnoreCase("Terminate All Access")
					|| actualCategoryValue.equalsIgnoreCase("Modify Badge"));
		}
		else if(actualRequestType.equals("OSP Access")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase(""));
					 
		}
		else if(actualRequestType.equals("PoP/POE Room Access")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase(""));
					 
		}
		else
		{
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Elevator")
					|| actualCategoryValue.equalsIgnoreCase("Loading Dock")
					|| actualCategoryValue.equalsIgnoreCase("Parking")
					|| actualCategoryValue.equalsIgnoreCase("General Inquiry"));
		}
	}
	
}
