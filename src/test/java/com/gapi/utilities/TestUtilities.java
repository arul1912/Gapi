package com.gapi.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;

import com.aventstack.extentreports.Status;
import com.gapi.testCases.AddAttachmentsToTheRemoteHandsTicket;
import com.gapi.testCases.AddCommentsToTheRemoteHandsTicket;
import com.gapi.testCases.BaseClass;
import com.gapi.testCases.GetFAAccessTicketDetails;
import com.gapi.testCases.GetPermissionGroupDetails;
import com.gapi.testCases.GetRemoteHandsTicketDetails;
import com.gapi.testCases.GetReports;
import com.gapi.testCases.GetRolesDetails;
import com.gapi.testCases.GetShipmentsTicketDetails;
import com.google.gson.JsonArray;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.internal.path.json.JSONAssertion;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;

public class TestUtilities extends BaseClass {
	private static Random random = new Random(93285);

	ReadConfig readconfig = new ReadConfig();
	String masterAccount = readconfig.getConfigValue("MasterAccountID");
	GetPermissionGroupDetails getobj = new GetPermissionGroupDetails();

	// String accountID = readconfig.getConfigValue("AccountID");
	// String userEmail = readconfig.getConfigValue("userEmail");
	// static Logger log= Logger.getLogger(getClass());

