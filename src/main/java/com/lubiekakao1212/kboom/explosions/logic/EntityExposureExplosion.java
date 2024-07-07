package com.lubiekakao1212.kboom.explosions.logic;

import com.google.gson.annotations.SerializedName;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.explosions.ExplosionReference;
import com.lubiekakao1212.kboom.explosions.IExplosion;
import com.lubiekakao1212.kboom.util.*;
import com.lubiekakao1212.qulib.math.extensions.AABBExtensionsKt;
import com.lubiekakao1212.qulib.math.mc.Vector3m;
import com.lubiekakao1212.qulib.random.RandomEx;
import com.lubiekakao1212.qulib.raycast.RaycastUtilKt;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Vector3d;

import java.util.List;

public class EntityExposureExplosion implements IExplosion {

    @SerializedName("damage-multiplier")
    public float damageMul = 1f;

    @SerializedName("knockback-multiplier")
    public float knockbackMul = 0f;

    @SerializedName("ray")
    private RayProperties rayProperties = null;

    private Anchors anchors = new Anchors();

    private ExplosionProperties.Overrides overrides = new ExplosionProperties.Overrides();

    private final transient RandomEx random = new RandomEx();

    @Override
    public void explode(ServerWorld world, Vector3m position, ExplosionProperties props) {
        props = overrides.apply(props);

        var maxDistance = rayProperties.maxDistanceForPower(props.power());
        var doEntityDamage = props.damageEntities();
        var maxDistanceSqr = maxDistance * maxDistance;
        var mcPos = position.toVec3d();

        List<Entity> entities = world.getNonSpectatingEntities(Entity.class,
                Box.of(
                        position.toVec3d(),
                        maxDistance, maxDistance, maxDistance
                ));

        var voidMap = new VoidMap<BlockPos, Double>(() -> 0.0);
        for(var entity : entities) {
            var box = entity.getBoundingBox();

            if(entity.getPos().squaredDistanceTo(mcPos) > maxDistanceSqr) {
                continue;
            }

            double totalPower = 0.0;
            var i = 0;
            for(var x : anchors.x)
                for(var y : anchors.y)
                    for(var z : anchors.z)
                    {
                        var point = AABBExtensionsKt.interpolate(box, x, y, z, new Vector3d());

                        var dir = point.sub(position, point);

                        var rayPower = new DoubleRef(props.power());

                        RaycastUtilKt.raycastGridUntil(position, dir.normalize(new Vector3d()), dir.length(), true,
                                (hit) -> ExplosionUtil.handleRayBlockDamage(world, random, rayProperties, rayPower, voidMap, hit)
                        );
                        totalPower += Math.max(rayPower.value, 0);
                        i++;
                    }

            var avgPower = totalPower / i;

            entity.addVelocity(entity.getPos().subtract(mcPos).normalize().multiply(avgPower * knockbackMul / 20.0));

            if(doEntityDamage) {
                entity.damage(props.damageSource(), (float)avgPower * damageMul);
            }
        }
    }

    /**
     * Finalizes and validates its data after deserialization
     *
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load {@link ExplosionReference}s in this method, instead use {@link #getDependencies()}
     */
    @Override
    public void initialize() {
        Validation.requiredProperty(rayProperties, "ray");
    }

    public static class Anchors {

        public double[] x;
        public double[] y;
        public double[] z;

        public Anchors() {
            this(
                    new double[] { 0.25f, 0.5f, 0.75f },
                    new double[] { 0.25f, 0.5f, 0.75f },
                    new double[] { 0.25f, 0.5f, 0.75f }
            );
        }

        public Anchors(double[] x,double[] y, double[] z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

}
