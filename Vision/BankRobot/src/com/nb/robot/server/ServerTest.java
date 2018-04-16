package com.nb.robot.server;

import java.io.File;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nb.robot.common.Pair;

public class ServerTest {

	public static void main(String[] args) {
		// rootControl();
		// speechControl();
		// speechUpload();
		//speechTalk();
		//controlDance();
		// controlMotion();
		batteryPercent();
	}

	private static HttpClient getHttpClient() {
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		// Set timeout to 10s because this is a blocking API at server side.
		requestBuilder = requestBuilder.setConnectTimeout(10 * 1000);
		requestBuilder = requestBuilder.setConnectionRequestTimeout(10 * 1000);

		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultRequestConfig(requestBuilder.build());
		return builder.build();
	}

	private static void rootControl() {
		try {
			HttpClient client = getHttpClient();
			URI baseURI = UriUtils.getBaseURI();
			HttpPost post = new HttpPost(baseURI.toString() + "/control");

			String jsonString = "";
			jsonString = new JSONObject().put("state", false).toString();
			StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void speechControl() {
		try {
			HttpClient client = getHttpClient();
			URI baseURI = UriUtils.getBaseURI();
			HttpPost post = new HttpPost(baseURI.toString() + "/speech/control");

			String jsonString = "";
			jsonString = new JSONObject().put("state", true).toString();
			StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void speechUpload() {
		try {
			HttpClient client = getHttpClient();
			URI baseURI = UriUtils.getBaseURI();
			HttpPost post = new HttpPost(baseURI.toString() + "/speech/upload");

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			File uploadFile = new File("bank2.bnf");
			// entityBuilder.addTextBody("name", "bank.bnf");
			entityBuilder.addPart("file", new FileBody(uploadFile));
			HttpEntity entity = entityBuilder.build();
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void controlMotion() {
		try {
			HttpClient client = getHttpClient();
			URI baseURI = UriUtils.getBaseURI();
			HttpPost post = new HttpPost(baseURI.toString() + "/motion/dynamicExpresssion");
			String jsonString = "";
			jsonString = new JSONObject().put("emotion", 1).put("emotionDuration", 5000).put("emotionRepeat", 3)
					.toString();
			/*
			 * HttpPost post=new HttpPost(baseURI.toString()+"/motion/chasis");
			 * String jsonString=""; jsonString = new JSONObject()
			 * .put("motionAction", 1) .put("motionSpeed", 500)
			 * .put("motionDuration", 10000) .toString();
			 */
			StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void speechTalk() {
		try {
			HttpClient client = getHttpClient();
			URI baseURI = UriUtils.getBaseURI();
			HttpPost post = new HttpPost(baseURI.toString() + "/speaker/talk");

			String talkString = "";
			talkString = new JSONObject().put("content", "控制机器人说出的指定文本内容").put("speed", 30).put("volume", 50)
					.put("pitch", 50).put("repeat", 3).toString();
			StringEntity requestEntity = new StringEntity(talkString, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));

			// Stop speaker module and stop playing audio after 10s.
			Thread.sleep(10000);
			post = new HttpPost(baseURI.toString() + "/speaker/control");
			String stopString = "";
			stopString = new JSONObject().put("state", "false").toString();
			requestEntity = new StringEntity(stopString, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			response = client.execute(post);
			httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));

			// Enable speaker module and stop playing audio after 5s.
			Thread.sleep(5000);
			post = new HttpPost(baseURI.toString() + "/speaker/control");
			String startString = "";
			startString = new JSONObject().put("state", "true").toString();
			requestEntity = new StringEntity(startString, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			response = client.execute(post);
			httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));

			// Another text to speak.
			Thread.sleep(5000);
			post = new HttpPost(baseURI.toString() + "/speaker/talk");
			String talkString2 = "";
			talkString2 = new JSONObject().put("content", "表示合成语音的语速，取值范围为0~100").put("speed", 80).put("volume", 70)
					.put("pitch", 50).put("repeat", 1).toString();
			requestEntity = new StringEntity(talkString2, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			response = client.execute(post);
			httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void controlDance() {
		try {
			HttpClient client = getHttpClient();

			URI baseURI = UriUtils.getBaseURI();
			HttpPost post = new HttpPost(baseURI.toString() + "/dance/control");
			String jsonString = "";
			jsonString = new JSONObject().put("duration", 0).put("repeat", 1).put("type", 2).put("muteFlag", false)
					.toString();

			StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));

			Thread.sleep(5000);
			HttpPost post1 = new HttpPost(baseURI.toString() + "/dance/danceFlag");
			String stopString = "";
			stopString = new JSONObject().put("state", "false").toString();
			StringEntity requestEntity1 = new StringEntity(stopString, ContentType.APPLICATION_JSON);
			post1.setEntity(requestEntity1);
			HttpResponse response1 = client.execute(post1);
			HttpEntity httpEntity1 = response1.getEntity();
			System.out.println(EntityUtils.toString(httpEntity1));

			Thread.sleep(15000);
			post = new HttpPost(baseURI.toString() + "/dance/danceFlag");
			String startString = "";
			startString = new JSONObject().put("state", "true").toString();
			requestEntity = new StringEntity(startString, ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);
			response = client.execute(post);
			httpEntity = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void batteryPercent() {
		try {
			HttpClient client = getHttpClient();

			URI baseURI = UriUtils.getBaseURI();
			HttpGet get = new HttpGet(baseURI.toString() + "/battery");
			HttpResponse response = client.execute(get);
			HttpEntity httpEntity1 = response.getEntity();
			System.out.println(EntityUtils.toString(httpEntity1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
