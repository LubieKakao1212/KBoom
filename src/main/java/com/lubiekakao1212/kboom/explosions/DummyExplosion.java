package com.lubiekakao1212.kboom.explosions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

//TODO make a dummy explosion
public class DummyExplosion extends Explosion {

    public DummyExplosion(World world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, DestructionType destructionType) {
        super(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
    }

    @Override
    public DamageSource getDamageSource() {
        return super.getDamageSource();
    }

    @Override
    public List<BlockPos> getAffectedBlocks() {
        return super.getAffectedBlocks();
    }

    @Override
    public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
        return super.getAffectedPlayers();
    }
}
