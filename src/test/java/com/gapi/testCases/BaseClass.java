package com.gapi.testCases;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.gapi.utilities.ReadConfig;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class BaseClass {
	
	public Map<String, String> testData;
    static TestUtilities tc = new TestUtilities();

	public static WebDriver driver;
	public static Logger log;
	public static String Token;
	public static String Tokenidp;// ="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImRDbkpzTk1maHU0R25mMUhlVzRIdyJ9.eyJodHRwczovL2FwaS5kaWdpdGFscmVhbHR5LmNvbS9hY2NvdW50X25hbWUiOiJUZWxYIEdyb3VwLCBJbmMuIiwiaHR0cHM6Ly9hcGkuZGlnaXRhbHJlYWx0eS5jb20vdXNlcl9lbWFpbCI6IlBoYW50b20uYWFvLmRsckBnbWFpbC5jb20iLCJpc3MiOiJodHRwczovL2Rscmdsb2JhbC1kZXYudXMuYXV0aDAuY29tLyIsInN1YiI6IkFoQjhDZElPb0VLTUVvSlpPTkk0QW5jdDM2bloyUzFMQGNsaWVudHMiLCJhdWQiOiJodHRwczovL2FwaS1kZXYuZGlnaXRhbHJlYWx0eS5jb20iLCJpYXQiOjE2NDY5MjYyMjgsImV4cCI6MTY0NzAxMjYyOCwiYXpwIjoiQWhCOENkSU9vRUtNRW9KWk9OSTRBbmN0MzZuWjJTMUwiLCJzY29wZSI6Imdsb2JhbF9hcGkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.tZtxb-bgaLLOviYBPdIZ9Tj0yF06e5Q0HzVn-hfKFT5Zjtr4jvwQ-CW-LRIVzGwoqLe_ym8oh_162j64_NNv5KsiDKkqnEEpaLFUPJIP-ArBcws94kIoX5BQJFIil5P7OqPPij0QEcRZHdbrwfoyFvmR_sOI-YAQzCjZGI9GVIYYwwrlbO4uWQ33PLho7OLCBqKmddRtlnstDF56OjNzt-nO16Va7bfBa77cV4-x-bkf-Emr6itdctSQ_tkZqh9Y7gB--LhgXbDddySwMHAY3wV6_x6iB_xrabXrRP9STWxbgclKpkWK78-QIYPb81OtWIhbkLLb0DXfC04LV0HTQA";
	//public static String TokenVuat;
	public static String TokenCC;
	public static String approveRejectToken;
	public static String createToken;
	public static String createOSPToken;
	public static String createApigeeToken;
	public static ReadConfig readconfig = new ReadConfig();
	static String CCdomain = readconfig.getConfigValue("CC_HostName");
	protected static String domain = readconfig.getConfigValue("HostName");
	static String idpdomain;
	static String iscgadomain;	
	static String excelPath = System.getProperty("user.dir") + "/src/test/java/com/gapi/testData/TestDataSheet.xlsx";
	static String excelPath1 = System.getProperty("user.dir") + "/src/test/java/com/gapi/testData/TestData.xlsx";
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	public static ExtentTest logger;
	public static String accountID;
	public static String masterAccountID;
	public static String caccountID;
	public static String cmasterAccountID;
	 
	public static String environement;

	public BaseClass() {

		log = Logger.getLogger("GAPI");
		PropertyConfigurator.configure("log4j.properties");
		
		
	}

	// Generating Authorization Token
	@org.testng.annotations.BeforeSuite
	public void generateAuthorizationToken() throws InterruptedException {
		environement = readconfig.getConfigValue("Environment");
		switch (environement) {
		
		case "UAT":
			String tokenApiURI = readconfig.getConfigValue("AuthorizationTokenURI");
			String client_id = readconfig.getConfigValue("ClientId");
			String client_secret = readconfig.getConfigValue("ClientSceret");

			Token = generateToken(client_id, client_secret, tokenApiURI);
			Thread.sleep(3000);
			Token.trim();
			log.info("Generated the authorization Token");

			String tokenApiURIIDP = readconfig.getConfigValue("IDP-AuthorizationTokenURI");
			String client_ididp = readconfig.getConfigValue("IDP-ClientId");
			String client_secretidp = readconfig.getConfigValue("IDP-ClientSceret");
			Tokenidp = generateToken(client_ididp, client_secretidp, tokenApiURIIDP);
			Thread.sleep(3000);
			Tokenidp.trim();
			log.info("Generated the IDP authorization Token");			
			String CCtokenApi = readconfig.getConfigValue("CC-AuthorizationTokenURI");
			String CCclient_id = readconfig.getConfigValue("CC-ClientId");
			String CCclient_secret = readconfig.getConfigValue("CC-ClientSceret");
			TokenCC  = generateToken(CCclient_id, CCclient_secret, CCtokenApi);
			Thread.sleep(3000);
			TokenCC.trim();
			log.info("Generated the authorization Token");
			
			accountID = readconfig.getConfigValue("AccountId_UAT");
			masterAccountID = readconfig.getConfigValue("MasterAccountID_UAT");			
			domain = readconfig.getConfigValue("HostName_UAT");
			idpdomain = readconfig.getConfigValue("IDP-HostName");		
			
			break;
			
		case "TEST":
			
			tokenApiURI= readconfig.getConfigValue("AuthorizationTokenURI_TEST_USER1");
			client_id = readconfig.getConfigValue("ClientId_TEST_USER1");
			client_secret = readconfig.getConfigValue("ClientSceret_TEST_USER1");
			Token = generateToken(client_id, client_secret, tokenApiURI);
			Thread.sleep(3000);
			Token.trim();
			log.info("Generated the authorization Token");
			
			tokenApiURIIDP = readconfig.getConfigValue("IDP-AuthorizationTokenURI_TEST");
			client_ididp = readconfig.getConfigValue("IDP-ClientId_TEST");
			client_secretidp = readconfig.getConfigValue("IDP-ClientSceret_TEST");
			Tokenidp = generateToken(client_ididp, client_secretidp, tokenApiURIIDP);
			Thread.sleep(3000);
			Tokenidp.trim();
			log.info("Generated the authorization Token");	
			
			accountID = readconfig.getConfigValue("AccountId_TEST");
			masterAccountID = readconfig.getConfigValue("MasterAccountID_TEST");			
			domain = readconfig.getConfigValue("HostName_TEST");
			idpdomain = readconfig.getConfigValue("HostName_IDP_TEST");
			 iscgadomain = readconfig.getConfigValue("HostName_ISCGA_TEST");
			 CCtokenApi = readconfig.getConfigValue("CC-AuthorizationTokenURI");
			 CCclient_id = readconfig.getConfigValue("CC-ClientId");
			 CCclient_secret = readconfig.getConfigValue("CC-ClientSceret");
			TokenCC  = generateToken(CCclient_id, CCclient_secret, CCtokenApi);
			Thread.sleep(3000);
			TokenCC.trim();
			log.info("Generated the authorization Token");
			
			caccountID = readconfig.getConfigValue("CC_Account-Id");
			cmasterAccountID = readconfig.getConfigValue("CC_MasterAccount-Id");			
			
			
			String client_id_Approve = readconfig.getConfigValue("ClientId_TEST_APPROVE_REJECT_USER");
			String client_secret_Approve = readconfig.getConfigValue("ClientSceret_TEST__APPROVE_REJECT_USER");
			approveRejectToken = generateToken(client_id_Approve, client_secret_Approve, tokenApiURI);
			log.info("Generated the Approve authorization Token");
			approveRejectToken.trim();
			String client_id_Create = readconfig.getConfigValue("ClientId_TEST_CREATE_USER");
			String client_secret_Create = readconfig.getConfigValue("ClientSceret_TEST_CREATE_USER");
			createToken = generateToken(client_id_Create, client_secret_Create, tokenApiURI);
			createToken.trim();
			log.info("Generated the  Create ticket authorization Token");
			
			String client_id_CreateOSP = readconfig.getConfigValue("ClientId_TEST_CREATE_OSP");
			String client_secret_CreateOSP = readconfig.getConfigValue("ClientSceret_TEST_CREATE_OSP");
			createOSPToken = generateToken(client_id_CreateOSP, client_secret_CreateOSP, tokenApiURI);
			Thread.sleep(3000);
			createOSPToken.trim();
			log.info("Generated the  Create OSP ticket authorization Token");
			
			Thread.sleep(3000);
			Token.trim();
			
			String client_id_CreateApigee = readconfig.getConfigValue("Client_Id_CreateApigee");
			String client_secret_CreateApigee = readconfig.getConfigValue("Client_Secret_CreateApigee");
			createApigeeToken = generateToken(client_id_CreateApigee, client_secret_CreateApigee, tokenApiURI);
			Thread.sleep(3000);
			createApigeeToken.trim();
			log.info("Generated the  Apigee ticket authorization Token");
			
			Thread.sleep(3000);
			Token.trim();
					
			
			break;
			

		}

	}

	//Re-usable Method to generate Token
	public String generateToken(String client_id, String client_secret, String apiURI) {

		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Content-Type", "application/x-www-form-urlencoded").header("cache-control", "no-cache")
				.formParam("client_id", client_id).formParam("client_secret", client_secret)
				.formParam("grant_type", "client_credentials").post(apiURI);
		String token = response.jsonPath().get("access_token");

		return token;

	}

	// Creating Report
	@org.testng.annotations.BeforeTest
	public void BeforeSuite() {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());// time stamp
		String repName = "Test-Report-" + timeStamp + ".html";

		htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/test-output/" + repName);
		htmlReporter.loadXMLConfig(System.getProperty("user.dir") + "/extent-config.xml");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("Environemnt", environement);
		extent.setSystemInfo("user", "Ramu Kasi");
		htmlReporter.config().setDocumentTitle("GAPI project"); // Title of report
		htmlReporter.config().setReportName("API Functional Test Automation Report"); // name of the report
		htmlReporter.config().setTheme(Theme.DARK);
		//htmlReporter.config().enableTimeline(true);
		
	}

	// Clean up the previous Report Data
	@org.testng.annotations.AfterTest
	public void AfterSuite() { 
		
		extent.flush();
		
		
	}

	// Adding Method Status to the Report
	@AfterMethod(alwaysRun = true)
	public void teardown(ITestResult tr) throws IOException {
		
		String ZephyrExcelPath = System.getProperty("user.dir") + "/src/test/java/com/gapi/testData/ZephyrExecutions.xlsx";

		if ((tr.getStatus() == ITestResult.FAILURE)) {
						
			String exceptionMessage = Arrays.toString(tr.getThrowable().getStackTrace());
			logger.fail("<details>" + "<summary>" + "<b>" + "<font color =" + " red>"
					+ "Exception Occured, Click to see details:" + "</font>" + "</b>" + "</summary>"
					+ exceptionMessage.replaceAll(",", "<br>") + "</details>" + "\n");
			// send the passed information
			logger.log(Status.FAIL, MarkupHelper.createLabel(tr.getThrowable() + "Test Failed", ExtentColor.RED));
			logger.log(Status.FAIL, MarkupHelper.createLabel(tr.getName(), ExtentColor.RED));
			//New lines added
//			tc.saveRequestIdToExcelSheet(ZephyrExcelPath, "Status", 0, 1,testData.get("TestCaseID"), "2");
				
	          
			
		} else if (tr.getStatus() == ITestResult.SKIP) {
			logger.log(Status.SKIP, MarkupHelper.createLabel(tr.getName(), ExtentColor.ORANGE));

		} else if (tr.getStatus() == ITestResult.SUCCESS) {
			logger.log(Status.PASS, MarkupHelper.createLabel(tr.getName(), ExtentColor.GREEN));
			
	//		tc.saveRequestIdToExcelSheet(ZephyrExcelPath, "Status", 0, 1,testData.get("TestCaseID"), "1");
		}

	}

	public static String captureScreen(WebDriver driver, String tname) throws IOException {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String target = System.getProperty("user.dir") + "\\Screenshots\\" + tname + ".png";
		File finalDestination = new File(target);
		FileUtils.copyFile(source, finalDestination);
		System.out.println("Screenshot taken");
		return target;
	}

}
