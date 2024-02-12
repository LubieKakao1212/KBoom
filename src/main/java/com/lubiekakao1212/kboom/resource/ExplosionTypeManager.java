package com.lubiekakao1212.kboom.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.lubiekakao1212.kboom.KBoom;
import com.lubiekakao1212.kboom.explosions.IExplosionType;
import com.lubiekakao1212.kboom.registry.KBoomRegistries;
import com.lubiekakao1212.kboom.util.DependencyUtil;
import com.mojang.datafixers.types.Func;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import static com.lubiekakao1212.kboom.resource.KBoomConstants.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ExplosionTypeManager implements SimpleResourceReloadListener<ExplosionTypeManager.LoadedData> {

    private final Gson gson = new GsonBuilder().create();
    private final String prefixPath = "kboom/explosions";
    private final int prefixPathLength = prefixPath.length() + 1;

    private Map<Identifier, IExplosionType> explosionTypes = new HashMap<>();

    public ExplosionTypeManager() {

    }


    /**
     * Asynchronously process and load resource-based data. The code
     * must be thread-safe and not modify game state!
     *
     * @param manager  The resource manager used during reloading.
     * @param profiler The profiler which may be used for this stage.
     * @param executor The executor which should be used for this stage.
     * @return A CompletableFuture representing the "data loading" stage.
     */
    @Override
    public CompletableFuture<LoadedData> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var resources = manager.findResources(prefixPath, (identifier) -> identifier.getPath().endsWith(".json"));
            var output = new HashMap<Identifier, JsonObject>();
            try {
                for (var res : resources.entrySet()) {
                    var stream = res.getValue().getInputStream();

                    var reader = new InputStreamReader(stream);
                    var json = gson.fromJson(reader, JsonObject.class);
                    stream.close();

                    var id = res.getKey();
                    var path = id.getPath();
                    //".json" = 5
                    var pathLength = path.length() - 5;
                    path = path.substring(prefixPathLength, pathLength);
                    output.put(new Identifier(id.getNamespace(), path), json);
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            return new LoadedData(output);
        }, executor);
    }

    /**
     * Synchronously apply loaded data to the game state.
     *
     * @param data
     * @param manager  The resource manager used during reloading.
     * @param profiler The profiler which may be used for this stage.
     * @param executor The executor which should be used for this stage.
     * @return A CompletableFuture representing the "data applying" stage.
     */
    @Override
    public CompletableFuture<Void> apply(LoadedData data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var explosionSourcesReg = KBoomRegistries.EXPLOSION_SOURCES;
            ImmutableMap.Builder<Identifier, IExplosionType> newTypesBuilder = ImmutableMap.builder();
            for(var res : data.resources.entrySet()) {
                var json = res.getValue();
                var id = res.getKey();

                if(json == null) {
                    KBoom.LOGGER.warn("Explosion " + id + " has malformed data");
                    continue;
                }

                var sourceId = new Identifier(json.get(EXPLOSION_SOURCE_FIELD).getAsString());
                var source = explosionSourcesReg.get(sourceId);

                if(source == null) {
                    KBoom.LOGGER.warn("Explosion " + id + " has invalid source id: " + sourceId);
                    continue;
                }
                var explosion = source.createExplosionType(json);
                explosion.initialize();
                newTypesBuilder.put(id, explosion);
            }

            var newTypes = newTypesBuilder.build();

            for(var id : DependencyUtil.dependencySort(newTypes.keySet(), (element) -> Objects.requireNonNull(newTypes.get(element)).getDependencies())) {
                Objects.requireNonNull(newTypes.get(id)).loadDependencies(newTypes);
            }

            explosionTypes = newTypes;
            return null;
        }, executor);
    }



    @Nullable
    public IExplosionType get(Identifier identifier) {
        return explosionTypes.get(identifier);
    }


    /**
     * @return The unique identifier of this listener.
     */
    @Override
    public Identifier getFabricId() {
        return new Identifier(KBoom.MODID, "explosions");
    }

    public static class LoadedData {

        public Map<Identifier, JsonObject> resources;

        public LoadedData(Map<Identifier, JsonObject> resources) {
            this.resources = resources;
        }
    }
}
