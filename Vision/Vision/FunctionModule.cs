using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Vision
{
    public interface FunctionModule
    {
        // Starts running the module and returns whether the module launches successfully.
        Boolean start();
        // Stops running the module.
        void stop();
        Boolean isHealthy();
        // Returns latest error message of this module, if any.
        String errorMessage();
    }
}
