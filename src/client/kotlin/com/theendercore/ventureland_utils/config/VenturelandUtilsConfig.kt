package com.theendercore.ventureland_utils.config

import com.theendercore.ventureland_utils.VenturelandUtilsClient.MODID
import com.theendercore.ventureland_utils.VenturelandUtilsClient.id
import me.fzzyhmstrs.fzzy_config.annotations.Comment
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedRegistryType
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import java.awt.Color

@Suppress("unused")
class VenturelandUtilsConfig : Config(id(MODID)) {
    var cosmicWardTextColor = ValidatedColor(Color(170))

    @Comment("Shows hitboxes for invisible mobs")
    var invisibleEntityHitboxes = true

    @Comment("List of entity types that will not have there hitboxes when invisible")
    var hitboxBlacklist =
        ValidatedList(listOf(EntityType.ARMOR_STAND), ValidatedRegistryType.of(Registries.ENTITY_TYPE))

    @Comment("Hides the hitboxes for items that are used to custom display texts like HP")
    var hideDisplayItemHitboxes = true

    var cooldownGroup = ConfigGroup("cooldowns")

    @Comment("Cooldown in seconds")
    var scrollOfLifeCooldown = ValidatedInt(5 * 60, 1024, 1)
    var scrollOfLifeColor = ValidatedColor(255, 85, 255)

    var revengeColor = ValidatedColor(170, 0, 0)

    var resurrectionCooldown = ValidatedInt(100, 1024, 1)

    @ConfigGroup.Pop
    @Comment("Cooldown in seconds")
    var resurrectionColor = ValidatedColor(255, 85, 85)

    @Comment("Used to print debug info. Don't turn it on unless asked to do so")
    var debugInfo = false
}