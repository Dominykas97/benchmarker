import java.util.LinkedList;
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

    // General performance parameters (from components.yaml)
    public double cpuTime; // how much time should be spent on a single message while using CPU resources (in ms)
    public double memoryUsage; // how much memory to fill in total (in MB)
    public double outputSize; // the size of the output data passed to the next component (in KB)

    // I/O-specific performance parameters (also from components.yaml)
    public IOMode ioMode; // one of: off, startup, regular; determines when to simulate I/O
    public int numRequests; // how many I/O requests to make
    public double responseSize; // size of a single I/O response (in KB)
    public double ioLatency; // the amount of time between a request and the first byte of the response (in ms)
    public double bandwidth; // I/O response bandwidth (in Mb/s)
    public double intervalBetweenRequests; // how long to wait between I/O requests (in ms)

    // A Flink-specific variable used to collect throughput data
    private transient Meter meter;

    // These constants are measure experimentally (except the last three)
    private static final double BASE_MEMORY_CONSUMPTION = 2.409e7; // 23 MB
    private static final double BYTES_PER_BYTE = 1.016; // yes, this sounds super weird
    private static final double BYTES_PER_CHAR = 5.79;
    private static final double NODE_SIZE = 61.94;
    private static final int BYTES_IN_MB = 1 << 20;
    private static final int BYTES_IN_KB = 1 << 10;
    private static final int MIN_SLEEP_TIME = 15; // (in ms)

    // Avoid calling sleep() with values that are too small to be accurate
    private static void mySleep(long ns) throws Exception {
        if (ns >= TimeUnit.NANOSECONDS.convert(MIN_SLEEP_TIME, TimeUnit.MILLISECONDS))
            TimeUnit.NANOSECONDS.sleep(ns);
    }

    // See the pseudo code in the report: it should be easier to follow
    private void simulateIO() throws Exception {
        long latency = (long) (ioLatency * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        long sleepTime = (long) (intervalBetweenRequests * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        int numNodes = (int) (responseSize * BYTES_IN_KB / NODE_SIZE);
        long bandwidthLatency = (long) (NODE_SIZE / (bandwidth / 8 * BYTES_IN_MB) *
                TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
        long expectedRuntime = (long) (8e9 * responseSize / 1024 / bandwidth);
        Random rng = new Random();

        long sleepDebt = 0;
        mySleep(latency);
        for (int i = 0; i < numRequests; i++) {
            // Gradually build up a linked list of random data, simulating a slow data transfer
            long innerStartTime = System.nanoTime();
            List<Long> data = new LinkedList<>();
            for (int j = 0; j < numNodes; j++) {
                data.add(rng.nextLong());
                mySleep(bandwidthLatency);
            }
            long innerEndTime = System.nanoTime();
            sleepDebt += expectedRuntime - innerEndTime + innerStartTime;
            if (i < numRequests - 1)
                sleepDebt += sleepTime + latency;
            mySleep(sleepDebt);
            if (sleepDebt >= 15e6)
                sleepDebt = 0;
        }
    }

    // Sets up a throughput meter and possibly runs an I/O simulation at startup
    @Override
    public void open(Configuration config) throws Exception {
        meter = getRuntimeContext()
                .getMetricGroup()
                .meter("componentThroughput", new MeterView(1));
        if (ioMode == IOMode.STARTUP)
            simulateIO();
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
        if (ioMode != IOMode.OFF)
            tempArraySize -= responseSize * BYTES_IN_KB;
        tempArraySize /= BYTES_PER_BYTE;
        int arraySize = Math.max((int) tempArraySize, stringLength); // arraySize >= stringLength

        // Fill the required amount of memory with random data
        byte[] memory = new byte[arraySize];
        new Random().nextBytes(memory);

        // Construct the output string
        String out = new String(memory, 0, stringLength);

        // Perform I/O if needed
        if (ioMode == IOMode.REGULAR)
            simulateIO();

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
