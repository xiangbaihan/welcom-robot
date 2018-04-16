/*
 *  RPLIDAR
 *  Ultra Simple Data Grabber Demo App
 *
 *  Copyright (c) 2009 - 2014 RoboPeak Team
 *  http://www.robopeak.com
 *  Copyright (c) 2014 - 2016 Shanghai Slamtec Co., Ltd.
 *  http://www.slamtec.com
 *
 */
 /*
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  *
  */

#include <stdio.h>
#include <stdlib.h>

#include "rplidar.h" //RPLIDAR standard sdk, all-in-one header

#ifndef _countof
#define _countof(_Array) (int)(sizeof(_Array) / sizeof(_Array[0]))
#endif

#ifdef _WIN32
#include <Windows.h>
#define delay(x)   ::Sleep(x)
#else
#include <unistd.h>
static inline void delay(_word_size_t ms) {
	while (ms >= 1000) {
		usleep(1000 * 1000);
		ms -= 1000;
	};
	if (ms != 0)
		usleep(ms * 1000);
}
#endif

using namespace rp::standalone::rplidar;

bool checkRPLIDARHealth(RPlidarDriver * drv)
{
	u_result     op_result;
	rplidar_response_device_health_t healthinfo;


	op_result = drv->getHealth(healthinfo);
	if (IS_OK(op_result)) { // the macro IS_OK is the preperred way to judge whether the operation is succeed.
		printf("/nRPLidar health status : %d\n", healthinfo.status);
		if (healthinfo.status == RPLIDAR_STATUS_ERROR) {
			fprintf(stderr, "Error, rplidar internal error detected. Please reboot the device to retry.\n");
			// enable the following code if you want rplidar to be reboot by software
			// drv->reset();
			return false;
		}
		else {
			return true;
		}

	}
	else {
		fprintf(stderr, "Error, cannot retrieve the lidar health code: %x\n", op_result);
		return false;
	}
}

#include <signal.h>
bool ctrl_c_pressed;
void ctrlc(int)
{
	ctrl_c_pressed = true;
}

int main(int argc, const char * argv[]) {
	const char * opt_com_path = NULL;
	_u32         baudrateArray[2] = { 115200, 256000 };
	_u32         opt_com_baudrate = 0;
	u_result     op_result;

	bool useArgcBaudrate = false;

	printf("Ultra simple LIDAR data grabber for RPLIDAR.\n"
		"Version: ""\n");

	// read serial port from the command line...
	if (argc > 1) opt_com_path = argv[1]; // or set to a fixed value: e.g. "com3" 

	// read baud rate from the command line if specified...
	if (argc > 2)
	{
		opt_com_baudrate = strtoul(argv[2], NULL, 10);
		useArgcBaudrate = true;
	}

	if (!opt_com_path) {
#ifdef _WIN32
		// use default com port
		opt_com_path = "\\\\.\\com3";
#else
		opt_com_path = "/dev/ttyUSB0";
#endif
	}

	// create the driver instance
	RPlidarDriver * drv = RPlidarDriver::CreateDriver(DRIVER_TYPE_SERIALPORT);
	if (!drv) {
		fprintf(stderr, "insufficent memory, exit\n");
		exit(-2);
	}

	rplidar_response_device_info_t devinfo;
	bool connectSuccess = false;
	// make connection...
	if (useArgcBaudrate)
	{
		if (!drv)
			drv = RPlidarDriver::CreateDriver(DRIVER_TYPE_SERIALPORT);
		if (IS_OK(drv->connect(opt_com_path, opt_com_baudrate)))
		{
			op_result = drv->getDeviceInfo(devinfo);

			if (IS_OK(op_result))
			{
				connectSuccess = true;
			}
			else
			{
				delete drv;
				drv = NULL;
			}
		}
	}
	else
	{
		size_t baudRateArraySize = (sizeof(baudrateArray)) / (sizeof(baudrateArray[0]));
		for (size_t i = 0; i < baudRateArraySize; ++i)
		{
			if (!drv)
				drv = RPlidarDriver::CreateDriver(DRIVER_TYPE_SERIALPORT);
			if (IS_OK(drv->connect(opt_com_path, baudrateArray[i])))
			{
				op_result = drv->getDeviceInfo(devinfo);

				if (IS_OK(op_result))
				{
					connectSuccess = true;
					break;
				}
				else
				{
					delete drv;
					drv = NULL;
				}
			}
		}
	}
	if (!connectSuccess) {

		fprintf(stderr, "Error, cannot bind to the specified serial port %s.\n"
			, opt_com_path);
		goto on_finished;
	}

	// print out the device serial number, firmware and hardware version number..
	printf("RPLIDAR S/N: ");
	for (int pos = 0; pos < 16; ++pos) {
		printf("%02X", devinfo.serialnum[pos]);
	}

	printf("\n"
		"Firmware Ver: %d.%02d\n"
		"Hardware Rev: %d\n"
		, devinfo.firmware_version >> 8
		, devinfo.firmware_version & 0xFF
		, (int)devinfo.hardware_version);



	// check health...
	if (!checkRPLIDARHealth(drv)) {
		goto on_finished;
	}

	signal(SIGINT, ctrlc);

	drv->startMotor();
	// start scan...
	drv->startScan(0, 1);

	// fetech result and print it out...
	while (1) {
		rplidar_response_measurement_node_t nodes[8192];
		size_t   count = _countof(nodes);

		op_result = drv->grabScanData(nodes, count);

		if (IS_OK(op_result)) {
			drv->ascendScanData(nodes, count);
			for (int pos = 0; pos < (int)count; ++pos) {
				printf("%s theta: %03.2f Dist: %08.2f Q: %d \n",
					(nodes[pos].sync_quality & RPLIDAR_RESP_MEASUREMENT_SYNCBIT) ? "S " : "  ",
					(nodes[pos].angle_q6_checkbit >> RPLIDAR_RESP_MEASUREMENT_ANGLE_SHIFT) / 64.0f,
					nodes[pos].distance_q2 / 4.0f,
					nodes[pos].sync_quality >> RPLIDAR_RESP_MEASUREMENT_QUALITY_SHIFT);
			}
		}

		if (ctrl_c_pressed) {
			break;
		}
	}

	drv->stop();
	drv->stopMotor();
	// done!
on_finished:
	RPlidarDriver::DisposeDriver(drv);
	drv = NULL;
	return 0;
}

