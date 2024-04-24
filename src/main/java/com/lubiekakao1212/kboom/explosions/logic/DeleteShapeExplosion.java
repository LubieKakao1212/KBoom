package com.lubiekakao1212.kboom.explosions.logic;

import com.google.gson.annotations.SerializedName;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.explosions.IExplosion;
import com.lubiekakao1212.kboom.util.Shape;
import com.lubiekakao1212.qulib.math.mc.Vector3m;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static com.lubiekakao1212.kboom.util.ExplosionUtil.*;

public class DeleteShapeExplosion implements IExplosion {

    private ExplosionProperties.Overrides overrides;

    @SerializedName("drop-chance")
    private float dropChance = 0f;

    @SerializedName("shape")
    private Shape shape = Shape.SPHERE;

    @SerializedName("max-resistance")
    private float maxResistance = 10;

    private final transient Random random = new Random();

    @Override
    public void explode(ServerWorld world, Vector3m position, ExplosionProperties props) {
        props = overrides.apply(props);

        var radius = props.power();

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

            var delta = pos.subtract(origin);
            if(shape.getDistanceFunction().getDistanceToCenter(delta) > radius) {
                continue;
            }
            if(world.getBlockState(pos).getBlock().getBlastResistance() > maxResistance) {
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


    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load ExplosionTypes in this method, instead use {@link #getDependencies()}
     */
    @Override
    public void initialize() {
        if(shape == null) {
            throw new IllegalArgumentException("JSON field \"shape\" has an invalid value");
        }
    }
}
