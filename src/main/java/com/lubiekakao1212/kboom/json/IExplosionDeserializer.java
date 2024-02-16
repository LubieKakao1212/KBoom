package com.lubiekakao1212.kboom.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lubiekakao1212.kboom.KBoom;
import com.lubiekakao1212.kboom.explosions.ExplosionReference;
import com.lubiekakao1212.kboom.explosions.IExplosion;
import com.lubiekakao1212.kboom.registry.KBoomRegistries;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;

import static com.lubiekakao1212.kboom.resource.KBoomConstants.EXPLOSION_SOURCE_FIELD;

public class IExplosionDeserializer implements JsonDeserializer<IExplosion> {

    @Override
    public IExplosion deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            var jobj = jsonElement.getAsJsonObject();
            var sourceId = (Identifier) jsonDeserializationContext.deserialize(jobj.get(EXPLOSION_SOURCE_FIELD), Identifier.class);
            var source = KBoomRegistries.EXPLOSION_SOURCES.get(sourceId);
            if(source == null) {
                KBoom.LOGGER.warn("Invalid source id: " + sourceId);
                throw new JsonParseException("Invalid source id: " + sourceId);
            }
            return source.createExplosionType(jobj);
        }
        catch (Exception e) {
            if(e instanceof JsonParseException) {
                throw e;
            }
            throw new JsonParseException(e);
        }
    }
}
