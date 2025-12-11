package com.theendercore.ventureland_utils

import com.theendercore.ventureland_utils.VenturelandUtilsClient.config
import com.theendercore.ventureland_utils.utils.getCosmicWard
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.item.ItemStack
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
        HudRenderCallback.EVENT.register { gui, tick ->
            renderCooldowns(gui)
            renderCosmicWard(gui)
        }
    }

    fun renderCosmicWard(gui: GuiGraphics) {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        val infoMap = mutableMapOf<ItemStack, Text>()

        val leggings = player.inventory.armor[1]
        if (!leggings.isEmpty) {
            val ward = getCosmicWard(leggings)
            if (ward != null) {
                infoMap[leggings] = Text.literal("⚡$ward").setColor(config.cosmicWardTextColor.toInt())
            }
        }
        val chestplate = player.inventory.armor[2]
        if (!chestplate.isEmpty) {
            val ward = getCosmicWard(chestplate)
            if (ward != null) {
                infoMap[chestplate] = Text.literal("⚡$ward").setColor(config.cosmicWardTextColor.toInt())
            }
        }
        val slotNumberUno = player.inventory.main[0]
        if (!slotNumberUno.isEmpty) {
            val ward = getCosmicWard(slotNumberUno)
            if (ward != null) {
                infoMap[slotNumberUno] = Text.literal("⚡$ward").setColor(config.cosmicWardTextColor.toInt())
            }
        }

        val font = client.textRenderer
        for ((idx, pair) in infoMap.toList().withIndex()) {
            val y = gui.scaledWindowHeight - 20
            val x = (gui.scaledWindowWidth / 2) - 116 - (idx * 28)

            gui.matrices.push()
            gui.matrices.translate(0f, 0f, -1000f)
            gui.drawItem(pair.first, x, y)
            gui.matrices.pop()

            gui.drawCenteredShadowedText(font, pair.second, x + 7, (y + font.fontHeight), 0x0)
        }

    }

    fun renderCooldowns(gui: GuiGraphics) {
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
                gui.scaledWindowHeight - (font.fontHeight / 2) - ((idx + 1) * (font.fontHeight * 1.25).toInt()),
                0x0
            )
        }
    }

    fun Int.formatTime(): String = String.format("%d:%02d", this / 60, this % 60)
}
