import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Component {
    public static int numRequests; // how many requests to make to the database
    public static double responseSize; // size of a single database response (in KB)
    public static double databaseLatency; // the amount of time between a request and the first byte of the response (in ms)
    public static double bandwidth; // database response bandwidth (in bytes/s)
    public static double intervalBetweenRequests; // how long to wait between database requests (in ms)

    // These constants are measure experimentally
    private static final double NODE_SIZE = 61.94;
    private static final int BYTES_IN_KB = 1 << 10;
    private static final int BYTES_IN_MB = 1 << 20;

    public static void main(String[] args) throws Exception {
        numRequests = Integer.parseInt(args[0]);
        responseSize = BYTES_IN_KB * Double.parseDouble(args[1]); // MB to KB
        databaseLatency = Double.parseDouble(args[2]);
        bandwidth = Double.parseDouble(args[2]);
        intervalBetweenRequests = Double.parseDouble(args[4]);

        long startTime = System.nanoTime();

        int latency = (int) (databaseLatency * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        int sleepTime = (int) (intervalBetweenRequests * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS));
        int numNodes = (int) (responseSize * BYTES_IN_KB / NODE_SIZE);
        int bandwidthLatency = (int) (NODE_SIZE / (bandwidth / 8 * BYTES_IN_MB) *
                                      TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
        Random rng = new Random();

        for (int i = 0; i < numRequests; i++) {
            TimeUnit.NANOSECONDS.sleep(latency);

            // Gradually build up a linked list of random data, simulating a slow database response transfer
            List<Long> data = new LinkedList<>();
            for (int j = 0; j < numNodes; j++) {
                data.add(rng.nextLong());
                TimeUnit.NANOSECONDS.sleep(bandwidthLatency);
            }

            if (i < numRequests - 1)
                TimeUnit.NANOSECONDS.sleep(sleepTime);
        }

        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
    }
}
