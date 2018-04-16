package com.nb.robot.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;
import com.nb.robot.common.Pair;

public class SpeechRecognitionUtils {
	private static Logger logger = Logger.getLogger(SpeechRecognitionUtils.class);
	
	static SpeechRecognitionResponse parseAsrResult(String result) {
		if (result == null) {
			return null;
		}
		logger.trace("ASR result:\n" + result);
		SpeechRecognitionResponse response = null;
		if (result.isEmpty()) {
			return response;
		}
		JsonReader jsonReader = Json.createReader(new StringReader(result));
		if (jsonReader == null) {
			return response;
		}
		JsonObject jsonObject = jsonReader.readObject();
		if (jsonObject == null) {
			return response;
		}
		
		String fullText = "";
		List<Pair<String, String>> keywords = new ArrayList<Pair<String, String>>();
		// Overall score.
		int score = jsonObject.getInt("sc");
		if (score < Constants.SPEECH_RECOGNITION_SCORE_THRESHOLD_NOISE) {
			logger.trace("Skipping very low score result (due to background noise?):\n" + result);
			return null;
		}
		if (score < Constants.SPEECH_RECOGNITION_SCORE_THRESHOLD_VALID) {
			logger.trace("Skipping low score result:\n" + result);
			response = new SpeechRecognitionResponse(fullText, keywords,
		    		/*isRecognized*/false, score, System.currentTimeMillis());
			return response;
		}
		JsonArray words = jsonObject.getJsonArray("ws");
		for (int i = 0; i < words.size(); i++) {
			JsonObject word = words.getJsonObject(i);
			String slot = word.getString("slot");
			String keyword = "";
			JsonArray cws = word.getJsonArray("cw");
			// Only keep the word with max score.
			int maxWordScore = -1;
			for (int j = 0; j<cws.size(); j++) {
				JsonObject cw = cws.getJsonObject(j);
				int wordScore = cw.getInt("sc");
				if (wordScore > maxWordScore) {
					maxWordScore = wordScore;
					keyword = cw.getString("w");
				}
			}
			fullText += keyword;
			keywords.add(new Pair<String, String>(slot, keyword));
		}
		response = new SpeechRecognitionResponse(fullText, keywords,
	    		/*isRecognized*/true, score, System.currentTimeMillis());
		return response;
	}

}
