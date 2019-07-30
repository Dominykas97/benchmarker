import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class UserDeserializer extends StdDeserializer<User> {
    UserDeserializer() {
        this(null);
    }

    UserDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public User deserialize(final JsonParser parser, final DeserializationContext context)
            throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        return mapper.treeToValue(node, PeriodicUser.class);
    }
}
