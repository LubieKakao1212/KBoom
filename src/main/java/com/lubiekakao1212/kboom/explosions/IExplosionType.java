package com.lubiekakao1212.kboom.explosions;

import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3d;

public interface IExplosionType {

    void explode(ServerWorld world, Vector3d position);

    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     */
    void initialize();
}
