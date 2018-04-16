using Emgu.CV;
using Emgu.CV.Structure;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data;
using System.Windows.Forms;

namespace Vision
{
    class Mat2Image
    {
        Mat mat = new Mat();
        Image img;//--------------------------------------------?
        byte[] dat;
        Bitmap map;

        public Mat2Image()
        {
        }

        public Mat2Image(Mat mat)
        {
            getSpace(mat);
        }

        public void getSpace(Mat mat)
        {

            this.mat = mat;
            int w = mat.Cols, h = mat.Rows;
            if (dat == null || dat.Length != w * h * 3)
                dat = new byte[w * h * 3];
            //Graphics g = Graphics.FromImage(img);
            if (img == null || img.Width != w || img.Height != h || img.GetType() != typeof(Bgr))
                map = new Bitmap(mat.Bitmap, w, h);
            img = map;
                //g.DrawImage(img, 0, 0, w, h);//这只是在输出的时候改变了输出大小，而没有根本改变img的缓存数据
            //g.Dispose();
        }

        
        public Image getImage(Mat mat)
        {
            getSpace(mat);
            map = new Bitmap(mat.Bitmap, mat.Cols, mat.Rows);
            img = map;

            return img;
        }

        //static {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //}
    }
}
