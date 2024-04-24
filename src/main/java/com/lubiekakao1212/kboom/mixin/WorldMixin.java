package com.lubiekakao1212.kboom.mixin;

import com.lubiekakao1212.kboom.KBoom;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.registry.KBoomRegistries;
import com.lubiekakao1212.qulib.math.mc.Vector3m;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
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

		var world = (ServerWorld) (Object) this;
		var destroyBlocks = explosionSourceType != World.ExplosionSourceType.MOB || world.getLevelProperties().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);

		var explosion = KBoomRegistries.EXPLOSIONS.get(exploionId);
		if(explosion == null) {
			KBoom.LOGGER.warn("Explosion not found: " + exploionId);
		} else {
			damageSource = damageSource!= null ? damageSource : world.getDamageSources().explosion(null);
			var props = new ExplosionProperties(power, destroyBlocks, true, createFire, null, entity, damageSource, behavior);
			explosion.explode(world, new Vector3m(x, y, z), props);
		}

		cir.setReturnValue(new Explosion(world, entity, x, y, z, power, new ArrayList<>()));
		cir.cancel();
	}
}