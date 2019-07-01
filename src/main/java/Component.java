import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.MeterView;

public class Component extends RichMapFunction<String, String> {
    public int cpuTime; // time to sleep (in ms)
    public int memoryUsage; // how much memory to fill (in MB)
    public int outputSize; // the size of the output data (in KB)
    private transient Meter meter;
    // TODO: support more than ~2 GB memory usage using multiple arrays

    @Override
    public void open(Configuration config) {
        meter = getRuntimeContext()
                .getMetricGroup()
                .meter("componentThroughput", new MeterView(1));
    }

    @Override
    public String map(String in) {
        long startTime = System.nanoTime();
        long timeDifference = TimeUnit.NANOSECONDS.convert(cpuTime, TimeUnit.MILLISECONDS);
        long endTime = startTime + timeDifference;

        assert memoryUsage << 10 >= 2 * outputSize;

        // Fill the required amount of memory with random data
        byte[] memory = new byte[(memoryUsage << 20) - (outputSize << 10)];
        new Random().nextBytes(memory);

        // Construct the output (recall that each unicode character takes up 2 bytes)
        String out = new String(memory, 0, outputSize << 9);

        // Let's waste some CPU power testing Collatz conjecture
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
