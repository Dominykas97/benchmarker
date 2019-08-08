import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Component {
    // Results of the experiment:
    // memory = 40 MB + array_size + 3.268 * string_size
    public static void main(String[] args) {
        int memoryUsage = Integer.parseInt(args[0]);
        int outputSize = Integer.parseInt(args[1]);

        byte[] memory = new byte[(memoryUsage << 20)];
        new Random().nextBytes(memory);

        String out = new String(memory, 0, outputSize << 20);
    }
}
