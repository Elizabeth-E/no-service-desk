package nl.inholland.student.noservicedesk.database;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    @Override
    public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // If it's already a string: "695e..."
        if (node.isTextual()) {
            return new ObjectId(node.asText());
        }

        // If it's Extended JSON: { "$oid": "695e..." }
        if (node.isObject() && node.has("$oid")) {
            return new ObjectId(node.get("$oid").asText());
        }

        return null;
    }
}
