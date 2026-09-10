package moe.pxe.macecompanion.util

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.minecraft.network.chat.Component
import java.awt.Color

object OptionUtils {
    fun overrideColorsOption(name: String, configBinding: Binding<Boolean>): Option<Boolean> {
        return Option.createBuilder<Boolean>()
            .name(Component.translatable("mrc.config.$name.category.styling.group.colors.option.override_colors"))
            .description(OptionDescription.of(Component.translatable("mrc.config.$name.category.styling.group.colors.option.override_colors.description")))
            .binding(configBinding)
            .controller(TickBoxControllerBuilder::create)
            .build()
    }

    fun overrideColorOption(name: String, configBinding: Binding<Color>, overrideType: String): Option<Color> {
        return Option.createBuilder<Color>()
            .name(Component.translatable("mrc.config.$name.category.styling.group.colors.option.$overrideType"))
            .description(OptionDescription.of(Component.translatable("mrc.config.$name.category.styling.group.colors.option.$overrideType.description")))
            .binding(configBinding)
            .controller(ColorControllerBuilder::create)
            .build()
    }

    fun addColorOptionDependency(dependentOption: Option<Color>, dependency: Option<Boolean>) {
        dependentOption.setAvailable(dependency.pendingValue())
        dependency.addEventListener { option, event ->
            if (event == OptionEventListener.Event.INITIAL) dependentOption.setAvailable(option.pendingValue())
            if (event == OptionEventListener.Event.STATE_CHANGE) dependentOption.setAvailable(option.pendingValue())
        }
    }

    fun hideWhenEliminatedOption(name: String, configBinding: Binding<Boolean>): Option<Boolean> {
        return Option.createBuilder<Boolean>()
            .name(Component.translatable("mrc.config.$name.category.misc.option.hide_when_eliminated"))
            .description(OptionDescription.of(Component.translatable("mrc.config.$name.category.misc.option.hide_when_eliminated.description")))
            .binding(configBinding)
            .controller(TickBoxControllerBuilder::create)
            .build()
    }
    fun sliderOption(name: String, configBinding: Binding<Int>, minValue: Int, maxValue: Int, step: Int, sliderType: String): Option<Int> {
        return Option.createBuilder<Int>()
            .name(Component.translatable("mrc.config.$name.category.misc.option.$sliderType"))
            .description(OptionDescription.of(Component.translatable("mrc.config.$name.category.misc.option.$sliderType.description")))
            .binding(configBinding)
            .controller {
                IntegerSliderControllerBuilder.create(it)
                    .range(minValue, maxValue)
                    .step(step)
            }
            .build()
    }
    fun iconBooleanOption(name: String, configBinding: Binding<Boolean>, type: String): Option<Boolean> {
        return Option.createBuilder<Boolean>()
            .name(Component.translatable("mrc.config.$name.category.styling.group.icons.option.$type"))
            .description(OptionDescription.of(Component.translatable("mrc.config.$name.category.styling.group.icons.option.$type.description")))
            .binding(configBinding)
            .controller(TickBoxControllerBuilder::create)
            .build()
    }
    fun genericBooleanOption(translationKey: String, configBinding: Binding<Boolean>): Option<Boolean> {
        return Option.createBuilder<Boolean>()
            .name(Component.translatable(translationKey))
            .description(OptionDescription.of(Component.translatable("$translationKey.description")))
            .binding(configBinding)
            .controller(TickBoxControllerBuilder::create)
            .build()
    }
}