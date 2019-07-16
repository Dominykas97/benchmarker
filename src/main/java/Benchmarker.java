import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Benchmarker {

    public static void main(String[] args) throws Exception {
        // Construct a list of components from the config file
        String componentsText = new String(Files.readAllBytes(Paths.get("config/components.yaml")));
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Component.class);
        List<Component> components = mapper.readValue(componentsText, listType);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Config config = Config.getInstance();

        System.out.println("Connecting to the control server " + config.controlHostname + ":" + config.controlPort);
        DataStream<String> dataStream = env.socketTextStream(config.controlHostname, config.controlPort);
        for (Component component : components)
            dataStream = dataStream.map(component);
        dataStream.print();
        JobExecutionResult result = env.execute();

        // Send the server the job's running time
        boolean tryAgain = true;
        while (tryAgain) {
            try (
                    Socket socket = new Socket(config.controlHostname, config.controlPort);
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            ) {
                pw.write(result.getNetRuntime() + "\n");
                tryAgain = false;
            }
        }
    }
}
