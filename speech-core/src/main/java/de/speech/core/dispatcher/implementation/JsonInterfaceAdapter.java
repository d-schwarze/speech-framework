package de.speech.core.dispatcher.implementation;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Used to serialize, deserialize interfaces with gson.
 *
 * @param <T> interface
 */
public class JsonInterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private static final String CLASSNAME = "CLASSNAME";
    private static final String DATA = "DATA";

    @Override
    public T deserialize(JsonElement jsonElement, Type type,
                         JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();
        Class klass;
        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage());
        }
        return jsonDeserializationContext.deserialize(jsonObject.get(DATA), klass);
    }

    @Override
    public JsonElement serialize(T jsonElement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CLASSNAME, jsonElement.getClass().getName());
        jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement));
        return jsonObject;
    }
}