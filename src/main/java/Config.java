import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;

public class Config {
    public String hostname;
    public int portNumber;
    public int numMessages;
    public int interMessageTime;

    static Config getInstance() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File("./config.yaml"), Config.class);
    }
}
