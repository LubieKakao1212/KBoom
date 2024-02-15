package com.lubiekakao1212.kboom.explosions.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.explosions.IExplosionType;
import com.lubiekakao1212.qulib.raycast.RaycastUtilKt;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.*;
import java.util.stream.Collectors;

import static com.lubiekakao1212.kboom.util.ExplosionUtil.*;

public class RaySphereExplosion implements IExplosionType {

    @SerializedName("sphere-size")
    private float sphereSize = 8.5f;

    private float penetration = 1f;

    @SerializedName("natural-decay")
    private float naturalDecay = 1f / 16f;

    @SerializedName("drop-chance")
    private float dropChance = 0f;

    @SerializedName("randomness")
    private float randomness = 0f;

    private ExplosionProperties.Overrides overrides = new ExplosionProperties.Overrides();

    private transient Set<Vector3d> rayDirections;

    private transient Random random = new Random();

    @Override
    public void explode(ServerWorld world, Vector3d position, ExplosionProperties props) {
        props = overrides.apply(props);

        var destroyedBlocks = new HashMap<BlockPos, Double>();


        for(var dir : randomizeRays()) {
            double[] rayPower = new double[] { props.power() };
            //Find a better algorithm
            rayPower[0] = random.nextDouble(Math.max(rayPower[0] - randomness, 0), rayPower[0] + randomness);

            RaycastUtilKt.raycastGridUntil(position, dir, Double.POSITIVE_INFINITY, true, (hit) -> {
                var pos = hit.component2();

                if(pos.y < world.getBottomY() || pos.y > world.getTopY()) {
                    return false;
                }

                var intersection = hit.component1();
                var intersectionDistance = intersection.getDistanceMax() - intersection.getDistanceMin();
                var bPos = new BlockPos(pos.x, pos.y, pos.z);
                var resistanceTotal = world.getBlockState(bPos).getBlock().getBlastResistance();
                var resistanceScaled = resistanceTotal * intersectionDistance;

                var damage = intersectionDistance * rayPower[0];
                rayPower[0] -= resistanceScaled / penetration;
                if(rayPower[0] <= 0) {
                    //we want to offset tha damage dealt by the overused power
                    //ray power is negative, so we add instead of subtracting
                    damage += rayPower[0];
                }

                var blockHealth = destroyedBlocks.getOrDefault(bPos, (double)resistanceTotal);
                blockHealth -= damage;
                destroyedBlocks.put(bPos, blockHealth);

                rayPower[0] -= naturalDecay;

                return rayPower[0] > 0;
            });
        }

        for (var blockEntry : destroyedBlocks.entrySet()) {
            if(blockEntry.getValue() <= 0) {
                world.setBlockState(blockEntry.getKey(), Blocks.AIR.getDefaultState());
            }
        }
    }

    /**
     * Finalizes and validates its data after deserialization
     *
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load ExplosionTypes in this method, instead use {@linkplain IExplosionType#loadDependencies(ImmutableMap)} together with {@link #getDependencies()}
     */
    @Override
    public void initialize() {
        var queue = new ArrayDeque<BlockPos>();
        var visited = new HashSet<BlockPos>();
        rayDirections = new HashSet<>();

        BlockPos pos = new BlockPos(0,0,0);
        queue.add(pos);
        visited.add(pos);

        var radiusSq = sphereSize * sphereSize;

        while((pos = queue.poll()) != null) {
            var flag = true;

            flag &= handleElement(queue, visited, pos.up(), radiusSq);
            flag &= handleElement(queue, visited, pos.down(), radiusSq);
            flag &= handleElement(queue, visited, pos.north(), radiusSq);
            flag &= handleElement(queue, visited, pos.south(), radiusSq);
            flag &= handleElement(queue, visited, pos.east(), radiusSq);
            flag &= handleElement(queue, visited, pos.west(), radiusSq);

            if(!flag) {
                rayDirections.add(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
            }
        }
    }

    private boolean handleElement(Queue<BlockPos> target, Set<BlockPos> elements, BlockPos pos, float radiusSq) {
        if(pos.getSquaredDistance(0,0,0) > radiusSq) {
            return false;
        }
        addNotPresent(target, elements, pos);
        return true;
    }

    private List<Vector3d> randomizeRays() {
        return rayDirections.stream().map((v) -> new Vector3d(
                        random.nextDouble(-0.5, 0.5),
                        random.nextDouble(-0.5, 0.5),
                        random.nextDouble(-0.5, 0.5)
                ).add(v).normalize()).collect(Collectors.toList());
    }

}
