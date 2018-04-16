using Emgu.CV;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;
using Emgu.CV.Structure;

namespace Vision
{
    public class VideoCap
    {
        VideoCapture capture;

        Mat2Image mat2Img = new Mat2Image();

        FaceDetector faceDetector = new FaceDetector();

        public VideoCap()
        {
            capture = new VideoCapture(0);
            capture.Start();  // Default camera.
                              //capture.open("rtp://230.0.0.1:5555");  // RTP  
        }

        public void close()
        {
            capture.Stop();
        }

        // Returns one image with detection results.
        public Image getOneFrame()
        {
            Mat mat = getOneMat();
            List<Rectangle> faceRects = faceDetector.detectFace(mat);
            mat = faceDetector.addFaceRectToMat(mat, faceRects);
            return mat2Img.getImage(mat);
        }

        // Returns one frame as Mat.
        private Mat getOneMat()
        {
            Mat mat = new Mat();
            capture.Read(mat);
            //CvInvoke.CvtColor(mat, mat, Emgu.CV.CvEnum.ColorConversion.Rgb2Bgr);
            return mat;
        }
    }

}
