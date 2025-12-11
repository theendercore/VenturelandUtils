package com.theendercore.ventureland_utils.config

import com.theendercore.ventureland_utils.VenturelandUtilsClient.MODID
import com.theendercore.ventureland_utils.VenturelandUtilsClient.id
import me.fzzyhmstrs.fzzy_config.annotations.Comment
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedRegistryType
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries

@Suppress("unused")
class VenturelandUtilsConfig : Config(id(MODID)) {
    @Comment("Shows hitboxes for invisible mobs")
    var invisibleEntityHitboxes = true
    @Comment("List of entity types that will not have there hitboxes when invisible")
    var hitboxBlacklist =
        ValidatedList(listOf(EntityType.ARMOR_STAND), ValidatedRegistryType.of(Registries.ENTITY_TYPE))
}