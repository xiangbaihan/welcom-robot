# Environment
* OS: Ubuntu 14.04 LTS (64-bit)

# Dependencies
* Java SE 1.8: /opt/java/jdk1.8.0_05/
* opencv-3.2.0: ~/Projects/opencv-3.2.0
* Xunfei Linux offline SDK: ~/workspace/BankRobot/xf_linux/
* RpLidar SDK: ~/workspace/BankRobot/rplidar_sdk/

# Installation
1. Install Java, OpenCV
1. Use Eclipse to open BankRobot project
1. Resolve all compilation errors  (check notes.md for some hint on each individual package)
1. Run com.nb.robot.server.ServerMain to launch web service
1. Run DemoApplication on Android

# Auto-run JAR after startup
1. sudo cp ~/workspace/BankRobot/config/bankrobot-start.sh /usr/local/bin/
1. sudo cp ~/workspace/BankRobot/config/bankrobot-stop.sh /usr/local/bin/
1. sudo cp ~/workspace/BankRobot/config/bankrobot-script /etc/init.d/
1. sudo update-rc.d bankrobot-script defaults 

(Reference: https://askubuntu.com/questions/99232/how-to-make-a-jar-file-run-on-startup-and-when-you-log-out)

# How to set persistent names for usb-serial devices
http://hintshop.ludvig.co.nz/show/persistent-names-usb-serial-devices/
