package com.lubiekakao1212.kboom.explosions.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.Expose;
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
        props = overrides.apply(props);
        for(var explosion : subExplosions) {
            explosion.explode(world, position, props);
        }
    }

    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load ExplosionTypes in this method, instead use {@linkplain IExplosionType#loadDependencies(ImmutableMap)} ()} together with {@link #getDependencies()}
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

    @Override
    public List<Identifier> getDependencies() {
        return subExplosionIds;
    }

    @Override
    public void loadDependencies(ImmutableMap<Identifier, IExplosionType> registry) {
        for(var id : subExplosionIds) {
            var explosion = registry.get(id);
            //No need for null check, we are ensured by the dependency sorter that this is correct
            /*if(explosion == null) {
                throw new IllegalArgumentException("Invalid explosion type: " + id);
            }*/
            subExplosions.add(explosion);
        }
    }
}
