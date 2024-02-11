package com.lubiekakao1212.kboom.explosions;

import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3d;

public interface IExplosionPowerOverride extends IExplosionType {

    float getDefaultPower();

    @Override
    default void explode(ServerWorld world, Vector3d position) {
        explode(world, position, getDefaultPower());
    }

    void explode(ServerWorld world, Vector3d position, float powerOverride);

}
