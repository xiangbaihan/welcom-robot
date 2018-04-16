package com.nb.robot.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class PropertiesUtil {
	private static InputStream inStream = ClassLoader.getSystemResourceAsStream("ports.properties");
	private static Properties prop = new Properties();
	static {
		try {
			prop.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static HashMap<String, String> loaderMap = new HashMap<String, String>();

	public static String getPropFromProperties(String propName) {
		String pString = loaderMap.get(propName);
		if (pString == null) {
			pString = prop.getProperty(propName);
			loaderMap.put(propName, pString);
		}
		return pString;
	}
}
