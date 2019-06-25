import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Benchmarker {

    public static void main(String[] args) throws Exception {


        // Construct a list of components from the config file
        String componentsText = new String(Files.readAllBytes(Paths.get("/home/paulius/benchmarker/components.yaml")));
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Component.class);
        List<Component> components = mapper.readValue(componentsText, listType);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration config = Configuration.getInstance();

        DataStream<String> dataStream = env.socketTextStream(config.hostname, config.portNumber);
        for (Component component : components)
            dataStream = dataStream.flatMap(component);
        dataStream.print();

        env.execute("Benchmarker");
    }
}