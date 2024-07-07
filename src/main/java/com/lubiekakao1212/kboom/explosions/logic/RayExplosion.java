package com.lubiekakao1212.kboom.explosions.logic;

import com.google.gson.annotations.SerializedName;
import com.lubiekakao1212.kboom.explosions.ExplosionProperties;
import com.lubiekakao1212.kboom.explosions.ExplosionReference;
import com.lubiekakao1212.kboom.explosions.IExplosion;
import com.lubiekakao1212.kboom.util.DoubleRef;
import com.lubiekakao1212.kboom.util.ExplosionUtil;
import com.lubiekakao1212.kboom.util.RayProperties;
import com.lubiekakao1212.kboom.util.Validation;
import com.lubiekakao1212.qulib.math.mc.Vector3m;
import com.lubiekakao1212.qulib.random.RandomEx;
import com.lubiekakao1212.qulib.raycast.RaycastHit;
import com.lubiekakao1212.qulib.raycast.RaycastUtilKt;
import com.lubiekakao1212.qulib.raycast.config.RaycastResultConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.*;
import java.util.stream.Collectors;

import static com.lubiekakao1212.kboom.util.ExplosionUtil.*;

public class RayExplosion implements IExplosion {

    @SerializedName("sphere-size")
    private float sphereSize = 8.5f;

    @SerializedName("drop-chance")
    private float dropChance = 0f;

    @SerializedName("randomize-direction")
    private boolean randomizeDirection = true;

    @SerializedName("entity-behaviour")
    private EntityBehaviour entityBehaviour = null;

    @SerializedName("ray")
    private RayProperties rayProperties = null;

    private ExplosionProperties.Overrides overrides = new ExplosionProperties.Overrides();

    private transient Set<Vector3d> rayDirections;

    private final transient RandomEx random = new RandomEx();

    @Override
    public void explode(ServerWorld world, Vector3m position, ExplosionProperties props) {
        props = overrides.apply(props);

        var doEntityDamage = props.damageEntities();
        var doBlockDamage = props.destroyBlocks();

        var damagedBlocks = new HashMap<BlockPos, Double>();
        var damagedEntities = new HashMap<Entity, Pair<Double, Integer>>();

        var maxDistance = rayProperties.maxDistanceForPower(props.power());

        var shouldAffectEntities = entityBehaviour != null;

        List<Entity> entities = new ArrayList<>();
        if(shouldAffectEntities) {
            entities = world.getNonSpectatingEntities(Entity.class,
                    Box.of(
                            position.toVec3d(),
                            maxDistance, maxDistance, maxDistance
                    ));
        }

        for(var dir : prepareRays(randomizeDirection)) {
            DoubleRef rayPower = new DoubleRef(props.power());

            var entityQueue = doEntityRaycast(entities, position, dir, maxDistance);

            RaycastUtilKt.raycastGridUntil(position, dir, Double.POSITIVE_INFINITY, true, (hit) -> {
                handleEntities(rayPower, damagedEntities, entityQueue, hit);
                return ExplosionUtil.handleRayBlockDamage(world, random, rayProperties, rayPower, damagedBlocks, hit);
            });
        }


        for (var entityEntry : damagedEntities.entrySet()) {
            var entity = entityEntry.getKey();

            var exposure = calculateExposure(entityEntry.getValue());
            var ePos = entity.getPos();

            //Don't need to check for shouldAffectEntities, damagedEntities would be empty
            //We divide by 20 so it represents blocks/s and not blocks/t
            entity.addVelocity(ePos.subtract(position.toVec3d()).normalize().multiply(exposure * entityBehaviour.knockbackMul / 20.0));
            
            if(doEntityDamage) {
                entity.damage(props.damageSource(), (float)exposure * entityBehaviour.damageMul);
            }
        }

        for (var blockEntry : damagedBlocks.entrySet()) {
            if(doBlockDamage && blockEntry.getValue() <= 0) {
                world.setBlockState(blockEntry.getKey(), Blocks.AIR.getDefaultState());
            }
        }
    }

