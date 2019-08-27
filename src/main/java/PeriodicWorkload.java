import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/* This workload class sends requestsPerMessage messages/requests every n seconds, where n is calculated from
   messagesPerSecond */
public class PeriodicWorkload extends Workload {
    public int experimentDuration;
    public float batchesPerSecond;
    public int messagesPerBatch;

    public void execute(PrintWriter out) throws Exception {
        int numBatches = (int) Math.ceil(batchesPerSecond * experimentDuration);

        for (int i = 0; i < numBatches; i++) {
            System.out.println("Sending " + (i + 1) + "/" + numBatches + " batch of messages");
            for (int j = 0; j < messagesPerBatch; j++)
                out.println(".");
            if (i < numBatches - 1)
                TimeUnit.NANOSECONDS.sleep((long) (1e+9 / batchesPerSecond));
        }
    }
}
