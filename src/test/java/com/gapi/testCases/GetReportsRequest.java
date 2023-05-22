package com.gapi.testCases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

public class GetReportsRequest extends BaseClass {
	static TestUtilities tc = new TestUtilities();
	String path = "/reports";

	@Test
	public void getReportsDetails() throws InterruptedException {

		logger = extent.createTest("getReportsInfo");
		String apiURI = idpdomain + path + "/request";
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", cmasterAccountID)
				.header("Account-Id", caccountID).get(apiURI);
		System.out.println("Response is: " + response.asString());
		System.out.println("Status Message is: " + response.statusLine());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));

		log.info("Successfully extracted all get Reports Info ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());
		logger.log(Status.INFO, "Successfully extracted getreports Info");

	}

	// Security Reports Sort By- Site
	//GAPI-5601-Get All Security Reports - Sort By- Site

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getReportsDetailsSortBySite(Map<String, String> data) throws InterruptedException, IOException {
		String path = "/reports";

		logger = extent.createTest(data.get("TestCaseName"));
		String body = "{\r\n  \"user_id\": \"" + data.get("") + "\",\r\n  \"ci\": [\r\n     \"" + data.get("")
				+ "\"\r\n  ]," + "\r\n  \"location\": \"" + data.get("") + "\"\",\r\n  \"report_type\": \""
				+ data.get("") + "\"\"," + "\r\n  \"yardi_id\": \"" + data.get("") + "\"\",\r\n  \"page\":"
				+ Integer.valueOf(data.get("page")) + "," + "\r\n  \"search_query\": \"\"\r\n}";
		System.out.println("Body is: " + body);
		String reportType = data.get("filters");
		String apiURI = domain + path + reportType;

		Response response = extractReports(Token, body, apiURI);

		System.out.println("Response is: " + response.asString());
		System.out.println("Status Message is: " + response.statusLine());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
		log.info("Successfully extracted all get Reports Info ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());
		logger.log(Status.INFO, "Successfully extracted getreports Info");
	}

	// SecurityPlannedReports using sortby
	//GAPI-5614-Get All SecurityPlanned Site  Maintenance Reports- Sort By - Report End Date
	//GAPI-5613-Get All SecurityPlanned Site  Maintenance Reports- Sort By - Report Start Date
	//GAPI-5612-Get All SecurityPlanned Site  Maintenance Reports- Sort By - Report Name
	//GAPI-5609-Get All SecurityPlanned Site  Maintenance Reports - Sort By- Site
	//GAPI-5610-Get All SecurityPlanned Site  Maintenance Reports -Sort By - Location

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getAllSecurityPlannedReportsMaintenanceSortBy(Map<String, String> data)
			throws InterruptedException, IOException {
		String path2 = "/work-orders";
		logger = extent.createTest(data.get("TestCaseName"));
		String body = "{\r\n    \"completed\": [\r\n        \"" + data.get("ci") + "\"\r\n    ]\r\n}";
		System.out.println("Body is: " + body);
		String reportType = data.get("filters");

		String apiURI = idpdomain + path2 + reportType;
		System.out.println("Status Message is: " + apiURI);
		Response response1 = extractReports(Tokenidp, body, apiURI);
		System.out.println("Response is: " + response1.asPrettyString());
		System.out.println("Status Message is: " + response1.statusLine());
		logger.log(Status.INFO, response1.statusLine());
		Assert.assertEquals(response1.getStatusCode(), 200);
		Assert.assertTrue(response1.statusLine().contains("HTTP/1.1 200"));
		log.info("Successfully extracted all get Reports Info ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response1.getStatusLine());
		logger.log(Status.INFO, "Successfully extracted getreports Info");
		System.out.println("===>" + response1.asString());

	}

	// Download Report - DCIM By invalid-ID --ARUL
	//GAPI-5617-Download Report - DCIM By invalid ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void invalidReportByDCIMID(Map<String, String> data) throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));

		String id = "CUS1234";
		String apiURI = idpdomain + "/reports/" + id + "/download";
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", cmasterAccountID)
				.header("Account-Id", caccountID).get(apiURI);
		System.out.println("Response Code is: " + response.getStatusCode());
		System.out.println("Response Message is: " + response.getStatusLine());
		Assert.assertEquals(response.getStatusCode(), 500);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 500"));
		log.info("Status Code and Status Message is" + response.getStatusLine());
		log.info("Successfully downloaded Reports by DCIM invalid ID");
		logger.log(Status.INFO, "Successfully downloaded reports by invalid DCIM ID ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());

	}

	// Download Report - Security By invalid-ID
	//GAPI-5619-Download Report - Security By invalid ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void invalidReportBySecurityID(Map<String, String> data) throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String id = "CUS1234";
		String apiURI = idpdomain + "/reports/" + id + "/download";
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", cmasterAccountID)
				.header("Account-Id", caccountID).get(apiURI);
		System.out.println("Response Code is: " + response.getStatusCode());
		System.out.println("Response Message is: " + response.getStatusLine());
		Assert.assertEquals(response.getStatusCode(), 500);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 500"));
		log.info("Status Code and Status Message is" + response.getStatusLine());
		log.info("Successfully downloaded Reports by invalid Security ID");
		logger.log(Status.INFO, "Cannot download reports by invalid Security ID ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());

	}

	// Report List filters
	//GAPI-5593-Report List - Security
	//GAPI-5592-Report List - DCIM

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getReportsDetailsusingFilters(Map<String, String> data) throws InterruptedException, IOException {

		logger = extent.createTest(data.get("TestCaseName"));
		String payLoad = cretaeReportsPayLoad(data);
		String reportType = data.get("filters");
		String apiURI = idpdomain + path + reportType;
		System.out.println("URI :" + apiURI);

		Response response = extractReports(Tokenidp, payLoad, apiURI);
		System.out.println("Response is: " + response.asString());
		System.out.println("Status Message is: " + response.statusLine());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));

		log.info("Successfully extracted all get Reports Info ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());
		logger.log(Status.INFO, "Successfully extracted getreports Info");

	}

	@Test
	public void getReportsWorkOrdersDetails() throws InterruptedException {

		logger = extent.createTest("getReportsWorkOrdersDetails");
		String apiURI = idpdomain + "/work-orders" + "/request";
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", cmasterAccountID)
				.header("Account-Id", caccountID).get(apiURI);
		System.out.println("Response is: " + response.asString());
		System.out.println("Status Message is: " + response.statusLine());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));

		log.info("Successfully extracted all get Reports Info ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());
		logger.log(Status.INFO, "Successfully extracted getreports Work Orders Info");

	}

	/*
	 * @Test public void getDownloadByReportURL() throws InterruptedException {
	 * 
	 * String report_id="CUS0057001";
	 * 
	 * logger = extent.createTest("getDownloadByReportURL"); String apiURI = domain
	 * + "/reports/" + report_id+"/download"; Response response =
	 * RestAssured.given().relaxedHTTPSValidation() .header("Authorization",
	 * "Bearer " + TokenVuat) .header("Content-Type", "application/json")
	 * .header("Master-Account-Id", 74834648) .header("Account-Id",
	 * "0012E00002dmMY7QAM") .get(apiURI); //System.out.println("Response is: " +
	 * response.asString()); //System.out.println("Status Message is: " +
	 * response.statusLine()); logger.log(Status.INFO, response.statusLine());
	 * System.out.println("Response Code is: " + response.getStatusCode());
	 * System.out.println("Response Message is: " + response.getStatusLine());
	 * Assert.assertEquals(response.getStatusCode(), 200);
	 * Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
	 * 
	 * log.info("Successfully downloaded Reports by ReportURL");
	 * logger.log(Status.INFO, "Status Code and Status Message is" +
	 * response.getStatusLine()); logger.log(Status.INFO,
	 * "Successfully downloaded getreports by Report URL ");
	 * 
	 * }
	 */
	// Download Report - DCIM By ID
	//GAPI-5616-Download Report - DCIM By ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getDownloadReportByDCIMID(Map<String, String> data) throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String payLoad = cretaeReportsPayLoad(data);
		String id1 = getIDBasedonReportTypeDCIM(payLoad, "DCIM","id");
		String apiURI = idpdomain + "/reports/" + id1 + "/download";
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", cmasterAccountID)
				.header("Account-Id", caccountID).get(apiURI);
		System.out.println("Response Code is: " + response.getStatusCode());
		System.out.println("Response Message is: " + response.getStatusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
		log.info("Status Code and Status Message is" + response.getStatusLine());
		log.info("Successfully downloaded Reports by DCIM ID");
		logger.log(Status.INFO, "Successfully downloaded reports by DCIM ID ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());

	}

	// Download Report - Security By ID
	//GAPI-5618-Download Report - Security By ID

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getDownloadReportBySecurityID(Map<String, String> data) throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String payLoad = cretaeReportsPayLoad(data);
		String id1 = getIDBasedonReporttypeSecurity(payLoad, "Security","id");
		String apiURI = idpdomain + "/reports/" + id1 + "/download";
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", cmasterAccountID)
				.header("Account-Id", caccountID).get(apiURI);
		System.out.println("Response Code is: " + response.getStatusCode());
		System.out.println("Response Message is: " + response.getStatusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
		log.info("Status Code and Status Message is" + response.getStatusLine());
		log.info("Successfully downloaded Reports by Security ID");
		logger.log(Status.INFO, "Successfully downloaded reports by Security ID ");
		logger.log(Status.INFO, "Status Code and Status Message is" + response.getStatusLine());

	}

	// Get All DCIM Reports- Sort By - Report Start Date,Name,Type,Location and Site
	//GAPI-5597-Get All DCIM Reports- Sort By - Report Name
	//GAPI-5594-Get All DCIM Reports - Sort By- Site
	//GAPI-5595-Get All DCIM Reports -Sort By - Location
	//GAPI-5598-Get All DCIM Reports- Sort By - Report Start Date
	//GAPI-5596-Get All DCIM Reports- Sort By - Report Type

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getDCIMReportsSortedWithDifferentFilters(Map<String, String> data)
			throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String payLoad = cretaeReportsPayLoad(data);
		String reportsFilters = data.get("filters");
		String apiURI = idpdomain + path +"?reportType=DCIM&" + reportsFilters;

		Response response = extractReports(Tokenidp, payLoad, apiURI);
		System.out.println("URI: " + response.getStatusLine());
		System.out.println("Response is: " + response.getBody().asString());
		JSONObject jsonObject = new JSONObject(response.getBody().asString());
		JSONArray contentArray = jsonObject.getJSONArray("content");
		String sortAttribute = reportsFilters.replace("?", "").split(",")[0];
		System.out.println("abc is: " + sortAttribute);
		List<String> actualList = new ArrayList<>();
		List<String> sortedList = new ArrayList<>();

		if (response.getStatusCode() == 200) {

			log.info("Extracted the DCIM Reports Successfully");
			logger.log(Status.PASS, "Extracted the DCIM Reports Successfully");

		} else {
			log.info("Extracted the DCIM Reports Successfully");
			logger.log(Status.FAIL, "Extracted the DCIM Reports Successfully");

		}

		for (int index = 0; index < contentArray.length(); index++) {
			JSONObject jObject = contentArray.getJSONObject(index);
       		System.out.println(jObject.getString(sortAttribute));
       //	      actualList.add(jObject.getString(sortAttribute));
			
				  if (sortAttribute.contains("name")) { String name =
				  jObject.getString(sortAttribute).split(" ")[0]; actualList.add(name); } else
				  { actualList.add(jObject.getString(sortAttribute)); }
				 
		}

		sortedList = actualList.stream().sorted().collect(Collectors.toList());
		System.out.println("Actual List" + actualList);
		System.out.println("Sorted List" + sortedList);
		if (actualList.equals(sortedList)) {
			log.info("DCIM Reports were sorted by " + sortAttribute);
			System.out.println("DCIM Reports were sorted by " + sortAttribute);
			logger.log(Status.PASS, "Successfully DCIM Reports were sorted by " + sortAttribute);
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
			logger.log(Status.INFO, "Status Code and Status Message is " + response.getStatusLine());
			Assert.assertTrue(true);
		} else {
			log.info("DCIM Reports were not sorted by " + sortAttribute);
			System.out.println("DCIM Reports were not sorted by " + sortAttribute);
			logger.log(Status.FAIL, "DCIM Reports were not sorted by " + sortAttribute);
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
			Assert.assertFalse(true);
		}

	}

	// Arul changes on 10-03-2023 Get All Security Reports- Sort By -
	// ReportID,Report End Date, Report Start Date,Report Name,Report Type
	//GAPI-5605-Get All Security Reports- Sort By - Report Start Date
	//GAPI-5607-Get All Security Reports- Sort By - ReportID
	//GAPI-5606-Get All Security Reports- Sort By - Report End Date
	//GAPI-5604-Get All Security Reports- Sort By - Report Name
	//GAPI-5603-Get All Security Reports- Sort By - Report Type


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void securityReportsSortedWithDifferentFilters(Map<String, String> data)
			throws InterruptedException, IOException {
		logger = extent.createTest(data.get("TestCaseName"));
		String payLoad = cretaeReportsPayLoad(data);
		String reportsFilters = data.get("filters");
		String apiURI = idpdomain + path +"?reportType=Security&" + reportsFilters;
		Response response = extractReports(Tokenidp, payLoad, apiURI);
		System.out.println("URI: " + response.getStatusLine());
		System.out.println("Response is: " + response.getBody().asString());
		JSONObject jsonObject = new JSONObject(response.getBody().asString());
		JSONArray contentArray = jsonObject.getJSONArray("content");
		String sortAttribute = reportsFilters.replace("?", "").split(",")[0];
		System.out.println("abc is: " + sortAttribute);
		List<String> actualList = new ArrayList<>();
		List<String> sortedList = new ArrayList<>();

		if (response.getStatusCode() == 200) {

			log.info("Extracted the Security Reports Successfully");
			logger.log(Status.PASS, "Extracted the Security Reports Successfully");

		} else {
			log.info("Extracted the Security Reports Successfully");
			logger.log(Status.FAIL, "Extracted the Security Reports Successfully");

		}

		for (int index = 0; index < contentArray.length(); index++) {
			JSONObject jObject = contentArray.getJSONObject(index);
			System.out.println(jObject.getString(sortAttribute));
			if (sortAttribute.contains("name")) {
				String name = jObject.getString(sortAttribute).split(" ")[0];
				actualList.add(name);
			} else {
				actualList.add(jObject.getString(sortAttribute));
			}

		}

		sortedList = actualList.stream().sorted().collect(Collectors.toList());
		System.out.println("Actual List" + actualList);
		System.out.println("Sorted List" + sortedList);
		if (actualList.equals(sortedList)) {
			log.info("Security Reports were sorted by " + sortAttribute);
			System.out.println("Security Reports were sorted by " + sortAttribute);
			logger.log(Status.PASS, "Successfully Security Reports were sorted by " + sortAttribute);
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
			logger.log(Status.INFO, "Status Code and Status Message is " + response.getStatusLine());
			Assert.assertTrue(true);
		} else {
			log.info("Security Reports were not sorted by " + sortAttribute);
			System.out.println("Security Reports were not sorted by " + sortAttribute);
			logger.log(Status.FAIL, "Security Reports were not sorted by " + sortAttribute);
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("HTTP/1.1 200"));
			Assert.assertFalse(true);
		}

	}

	// Re-usable method to create Pay Load.
	public String cretaeReportsPayLoad(Map<String, String> data) {
		
		String payLoad = "{\r\n \"userId\": \""+ data.get("user_id") +"\",\r\n    "
				+ "\"ci\": [\r\n \""+ data.get("ci") +"\"\r\n ],\r\n    "
				+ "\"location\": \""+ data.get("location") +"\",\r\n    "
				+ "\"yardiId\": \""+ data.get("yardi_id") +"\",\r\n    "
				+ "\"page\": "+ Integer.valueOf(data.get("page")) +",\r\n    "
				+ "\"search_query\": \"\"\r\n}";
		System.out.println("PayLoad is: " + payLoad);
		return payLoad;
	}
