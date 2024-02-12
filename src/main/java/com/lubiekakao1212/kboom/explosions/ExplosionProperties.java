package com.lubiekakao1212.kboom.explosions;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.explosion.ExplosionBehavior;

public record ExplosionProperties(float power, boolean createFire, Entity entity, DamageSource damageSource, ExplosionBehavior behaviour) {

    public static class Overrides {
        private Float power = null;

        @SerializedName("power-scale")
        private float powerScale = 1f;

        private Boolean createFire = null;

        public ExplosionProperties apply(ExplosionProperties source) {
            return new ExplosionProperties(
                    power != null ? power * powerScale : source.power * powerScale,
                    createFire != null ? createFire : source.createFire,
                    source.entity,
                    source.damageSource,
                    source.behaviour
            );
        }

    }

}
