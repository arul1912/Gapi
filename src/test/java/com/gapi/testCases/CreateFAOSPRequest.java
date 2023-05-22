package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreateFAOSPRequest extends BaseClass {
	public static String path ="/facility-access/osps/access-requests";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String accessStartDate = tc.getCurrentDateAndTime();
	static String accessEndDate = tc.getCurrentDateTime();

	// Create OSP Access Ticket with all possible combinations
	//GAPI-6264-OSP Access - Create

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateOSPAccessRequest(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		JSONObject mainObject = createPayloadForOSPTicket(data);

		System.out.println("Body is------ :");
		System.out.println(mainObject.toString());

//		String apiURI = idpdomain + path;
		String apiURI =	"https://asc-tst-gapi-facility-access-service.tst-api.digitalrealty.com/facility-access/osps/access-requests";
		System.out.println(apiURI);
		String jsonbody = mainObject.toString();
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + createOSPToken)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "Dportal211@digitaltest.com")
				.body(jsonbody).post(apiURI).then().extract().response();
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		Assert.assertEquals(200, response.getStatusCode());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		if (response.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created OSP access request");
			logger.log(Status.PASS, "Created OSP access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "osp_" + WONumber);
			// tc.retrieveFAGETResponseAndRequestParameters(WONumber, jsonbody,
			// "service-tickets?id=");
		} else {
			logger.fail("Error Response message is " + response.asString());
			logger.log(Status.FAIL, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
		}
	}

	// Verify the Retrieval Of OSP Access Request Using ID
	//GAPI-6276-Retrieval of OSP access ticket details using ID
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyRetrievalOfOSPAccessRequestUsingID(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		JSONObject mainObject = createPayloadForOSPTicket(data);

		System.out.println("Body is------ :");
		System.out.println(mainObject.toString());

//		String apiURI = domain + path;
		String apiURI =	"https://asc-tst-gapi-facility-access-service.tst-api.digitalrealty.com/facility-access/osps/access-requests";
		
		System.out.println(apiURI);
		String jsonbody = mainObject.toString();
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + createOSPToken)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "Dportal211@digitaltest.com")
				.body(jsonbody).post(apiURI).then().extract().response();
		logger.log(Status.PASS, "Created OSP Access ticket payload and Fired API");
		log.info("Created OSP Access ticket payload and Fired API");
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		Assert.assertEquals(200, response.getStatusCode());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		log.info("Response Status Code and Status Message is " + response.statusLine());
		
		if (response.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created OSP access request");
			logger.log(Status.PASS, "Created OSP access request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "osp_" + WONumber);
			validateFacilityAccessTickedBasedonID(data, WONumber);
		} else {
			logger.fail("Error Response message is " + response.asString());
			logger.log(Status.FAIL, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
		}
	}

	// Create OSP Access Ticket with Invalid token
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateOSPAccessRequestForInvalidToken(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		JSONObject mainObject = createPayloadForOSPTicket(data);

		System.out.println("Body is------ :");
		System.out.println(mainObject.toString());

	//	String apiURI = domain + path;
		String apiURI =	"https://asc-tst-gapi-facility-access-service.tst-api.digitalrealty.com/facility-access/osps/access-requests";
		System.out.println(apiURI);
		String jsonbody = mainObject.toString();
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", 74834648)
				.header("Account-Id", "0012E00002dmMY7QAM")
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(jsonbody)
				.post(apiURI)
				.then().extract().response();
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		Assert.assertEquals(404, response.getStatusCode());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 404"));
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
	//	tc.verifyErrorResponseMessage(response.asString(), "client",
	//			"Unauthorized. Access token is missing or invalid.");
		tc.verifyErrorMessagefromResponse(response.asString(), "MISSING_ASSET_DATA",
				"No Asset is mapped with the given account");
		
		logger.pass("Not Allowed to Create a OSP Request Details With Invalid token");
	}

	// Create OSP Access Ticket for For Invalid Legal Entity
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateOSPAccessRequestForInvalidLegalentity(Map<String, String> data)
			throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));

		JSONObject mainObject = createPayloadForOSPTicket(data);

