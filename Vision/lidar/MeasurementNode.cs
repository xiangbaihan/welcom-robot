using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Runtime.InteropServices;

namespace lidar
{
    public class MeasurementNode
    {
        public int sync_quality;
        // Unit: degree (360)
        public float angle;
        // Unit: millimeter
        public float distance;


        public MeasurementNode(int sync_quality, float angle, float distance)
        {
            this.sync_quality = RplidarNative.CopySync_quality();
            this.angle = RplidarNative.CopyAngle();
            this.distance = RplidarNative.CopyDistance();
        }

        public int getSyncQuality()
        {
            return this.sync_quality;
        }

        public float getAngle()
        {
            return this.angle;
        }

        public float getDistance()
        {
            return this.distance;
        }

        public String toString()
        {
            return "angle: " + sync_quality + "; distance: " + angle + "; sync_quality: " + distance;
        }
    }
}
