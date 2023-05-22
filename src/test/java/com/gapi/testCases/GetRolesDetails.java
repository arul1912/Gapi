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

public class GetRolesDetails extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	String path = "/roles";

	
	//GAPI-5452-ROLES -  Retrieve all Roles info - By Size
	//GAPI-5450-ROLES -  Retrieve all Roles info - By Sort
	//GAPI-5451-ROLES -  Retrieve all Roles info - By Page
	//GAPI-5454-ROLES -  Retrieve all Roles info - By Page&Size
	//GAPI-5448-ROLES -  Retrieve all Roles info
	//GAPI-5453-ROLES -  Retrieve all Roles info - By globalUltimateKey&Sort
	//GAPI-5455-ROLES -  Retrieve all Roles info - By globalUltimateKey,Sort,Page&Size
	//GAPI-5449-ROLES -  Retrieve all Roles info - By valid Global Ultimate Key


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getallRolesinfo(Map<String, String> data) throws InterruptedException, IOException {
		System.out.println(data.get("TestCaseName"));
		logger = extent.createTest(data.get("TestCaseName"));
		String filters = data.get("filters");
		String apiURI = idpdomain + path + filters;
		System.out.println(apiURI);
		
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.get(apiURI)
				.then().extract().response();
		System.out.println("Response is: " + response.asString());
		System.out.println("Status Message is: " + response.statusLine());
		logger.log(Status.INFO, response.statusLine());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualstatuscode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
		logger.log(Status.INFO, "Successfully extracted Roles Info" );

	}
