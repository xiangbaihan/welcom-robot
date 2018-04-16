using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Vision
{
    public class Constants
    {
        // Default sleep interval. In milliseconds.
        //public  static long DEFAULT_SLEEP_INTERVAL = 1000;
        public static int DEFAULT_SLEEP_INTERVAL = 300;
        // Default error code for OK.
        public static int DEFAULT_ERROR_CODE_OK = 0;

        // Default base URI. $IP$ will be replaced with local IP address.
        public static  String DEFAULT_BASE_URI = "http://$IP$:8080/";
	// Default server socket port.
	public  static int DEFAULT_SERVER_SOCKET_PORT = 9090;

        // Default LED state.
        public  static int DEFAULT_LED_STATE = 1;

        // 74 degree for Logitech C615 camera.
        // 78 degree for Logitech C920 camera.
        // 60 degree for Logitech C270 camera.
        //115 degree for Industrial camera.
        public  static double CAMERA_FIELD_OF_VIEW = 115;
        // Camera frame-per-second.
        // 30 for Logitech C615 camera.
        // 30 for Logitech C920 camera.
        public  static long CAMERA_FPS = 30;
        // 0 means default camera.
        public  static int DEFAULT_CAMERA_INDEX = 0;
        // Sleep interval for face detection module. In milliseconds.
        public  static long FACE_DETECTION_SLEEP_INTERVAL = DEFAULT_SLEEP_INTERVAL;
        // Sleep interval between two face detections performed on consecutive frames. In milliseconds.
        public  static long FACE_DETECTION_SLEEP_INTERVAL_BETWEEN_FRAME = 0;//20ms
                                                                                 // The number of consecutive frames that are counted together to report one detection result.
                                                                                 // This is to avoid false-positives.
        public  static int FACE_DETECTION_FRAME_COUNT_FOR_ONE_RESULT = 1;
        // Faces appear in over this ratio of all frames are considered as positive results.
        public  static double FACE_DETECTION_POSITIVE_RATIO = 0.4;
        // Whether to enable display on face detection.
        public  static Boolean FACE_DETECTION_ENABLE_DISPLAY = false;
        // Display frame refresh interval. In milliseconds.
        public  static int FACE_DETECTION_DISPLAY_REFRESH_INTERVAL = 50;

        // Sleep interval for LIDAR scan. In milliseconds.
        public  static int LIDAR_SLEEP_INTERVAL = DEFAULT_SLEEP_INTERVAL;
        // Default serial port used to connect to LIDAR.
        public  static String LIDAR_DEFAULT_PORT = "/dev/rplidar";
        // Max age of scan result that will be kept in cache. In milliseconds.
        public  static long LIDAR_CACHE_MAX_AGE = 3000;

        // Sleep interval for HumanDetectionModule. In milliseconds.
        public  static long HUMAN_DETECTION_MODULE_SLEEP_INTERVAL = DEFAULT_SLEEP_INTERVAL;
        // Small range circle for human detection. In millimeters.
        public  static float HUMAN_DETECTION_RANGE_SMALL = 1600f;
        // Large range circle for human detection. In millimeters.
        public  static float HUMAN_DETECTION_RANGE_LARGE = 2500f;
        // 0 +/- this angle degree is considered as frontal view. 
        public  static int HUMAN_DETECTION_RANGE_ANGLE = 60;
        // If faces disappear for such long time, we report humans have left. In milliseconds.
        //public  static long HUMAN_DETECTION_FACE_LOST_DURATION = 5 * 1000;
        public  static long HUMAN_DETECTION_FACE_LOST_DURATION = 5 * 1000;
        // Max allowed degree alignment difference between face (detected by camera) and object
        // (detected by LIDAR).
        public  static int HUMAN_DETECTION_MAX_DEGREE_ALIGNMENT_DIFF = 15;//5°

        // Sleep interval for SpeechRecognitionModule. In milliseconds.
        public  static long SPEECH_RECOGNITION_MODULE_SLEEP_INTERVAL = 200;
        // Default BNF file path.
        //public  static String SPEECH_RECOGNITION_DEFAULT_BNF = "./config/bank.bnf";
        // Recognition result with score lower than this number is considered as background noise.
        public  static long SPEECH_RECOGNITION_SCORE_THRESHOLD_NOISE = 5;
        // Only recognition result whose overall score is above this number is considered valid result.
        public  static long SPEECH_RECOGNITION_SCORE_THRESHOLD_VALID = 25;
        // Minimal interval to send socket messages on unrecognized speech. In milliseconds.
        public  static long SPEECH_UNRECOGNIZED_MESSAGE_INTERVAL = 5000;

        // Default audit file path for speech synthesis.
        public  static String SPEECH_SYNTHESIS_DEFAULT_FILE = "E:/BankRobot/audio.wav";
        // Sleep interval for audio file playing. In milliseconds.
        public  static long SPEECH_SYNTHESIS_REPEAT_SLEEP_INTERVAL = 200;

        // Time interval for chasis and LED's serial communication frequency. In milliseconds.
        public  static long CHASIS_LED_SERIAL_COM_TIME_INTERVAL = 50;
        // The minimal time interval of sending chasis location message. In milliseconds.
        public  static long CHASIS_LOCATION_NOTIFICATION_TIME_INTERVAL = 1500;
        // audio file prefix path 
        public  static String SPEECH_STATE_AUDIO_FILE_PREFIX = "/home/mj/workspace/BankRobot/";

        // Song file paths for dance module.
        public  static String DANCE_SONG_1 = "E:/BankRobot/1.wav";
        public  static String DANCE_SONG_2 = "E:/BankRobot/2.wav";
        public  static String DANCE_SONG_3 = "E:/BankRobot/3.wav";
    }
}