/*
* Class:     com_nb_robot_lidar_RplidarNative
* Method:    hello
* Signature: ()V
*/
extern "C" void _declspec(dllexport) Hello() {
	printf("Hello from C...");
}

/*
* Class:     com_nb_robot_lidar_RplidarNative
* Method:    createDriver
* Signature: ()J
*/
extern "C" long _declspec(dllexport) CreateDriver() {
	RPlidarDriver *driver = RPlidarDriver::CreateDriver(
		RPlidarDriver::DRIVER_TYPE_SERIALPORT);
	return (long)driver;
}

/*
* Class:     com_nb_robot_lidar_RplidarNative
* Method:    connect
* Signature: (JLjava/lang/String;)Z
*/
void GBKorUTF(LPSTR text, int maxLen, int whichone);

extern "C" boolean _declspec(dllexport) Connect
(long long driver_address, char* port_path){
	RPlidarDriver* driver = (RPlidarDriver*)driver_address;
	const char* native_port_path = port_path;
	_u32 opt_com_baudrate = 115200;
	fprintf(stderr, port_path);
	bool is_ok = IS_OK(driver->connect(native_port_path, opt_com_baudrate));
	if (!is_ok) {
		fprintf(stderr, "Error, cannot bind to the specified serial port %s.\n", native_port_path);
	}
	return is_ok;
}

void GBKorUTF(LPSTR text, int maxLen, int whichone)
{
	//this conversion is between GBK & UTF8 and written based on UTF8toGBK
	int len;
	wchar_t * wszGBK;
	char *szGBK;

	int n = strlen(text);
	if (n > maxLen) {
		n = maxLen - 1;
	}

	if (whichone == 0)//GBK
	{
		len = MultiByteToWideChar(CP_UTF8, 0, text, -1, NULL, 0);
		wszGBK = new wchar_t[len * sizeof(wchar_t)];
		MultiByteToWideChar(CP_UTF8, 0, (LPCTSTR)text, -1, wszGBK, len);

		len = WideCharToMultiByte(CP_ACP, 0, wszGBK, -1, NULL, 0, NULL, NULL);
		szGBK = new char[(len + 1) * sizeof(wchar_t)];
		WideCharToMultiByte(CP_ACP, 0, wszGBK, -1, szGBK, len, NULL, NULL);

		strncpy(text, szGBK, len + 1);
		text[len] = '\0';

		free(wszGBK);
		free(szGBK);
		wszGBK = NULL;
		szGBK = NULL;

		fprintf(stderr, "Info:Result is %s\n", text);
	}
	else//UTF8
	{
		len = MultiByteToWideChar(CP_ACP, 0, text, -1, NULL, 0);
		wszGBK = new wchar_t[len * sizeof(wchar_t)];
		MultiByteToWideChar(CP_ACP, 0, (LPCTSTR)text, -1, wszGBK, len);

		len = WideCharToMultiByte(CP_UTF8, 0, wszGBK, -1, NULL, 0, NULL, NULL);
		szGBK = new char[(len + 1) * sizeof(wchar_t)];
		WideCharToMultiByte(CP_UTF8, 0, wszGBK, -1, szGBK, len, NULL, NULL);

		strncpy(text, szGBK, len + 1);
		text[len] = '\0';

		free(wszGBK);
		free(szGBK);
		wszGBK = NULL;
		szGBK = NULL;

		fprintf(stderr, "Info:Result is %s\n", text);
	}
}

