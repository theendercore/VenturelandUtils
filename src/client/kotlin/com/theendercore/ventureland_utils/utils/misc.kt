package com.theendercore.ventureland_utils.utils

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.theendercore.ventureland_utils.VenturelandUtilsClient.config
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

fun <S> CommandNode<S>.childOf(node: CommandNode<S>): CommandNode<S> {
    node.addChild(this)
    return this
}

fun <S, Q : ArgumentBuilder<S, Q>> ArgumentBuilder<S, Q>.buildChildOf(node: CommandNode<S>): CommandNode<S> {
    return this.build().childOf(node)
}

fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment || config.debugInfo

fun getCosmicWard(stack: ItemStack): Int? {
    if (stack.isEmpty) return null
    val lore = stack.get(DataComponentTypes.LORE) ?: return null

    var qualityLine: Text = Text.literal("Helo!")
    for (line in lore.lines().reversed()) {
        if (line.siblings.isEmpty()) continue
        if (line.siblings.firstOrNull()?.string == "Quality: ") {
            qualityLine = line
            break
        }
    }
    if (qualityLine.siblings.isEmpty()) return null

    val rawText = qualityLine.siblings.reversed().first().string.trim()
    if (!rawText.startsWith("⚡")) return null

    return rawText.substring(1).toInt()
}