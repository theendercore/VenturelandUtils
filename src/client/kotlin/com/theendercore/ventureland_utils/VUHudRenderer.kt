package com.theendercore.ventureland_utils

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.render.DeltaTracker
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object VUHudRenderer {
    var scrollOfLifeCooldown = 0
    var revengeCooldown = 0

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register {
            if (scrollOfLifeCooldown > 0) scrollOfLifeCooldown--
            if (revengeCooldown > 0) revengeCooldown--
        }
        HudRenderCallback.EVENT.register(::render)
    }

    fun render(gui: GuiGraphics, tick: DeltaTracker) {
        val textList = mutableListOf<Text>()
        if (scrollOfLifeCooldown > 0)
            textList.add(
                Text.literal("Scroll of life cooldown: ${(scrollOfLifeCooldown / 20).formatTime()}")
                    .formatted(Formatting.LIGHT_PURPLE)
            )
        if (revengeCooldown > 0)
            textList.add(
                Text.literal("Revenge cooldown: ${(revengeCooldown / 20).formatTime()}")
                    .formatted(Formatting.DARK_RED)
            )
        if (textList.isEmpty()) return

        val font = MinecraftClient.getInstance().textRenderer
        for ((idx, text) in textList.withIndex()) {
            gui.drawShadowedText(
                font,
                text,
                gui.scaledWindowWidth / 2 + 104,
                gui.scaledWindowHeight - (font.fontHeight/2) - ((idx + 1) * (font.fontHeight * 1.25).toInt()),
                0x0
            )
        }
    }

    fun Int.formatTime(): String = String.format("%d:%02d", this / 60, this % 60)
}
