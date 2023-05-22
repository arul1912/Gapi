package com.gapi.utilities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.thed.zephyr.cloud.rest.ZFJCloudRestClient;
import com.thed.zephyr.cloud.rest.client.JwtGenerator;

@SuppressWarnings("deprecation")
public class ZephyrUpdatePage {

	String zephyrBaseUrl = "https://prod-api.zephyr4jiracloud.com/connect";
	String API_GET_CYCLES = zephyrBaseUrl + "/public/rest/api/1.0/";

	String API_UPDATE_EXECUTION = zephyrBaseUrl + "/public/rest/api/1.0/execution/";
	String API_GET_CYCLES_FOLDER = zephyrBaseUrl + "/public/rest/api/1.0";

	String versionId = "18063";
	String projectId = "12995";
	// zephyr accessKey , we can get from Addons >> zapi section
	String accessKey = "amlyYTo0N2ViOWY4MS0wMmMwLTQxODItYjY3YS04MGJjZDIyY2YzYTYgNjJhYzhhMjlmOGJkNzkwMDY5ZGE0ODhlIFVTRVJfREVGQVVMVF9OQU1F";
	// zephyr secretKey , we can get from Addons >> zapi section
	String secretKey = "QIWLOccwqJaHSQ0WDvpoQf9jjZV75djDHaMnL_lrQ9c";
	// Jira accountId
	String accountId = "62ac8a29f8bd790069da488e";

	//String testCycleId = "6a6842ed-19a9-49cb-8d80-a31bcf5801e3";
	//String versionId23A = "19446";
	//String testCycleId23A = "031d54fd-236e-40c0-9a4c-1853c21916a8";

	/**
	 * 
	 * @throws URISyntaxException
	 * @throws ParseException
	 * @throws JSONException
	 * @throws IOException
	 */
	public void updateZephyrResults(String issueKey, String status, String cycleName, String filePath)
			throws URISyntaxException, JSONException, ParseException, IOException {

		ZFJCloudRestClient restClient = ZFJCloudRestClient.restBuilder(zephyrBaseUrl, accessKey, secretKey, accountId)
				.build();

		// Get Cycle Details
		final String getCyclesUri = API_GET_CYCLES + "cycles/search?projectId=" + projectId + "&versionId=" + versionId;

		Map<String, String> cycles = getCyclesByProjectVersion(getCyclesUri, restClient, accessKey);

		for (String cyclekey : cycles.keySet()) {

			if (cyclekey.equalsIgnoreCase(cycleName)) {
				String cycleId = cycles.get(cyclekey);

				final String getCyclesFolderUri = API_GET_CYCLES + "folders?versionId=" + versionId + "&cycleId="
						+ cycleId + "&projectId=" + projectId;

				Map<String, String> folderIds = getFolderIds(getCyclesFolderUri, restClient, accessKey);

				for (String folderId : folderIds.values()) {

					String API_GET_EXECUTIONS_FOLDER = zephyrBaseUrl + "/public/rest/api/1.0/executions/search/folder/";

					final String getExecutionFromFOlder = API_GET_EXECUTIONS_FOLDER + folderId + "?versionId="
							+ versionId + "&projectId=" + projectId + "&cycleId=" + cycleId;

					Map<String, ArrayList<String>> executionIdsList = getExecutionsIdFromFolder(getExecutionFromFOlder,
							restClient, accessKey);
					for (String executionKey : executionIdsList.keySet()) {
						System.out.println("executionId = " + executionKey);
						if (executionKey.equalsIgnoreCase(issueKey)) {
							ArrayList<String> executionIdDetails = executionIdsList.get(executionKey);
							String executionId = executionIdDetails.get(0);
							String issueId = executionIdDetails.get(1);

							JSONObject statusObj = new JSONObject();
							statusObj.put("id",status);

							JSONObject executeTestsObj = new JSONObject();
							executeTestsObj.put("status", statusObj);
							executeTestsObj.put("cycleId", cycleId);
							executeTestsObj.put("projectId", projectId);
							executeTestsObj.put("versionId", versionId);
							executeTestsObj.put("comment", "Executed by ZAPI Cloud");
							executeTestsObj.put("issueId", issueId);

							final String updateExecutionUri = API_UPDATE_EXECUTION + executionId;

							// System.out.println(executeTestsObj.toString());
							StringEntity executeTestsJSON = null;
							try {
								executeTestsJSON = new StringEntity(executeTestsObj.toString());
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}
							updateExecutions(updateExecutionUri, restClient, accessKey, executeTestsJSON);
						}

					}
				}

			}

		}
	}

