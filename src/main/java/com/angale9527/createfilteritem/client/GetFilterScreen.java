package com.angale9527.createfilteritem.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GetFilterScreen extends Screen {
	private enum FilterChoice {
		LIST("create:filter", "清单过滤器"),
		ATTRIBUTE("create:attribute_filter", "属性过滤器"),
		PACKAGE("create:package_filter", "包裹过滤器");

		final String id;
		final String label;

		FilterChoice(String id, String label) {
			this.id = id;
			this.label = label;
		}
	}

	private static final int PANEL_W = 240;
	private static final int PANEL_H = 196;

	private FilterChoice selected = FilterChoice.LIST;
	private EditBox nbtBox;
	private Button[] choiceButtons;

	public GetFilterScreen() {
		super(Component.literal("GetFilter"));
	}

	@Override
	protected void init() {
		int left = (this.width - PANEL_W) / 2;
		int top = (this.height - PANEL_H) / 2;

		choiceButtons = new Button[FilterChoice.values().length];
		int y = top + 48;
		int i = 0;
		for (FilterChoice choice : FilterChoice.values()) {
			final FilterChoice c = choice;
			choiceButtons[i] = Button.builder(choiceLabel(c), btn -> {
				selected = c;
				refreshChoiceButtons();
			})
				.bounds(left + 12, y, PANEL_W - 24, 20)
				.build();
			addRenderableWidget(choiceButtons[i]);
			y += 22;
			i++;
		}
		refreshChoiceButtons();

		nbtBox = new EditBox(this.font, left + 12, top + 124, PANEL_W - 24, 20, Component.literal("brackets"));
		nbtBox.setMaxLength(32500);
		nbtBox.setHint(Component.literal("括号内内容，可留空"));
		nbtBox.setValue("");
		addRenderableWidget(nbtBox);

		addRenderableWidget(Button.builder(Component.literal("取消"), btn -> onClose())
			.bounds(left + 12, top + PANEL_H - 28, 100, 20)
			.build());
		addRenderableWidget(Button.builder(Component.literal("确定"), btn -> onConfirm())
			.bounds(left + PANEL_W - 112, top + PANEL_H - 28, 100, 20)
			.build());

		setInitialFocus(nbtBox);
	}

	private Component choiceLabel(FilterChoice choice) {
		String mark = choice == selected ? "[*] " : "[ ] ";
		return Component.literal(mark + choice.label + " (" + choice.id + ")");
	}

	private void refreshChoiceButtons() {
		if (choiceButtons == null) {
			return;
		}
		FilterChoice[] values = FilterChoice.values();
		for (int i = 0; i < choiceButtons.length; i++) {
			choiceButtons[i].setMessage(choiceLabel(values[i]));
		}
	}

	private String previewArgument() {
		return GetFilterRefund.buildItemArgument(selected.id, nbtBox == null ? "" : nbtBox.getValue());
	}

	private void onConfirm() {
		GetFilterRefund.Outcome outcome = GetFilterRefund.trySendParsed(previewArgument());
		if (minecraft != null && minecraft.player != null) {
			if (outcome.success()) {
				minecraft.player.displayClientMessage(outcome.message(), false);
			} else {
				minecraft.player.displayClientMessage(outcome.message(), false);
			}
		}
		onClose();
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		// Skip vanilla blur / dim so the panel reads as a translucent overlay.
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.fill(0, 0, this.width, this.height, 0x66000000);

		int left = (this.width - PANEL_W) / 2;
		int top = (this.height - PANEL_H) / 2;
		graphics.fill(left, top, left + PANEL_W, top + PANEL_H, 0xB0181C22);
		graphics.renderOutline(left, top, PANEL_W, PANEL_H, 0x66FFFFFF);

		graphics.drawString(this.font, this.title, left + 12, top + 8, 0xFFFFFFFF, false);
		graphics.drawString(this.font, Component.literal(previewArgument()), left + 12, top + 20, 0xFF9FD6FF, false);
		graphics.drawString(this.font, Component.literal("过滤器类型"), left + 12, top + 36, 0xFFB8C0C8, false);
		graphics.drawString(this.font, Component.literal("括号内内容"), left + 12, top + 112, 0xFFB8C0C8, false);

		super.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
