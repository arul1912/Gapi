package com.gapi.testCases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CreationOfReports extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	public static String path = "/reports";
	static String date = tc.getCurrentDateAndTime();


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void CreationOfReports(Map<String, String> data) throws InterruptedException, IOException {
			
		int page=0;
		String body = "  {\r\n" +
				"\"user_id\": \""+data.get("user_id")+"\",\r\n" +
				" \"ci\": ["+data.get("ci")+"],\r\n" +
				" \"location\": \""+data.get("location")+"\",\r\n" +
				" \"report_type\": \""+data.get("report_type")+"\",\r\n" +
				" \"yardi_id\": \""+data.get("yardi_id")+"\",\r\n" +
				" \"page\": "+page+",\r\n"+				
				" \"search_query\": \""+data.get("search_query")+"\"\r\n" +
				"    }\r\n";
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createNewReport(Token,body);
		System.out.println(response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		System.out.println(expectedStatusCode);
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	  //  Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode()==200) {
			JSONArray jsonArray=new JSONArray(response.asString());
			Assert.assertTrue(jsonArray.length() > 0);
			for(int i=0;i<=jsonArray.length()-1;i++) {
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			logger.log(Status.PASS, "Generated Report ID at " +i+ "::index position" + jsonObject);
			}
			Assert.assertTrue(response.asString().contains("id"));
			JSONObject object1 = jsonArray.getJSONObject(0);
			String firstReportID= object1.getString("id");
			System.out.println(firstReportID);
			log.info("First generated report id is " + firstReportID);
			logger.log(Status.PASS, "First generated Report ID is  " + firstReportID);
			logger.log(Status.PASS, "Successfully Created Report");
			GetReports gc=new GetReports();
			gc.getReportsInformationByID(firstReportID, path);
			}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create New Report");
			System.out.println(response.asString());
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}		
		}
	// Re-usable method

		public static Response createNewReport(String Token, String body) throws IOException {

			   Response response = RestAssured.given().relaxedHTTPSValidation()
	                    .header("Authorization","Bearer "+Token)
	                    .header("Content-Type", "application/json")
	                    .header("Master-Account-Id",masterAccountID)
	                    .header("Account-Id",accountID)
	                    .body(body)
						.post(domain + path)
						.then().extract().response();
			
			return response;
		}


}
