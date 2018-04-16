package com.nb.robot.common;

public class Constants {
	// Default sleep interval. In milliseconds.
	//public final static long DEFAULT_SLEEP_INTERVAL = 1000;
	public final static long DEFAULT_SLEEP_INTERVAL = 300;
	// Default error code for OK.
	public final static int DEFAULT_ERROR_CODE_OK = 0;

	// Default base URI. $IP$ will be replaced with local IP address.
	public static final String DEFAULT_BASE_URI = "http://$IP$:8080/";
	// Default server socket port.
	public final static int DEFAULT_SERVER_SOCKET_PORT = 9090;
	
	// Default LED state.
	public final static int DEFAULT_LED_STATE = 1;
	
	// 74 degree for Logitech C615 camera.
	// 78 degree for Logitech C920 camera.
	// 60 degree for Logitech C270 camera.
	//115 degree for Industrial camera.
	public final static double CAMERA_FIELD_OF_VIEW = 115;
	// Camera frame-per-second.
	// 30 for Logitech C615 camera.
	// 30 for Logitech C920 camera.
	public final static long CAMERA_FPS = 30;
	// 0 means default camera.
	public final static int DEFAULT_CAMERA_INDEX = 0;
	// Sleep interval for face detection module. In milliseconds.
	public final static long FACE_DETECTION_SLEEP_INTERVAL = DEFAULT_SLEEP_INTERVAL;
	// Sleep interval between two face detections performed on consecutive frames. In milliseconds.
	public final static long FACE_DETECTION_SLEEP_INTERVAL_BETWEEN_FRAME = 0;//20ms
	// The number of consecutive frames that are counted together to report one detection result.
	// This is to avoid false-positives.
	public final static int FACE_DETECTION_FRAME_COUNT_FOR_ONE_RESULT = 1;
	// Faces appear in over this ratio of all frames are considered as positive results.
	public final static double FACE_DETECTION_POSITIVE_RATIO = 0.4;
	// Whether to enable display on face detection.
	public final static boolean FACE_DETECTION_ENABLE_DISPLAY = false;
	// Display frame refresh interval. In milliseconds.
	public final static long FACE_DETECTION_DISPLAY_REFRESH_INTERVAL = 50;
	
	// Sleep interval for LIDAR scan. In milliseconds.
	public final static long LIDAR_SLEEP_INTERVAL = DEFAULT_SLEEP_INTERVAL;
	// Default serial port used to connect to LIDAR.
	public final static String LIDAR_DEFAULT_PORT = "/dev/rplidar";
	// Max age of scan result that will be kept in cache. In milliseconds.
	public final static long LIDAR_CACHE_MAX_AGE = 3000;
	
	// Sleep interval for HumanDetectionModule. In milliseconds.
	public final static long HUMAN_DETECTION_MODULE_SLEEP_INTERVAL = DEFAULT_SLEEP_INTERVAL;
	// Small range circle for human detection. In millimeters.
	public final static float HUMAN_DETECTION_RANGE_SMALL = 1600f;
	// Large range circle for human detection. In millimeters.
	public final static float HUMAN_DETECTION_RANGE_LARGE = 2500f;
	// 0 +/- this angle degree is considered as frontal view. 
	public final static int HUMAN_DETECTION_RANGE_ANGLE = 60;
	// If faces disappear for such long time, we report humans have left. In milliseconds.
	//public final static long HUMAN_DETECTION_FACE_LOST_DURATION = 5 * 1000;
	public final static long HUMAN_DETECTION_FACE_LOST_DURATION = 5 * 1000;
	// Max allowed degree alignment difference between face (detected by camera) and object
	// (detected by LIDAR).
	public final static int HUMAN_DETECTION_MAX_DEGREE_ALIGNMENT_DIFF = 15;//5°
	
	// Sleep interval for SpeechRecognitionModule. In milliseconds.
	public final static long SPEECH_RECOGNITION_MODULE_SLEEP_INTERVAL = 200;
	// Default BNF file path.
	//public final static String SPEECH_RECOGNITION_DEFAULT_BNF = "./config/bank.bnf";
	// Recognition result with score lower than this number is considered as background noise.
	public final static long SPEECH_RECOGNITION_SCORE_THRESHOLD_NOISE = 5;
	// Only recognition result whose overall score is above this number is considered valid result.
	public final static long SPEECH_RECOGNITION_SCORE_THRESHOLD_VALID = 25;
	// Minimal interval to send socket messages on unrecognized speech. In milliseconds.
	public final static long SPEECH_UNRECOGNIZED_MESSAGE_INTERVAL = 5000;
	
	// Default audit file path for speech synthesis.
	public final static String SPEECH_SYNTHESIS_DEFAULT_FILE = "/home/mj/workspace/BankRobot/audio.wav";
	// Sleep interval for audio file playing. In milliseconds.
	public final static long SPEECH_SYNTHESIS_REPEAT_SLEEP_INTERVAL = 200;
	
	// Time interval for chasis and LED's serial communication frequency. In milliseconds.
	public final static long CHASIS_LED_SERIAL_COM_TIME_INTERVAL = 50;
	// The minimal time interval of sending chasis location message. In milliseconds.
	public final static long CHASIS_LOCATION_NOTIFICATION_TIME_INTERVAL = 1500;
	// audio file prefix path 
	public final static String SPEECH_STATE_AUDIO_FILE_PREFIX = "/home/mj/workspace/BankRobot/";
	
	// Song file paths for dance module.
	public final static String DANCE_SONG_1 = "/home/mj/workspace/BankRobot/1.wav";
	public final static String DANCE_SONG_2 = "/home/mj/workspace/BankRobot/2.wav";
	public final static String DANCE_SONG_3 = "/home/mj/workspace/BankRobot/3.wav";
}
