import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class PeriodicWorkload extends Workload {
    public float messagesPerSecond;
    public int experimentDuration;
    public int requestsPerMessage;

    public void execute(PrintWriter out) throws Exception {
        int numMessages = (int) Math.ceil(messagesPerSecond * experimentDuration);

        for (int i = 0; i < numMessages; i++) {
            System.out.println("Sending " + (i + 1) + "/" + numMessages + " message");
            for (int j = 0; j < requestsPerMessage; j++)
                out.println(".");
            if (i < numMessages - 1)
                TimeUnit.NANOSECONDS.sleep((long) (1e+9 / messagesPerSecond));
        }    }
}
