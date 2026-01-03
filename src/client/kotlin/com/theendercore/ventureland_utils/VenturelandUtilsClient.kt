package com.theendercore.ventureland_utils

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.theendercore.ventureland_utils.config.VenturelandUtilsConfig
import com.theendercore.ventureland_utils.utils.buildChildOf
import com.theendercore.ventureland_utils.utils.getCosmicWard
import com.theendercore.ventureland_utils.utils.isDev
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandBuildContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object VenturelandUtilsClient {
    const val MODID = "ventureland_utils"

    @JvmField
    val log: Logger = LoggerFactory.getLogger(VenturelandUtilsClient::class.simpleName)

    @JvmField
    var config = ConfigApi.registerAndLoadConfig(::VenturelandUtilsConfig, RegisterType.CLIENT)
    fun init() {
        log.info("Hello from Client")
        ClientCommandRegistrationCallback.EVENT.register(::command)
        ClientReceiveMessageEvents.GAME.register(::processMessage)
        VUHudRenderer.init()
    }

    const val SCROLL_OF_LIFE = "Scroll of life saves you from death"
    val REVENGE = Regex("(Revenge is now on cooldown against mobs for )(\\d+)(s)")

    fun processMessage(text: Text, overlay: Boolean) {
        if (overlay) return
        val player = MinecraftClient.getInstance().player ?: return

        val textClean = text.string.trim()
        if (textClean.isEmpty()) return

        if (config.debugInfo) {
            log.info("Message log: [{}]", textClean)
        }

        if (SCROLL_OF_LIFE == textClean) {
            if (isDev()) player.sendMessage(Text.literal("Received Scroll of life message!"), false)
            VUHudRenderer.scrollOfLifeCooldown = 5 * 60 * 20
            return
        }
        if (REVENGE.matches(textClean)) {
            val cooldown = REVENGE.findAll(textClean).first().groups[2]?.value?.toInt() ?: 10
            if (isDev()) player.sendMessage(Text.literal("Received Revenge message! $cooldown"), false)
            VUHudRenderer.revengeCooldown = cooldown * 20
        }
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
                VUHudRenderer.scrollOfLifeCooldown = 80
                VUHudRenderer.revengeCooldown = 80
                1
            }.buildChildOf(test)
            literal("message").executes {
                processMessage(Text.literal(SCROLL_OF_LIFE), false)
                processMessage(Text.literal("Revenge is now on cooldown against mobs for 69s"), false)
                1
            }.buildChildOf(test)
        }
    }

    fun id(path: String): Identifier = Identifier.of(MODID, path)
}
