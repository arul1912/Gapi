package com.gapi.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import com.gapi.testCases.BaseClass;

public class DataProviderUtility extends BaseClass {

	private static List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	static String excelPath = System.getProperty("user.dir") + "/src/test/java/com/gapi/testData/TestDataSheet.xlsx";
	static String excelPath1 = System.getProperty("user.dir") + "/src/test/java/com/gapi/testData/TestData.xlsx";

	@DataProvider(name = "getRemoteHandsData")
	String[][] getRemoteHandsRequestData() throws IOException {

		int columnCount = XLUtilities.getCellCount(excelPath, "Sheet1", 1);
		int rowCount = XLUtilities.getRowCount(excelPath, "Sheet1");
		String data[][] = new String[rowCount][columnCount];
		for (int i = 1; i <= rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				data[i - 1][j] = XLUtilities.getCellData(excelPath, "Sheet1", i, j);
			}
		}
		return (data);
	}

	@DataProvider(name = "testData")
	public static Object[] getTestData(Method m) throws IOException {
		String testName = m.getName();
		if (list.isEmpty()) {
			list = getTestDataInformation("TestData");
		}
		List<Map<String, String>> executableTestData = new ArrayList<Map<String, String>>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).get("testCaseName").equalsIgnoreCase(testName)) {
				if (list.get(i).get("executionFlag").equalsIgnoreCase("Y")) {
					executableTestData.add(list.get(i));
				}
			}
		}
		return executableTestData.toArray();
	}

	@SuppressWarnings("resource")
	public static List<Map<String, String>> getTestDataInformation(String sheetName) throws IOException {

		List<Map<String, String>> list = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(excelPath1);
			XSSFWorkbook workBook = new XSSFWorkbook(fs);
			XSSFSheet sheet = workBook.getSheet(sheetName);
			int lastrowNumber = sheet.getLastRowNum();
			int lastColumnNumber = sheet.getRow(0).getLastCellNum();
			Map<String, String> map = null;
			list = new ArrayList<Map<String, String>>();
			for (int i = 1; i <= lastrowNumber; i++) {
				map = new HashMap<String, String>();
				for (int j = 0; j < lastColumnNumber; j++) {
					String key = sheet.getRow(0).getCell(j).getStringCellValue();
					String value = sheet.getRow(i).getCell(j).getStringCellValue();
					map.put(key, value);
				}
				list.add(map);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}

	@DataProvider(name = "testCasesData")
	public static Object[] getTestDataSample(Method m) throws IOException {
		String testName = m.getName();
		String environment = readconfig.getConfigValue("Environment");
		String sheetName = null;

		switch (environement) {
		case "UAT":
			sheetName = "TestData_UAT";
			break;

		case "TEST":
			sheetName = "TestData_TEST";
			break;
		}

		if (list.isEmpty()) {
			list = getTestDataInformationSample(sheetName);
		}
		List<Map<String, String>> executableTestData1 = new ArrayList<Map<String, String>>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).get("methodName").equalsIgnoreCase(testName)) {
				if (list.get(i).get("executionFlag").equalsIgnoreCase("Y")) {
					executableTestData1.add(list.get(i));
				}
			}
		}

		return executableTestData1.toArray();
	}

	@SuppressWarnings("resource")
	public static List<Map<String, String>> getTestDataInformationSample(String sheetName) throws IOException {

		List<Map<String, String>> list1 = null;
		FileInputStream fs = null;

		try {
			fs = new FileInputStream(excelPath);
			XSSFWorkbook workBook = new XSSFWorkbook(fs);
			XSSFSheet sheet = workBook.getSheet(sheetName);
			int lastrowNumber = sheet.getLastRowNum();
			int lastColumnNumber = sheet.getRow(0).getLastCellNum();
			Map<String, String> map1 = null;
			list1 = new ArrayList<Map<String, String>>();
			for (int i = 1; i <= lastrowNumber; i++) {
				map1 = new HashMap<String, String>();
				for (int j = 0; j < lastColumnNumber; j++) {
					String key = sheet.getRow(0).getCell(j).getStringCellValue();
					if (sheet.getRow(i).getCell(j).getCellType() == CellType.STRING) {
						String value = sheet.getRow(i).getCell(j).getStringCellValue();
						map1.put(key, value);
					}
					if (sheet.getRow(i).getCell(j).getCellType() == CellType.NUMERIC) {
						String value = String.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue());
						map1.put(key, value);
					} else if (sheet.getRow(i).getCell(j).getCellType() == CellType.BLANK) {
						String value = "";
						map1.put(key, value);
					}

				}
				list1.add(map1);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return list1;
	}
}
