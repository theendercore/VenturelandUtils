package com.theendercore.ventureland_utils

import com.mojang.brigadier.CommandDispatcher
import com.theendercore.ventureland_utils.config.TemplateConfig
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
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
    var config = ConfigApi.registerAndLoadConfig(::TemplateConfig, RegisterType.CLIENT)
    fun init() {
        log.info("Hello from Client")
        ClientCommandRegistrationCallback.EVENT.register(::command)
        ClientReceiveMessageEvents.GAME.register (::processMessage)
    }
    fun processMessage(text: Text, overlay: Boolean){
        if (overlay) return
        println(text.toString())
    }

    fun command(dispatcher: CommandDispatcher<FabricClientCommandSource>, registry: CommandBuildContext) {
       val root = literal("dump_item").executes {
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
        }.build()
        dispatcher.root.addChild(root)
    }

    fun id(path: String): Identifier = Identifier.of(MODID, path)
}
