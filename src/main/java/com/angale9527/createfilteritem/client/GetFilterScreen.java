package com.angale9527.createfilteritem.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GetFilterScreen extends Screen {
	private enum InputMode {
		COMPONENTS("组件括号"),
		SNBT("SNBT");

		final String label;

		InputMode(String label) {
			this.label = label;
		}
	}

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

	private static final int PANEL_W = 280;
	private static final int PANEL_H_COMPONENTS = 232;
	private static final int PANEL_H_SNBT = 180;

	private InputMode mode = InputMode.COMPONENTS;
	private FilterChoice selected = FilterChoice.LIST;
	private EditBox inputBox;
	private Button modeComponentsButton;
	private Button modeSnbtButton;
	private Button[] choiceButtons;

	public GetFilterScreen() {
		super(Component.literal("GetFilter"));
	}

	private int panelH() {
		return mode == InputMode.COMPONENTS ? PANEL_H_COMPONENTS : PANEL_H_SNBT;
	}

	@Override
	protected void init() {
		String previous = inputBox == null ? "" : inputBox.getValue();
		clearWidgets();

		int left = (this.width - PANEL_W) / 2;
		int top = (this.height - panelH()) / 2;

		modeComponentsButton = Button.builder(modeLabel(InputMode.COMPONENTS), btn -> switchMode(InputMode.COMPONENTS))
			.bounds(left + 12, top + 46, 120, 20)
			.build();
		modeSnbtButton = Button.builder(modeLabel(InputMode.SNBT), btn -> switchMode(InputMode.SNBT))
			.bounds(left + PANEL_W - 132, top + 46, 120, 20)
			.build();
		addRenderableWidget(modeComponentsButton);
		addRenderableWidget(modeSnbtButton);
		refreshModeButtons();

		choiceButtons = new Button[FilterChoice.values().length];
		int y = top + 84;
		int i = 0;
		for (FilterChoice choice : FilterChoice.values()) {
			final FilterChoice c = choice;
			choiceButtons[i] = Button.builder(choiceLabel(c), btn -> {
				selected = c;
				refreshChoiceButtons();
			})
				.bounds(left + 12, y, PANEL_W - 24, 20)
				.build();
			choiceButtons[i].visible = mode == InputMode.COMPONENTS;
			choiceButtons[i].active = mode == InputMode.COMPONENTS;
			addRenderableWidget(choiceButtons[i]);
			y += 22;
			i++;
		}
		refreshChoiceButtons();

		int inputY = mode == InputMode.COMPONENTS ? top + 160 : top + 84;
		inputBox = new EditBox(this.font, left + 12, inputY, PANEL_W - 24, 20, Component.literal("input"));
		inputBox.setMaxLength(32500);
		inputBox.setHint(mode == InputMode.COMPONENTS ? Component.literal("括号内内容，可留空")
			: Component.literal("完整物品 SNBT，含 id/count/components"));
		inputBox.setValue(previous);
		addRenderableWidget(inputBox);

		int buttonY = top + panelH() - 28;
		addRenderableWidget(Button.builder(Component.literal("取消"), btn -> onClose())
			.bounds(left + 12, buttonY, 110, 20)
			.build());
		addRenderableWidget(Button.builder(Component.literal("确定"), btn -> onConfirm())
			.bounds(left + PANEL_W - 122, buttonY, 110, 20)
			.build());

		setInitialFocus(inputBox);
	}

	private void switchMode(InputMode next) {
		if (mode == next) {
			return;
		}
		mode = next;
		init();
	}

	private Component modeLabel(InputMode value) {
		String mark = mode == value ? "[*] " : "[ ] ";
		return Component.literal(mark + value.label);
	}

	private void refreshModeButtons() {
		if (modeComponentsButton != null) {
			modeComponentsButton.setMessage(modeLabel(InputMode.COMPONENTS));
		}
		if (modeSnbtButton != null) {
			modeSnbtButton.setMessage(modeLabel(InputMode.SNBT));
		}
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

	private String previewText() {
		String value = inputBox == null ? "" : inputBox.getValue();
		if (mode == InputMode.SNBT) {
			String snbt = GetFilterRefund.unwrapSnbt(value);
			if (snbt.length() > 48) {
				return "SNBT(" + snbt.length() + "): " + snbt.substring(0, 48) + "...";
			}
			return snbt.isEmpty() ? "SNBT: (空)" : "SNBT: " + snbt;
		}
		return GetFilterRefund.buildItemArgument(selected.id, value);
	}

	private void onConfirm() {
		String value = inputBox == null ? "" : inputBox.getValue();
		GetFilterRefund.Outcome outcome = mode == InputMode.SNBT ? GetFilterRefund.trySendFromSnbt(value)
			: GetFilterRefund.trySendParsed(GetFilterRefund.buildItemArgument(selected.id, value));
		if (minecraft != null && minecraft.player != null) {
			minecraft.player.displayClientMessage(outcome.message(), false);
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
		int top = (this.height - panelH()) / 2;
		graphics.fill(left, top, left + PANEL_W, top + panelH(), 0xB0181C22);
		graphics.renderOutline(left, top, PANEL_W, panelH(), 0x66FFFFFF);

		graphics.drawString(this.font, this.title, left + 12, top + 8, 0xFFFFFFFF, false);
		graphics.drawString(this.font, Component.literal(previewText()), left + 12, top + 20, 0xFF9FD6FF, false);
		graphics.drawString(this.font, Component.literal("输入方式"), left + 12, top + 34, 0xFFB8C0C8, false);

		if (mode == InputMode.COMPONENTS) {
			graphics.drawString(this.font, Component.literal("过滤器类型"), left + 12, top + 72, 0xFFB8C0C8, false);
			graphics.drawString(this.font, Component.literal("括号内内容"), left + 12, top + 148, 0xFFB8C0C8, false);
		} else {
			graphics.drawString(this.font, Component.literal("完整物品 SNBT"), left + 12, top + 72, 0xFFB8C0C8, false);
		}

		super.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
