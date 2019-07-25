import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.MeterView;

public class Component extends RichMapFunction<String, String> {
    public double cpuTime; // time to sleep (in ms)
    public double memoryUsage; // how much memory to fill (in MB)
    public double outputSize; // the size of the output data (in KB)

    public boolean databaseOnStartup; // true: database access is simulated at the start, false: with each map
    public int numRequests; // how many requests to make to the database
    public double responseSize; // size of a single database response (in KB)
    public double databaseLatency; // the amount of time between a request and the first byte of the response (in ms)
    public double bandwidth; // database response bandwidth (in bytes/s)
    public double intervalBetweenRequests; // how long to wait between database requests (in ms)

    private transient Meter meter;

    // These constants are measure experimentally. The error seems to be within 0.5 MB.
    private static final int BASE_MEMORY_CONSUMPTION = 40 << 20; // 40 MB
    private static final double BYTES_PER_CHAR = 3.26845703125; // measured experimentally
    private static final int NODE_SIZE = 38;
    private static final int BYTES_IN_MB = 1 << 20;
    private static final int BYTES_IN_KB = 1 << 10;

    private void simulateDatabaseAccess() throws Exception {
        int latency = (int) (databaseLatency * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        int sleepTime = (int) (intervalBetweenRequests * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        int numNodes = (int) (responseSize * BYTES_IN_KB / NODE_SIZE);
        int bandwidthLatency = (int) (NODE_SIZE / bandwidth * TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
        Random rng = new Random();

        System.out.println("Number of nodes: " + numNodes);
        System.out.println("Bandwidth latency: " + bandwidthLatency + " ms");

        for (int i = 0; i < numRequests; i++) {
            TimeUnit.NANOSECONDS.sleep(latency);

            // Gradually build up a linked list of random data, simulating a slow database response transfer
            List<Integer> data = new LinkedList<>();
            for (int j = 0; j < numNodes; j++) {
                data.add(rng.nextInt());
                TimeUnit.NANOSECONDS.sleep(bandwidthLatency);
            }

            if (i < numRequests - 1)
                TimeUnit.NANOSECONDS.sleep(sleepTime);
        }
    }

    @Override
    public void open(Configuration config) throws Exception {
        meter = getRuntimeContext()
                .getMetricGroup()
                .meter("componentThroughput", new MeterView(1));
        if (databaseOnStartup)
            simulateDatabaseAccess();
    }

    @Override
    public String map(String in) throws Exception {
        long startTime = System.nanoTime();
        long timeDifference = (long) (cpuTime * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        long endTime = startTime + timeDifference;

        // Memory calculations
        int stringLength = (int) (outputSize * BYTES_IN_KB / BYTES_PER_CHAR);
        int arraySize = (int) (memoryUsage * BYTES_IN_MB - BASE_MEMORY_CONSUMPTION - outputSize * BYTES_IN_KB);
        arraySize = Math.max(arraySize, stringLength); // arraySize >= stringLength

        // Fill the required amount of memory with random data
        byte[] memory = new byte[arraySize];
        new Random().nextBytes(memory);

        // Construct the output string
        String out = new String(memory, 0, stringLength);

        if (!databaseOnStartup)
            simulateDatabaseAccess();

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

        meter.markEvent();
        return out;
    }
}
