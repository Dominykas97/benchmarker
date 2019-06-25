import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class Component implements FlatMapFunction<String, String> {
    public int cpuTime; // time to sleep (in ms)
    public int memoryUsage; // how much memory to fill (in MB)
    public int outputSize; // the size of the output data (in KB)

    // TODO: support more than ~2 GB memory usage using multiple arrays
    // TODO: do I need to subtract the string size from total memory?

    @Override
    public void flatMap(String in, Collector<String> out) throws Exception {

        long startTime = System.nanoTime();

        // Fill the required amount of memory with random data
        byte[] memory = new byte[memoryUsage << 20];
        new Random().nextBytes(memory);

        // Construct the output
        out.collect(new String(memory, 0, outputSize << 10));

        long endTime = System.nanoTime();

        // Sleep for the remaining time
        long initialTimeToSleep = TimeUnit.MILLISECONDS.convert(cpuTime, TimeUnit.NANOSECONDS);
        long timeSpent = endTime - startTime;
        long remainingTime = initialTimeToSleep - timeSpent;
        if (remainingTime > 0)
            TimeUnit.NANOSECONDS.sleep(remainingTime);
    }
}
