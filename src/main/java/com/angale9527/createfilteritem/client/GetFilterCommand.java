package com.angale9527.createfilteritem.client;

import com.angale9527.createfilteritem.CreateFilterItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryRefundPacket;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = CreateFilterItem.MODID, value = Dist.CLIENT)
public final class GetFilterCommand {
	private GetFilterCommand() {
	}

	@SubscribeEvent
	public static void register(RegisterClientCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		dispatcher.register(Commands.literal("getfilter")
			.then(Commands.argument("item", ItemArgument.item(event.getBuildContext()))
				.executes(GetFilterCommand::execute)));
	}

	private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null) {
			ctx.getSource()
				.sendFailure(Component.literal("无本地玩家或世界"));
			return 0;
		}

		ItemInput itemInput = ItemArgument.getItem(ctx, "item");
		ItemStack filter = itemInput.createItemStack(1, false);
		if (filter.isEmpty() || !(filter.getItem() instanceof FilterItem)) {
			ctx.getSource()
				.sendFailure(Component.literal("物品必须是 Create 过滤器（filter / attribute_filter / package_filter）"));
			return 0;
		}

		HitResult hit = mc.hitResult;
		if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
			ctx.getSource()
				.sendFailure(Component.literal("请对准 Stock Ticker 方块"));
			return 0;
		}

		BlockPos pos = blockHit.getBlockPos();
		Level level = mc.level;
		if (!(level.getBlockEntity(pos) instanceof StockTickerBlockEntity)) {
			ctx.getSource()
				.sendFailure(Component.literal("准星方块不是 Stock Ticker"));
			return 0;
		}

		CatnipServices.NETWORK.sendToServer(new StockKeeperCategoryRefundPacket(pos, filter));
		ctx.getSource()
			.sendSuccess(() -> Component.literal("已发送退货包: ")
				.append(filter.getDisplayName())
				.append(Component.literal(" @ " + pos.toShortString())), false);
		CreateFilterItem.LOGGER.info("Sent StockKeeperCategoryRefundPacket pos={} item={}", pos, filter);
		return 1;
	}
}
