package com.lubiekakao1212.kboom.explosions.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.explosions.IExplosion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.Random;

import static java.lang.Math.*;

public class EntityExplosion implements IExplosion {

    @SerializedName("entity-type")
    private Identifier entityTypeId;

    @SerializedName("entity-count")
    private int entityCount;

    private ExplosionProperties.Overrides overrides = new ExplosionProperties.Overrides();

    private double offset = 0.1f;

    private transient EntityType<?> entityType = null;
    private final transient Random random = new Random();

    @Override
    public void explode(ServerWorld world, Vector3d position, @NotNull ExplosionProperties properties) {
        var props = overrides.apply(properties);

        var power = props.power();

        for(int i = 0; i < entityCount; i++) {
            var phi = random.nextDouble(-1, 1);
            phi = acos(phi);
            var theta = random.nextDouble(PI * 2);

            var x = sin(phi) * cos(theta);
            var y = cos(phi);
            var z = sin(phi) * sin(theta);

            entityType.spawn(world, new NbtCompound(), (entity) -> {
                entity.setVelocity(x * power, y * power, z * power);
                entity.setPos(position.x + x * offset, position.y + y * offset, position.z + z * offset);
            }, BlockPos.ofFloored(position.x, position.y, position.z), SpawnReason.MOB_SUMMONED, false, false);
        }
    }

    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load ExplosionTypes in this method, instead use {@link #getDependencies()}
     */
    @Override
    public void initialize() {
        var registry = Registries.ENTITY_TYPE;
        if(!registry.containsId(entityTypeId)) {
            throw new IllegalArgumentException("Invalid entity type: " + entityTypeId);
        }
        entityType = registry.get(entityTypeId);
    }
}
