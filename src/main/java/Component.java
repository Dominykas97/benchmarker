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
    // TODO: actual computation instead of waiting
    // TODO: support more than ~2 GB memory usage using multiple arrays
    // TODO: do I need to subtract the string size from total memory?

    @Override
    public void open(Configuration config) {
        meter = getRuntimeContext()
                .getMetricGroup()
                .meter("componentThroughput", new MeterView(1));
    }

    @Override
    public String map(String in) throws Exception {
        long startTime = System.nanoTime();

        // Fill the required amount of memory with random data
        byte[] memory = new byte[memoryUsage << 20];
        new Random().nextBytes(memory);

        // Construct the output
        String out = new String(memory, 0, outputSize << 10);

        long endTime = System.nanoTime();

        // Sleep for the remaining time
        long initialTimeToSleep = TimeUnit.MILLISECONDS.convert(cpuTime, TimeUnit.NANOSECONDS);
        long timeSpent = endTime - startTime;
        long remainingTime = initialTimeToSleep - timeSpent;
        if (remainingTime > 0)
            TimeUnit.NANOSECONDS.sleep(remainingTime);

        meter.markEvent();
        return out;
    }
}
