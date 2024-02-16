package com.lubiekakao1212.kboom.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lubiekakao1212.kboom.explosions.ExplosionReference;
import com.lubiekakao1212.kboom.explosions.IExplosion;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;

public class ExplosionReferenceDeserializer implements JsonDeserializer<ExplosionReference> {

    @Override
    public ExplosionReference deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(jsonElement.isJsonObject()) {
            return new ExplosionReference((IExplosion) jsonDeserializationContext.deserialize(jsonElement, IExplosion.class));
        }
        return new ExplosionReference((Identifier) jsonDeserializationContext.deserialize(jsonElement, Identifier.class));
    }

}
