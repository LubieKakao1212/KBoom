package com.lubiekakao1212.kboom.registry;

import com.lubiekakao1212.kboom.KBoom;
import com.lubiekakao1212.kboom.explosions.EntityExplosion;
import com.lubiekakao1212.kboom.explosions.ExplosionTypeSource;
import com.lubiekakao1212.kboom.resource.ExplosionTypeManager;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class KBoomRegistries {

    public static final RegistryKey<Registry<ExplosionTypeSource>> EXPLOSION_SOURCES_KEY = RegistryKey.ofRegistry(new Identifier(KBoom.MODID, "explosion_source"));

    public static final ExplosionTypeManager EXPLOSIONS = new ExplosionTypeManager();

    public static final Registry<ExplosionTypeSource> EXPLOSION_SOURCES = FabricRegistryBuilder.createDefaulted(EXPLOSION_SOURCES_KEY, new Identifier("minecraft","explosion"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static void init() {
        Registry.register(EXPLOSION_SOURCES, new Identifier("minecraft","explosion"), ExplosionTypeSource.defaultFor(EntityExplosion.class));
    }

}
