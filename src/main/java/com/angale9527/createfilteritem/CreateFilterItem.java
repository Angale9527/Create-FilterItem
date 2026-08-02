package com.angale9527.createfilteritem;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = CreateFilterItem.MODID, dist = Dist.CLIENT)
public class CreateFilterItem {
    public static final String MODID = "createfilteritem";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateFilterItem(IEventBus modEventBus, ModContainer modContainer) {
    }
}
