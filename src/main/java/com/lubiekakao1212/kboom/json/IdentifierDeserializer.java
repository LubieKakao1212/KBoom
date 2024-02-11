package com.lubiekakao1212.kboom.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.lang.reflect.Type;

public class IdentifierDeserializer implements JsonDeserializer<Identifier> {

    @Override
    public Identifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try
        {
            return new Identifier(jsonElement.getAsString());
        }
        catch (InvalidIdentifierException e)
        {
            throw new JsonParseException(e);
        }
    }


}