	public String getCurrentDateAndTime() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currentDate = formatter.format(date);
		return currentDate;
	}

	public String getCurrentDateNextYear()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1); // to get previous year add -1
		Date nextYear = cal.getTime();
		String nextyear = formatter.format(nextYear);
		return nextyear;
	}

	public String getCurrentDate() {

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		String currentDate = formatter.format(date);
		return currentDate;
	}

	public String getCurrentDateTime() {

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Using today's date
		c.add(Calendar.DATE, 1); // Adding extra days

		String output = sdf.format(c.getTime());
		return output;
	}
	public String getLessThanCurrentDate() {

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Using today's date
		c.add(Calendar.DATE, -1); // Adding extra days

		String output = sdf.format(c.getTime());
		return output;
	}
	// Method to get single element from Response

	public String getJsonResponseElement(String response, String keyName) {

		JSONObject json = new JSONObject(response);
		String value = (String) json.get(keyName);
		value.trim();
		return value;
	}

	public String getJsonvalueResponseElement(String response, String keyName) {

		JSONArray object = new JSONArray(response);
		JSONObject Jsonvalue = (JSONObject) object.get(0);
		String ID = Jsonvalue.getString("notificationId");
		return ID;
	}

	public  String getAttributeFromArrayResponse(String responseBody,String array,String attribute)
	{
		JSONObject object = new JSONObject(responseBody);
		JSONArray jsonarray = object.getJSONArray(array);
		String getvalue = jsonarray.getJSONObject(0).getString(attribute);
		return getvalue;
	}
	public  String getAttributeFromJsonResponse(String responseBody,String attribute)
	{
		JSONObject object = new JSONObject(responseBody);
		String getvalue = object.getString(attribute);
		return getvalue;
	}
	public String getAttributeFromArrayResponse1(String responseBody,String array,String attribute) {
		JSONObject object = new JSONObject(responseBody);
		JSONArray jsonarray = object.getJSONArray(array);
		String getvalue = jsonarray.getJSONObject(0).getString(attribute);
		return getvalue;
	}
	public  String getWONumberIdFromResponse(Response response) {

		String responseBody = response.asString();
		JSONObject object = new JSONObject(responseBody);
		JSONObject woorder = object.getJSONObject("workOrder");
		String WoNumber = woorder.getString("workOrderId");
		return WoNumber;

	}
	
	public String getJsonfromResponseElement(String response) {

		JsonPath j = new JsonPath(response.toString());
		String WOIDInformation = j.getString("workOrder[0].workOrderId");

		return WOIDInformation;

	}

	// Method to get attribute value

	public static String getAttributeValue(WebElement element) {

		String attributeValue = element.getAttribute("value");
		return attributeValue;

	}
	// Method to get all elements from Request body

	public ArrayList<String> getKeyValueFromRequestBody(String requestBody) {

		JSONObject object = new JSONObject(requestBody);
		ArrayList<String> keyValues = new ArrayList<String>();
		for (Iterator iterator = object.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (key.contains("isLoadingBayRequired")) {
				String value = String.valueOf(object.get(key));
			}
			String value = (String) object.get(key);
			// Boolean value1 = (Boolean) object.get(key);
			keyValues.add(value);
			/*
			 * if (value.equals("Planned Work") || value.equals("Urgent Work")) {
			 * keyValues.add("Remote Hands Services"); } else { keyValues.add(value); }
			 */

		}
		return (keyValues);
	}

	//Re-usabale Method to Get SysID for the given WO#
	
	public List<String> getSysID(String WONumber) throws IOException, InterruptedException 
	{
		String getChildTcktsAPI=domain+"/facility-access/"+WONumber+"/children-tickets";
		Response response=getRequest(Token, masterAccountID, accountID, getChildTcktsAPI);
		List<String> listofSysIDs=new ArrayList<>();		
		JsonPath jsonPathEvaluator = response.jsonPath();		 
		JSONObject object = new JSONObject(response.asString());
		JSONArray requestArray = object.getJSONArray("wot");
		for(int i=0;i<requestArray.length();i++)
		{
			listofSysIDs.add(requestArray.getJSONObject(i).getString("sysIdWOTask"));
					
		 }		
		System.out.println(listofSysIDs);
		return listofSysIDs;
	}
	
	//Re-usabale Method to Get ticketStatus for the given WO#
	public String getFATicketStatus(String WONumber) throws IOException, InterruptedException 
	{
		String apiURI=domain+"/facility-access/access-tickets/"+WONumber;
		Response response=getRequest(Token, masterAccountID, accountID, apiURI);
		String status=response.jsonPath().getString("ticketStatus");
		return status;
		
	}
	
	   //Reusable Method to approve based on sysID
		public void approveSysID(String sysID) throws IOException, InterruptedException 
		{
			String apiURI=domain+"/facility-access/access-tickets/"+sysID+"/approve";
			Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + approveRejectToken)
					.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID).put(apiURI).then().extract().response();
			System.out.println(response.asString());
			Assert.assertTrue(response.asString().contains("success"));
			
		}
		
		 //Reusable Method to approve based on sysID
		public void rejectSysID(String sysID) throws IOException, InterruptedException 
		{
			String apiURI=domain+"/facility-access/access-tickets/"+sysID+"/reject";
			Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + approveRejectToken)
					.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID).put(apiURI).then().extract().response();
			System.out.println(response.asString());
			Assert.assertTrue(response.asString().contains("success"));
			
		}
	
	// Method to get all key Names

	public ArrayList<String> getKeyNamesFromRequestBody(String requestBody) {

		JSONObject object = new JSONObject(requestBody);
		ArrayList<String> keyNames = new ArrayList<String>();
		for (Iterator iterator = object.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			keyNames.add(key);
		}
		return (keyNames);
	}

	public ArrayList<String> getKeyNamesFromFARequestBody(String body) {

		JSONObject object = new JSONObject(body);
		ArrayList<String> keyNames = new ArrayList<String>();
		for (Iterator iterator = object.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			keyNames.add(key);
		}
		return (keyNames);
	}

	// Method to get parameters from response based on passed keyNames array
	public ArrayList<String> getResponseElementsOfGetRequest(String responsebody, String RequestBody) {

		JSONObject object1 = new JSONObject(responsebody);
		ArrayList<String> getResponseElements = new ArrayList<String>();
		ArrayList<String> text = getKeyNamesFromRequestBody(RequestBody);
		String responseElement;
		for (int i = 0; i < text.size(); i++) {
			String keyname = text.get(i);
			// String responseElement = (String) object1.get(keyname);
			if (object1.get(keyname).getClass().getSimpleName().equalsIgnoreCase("Integer")) {
				responseElement = String.valueOf(object1.get(keyname));
			} else if (object1.get(keyname).getClass().getSimpleName().equals(keyname)) {
				responseElement = String.valueOf(object1.get(keyname));
			}

			else {
				if (keyname.contains("isLoadingBayRequired")) {
					responseElement = String.valueOf(object1.get(keyname));
				} else {
					responseElement = object1.getString(keyname);
				}
			}
			getResponseElements.add(responseElement);
		}
		return (getResponseElements);
	}

	public ArrayList<String> getResponseElementsOfFAGetRequest(String responsebody, String body) {

		JSONObject object1 = new JSONObject(responsebody);
		System.out.println("Request 2 GET---:" + object1);
		ArrayList<String> getResponseElements = new ArrayList<String>();
		ArrayList<String> text = getKeyNamesFromFARequestBody(body);
		JSONArray array = new JSONArray(text);
		System.out.println("Request 3 GET---:" + array);
		String responseElement;
		for (int i = 0; i < text.size(); i++) {
			String keyname = text.get(i);
			// String responseElement = (String) object1.get(keyname);
			if (object1.get(keyname).getClass().getSimpleName().equalsIgnoreCase("Integer")) {
				responseElement = String.valueOf(object1.get(keyname));
			} else {
				responseElement = object1.getString(keyname);
			}
			getResponseElements.add(responseElement);
		}
		return (getResponseElements);
	}

	public void saveRequestIdToExcelSheet(String excelPath, String sheet, int cloumnIndex1, int index2, String data,
			String data2) throws IOException {

		int rowCount = XLUtilities.getRowCount(excelPath, sheet);
		XLUtilities.setCellData(excelPath, sheet, rowCount, cloumnIndex1, index2, data, data2);

	}

	public void retrieveGETCallResponseAndCompareWithRequestParameters(String woNumber, String requestBody,
			String path) {

		TestUtilities tc = new TestUtilities();
		// Retrieve Request details by using ID
		GetRemoteHandsTicketDetails getobj = new GetRemoteHandsTicketDetails();
		// String getCallResponse = getobj.getRemoteHandsTicketInfoById(Id);
		String getCallResponse = getobj.getRequestDetailsById(woNumber, path);
		ArrayList<String> getCallResponseParameters = tc.getResponseElementsOfGetRequest(getCallResponse, requestBody);

		// Verifying POST call Response vs GET call response
		Assert.assertEquals(tc.getJsonResponseElement(getCallResponse, "id"), woNumber);

		log.info("The Details of retrievevd WO Number\n" + getCallResponse + "\n");
		logger.log(Status.PASS, "The Details of retrievevd WO Numbers\n" + getCallResponse);

		ArrayList<String> requestParameters = tc.getKeyValueFromRequestBody(requestBody);
		for (int i = 0; i < requestParameters.size(); i++) {
			Assert.assertEquals(getCallResponseParameters.get(i).trim(), requestParameters.get(i).trim(),
					"both request body and get call response parameters should match");
		}
		log.info("The Posted details are same as retrieved details");
		logger.log(Status.PASS, "The Posted details are same as retrieved details");

	}

	/*
	 * public void retrieveGETResponseAndRequestParameters(String permissionGroupId,
	 * String requestBody, String path) {
	 * 
	 * TestUtilities tc = new TestUtilities(); GetPermissionGroupDetails getobj =
	 * new GetPermissionGroupDetails(); String getCallResponse =
	 * getobj.getPermissionGroupInfoByGroupId(permissionGroupId, path);
	 * ArrayList<String> getCallResponseParameters =
	 * tc.getResponseElementsOfGetRequest(getCallResponse, requestBody);
	 * Assert.assertEquals(tc.getJsonResponseElement(getCallResponse, "id"),
	 * permissionGroupId);
	 * log.info("The Details of retrievevd Permission GroupID Number\n" +
	 * getCallResponse + "\n"); logger.log(Status.PASS,
	 * "The Details of retrievevd Permission GroupID Numbers\n" + getCallResponse);
	 * ArrayList<String> requestParameters =
	 * tc.getKeyValueFromRequestBody(requestBody); for (int i = 0; i <
	 * requestParameters.size(); i++) {
	 * Assert.assertEquals(getCallResponseParameters.get(i).trim(),
	 * requestParameters.get(i).trim(),
	 * "both request body and get call response parameters should match"); }
	 * log.info("The Posted details are same as retrieved details");
	 * logger.log(Status.PASS, "The Posted details are same as retrieved details");
	 * 
	 * }
	 */

	/*
	 * public void retrieveFAGETResponseAndRequestParameters(String woNumber, String
	 * body, String path) {
	 * 
	 * TestUtilities tc = new TestUtilities(); GetFAAccessTicketDetails getobj = new
	 * GetFAAccessTicketDetails(); // String getCallResponse =
	 * getobj.getFARequestDetailsById(woNumber, path); ArrayList<String>
	 * getCallResponseParameters =
	 * tc.getResponseElementsOfFAGetRequest(getCallResponse, body);
	 * Assert.assertEquals(tc.getJsonResponseElement(getCallResponse, "id"),
	 * woNumber); log.info("The Details of retrievevd Permission GroupID Number\n" +
	 * getCallResponse + "\n"); logger.log(Status.PASS,
	 * "The Details of retrievevd Permission GroupID Numbers\n" + getCallResponse);
	 * ArrayList<String> requestParameters = tc.getKeyValueFromRequestBody(body);
	 * for (int i = 0; i < requestParameters.size(); i++) {
	 * Assert.assertEquals(getCallResponseParameters.get(i).trim(),
	 * requestParameters.get(i).trim(),
	 * "both request body and get call response parameters should match"); }
	 * log.info("The Posted details are same as retrieved details");
	 * logger.log(Status.PASS, "The Posted details are same as retrieved details");
	 */

	public void retrieveRolesGETResponseAndRequestParameters(String rolesId, String requestBody, String path) {

		TestUtilities tc = new TestUtilities();
		GetRolesDetails getobj = new GetRolesDetails();
		String getCallResponse = getobj.getRolesInfoByRoleId(rolesId, path);
		ArrayList<String> getCallResponseParameters = tc.getResponseElementsOfGetRequest(getCallResponse, requestBody);
		Assert.assertEquals(tc.getJsonResponseElement(getCallResponse, "id"), rolesId);
		log.info("The Details of retrievevd Roles ID \n" + getCallResponse + "\n");
		logger.log(Status.PASS, "The Details of retrievevd Roles ID \n" + getCallResponse);
		ArrayList<String> requestParameters = tc.getKeyValueFromRequestBody(requestBody);
		for (int i = 0; i < requestParameters.size(); i++) {
			Assert.assertEquals(getCallResponseParameters.get(i).trim(), requestParameters.get(i).trim(),
					"both request body and get call response parameters should match");
		}
		log.info("The Posted details are same as retrieved details");
		logger.log(Status.PASS, "The Posted details are same as retrieved details");

	}

	public void retrieveReportsGETResponseAndRequestParameters(String reportId, String requestBody, String path) {

		TestUtilities tc = new TestUtilities();
		GetReports getobj = new GetReports();
		String getCallResponse = getobj.getReportsInformationByID(reportId, path);
		ArrayList<String> getCallResponseParameters = tc.getResponseElementsOfGetRequest(getCallResponse, requestBody);
		Assert.assertEquals(tc.getJsonResponseElement(getCallResponse, "reportID"), reportId);
		log.info("The Details of retrievevd Report Details \n" + getCallResponse + "\n");
		logger.log(Status.PASS, "The Details of retrievevd Report Details \n" + getCallResponse);
		ArrayList<String> requestParameters = tc.getKeyValueFromRequestBody(requestBody);
		for (int i = 0; i < requestParameters.size(); i++) {
			Assert.assertEquals(getCallResponseParameters.get(i).trim(), requestParameters.get(i).trim(),
					"both request body and get call response parameters should match");
		}
		log.info("The Posted details are same as retrieved details");
		logger.log(Status.PASS, "The Posted details are same as retrieved details");

	}

	public void retrieveUserGETCallResponseAndCompareWithRequestParameters(String userId, String requestBody,
			String path) {

		TestUtilities tc = new TestUtilities();
		// Retrieve Request details by using ID
		GetRemoteHandsTicketDetails getobj = new GetRemoteHandsTicketDetails();
		// String getCallResponse = getobj.getRemoteHandsTicketInfoById(Id);
		// sending GET call request using userId
		String getCallResponse = getobj.getRequestDetailsById(userId, path);
		// to retrieve get call response parameters which are part of the POST request
		// body
		ArrayList<String> getCallResponseParameters = tc.getResponseElementsOfGetRequest(getCallResponse, requestBody);

		// Verifying POST call Response vs GET call response
		Assert.assertEquals(tc.getJsonResponseElement(getCallResponse, "userId"), userId);
		Assert.assertTrue(tc.getJsonResponseElement(getCallResponse, "status").equalsIgnoreCase("CREATED"));
		Assert.assertTrue(
				tc.getJsonResponseElement(getCallResponse, "termsAndConditionsStatus").equalsIgnoreCase("ALERTED"));
		Assert.assertTrue(!tc.getJsonResponseElement(getCallResponse, "termsAndConditionsDate").isBlank());
		Assert.assertTrue(!tc.getJsonResponseElement(getCallResponse, "statusChangeDate").isBlank());
		log.info("The Details of retrievevd UserId is\n" + getCallResponse + "\n");
		logger.log(Status.PASS, "The Details of retrievevd User id\n" + getCallResponse);

		// get POST request parameters
		ArrayList<String> requestParameters = tc.getKeyValueFromRequestBody(requestBody);
		for (int i = 0; i < requestParameters.size(); i++) {
			Assert.assertEquals(getCallResponseParameters.get(i), requestParameters.get(i),
					"both request body and get call response parameters should match");
		}
		log.info("The Posted details are same as retrieved details");
		logger.log(Status.PASS, "The Posted details are same as retrieved details");

	}

	public void addCommentToTheRequestAndVerify(String Id, String path) throws InterruptedException {

		AddCommentsToTheRemoteHandsTicket comment = new AddCommentsToTheRemoteHandsTicket();
		String commentResponseBody = comment.addCommentsToTheExistingTicket(Id, path);
		GetRemoteHandsTicketDetails getobj = new GetRemoteHandsTicketDetails();
		String retrievePostedCommentResponse = getobj.getRequestDetailsById(Id, path);
		Thread.sleep(5000);
		System.out.println(retrievePostedCommentResponse.toString());
		JsonPath retreievedCommentsjsonpath = new JsonPath(retrievePostedCommentResponse.toString());
		JsonPath commentResponseBodyjsonpath = new JsonPath(commentResponseBody);
		//Assert.assertTrue(retreievedCommentsjsonpath.get("comments[0].ticketComment"),commentResponseBodyjsonpath.get("comment"));
		Assert.assertTrue(retrievePostedCommentResponse.contains(commentResponseBody));
		String expectedCommentInfo = extractCommentInfoFromGetCallResponse(retrievePostedCommentResponse);
		JSONAssert.assertEquals(expectedCommentInfo, commentResponseBody, true);
		logger.log(Status.PASS, "The Posted Comments details are same as retrieved details");

	}
	
	// Added by k.pavan

