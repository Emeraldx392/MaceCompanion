package moe.pxe.macecompanion.config

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.controllers.EnumWithConfigControllerBuilder
import moe.pxe.macecompanion.config.controllers.FormattedStringControllerBuilder
import moe.pxe.macecompanion.enums.BetConditions
import moe.pxe.macecompanion.enums.HudElements
import moe.pxe.macecompanion.enums.HudLocation
import moe.pxe.macecompanion.stateManagers.PlotManager
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import java.awt.Color

object ConfigMenu {
    fun generateScreen(parent: Screen?): Screen? {
        return YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.config.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.category.automsg"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.automsg.group.autogg"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.autogg.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.category.automsg.group.autogg.option.useAutoGG"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.autogg.option.useAutoGG.description")))
                        .binding(
                            Config.useAutoGG.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Component.translatable("mrc.config.category.automsg.group.autogg.option.ggDelayTicks"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.autogg.option.ggDelayTicks.description")))
                        .binding(
                            Config.ggDelayTicks.asBinding()
                        )
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(0, 100)
                                .step(1)
                                .formatValue { i -> Component.nullToEmpty("${String.format("%.2f", i.toFloat()/20f)} seconds") }
                        }
                        .build())
                    .build())
                .group(ListOption.createBuilder<String>()
                    .name(Component.translatable("mrc.config.category.automsg.group.autoGGStrings"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.autoGGStrings.description")))
                    .binding(
                        Config.autoGGStrings.asBinding()
                    )
                    .controller { FormattedStringControllerBuilder.create(it)
                        .valueFormatter { str ->
                            val text = Component.empty()
                            val matches = """(gg)|(good game)""".toRegex(RegexOption.IGNORE_CASE).findAll(str)
                            var lastIdx = 0
                            matches.forEach { match ->
                                text.append(str.substring(lastIdx, match.range.first))
                                    .append(Component.literal(match.value).setStyle(Style.EMPTY.withColor(0xA0F9FF)))
                                lastIdx = match.range.last + 1
                            }
                            return@valueFormatter text.append(str.substring(matches.lastOrNull()?.let { it.range.last + 1 } ?: 0))
                        }
                    }
                    .initial("")
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.automsg.group.autogl"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.autogl.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.category.automsg.group.autogl.option.useAutoGL"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.autogl.option.useAutoGL.description")))
                        .binding(
                            Config.useAutoGL.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Component.translatable("mrc.config.category.automsg.group.autogl.option.glDelayTicks"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.autogl.option.glDelayTicks.description")))
                        .binding(
                            Config.glDelayTicks.asBinding()
                        )
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(0, 100)
                                .step(1)
                                .formatValue { i -> Component.nullToEmpty("${String.format("%.2f", i.toFloat()/20f)} seconds") }
                        }
                        .build())
                    .build())
                .group(ListOption.createBuilder<String>()
                    .name(Component.translatable("mrc.config.category.automsg.group.autoGLStrings"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.autoGLStrings.description")))
                    .binding(
                        Config.autoGLStrings.asBinding()
                    )
                    .controller { FormattedStringControllerBuilder.create(it)
                        .valueFormatter { str ->
                            val text = Component.empty()
                            val matches = """(gl)|(hf)|(good luck)|(have fun)""".toRegex(RegexOption.IGNORE_CASE).findAll(str)
                            var lastIdx = 0
                            matches.forEach { match ->
                                text.append(str.substring(lastIdx, match.range.first))
                                    .append(Component.literal(match.value).setStyle(Style.EMPTY.withColor(0xFFCE2D)))
                                lastIdx = match.range.last + 1
                            }
                            text.append(str.substring(matches.lastOrNull()?.let { it.range.last + 1 } ?: 0))
                        }
                    }
                    .initial("")
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.automsg.group.hidemessages"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.hidemessages.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.category.automsg.group.hidemessages.option.hideGGMessages"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.hidemessages.option.hideGGMessages.description")))
                        .binding(
                            Config.hideGGMessages.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.category.automsg.group.hidemessages.option.hideGLMessages"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.automsg.group.hidemessages.option.hideGLMessages.description")))
                        .binding(
                            Config.hideGLMessages.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.category.autobet"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.autobet.group.autobet"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.autobet.group.autobet.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.category.autobet.group.autobet.option.useAutoBet"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.autobet.group.autobet.option.useAutoBet.description")))
                        .binding(
                            Config.useAutoBet.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Component.translatable("mrc.config.category.autobet.group.autobet.option.autoBetDelayTicks"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.autobet.group.autobet.option.autoBetDelayTicks.description")))
                        .binding(
                            Config.autoBetDelayTicks.asBinding()
                        )
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(0, 300)
                                .step(1)
                                .formatValue { i -> Component.nullToEmpty("${String.format("%.2f", i.toFloat()/20f)} seconds") }
                        }
                        .build())
                    .build())
                .group(ListOption.createBuilder<BetConditions>()
                    .name(Component.translatable("mrc.config.category.autobet.group.autoBetConditions"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.autobet.group.autoBetConditions.description")))
                    .binding(Config.autoBetConditions.asBinding())
                    .controller { EnumControllerBuilder.create(it).enumClass(BetConditions::class.java) }
                    .initial(BetConditions.IS_RED)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.category.roundhud"))
                .option(Option.createBuilder<Boolean>()
                    .name(Component.translatable("mrc.config.category.roundhud.option.displayHud"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.option.displayHud.description")))
                    .binding(Config.displayHud.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.roundhud.group.hudtransforms"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.description")))
                    .option(Option.createBuilder<HudLocation>()
                        .name(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.leftHudLocation"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.leftHudLocation.description")))
                        .binding(Config.leftHudLocation.asBinding())
                        .controller { EnumControllerBuilder.create(it).enumClass(HudLocation::class.java) }
                        .build())
                    .option(Option.createBuilder<HudLocation>()
                        .name(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.rightHudLocation"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.rightHudLocation.description")))
                        .binding(Config.rightHudLocation.asBinding())
                        .controller { EnumControllerBuilder.create(it).enumClass(HudLocation::class.java) }
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudXMargin"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudXMargin.description")))
                        .binding(Config.hudXMargin.asBinding())
                        .controller {
                            IntegerFieldControllerBuilder.create(it).formatValue { Component.nullToEmpty("$it pixels") }
                        }
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudYMargin"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudYMargin.description")))
                        .binding(Config.hudYMargin.asBinding())
                        .controller {
                            IntegerFieldControllerBuilder.create(it).formatValue { Component.nullToEmpty("$it pixels") }
                        }
                        .build())
                    .option(Option.createBuilder<Float>()
                        .name(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudScale"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudScale.description")))
                        .binding(Config.hudScale.asBinding())
                        .controller { FloatSliderControllerBuilder.create(it)
                            .formatValue { Component.nullToEmpty("${it * 100}%") }
                            .range(0.05f, 3f)
                            .step(0.05f)
                        }
                        .build())
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.displayNewRoundInActionbar"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudtransforms.option.displayNewRoundInActionbar.description")))
                        .binding(Config.displayNewRoundInActionbar.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.roundhud.group.hudstyle"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudstyle.description")))
                    .also {
                        val useAccentColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.useAccentColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.useAccentColors.description")))
                            .binding(Config.useAccentColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val accentColorNumber = Option.createBuilder<Int>()
                                .name(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.accentColorNumber"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.accentColorNumber.description")))
                                .binding(Config.accentColorNumber.asBinding())
                                .controller {
                                    IntegerSliderControllerBuilder.create(it)
                                        .range(1, 3)
                                        .step(1)
                                }
                                .build()
                        accentColorNumber.setAvailable(useAccentColors.pendingValue())
                        useAccentColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) accentColorNumber.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) accentColorNumber.setAvailable(option.pendingValue())
                        }
                        val mainAccentColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.mainAccentColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.mainAccentColor.description")))
                            .binding(Config.mainAccentColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        mainAccentColor.setAvailable(useAccentColors.pendingValue())
                        useAccentColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) mainAccentColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) mainAccentColor.setAvailable(option.pendingValue())
                        }
                        val secondAccentColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.secondAccentColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.secondAccentColor.description")))
                            .binding(Config.secondAccentColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        secondAccentColor.setAvailable(useAccentColors.pendingValue() && accentColorNumber.pendingValue() > 1)
                        useAccentColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) secondAccentColor.setAvailable(option.pendingValue() && accentColorNumber.pendingValue() > 1)
                            if (event == OptionEventListener.Event.STATE_CHANGE) secondAccentColor.setAvailable(option.pendingValue( )&& accentColorNumber.pendingValue() > 1)
                        }
                        accentColorNumber.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) secondAccentColor.setAvailable(option.pendingValue() > 1 && useAccentColors.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) secondAccentColor.setAvailable(option.pendingValue() > 1 && useAccentColors.pendingValue())
                        }
                        val thirdAccentColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.thirdAccentColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.hudstyle.option.thirdAccentColor.description")))
                            .binding(Config.thirdAccentColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        thirdAccentColor.setAvailable(useAccentColors.pendingValue() && accentColorNumber.pendingValue() > 2)
                        useAccentColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) thirdAccentColor.setAvailable(option.pendingValue() && accentColorNumber.pendingValue() > 2)
                            if (event == OptionEventListener.Event.STATE_CHANGE) thirdAccentColor.setAvailable(option.pendingValue( )&& accentColorNumber.pendingValue() > 2)
                        }
                        accentColorNumber.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) thirdAccentColor.setAvailable(option.pendingValue() > 2 && useAccentColors.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) thirdAccentColor.setAvailable(option.pendingValue() > 2 && useAccentColors.pendingValue())
                        }
                        it.option(useAccentColors)
                        it.option(accentColorNumber)
                        it.option(mainAccentColor)
                        it.option(secondAccentColor)
                        it.option(thirdAccentColor)
                    }
                    .build())
                .group(ListOption.createBuilder<HudElements>()
                    .name(Component.translatable("mrc.config.category.roundhud.group.leftHudElements"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.leftHudElements.description")))
                    .binding(Config.leftHudElements.asBinding())
                    .controller { EnumWithConfigControllerBuilder.create(it).enumClass(HudElements::class.java) }
                    .initial(HudElements.ROUND_NUMBER)
                    .build())
                .group(ListOption.createBuilder<HudElements>()
                    .name(Component.translatable("mrc.config.category.roundhud.group.rightHudElements"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.roundhud.group.rightHudElements.description")))
                    .binding(Config.rightHudElements.asBinding())
                    .controller { EnumWithConfigControllerBuilder.create(it).enumClass(HudElements::class.java) }
                    .initial(HudElements.ROUND_NUMBER)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.category.toasts"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.toasts.group.game"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.game.description")))
                        .option(Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.category.toasts.group.game.option.showNewEventToasts"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.game.option.showNewEventToasts.description")))
                            .binding(Config.showNewEventToasts.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                        .option(Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.category.toasts.group.game.option.showConsumableToasts"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.game.option.showConsumableToasts.description")))
                            .binding(Config.showConsumableToasts.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                        .option(Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.category.toasts.group.game.option.showBountyToasts"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.game.option.showBountyToasts.description")))
                            .binding(Config.showBountyToasts.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.toasts.group.player"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.player.description")))
                        .option(Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.category.toasts.group.player.option.showPlayerToasts"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.player.option.showPlayerToasts.description")))
                            .binding(Config.showPlayerToasts.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.category.toasts.group.player.option.hidePlayerJoinedLeftMessages"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.player.option.hidePlayerJoinedLeftMessages.description")))
                        .binding(Config.hidePlayerJoinedLeftMessages.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .group(ListOption.createBuilder<String>()
                    .name(Component.translatable("mrc.config.category.toasts.group.playerStrings"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.playerStrings.description")))
                    .binding(Config.playerStrings.asBinding())
                    .controller { FormattedStringControllerBuilder.create(it) }
                    .initial("")
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.category.miscellaneous"))
                .group(ListOption.createBuilder<String>()
                    .name(Component.translatable("mrc.config.category.miscellaneous.group.plotIds"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.miscellaneous.group.plotIds.description")))
                    .binding(Config.plotIds.asBinding())
                    .controller { FormattedStringControllerBuilder.create(it)
                        .valueFormatter { str -> Component.literal(str).also { text ->
                            str.toIntOrNull()?.let { text.withStyle(ChatFormatting.AQUA) } ?: text.withStyle(ChatFormatting.YELLOW)
                        } }
                    }
                    .initial("")
                    .flag({
                        PlotManager.fillPlotIds(Config.plotIds.value.toSet())
                    })
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.category.miscellaneous.group.daytime"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.category.toasts.group.daytime.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.category.miscellaneous.group.daytime.option.syncTime"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.category.miscellaneous.group.daytime.option.syncTime.description")))
                        .binding(Config.syncTime.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build())
            .save(Config::saveToFileAndRefreshRendering)
            .build()
            .generateScreen(parent)
    }
}