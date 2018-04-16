using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Emgu.CV;
using Emgu.CV.Util;
using Emgu.Util;
using Emgu.CV.CvEnum;
using Emgu.CV.Structure;
using Emgu.CV.Cuda;
using System.Threading;
using System.Drawing.Printing;

namespace Vision
{
    public partial class VideoFrame : Form
    {
        public VideoCap videoCap = null;
        public FaceDetectionRunnable faceDetectionRunnable = null;

        public VideoFrame()
        {
            InitializeComponent();
            CvInvoke.UseOpenCL = false;
            CheckForIllegalCrossThreadCalls = false;
            try
            {
                videoCap = new VideoCap();
                Thread thread1 = new Thread(paint);
                thread1.IsBackground = true;
                thread1.Start();
            }
            catch (NullReferenceException excpt)
            {
                MessageBox.Show(excpt.Message);
            }
        }

        //public VideoFrame(VideoCap videoCap)
        //{
        //    InitializeComponent();
        //    this.videoCap = videoCap;

        //    //if (videoCap != null)
        //    //{
        //    //    pictureBox1.Image = videoCap.getOneFrame();
        //    //    pictureBox1.Refresh();
        //    //}
        //    //else
        //    //{
        //    //    pictureBox1.Image = faceDetectionRunnable.getCurFrame();
        //    //    pictureBox1.Refresh();
        //    //}

        //    //try
        //    //{
        //    //    Thread.Sleep(Constants.FACE_DETECTION_DISPLAY_REFRESH_INTERVAL);
        //    //}
        //    //catch (ThreadInterruptedException ex)
        //    //{
        //    //    Console.WriteLine(ex.Message);
        //    //}
        //}

        //public VideoFrame(FaceDetectionRunnable faceDetectionRunnable)
        //{
        //    this.faceDetectionRunnable = faceDetectionRunnable;
        //    InitializeComponent();
        //    CvInvoke.UseOpenCL = false;
        //}

        private void paint()
        {
            while (true)
            {
                pictureBox1.Image = videoCap.getOneFrame();
                pictureBox1.Refresh();
            }
        }

        private void VideoFrame_FormClosed(object sender, FormClosedEventArgs e)
        {
            this.Dispose();
        }
    }
}

