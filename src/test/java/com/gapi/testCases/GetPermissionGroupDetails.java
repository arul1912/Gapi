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

public class GetPermissionGroupDetails extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/permission-groups";

	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	  
	
	 
	  // public String getPermissionGroupInfoByGroupId(String id,String path) {
	  //GAPI-5558-PG - Retrieve PermissionGroups details by - Valid PermissionGroupsID

		public void getPermissionGroupInfoByGroupId(Map<String, String> data) throws InterruptedException, IOException {
			
		    //logger.createNode("getPermissionGroupInfoByGroupId");
		    logger = extent.createTest(data.get("TestCaseName"));
			String apiURI = idpdomain+path;
			System.out.println(apiURI);
			Response response =  response = getpermissions(Tokenidp,apiURI);     		     	
			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
	 		String allResponsebody = response.getBody().asString();
	 		System.out.println(allResponsebody);
	 		JSONObject object = new JSONObject(allResponsebody);
	 		JSONArray requestArray = object.getJSONArray("content");
	 		String Id = requestArray.getJSONObject(0).getString("id");
	 		String apiURI1 = idpdomain+path+"/"+Id;
			System.out.println(apiURI1);
			Response response1 =  response = getpermissions(Tokenidp,apiURI);     		     	
			Assert.assertEquals(response1.getStatusCode(), 200);
	 		log.info("Successfully extracted Permission Group Details Info ");
	 		logger.log(Status.PASS, "Successfully extracted Permission Group Details Info" );
	 		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response1.getStatusLine());

		}
	
	
		////GAPI-5546-PM -  Retrieve all Permission info by filtering with PermissionGroupID
	  @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	  // public String getPermissionGroupInfoByGroupId(String id,String path) {
		public void createPermissionGroupInfoByGroupId(Map<String, String> data) throws InterruptedException, IOException {
				
			    //logger.createNode("getPermissionGroupInfoByGroupId");
		    logger = extent.createTest(data.get("TestCaseName"));
		    String getPermissionname = data.get("filters");
			String apiURI = idpdomain+path;
			System.out.println(apiURI);
			Response response =  response = getpermissions(Tokenidp,apiURI);     		     	
			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
	 		String allResponsebody = response.getBody().asString();
	 		System.out.println(allResponsebody);
	 		JSONObject object = new JSONObject(allResponsebody);
	 		JSONArray requestArray = object.getJSONArray("content");
	 		String Id = requestArray.getJSONObject(0).getString("id");
	 		String apiURI1 = idpdomain+path+"/"+Id;
			System.out.println(apiURI1);
			Response response1 =  response = getpermissions(Tokenidp,apiURI);
			JSONObject object1 = new JSONObject(allResponsebody);
	 		JSONArray requestArray1 = object.getJSONArray("content");
	 		String name = requestArray.getJSONObject(0).getString("name");
	 		Assert.assertEquals(name,getPermissionname);
			Assert.assertEquals(response1.getStatusCode(), 200);
	 		log.info("Successfully extracted Permission Group Details Info ");
	 		logger.log(Status.PASS, "Successfully extracted Permission Group Details Info" );
	 		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response1.getStatusLine());
			}

	  //Re-usable method for get permissions
		public static Response getpermissions(String Token, String apiURI) throws IOException {

			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization","Bearer "+Token)
	 				.header("Content-Type", "application/json") 
	 				.header("Master-Account-Id",masterAccountID)
					.header("Account-Id",accountID)
					.header("User-Email","admin@digitalrealty.com")
					//.header("Correlation-Id","dbb28da7-2cda-477c-b76e-ecce1bd1a056")
	 				.get(apiURI);

			return response;
		}
}