package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreateFAVisitoraccess extends BaseClass {
	public static String path="/facility-access/visitors";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	
	// create Visitor Access Ticket with all possible combinations
	//GAPI-6253-Visitor Access - Create

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateVisitorAccessRequest(Map<String, String> data) throws IOException, InterruptedException{
		
		logger = extent.createTest("verifyCreateVisitorAccessRequest");
		
		JSONArray mainarray = new JSONArray();
		JSONObject mainObject = new JSONObject();
		JSONArray visitorsArray = new JSONArray();
		JSONObject visitorsObject = new JSONObject();
		
		visitorsObject.put("visitorFirstName", "john");
		visitorsObject.put("visitorLastName", "test");
		visitorsObject.put("visitorEmail", "test@test.com");
		visitorsObject.put("visitorPhone", "919876543210");
		visitorsObject.put("company", "");
		visitorsArray.put(visitorsObject);
		mainObject.put("visitors", visitorsArray);
		mainObject.put("visitorType", "false");
		mainObject.put("escortFirstName", "test5");
		mainObject.put("escortLastName", "test5");
		mainObject.put("visitorHostFirstName", "hello");
		mainObject.put("visitorHostLastName", "test8");
		mainObject.put("visitorHostCompany", "civil");
		mainObject.put("visitorBadgeStartDate", "2023-01-07");
		mainObject.put("visitorBadgeEndDate", "2023-01-08");
		mainObject.put("company", "Zayo�Group,�LLC");
		mainObject.put("notes", "unknow test");
		mainObject.put("emailNotifications", "test1@forvisitor.com,test2@forvisitor.com,test3@forvisitor.com");
		mainObject.put("isExtendedVisitorBadge", false);
		mainObject.put("standardVisitorBadgeStr", "Visitor�Access�(Escorted)");
		mainObject.put("extendedVisitorBadgeStr", "Visitor�Access�(Escorted)");
		JSONArray sitesArray = new JSONArray();
		JSONObject sitesObject = new JSONObject();
		sitesObject.put("site", "IAD039");
		JSONArray locationArray = new JSONArray();
		JSONObject locationObject = new JSONObject();
		locationObject.put("location", "S106-3A Storage");
		locationObject.put("accessStartDate", "2023-01-07");
		locationObject.put("accessEndDate", "2023-01-08");
		locationObject.put("accessStartTime", "06:22:00");
		locationObject.put("accessEndTime", "06:22:00");
		locationArray.put(locationObject);
		sitesObject.put("locations", locationArray);
		sitesArray.put(sitesObject);
		mainObject.put("sites", sitesArray);
		mainObject.put("isBulkRequest", false);
		mainarray.put(mainObject);
		System.out.println(mainarray.toString());
		System.out.println("Body is------ :");
		String apiURI = domain + path;
		System.out.println(apiURI);
		
		String jsonbody=mainarray.toString();
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id",accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(jsonbody)
				.post(domain + path)
				.then().extract().response();
		System.out.println("Response is : "+response.asString());
}}