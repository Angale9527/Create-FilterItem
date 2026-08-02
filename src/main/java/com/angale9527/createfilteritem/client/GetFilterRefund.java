package com.angale9527.createfilteritem.client;

import com.angale9527.createfilteritem.CreateFilterItem;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryRefundPacket;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Shared client-side logic for StockKeeperCategoryRefundPacket reproduction.
 */
public final class GetFilterRefund {
	public record Outcome(boolean success, Component message) {
		public static Outcome ok(Component message) {
			return new Outcome(true, message);
		}

		public static Outcome fail(Component message) {
			return new Outcome(false, message);
		}
	}

	private GetFilterRefund() {
	}

	public static String buildItemArgument(String itemId, String bracketContents) {
		String inside = bracketContents == null ? "" : bracketContents;
		return itemId + "[" + inside + "]";
	}

	public static ItemStack parseFilterStack(String itemArgument) throws CommandSyntaxException {
		Minecraft mc = Minecraft.getInstance();
		if (mc.getConnection() == null) {
			throw new IllegalStateException("No client connection");
		}
		ItemParser.ItemResult result = new ItemParser(mc.getConnection()
			.registryAccess()).parse(new StringReader(itemArgument));
		ItemStack stack = new ItemStack(result.item(), 1);
		DataComponentPatch components = result.components();
		if (!components.isEmpty()) {
			stack.applyComponents(components);
		}
		return stack;
	}

	public static Outcome trySend(ItemStack filter) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null) {
			return Outcome.fail(Component.literal("无本地玩家或世界"));
		}
		if (filter.isEmpty() || !(filter.getItem() instanceof FilterItem)) {
			return Outcome.fail(Component.literal("物品必须是 Create 过滤器（filter / attribute_filter / package_filter）"));
		}

		HitResult hit = mc.hitResult;
		if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
			return Outcome.fail(Component.literal("请对准 Stock Ticker 方块"));
		}

		BlockPos pos = blockHit.getBlockPos();
		if (!(mc.level.getBlockEntity(pos) instanceof StockTickerBlockEntity)) {
			return Outcome.fail(Component.literal("准星方块不是 Stock Ticker"));
		}

		CatnipServices.NETWORK.sendToServer(new StockKeeperCategoryRefundPacket(pos, filter));
		CreateFilterItem.LOGGER.info("Sent StockKeeperCategoryRefundPacket pos={} item={}", pos, filter);
		return Outcome.ok(Component.literal("已发送退货包: ")
			.append(filter.getDisplayName())
			.append(Component.literal(" @ " + pos.toShortString())));
	}

	public static Outcome trySendParsed(String itemArgument) {
		try {
			return trySend(parseFilterStack(itemArgument));
		} catch (CommandSyntaxException e) {
			return Outcome.fail(Component.literal("物品参数解析失败: " + e.getMessage()));
		} catch (IllegalStateException e) {
			return Outcome.fail(Component.literal(e.getMessage()));
		}
	}
}