/*
* Class:     com_nb_robot_lidar_RplidarNative
* Method:    disposeDriver
* Signature: (J)V
*/
extern "C" void _declspec(dllexport) DisposeDriver
(long long driver_address) {
	RPlidarDriver* driver = (RPlidarDriver*)driver_address;
	RPlidarDriver::DisposeDriver(driver);
}

/*
* Class:     com_nb_robot_lidar_RplidarNative
* Method:    checkHealth
* Signature: (J)Z
*/
extern "C" boolean _declspec(dllexport) CheckHealth(
	long long driver_address) {
	RPlidarDriver* driver = (RPlidarDriver*)driver_address;
	return checkRPLIDARHealth(driver);
}

/*
* Class:     com_nb_robot_lidar_RplidarNative
* Method:    startScan
* Signature: (J)V
*/
extern "C" void _declspec(dllexport) StartScan
(long long driver_address) {
	RPlidarDriver* driver = (RPlidarDriver*)driver_address;
	driver->startMotor();
	driver->startScan(0, 1);
}

/*
* Class:     com_nb_robot_lidar_RplidarNative
* Method:    stopScan
* Signature: (J)V
*/
extern "C" void _declspec(dllexport) StopScan
(long long driver_address) {
	RPlidarDriver* driver = (RPlidarDriver*)driver_address;
	driver->stop();
	driver->stopMotor();
}

//Copy MeasurementNode in C to Java UserData.
//const rplidar_response_measurement_node_t &measurement_node;
extern "C" int _declspec(dllexport) CopySync_quality(const rplidar_response_measurement_node_t &measurement_node) {
	int sync_quality = measurement_node.sync_quality
		>> RPLIDAR_RESP_MEASUREMENT_QUALITY_SHIFT;
	return sync_quality;
}

extern "C" float _declspec(dllexport) CopyAngle(const rplidar_response_measurement_node_t &measurement_node) {
	float angle = (measurement_node.angle_q6_checkbit
		>> RPLIDAR_RESP_MEASUREMENT_ANGLE_SHIFT) / 64.0f;
	return angle;
}

extern "C" float _declspec(dllexport) CopyDistance(const rplidar_response_measurement_node_t &measurement_node) {
	float distance = measurement_node.distance_q2 / 4.0f;
	return distance;
}

//// Testing only.
//extern "C" void _declspec(dllexport) populateMeasurementNodesForTesting(rplidar_response_measurement_node_t *nodes) {
//	size_t count = _countof(nodes);
//	for (int pos = 0; pos < (int)count; ++pos) {
//		nodes[pos].sync_quality = 0;
//		nodes[pos].angle_q6_checkbit = 1;
//		nodes[pos].distance_q2 = 2;
//	}
//}

/*
* Class:     com_nb_robot_lidar_RplidarNative
* Method:    getScanData
* Signature: (J)[Lcom/nb/robot/lidar/MeasurementNode;
*/
extern "C" long long _declspec(dllexport) GetScanData(
	long long driver_address) {
	RPlidarDriver* driver = (RPlidarDriver*)driver_address;
	rplidar_response_measurement_node_t nodes[360];
	size_t count = _countof(nodes);
	u_result op_result;
	op_result = driver->grabScanData(nodes, count);
	//if (IS_OK(op_result)) {
	//	jobjectArray result = env->NewObjectArray(count,
	//		env->FindClass("com/nb/robot/lidar/MeasurementNode"), NULL);
	//	driver->ascendScanData(nodes, count);
	//	// populateMeasurementNodesForTesting(nodes);  // testing
	//	for (int pos = 0; pos < (int)count; ++pos) {
	//		env->SetObjectArrayElement(result, pos,
	//			copyMeasurementNodeToJavaObject(env, nodes[pos]));
	//	}
	//	return result;
	//}
	if (IS_OK(op_result)) {
		driver->ascendScanData(nodes, count);
		for (int pos = 0; pos < (int)count; ++pos) {
			printf("%s theta: %03.2f Dist: %08.2f Q: %d \n",
				(nodes[pos].sync_quality & RPLIDAR_RESP_MEASUREMENT_SYNCBIT) ? "S " : "  ",
				(nodes[pos].angle_q6_checkbit >> RPLIDAR_RESP_MEASUREMENT_ANGLE_SHIFT) / 64.0f,
				nodes[pos].distance_q2 / 4.0f,
				nodes[pos].sync_quality >> RPLIDAR_RESP_MEASUREMENT_QUALITY_SHIFT);
		}
	}
	fprintf(stderr, "Error, rplidar grabScanData failed. Error code: %x\n", op_result);
	return NULL;
}
