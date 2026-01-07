package com.theendercore.ventureland_utils.utils

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.theendercore.ventureland_utils.VUHudRenderer
import com.theendercore.ventureland_utils.VenturelandUtilsClient
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandBuildContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object VCommands {
    fun init() {
        ClientCommandRegistrationCallback.EVENT.register(::command)
    }

    fun command(dispatcher: CommandDispatcher<FabricClientCommandSource>, registry: CommandBuildContext) {
        val root = literal("vutils").buildChildOf(dispatcher.root)
        literal("evaluate_item").executes {
            val player = it.source.player ?: return@executes 0

            val stack = player.mainHandStack
            if (stack.isEmpty) {
                player.sendMessage(Text.literal("No Item"), false)
                return@executes 0
            }

            val ward = getCosmicWard(stack)
            if (ward == null) {
                player.sendMessage(Text.literal("No Ward"), false)
                return@executes 0
            }
            player.sendMessage(Text.literal("Held Item has $ward Cosmic Ward"), false)
            1
        }.buildChildOf(root)

        literal("clear").executes {
            VUHudRenderer.scrollOfLifeCooldown = 0
            VUHudRenderer.revengeCooldown = 0
            VUHudRenderer.resurrectionCooldown = 0
            1
        }.buildChildOf(root)

        if (isDev()) {
            literal("dump_item").executes {
                val player = it.source.player ?: return@executes 0

                val stack = player.mainHandStack
                if (stack.isEmpty) return@executes 0

                val ops = player.world.registryManager.createSerializationContext(NbtOps.INSTANCE)
                val data = ItemStack.CODEC.encodeStart(ops, stack)
                if (data.isError) {
                    player.sendMessage(
                        Text.literal("Error while trying to get hand data: ${data.error().get().message()}")
                            .formatted(Formatting.RED), false
                    )
                    return@executes 0
                }
                player.sendMessage(Text.literal("Stack data: ${data.getOrThrow()}"), false)
                1
            }.buildChildOf(root)

            val post = literal("post").buildChildOf(root)
            argument("message", StringArgumentType.greedyString()).executes {
                it.source.sendFeedback(Text.literal(StringArgumentType.getString(it, "message")))
                1
            }.buildChildOf(post)

            val test = literal("test").buildChildOf(root)
            literal("cooldown").executes {
                val time = 20 * 20
                VUHudRenderer.scrollOfLifeCooldown = time
                VUHudRenderer.revengeCooldown = time
                VUHudRenderer.resurrectionCooldown = time
                1
            }.buildChildOf(test)
            literal("message").executes {
                VenturelandUtilsClient.processMessage(Text.literal(VenturelandUtilsClient.SCROLL_OF_LIFE), false)
                VenturelandUtilsClient.processMessage(
                    Text.literal("Revenge is now on cooldown against mobs for 69s"),
                    false
                )
                VenturelandUtilsClient.processMessage(Text.literal("TheCrazyPerson revived."), false)
                1
            }.buildChildOf(test)
        }
    }
}