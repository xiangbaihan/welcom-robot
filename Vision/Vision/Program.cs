using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Collections;
using System.Threading;
using Vision;

namespace Vision
{
    static class Program
    {
        //static VideoFrame vf = new VideoFrame(videoCap);
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [MTAThread]
        static void Main()
        {

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new VideoFrame());

            //Thread thread1 = new Thread(new ParameterizedThreadStart(delegate
            //{
            //    vf.videoFrame(videoCap);
            //}));
            //thread1.Start();

            //Thread thread = new Thread(ThreadStartC);
            //thread.Start();
        }

        //private static void ThreadStartC()
        //{
        //    vf.Visible = true;
        //}
    }
}
