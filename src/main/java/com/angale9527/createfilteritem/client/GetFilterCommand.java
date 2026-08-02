package com.angale9527.createfilteritem.client;

import com.angale9527.createfilteritem.CreateFilterItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

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
			.then(Commands.literal("gui")
				.executes(GetFilterCommand::openGui))
			.then(Commands.argument("item", ItemArgument.item(event.getBuildContext()))
				.executes(GetFilterCommand::executeItem)));
	}

	private static int openGui(CommandContext<CommandSourceStack> ctx) {
		Minecraft.getInstance()
			.setScreen(new GetFilterScreen());
		ctx.getSource()
			.sendSuccess(() -> Component.literal("已打开 GetFilter 界面"), false);
		return 1;
	}

	private static int executeItem(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ItemInput itemInput = ItemArgument.getItem(ctx, "item");
		ItemStack filter = itemInput.createItemStack(1, false);
		GetFilterRefund.Outcome outcome = GetFilterRefund.trySend(filter);
		if (outcome.success()) {
			ctx.getSource()
				.sendSuccess(() -> outcome.message(), false);
			return 1;
		}
		ctx.getSource()
			.sendFailure(outcome.message());
		return 0;
	}
}
