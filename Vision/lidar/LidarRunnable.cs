using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using log4net;
using log4net.Repository.Hierarchy;
using System.Threading;
using Vision;
using System.Collections;

namespace lidar
{
    public class LidarRunnable
    {
        private static log4net.ILog logger = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
        private static double MAX_DEGREE = 720;

        private RplidarNative rplidar = null;
        private long driver = 0;
        volatile Boolean isRunning = true;

        // Current cached LIDAR scan result. It may combine several recent scans.
        CachedMeasurementNode[] cachedMeasurementNodes = new CachedMeasurementNode[(int)MAX_DEGREE];
        // Mutex for cachedMeasurementNodes.
        private static Semaphore semaphore = new Semaphore(1, 1);
        // Mutex for rplidar and driber.
        private static Semaphore semaphoreDriver = new Semaphore(1, 1);
        // This is empty if OK.
        String errorMessage = "";

        private double degree;

        public void run()
        {
            while (Thread.CurrentThread.ThreadState != ThreadState.Suspended && isRunning)
            {
                try
                {
                    Thread.Sleep(Constants.LIDAR_SLEEP_INTERVAL);
                }
                catch (ThreadInterruptedException e)
                {
                    Console.WriteLine(e.Message);
                    continue;
                }

                // Restart driver if necessary.
                //			if (!isHealthy() && !init()) {
                //				logger.debug("LidarRunnable is not healthy: " + errorMessage());
                //				continue;
                //			}

                MeasurementNode[] oneScan = null;
                try
                {
                    semaphoreDriver.WaitOne();
                    if (rplidar != null && driver > 0)
                    {
                        oneScan = RplidarNative.getScanData(driver);
                    }
                    // oneScan = getScanDataForTesting();
                    semaphoreDriver.Release();
                }
                catch (ThreadInterruptedException e)
                {
                    Console.WriteLine(e.Message);
                }

                if (oneScan == null)
                {
                    errorMessage = "RpLidar reports no scan result.";
                    continue;
                }
                addOneScanToCache(oneScan);
                reportSuccess();

            }
        }

        public Boolean init()
        {
            close();
            isRunning = true;
            Boolean is_connected = false;
            try
            {
                semaphoreDriver.WaitOne();
                rplidar = new RplidarNative();
                driver = RplidarNative.createDriver();
                if (driver > 0)
                {
                    //is_connected = RplidarNative.connect(driver, Constants.LIDAR_DEFAULT_PORT);
                    if (is_connected)
                    {
                        RplidarNative.startScan(driver);
                    }
                    else
                    {
                        errorMessage = "Failed to bind serial port " + Constants.LIDAR_DEFAULT_PORT + " for RpLidar driver.";
                    }
                }
                else
                {
                    errorMessage = "Failed to create RpLidar driver.";
                }
                semaphoreDriver.Release();
            }
            catch (ThreadInterruptedException e)
            {
                Console.WriteLine(e.Message);
            }
            return is_connected;
        }

        public void close()
        {
            try
            {
                semaphoreDriver.WaitOne();
                if (rplidar != null && driver > 0)
                {
                    RplidarNative.stopScan(driver);
                    RplidarNative.disposeDriver(driver);
                }
                rplidar = null;
                driver = 0;
                semaphoreDriver.Release();
            }
            catch (ThreadInterruptedException e)
            {
                Console.WriteLine(e.Message);
            }
            isRunning = false;
        }

        public Boolean isHealthy()
        {
            Boolean isLidarHealthy = false;
            try
            {
                semaphoreDriver.WaitOne();
                if (driver > 0)
                {
                    isLidarHealthy = RplidarNative.checkHealth(driver);
                    if (!isLidarHealthy)
                    {
                        errorMessage = "RpLidar is unhealthy.";
                    }
                }
                else
                {
                    errorMessage = "Failed to create RpLidar driver.";
                }
                semaphoreDriver.Release();
            }
            catch (ThreadInterruptedException e)
            {
                Console.WriteLine(e.Message);
            }
            return isLidarHealthy;
        }

        public List<MeasurementNode> getLidarInfos()
        {
            List<MeasurementNode> measurementNodes = new List<MeasurementNode>();
            try
            {
                semaphore.WaitOne();
                DateTime dt=new DateTime();
                DateTime dtStart = TimeZone.CurrentTimeZone.ToLocalTime(new DateTime(1970, 1, 1));
                TimeSpan toNow = dt.Subtract(dtStart);
                long now = toNow.Ticks;
                for (int degree = 0; degree < MAX_DEGREE; ++degree)
                {
                    if (cachedMeasurementNodes[degree] == null)
                    {
                        continue;
                    }
                    // Remove obsolete MeasurementNode.
                    if (now - cachedMeasurementNodes[degree].getTimestamp() > Constants.LIDAR_CACHE_MAX_AGE)
                    {
                        cachedMeasurementNodes[degree] = null;
                        continue;
                    }
                    measurementNodes.Add(cachedMeasurementNodes[degree].getNode());
                }
                semaphore.Release();
            }
            catch (ThreadInterruptedException e)
            {
                Console.WriteLine(e.Message);
            }
            return measurementNodes;
        }

        private void addOneScanToCache(MeasurementNode[] oneScan)
        {
            try
            {
                semaphore.WaitOne();
                DateTime dt = new DateTime();
                DateTime dtStart = TimeZone.CurrentTimeZone.ToLocalTime(new DateTime(1970, 1, 1));
                TimeSpan toNow = dt.Subtract(dtStart);
                long now = toNow.Ticks;
                foreach (MeasurementNode node in oneScan)
                {
                    if (node.getSyncQuality() <= 0)
                    {
                        continue;
                    }
                    if (node.getAngle() < 30 || (node.getAngle() < 360 && node.getAngle() > 330))
                    {
                        degree = Math.Round(node.getAngle());
                        cachedMeasurementNodes[(int)degree] = new CachedMeasurementNode(node, now);
                    }
                }
                semaphore.Release();
            }
            catch (ThreadInterruptedException e)
            {
                Console.WriteLine(e.Message);
            }
        }

        Random rand = new Random();

        // For testing.
        //private MeasurementNode[] getScanDataForTesting()
        //{
        //    List<MeasurementNode> nodes = new List<MeasurementNode>();
        //    for (int i = 0; i < 360; i++)
        //    {
        //        if (rand.nextBoolean())
        //        {
        //            continue;
        //        }
        //        nodes.Add(new MeasurementNode(1, i, rand.Next() * 4000.0f));
        //    }
        //    MeasurementNode[] nodesArray = new MeasurementNode[nodes.Count()];
        //    nodesArray = nodes.ToArray();
        //    return nodesArray;
        //}

        // For testing.
        //public List<MeasurementNode> getLidarInfosForTesting()
        //{
        //    List<MeasurementNode> nodes = new List<MeasurementNode>();
        //    for (int i = 0; i < 360; i++)
        //    {
        //        if (rand.nextBoolean())
        //        {
        //            continue;
        //        }
        //        nodes.Add(new MeasurementNode(1, i, rand.Next() * 4000.0f));
        //    }
        //    return nodes;
        //}

        // Reports success.
        private void reportSuccess()
        {
            errorMessage = "";
        }
    }
}
