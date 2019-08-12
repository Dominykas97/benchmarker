import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.List;

/* Jackson instantiates this class after reading the global.yaml config file */
public class Config {
    public String controlHostname;
    public int controlPort;
    public String prometheusHostname;
    public String prometheusPort;
    public int numExperiments;
    public List<Metric> metrics;
    public Workload workload;

    static Config getInstance() throws Exception {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(Workload.class, new WorkloadDeserializer());
        mapper.registerModule(module);

        return mapper.readValue(new File("config/global.yaml"), Config.class);
    }
}
