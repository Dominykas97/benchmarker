import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/* A Jackson extension teaching Jackson how to recognise which Workload class to initialise */
public class WorkloadDeserializer extends StdDeserializer<Workload> {

    WorkloadDeserializer() {
        this(null);
    }

    WorkloadDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public Workload deserialize(final JsonParser parser, final DeserializationContext context)
            throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        if (node.has("function"))
            return mapper.treeToValue(node, FunctionalWorkload.class);
        return mapper.treeToValue(node, PeriodicWorkload.class);
    }
}
