package com.lubiekakao1212.kboom.registry;

import com.lubiekakao1212.kboom.KBoom;
import com.lubiekakao1212.kboom.explosions.impl.DeleteSphereExplosion;
import com.lubiekakao1212.kboom.explosions.impl.EntityExplosion;
import com.lubiekakao1212.kboom.explosions.ExplosionTypeSource;
import com.lubiekakao1212.kboom.explosions.impl.MultiExplosion;
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
        //Temporary
        Registry.register(EXPLOSION_SOURCES, new Identifier("minecraft","explosion"), ExplosionTypeSource.defaultFor(EntityExplosion.class));

        Registry.register(EXPLOSION_SOURCES, new Identifier("kboom","entity"), ExplosionTypeSource.defaultFor(EntityExplosion.class));
        Registry.register(EXPLOSION_SOURCES, new Identifier("kboom","multi-explosion"), ExplosionTypeSource.defaultFor(MultiExplosion.class));
        Registry.register(EXPLOSION_SOURCES, new Identifier("kboom","delete-sphere"), ExplosionTypeSource.defaultFor(DeleteSphereExplosion.class));
    }

    public static class EntryIds {
        public static final Identifier EXPLOSION_BASIC = new Identifier("minecraft:explosion");
        public static final Identifier EXPLOSION_MOB = new Identifier("minecraft:explosion-mob");
        public static final Identifier EXPLOSION_TNT = new Identifier("minecraft:explosion-tnt");
        public static final Identifier EXPLOSION_BLOCK = new Identifier("minecraft:explosion-block");
    }

}
