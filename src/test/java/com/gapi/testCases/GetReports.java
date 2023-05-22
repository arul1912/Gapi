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
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GetReports extends BaseClass{
	static TestUtilities tc = new TestUtilities();
	String path = "/reports";
	 
	   public String getReportsInformationByID(String id,String path) {
			
		    logger.createNode("getReportsInformationByID");
			String apiURI = domain+path+"/" + id;
			System.out.println(apiURI);
			   Response response = RestAssured.given().relaxedHTTPSValidation()
	                    .header("Authorization","Bearer "+Token)
	                    .header("Content-Type", "application/json")
	                    .header("Master-Account-Id",masterAccountID)
	                    .header("Account-Id",accountID)
	 				.get(apiURI);      		     	
			Assert.assertEquals(response.getStatusCode(), 200);
			//Assert.assertTrue(response.statusLine().contains("OK"));
	 		String allResponsebody = response.getBody().asString();
	 		JsonPath jsonPath = new JsonPath(allResponsebody);
			String reportType = jsonPath.getString("type");
			String reportURL = jsonPath.getString("url");
			String reportName=jsonPath.getString("name");
	 		logger.log(Status.PASS, "GET Call retrieved Reportid:: "  +id+ "Report Name:: "  +reportName+ "Report Type::" +reportType+ "Report URL::" +reportURL );
			Assert.assertTrue(response.asString().contains("name")); 
	 		Assert.assertTrue(response.asString().contains("type"));
			Assert.assertTrue(response.asString().contains("url"));
			log.info("Successfully extracted Reports Details Info ");
	 		logger.log(Status.PASS, "Successfully extracted Reports Details Info" );
	 		logger.log(Status.PASS, "GET Call Status Code and Status Message is" + response.getStatusLine());
	 		return allResponsebody;
		}
	 //Report List - Planned Site Maintenance Work Orders
	    //GAPI-5608-Report List - Planned Site Maintenance Work Orders

	    @Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	    public void reportListPlannedReportsMaintenance (Map<String, String> data) throws InterruptedException, IOException {
	         String path2 = "/work-orders";
	    	logger = extent.createTest(data.get("TestCaseName"));
	    	  String body = "{\r\n    \"completed\": [\r\n        \""+data.get("ci")+"\"\r\n    ]\r\n}";
	    	  System.out.println("Body is: " + body);
	    	//String reportType = data.get("filters");
	    
	        String apiURI = domain + path2 + body ;
	        System.out.println("Status Message is: " + apiURI);
	        Response response1 = tc.postRequest(Token, body, masterAccountID, accountID, apiURI);
	        System.out.println("Response is: " + response1.asPrettyString());
	        System.out.println("Status Message is: " + response1.statusLine());
	        logger.log(Status.INFO, response1.statusLine());
	        Assert.assertEquals(response1.getStatusCode(), 200);
	        Assert.assertTrue(response1.statusLine().contains("HTTP/1.1 200"));
	        log.info("Successfully extracted all get Reports Info ");
	        logger.log(Status.INFO, "Status Code and Status Message is" + response1.getStatusLine());
	        logger.log(Status.INFO, "Successfully extracted getreports Info");
	        System.out.println("===>"+response1.asString());

	       
	    }
}