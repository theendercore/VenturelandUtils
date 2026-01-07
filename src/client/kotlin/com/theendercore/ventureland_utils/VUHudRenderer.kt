package com.theendercore.ventureland_utils

import com.theendercore.ventureland_utils.VenturelandUtilsClient.config
import com.theendercore.ventureland_utils.utils.keyText
import com.theendercore.ventureland_utils.utils.wardExtraction
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

object VUHudRenderer {
    var scrollOfLifeCooldown = 0
    var revengeCooldown = 0
    var resurrectionCooldown = 0

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register {
            if (scrollOfLifeCooldown > 0) scrollOfLifeCooldown--
            if (revengeCooldown > 0) revengeCooldown--
            if (resurrectionCooldown > 0) resurrectionCooldown--
        }
        HudRenderCallback.EVENT.register { gui, _ ->
            renderCooldowns(gui)
            renderCosmicWard(gui)
        }
    }

    fun renderCosmicWard(gui: GuiGraphics) {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        val infoMap = mutableMapOf<ItemStack, Text>()

        wardExtraction(player.inventory.armor[1], infoMap) // Leggings
        wardExtraction(player.inventory.armor[2], infoMap) // Chestplate
        wardExtraction(player.inventory.main[0], infoMap) // Weapon

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
        if (scrollOfLifeCooldown > 0) textList.add(
            keyText("scroll_of_life", scrollOfLifeCooldown.formatTime()).setColor(config.scrollOfLifeColor.toInt())
        )
        if (revengeCooldown > 0) textList.add(
            keyText("revenge", revengeCooldown.formatTime()).setColor(config.revengeColor.toInt())
        )
        if (resurrectionCooldown > 0) textList.add(
            keyText("resurrection", resurrectionCooldown.formatTime()).setColor(config.resurrectionColor.toInt())
        )

        if (textList.isEmpty()) return

        val font = MinecraftClient.getInstance().textRenderer
        for ((idx, text) in textList.withIndex()) {
            gui.drawShadowedText(
                font, text,
                gui.scaledWindowWidth / 2 + 104,
                gui.scaledWindowHeight - (font.fontHeight / 2) - ((idx + 1) * (font.fontHeight * 1.25).toInt()),
                0x0
            )
        }
    }

    fun Int.formatTime(): String = String.format("%d:%02d", (this / 20) / 60, (this / 20) % 60)
}
