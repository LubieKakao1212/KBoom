package com.lubiekakao1212.kboom.explosions;

import com.google.gson.JsonObject;
import com.lubiekakao1212.kboom.KBoom;

@FunctionalInterface
public interface IExplosionSource {
    IExplosion createExplosionType(JsonObject serialized);

    static IExplosionSource defaultFor(Class<? extends IExplosion> clazz) {
        return (json) -> KBoom.GSON.fromJson(json, clazz);
    }

}
