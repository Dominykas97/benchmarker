import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/* The main class of the Flink application, responsible for setting up a chain of components with the control server
   as the source of input */
public class Benchmarker {

    private static Config config;
    private static List<Component> components;
    private static StreamExecutionEnvironment env;

    /* Construct a chain of components, set the control server as the source of input, and perform an experiment */
    private static JobExecutionResult runExperiment() throws Exception {
        System.out.println("Starting a new experiment and connecting to the control server " +
                config.controlHostname + ":" + config.controlPort);
        DataStream<String> initial = env.socketTextStream(config.controlHostname, config.controlPort);

        // A list of DataStream nodes that could be used as inputs to new components
        List<DataStream<String>> nodes = new ArrayList<>();
        nodes.add(initial);
        for (Component component : components) {
            try {
                // Take the DataStream of the first parent and union it with all the others
                DataStream<String> unifiedInput = nodes.get(component.parents.get(0));
                List<Integer> otherParentsIndices = component.parents.subList(1, component.parents.size());
                for (int i : otherParentsIndices)
                    unifiedInput = unifiedInput.union(nodes.get(i));
                nodes.add(unifiedInput.map(component));
            } catch (IndexOutOfBoundsException e) {
                System.err.println("The list of parents is incorrect");
                e.printStackTrace();
                System.exit(1);
            }
        }
        return env.execute();
    }

    /* Send the job's running time to the control server (looping repeatedly in case the control server is delayed in
       setting up a new server socket) */
    private static void sendRuntime(long runtime) throws Exception {
        boolean tryAgain = true;
        while (tryAgain) {
            try (
                    Socket socket = new Socket(config.controlHostname, config.controlPort);
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            ) {
                pw.write(runtime + "\n");
                tryAgain = false;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Construct a list of components from the config file
        String componentsText = new String(Files.readAllBytes(Paths.get("config/components.yaml")));
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Component.class);
        components = mapper.readValue(componentsText, listType);

        // Set up a few other important variables
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        config = Config.getInstance();
        long delay = (long) (config.delayBetweenExperiments * TimeUnit.NANOSECONDS.convert(1, TimeUnit.MINUTES));

        // Run a number of (identical) experiments
        for (int i = 0; i < config.numExperiments; i++) {
            if (i > 0)
                TimeUnit.NANOSECONDS.sleep(delay);
            JobExecutionResult result = runExperiment();
            sendRuntime(result.getNetRuntime());
        }
    }
}
