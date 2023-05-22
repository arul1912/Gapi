package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class EditShippingReceivingTicket extends BaseClass {

	public String path = "/shipment-tickets";
	static TestUtilities tc = new TestUtilities();
	CreateShipmentsTicket createShipmentsTicket = new CreateShipmentsTicket();
	static String date = tc.getCurrentDateAndTime();

	// Editing the Shipping and Receiving ticket with tracking number
	//GAPI-4045-SR- Edit the Tracking Number of an Existing SR Ticket  -Status -  New

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyEditTrackingNumberOfAnExistingShippingReceivingTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
		System.out.println("Request is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String WONumber = tc.getWONumberFromResponse(response);

		if (response.getStatusCode() == 201) {
			WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Shipments Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}

		 

		String title = data.get("title");
		String site = data.get("site");
		String description = data.get("description");
		String courier = data.get("courier");
		String trackingNumber = "TRACK4567";

		body = "{\r\n  \"title\": \"" + title + "\",\r\n  " + "\"site\": \"" + site + "\",\r\n  "
				+ "\"description\": \"" + description + "\",\r\n  " + "\"courier\": \"" + courier + "\",\r\n "
				+ "\"trackingNumber\": \"" + trackingNumber + "\"\r\n}";
		
		log.info("Created pay load with updated tracking number");
		logger.log(Status.PASS, "Created pay load with updated tracking number");
		Response editResponse = editShipmentsTicket(Token, body,WONumber);
		log.info("Edited the Shipping and Recieveing ticket with updated tracking number");
		logger.log(Status.PASS, "Edited the Shipping and Recieveing ticket with updated tracking number");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
		Assert.assertEquals(editResponse.jsonPath().get("trackingNumber"), trackingNumber);
		actualStatusCode = editResponse.getStatusCode();
		expectedStatusCode = "200";
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		log.info("Updated tracking numebr is reflecting Correctly.");
		logger.log(Status.PASS, "Updated tracking numebr is reflecting Correctly.");

	}

	// Editing the Shipping and Receiving ticket with courier
	//GAPI-4041-SR- Edit the Courier of an Existing SR Ticket  -Status -  New

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyEditCourierOfAnExistingShippingReceivingTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
		System.out.println("Request is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String WONumber = tc.getWONumberFromResponse(response);

		if (response.getStatusCode() == 201) {
			WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Shipments Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}
		
		String title = data.get("title");
		String site = data.get("site");
		String description = data.get("description");
		String courier = "UPS";
		body = "{\r\n  \"title\": \"" + title + "\",\r\n  " + "\"site\": \"" + site + "\",\r\n  "
				+ "\"description\": \"" + description + "\",\r\n  " + "\"courier\": \"" + courier + "\"\r\n }";
		log.info("Created pay load with updated courier");
		logger.log(Status.PASS, "Created pay load with updated courier");
		Response editResponse = editShipmentsTicket(Token, body,WONumber);
		log.info("Edited the Shipping and Recieveing ticket with updated courier");
		logger.log(Status.PASS, "Edited the Shipping and Recieveing ticket with updated courier");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
		actualStatusCode = editResponse.getStatusCode();
		expectedStatusCode = "200";
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		Assert.assertEquals(editResponse.jsonPath().get("courier"), courier);
		log.info("Updated courier is reflecting Correctly.");
		logger.log(Status.PASS, "Updated courier is reflecting Correctly.");

	}

	//Editing the Shipping and Receiving ticket with package Count
	//GAPI-4043-SR- Edit the Number of Packages of an Existing SR Ticket -Status -  New

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyEditNumberOfPackagesOfAnExistingShippingReceivingTicket(Map<String, String> data)
			throws IOException, InterruptedException {

		String body = tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName") + "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
		System.out.println("Request is: " + response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String WONumber = tc.getWONumberFromResponse(response);

		if (response.getStatusCode() == 201) {
			WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Shipments Ticket");
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
		} else {
			logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
		}


		String title = data.get("title");
		String site = data.get("site");
		String description = data.get("description");
		String courier = "UPS";
		int packageCount=2;
		body = "{\r\n  \"title\": \"" + title + "\",\r\n  " + "\"site\": \"" + site + "\",\r\n  "
				+ "\"description\": \"" + description + "\",\r\n  " + "\"courier\": \"" + courier + "\",\r\n "
				+ "\"packageCount\":  "+ packageCount + "}";
		log.info("Created pay load with updated Package Count");
		logger.log(Status.PASS, "Created pay load with updated Package Count");
		System.out.println("Request payload is \n" + body);
		Response editResponse = editShipmentsTicket(Token, body,WONumber);
		log.info("Edited the Shipping and Recieveing ticket with updated Package Count");
		logger.log(Status.PASS, "Edited the Shipping and Recieveing ticket with updated Package Count");
		logger.log(Status.PASS,
				"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
		actualStatusCode = editResponse.getStatusCode();
		Assert.assertEquals(String.valueOf(actualStatusCode), "200");
		Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		Assert.assertEquals(editResponse.jsonPath().getInt("packageCount"), packageCount);
		logger.log(Status.PASS, "Updated Package Count is reflecting Correctly.");
		log.info("Updated Package Count is reflecting Correctly.");

	}
	
	   // Editing the Shipping and Receiving ticket with reference number
	  //GAPI-4049-SR- Edit the Reference Number of an Existing SR Ticket -Status -  New

		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyEditReferenceNumberOfAnExistingShippingReceivingTicket(Map<String, String> data)
				throws IOException, InterruptedException {

			String body = tc.getRequestBody(data);
			System.out.println(data.get("TestCaseName") + "\n");
			System.out.println("Request payload is \n" + body);
			logger = extent.createTest(data.get("TestCaseName"));
			Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
			System.out.println("Response is: " + response.asString());
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			String WONumber = tc.getWONumberFromResponse(response);

			if (response.getStatusCode() == 201) {
				WONumber = tc.getWONumberFromResponse(response);
				log.info("The WO Number Created is " + WONumber);
				logger.log(Status.PASS, "Successfully Created Shipments Ticket");
				tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
			} else {
				logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");
				tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
			}

			 

			String title = data.get("title");
			String site = data.get("site");
			String description = data.get("description");
			String courier = data.get("courier");
			String customerReference = "nr. RF000187";

			body = "{\r\n  \"title\": \"" + title + "\",\r\n  " + "\"site\": \"" + site + "\",\r\n  "
					+ "\"description\": \"" + description + "\",\r\n  " + "\"courier\": \"" + courier + "\",\r\n "
					+ "\"customerReference\": \"" + customerReference + "\"\r\n}";
			
			log.info("Created pay load with updated Reference Number");
			logger.log(Status.PASS, "Created pay load with updated Reference Number");
			Response editResponse = editShipmentsTicket(Token, body,WONumber);
			log.info("Edited the Shipping and Recieveing ticket with updated Reference Number");
			logger.log(Status.PASS, "Edited the Shipping and Recieveing ticket with updated Reference Number");
			logger.log(Status.PASS,
					"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
			
			Assert.assertEquals(editResponse.jsonPath().get("customerReference"), customerReference);
			actualStatusCode = editResponse.getStatusCode();
			expectedStatusCode = "200";
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			log.info("Updated Reference Number is reflecting Correctly.");
			logger.log(Status.PASS, "Updated Reference Number is reflecting Correctly.");

		}
		
			   // Editing the Shipping and Receiving ticket with Estimated Delivery Date
			   //GAPI-4047-SR- Edit the Estimated Delivery Date of an Existing SR Ticket -Status -  New

				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyEditEstimatedDeliveryDateOfAnExistingShippingReceivingTicket(Map<String, String> data)
						throws IOException, InterruptedException {

					String body = tc.getRequestBody(data);
					System.out.println(data.get("TestCaseName") + "\n");
					System.out.println("Request payload is \n" + body);
					logger = extent.createTest(data.get("TestCaseName"));
					Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
					System.out.println("Response is: " + response.asString());
					String expectedStatusCode = data.get("expectedStatusCode");
					int actualStatusCode = response.getStatusCode();
					String expectedStatusMessage = data.get("ExpectedStatusMessage");
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					String WONumber = tc.getWONumberFromResponse(response);

					if (response.getStatusCode() == 201) {
						WONumber = tc.getWONumberFromResponse(response);
						log.info("The WO Number Created is " + WONumber);
						logger.log(Status.PASS, "Successfully Created Shipments Ticket");
						tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
					} else {
						logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
						String expectedErrorMessageType = data.get("errorMessageType");
						String expectedErrorMessage = data.get("errorMessage");
						tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
					}

					String title = data.get("title");
					String site = data.get("site");
					String description = data.get("description");
					String courier = data.get("courier");
					String estimatedDeliveryDate = "2023-12-01 00:00:00";

					body = "{\r\n  \"title\": \"" + title + "\",\r\n  " + "\"site\": \"" + site + "\",\r\n  "
							+ "\"description\": \"" + description + "\",\r\n  " + "\"courier\": \"" + courier + "\",\r\n "
							+ "\"estimatedDeliveryDate\": \"" + estimatedDeliveryDate + "\"\r\n}";
					
					log.info("Created pay load with updated Estimated Delivery Date");
					logger.log(Status.PASS, "Created pay load with updated Estimated Delivery Date");
					Response editResponse = editShipmentsTicket(Token, body,WONumber);
					log.info("Edited the Shipping and Recieveing ticket with updated Estimated Delivery Date");
					logger.log(Status.PASS, "Edited the Shipping and Recieveing ticket with updated Estimated Delivery Date");
					logger.log(Status.PASS,
							"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
					Assert.assertEquals(editResponse.jsonPath().get("estimatedDeliveryDate"), estimatedDeliveryDate);
					System.out.println( "Edit Response - "+editResponse.asString());
					actualStatusCode = editResponse.getStatusCode();
					expectedStatusCode = "200";
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					log.info("Updated Estimated Delivery Date is reflecting Correctly.");
					logger.log(Status.PASS, "Updated Estimated Delivery Date is reflecting Correctly.");

				}

				//Editing the Shipping and Receiving ticket with  Short Description
				//GAPI-4039-SR- Edit the Short Description of an Existing SR Ticket -Status -  New

				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyEditDescriptionOfAnExistingShippingReceivingTicket(Map<String, String> data)
						throws IOException, InterruptedException {

					String body = tc.getRequestBody(data);
					System.out.println(data.get("TestCaseName") + "\n");
					System.out.println("Request payload is \n" + body);
					logger = extent.createTest(data.get("TestCaseName"));
					Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
					System.out.println("Request is: " + response.asString());
					String expectedStatusCode = data.get("expectedStatusCode");
					int actualStatusCode = response.getStatusCode();
					String expectedStatusMessage = data.get("ExpectedStatusMessage");
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					String WONumber = tc.getWONumberFromResponse(response);

					if (response.getStatusCode() == 201) {
						WONumber = tc.getWONumberFromResponse(response);
						log.info("The WO Number Created is " + WONumber);
						logger.log(Status.PASS, "Successfully Created Shipments Ticket");
						tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
					} else {
						logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
						String expectedErrorMessageType = data.get("errorMessageType");
						String expectedErrorMessage = data.get("errorMessage");
						tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
					}

					 

					String title = data.get("title");
					String site = data.get("site");
					String description ="New description";
					String courier = data.get("courier");
					

					body = "{\r\n  \"title\": \"" + title + "\",\r\n  " + "\"site\": \"" + site + "\",\r\n  "
							+ "\"description\": \"" + description + "\",\r\n  " + "\"courier\": \"" + courier + "\"}";
							
					
					log.info("Created pay load with updated tracking number");
					logger.log(Status.PASS, "Created pay load with updated Short Description");
					Response editResponse = editShipmentsTicket(Token, body,WONumber);
					log.info("Edited the Shipping and Recieveing ticket with updated Short Description");
					logger.log(Status.PASS, "Edited the Shipping and Recieveing ticket with updated Short Description");
					logger.log(Status.PASS,
							"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
					//Assert.assertEquals(editResponse.jsonPath().get("description"), description);
					actualStatusCode = editResponse.getStatusCode();
					expectedStatusCode = "200";
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					Assert.assertEquals(editResponse.jsonPath().get("description"), description);
					log.info("Updated Short Description is reflecting Correctly.");
					logger.log(Status.PASS, "Updated Short Description is reflecting Correctly.");

				}
				
				//Editing the Shipping and Receiving ticket with  Title	
				//GAPI-4037-SR- Edit the Title of an Existing SR Ticket -Status -  New

				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyEditTitleOfAnExistingShippingReceivingTicket(Map<String, String> data)
						throws IOException, InterruptedException {

					String body = tc.getRequestBody(data);
					System.out.println(data.get("TestCaseName") + "\n");
					System.out.println("Request payload is \n" + body);
					logger = extent.createTest(data.get("TestCaseName"));
					Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
					System.out.println("Request is: " + response.asString());
					String expectedStatusCode = data.get("expectedStatusCode");
					int actualStatusCode = response.getStatusCode();
					String expectedStatusMessage = data.get("ExpectedStatusMessage");
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					String WONumber = tc.getWONumberFromResponse(response);

					if (response.getStatusCode() == 201) {
						WONumber = tc.getWONumberFromResponse(response);
						log.info("The WO Number Created is " + WONumber);
						logger.log(Status.PASS, "Successfully Created Shipments Ticket");
						tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
					} else {
						logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
						String expectedErrorMessageType = data.get("errorMessageType");
						String expectedErrorMessage = data.get("errorMessage");
						tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
					}

					 

					String title = "Edit title";
					String site = data.get("site");
					String description = data.get("description");
					String courier = data.get("courier");
					

					body = "{\r\n  \"title\": \"" + title + "\",\r\n  " + "\"site\": \"" + site + "\",\r\n  "
							+ "\"description\": \"" + description + "\",\r\n  " + "\"courier\": \"" + courier + "\"}";
							
					
					log.info("Created pay load with updated tracking number");
					logger.log(Status.PASS, "Created pay load with updated Title");
					Response editResponse = editShipmentsTicket(Token, body,WONumber);
					log.info("Edited the Shipping and Recieveing ticket with updated Title");
					logger.log(Status.PASS, "Edited the Shipping and Recieveing ticket with updated Title");
					logger.log(Status.PASS,
							"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
					//Assert.assertEquals(editResponse.jsonPath().get("description"), description);
					actualStatusCode = editResponse.getStatusCode();
					expectedStatusCode = "200";
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					Assert.assertEquals(editResponse.jsonPath().get("title"), title);
					log.info("Updated Title is reflecting Correctly.");
					logger.log(Status.PASS, "Updated Title is reflecting Correctly.");

				}
				
				//arul changes on 06-03-2023 Edit SH Ticket with Loading bay Required
				//GAPI-4051-SR- Edit the Loading Bay of an Existing SR Ticket  -Status -  New

				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyEditLoadingBayofanExistingSRTicket(Map<String, String> data) throws IOException, InterruptedException {

					logger = extent.createTest(data.get("TestCaseName"));
					JSONObject shipmentsTicketdetails = new JSONObject();
					shipmentsTicketdetails.put("site", data.get("site"));
					shipmentsTicketdetails.put("title", data.get("title"));
					shipmentsTicketdetails.put("description", data.get("description"));
					shipmentsTicketdetails.put("courier", data.get("courier"));
					shipmentsTicketdetails.put("customerReference", data.get("customerReference"));
					shipmentsTicketdetails.put("trackingNumber", data.get("trackingNumber"));
					//shipmentsTicketdetails.put("isLoadingBayRequired", Boolean.valueOf(data.get("isLoadingBayRequired")));
					
					System.out.println("Body is------ :");
					System.out.println(shipmentsTicketdetails.toString());
					
					String apiURI = domain + path;
					System.out.println(apiURI);
					String body = shipmentsTicketdetails.toString();
					Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
					String WONumber = tc.getWONumberFromResponse(response);
					shipmentsTicketdetails = new JSONObject();
					shipmentsTicketdetails.put("site", data.get("site"));
					shipmentsTicketdetails.put("title", data.get("title"));
					shipmentsTicketdetails.put("description", data.get("description"));
					shipmentsTicketdetails.put("courier", data.get("courier"));
					shipmentsTicketdetails.put("customerReference", data.get("customerReference"));
					shipmentsTicketdetails.put("trackingNumber", data.get("trackingNumber"));
					shipmentsTicketdetails.put("isLoadingBayRequired", Boolean.valueOf(data.get("isLoadingBayRequired")));
					body = shipmentsTicketdetails.toString();
					
					response = editShipmentsTicket(Token, body,WONumber);
					System.out.println("Response is : " + response.asString());
					System.out.println("Status Code is : " + response.getStatusCode());
					Assert.assertEquals(200, response.getStatusCode());
					logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
					
					logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
					if (response.getStatusCode()==200) {
						 WONumber = tc.getWONumberFromResponse(response);
						log.info("The WO Number Created is " + WONumber);
						logger.log(Status.PASS, "Successfully Created Shipments Ticket");
						logger.log(Status.PASS, "Created Shipments Ticket  Id Is " + WONumber);
						tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
						Assert.assertTrue(response.jsonPath().get("isLoadingBayRequired"));
					}
					else {
						logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
						String expectedErrorMessageType = data.get("errorMessageType");
						String expectedErrorMessage = data.get("errorMessage");
						tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					}

			}
				
				//Arul 0n 08-03-2023 SR- Edit the Contacts of an Existing SR Ticket -Status -  New
				//GAPI-4053-SR- Edit the Contacts of an Existing SR Ticket -Status -  New

				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void editTheContactsOfAnExistingShippingReceivingTicket(Map<String, String> data)
						throws IOException, InterruptedException {

					String body = tc.getRequestBody(data);
					System.out.println(data.get("TestCaseName") + "\n");
					System.out.println("Request payload is \n" + body);
					logger = extent.createTest(data.get("TestCaseName"));
					Response response = createShipmentsTicket.createShipmentsTicket(Token, body);
					System.out.println("Request is: " + response.asString());
					String expectedStatusCode = data.get("expectedStatusCode");
					int actualStatusCode = response.getStatusCode();
					String expectedStatusMessage = data.get("ExpectedStatusMessage");
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					String WONumber = tc.getWONumberFromResponse(response);

					if (response.getStatusCode() == 201) {
						WONumber = tc.getWONumberFromResponse(response);
						log.info("The WO Number Created is " + WONumber);
						logger.log(Status.PASS, "Successfully Created Shipments Ticket");
						tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "ShipmentsId_" + WONumber);
					} else {
						logger.log(Status.PASS, "Not allowed to create Shipments Ticket");
						String expectedErrorMessageType = data.get("errorMessageType");
						String expectedErrorMessage = data.get("errorMessage");
						tc.verifyErrorResponseMessage(response.asString(), expectedErrorMessageType, expectedErrorMessage);
					}

					 

					String title = data.get("title");
					String site = data.get("site");
					String description ="New description";
					String courier = data.get("courier");
					

					body = "{\r\n  \"title\": \"" + title + "\",\r\n  " + "\"site\": \"" + site + "\",\r\n  "
							+ "\"description\": \"" + description + "\",\r\n  " + "\"courier\": \"" + courier + "\"}";
							
					
					log.info("Created pay load with updated tracking number");
					logger.log(Status.PASS, "Created pay load with updated Short Description");
					Response editResponse = editShipmentsTicket(Token, body,WONumber);
					log.info("Edited the Shipping and Recieveing ticket with updated Short Description");
					logger.log(Status.PASS, "Edited the Shipping and Recieveing ticket with updated Short Description");
					logger.log(Status.PASS,
							"Response Status Code and Status Message is after editing the ticket" + editResponse.statusLine());
					//Assert.assertEquals(editResponse.jsonPath().get("description"), description);
					actualStatusCode = editResponse.getStatusCode();
					expectedStatusCode = "200";
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
					Assert.assertEquals(editResponse.jsonPath().get("description"), description);
					log.info("Updated Short Description is reflecting Correctly.");
					logger.log(Status.PASS, "Updated Short Description is reflecting Correctly.");

				}

	//Edit the Shipping and Receiving ticket
	public Response editShipmentsTicket(String Token, String body,String WONumber) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).body(body).put(domain + path+ "/" + WONumber).then().extract().response();

		return response;
	}

}
