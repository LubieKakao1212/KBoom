package com.lubiekakao1212.kboom;

import com.lubiekakao1212.kboom.registry.KBoomRegistries;
import com.lubiekakao1212.kboom.resource.ExplosionTypeManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class KBoomCommon implements ModInitializer {

	@Override
	public void onInitialize() {
		KBoomRegistries.init();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(KBoomRegistries.EXPLOSIONS);
	}
}