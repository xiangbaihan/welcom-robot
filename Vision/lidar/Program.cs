using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace lidar
{
    public class Program
    {
        static void Main(String[] args)
        {
            RplidarNative rplidar = new RplidarNative();
            long driver = RplidarNative.createDriver();
            if (driver == 0)
            {
                Console.WriteLine("Failed to create driver...");
                return;
            }

            StringBuilder serialPort = new StringBuilder("\\\\.\\COM3");
            bool is_connected = RplidarNative.connect(driver, serialPort);
            if (!is_connected)
            {
                Console.WriteLine("Failed to bind " + serialPort);
                RplidarNative.disposeDriver(driver);
                return;
            }

            bool is_healthy = RplidarNative.checkHealth(driver);
            if (!is_healthy)
            {
                Console.WriteLine("RpLidar is not healthy.");
                RplidarNative.disposeDriver(driver);
                return;
            }

            RplidarNative.startScan(driver);
            MeasurementNode[] nodes = RplidarNative.getScanData(driver);
            if (nodes == null)
            {
                Console.WriteLine("No scan result.");
            }
            else
            {
                foreach (MeasurementNode node in nodes)
                {
                    Console.WriteLine(node.angle + " " + node.distance + " " + node.sync_quality);
                }
            }
            RplidarNative.stopScan(driver);
            RplidarNative.disposeDriver(driver);
        }
    }
}
