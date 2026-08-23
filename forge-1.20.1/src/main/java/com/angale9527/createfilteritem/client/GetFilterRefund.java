package com.angale9527.createfilteritem.client;

import com.angale9527.createfilteritem.CreateFilterItem;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryRefundPacket;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
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
		return itemId + "{" + inside + "}";
	}

	/** Strip one layer of wrapping single/double quotes from a pasted SNBT blob. */
	public static String unwrapSnbt(String raw) {
		if (raw == null) {
			return "";
		}
		String text = raw.trim();
		if (text.length() >= 2) {
			char first = text.charAt(0);
			char last = text.charAt(text.length() - 1);
			if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
				text = text.substring(1, text.length() - 1)
					.trim();
			}
		}
		return text;
	}

	public static ItemStack parseFilterStack(String itemArgument) throws CommandSyntaxException {
		Minecraft mc = Minecraft.getInstance();
		if (mc.getConnection() == null) {
			throw new IllegalStateException("No client connection");
		}
		ItemParser.ItemResult result = ItemParser.parseForItem(mc.getConnection()
			.registryAccess()
			.lookupOrThrow(Registries.ITEM), new StringReader(itemArgument));
		ItemStack stack = new ItemStack(result.item()
			.value(), 1);
		CompoundTag nbt = result.nbt();
		if (nbt != null) {
			stack.setTag(nbt);
		}
		return stack;
	}

	public static ItemStack parseFilterStackFromSnbt(String snbt) throws CommandSyntaxException {
		Minecraft mc = Minecraft.getInstance();
		if (mc.getConnection() == null) {
			throw new IllegalStateException("No client connection");
		}
		String text = unwrapSnbt(snbt);
		if (text.isEmpty()) {
			throw new IllegalArgumentException("SNBT 为空");
		}
		ItemStack parsed = ItemStack.of(TagParser.parseTag(text));
		if (parsed.isEmpty()) {
			throw new IllegalArgumentException("无法从 SNBT 解析出物品");
		}
		return parsed;
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

		AllPackets.getChannel().sendToServer(new StockKeeperCategoryRefundPacket(pos, filter));
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

	public static Outcome trySendFromSnbt(String snbt) {
		try {
			return trySend(parseFilterStackFromSnbt(snbt));
		} catch (CommandSyntaxException e) {
			return Outcome.fail(Component.literal("SNBT 解析失败: " + e.getMessage()));
		} catch (IllegalArgumentException | IllegalStateException e) {
			return Outcome.fail(Component.literal(e.getMessage()));
		}
	}
}