public void addCommentToTheSHRequestAndVerify(String Id, String path) throws InterruptedException {

	AddCommentsToTheRemoteHandsTicket comment = new AddCommentsToTheRemoteHandsTicket();
	String commentResponseBody = comment.addCommentsToTheExistingTicket(Id, path);
	GetRemoteHandsTicketDetails getobj = new GetRemoteHandsTicketDetails();
	String retrievePostedCommentResponse = getobj.getRequestDetailsById(Id, path);
	Thread.sleep(5000);
	System.out.println(retrievePostedCommentResponse.toString());
	retrievePostedCommentResponse=retrievePostedCommentResponse.replace("ticketComment","comment");
	Assert.assertTrue(retrievePostedCommentResponse.contains(commentResponseBody));
	String expectedCommentInfo = extractCommentInfoFromGetCallResponse(retrievePostedCommentResponse);
	JSONAssert.assertEquals(expectedCommentInfo, commentResponseBody, true);
	logger.log(Status.PASS, "The Posted Comments details are same as retrieved details");

}

	public void addAttachmentToTheRequestAndVerify(String Id, String path, String token) throws InterruptedException {

		String attachmentResponseBody = uploadAttachmentToTheExistingTicket(Id, token);
		String getCallresponse = getAttachmentDetailsByRequestId(Id, token);
		// Assert.assertTrue(getCallresponse.contains(attachmentResponseBody));
		String expectedAttachmentInfo = extractAttachmentInfoFromGetCallResponse(getCallresponse);
		// JSONAssert.assertEquals(expectedAttachmentInfo, attachmentResponseBody,
		// true);
		logger.log(Status.PASS, "The Posted Attachment details are same as retrieved details");

	}

	// attachments reusable method for FA
	public String getAttachmentDetailsByRequestId(String Id, String token) {

		logger.createNode("getAttachmentInfoById");
		String apiURI = domain + "/attachments/" + Id;
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "msirikonda@digitalrealty.com").get(apiURI);

		Assert.assertEquals(response.getStatusCode(), 200);
		// Assert.assertTrue(response.statusLine().contains("OK"));
		String allResponsebody = response.getBody().asString();

		log.info("Successfully extracted the Attachments Ticket Info ");
		logger.log(Status.PASS, "Successfully extracted theAttachments Ticket Info for " + Id);
		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response.getStatusLine());
		;

		return allResponsebody;

	}

	public String uploadAttachmentToTheExistingTicket(String Id, String Token) {

		logger.createNode("uploadAttachmentToTheExistingTicket");
		File testUploadFile = new File(
				System.getProperty("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Attachment.txt");

		String apiURI = domain + "/attachments?file_name=test&requestId=" + Id;
		System.out.println(apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.contentType("multipart/form-data").multiPart("file", testUploadFile)
				.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com").body(testUploadFile).post(apiURI).then().extract()
				.response();

		String responseBody = response.getBody().asString();
		System.out.println("Response is:" + responseBody);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.asString().contains("filename"));
		String actualFileName = getAttachmentFileInfoFromResponse(response.asString());
		Assert.assertEquals(actualFileName, "test");
		JSONObject object = new JSONObject(responseBody);
		String id = object.getString("id");
		String hash = object.getString("hash");
		String createdBy = object.getString("hash");
		String createdOn = object.getString("hash");
		Assert.assertTrue(!hash.isEmpty());
		Assert.assertTrue(!id.isEmpty());
		Assert.assertTrue(!createdBy.isEmpty());
		Assert.assertTrue(!createdOn.isEmpty());
		log.info("Successfully Posted attachment to the Existing Ticket ");
		logger.log(Status.INFO, "Posted Attachment to the Existing Ticket");

		return responseBody;

	}

	public static String getAttachmentFileInfoFromResponse(String responseBody) {

		JSONObject object = new JSONObject(responseBody);
		String ExpectedFilename = object.getString("filename");
		return ExpectedFilename;

	}

	public static String extractCommentInfoFromGetCallResponse(String responseBody) {

		JSONObject object = new JSONObject(responseBody);
		JSONArray array = object.getJSONArray("comments");
		JSONObject CommentInfo = array.getJSONObject(0);
		String commentInformation = CommentInfo.toString();

		return commentInformation;

	}
	

	public static String extractAttachmentInfoFromGetCallResponse(String responseBody) {

		JSONArray object = new JSONArray(responseBody);
		JSONObject AttachmentInfo = (JSONObject) object.get(0);
		String expectedAttachmentInfo = AttachmentInfo.toString();
		return expectedAttachmentInfo;

	}

	public void addAttachmentToTheRequestAndVerify(String Id, String path) throws InterruptedException, IOException {

		AddAttachmentsToTheRemoteHandsTicket attachment = new AddAttachmentsToTheRemoteHandsTicket();
		String attachmentResponseBody = attachment.uploadAttachmentToTheExistingTicket(Id);
		String getCallresponse = attachment.getAttachmentDetailsByRequestId(Id);
		// Assert.assertTrue(getCallresponse.contains(attachmentResponseBody));
		String expectedAttachmentInfo = extractAttachmentInfoFromGetCallResponse(getCallresponse);
		// JSONAssert.assertEquals(expectedAttachmentInfo, attachmentResponseBody,
		// true);
		logger.log(Status.PASS, "The Posted Attachment details are same as retrieved details");

	}

	public String getWONumberFromResponse(Response response) {

		String responseBody = response.asString();
		Assert.assertTrue(responseBody.contains("id"));
		TestUtilities tc = new TestUtilities();
		String WONumber = tc.getJsonResponseElement(responseBody, "id");
		return WONumber;

	}

	public String getNotificationIDFromResponse(Response response) {

		String responseBody = response.asString();
		Assert.assertTrue(responseBody.contains("notificationId"));
		TestUtilities tc = new TestUtilities();
		String NotifID = tc.getJsonvalueResponseElement(responseBody, "notificationId");
		return NotifID;

	}

	public String getWOIDFromResponse(Response response) {

		String responseBody = response.asString();
		Assert.assertTrue(responseBody.contains("workOrder"));
		TestUtilities tc = new TestUtilities();
		String WONumber = tc.getJsonfromResponseElement(responseBody);
		return WONumber;

	}

	public String getUserIdFromResponse(Response response) {

		String responseBody = response.asString();
		Assert.assertTrue(responseBody.contains("userId"));
		TestUtilities tc = new TestUtilities();
		String userId = tc.getJsonResponseElement(responseBody, "userId");
		return userId;

	}

	public String getIdFromResponse(Response response) {

		String responseBody = response.asString();
		Assert.assertTrue(responseBody.contains("id"));
		TestUtilities tc = new TestUtilities();
		String permissionGroupId = tc.getJsonResponseElement(responseBody, "id");
		return permissionGroupId;

	}

	public String getReportIdFromResponse(Response response) {

		String responseBody = response.asString();
		Assert.assertTrue(responseBody.contains("reportID"));
		TestUtilities tc = new TestUtilities();
		String reportID = tc.getJsonResponseElement(responseBody, "reportID");
		return reportID;

	}

	public void verifyErrorResponseMessage(String response, String message1, String message2) {

		JSONObject json = new JSONObject(response);
		JSONObject json1 = json.getJSONObject("error");
		String actualType = (String) json1.get("type");
		String actualMessage = json1.getString("message");
		// String actualCorrelationId = valueobject.getString("correlationId");
		// Assert.assertTrue(!actualCorrelationId.isBlank());
		Assert.assertEquals(actualType, message1);
		Assert.assertEquals(actualMessage, message2);

	}
	public void verify500ErrorResponseMessage(String response, String message1, String message2) {

		JSONObject json = new JSONObject(response);
//		JSONObject json1 = json.getJSONObject("error");
		String actualType = (String) json.get("error");
		String actualMessage = json.getString("message");
		// String actualCorrelationId = valueobject.getString("correlationId");
		// Assert.assertTrue(!actualCorrelationId.isBlank());
		Assert.assertEquals(actualType, message1);
		Assert.assertEquals(actualMessage, message2);

	}
	
	/*
	 * public void verifyErrorfromResponseMessage(String response, String
	 * message1,String message2) {
	 * 
	 * JSONObject json = new JSONObject(response); JSONObject json2 =
	 * json.getJSONObject("ErrorWrapper"); JSONObject valueobject =
	 * json2.getJSONObject("Errors"); JSONObject valueobject1 =
	 * valueobject.getJSONObject("Error"); JSONObject valueobject2 =
	 * valueobject1.getJSONObject("Error"); String actualType = (String)
	 * valueobject2.get("ReasonCode"); String actualMessage =
	 * valueobject2.getString("Description"); Assert.assertEquals(actualType,
	 * message1); Assert.assertEquals(actualMessage, message2); }
	 */
	  
	/*
	 * public void verifyErrorMessagefromResponse(String json1, String message1,
	 * String message2) {
	 * 
	 * JSONObject json = new JSONObject(json1); JSONObject xmlconvertedjson =
	 * XML.toJSONObject(json1); JSONObject json2 =
	 * xmlconvertedjson.getJSONObject("ErrorWrapper"); JSONObject valueobject =
	 * json2.getJSONObject("Errors"); JSONObject valueobject1 =
	 * valueobject.getJSONObject("Error"); JSONObject valueobject2 =
	 * valueobject1.getJSONObject("Error"); String actualType = (String)
	 * valueobject2.get("ReasonCode"); String actualMessage =
	 * valueobject2.getString("Description"); Assert.assertEquals(actualType,
	 * message1); Assert.assertEquals(actualMessage, message2);
	 * 
	 * }
	 */
	  
	  public void verifyErrorMessagefromResponse(String response, String expectedReasonCode, String expectedDescription) {
		  
		  XmlPath xmlPath = new XmlPath(response);
		  String actualReasonCode = xmlPath.get("ErrorWrapper.Errors.Error.Error.ReasonCode");
		  String actualDescription = xmlPath.get("ErrorWrapper.Errors.Error.Error.Description");
		  Assert.assertEquals(actualReasonCode, expectedReasonCode); 
		  Assert.assertEquals(actualDescription, expectedDescription);
	  }
	 

	

	public String getRequestBody(Map<String, String> data) {
		String[] keyNames;
		String message = null;
		JSONObject json = new JSONObject();
		if (data.get("methodName").contains("RemoteHandsTicket")) {
			String[] remoteHandsKeyNamesList = { "location", "site", "requestType", "category", "title",
					"detailedInstruction", "customerReference", "referenceTicket", "notificationRecipients" };// asset
			keyNames = remoteHandsKeyNamesList;
		} else if (data.get("methodName").contains("CustomerSupport")) {
			String[] CustomerSupportkeyNamesList = { "accountName", "site", "requestType", "location", "category",
					"title", "description", "customerReference", "notificationRecipients" };
			keyNames = CustomerSupportkeyNamesList;
		} else if (data.get("methodName").contains("CreateNewUser")) {
			String[] createNewUsersKeyNamesList = { "firstName", "lastName", "email", "phone", "position" };
			keyNames = createNewUsersKeyNamesList;
		} else if (data.get("methodName").contains("CreationOfReports")) {
			String[] ReportServicekeyNamesList = { "user_id", "ci", "location", "report_type", "yardi_id", "page",
					"search_query" };
			keyNames = ReportServicekeyNamesList;
		} else if (data.get("methodName").contains("CreatePermissionGroup")) {
			String[] PermissionGroupkeyNamesList = { "name" };
			keyNames = PermissionGroupkeyNamesList;
		} else if (data.get("methodName").contains("CreatePermissions")) {
			String[] PermissionskeyNamesList = { "name", "action", "permissionGroupId" };
			keyNames = PermissionskeyNamesList;
		} else if (data.get("methodName").contains("VisitorAccessRequest")) {
			String[] FacilityAccesskeyNamesList = { "visitorFirstName", "visitorLastName", "visitorEmail",
					"visitorPhone", "visitorType", "escortFirstName", "escortLastName", "visitorHostFirstName",
					"visitorHostLastName", "visitorHostCompany", "visitorBadgeStartDate", "visitorBadgeEndDate",
					"company", "notes", "emailNotifications", "isExtendedVisitorBadge", "standardVisitorBadgeStr",
					"extendedVisitorBadgeStr", "isBulkRequest", "site", "location", "accessStartDate", "accessEndDate",
					"details", "customerExternalReference", "contactType" };
			keyNames = FacilityAccesskeyNamesList;
		} else {
			String[] shipmentskeyNamesList = { "site", "title", "description", "courier", "trackingNumber",
					"estimatedDeliveryDate", "customerReference", "notificationRecipients", "packageCount",
					"isLoadingBayRequired" };
			keyNames = shipmentskeyNamesList;
		}
		for (int i = 0; i < keyNames.length; i++) {
			String name1 = keyNames[i];
			String value = data.get(name1);
//			if(name1.equals("visitorFirstName") || ("visitorLastName") || ("visitorEmail") || ("visitorPhone")))
//				if(name1.equals(value))

			if (value != null && !value.isEmpty()) {
				if (keyNames[i].contains("isLoadingBayRequired")) {
					json.put(keyNames[i], Boolean.valueOf(value));
				} else {
					json.put(keyNames[i], value);
				}
			}
			message = json.toString();
		}
		return message;
	}

	public String getRequestBodyWithDynamicData(Map<String, String> data) {
		String[] keyNames;
		String message = null;
		JSONObject json = new JSONObject();
		if (data.get("methodName").contains("RemoteHandsTicket")) {
			String[] remoteHandsKeyNamesList = { "location", "site", "requestType", "category", "title",
					"detailedInstruction", "customerReference", "referenceTicket", "notificationRecipients" };// asset
			keyNames = remoteHandsKeyNamesList;
		} else if (data.get("methodName").contains("CustomerSupport")) {
			String[] CustomerSupportkeyNamesList = { "accountName", "site", "requestType", "location", "category",
					"title", "description", "customerReference", "notificationRecipients" };
			keyNames = CustomerSupportkeyNamesList;
		} else if (data.get("methodName").contains("CreateNewUser")) {
			String[] createNewUsersKeyNamesList = { "firstName", "lastName", "email", "phone", "position" };
			keyNames = createNewUsersKeyNamesList;
		} else if (data.get("methodName").contains("CreationOfReports")) {
			String[] ReportServicekeyNamesList = { "user_id", "ci", "location", "report_type", "yardi_id", "page",
					"search_query" };
			keyNames = ReportServicekeyNamesList;
		} else if (data.get("methodName").contains("CreatePermissionGroup")) {
			String[] PermissionGroupkeyNamesList = { "name" };
			keyNames = PermissionGroupkeyNamesList;
		} else if (data.get("methodName").contains("CreatePermissions")) {
			String[] PermissionskeyNamesList = { "name", "action", "permissionGroupId" };
			keyNames = PermissionskeyNamesList;
		} else if (data.get("methodName").contains("CreateRoles")) {
			String[] RoleskeyNamesList = { "name", "globalUltimateKey", "permissionGroups", "privileges" };
			keyNames = RoleskeyNamesList;
		} else if (data.get("methodName").contains("CreateIdentityServerIDPToken")) {
			String[] RoleskeyNamesList = {};
			keyNames = RoleskeyNamesList;
		} else if (data.get("methodName").contains("getAccountsIDP")) {
			String[] RoleskeyNamesList = {};
			keyNames = RoleskeyNamesList;
		} else {
			String[] shipmentskeyNamesList = { "site", "title", "description", "courier", "trackingNumber",
					"estimatedDeliveryDate", "customerReference", "notificationRecipients", "packageCount",
					"isLoadingBayRequired" };
			keyNames = shipmentskeyNamesList;
		}
		for (int i = 0; i < keyNames.length; i++) {
			String name1 = keyNames[i];
			String value = data.get(name1);
			if ((value.equalsIgnoreCase("testFirstName") || value.equalsIgnoreCase("testLastName"))) {
				if (value != null && !value.isEmpty()) {
					json.put(keyNames[i], value + generateRandomDigits(5));
				}
			} else if (value.equalsIgnoreCase("testEmail")) {
				if (value != null && !value.isEmpty()) {
					json.put(keyNames[i], value + generateRandomDigits(5) + "@digitalrealty.com");
				}

			} else if (value.equalsIgnoreCase("automationPermissionGroupName")
					|| value.equalsIgnoreCase("automationPermissionName")
					|| value.equalsIgnoreCase("automationRoleName")
					|| value.equalsIgnoreCase("automationPermissionAction")) {
				if (value != null && !value.isEmpty()) {
					json.put(keyNames[i], value + generateRandomDigits(3));
				}

			}

			else {
				if (value != null && !value.isEmpty()) {
					json.put(keyNames[i], value);
				}
			}
//				json.put(keyNames[i], value);

			message = json.toString();
		}

		return message;

	}

	public String getRequestBodyForPermissions(Map<String, String> data, String permissionGroupId) {
		String[] keyNames;
		String message = null;
		JSONObject json = new JSONObject();
		if (data.get("methodName").contains("RemoteHandsTicket")) {
			String[] remoteHandsKeyNamesList = { "location", "site", "requestType", "category", "title",
					"detailedInstruction", "customerReference", "referenceTicket", "notificationRecipients" };// asset
			keyNames = remoteHandsKeyNamesList;
		} else if (data.get("methodName").contains("CustomerSupport")) {
			String[] CustomerSupportkeyNamesList = { "accountName", "site", "requestType", "location", "category",
					"title", "description", "customerReference", "notificationRecipients" };
			keyNames = CustomerSupportkeyNamesList;
		} else if (data.get("methodName").contains("CreateNewUser")) {
			String[] createNewUsersKeyNamesList = { "firstName", "lastName", "email", "phone", "position" };
			keyNames = createNewUsersKeyNamesList;
		} else if (data.get("methodName").contains("CreationOfReports")) {
			String[] ReportServicekeyNamesList = { "user_id", "ci", "location", "report_type", "yardi_id", "page",
					"search_query" };
			keyNames = ReportServicekeyNamesList;
		} else if (data.get("methodName").contains("CreatePermissionGroup")) {
			String[] PermissionGroupkeyNamesList = { "name" };
			keyNames = PermissionGroupkeyNamesList;
		} else if (data.get("methodName").contains("CreatePermissions")) {
			String[] PermissionskeyNamesList = { "name", "action", "permissionGroupId" };
			keyNames = PermissionskeyNamesList;
		} else {
			String[] shipmentskeyNamesList = { "site", "title", "description", "courier", "trackingNumber",
					"estimatedDeliveryDate", "customerReference", "notificationRecipients", "packageCount",
					"isLoadingBayRequired" };
			keyNames = shipmentskeyNamesList;
		}
		for (int i = 0; i < keyNames.length; i++) {
			String name1 = keyNames[i];
			String value = data.get(name1);
			if (value.equalsIgnoreCase("dummyPG")) {
				if (value != null && !value.isEmpty()) {
					json.put(keyNames[i], value.replaceAll(value, permissionGroupId));
				}
//				value.replaceAll(value, permissionGroupId);
			} else {
				if (value != null && !value.isEmpty()) {
					json.put(keyNames[i], value);
				}
			}
			message = json.toString();

		}

		return message;

	}

	public Response createRequest(String Token, String body, String URL) throws IOException {

		Response response = RestAssured.given().header("Authorization", Token)
		.header("Master-Account-Id", masterAccountID)
		 .header("Account-Id", accountID)
		.header("Content-Type", "application/json").body(body).post(URL).then().extract().response();
		return response;
		 }

	// FA requests
	public Response createFARequest(String Token, String body, String URL) throws IOException {

		Response response = RestAssured.given().header("Authorization", Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "vigneswarareddy@digitalrealty.com").body(body)
				.post(URL).then().extract().response();
		return response;
	}

	// Post Attachment Request
	public Response postAttachmentRequest(String token, String masterAccountID, String accountID, String apiURI,
			File testUploadFile) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + token)
				.contentType("multipart/form-data").multiPart("file", testUploadFile)
				.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com").body(testUploadFile).post(apiURI).then().extract()
				.response();
		return response;
	}

	// Post Request
	public Response postRequest(String token, String body, String masterAccountID, String accountID, String apiURI)
			throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(body).post(apiURI).then().extract().response();
		return response;
	}

	    // Post Request
		public Response putRequest(String token, String body, String masterAccountID, String accountID, String apiURI)
				throws IOException {

			Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + token)
					.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID).body(body).post(apiURI).then().extract().response();
			return response;
		}
	// Get Request
	public Response getRequest(String token, String masterAccountID, String accountID, String apiURI)
			throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).get(apiURI);
		return response;
	}

	public Headers sendMultipleHeaders() {
		Header masterAccount = new Header("Master-Account-Id", masterAccountID);
		Header accountId = new Header("Account-Id", accountID);
		Header userEmail = new Header("User-Email", "phantom.aao.dlr@gmail.com");
		Headers headers = new Headers(masterAccount, accountId, userEmail);
		return headers;

	}

	public Headers sendMultipleHeadersforGetAllAssets() {
		Header masterAccount = new Header("Master-Account-Id", masterAccountID);
		Header accountId = new Header("Account-Id", accountID);
		Header userEmail = new Header("User-Email", "phantom.aao.dlr@gmail.com");
		Headers headers = new Headers(masterAccount, accountId, userEmail);
		return headers;

	}

	public int generateRandomDigits(int n) {
		int m = (int) Math.pow(10, n - 1);
		return m + new Random().nextInt(9 * m);
	}

	// Re-usable Components for Negative Scenarios

}
