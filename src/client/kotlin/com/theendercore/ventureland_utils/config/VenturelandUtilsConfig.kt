package com.theendercore.ventureland_utils.config

import com.theendercore.ventureland_utils.VenturelandUtilsClient.MODID
import com.theendercore.ventureland_utils.VenturelandUtilsClient.id
import me.fzzyhmstrs.fzzy_config.annotations.Comment
import me.fzzyhmstrs.fzzy_config.config.Config

@Suppress("unused")
class VenturelandUtilsConfig : Config(id(MODID)) {
    @Comment("Shows hitboxes for invisible mobs.")
    var invisibleEntityHitboxes = true
}