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
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GetAssets extends BaseClass{
	
	public static String path = "/assets";
	public static String path1 = "/assets/Names";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();

	//GAPI-5317-Retrieve Asset list by Customer using Account
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyAssetNames(Map<String, String> data) throws IOException, InterruptedException{
		
		String body ="{\r\n"
				+ "    \"assetIds\": [\r\n"
				+ "        \""+data.get("assetId")+"\"       \r\n"
				+ "    ]\r\n"
				+ "}";
		
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createAssetName(Tokenidp,body,"/names");
		System.out.println("THE RESPONSE IS****** "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("RESPONSE IS : "+response.asString());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	   // Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		log.info("Create Asset Name");
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String assetIdsInput = data.get("assetId");
		String assetNamesInput = data.get("assetName");
		System.out.println(assetNamesInput);
		String[] values=null;
		String[] names=null;
		if(assetIdsInput.contains(",")) {
			values = assetIdsInput.split(",");
			names = assetNamesInput.split(",");
			
		}
		if (response.getStatusCode()==200) {
			
			JSONArray array = new JSONArray(response.asString());
			System.out.println("rest:"+array);
			if(array.length()>1) {
				for (int i=1;i<array.length();i++) {
					JSONObject obj = array.getJSONObject(i);
				//	JSONArray value = obj.getJSONArray("content");
					String responseString = response.asString();
					String value = values[i].replace("\"", "");
					Assert.assertEquals(obj.get("assetName"), names[i]);
					Assert.assertEquals(obj.get("assetId"), value);
				}
			}
			else {
				Assert.assertEquals(array.getJSONObject(0).get("assetName"), assetNamesInput);
				Assert.assertEquals(array.getJSONObject(0).get("assetId"), assetIdsInput);
			}
			
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create New User request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		
	}
	
	// Retrieve all assets info
	@Test
	public void getAllAssetsInfo() throws InterruptedException, IOException {

		
		logger = extent.createTest("getAllAssetsInfo");
		//Headers headers = tc.sendMultipleHeadersforGetAllAssets();
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Tokenidp)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.get(idpdomain + path+"/all")
				.then().extract().response();
		System.out.println("status : "+response.statusLine());
		
		System.out.println("RESPONSE IS : "+response.asString());

		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		//Assert.assertTrue(response.statusLine().contains("OK"));
		
		JSONObject obj = new JSONObject(response.asString());
		JSONArray array = obj.getJSONArray("content");
		JsonPath responseJsonPath = response.jsonPath();
	    String AssetID=responseJsonPath.get("content[0].id").toString();
		String responseString = response.asString();
		//Assert.assertEquals(AssetID,"2ca74e8713123600a501b7a66144b0f4");
		//Assert.assertTrue(responseString.contains("2ca74e8713123600a501b7a66144b0f4"));
		for(int i=0; i<array.length();i++) {
			JSONObject individualObj = array.getJSONObject(i);
			Assert.assertEquals(individualObj.get("datasource"), "Insite");
			Assert.assertEquals(individualObj.get("status"), "Active");
			Assert.assertTrue(!individualObj.getString("id").isBlank());
						
		}
		
		
		log.info("Successfully extracted all Remote Hands Ticket Info ");
		logger.log(Status.INFO, "Successfully extracted all Remote Hands Ticket Info");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

	}
	
	// Retrieve locations details with filter(add in get assets)
	//GAPI-5395-Retrieve Locations for Customer using Valid account id and Invalid Site Code
	//GAPI-5394-Retrieve Locations for Customer using invalid account id and valid Site Code
	//GAPI-5397-Retrieve Locations for Customer using Valid account id &Site Code
	//GAPI-5391-Retrieve Locations for Customer using account id,Site Code&Type

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getLocationswithFilter(Map<String, String> data) throws InterruptedException, IOException {

				
				logger = extent.createTest("verifyGetLocationswithFilter");
				String apiURI = domain + "/locations?"+data.get("filters");
				System.out.println(apiURI);
				Headers headers = tc.sendMultipleHeadersforGetAllAssets();
				Response response = RestAssured.given().relaxedHTTPSValidation()
						.header("Authorization","Bearer "+Token)
						.header("Master-Account-Id",masterAccountID)
		 				.header("Account-Id",accountID)
		 				.header("User-Email","phantom.aao.dlr@gmail.com")
						.get(apiURI)
						.then().extract().response();

				logger.log(Status.INFO, response.statusLine());
				Assert.assertEquals(response.getStatusCode(), 200);
				//Assert.assertTrue(response.statusLine().contains("OK"));
				System.out.println(response.asString());
				//JSONObject obj = new JSONObject(response.asString());
				JSONArray array = new JSONArray(response.asString());
				//String responseString = response.asString();
				//Assert.assertTrue(responseString.contains("a4a74e8713123600a501b7a66144b0f7"));
				for(int i=0; i<array.length();i++) {
					JSONObject individualObj = array.getJSONObject(i);
//					Assert.assertEquals(individualObj.get("datasource"), "Insite");
//					Assert.assertEquals(individualObj.get("type"), "room");
//					Assert.assertEquals(individualObj.get("status"), "Active");
//					Assert.assertEquals(individualObj.get("sitecode"), "ORD10");
					Assert.assertTrue(response.asString().contains("id"));
					Assert.assertTrue(response.asString().contains("parentId"));
					Assert.assertTrue(response.asString().contains("location"));
					/*
					 * Assert.assertTrue(response.asString().contains("createTimestamp"));
					 * Assert.assertTrue(response.asString().contains("modifiedTimestamp"));
					 * Assert.assertTrue(response.asString().contains("assetName"));
					 * Assert.assertTrue(response.asString().contains("type"));
					 * Assert.assertTrue(response.asString().contains("legalEntityKey"));
					 * Assert.assertTrue(response.asString().contains("sitecode"));
					 * Assert.assertTrue(response.asString().contains("datasource"));
					 * Assert.assertTrue(response.asString().contains("status"));
					 * Assert.assertTrue(response.asString().contains("internalCompanyFlag"));
					 */
					
					//Assert.assertTrue(!individualObj.getString("id").isBlank());
								
				}
				
				
				log.info("Successfully extracted all Remote Hands Ticket Info ");
				logger.log(Status.INFO, "Successfully extracted all Remote Hands Ticket Info");
				logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

			}
		
	// Retrieve  asset info by asset id
		// *************************************************************************************
		
	    @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void getAssetInfoById(Map<String, String> data) throws InterruptedException, IOException {

			
			logger = extent.createTest("getAssetInfoById");
			String apiURI = idpdomain + path+"/all"+data.get("filters");
			System.out.println(apiURI);
			Headers headers = tc.sendMultipleHeaders();
			
			Response response = RestAssured.given().relaxedHTTPSValidation()
					  .header("Authorization","Bearer "+ Tokenidp)
					 .header("Content-Type", "application/json")
					 .header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "phantom.aao.dlr@gmail.com")
					.get(apiURI)
					.then().extract().response();

			logger.log(Status.INFO, response.statusLine());
			System.out.println(response.asString());
			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
			
			/*
			 * JSONObject obj = new JSONObject(response.asString()); JSONArray array =
			 * obj.getJSONArray("content"); String responseString = response.asString();
			 * Assert.assertTrue(responseString.contains("a4a74e8713123600a501b7a66144b0f7")
			 * ); for(int i=0; i<array.length();i++) { JSONObject individualObj =
			 * array.getJSONObject(i); Assert.assertEquals(individualObj.get("datasource"),
			 * "Insite"); Assert.assertEquals(individualObj.get("status"), "Active");
			 * Assert.assertTrue(!individualObj.getString("id").isBlank());
			 * 
			 * }
			 */
			
			
			log.info("Successfully extracted all Remote Hands Ticket Info ");
			logger.log(Status.INFO, "Successfully extracted all Remote Hands Ticket Info");
			logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

		}

	public static Response createAssetName(String Token, String body,String param) throws IOException {

		Headers headers = tc.sendMultipleHeaders();
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization","Bearer "+ Tokenidp)
				.headers(headers)
				.header("Content-Type", "application/json")
				.body(body)
				.post(idpdomain + path+param)
				.then().extract().response();
		
		return response;
	}

	}
