package com.lubiekakao1212.kboom.explosions.impl;

import com.google.gson.annotations.SerializedName;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.explosions.ExplosionTypeSource;
import com.lubiekakao1212.kboom.explosions.IExplosionType;
import com.lubiekakao1212.kboom.registry.KBoomRegistries;
import com.lubiekakao1212.kboom.resource.ExplosionTypeManager;
import com.lubiekakao1212.kboom.resource.KBoomConstants;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class MultiExplosion implements IExplosionType {

    @SerializedName("list")
    private List<Identifier> subExplosionIds;

    private ExplosionProperties.Overrides overrides = new ExplosionProperties.Overrides();

    private transient List<IExplosionType> subExplosions = new ArrayList<>();

    @Override
    public void explode(ServerWorld world, Vector3d position, ExplosionProperties props) {
        ensureInitialized();
        props = overrides.apply(props);
        for(var explosion : subExplosions) {
            explosion.explode(world, position, props);
        }
    }

    /**
     * Finalizes and validates its data after deserialization
     *
     * @throws IllegalArgumentException when given instance has corrupted data
     */
    @Override
    public void initialize() {
        //TODO
        //hmm...
        //Can't load subExplosionsHere since they are not loaded yet
        //Do a topo sort?
        /*for(var id : subExplosionIds) {
            if(expl == null) {

            }
        }*/
    }

    @Deprecated(forRemoval = true, since = "Temporary")
    private void ensureInitialized() {
        if(!subExplosions.isEmpty()) {
            return;
        }
        for(var id : subExplosionIds) {
            var explosion = KBoomRegistries.EXPLOSIONS.get(id);
            if(explosion == null) {
                throw new IllegalArgumentException("Invalid explosion type: " + id);
            }
            subExplosions.add(explosion);
        }
    }

}
