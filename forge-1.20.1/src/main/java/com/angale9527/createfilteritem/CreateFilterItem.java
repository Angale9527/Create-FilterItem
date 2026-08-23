package com.angale9527.createfilteritem;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.common.Mod;

@Mod(CreateFilterItem.MODID)
public class CreateFilterItem {
	public static final String MODID = "createfilteritem";
	public static final Logger LOGGER = LogUtils.getLogger();

	public CreateFilterItem() {
		LOGGER.info("Create-FilterItem loaded. Use /getfilter <item> while looking at a Stock Ticker.");
	}
}
