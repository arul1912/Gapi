package com.gapi.testCases;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.ParseException;
import org.codehaus.jettison.json.JSONException;
import org.testng.annotations.Test;

import com.gapi.utilities.XLUtilities;
import com.gapi.utilities.ZephyrUpdatePage;

public class ZepyhrUpdate {
	static String excelPath = System.getProperty("user.dir") + "/src/test/java/com/gapi/testData/ZephyrExecutions.xlsx";
	@Test
	public void updateZephyrStatus() throws ParseException, URISyntaxException, JSONException, IOException
	{		
		ZephyrUpdatePage updatezephyrCases = new ZephyrUpdatePage();
		int columnCount = XLUtilities.getCellCount(excelPath, "Status", 1);
		int rowCount = XLUtilities.getRowCount(excelPath, "Status");
		String issueKey;
		String status;
		String cycleName;
		String filePath="";
		String data[][] = new String[rowCount][columnCount];
		for (int i = 1; i <= rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				issueKey=XLUtilities.getCellData(excelPath, "Status", i, 0);
				status=XLUtilities.getCellData(excelPath, "Status", i, 1);
				cycleName=XLUtilities.getCellData(excelPath, "Status", i, 2);
				filePath=XLUtilities.getCellData(excelPath, "Status", i, 3);
				updatezephyrCases.updateZephyrResults(issueKey,status,cycleName,filePath);
			}
		}
		
	}
	
}
