import java.util.concurrent.TimeUnit;
import java.util.Random;

public class FullComponent {
    public static int cpuTime = 0; // time to sleep (in ms)
    //public static int memoryUsage = 512; // how much memory to fill (in MB)
    //public static int outputSize = 100 * 1024; // the size of the output data (in KB)
    private static final int BASE_MEMORY_CONSUMPTION = 40 << 20; // 40 MB
    private static final double BYTES_PER_CHAR = 3.26845703125; // measured experimentally

    public static void main(String[] args) {
        // Time calculations
        long startTime = System.nanoTime();
        long timeDifference = TimeUnit.NANOSECONDS.convert(cpuTime, TimeUnit.MILLISECONDS);
        long endTime = startTime + timeDifference;

        int memoryUsage = Integer.parseInt(args[0]);
        int outputSize = Integer.parseInt(args[1]) << 10;

        // Memory calculations
        int stringLength = (int) ((outputSize << 10) / BYTES_PER_CHAR);
        int arraySize = (memoryUsage << 20) - BASE_MEMORY_CONSUMPTION - outputSize;
        assert stringLength <= arraySize;

        // Fill the required amount of memory with random data
        byte[] memory = new byte[arraySize];
        new Random().nextBytes(memory);

        // Construct the output string
        String out = new String(memory, 0, stringLength);

        // Let's waste some CPU power checking the Collatz conjecture
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
    }
}