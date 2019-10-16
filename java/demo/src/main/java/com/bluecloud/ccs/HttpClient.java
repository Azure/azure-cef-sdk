package com.bluecloud.ccs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.StringWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import com.bluecloud.model.*;
import com.bluecloud.utils.CEFUtils;
import com.google.gson.JsonObject;

public class HttpClient {

	/**
	 * 创建基于auth basic认证的CloseableHttpClient
	 * 
	 * @return
	 */
	private static CloseableHttpClient createCloseableHttpClient() {
		// UsernamePasswordCredentials credentials = new
		// UsernamePasswordCredentials("admin", "sysadmin");
		// BasicCredentialsProvider credentialsProvider = new
		// BasicCredentialsProvider();
		// credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		return httpClient;
    }
    

    public static void createSMSHttpPostClient(String account, JsonObject json) throws Exception {
		CloseableHttpClient httpClient = createCloseableHttpClient();
        String url = Config.CEFSERVICE_DEFAULT_ENDPOINT + "/services/sms/messages?api-version=2018-10-01";

        String token = CEFUtils.prepareSharedAccessToken();
        System.out.println(token);

		try {
			// Define a postRequest request
			HttpPost postRequest = new HttpPost(url);

			// Set the API media type in http content-type header
			postRequest.addHeader("content-type", "application/json");
			// postRequest.addHeader("Accept", "application/xml");
            postRequest.addHeader("Accept", "application/json");
            postRequest.addHeader("Account", account);


            postRequest.addHeader("Authorization", token);

			// Set the request post body
            StringEntity userEntity = new StringEntity(String.valueOf(json), 
            Charset.forName("utf-8"));// 设置发送的编码
			postRequest.setEntity(userEntity);

			// Send the request; It will immediately return the response in HttpResponse
			// object if any
			HttpResponse response = httpClient.execute(postRequest);

			// verify the valid error code first
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new RuntimeException("Failed with HTTP error code : " + statusCode);
			}
			// Now pull back the response object
			HttpEntity httpEntity = response.getEntity();
			String output = new String(EntityUtils.toByteArray(httpEntity), "utf-8");// 处理中文乱码
			// Lets see what we got from API
			System.out.println(output);
		} catch (IOException e){
			
			System.out.println(e.getMessage());
		}
		finally {
			// Important: Close the connect
			httpClient.close();
		}
	}





}