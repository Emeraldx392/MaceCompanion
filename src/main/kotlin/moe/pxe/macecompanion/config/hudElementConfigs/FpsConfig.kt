package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.fpsNumberColor
import moe.pxe.macecompanion.config.Config.fpsOverrideColors
import moe.pxe.macecompanion.config.Config.fpsTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object FpsConfig {
    const val NAME = "fps"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.fps"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.fps.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.fps.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.fps.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, fpsOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, fpsTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, fpsNumberColor.asBinding(), "number_color")
                            addColorOptionDependency(numberColor, overrideColors)

                            it.option(overrideColors)
                            it.option(textColor)
                            it.option(numberColor)
                        }
                        .build())
                .build())
        .save(Config::saveToFileAndRefreshRendering)
        .build()
        .generateScreen(parent)
}