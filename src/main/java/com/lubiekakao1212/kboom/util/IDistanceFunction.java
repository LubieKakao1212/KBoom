package com.lubiekakao1212.kboom.util;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.List;

import static java.lang.Math.*;

@FunctionalInterface
public interface IDistanceFunction {

    double getDistanceToCenter(@NotNull BlockPos pos);

    static double cube(@NotNull BlockPos pos) {
        return max(
                abs(pos.getX()),
                max(
                        abs(pos.getY()),
                        abs(pos.getZ())
                )
        );
    }

    static double sphere(@NotNull BlockPos pos) {
        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();
        return sqrt(x*x + y*y + z*z);
    }

    static double manhattan(@NotNull BlockPos pos) {
        return abs(pos.getX()) + abs(pos.getY()) + abs(pos.getZ());
    }

    @NotNull
    static IDistanceFunction minkowski(double exponent) {
        return (pos) -> {
            var x = abs(pos.getX());
            var y = abs(pos.getY());
            var z = abs(pos.getZ());
            return pow(
                    pow(x, exponent) + pow(y, exponent) + pow(z, exponent),
                    1 / exponent
            );
        };
    }

}