//		String apiURI = domain + path;
		String apiURI =	"https://asc-tst-gapi-facility-access-service.tst-api.digitalrealty.com/facility-access/osps/access-requests";
		System.out.println(apiURI);
		String jsonbody = mainObject.toString();
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + createOSPToken)
				.header("Content-Type", "application/json").header("Master-Account-Id",masterAccountID)
				.header("Account-Id", "abcde").header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(jsonbody).post(apiURI).then().extract().response();

		log.info("Provided Invalid Legal Entity");
		logger.pass("Created Body with Invalid Global Ultimate and Fired API");
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());

		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 404"));
		log.info("Response Status Code and Message Is  " + response.getStatusLine());
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
//		tc.verifyErrorResponseMessage(response.asString(), "client", "Bad request was submitted.");
		tc.verifyErrorMessagefromResponse(response.asString(), "MISSING_ASSET_DATA",
				"No Asset is mapped with the given account");
		logger.pass("Not Allowed to Create a OSP Request  With Invalid Legal Entity");
		log.info("Not Allowed to Create a  OSP Request With Invalid Legal Entity");

	}

	// Create OSP Access Ticket for For Invalid Global Ultimate
	/*
	 * @Test(dataProvider = "testCasesData", dataProviderClass =
	 * DataProviderUtility.class) public void
	 * verifyCreateOSPAccessRequestForInvalidGlobalUltimate(Map<String, String>
	 * data) throws IOException, InterruptedException { Thread. sleep(5000); logger
	 * = extent.createTest(data.get("TestCaseName"));
	 * 
	 * JSONObject mainObject = createPayloadForOSPTicket(data);
	 * 
	 * System.out.println("Body is------ :");
	 * System.out.println(mainObject.toString());
	 * 
	 * String apiURI = Vuatdomain + path; System.out.println(apiURI); String
	 * jsonbody = mainObject.toString();
	 * 
	 * Response response = RestAssured.given().relaxedHTTPSValidation()
	 * .header("Authorization", "Bearer " + TokenVuat) .header("Content-Type",
	 * "application/json") .header("Master-Account-Id", 12345) .header("Account-Id",
	 * "0012E00002dmMY7QAM") .header("User-Email",
	 * "vigneswarareddy@digitalrealty.com") .body(jsonbody) .post(Vuatdomain + path)
	 * .then().extract().response();
	 * log.info("Created Body with Invalid Global Ultimate and Fired API");
	 * logger.pass("Created Body with Invalid Global Ultimate and Fired API");
	 * System.out.println("Response is : " + response.asString());
	 * System.out.println("Status Code is : " + response.getStatusCode());
	 * 
	 * Assert.assertEquals(response.getStatusCode(), 400);
	 * Assert.assertTrue(response.statusLine().contains("HTTP/1.1 400"));
	 * log.info("Response Status Code and Message Is  " + response.getStatusLine());
	 * logger.pass("Response Status Code and Message Is " +
	 * response.getStatusLine()); logger.pass("Error Response Message Is" +
	 * response.asString()); tc.verifyErrorResponseMessage(response.asString(),
	 * "client", "Bad request was submitted."); logger.
	 * pass("Not Allowed to Create a OSP Request  With Invalid Global Ultimate");
	 * log.info("Not Allowed to Create a  OSP Request With Invalid Global Ultimate"
	 * );
	 * 
	 * }
	 */

	// Creates Pay load for OSP ticket
	public JSONObject createPayloadForOSPTicket(Map<String, String> data) {
		JSONObject mainObject = new JSONObject();
		JSONArray visitorsArray = new JSONArray();
		JSONObject visitorsObject = new JSONObject();
		visitorsObject.put("visitorFirstName", data.get("visitorFirstName"));
		visitorsObject.put("visitorLastName", data.get("visitorLastName"));
		visitorsObject.put("visitorEmail", data.get("visitorEmail"));
		visitorsObject.put("visitorPhone", data.get("visitorPhone"));
		visitorsObject.put("contactType", "Escorted");
		visitorsObject.put("company", "Test");
		visitorsArray.put(visitorsObject);

		mainObject.put("visitors", visitorsArray);
		mainObject.put("company", "Test");
		mainObject.put("visitorType", data.get("visitorType"));
		mainObject.put("escortFirstName", data.get("escortFirstName"));
		mainObject.put("escortLastName", data.get("escortLastName"));
		mainObject.put("visitorHostFirstName", data.get("visitorHostFirstName"));
		mainObject.put("visitorHostLastName", data.get("visitorHostLastName"));
		mainObject.put("visitorHostCompany", data.get("visitorHostCompany"));
		mainObject.put("workDescription", data.get("workDescription"));
		mainObject.put("serviceImpact", data.get("serviceImpact"));
		mainObject.put("serviceImpactedDescription", data.get("serviceImpactedDescription"));
		mainObject.put("serviceType", data.get("serviceType"));
		mainObject.put("customerExternalReference", data.get("customerExternalReference"));
		mainObject.put("emailNotifications", data.get("emailNotifications"));
		mainObject.put("customerExternalReference", data.get("customerExternalReference"));
		mainObject.put("details", data.get("details"));
		JSONArray sitesArray = new JSONArray();
		JSONObject sitesObject = new JSONObject();
		sitesObject.put("site", data.get("site"));
		JSONArray locationArray = new JSONArray();
		JSONObject locationObject = new JSONObject();
		locationObject.put("location", data.get("locations"));
		locationObject.put("accessStartDate", accessStartDate);
		locationObject.put("accessEndDate", accessEndDate);
		locationObject.put("accessStartTime", "06:22:00");
		locationObject.put("accessEndTime", "06:22:00");
		locationArray.put(locationObject);
		sitesObject.put("locations", locationArray);
		sitesArray.put(sitesObject);
		mainObject.put("sites", sitesArray);

		return mainObject;
	}
	
	// Upload Attachment to OSP Access Ticket
	//GAPI-6850-FA-OSP-  Attachments

			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyUploadAttachmentToOSPAccessRequest(Map<String, String> data) throws IOException, InterruptedException {

				logger = extent.createTest(data.get("TestCaseName"));
				JSONObject mainObject = createPayloadForOSPTicket(data);

				System.out.println("Body is------ :");
				System.out.println(mainObject.toString());

//				String apiURI = domain + path;
				String apiURI =	"https://asc-tst-gapi-facility-access-service.tst-api.digitalrealty.com/facility-access/osps/access-requests";
				System.out.println(apiURI);
				String jsonbody = mainObject.toString();
				Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + createOSPToken)
						.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
						.header("Account-Id", accountID).header("User-Email", "Dportal211@digitaltest.com")
						.body(jsonbody).post(apiURI).then().extract().response();
				System.out.println("Response is : " + response.asString());
				System.out.println("Status Code is : " + response.getStatusCode());
				Assert.assertEquals(200, response.getStatusCode());
				logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
				String WONumber=null;
				if (response.getStatusCode() == 200) {
					WONumber = tc.getWOIDFromResponse(response);
					log.info("The WO Number Created is " + WONumber);
					logger.log(Status.PASS, "Successfully Created OSP access request");
					logger.log(Status.PASS, "Created OSP access request Id  " + WONumber);
					tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "osp_" + WONumber);
					// tc.retrieveFAGETResponseAndRequestParameters(WONumber, jsonbody,
					// "service-tickets?id=");
				} else {
					logger.fail("Error Response message is " + response.asString());
					logger.log(Status.FAIL, "Not allowed to create Visitor access request");
				}
				tc.addAttachmentToTheRequestAndVerify(WONumber,path,Token);
			}


	//Reusable Method to validate Retrieval of facility Access requests based on ID
	public void validateFacilityAccessTickedBasedonID(Map<String, String> data, String WONumber) {
		String retrieveapiURI = domain + "/facility-access/access-tickets/" + WONumber;
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.get(retrieveapiURI).then().extract().response();

		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().get("ticketId"), WONumber);
		Assert.assertEquals(response.jsonPath().get("requestType"), "OSP Access");
		Assert.assertEquals(response.jsonPath().get("contactFirstName"), data.get("visitorFirstName"));
		Assert.assertEquals(response.jsonPath().get("contactLastName"), data.get("visitorLastName"));
		Assert.assertEquals(response.jsonPath().get("contactEmail"), data.get("visitorEmail"));
		Assert.assertEquals(response.jsonPath().get("contactPhone"), data.get("visitorPhone"));
		Assert.assertEquals(response.jsonPath().get("site[0].site"), data.get("site"));
		Assert.assertEquals(response.jsonPath().get("site[0].locations[0].location"), data.get("locations"));
		Assert.assertEquals(response.jsonPath().get("escortFirstName"), data.get("escortFirstName"));
		Assert.assertEquals(response.jsonPath().get("escortLastName"), data.get("escortLastName"));
		Assert.assertEquals(response.jsonPath().get("visitorBadgeStartDate"),accessStartDate);
		Assert.assertEquals(response.jsonPath().get("visitorBadgeEndDate"),accessEndDate);
		Assert.assertEquals(response.jsonPath().get("customerExternalReference"), data.get("customerExternalReference"));
		Assert.assertEquals(response.jsonPath().get("visitorHostFirstName"), data.get("visitorHostFirstName"));
		Assert.assertEquals(response.jsonPath().get("visitorHostLastName"), data.get("visitorHostLastName"));
		Assert.assertEquals(response.jsonPath().get("hostCompany"), data.get("visitorHostCompany"));
		Assert.assertEquals(response.jsonPath().get("workDescription"), data.get("workDescription"));
		Assert.assertEquals(response.jsonPath().get("details"), data.get("details"));
		Assert.assertEquals(response.jsonPath().get("contactType"), "Escorted");
		logger.log(Status.PASS, "Successfully Retrieved and validated OSP ticket generated");
		log.info("Successfully Retrieved and validated the OSP ticket generated");
		
	}
}
