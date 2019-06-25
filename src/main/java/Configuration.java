import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;

public class Configuration {
    public String hostname;
    public int portNumber;
    public int numMessages;
    public int interMessageTime;

    static Configuration getInstance() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File("/home/paulius/benchmarker/config.yaml"), Configuration.class);
    }
}
