# GitHub MD Preview
https://jbt.github.io/markdown-editor/

# com.nb.robot.lidar
To create JNI .h file:
* ~/workspace/BankRobot/src$ javah -jni com.nb.robot.lidar.RplidarNative
* ~/workspace/BankRobot/src$ cp com_nb_robot_lidar_RplidarNative.h ../rplidar_sdk/jni_lib/

To build LIDAR sample executables and JNI shared library:
* cd ~/workspace/BankRobot/rplidar_sdk/
* make
* cp output/Linux/Release/librplidar_native.so ~/workspace/BankRobot/lib/
* Add BankRobot/lib to "Native library locations" under "Project properties->Java Build Path-> Tab Source"


# com.nb.robot.serialComm
Maven should find and include rxtx library automatically. Otherwise, to install rxtx library:
* sudo apt-get install librxtx-java
* sudo cp /usr/lib/jni/librxtxSerial.so /opt/java/jdk1.8.0_05/jre/lib/amd64/
* sudo cp /usr/lib/jni/librxtxParallel.so /opt/java/jdk1.8.0_05/jre/lib/amd64/
* add /usr/share/java/RXTXcomm.jar to project Java build path

Permission issue: http://rxtx.qbang.org/wiki/index.php/Trouble_shooting
* sudo chmod o+rw /dev/ttyS*

Quota from INSTALL file of rxtx download package:

```
A person is added to group lock or uucp by editing /etc/groups. Distributions have various tools but this works: `lock:x:54:` becomes: `lock:x:53:jarvi,taj`. Now jarvi and taj are in group lock. Also make sure jarvi and taj have read and write permissions on the port.
```

To create a virtual serial port/device: https://stackoverflow.com/questions/52187/virtual-serial-port-for-linux

1. $ socat -d -d pty,raw,echo=0 pty,raw,echo=0
> socat[27201] N PTY is /dev/pts/18
> 
> socat[27201] N PTY is /dev/pts/26

2. $ sudo ln -s /dev/pts/18 /dev/ttyS99
> (/dev/ttyS99 is the virtual serial port)

3. $ cat < /dev/pts/18
> (This will print the content written into /dev/ttyS99)
****


# com.nb.robot.service

* Bug: when sending "status:false" (STOP) request to "/control" or "/speech/control API, the response may be "Connection RESET" or "Server not responding". But the request is actually processed, and the subsequent STOP request will also get OK status. START request does not have such issue though.


# com.nb.robot.speech
To run com.nb.robot.speech.MscTest:
* add Msc.jar dependency into lib
* copy libmsc32.so and libmsc64.so (from Xunfei Java SDK) to project root dir: ～/workspace/BankRobot/
* set LD_LIBRARY_PATH to dir of the above .so (one way is to set via Eclipse "Run Configuration" -> "Environment"): http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9864


# com.nb.robot.vision
To install OpenCV 3.2.0, and use it in EClipse:
http://docs.opencv.org/3.2.0/d7/d9f/tutorial_linux_install.html
http://docs.opencv.org/3.2.0/d9/d52/tutorial_java_dev_intro.html
http://docs.opencv.org/3.2.0/d1/d0a/tutorial_java_eclipse.html

Notes:
* Native library location for OpenCV-3.2.0 User Library: OpenCV-3.2.0\build\lib


# com.nb.robot.xf
To create JNI .h file:
1. cd ~/workspace/BankRobot/src
1. javah -jni com.nb.robot.xf.AsrNative
1. javah -jni com.nb.robot.xf.TtsNative
1. cp com_nb_robot_xf_AsrNative.h ../xf_linux/samples/asr_record_sample/
1. cp com_nb_robot_xf_TtsNative.h ../xf_linux/samples/tts_sample/

To build xf binary and shared library:
* cd ~/workspace/BankRobot/xf_linux/samples/asr_record_sample
* cd ~/workspace/BankRobot/xf_linux/samples/tts_sample
* source 64bit_make.sh
* cp ../../bin/libasr_native.so ~/workspace/BankRobot/lib/
* cp ../../bin/libtts_native.so ~/workspace/BankRobot/lib/
* Add BankRobot/lib to "Native library locations" under "Project properties->Java Build Path-> Tab Source"

To run JNI Java program:
* cp xf_linux/libs/x64/libmsc.so /usr/local/lib/
* (Running in terminal) export LD_LIBRARY_PATH=/usr/local/lib 
* (Running in Eclipse) Run configurations -> Environment -> new -> name=LD_LIBRARY_PATH, value=/usr/local/lib 
* Make sure "call.bnf" exist in ～/workspace/BankRobot/config/
* Copy offline resource files (~/workspace/BankRobot/xf_linux/bin/msc/res) to ～/workspace/BankRobot/msc/res

Notes:
* Both ASR login and TTS login will conflict and result in MSP_ERROR_NOT_INIT.
