import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Component {
    public static double memoryUsage; // how much memory to fill (in MB)
    public static double outputSize; // the size of the output data (in KB)

    public static boolean databaseOnStartup = false; // true: database access is simulated at the start, false: with each map
    public static int numRequests = 1; // how many requests to make to the database
    //public static double responseSize; // size of a single database response (in MB)
    public static long numNodesL;
    public static double databaseLatency = 0; // the amount of time between a request and the first byte of the response (in ms)
    public static double bandwidth = 0; // database response bandwidth (in bytes/s)
    public static double intervalBetweenRequests = 0; // how long to wait between database requests (in ms)

    // These constants are measure experimentally. The error seems to be within 0.5 MB.
    private static final int BASE_MEMORY_CONSUMPTION = 40 << 20; // 40 MB
    private static final double BYTES_PER_CHAR = 3.26845703125; // measured experimentally
    private static final int NODE_SIZE = 37; // in bytes
    private static final int BYTES_IN_MB = 1 << 20;
    private static final int BYTES_IN_KB = 1 << 10;

    private static void simulateDatabaseAccess() throws Exception {
        int latency = 0;
        int sleepTime = 0;
        int numNodes = (int) numNodesL;
        int bandwidthLatency = 0;
        Random rng = new Random();

        for (int i = 0; i < numRequests; i++) {
            // Gradually build up a linked list of random data, simulating a slow database response transfer
            List<Long> data = new LinkedList<>();
            for (int j = 0; j < numNodes; j++) {
                data.add(rng.nextLong());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        numNodesL = Long.parseLong(args[0]);

        // Memory calculations
        int stringLength = Integer.parseInt(args[2]);
        int arraySize = Integer.parseInt(args[1]);
        assert(arraySize >= stringLength);

        // Fill the required amount of memory with random data
        byte[] memory = new byte[arraySize];
        new Random().nextBytes(memory);

        // Construct the output string
        String out = new String(memory, 0, stringLength);

        simulateDatabaseAccess();
    }
}
