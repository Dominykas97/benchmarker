import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/* This workload class sends requestsPerMessage messages/requests every n seconds, where n is calculated from
   messagesPerSecond */
public class PeriodicWorkload extends Workload {
    public int experimentDuration;
    public float messagesPerSecond;
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
