package com.lubiekakao1212.kboom.explosions;

import com.google.common.collect.Lists;
import com.lubiekakao1212.kboom.registry.KBoomRegistries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExplosionReference {

    private IExplosion explosion;
    private final Identifier identifier;

    public ExplosionReference(@NotNull Identifier id) {
        this.identifier = id;
        explosion = null;
    }

    public ExplosionReference(@NotNull IExplosion explosion) {
        this.explosion = explosion;
        this.identifier = null;
    }

    public IExplosion get() {
        if(explosion == null) {
            explosion = KBoomRegistries.EXPLOSIONS.get(identifier);
        }
        return explosion;
    }

    public List<Identifier> getDependencies() {
        if(identifier != null) {
            return Lists.newArrayList(identifier);
        }
        return explosion.getDependencies();
    }

    public void initializeEmbedded() {
        if(identifier != null) {
            return;
        }
        explosion.initialize();
    }
}