//************************************************************************
	//GAPI-5443-ROLES - Retrieve a Roles details by - Valid Role ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getRolesinfoByValidRoleId(Map<String, String> data)
	{
		System.out.println(data.get("TestCaseName"));
		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = idpdomain + path;
		Response response =  getRoles(Tokenidp,apiURI);
		String responseBody = response.asString();
		JSONObject object = new JSONObject(responseBody);
		JSONArray requestArray = object.getJSONArray("content");
		String roleId = requestArray.getJSONObject(0).getString("id");
		System.out.println("The roleId is : "+roleId);
		logger.log(Status.INFO, "The roleId is : "+roleId);
		String apiURI1 = idpdomain + path +"/" + roleId;
		Response response1 =  getRoles(Tokenidp,apiURI1);
		System.out.println("Response is: " + response1.asString());
		System.out.println("Status Message is: " + response1.statusLine());
		logger.log(Status.INFO, response1.statusLine());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualstatuscode = response.getStatusCode();
		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
		Assert.assertTrue(response1.statusLine().contains("HTTP/1.1 200"));
		logger.log(Status.INFO, "Successfully extracted Roles Info By ID" );	
	}
  
	
	 
	   public String getRolesInfoByRoleId(String id,String path) {
			
		    logger.createNode("getRolesInfoByRoleId");
			String apiURI = domain+path+"/" + id;
			System.out.println(apiURI);
			Response response = RestAssured.given()
					.header("Authorization",Token)
	 				.header("Content-Type", "application/json") 
	 				.header("Master-Account-Id",3)
					.header("Account-Id",3)
					.header("User-Email","admin@digitalrealty.com")
					.header("Correlation-Id","7de402f3-7721-4e8a-ae06-0579f21893d2")
	 				.get(apiURI);      		     	

			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
	 		String allResponsebody = response.getBody().asString();
	 		
	 		log.info("Successfully extracted Roles Details Info ");
	 		logger.log(Status.PASS, "Successfully extracted Roles Details Info" );
	 		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response.getStatusLine());
	 		
				
			
			return allResponsebody;
		}
	   
	   //GAPI-5457-ROLES - Retrieve the Privileges info by Valid RoleID

	   @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getPrivilegesinfoByValidRoleId(Map<String, String> data)
		{
			System.out.println(data.get("TestCaseName"));
			logger = extent.createTest(data.get("TestCaseName"));
			String apiURI = idpdomain + path;
			Response response =  getRoles(Tokenidp,apiURI);
			String responseBody = response.asString();
			JSONObject object = new JSONObject(responseBody);
			JSONArray requestArray = object.getJSONArray("content");
			String roleId = requestArray.getJSONObject(0).getString("id");
			System.out.println("The roleId is : "+roleId);
			logger.log(Status.INFO, "The roleId is : "+roleId);
			String apiURI1 = idpdomain + path +"/" + roleId+"/" + "privileges";
			Response response1 =  getRoles(Tokenidp,apiURI1);
			System.out.println("Response is: " + response1.asString());
			System.out.println("Status Message is: " + response1.statusLine());
			logger.log(Status.INFO, response1.statusLine());
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualstatuscode = response.getStatusCode();
			Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
			Assert.assertTrue(response1.statusLine().contains("HTTP/1.1 200"));
			logger.log(Status.INFO, "Successfully extracted Privileges Info By ID" );
			
		}
	   
	 //********************************Retrieve a Roles details by - Invalid Role ID****************************************
	 //GAPI-5444-ROLES - Retrieve a Roles details by -In valid Role ID

	   @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	   	public void getRolesinfoByInvalidRoleId(Map<String, String> data)
	   	{
	   		System.out.println(data.get("TestCaseName"));
	   		logger = extent.createTest(data.get("TestCaseName"));
	   		String apiURI = idpdomain + path;
	   		Response response =  getRoles(Tokenidp,apiURI);
	   		String responseBody = response.asString();
	   		JSONObject object = new JSONObject(responseBody);
	   		JSONArray requestArray = object.getJSONArray("content");
	   		String roleId = requestArray.getJSONObject(0).getString("id");
	   		String invalidId = roleId+"abc";
	   		String apiURI1 = idpdomain + path +"/" + invalidId;
	   		System.out.println("The Invalid roleId is : "+invalidId);
	   		logger.log(Status.INFO, "The Invalid roleId is : "+invalidId);
	   		Response response1 =  getRoles(Tokenidp,apiURI1);
	   		System.out.println("Response is: " + response1.asString());
	   		System.out.println("Status Message is: " + response1.statusLine());
	   		logger.log(Status.INFO, response1.statusLine());
	   		String expectedStatusCode = data.get("expectedStatusCode");
	   		int actualstatuscode = response1.getStatusCode();
	   		Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
	   		Assert.assertTrue(response1.statusLine().contains("HTTP/1.1 400"));
	   		logger.log(Status.INFO, "Bad request was submitted" );
	   	}



	   //*************************************Retrieve the Privileges info by Invalid RoleID*************************************************************
	   //GAPI-5458-ROLES - Retrieve the Privileges info by Invalid RoleID

	    @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	   		public void getPrivilegesinfoByInvalidRoleId(Map<String, String> data)
	   		{
	   			System.out.println(data.get("TestCaseName"));
	   			logger = extent.createTest(data.get("TestCaseName"));
	   			String apiURI = idpdomain + path;
	   			Response response =  getRoles(Tokenidp,apiURI);
	   			String responseBody = response.asString();
	   			JSONObject object = new JSONObject(responseBody);
	   			JSONArray requestArray = object.getJSONArray("content");
	   			String roleId = requestArray.getJSONObject(0).getString("id");
	   			String invalidId = roleId+"abc";
	   			String apiURI1 = idpdomain + path +"/" + invalidId+"/" + "privileges";
	   			System.out.println("The Invalid roleId is : "+invalidId);
	   			logger.log(Status.INFO, "The Invalid roleId is : "+invalidId);
	   			Response response1 =  getRoles(Tokenidp,apiURI1);
	   			System.out.println("Response is: " + response1.asString());
	   			System.out.println("Status Message is: " + response1.statusLine());
	   			logger.log(Status.INFO, response1.statusLine());
	   			String expectedStatusCode = data.get("expectedStatusCode");
	   			int actualstatuscode = response1.getStatusCode();
	   			Assert.assertEquals(String.valueOf(actualstatuscode), expectedStatusCode);
	   			Assert.assertTrue(response1.statusLine().contains("HTTP/1.1 400"));
	   			logger.log(Status.INFO, "Bad request was submitted" );
	   			
	   		}
	   
	 //****************************************Re-usable method**********************************************************
	  
	   public static Response getRoles(String Token,String apiURI)
	   	{
	   		Response response = RestAssured.given().relaxedHTTPSValidation()
	   				.header("Authorization", "Bearer " + Token)
	   				.header("Content-Type", "application/json")
	   				.header("Master-Account-Id", masterAccountID)
	   				.header("Account-Id", accountID)
	   				.get(apiURI)
	   				.then().extract().response();
	   		
	   		return response;
	   		}

}