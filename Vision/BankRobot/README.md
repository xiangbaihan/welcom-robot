# BankRobot
(Under active development)

## Code Structure

* android/: Android Application 
* config/: configuration files and useful scripts
* html/: html files for HTTP Server
* lib/: shared libs for Java
    * libasr_native.so: JNI shared library for Xunfei ASR (语音识别)
    * libtts_native.so: JNI shared library for Xunfei TTS (语音合成)
    * librplidar_native.so: JNI shared library for RpLidar
* msc/: Xunfei resource files, needed when running speech recognition and synthesis module
* rplidar_sdk/: RpLidar SDK (in c++)
* src/: Robot platform service (in Java)
    * (package) com.nb.robot.common: common data structures, interfaces and utils
    * (package) com.nb.robot.lidar: JNI wrapper of RpLidar SDK, object distance detection module
    * (package) com.nb.robot.serialComm: serial communication for motion control module and expression module
    * (package) com.nb.robot.server: HTTP server
    * (package) com.nb.robot.service: RESTful resource and interface
    * (package) com.nb.robot.speech: speech recognition module, using Xunfei online SDK (**NOT** used as of 2017/06)
    * (package) com.nb.robot.video: video chat module (**NOT** used as of 2017/06)
    * (package) com.nb.robot.vision: computer vision moduel for face detection
    * (package) com.nb.robot.xf: JNI wrapper of Xunfei offline SDK, for speech recognition and synthesis module
* xf_linux/: Xunfei offline SDK, including 离线命令词识别 and 离线语音合成

* bank.bnf & call.bnf: Xunfei BNF file, needed when running speech recognition module
* haarcascade_*.xml: classification file for human faces, needed when running face detection module
* INSTALL.md: instructions on installation
* NOTES.md: some random notes during development
* pom.xml: Maven config for Jersey library.

