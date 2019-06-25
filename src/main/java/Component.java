import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class Component implements FlatMapFunction<String, String> {
    public int cpuTime; // time to sleep in seconds
    public int memoryUsage; // how much memory to fill (in MB)
    public int outputDataSize;

    @Override
    public void flatMap(String in, Collector<String> out) throws Exception {

        out.collect("");

        long startTime = System.nanoTime();
        byte[] memory = new byte[memoryUsage << 20];
        new Random().nextBytes(memory);
        long endTime = System.nanoTime();
        System.out.println("Data initialization took " + (endTime-startTime) + " s.");
        // TODO: maybe subtract this time from cpuTime (in the right units)?

        TimeUnit.SECONDS.sleep(cpuTime);
    }
}
