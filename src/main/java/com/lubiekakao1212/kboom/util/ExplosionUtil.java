package com.lubiekakao1212.kboom.util;

import net.minecraft.util.math.BlockPos;

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

}
