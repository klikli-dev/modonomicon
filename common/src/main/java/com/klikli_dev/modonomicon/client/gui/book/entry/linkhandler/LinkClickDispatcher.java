// SPDX-FileCopyrightText: 2026 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.modonomicon.client.gui.book.entry.linkhandler;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Dispatches clicks on chat component styles to Modonomicon's {@link LinkHandler}s.
 * <p>
 * Styles that no handler claims fall through to vanilla handling (open url with confirm screen, open file, suggest
 * command, copy to clipboard), matching chat behaviour. Raw run-command events are deliberately never passed to
 * vanilla: our command protocol links are always consumed by {@link CommandLinkHandler} (success or failure), so
 * anything else carrying run-command did not come from book markdown and must not execute as the player.
 */
public final class LinkClickDispatcher {

    private LinkClickDispatcher() {
    }

    public static List<LinkHandler> defaultHandlers(LinkClickContext context) {
        return List.of(
                new BookLinkHandler(context),
                new PatchouliLinkHandler(context),
                new ItemLinkHandler(context),
                new CommandLinkHandler(context)
        );
    }

    /**
     * @param vanillaClickHandler handles click events none of our handlers claim, e.g.
     *                            {@code event -> Screen.defaultHandleGameClickEvent(event, this.minecraft, this)}.
     * @return true if the click was handled and must not propagate further.
     */
    public static boolean dispatch(LinkClickContext context, List<LinkHandler> handlers, @Nullable Style style, Consumer<ClickEvent> vanillaClickHandler) {
        if (style == null)
            return false;

        for (LinkHandler handler : handlers) {
            var result = handler.handleClick(style);

            //before the command pattern was implemented we returned false for failures
            //however, failure should also be treated as "handled" to avoid vanilla code doing fun stuff.
            if (result != LinkHandler.ClickResult.UNHANDLED)
                return true;
        }

        var clickEvent = style.getClickEvent();
        if (clickEvent == null)
            return false;

        if (clickEvent instanceof ClickEvent.RunCommand) {
            //never run raw commands as the player; our own command links never reach this point.
            return false;
        }

        vanillaClickHandler.accept(clickEvent);
        return true;
    }
}
