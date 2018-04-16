using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Vision;
using lidar;

namespace HumanDetectionResponse
{
    class HumanDetectionModule
    {
        private static log4net.ILog logger = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private static volatile HumanDetectionModule instance = null;
        private FaceDetectionRunnable faceDetectionRunnable = null;
        private Thread faceDetectionThread = null;
        private LidarRunnable lidarRunnable = null;
        private Thread lidarThread = null;
        private HumanDetectionRunnable humanDetectionRunnable = null;
        private Thread humanDetectionThread = null;
        private VideoFrame videoFrame = null;
        private Boolean isRunning = false;

        public static HumanDetectionModule getInstance()
        {
            if (instance == null)
            {
                lock(HumanDetectionModule) {
				if (instance == null) {
					instance = new HumanDetectionModule();
                    }
                }
            }
		return instance;
        }
    }
}
