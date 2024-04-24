package com.lubiekakao1212.kboom.explosions;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public record ExplosionProperties(float power, boolean destroyBlocks, boolean damageEntities, boolean createFire, @Nullable Vector3d direction, @Nullable Entity entity, @NotNull DamageSource damageSource, @Nullable ExplosionBehavior behaviour) {

    public static class Overrides {
        private Float power = null;

        @SerializedName("power-scale")
        private float powerScale = 1f;

        @SerializedName("fire")
        private Boolean createFire = null;

        @SerializedName("block-damage")
        private Boolean destroyBlocks = null;

        @SerializedName("entity-damage")
        private Boolean damageEntities = null;

        //TODO Add direction bias
        private Vector3d direction = null;

        public ExplosionProperties apply(ExplosionProperties source) {
            return new ExplosionProperties(
                    power != null ? power * powerScale : source.power * powerScale,
                    destroyBlocks != null ? destroyBlocks : source.destroyBlocks,
                    damageEntities != null ? damageEntities : source.damageEntities,
                    createFire != null ? createFire : source.createFire,
                    direction != null ? direction : source.direction,
                    source.entity,
                    source.damageSource,
                    source.behaviour
            );
        }
    }
}
