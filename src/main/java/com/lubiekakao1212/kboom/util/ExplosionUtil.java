package com.lubiekakao1212.kboom.util;

import com.lubiekakao1212.qulib.random.RandomEx;
import com.lubiekakao1212.qulib.raycast.RaycastHit;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ExplosionUtil {

    public static boolean addNotPresent(Queue<BlockPos> target, Set<BlockPos> elements, BlockPos pos) {
        if(!elements.contains(pos)) {
            target.add(pos);
            elements.add(pos);
            return true;
        }
        return false;
    }

    public static boolean handleRayBlockDamage(ServerWorld world, RandomEx random, RayProperties props, DoubleRef rayPower, Map<BlockPos, Double> damagedBlocks, RaycastHit<Vector3i> hit) {
        var pos = hit.getTarget();

        if(pos.y < world.getBottomY() || pos.y > world.getTopY()) {
            return false;
        }

        var intersection = hit.getIntersection();
        var intersectionDistance = intersection.getDistanceMax() - intersection.getDistanceMin();
        var bPos = new BlockPos(pos.x, pos.y, pos.z);
        var resistanceTotal = world.getBlockState(bPos).getBlock().getBlastResistance();
        var resistanceScaled = resistanceTotal * intersectionDistance;

        var damage = intersectionDistance * rayPower.value;
        rayPower.value -= resistanceScaled / props.penetration * random.randomScaleLinear(props.penertationNoise);
        if(rayPower.value <= 0) {
            //we want to offset the damage dealt by the overused power
            //ray power is negative, so we add instead of subtracting
            damage += rayPower.value;
        }

        var blockHealth = damagedBlocks.getOrDefault(bPos, (double)resistanceTotal);
        blockHealth -= damage;
        damagedBlocks.put(bPos, blockHealth);

        rayPower.value -= props.naturalDecay * intersectionDistance;

        return rayPower.value > 0;
    }
}