    private Queue<RaycastHit<Entity>> doEntityRaycast(List<Entity> entities, Vector3m origin, Vector3d direction, double maxDistance) {
        var entityRaycast = RaycastUtilKt.raycastEntitiesAll(entities, origin, direction);
        entityRaycast = RaycastUtilKt.configure(entityRaycast,
                new RaycastResultConfig<Entity>(raycastHits -> raycastHits)
                        .filtering(hit -> hit.getIntersection().isValid())
                        .clipping(maxDistance)
                        .sorting(hit -> hit.component1().getDistanceMin())
        );
        return new ArrayDeque<>(entityRaycast);
    }

    private void handleEntities(DoubleRef rayPower, HashMap<Entity, Pair<Double, Integer>> damagedEntities, Queue<RaycastHit<Entity>> entityQueue, RaycastHit<Vector3i> blockHit) {
        var entityHit = entityQueue.peek();
        while(entityHit != null) {
            var entityNear = entityHit.getIntersection().getDistanceMin();
            var blockFar = blockHit.getIntersection().getDistanceMax();
            if(entityNear > blockFar) {
                break;
            }
            entityQueue.remove();
            var entity = entityHit.getTarget();
            var entry = damagedEntities.get(entity);

            if(entry == null) {
                entry = new Pair<>(rayPower.value,1);
                damagedEntities.put(entity, entry);
            } else
            {
                var damageDealt = entry.getLeft() + rayPower.value;
                var rays = entry.getRight() + 1;
                entry.setLeft(damageDealt);
                entry.setRight(rays);
            }
            entityHit = entityQueue.peek();
        }
    }

    /**
     * Finalizes and validates its data after deserialization
     * @throws IllegalArgumentException when given instance has corrupted data
     * @implNote Don't load {@link ExplosionReference}s in this method, instead use {@link #getDependencies()}
     */
    @Override
    public void initialize() {
        Validation.requiredProperty(rayProperties, "ray");

        var queue = new ArrayDeque<BlockPos>();
        var visited = new HashSet<BlockPos>();
        rayDirections = new HashSet<>();

        BlockPos pos = new BlockPos(0,0,0);
        queue.add(pos);
        visited.add(pos);

        var radiusSq = sphereSize * sphereSize;

        while((pos = queue.poll()) != null) {
            var flag = true;

            flag &= handleElement(queue, visited, pos.up(), radiusSq);
            flag &= handleElement(queue, visited, pos.down(), radiusSq);
            flag &= handleElement(queue, visited, pos.north(), radiusSq);
            flag &= handleElement(queue, visited, pos.south(), radiusSq);
            flag &= handleElement(queue, visited, pos.east(), radiusSq);
            flag &= handleElement(queue, visited, pos.west(), radiusSq);

            if(!flag) {
                rayDirections.add(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
            }
        }
    }

    private static boolean handleElement(Queue<BlockPos> target, Set<BlockPos> elements, BlockPos pos, float radiusSq) {
        if(pos.getSquaredDistance(0,0,0) > radiusSq) {
            return false;
        }
        addNotPresent(target, elements, pos);
        return true;
    }

    private List<Vector3d> prepareRays(boolean randomizeDirection) {
        var stream = rayDirections.stream();
        if(randomizeDirection) {
            stream = stream.map((v) -> new Vector3d(
                    random.nextDouble(-0.5, 0.5),
                    random.nextDouble(-0.5, 0.5),
                    random.nextDouble(-0.5, 0.5)
            ).add(v).normalize());
        }
        else {
            stream = stream.map(Vector3d::normalize);
        }

        return stream.collect(Collectors.toList());
    }

    private double calculateExposure(Pair<Double, Integer> entry) {
        var rawExposure = entry.getLeft();
        var rayCount = entry.getRight();
        return rawExposure * Math.pow(rayCount, entityBehaviour.rayCountExponent) / rayCount;
    }


    public static class EntityBehaviour {

        @SerializedName("damage-multiplier")
        public float damageMul = 1f;

        @SerializedName("knockback-multiplier")
        public float knockbackMul = 0f;

        @SerializedName("ray-count-exponent")
        public float rayCountExponent = 0.5f;

    }
}
