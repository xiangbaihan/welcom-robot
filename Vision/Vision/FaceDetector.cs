using Emgu.CV;
using System.Collections.Generic;
using System.Linq;
using System.Drawing;
using Emgu.CV.CvEnum;
using Emgu.CV.Util;

namespace Vision
{
    class FaceDetector
    {
        private static log4net.ILog logger = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        static CascadeClassifier frontalFaceDetector;
        static CascadeClassifier profileFaceDetector;
        static CascadeClassifier eyeDetector;

        public FaceDetector()
        {
            frontalFaceDetector = new CascadeClassifier("E:/BankRobot/haarcascade_frontalface_alt.xml");
            profileFaceDetector = new CascadeClassifier("E:/BankRobot/haarcascade_profileface.xml");
            eyeDetector = new CascadeClassifier("E:/BankRobot/haarcascade_eye_tree_eyeglasses.xml");
        }

        // Detects faces in the given Mat.
        public List<Rectangle> detectFace(Mat mat)
        {
            // Convert the current frame to grayscale.
            Mat gray_mat = new Mat();
            CvInvoke.CvtColor(mat, gray_mat, Emgu.CV.CvEnum.ColorConversion.Rgb2Gray);

            List<Rectangle> faces = new List<Rectangle>();
            double w = (double)mat.Width;
            double h = (double)mat.Height;

            // Detect frontal faces.
            Rectangle[] faceDetections = frontalFaceDetector.DetectMultiScale(gray_mat, 1.1, 3,
                    new Size((int)(0.05 * w), (int)(0.05 * h)), new Size((int)(0.8 * w), (int)(0.8 * h)));
            faces.AddRange(faceDetections);
            // Detect right profile faces.
            faceDetections = profileFaceDetector.DetectMultiScale(gray_mat, 1.1, 3,
                    new Size((int)(0.05 * w), (int)(0.05 * h)), new Size((int)(0.8 * w), (int)(0.8 * h)));
            faces.AddRange(faceDetections);

            // Detect left profile faces.反转左脸
            // flipCode = 1 for horizontal flip, along y-axis in mat.
            Mat flipped_mat = new Mat();
            CvInvoke.Flip(gray_mat, flipped_mat, FlipType.Horizontal);
            faceDetections = profileFaceDetector.DetectMultiScale(flipped_mat, 1.1, 3,
                     new Size((int)(0.05 * w), (int)(0.05 * h)), new Size((int)(0.8 * w), (int)(0.8 * h)));
            //左脸回正
            foreach (Rectangle leftProfileInFlippedMat in faceDetections)
            {
                Rectangle leftProfileInOriginalMat = leftProfileInFlippedMat;
                leftProfileInOriginalMat.X = mat.Width - (leftProfileInFlippedMat.X + leftProfileInFlippedMat.Width);
                faces.Add(leftProfileInOriginalMat);
            }
            logger.Debug("CascadeClassifier detected face number: " + faces.Count());

            // Merge overlapped faces.
            //List<Rectangle> allMatOfRect = new List<Rectangle>();//----------------------------------------------
            VectorOfRect allMatOfRect = new VectorOfRect();
            faces.AddRange(faces);
            Rectangle[] tmpfaces = faces.ToArray();
            allMatOfRect.Push(tmpfaces);
            CvInvoke.GroupRectangles(allMatOfRect, 1, 0.2);
            logger.Debug("Grouped face number: " + allMatOfRect.Size);

            return allMatOfRect.ToArray().ToList();
        }

        // Converts a list of Rect into FaceInfo.
        public List<FaceInfo> convertToFaceInfo(Mat mat, List<Rectangle> faceRects)
        {
            List<FaceInfo> faceInfos = new List<FaceInfo>();
            foreach (Rectangle faceRect in faceRects)
            {
                faceInfos.Add(new FaceInfo(faceRect.Width, faceRect.Height,
                        ComputeFacePositionDegree(mat.Width, faceRect.X + faceRect.Width / 2)));
            }
            return faceInfos;
        }

        // Adds a list of Rect into Mat for display purpose.
        public Mat addFaceRectToMat(Mat mat, List<Rectangle> faceRects)//---------------这里会重复画框
        {
            {
                using (Graphics g=Graphics.FromImage(mat.Bitmap))
                    foreach (Rectangle faceRect in faceRects)
                    {
                        g.DrawRectangle(new Pen(Color.Red), faceRect);
                    }
            }
            return mat;
        }

        private int ComputeFacePositionDegree(int imageWidth, int position)
        {
            return (int)((position - imageWidth / 2) / (double)imageWidth * Constants.CAMERA_FIELD_OF_VIEW);
        }

        // Detects eyes based on faces and draws them on Mat.
        // NOTE: this is not used for now.
        /*private Mat detectEyes(Mat mat, Rectangle[] faceRects)
        {
            // Convert the current frame to grayscale.
            Mat gray_mat = new Mat();
            CvInvoke.CvtColor(mat, gray_mat, Emgu.CV.CvEnum.ColorConversion.Bgr2Gray);
            foreach (Rectangle faceRect in faceRects)
            {
                // -- In each face, detect eyes
                Mat faceROI = gray_mat.submat(faceRect);
                List<Rectangle> eyes = new List<Rectangle>();//----------------------------------------------
                eyeDetector.DetectMultiScale(faceROI, eyes);
                foreach (Rectangle rectEye in eyes.toArray())
                {
                    Point center1 = new Point(faceRect.x + rectEye.x + rectEye.Width * 0.5,
                            faceRect.y + rectEye.y + rectEye.Height * 0.5);
                    int radius = (int)Math.Round((rectEye.Width + rectEye.Height) * 0.25);
                    CvInvoke.Circle(mat, center1, radius, new MCvScalar(255, 0, 0), 4, 8, 0);
                }
            }
            return mat;
        }*/

        //static{
        //   System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
    }
}