	@SuppressWarnings("resource")
	private static Map<String, String> getCyclesByProjectVersion(String getCyclesUri, ZFJCloudRestClient client,
			String accessKey) throws URISyntaxException, JSONException {

		Map<String, String> cycleMap = new HashMap<String, String>();

		URI uri = new URI(getCyclesUri);
		int expirationInSec = 1600;

		JwtGenerator jwtGenerator = client.getJwtGenerator();

		String jwt = jwtGenerator.generateJWT("GET", uri, expirationInSec);

		//Print the URL and JWT token to be used for making the REST call
		System.out.println("FINAL API : " + uri.toString());
		System.out.println("JWT Token : " + jwt);

		HttpResponse response = null;
		HttpClient restClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setHeader("Authorization", jwt);
		httpGet.setHeader("zapiAccessKey", accessKey);

		try {
			response = restClient.execute(httpGet);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("status: " + statusCode);

		if (statusCode >= 200 && statusCode < 300) {
			HttpEntity entity1 = response.getEntity();
			String string1 = null;
			try {
				string1 = EntityUtils.toString(entity1);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JSONArray cyclesArray = new JSONArray(string1);
			for (int i = 0; i < cyclesArray.length(); i++) {
				JSONObject cycleObj = cyclesArray.getJSONObject(i);
				String cycleID = cycleObj.getString("id");
				String cycleName = cycleObj.getString("name");
				cycleMap.put(cycleName, cycleID);
				System.out.println("Cycle Name: " + cycleName);
				System.out.println("Cycle Id: " + cycleID);
			}
		}
		return cycleMap;
	}

	@SuppressWarnings("resource")
	private static JSONArray getExecutionsByCycleId(String getExecutionsUri, ZFJCloudRestClient client,
			String accessKey) throws URISyntaxException, JSONException {
		JSONArray IssuesArray = null;

		URI uri = new URI(getExecutionsUri);
		int expirationInSec = 1600;

		JwtGenerator jwtGenerator = client.getJwtGenerator();

		String jwt = jwtGenerator.generateJWT("GET", uri, expirationInSec);

		// Print the URL and JWT token to be used for making the REST call
		System.out.println("FINAL API : " + uri.toString());
		System.out.println("JWT Token : " + jwt);

		HttpResponse response = null;
		HttpClient restClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setHeader("Authorization", jwt);
		httpGet.setHeader("zapiAccessKey", accessKey);

		try {
			response = restClient.execute(httpGet);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("status: " + statusCode);

		if (statusCode >= 200 && statusCode < 300) {
			HttpEntity entity1 = response.getEntity();
			String string1 = null;
			try {
				string1 = EntityUtils.toString(entity1);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JSONObject allIssues = new JSONObject(string1);
			IssuesArray = allIssues.getJSONArray("searchObjectList");

		}
		return IssuesArray;
	}

	@SuppressWarnings("resource")
	private static Map<String, String> getExecutionsId(String getExecutionsUri, ZFJCloudRestClient client,
			String accessKey) throws URISyntaxException, JSONException {
		JSONArray IssuesArray = null;
		Map<String, String> executionIds = new HashMap<String, String>();

		URI uri = new URI(getExecutionsUri);
		int expirationInSec = 1600;

		JwtGenerator jwtGenerator = client.getJwtGenerator();

		String jwt = jwtGenerator.generateJWT("GET", uri, expirationInSec);

		// Print the URL and JWT token to be used for making the REST call
		System.out.println("FINAL API : " + uri.toString());
		System.out.println("JWT Token : " + jwt);

		HttpResponse response = null;
		HttpClient restClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setHeader("Authorization", jwt);
		httpGet.setHeader("zapiAccessKey", accessKey);

		try {
			response = restClient.execute(httpGet);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("status: " + statusCode);

		if (statusCode >= 200 && statusCode < 300) {
			HttpEntity entity1 = response.getEntity();
			String string1 = null;
			try {
				string1 = EntityUtils.toString(entity1);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JSONObject allIssues = new JSONObject(string1);
			IssuesArray = allIssues.getJSONArray("searchObjectList");

			if (IssuesArray.length() == 0) {
				return executionIds;
			}
			for (int j = 0; j <= IssuesArray.length() - 1; j++) {
				JSONObject jobj = IssuesArray.getJSONObject(j);
				JSONObject jobj2 = jobj.getJSONObject("execution");
				String executionId = jobj2.getString("id");
				long IssueId = jobj2.getLong("issueId");
				executionIds.put(executionId, String.valueOf(IssueId));
			}
		}
		return executionIds;
	}

	@SuppressWarnings("resource")
	private static Map<String, ArrayList<String>> getExecutionsIdFromFolder(String getExecutionsUri,
			ZFJCloudRestClient client, String accessKey) throws URISyntaxException, JSONException {
		JSONArray IssuesArray = null;
		Map<String, ArrayList<String>> executionIds = new HashMap<String, ArrayList<String>>();

		URI uri = new URI(getExecutionsUri);
		int expirationInSec = 1600;

		JwtGenerator jwtGenerator = client.getJwtGenerator();

		String jwt = jwtGenerator.generateJWT("GET", uri, expirationInSec);

		// Print the URL and JWT token to be used for making the REST call
		System.out.println("FINAL API : " + uri.toString());
		System.out.println("JWT Token : " + jwt);

		HttpResponse response = null;
		HttpClient restClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setHeader("Authorization", jwt);
		httpGet.setHeader("zapiAccessKey", accessKey);

		try {
			response = restClient.execute(httpGet);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("status: " + statusCode);

		if (statusCode >= 200 && statusCode < 300) {
			HttpEntity entity1 = response.getEntity();
			String string1 = null;
			try {
				string1 = EntityUtils.toString(entity1);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JSONObject allIssues = new JSONObject(string1);
			IssuesArray = allIssues.getJSONArray("searchObjectList");

			if (IssuesArray.length() == 0) {
				return executionIds;
			}
			for (int j = 0; j <= IssuesArray.length() - 1; j++) {
				JSONObject jobj = IssuesArray.getJSONObject(j);
				String issueKey = jobj.getString("issueKey");
				JSONObject jobj2 = jobj.getJSONObject("execution");
				String executionId = jobj2.getString("id");
				long IssueId = jobj2.getLong("issueId");
				executionIds.put(issueKey, new ArrayList<String>());
				executionIds.get(issueKey).add(executionId);
				executionIds.get(issueKey).add(String.valueOf(IssueId));
			}
		}
		return executionIds;
	}

	@SuppressWarnings("resource")
	private static Map<String, String> getFolderIds(String getExecutionsUri, ZFJCloudRestClient client,
			String accessKey) throws URISyntaxException, JSONException {
		JSONArray foldersArray = null;
		Map<String, String> allFoldersId = new HashMap<String, String>();

		URI uri = new URI(getExecutionsUri);
		int expirationInSec = 1600;

		JwtGenerator jwtGenerator = client.getJwtGenerator();

		String jwt = jwtGenerator.generateJWT("GET", uri, expirationInSec);

		// Print the URL and JWT token to be used for making the REST call
		System.out.println("FINAL API : " + uri.toString());
		System.out.println("JWT Token : " + jwt);

		HttpResponse response = null;
		HttpClient restClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setHeader("Authorization", jwt);
		httpGet.setHeader("zapiAccessKey", accessKey);

		try {
			response = restClient.execute(httpGet);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("status: " + statusCode);

		if (statusCode >= 200 && statusCode < 300) {
			HttpEntity entity1 = response.getEntity();
			String string1 = null;
			try {
				string1 = EntityUtils.toString(entity1);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			foldersArray = new JSONArray(string1);

			for (int j = 0; j <= foldersArray.length() - 1; j++) {
				JSONObject foldersDetails = foldersArray.getJSONObject(j);
				String foldersName = foldersDetails.getString("name");
				String foldersId = foldersDetails.getString("id");
				allFoldersId.put(foldersName, foldersId);
				System.out.println("Folder Name: " + foldersName);
				System.out.println("Folder Id: " + foldersId);
			}
		}
		return allFoldersId;
	}

	@SuppressWarnings("resource")
	public static String updateExecutions(String uriStr, ZFJCloudRestClient client, String accessKey,
			StringEntity executionJSON) throws URISyntaxException, JSONException, ParseException, IOException {

		URI uri = new URI(uriStr);
		int expirationInSec = 360;
		JwtGenerator jwtGenerator = client.getJwtGenerator();
		String jwt = jwtGenerator.generateJWT("PUT", uri, expirationInSec);

		HttpResponse response = null;
		HttpClient restClient = new DefaultHttpClient();

		HttpPut executeTest = new HttpPut(uri);
		executeTest.addHeader("Content-Type", "application/json");
		executeTest.addHeader("Authorization", jwt);
		executeTest.addHeader("zapiAccessKey", accessKey);
		executeTest.setEntity(executionJSON);

		try {
			response = restClient.execute(executeTest);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int statusCode = response.getStatusLine().getStatusCode();
		// System.out.println(statusCode);
		String executionStatus = "No Test Executed";
		// System.out.println(response.toString());
		HttpEntity entity = response.getEntity();

		if (statusCode >= 200 && statusCode < 300) {
			String string = null;
			try {
				string = EntityUtils.toString(entity);
				JSONObject executionResponseObj = new JSONObject(string);
				JSONObject descriptionResponseObj = executionResponseObj.getJSONObject("execution");
				JSONObject statusResponseObj = descriptionResponseObj.getJSONObject("status");
				executionStatus = statusResponseObj.getString("description");
				System.out.println(executionResponseObj.get("issueKey") + "--" + executionStatus);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {

			try {
				String string = null;
				string = EntityUtils.toString(entity);
				JSONObject executionResponseObj = new JSONObject(string);
//				cycleId = executionResponseObj.getString("clientMessage");
				// System.out.println(executionResponseObj.toString());
				throw new ClientProtocolException("Unexpected response status: " + statusCode);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}
		}
		return executionStatus;
	}

}
