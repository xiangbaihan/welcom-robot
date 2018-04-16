using System;
using System.Collections.Generic;
using Emgu.CV;
using Emgu.CV.Structure;
using System.Threading;
using System.Drawing;

namespace Vision
{
    public class FaceDetectionRunnable
    {
        private static log4net.ILog logger = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        VideoCapture cap = null;
        FaceDetector faceDetector = new FaceDetector();
        Boolean isRunning = true;
        private static Semaphore semaphoreCap = new Semaphore(1,1);
        private static Semaphore semaphoreFaceInfos = new Semaphore(1, 1);

        //current frame in mat format;
        Mat curMat = new Mat();
        //used to convert mat to bufferedImage;
        Mat2Image mat2Img = new Mat2Image();
        // Current face detection result;
        List<FaceInfo> curFaceInfos = new List<FaceInfo>();
        // Previous face detection result;
        List<FaceInfo> prevFaceInfos = new List<FaceInfo>();

        // This is empty if OK.
        String errorMessage = "";

        public void run()
        {
            while (Thread.CurrentThread.ThreadState!=ThreadState.Suspended && isRunning)//存疑
            {
                try
                {
                    Thread.Sleep((int)Constants.FACE_DETECTION_SLEEP_INTERVAL);
                }
                catch (ThreadInterruptedException e)
                {
                    Console.WriteLine(e.Message);
                    continue;
                }
                //logger.debug("当前人脸检测的时间"+System.currentTimeMillis()+"ms");
                // Reopen camera if necessary.
                if (!isHealthy() && !init())
                {
                    logger.Debug("FaceDetectionRunnable is not healthy: " + errorMessage);
                    continue;
                }

                prevFaceInfos.Clear();
                int frameCount = 0;
                int frameHasFaceCount = 0;
                for (int i = 0; i < Constants.FACE_DETECTION_FRAME_COUNT_FOR_ONE_RESULT; i++)
                {
                    try
                    {
                        Thread.Sleep((int)Constants.FACE_DETECTION_SLEEP_INTERVAL_BETWEEN_FRAME);
                        string start1 = DateTime.Now.ToString();
                        int start = int.Parse(start1);
                        List<FaceInfo> faceInfos = detectOneFrame();
                        string end1 = DateTime.Now.ToString();
                        int end = int.Parse(end1) - start;
                        //logger.debug("--one frame delay time is :"+end+"ms");
                        if (faceInfos.Count > 0)
                        {
                            frameHasFaceCount++;
                            // Keep a copy of face results in case subsequent frames
                            // fail to detect faces.
                            prevFaceInfos = faceInfos;
                        }
                        frameCount++;
                    }
                    catch (ThreadInterruptedException e)
                    {
                        Console.WriteLine(e.Message);
                    }
                }

                try
                {
                    semaphoreFaceInfos.WaitOne();
                    curFaceInfos.Clear();
                    double frameHasFaceRatio = frameHasFaceCount / (double)frameCount;
                    if (frameHasFaceRatio > Constants.FACE_DETECTION_POSITIVE_RATIO)
                    {
                        curFaceInfos = prevFaceInfos;
                    }
                    semaphoreFaceInfos.Release();
                }
                catch (ThreadInterruptedException e)
                {
                    Console.WriteLine(e.Message);
                }
                reportSuccess();
            }
        }

        public Boolean init()
        {
            close();
            isRunning = true;
            Boolean openCamera = false;//--------------------------为什么要写这个？
            try
            {               
                semaphoreCap.WaitOne();
                cap = new VideoCapture(Constants.DEFAULT_CAMERA_INDEX);
                cap.Start();
                openCamera = true;
                semaphoreCap.Release();
            }
            catch (ThreadInterruptedException e1)
            {
                Console.WriteLine(e1.Message);
            }
            if (!openCamera)
            {
                 errorMessage = "Failed to open camera id " + Constants.DEFAULT_CAMERA_INDEX;
                return false;
            }
            return true;
        }

        public void close()
        {
            try
            {
                semaphoreCap.WaitOne();
                if (cap != null)
                {
                    cap.Dispose();
                }
                semaphoreCap.Release();
            }
            catch (ThreadInterruptedException e1)
            {
                Console.WriteLine(e1.Message);
            }
            isRunning = false;
        }

        public Boolean isHealthy()
        {
            Boolean isOpened = false;
            try
            {
                semaphoreCap.WaitOne();
                isOpened = cap.IsOpened;
                semaphoreCap.Release();
            }
            catch (ThreadInterruptedException e1)
            {
                Console.WriteLine(e1.Message);
            }

            if (!isOpened)
            {
                errorMessage = "Video camera is not opened and seems unhealthy.";
            }
            return isOpened;
        }

        public String ErrorMessage()
        {
            return errorMessage;
        }

        // Returns current face detection result.
        public List<FaceInfo> getFaceInfos()
        {
            List<FaceInfo> result = null;
            try
            {
                semaphoreFaceInfos.WaitOne();
                result = curFaceInfos;
                semaphoreFaceInfos.Release();
            }
            catch (ThreadInterruptedException e)
            {
                Console.WriteLine(e.Message);
            }
            return result;
        }

        public Image getCurFrame()//-----------------------------------------?
        {
            Bitmap bm;
            Image im;
            if (curMat.IsEmpty==true)
            {
                //TODO : 我不知道这里怎么转，待解决
                //var iamge = Image.FromHbitmap(IntPtr.Zero);
                //return iamge;
                bm = new Bitmap(1,1);
                im = bm;
                return im;
            }
            return mat2Img.getImage(curMat);
        }

        // Detects faces on one frame.
        private List<FaceInfo> detectOneFrame()
        {
            try
            {
                semaphoreCap.WaitOne();
                cap.Read(curMat);
                semaphoreCap.Release();
            }
            catch (ThreadInterruptedException e1)
            {
                Console.WriteLine(e1.Message);
            }
            CvInvoke.CvtColor(curMat, curMat, Emgu.CV.CvEnum.ColorConversion.Rgb2Bgr);
            List<Rectangle> faceRects = faceDetector.detectFace(curMat);
            if (Constants.FACE_DETECTION_ENABLE_DISPLAY)
            {
                curMat = faceDetector.addFaceRectToMat(curMat, faceRects);
            }
            return faceDetector.convertToFaceInfo(curMat, faceRects);
        }

        private void reportSuccess()
        {
            throw new NotImplementedException();
        }
    }
}
