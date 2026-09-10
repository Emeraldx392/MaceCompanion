package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.areAchievementsShown
import moe.pxe.macecompanion.resourceLoaders.AchievementResourceLoader
import moe.pxe.macecompanion.util.OptionUtils.genericBooleanOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object AchievementsConfig {

    fun generateConfig(parent: Screen): Screen? =
        YetAnotherConfigLib.createBuilder().title(Component.translatable("mrc.hudelement.achievements")).category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.achievements.category.selected_achievements"))
                    .also { category ->
                        Config.saveToFile()
                        areAchievementsShown.value.forEach { (achievementId, _) ->
                            if(AchievementResourceLoader.getAllIdStrings().contains(achievementId)) category.option(
                                genericBooleanOption(
                                    "mrc.config.achievements.category.selected_achievements.option.$achievementId",
                                    Binding.generic(
                                        false,
                                        { areAchievementsShown.value[achievementId] ?: false },
                                        { newValue -> areAchievementsShown.value = areAchievementsShown.value
                                            .toMutableMap()
                                            .apply { this[achievementId] = newValue} }
                                    )
                                )
                            )
                        }
                    }
                    .build())
            .save(Config::saveToFileAndRefreshRendering)
            .build()
            .generateScreen(parent)
}