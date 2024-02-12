package com.lubiekakao1212.kboom.mixin;

import com.lubiekakao1212.kboom.KBoom;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.registry.KBoomRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

import static com.lubiekakao1212.kboom.registry.KBoomRegistries.EntryIds.*;

@Mixin(World.class)
public class WorldMixin {

	@Inject(at = @At("HEAD"), cancellable = true, method = "createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;Z)Lnet/minecraft/world/explosion/Explosion;")
	private void createExplosion(Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, boolean particles, CallbackInfoReturnable<Explosion> cir) {
		Identifier exploionId = null;
		switch (explosionSourceType)
		{
			case NONE -> exploionId = EXPLOSION_BASIC;
			case BLOCK -> exploionId = EXPLOSION_BLOCK;
			case MOB -> exploionId = EXPLOSION_MOB;
			case TNT -> exploionId = EXPLOSION_TNT;
		}

		var explosion = KBoomRegistries.EXPLOSIONS.get(exploionId);
		if(explosion == null) {
			KBoom.LOGGER.warn("Explosion not found: " + exploionId);
		} else {
			var props = new ExplosionProperties(power, createFire, entity, damageSource, behavior);
			explosion.explode((ServerWorld) entity.getWorld(), new Vector3d(x, y, z), props);
		}

		var world = (World)(Object)this;

		cir.setReturnValue(new Explosion(world, entity, x, y, z, power, new ArrayList<>()));
		cir.cancel();
	}
}