/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui.toast;

import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.research.networking.ResearchToastTrigger;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ResearchToast implements Toast {

    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/advancement");
    public static final int DISPLAY_TIME = 5000;

    private final Component description;
    private final Component title;
    @Nullable
    private final BookIcon icon;
    private final ResearchToastTrigger.ToastTriggerType triggerType;
    private final Identifier elementId;
    private final int currentValue;

    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;
    @Nullable
    private ItemStack cachedItemStack;

    public ResearchToast(
            Component description,
            Component title,
            @Nullable BookIcon icon,
            ResearchToastTrigger.ToastTriggerType triggerType,
            Identifier elementId,
            int currentValue
    ) {
        this.description = description;
        this.title = title;
        this.icon = icon;
        this.triggerType = triggerType;
        this.elementId = elementId;
        this.currentValue = currentValue;
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs) {
        this.wantedVisibility = fullyVisibleForMs >= DISPLAY_TIME * manager.getNotificationDisplayTimeMultiplier()
                ? Toast.Visibility.HIDE
                : Toast.Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());

        List<FormattedCharSequence> titleLines = font.split(this.title, 125);

        if (titleLines.size() == 1) {
            graphics.text(font, this.description, 30, 7, -256, false);
            graphics.text(font, titleLines.get(0), 30, 18, -1, false);
        } else {
            int y = this.height() / 2 - titleLines.size() * 9 / 2;
            for (FormattedCharSequence line : titleLines) {
                graphics.text(font, line, 30, y, -1, false);
                y += 9;
            }
        }

        if (this.icon != null) {
            renderIcon(graphics, font, 8, 8);
        }
    }

    private void renderIcon(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        if (this.icon.texture() != null) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    this.icon.texture(),
                    x, y,
                    0, 0,
                    16, 16,
                    this.icon.width(), this.icon.height(),
                    this.icon.width(), this.icon.height()
            );
        } else if (this.icon.itemStackTemplate() != null) {
            if (this.cachedItemStack == null) {
                this.cachedItemStack = this.icon.itemStackTemplate().create();
            }
            graphics.fakeItem(this.cachedItemStack, x, y);
        }
    }

    @Override
    public Object getToken() {
        // For value toasts, include the current value so each increment shows a new toast
        if (this.triggerType == ResearchToastTrigger.ToastTriggerType.VALUE_INCREMENTED) {
            return new ValueToastToken(this.triggerType, this.elementId, this.currentValue);
        }
        return new SimpleToastToken(this.triggerType, this.elementId);
    }

    private record SimpleToastToken(ResearchToastTrigger.ToastTriggerType type, Identifier elementId) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            SimpleToastToken that = (SimpleToastToken) obj;
            return this.type == that.type && this.elementId.equals(that.elementId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, elementId);
        }
    }

    private record ValueToastToken(ResearchToastTrigger.ToastTriggerType type, Identifier elementId, int value) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ValueToastToken that = (ValueToastToken) obj;
            return this.type == that.type && this.value == that.value && this.elementId.equals(that.elementId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, elementId, value);
        }
    }
}
