package com.lubiekakao1212.kboom.explosions;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.Random;

import static java.lang.Math.*;

public class EntityExplosion implements IExplosionPowerOverride {

    private float power;

    @SerializedName("entity-type")
    private Identifier entityTypeId;

    private int entityCount;

    private double offset = 0.1f;

    private transient EntityType<?> entityType = null;
    private final transient Random random = new Random();

    @Override
    public void explode(ServerWorld world, Vector3d position, float powerOverride) {
        for(int i = 0; i < entityCount; i++) {
            var phi = random.nextDouble(-1, 1);
            phi = acos(phi);
            var theta = random.nextDouble(PI * 2);

            var x = sin(phi) * cos(theta);
            var y = cos(phi);
            var z = sin(phi) * sin(theta);

            //world.createExplosion();

            entityType.spawn(world, new NbtCompound(), (entity) -> {
                entity.setVelocity(x * powerOverride, y * powerOverride, z * powerOverride);
                entity.setPos(position.x + x * offset, position.y + y * offset, position.z + z * offset);
            }, BlockPos.ofFloored(position.x, position.y, position.z), SpawnReason.MOB_SUMMONED, false, false);
        }
    }

    @Override
    public float getDefaultPower() {
        return power;
    }

    /**
     * Finalizes and validates its data after deserialization
     *
     * @throws IllegalArgumentException when given instance has corrupted data
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
