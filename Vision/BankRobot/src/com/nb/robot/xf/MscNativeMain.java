package com.nb.robot.xf;

import java.io.File;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

// Test Xunfei offline SDK.
public class MscNativeMain {

	public static void main(String[] args) {
		//runAsr();
		runTts();
	}

	public static void runAsr() {
		AsrNative asr_obj = new AsrNative();
		// asr_obj.hello();
		System.out.println("login: " + asr_obj.login());
		System.out.println("buildGrammar... ");
		//UserData udata = asr_obj.buildGrammar("bank.bnf");
		UserData udata = asr_obj.buildGrammar("demoApp.bnf");
		System.out.println("ret: " + udata.getErrorCode());
		int i = 0;
		while (i < 3) {
			i++;
			System.out.println("runAsr... ");
			udata = asr_obj.runAsr(udata);
			System.out.println(udata.errcode);
			if (!udata.getResult().isEmpty()) {
				parseAsrResult(udata.getResult());
			}
			if (i == 2) {
				System.out.println("updateLexicon... ");
				udata = asr_obj.updateLexicon(udata, "robotName", "大黑\n宁波");
				System.out.println("ret: " + udata.getErrorCode());
			}
		}
		System.out.println("logout: " + asr_obj.logout());
	}

	public static void runTts() {
		TtsNative tts_obj = new TtsNative();
		// tts_obj.hello();
		System.out.println("login: " + tts_obj.login());
		String audioFile = "tts_test.wav";
		int ret = tts_obj.runTts(audioFile, "拉斯维加斯夏季联赛继续进行，火箭在第二场对阵骑士", 10, 70, 10);
		System.out.println("ret: " + ret);
		System.out.println("logout: " + tts_obj.logout());
		playAudioFile(audioFile);
	}

	public static void playAudioFile(String filename) {
		System.out.println("playAudioFile: " + filename);
		try {
			Clip clip = AudioSystem.getClip();
			LineListener listener = new LineListener() {
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {
						clip.close();
					}
				}
			};
			clip.addLineListener(listener);
			clip.open(AudioSystem.getAudioInputStream(new File(filename)));
			clip.start();
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("playAudioFile: done");
	}
	

	static String testStr = "{\n" + 
			"  \"sn\":1,\n" + 
			"  \"ls\":true,\n" + 
			"  \"bg\":0,\n" + 
			"  \"ed\":0,\n" + 
			"  \"ws\":[{\n" + 
			"      \"bg\":0,\n" + 
			"      \"slot\":\"<robotName>\",\n" + 
			"      \"cw\":[{\n" + 
			"          \"id\":65535,\n" + 
			"          \"gm\":0,\n" + 
			"          \"sc\":62,\n" + 
			"          \"w\":\"小白\"\n" + 
			"        }]\n" + 
			"    },{\n" + 
			"      \"bg\":0,\n" + 
			"      \"slot\":\"<weather>\",\n" + 
			"      \"cw\":[{\n" + 
			"          \"id\":65535,\n" + 
			"          \"gm\":0,\n" + 
			"          \"sc\":0,\n" + 
			"          \"w\":\"天气\"\n" + 
			"        }]\n" + 
			"    },{\n" + 
			"      \"bg\":0,\n" + 
			"      \"slot\":\"好吗\",\n" + 
			"      \"cw\":[{\n" + 
			"          \"id\":65535,\n" + 
			"          \"gm\":0,\n" + 
			"          \"sc\":15,\n" + 
			"          \"w\":\"好吗\"\n" + 
			"        }]\n" + 
			"    }],\n" + 
			"  \"sc\":13\n" + 
			"}";

	static void parseAsrResult(String result) {
		JsonReader jsonReader = Json.createReader(new StringReader(result));
		JsonObject obj = jsonReader.readObject();
		// Overall score.
		System.out.println("score: " + obj.getInt("sc"));
		JsonArray wds = obj.getJsonArray("ws");
		for (int i = 0; i<wds.size(); i++) {
			JsonObject wd = wds.getJsonObject(i);
			// Slot.
			System.out.println("slot: " + wd.getString("slot"));
			JsonArray cws = wd.getJsonArray("cw");
			for (int j = 0; j<cws.size(); j++) {
				JsonObject cw = cws.getJsonObject(j);
				// Word.
				System.out.println("word: " + cw.getString("w"));
			}
		}
	}
}
