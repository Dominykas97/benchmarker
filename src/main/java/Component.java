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

    private transient Meter meter;

    // These constants are measure experimentally. The error seems to be within 0.5 MB.
    private static final int BASE_MEMORY_CONSUMPTION = 40 << 20; // 40 MB
    private static final double BYTES_PER_CHAR = 3.26845703125; // measured experimentally
    private static final int BYTES_IN_MB = 1 << 20;
    private static final int BYTES_IN_KB = 1 << 10;

    @Override
    public void open(Configuration config) {
        meter = getRuntimeContext()
                .getMetricGroup()
                .meter("componentThroughput", new MeterView(1));
    }

    @Override
    public String map(String in) {
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
