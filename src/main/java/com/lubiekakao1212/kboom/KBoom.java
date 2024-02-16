package com.lubiekakao1212.kboom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lubiekakao1212.kboom.explosions.ExplosionReference;
import com.lubiekakao1212.kboom.explosions.IExplosion;
import com.lubiekakao1212.kboom.json.ExplosionReferenceDeserializer;
import com.lubiekakao1212.kboom.json.IExplosionDeserializer;
import com.lubiekakao1212.kboom.json.IdentifierDeserializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KBoom {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new IdentifierDeserializer())
            .registerTypeAdapter(IExplosion.class, new IExplosionDeserializer())
            .registerTypeAdapter(ExplosionReference.class, new ExplosionReferenceDeserializer())
            .create();

    public static final String MODID = "kboom";

    public static final Logger LOGGER = LoggerFactory.getLogger("kboom");
}
