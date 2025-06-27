package com.theendercore.ventureland_utils.utils

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import net.fabricmc.loader.api.FabricLoader

fun <S> CommandNode<S>.childOf(node: CommandNode<S>): CommandNode<S> {
    node.addChild(this)
    return this
}

fun <S, Q : ArgumentBuilder<S, Q>> ArgumentBuilder<S, Q>.buildChildOf(node: CommandNode<S>): CommandNode<S> {
    return this.build().childOf(node)
}

fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment