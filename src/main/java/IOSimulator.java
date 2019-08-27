import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/* A class responsible for I/O simulation */
public class IOSimulator implements Serializable {
    public IOMode mode; // one of: off, startup, regular; determines when to simulate I/O
    public int numRequests; // how many I/O requests to make
    public double responseSize; // size of a single I/O response (in KB)
    public double latency; // the amount of time between a request and the first byte of the response (in ms)
    public double bandwidth; // I/O response bandwidth (in Mb/s)
    public double intervalBetweenRequests; // how long to wait between I/O requests (in ms)

    private static final int BYTES_IN_MB = 1 << 20;
    private static final int BYTES_IN_KB = 1 << 10;
    private static final int MIN_SLEEP_TIME = 15; // (in ms)
    private static final double NODE_SIZE = 61.94;

    public IOSimulator() {
    }

    // Avoid calling sleep() with values that are too small to be accurate
    private void mySleep(long ns) throws Exception {
        if (ns >= TimeUnit.NANOSECONDS.convert(MIN_SLEEP_TIME, TimeUnit.MILLISECONDS))
            TimeUnit.NANOSECONDS.sleep(ns);
    }

    // See the pseudo code in the report: it should be easier to follow
    void simulate() throws Exception {
        long myLatency = (long) (latency * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        long sleepTime = (long) (intervalBetweenRequests * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        int numNodes = (int) (responseSize * BYTES_IN_KB / NODE_SIZE);
        long bandwidthLatency = (long) (NODE_SIZE / (bandwidth / 8 * BYTES_IN_MB) *
                TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
        long expectedRuntime = (long) (8e9 * responseSize / 1024 / bandwidth);
        Random rng = new Random();

        long sleepDebt = 0;
        mySleep(myLatency);
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
                sleepDebt += sleepTime + myLatency;
            mySleep(sleepDebt);
            if (sleepDebt >= 15e6)
                sleepDebt = 0;
        }
    }
}
