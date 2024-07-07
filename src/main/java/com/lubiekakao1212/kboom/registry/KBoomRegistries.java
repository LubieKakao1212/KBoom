package com.lubiekakao1212.kboom.registry;

import com.lubiekakao1212.kboom.KBoom;
import com.lubiekakao1212.kboom.explosions.IExplosionSource;
import com.lubiekakao1212.kboom.explosions.logic.DeleteShapeExplosion;
import com.lubiekakao1212.kboom.explosions.logic.EntityExplosion;
import com.lubiekakao1212.kboom.explosions.MultiExplosion;
import com.lubiekakao1212.kboom.explosions.logic.EntityExposureExplosion;
import com.lubiekakao1212.kboom.explosions.logic.RayExplosion;
import com.lubiekakao1212.kboom.resource.ExplosionTypeManager;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class KBoomRegistries {

    public static final RegistryKey<Registry<IExplosionSource>> EXPLOSION_SOURCES_KEY = RegistryKey.ofRegistry(new Identifier(KBoom.MODID, "explosion_source"));

    public static final ExplosionTypeManager EXPLOSIONS = new ExplosionTypeManager();

    public static final Registry<IExplosionSource> EXPLOSION_SOURCES = FabricRegistryBuilder.createSimple(EXPLOSION_SOURCES_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static void init() {
        //Temporary
        //Registry.register(EXPLOSION_SOURCES, new Identifier("minecraft","explosion"), IExplosionSource.defaultFor(EntityExplosion.class));

        Registry.register(EXPLOSION_SOURCES, new Identifier("kboom","entity"), IExplosionSource.defaultFor(EntityExplosion.class));
        Registry.register(EXPLOSION_SOURCES, new Identifier("kboom","multi-explosion"), IExplosionSource.defaultFor(MultiExplosion.class));
        Registry.register(EXPLOSION_SOURCES, new Identifier("kboom","delete-shape"), IExplosionSource.defaultFor(DeleteShapeExplosion.class));
        Registry.register(EXPLOSION_SOURCES, new Identifier("kboom","ray-sphere"), IExplosionSource.defaultFor(RayExplosion.class));
        Registry.register(EXPLOSION_SOURCES, new Identifier("kboom","entity-exposure"), IExplosionSource.defaultFor(EntityExposureExplosion.class));
    }

    public static class EntryIds {
        public static final Identifier EXPLOSION_BASIC = new Identifier("minecraft:explosion");
        public static final Identifier EXPLOSION_MOB = new Identifier("minecraft:explosion-mob");
        public static final Identifier EXPLOSION_TNT = new Identifier("minecraft:explosion-tnt");
        public static final Identifier EXPLOSION_BLOCK = new Identifier("minecraft:explosion-block");
    }

}
