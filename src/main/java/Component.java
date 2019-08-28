import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.MeterView;

/* A Flink map responsible for simulating the desired performance characteristics */
public class Component extends RichMapFunction<String, String> {
    // which component to use as input (0 denotes the control server, higher numbers enumerate the
    // components in the order defined in components.yaml)
    public List<Integer> parents;
    public IOSimulator io;
    // A Flink-specific variable used to collect throughput data
    private transient Meter meter;

    // General performance parameters (from components.yaml)
    public double cpuTime; // how much time should be spent on a single message while using CPU resources (in ms)
    public double memoryUsage; // how much memory to fill in total (in MB)
    public double outputSize; // the size of the output data passed to the next component (in KB)

    // These constants are measured experimentally (except the last two)
    private static final double BASE_MEMORY_CONSUMPTION = 2.409e7; // 23 MB
    private static final double BYTES_PER_BYTE = 1.016; // yes, this sounds super weird
    private static final double BYTES_PER_CHAR = 5.79;
    private static final int BYTES_IN_MB = 1 << 20;
    private static final int BYTES_IN_KB = 1 << 10;

    // Sets up a throughput meter and possibly runs an I/O simulation at startup
    @Override
    public void open(Configuration config) throws Exception {
        meter = getRuntimeContext()
                .getMetricGroup()
                .meter("componentThroughput", new MeterView(1));
        if (io != null && io.mode == IOMode.STARTUP)
            io.simulate();
    }

    // Called with every message
    @Override
    public String map(String in) throws Exception {
        // Calculate when our time's up
        long startTime = System.nanoTime();
        long timeDifference = (long) (cpuTime * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        long endTime = startTime + timeDifference;

        // Memory calculations
        int stringLength = (int) (outputSize * BYTES_IN_KB / BYTES_PER_CHAR);
        double tempArraySize = memoryUsage * BYTES_IN_MB - BASE_MEMORY_CONSUMPTION - outputSize * BYTES_IN_KB;
        if (io != null)
            tempArraySize -= io.responseSize * BYTES_IN_KB;
        tempArraySize /= BYTES_PER_BYTE;
        int arraySize = Math.max((int) tempArraySize, stringLength); // arraySize >= stringLength

        // Fill the required amount of memory with random data
        byte[] memory = new byte[arraySize];
        new Random().nextBytes(memory);

        // Construct the output string
        String out = new String(memory, 0, stringLength);

        // Perform I/O if needed
        if (io != null && io.mode == IOMode.REGULAR)
            io.simulate();

        // Let's waste some CPU power testing the Collatz conjecture
        long starting = 1;
        long current = 1;
        while (System.nanoTime() < endTime) {
            if (current == 1) {
                current = ++starting;
            } else if (current % 2 == 0) {
                current /= 2;
            } else {
                current = 3 * current + 1;
            }
        }

        meter.markEvent(); // We have processed one message!
        return out;
    }
}
