using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HumanDetectionResponse
{
    class humanDetectionResponse
    {
        private int number;
        private int state;
        private long timestamp;

        public void HumanDetectionResponse(int state, int number, long timestamp)
        {
            this.state = state;
            this.number = number;
            this.timestamp = timestamp;
        }

    //@XmlElement(name = "state")
    public int getState()
        {
            return state;
        }

    //@XmlElement(name = "number")
    public int getNumber()
        {
            return number;
        }

    //@XmlElement(name = "timestamp")
    public long getTimeStamp()
        {
            return timestamp;
        }
    }
}
