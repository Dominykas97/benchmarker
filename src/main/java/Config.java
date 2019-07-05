import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;

public class Config {
    public String controlHostname;
    public int controlPort;
    public int numMessages;
    public int interMessageTime;
    public String prometheusHostname;

    static Config getInstance() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File("config/global.yaml"), Config.class);
    }
}
