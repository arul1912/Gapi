package com.gapi.utilities;

/** Reporting class is used to generate extent report after tests execution
 * @author rkasi
 * @version 17
 * @since 2022 
 * */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Reporting extends TestListenerAdapter {
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	public static ExtentTest logger;

	/*
	 * public static void setExtent() { String timeStamp = new
	 * SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());// time stamp
	 * String repName = "Test-Report-" + timeStamp + ".html";
	 * 
	 * htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") +
	 * "/test-output/" + repName);// specify // location // of the // report
	 * htmlReporter.loadXMLConfig(System.getProperty("user.dir") +
	 * "/extent-config.xml");
	 * 
	 * extent = new ExtentReports();
	 * 
	 * extent.attachReporter(htmlReporter); // extent.setSystemInfo("Host name",
	 * "localhost"); extent.setSystemInfo("Environemnt", "QA");
	 * extent.setSystemInfo("user", "Ramu Kasi");
	 * 
	 * htmlReporter.config().setDocumentTitle("GAPI project"); // Title of report
	 * htmlReporter.config().setReportName("API Functional Test Automation Report");
	 * // name of the report //
	 * htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP); //location
	 * // of the chart htmlReporter.config().setTheme(Theme.DARK); }
	 */

	public void onTestStart(ITestResult tr) {

		// logger = extent.createTest(tr.getName());
		System.out.println("Test Execution started");
		// create new entry in the report
		// logger.log(Status.PASS, MarkupHelper.createLabel(tr.getName(),
		// ExtentColor.GREEN)); // information to the report
		// highlighted
	}

	public void onTestSuccess(ITestResult tr) {

		// logger = extent.createTest(tr.getName()); // create new entry in the report
		// logger.log(Status.PASS, MarkupHelper.createLabel(tr.getName(),
		// ExtentColor.GREEN)); // information to the report
		System.out.println("Test Passed"); // highlighted
	}

	public void onTestFailure(ITestResult tr) {
		// logger = extent.createTest(tr.getName()); // create new entry in the report
		// logger.log(Status.FAIL, MarkupHelper.createLabel(tr.getName(),
		// ExtentColor.RED)); // send the passed information
		// logger.log(Status.FAIL, MarkupHelper.createLabel(tr.getThrowable()+ "Test
		// Failed", ExtentColor.RED)); // to the report with GREEN // color highlighted
		System.out.println("Test Failed");
	}

	public void onTestSkipped(ITestResult tr) {
		// logger = extent.createTest(tr.getName()); // create new entry in th report
		// logger.log(Status.SKIP, MarkupHelper.createLabel(tr.getName(),
		// ExtentColor.ORANGE));
		System.out.println("Test Skipped");
	}

	
	 // public static void endExtent() { extent.flush(); }
	 
}
