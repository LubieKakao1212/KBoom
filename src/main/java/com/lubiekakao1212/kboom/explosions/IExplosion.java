package com.lubiekakao1212.kboom.explosions;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public interface IExplosion {

    void explode(ServerWorld world, Vector3d position, ExplosionProperties props);

    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load ExplosionTypes in this method, instead use {@link #getDependencies()}
     */
    void initialize();

    /**
     * @return A list of all explosion types that are required to be loaded before this explosion type
     */
    default List<Identifier> getDependencies() { return new ArrayList<>(); }

    /**
     * @implSpec Use this to convert your IExplosionType instances
     */
    //default void loadDependencies(ImmutableMap<Identifier, IExplosion> registry) { }
}
