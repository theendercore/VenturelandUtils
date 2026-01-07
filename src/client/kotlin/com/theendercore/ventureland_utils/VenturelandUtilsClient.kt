package com.theendercore.ventureland_utils

import com.theendercore.ventureland_utils.config.VenturelandUtilsConfig
import com.theendercore.ventureland_utils.utils.VCommands
import com.theendercore.ventureland_utils.utils.isDev
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text
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
        VCommands.init()
        ClientReceiveMessageEvents.GAME.register(::processMessage)
        VUHudRenderer.init()
    }

    const val SCROLL_OF_LIFE = "Scroll of life saves you from death"
    const val RES_SCROLL = "revived."
    val REVENGE = Regex("(Revenge is now on cooldown against mobs for )(\\d+)(s)")

    fun processMessage(text: Text, overlay: Boolean) {
        if (overlay) return
        val player = MinecraftClient.getInstance().player ?: return

        val textClean = text.string.trim()
        if (textClean.isEmpty()) return

        if (isDev()) log.info("Message log: [{}]", textClean)

        if (SCROLL_OF_LIFE == textClean) {
            player.debug("Received Scroll of life message!")
            VUHudRenderer.scrollOfLifeCooldown = config.scrollOfLifeCooldown.get() * 20
            return
        }
        if (REVENGE.matches(textClean)) {
            val cooldown = REVENGE.findAll(textClean).first().groups[2]?.value?.toInt() ?: 10
            player.debug("Received Revenge message! $cooldown")
            VUHudRenderer.revengeCooldown = cooldown * 20
        }
        val resurrectionProc = textClean.split(" ")
        if (resurrectionProc.size == 2 && resurrectionProc[1] == RES_SCROLL) {
            player.debug("Received Revive message!")
            VUHudRenderer.resurrectionCooldown = config.resurrectionCooldown.get() * 20
        }
    }

    fun ClientPlayerEntity.debug(message: String) {
        if (isDev()) sendMessage(Text.literal(message), false)
    }

    fun id(path: String): Identifier = Identifier.of(MODID, path)
}
