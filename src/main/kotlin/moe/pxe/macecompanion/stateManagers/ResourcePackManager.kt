package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.MaceCompanion.Companion.MOD_ID
import moe.pxe.macecompanion.resourceLoaders.AchievementResourceLoader
import moe.pxe.macecompanion.resourceLoaders.AchievementResourceLoader.ACHIEVEMENT_DIRECTORY
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackType

object ResourcePackManager {
    fun registerResourcePackListeners() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(
                Identifier.fromNamespaceAndPath(MOD_ID, ACHIEVEMENT_DIRECTORY),
                AchievementResourceLoader
            )
    }

}