// + Integer.valueOf(data.get("page")) +
	// Re-usable method to get the ID based on type DCIM.
	public String getIDBasedonReportTypeDCIM(String payLoad, String type, String id) throws IOException {
		String apiURI = idpdomain + path +"?reportType=DCIM&";
		Response response = extractReports(Tokenidp, payLoad, apiURI);
		System.out.println("Response is: " + response.getBody().asString());
		JSONObject jsonObject = new JSONObject(response.getBody().asString());
		JSONArray contentArray = jsonObject.getJSONArray("content");
		for (int index = 0; index < contentArray.length(); index++) {
			JSONObject jObject = contentArray.getJSONObject(index);
			if (((String) jObject.get("type")).contains(type)) {
				id = jObject.getString("id");
				System.out.println("id is: " + id);
				break;
			}
		}
		return id;
		
	}
		
		// Re-usable method to get the ID based on type Security.
		public String getIDBasedonReporttypeSecurity(String payLoad, String type, String id) throws IOException {
			String apiURI = idpdomain + path +"?reportType=Security&";
			Response response = extractReports(Tokenidp, payLoad, apiURI);
			System.out.println("Response is: " + response.getBody().asString());
			JSONObject jsonObject = new JSONObject(response.getBody().asString());
			JSONArray contentArray = jsonObject.getJSONArray("content");
			for (int index = 0; index < contentArray.length(); index++) {
				JSONObject jObject = contentArray.getJSONObject(index);
				if (((String) jObject.get("type")).contains(type)) {
					id = jObject.getString("id");
					System.out.println("id is: " + id);
					break;
				}
			}
			return id;
		
	}

	/*
	 * String apiURI1 = idpdomain + path; Response response =
	 * extractReports(Tokenidp, payLoad, apiURI);
	 */
	// Reusable Method to extract Reports
	public static Response extractReports(String token, String payLoad, String apiURI) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(payLoad).post(apiURI).then().extract().response();

		return response;
		
		
	}
	
}