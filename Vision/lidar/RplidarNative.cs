using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Runtime.InteropServices;

namespace lidar
{
    public class RplidarNative
    {
        [DllImport("ultra_simple.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi, EntryPoint = "Hello")]
        public extern static void hello();

        // Creates RPLIDAR driver and returns pointer address.
        [DllImport("ultra_simple.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi, EntryPoint = "CreateDriver")]
        public extern static long createDriver();
        // Opens the specified serial port and connects to the target RPLIDAR device.
        [DllImport("ultra_simple.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi, EntryPoint = "Connect")]
        public extern static bool connect(long driver_address, [InAttribute()] [MarshalAsAttribute(UnmanagedType.LPStr)] StringBuilder portPath);

        // Disposes RPLIDAR driver.
        [DllImport("ultra_simple.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi, EntryPoint = "DisposeDriver")]
        public extern static void disposeDriver(long driver_address);

        // Returns if the RPLIDAR driver is healthy.
        [DllImport("ultra_simple.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi, EntryPoint = "CheckHealth")]
        public extern static bool checkHealth(long driver_address);

        // Starts motor and scanning.
        [DllImport("ultra_simple.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi, EntryPoint = "StartScan")]
        public extern static void startScan(long driver_address);

        // Stops motor and scanning.
        [DllImport("ultra_simple.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi, EntryPoint = "StopScan")]
        public extern static void stopScan(long driver_address);

        // Returns a list of MeasurementNode. The list size depends on the underlying RPLIDAR
        // scan result, which varies from one scan to another.
        [DllImport("ultra_simple.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi, EntryPoint = "GrabScanData()")]
        public extern static MeasurementNode[] getScanData(long driver_address);

        //Get some datas
        [DllImport("ultra_simple.dll", EntryPoint = "CopySync_quality")]
        public extern static int CopySync_quality();
        [DllImport("ultra_simple.dll", EntryPoint = "CopyAngle")]
        public extern static int CopyAngle();
        [DllImport("ultra_simple.dll", EntryPoint = "CopyDistance")]
        public extern static int CopyDistance();

    }
}
