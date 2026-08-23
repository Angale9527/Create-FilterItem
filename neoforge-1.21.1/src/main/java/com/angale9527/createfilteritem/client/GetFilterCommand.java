package com.angale9527.createfilteritem.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.angale9527.createfilteritem.CreateFilterItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.simibubi.create.content.logistics.filter.FilterItem;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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
				.suggests(GetFilterCommand::suggestFilterItems)
				.executes(GetFilterCommand::executeItem)));
	}

	private static CompletableFuture<Suggestions> suggestFilterItems(CommandContext<CommandSourceStack> ctx,
			SuggestionsBuilder builder) {
		String remaining = builder.getRemaining();
		if (remaining.indexOf('[') >= 0 || remaining.indexOf('{') >= 0) {
			return Suggestions.empty();
		}

		List<ResourceLocation> ids = new ArrayList<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (item instanceof FilterItem) {
				ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
				if (id != null) {
					ids.add(id);
				}
			}
		}
		return SharedSuggestionProvider.suggestResource(ids, builder);
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
