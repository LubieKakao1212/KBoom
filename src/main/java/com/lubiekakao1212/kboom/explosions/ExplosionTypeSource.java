package com.lubiekakao1212.kboom.explosions;

import com.google.gson.JsonObject;
import com.lubiekakao1212.kboom.KBoom;

@FunctionalInterface
public interface ExplosionTypeSource {
    IExplosionType createExplosionType(JsonObject serialized);

    static ExplosionTypeSource defaultFor(Class<? extends IExplosionType> clazz) {
        return (json) -> KBoom.GSON.fromJson(json, clazz);
    }

}
