package com.lubiekakao1212.kboom.explosions.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import com.lubiekakao1212.kboom.KBoom;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.explosions.IExplosionType;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.*;

public class DeleteSphereExplosion implements IExplosionType {

    private ExplosionProperties.Overrides overrides;

    @SerializedName("drop-chance")
    private float dropChance;

    private transient Random random = new Random();

    @Override
    public void explode(ServerWorld world, Vector3d position, ExplosionProperties props) {
        props = overrides.apply(props);

        var radius = props.power();

        var radiusSq = radius * radius;

        var x = position.x;
        var y = position.y;
        var z = position.z;

        var origin = BlockPos.ofFloored(x, y, z);

        var queue = new ArrayDeque<BlockPos>();
        queue.add(origin);

        var visited = new HashSet<BlockPos>();
        visited.add(origin);

        BlockPos pos = null;

        while((pos = queue.poll()) != null) {
            if(pos.getSquaredDistanceFromCenter(x, y, z) > radiusSq) {
                continue;
            }

            if(random.nextFloat() <= dropChance) {
                world.breakBlock(pos, true);
            }
            else {
                world.removeBlock(pos, false);
            }

            addNotPresent(queue, visited, pos.up());
            addNotPresent(queue, visited, pos.down());
            addNotPresent(queue, visited, pos.east());
            addNotPresent(queue, visited, pos.west());
            addNotPresent(queue, visited, pos.north());
            addNotPresent(queue, visited, pos.south());
        }
    }

    private void addNotPresent(Queue<BlockPos> target, Set<BlockPos> elements, BlockPos pos) {
        if(!elements.contains(pos)) {
            target.add(pos);
            elements.add(pos);
        }
    }


    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load ExplosionTypes in this method, instead use {@linkplain IExplosionType#loadDependencies(ImmutableMap)} ()} together with {@link #getDependencies()}
     */
    @Override
    public void initialize() {

    }
}
