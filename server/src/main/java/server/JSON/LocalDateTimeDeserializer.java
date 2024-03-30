package server.JSON;

import com.google.gson.*;
import common.Constants;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Custom deserializer to parse LocalDateTime from JSON files
 */
public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDateTime.parse(jsonElement.getAsString(), Constants.formatter);
    }
}
