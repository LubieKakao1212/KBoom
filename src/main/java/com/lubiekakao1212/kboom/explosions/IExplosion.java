package com.lubiekakao1212.kboom.explosions;

import com.google.common.collect.ImmutableMap;
import com.lubiekakao1212.qulib.math.mc.Vector3m;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public interface IExplosion {

    void explode(ServerWorld world, Vector3m position, ExplosionProperties props);

    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load {@link ExplosionReference}s in this method, instead use {@link #getDependencies()}
     */
    void initialize();

    /**
     * @return A list of all explosion types that are required to be loaded before this explosion type
     */
    default List<Identifier> getDependencies() { return new ArrayList<>(); }
}
