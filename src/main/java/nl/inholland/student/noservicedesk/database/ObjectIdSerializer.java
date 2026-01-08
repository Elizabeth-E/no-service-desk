package nl.inholland.student.noservicedesk.database;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

    @Override
    public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        if (value == null) {
            gen.writeNull();
            return;
        }

        // Mongo Extended JSON format
        gen.writeStartObject();
        gen.writeStringField("$oid", value.toHexString());
        gen.writeEndObject();
    }
}

