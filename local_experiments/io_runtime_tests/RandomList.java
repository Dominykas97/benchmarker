import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class RandomList {
    public static void main(String[] args) throws Exception {
        Random rng = new Random();
        BufferedWriter writer = new BufferedWriter(new FileWriter("linkedlist.csv"));

        for (int numNodes = 1; numNodes <= 10000000; numNodes *= 2) {
            System.out.println(numNodes);
            for (int rep = 0; rep < 10; rep++) {
                List<Long> data = new LinkedList<>();
                long startTime = System.nanoTime();
                for (int j = 0; j < numNodes; j++)
                    data.add(rng.nextLong());
                long endTime = System.nanoTime();
                writer.write(numNodes + "," + (endTime - startTime) + "\n");
            }
        }
        writer.close();
    }
}
