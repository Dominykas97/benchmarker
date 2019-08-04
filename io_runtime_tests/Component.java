import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Component {
    public static int numRequests; // how many requests to make to the database
    public static double responseSize; // size of a single database response (in KB)
    public static double databaseLatency; // the amount of time between a request and the first byte of the response (in ms)
    public static double bandwidth; // database response bandwidth (in Mb/s)
    public static double intervalBetweenRequests; // how long to wait between database requests (in ms)

    // These constants are measure experimentally
    private static final double NODE_SIZE = 61.94;
    private static final int BYTES_IN_KB = 1 << 10;
    private static final int BYTES_IN_MB = 1 << 20;

    private static void mySleep(long ns) throws Exception {
        if (ns >= 15e6) { // 15 ms
            //System.out.println("Sleeping for " + ns);
            TimeUnit.NANOSECONDS.sleep(ns);
        }
    }

    public static void main(String[] args) throws Exception {
        numRequests = Integer.parseInt(args[0]);
        responseSize = BYTES_IN_KB * Double.parseDouble(args[1]); // MB to KB
        databaseLatency = Double.parseDouble(args[2]);
        bandwidth = Double.parseDouble(args[3]);
        intervalBetweenRequests = Double.parseDouble(args[4]);

        long startTime = System.nanoTime();

        long latency = (long) (databaseLatency * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        long sleepTime = (long) (intervalBetweenRequests * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        double fullNumNodes = responseSize * BYTES_IN_KB / NODE_SIZE;
        int numNodes = (int) fullNumNodes;
        long bandwidthLatency = (long) (NODE_SIZE / (bandwidth / 8 * BYTES_IN_MB) *
                                      TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
        Random rng = new Random();

        for (int i = 0; i < numRequests; i++) {
            mySleep(latency);

            // Gradually build up a linked list of random data, simulating a slow database response transfer
            long innerStartTime = System.nanoTime();
            List<Long> data = new LinkedList<>();
            for (int j = 0; j < numNodes; j++) {
                data.add(rng.nextLong());
                mySleep(bandwidthLatency);
            }
            long innerEndTime = System.nanoTime();
            long expectedRuntime = (long) (8e9 * responseSize / 1024 / bandwidth);
            mySleep(expectedRuntime - innerEndTime + innerStartTime);

            // In case of a sleep debt...
            /*if (bandwidthLatency < 15e6) {
                mySleep((long) (8e9 * responseSize / 1024 / bandwidth));
            } else {
                mySleep((long) ((fullNumNodes - numNodes) * bandwidthLatency));
                }*/

            if (i < numRequests - 1)
                mySleep(sleepTime);
        }

        long endTime = System.nanoTime();
        System.out.println(numNodes + "," + bandwidthLatency + "," + (endTime - startTime));
    }
}
