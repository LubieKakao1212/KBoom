package com.lubiekakao1212.kboom.explosions;

import com.google.gson.annotations.SerializedName;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.explosions.ExplosionReference;
import com.lubiekakao1212.kboom.explosions.IExplosion;
import com.lubiekakao1212.qulib.math.mc.Vector3m;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.joml.Vector3d;

import java.util.List;
import java.util.stream.Collectors;

public class MultiExplosion implements IExplosion {

    @SerializedName("list")
    private List<ExplosionReference> subExplosions;

    private ExplosionProperties.Overrides overrides = new ExplosionProperties.Overrides();

    @Override
    public void explode(ServerWorld world, Vector3m position, ExplosionProperties props) {
        props = overrides.apply(props);
        for(var explosion : subExplosions) {
            explosion.get().explode(world, position, props);
        }
    }

    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load {@link ExplosionReference}s in this method, instead use {@link #getDependencies()}
     */
    @Override
    public void initialize() {
        subExplosions.forEach(ExplosionReference::initializeEmbedded);
    }

    @Override
    public List<Identifier> getDependencies() {
        return subExplosions.stream().<Identifier>mapMulti(
                (ref, consumer) -> ref.getDependencies().forEach(consumer)
        ).collect(Collectors.toList());
    }
}
