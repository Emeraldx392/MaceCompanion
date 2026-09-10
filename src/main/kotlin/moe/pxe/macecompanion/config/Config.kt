package moe.pxe.macecompanion.config

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.enums.BetConditions
import moe.pxe.macecompanion.enums.HudElements
import moe.pxe.macecompanion.enums.HudLocation
import moe.pxe.macecompanion.enums.Modifiers
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.CommonColors
import java.awt.Color

object Config : JsonFileCodecConfig<Config>(FabricLoader.getInstance().configDir.resolve("mrc.json")) {

    fun saveToFileAndRefreshRendering(){
        rightHudElements.value.forEach { it.refreshRendering() }
        leftHudElements.value.forEach { it.refreshRendering() }
        Modifiers.entries.forEach { it.refreshTranslatable() }
        saveToFile()
    }

    val RGB_COLOR_CODEC = object : PrimitiveCodec<Color> {
        override fun <T : Any> read(ops: DynamicOps<T>, input: T): DataResult<Color> {
            return ops.getStringValue(input).map(Color::decode)
        }

        override fun <T : Any> write(ops: DynamicOps<T>, value: Color): T {
            return ops.createString("#" + Integer.toHexString(value.rgb).substring(2))
        }
    }

    // AutoGG + AutoGL
    val useAutoGG by register<Boolean>(true, BOOL)
    val autoGGStrings by register<List<String>>(listOf("gg", "good game"), STRING.listOf())
    val ggDelayTicks by register<Int>(10, INT)

    val useAutoGL by register<Boolean>(true, BOOL)
    val autoGLStrings by register<List<String>>(listOf("gl", "hf", "glhf", "good luck", "have fun", "good luck have fun"), STRING.listOf())
    val glDelayTicks by register<Int>(60, INT)

    val hideGGMessages by register<Boolean>(false, BOOL)
    val hideGLMessages by register<Boolean>(false, BOOL)

    // AutoBet
    val useAutoBet by register<Boolean>(true, BOOL)
    val autoBetDelayTicks by register<Int>(40, INT)
    val autoBetConditions by register<List<BetConditions>>(listOf(BetConditions.IS_MOST_VOTED, BetConditions.CHOOSE_RANDOMLY), BetConditions.CODEC.listOf())

    // Round Info HUD
    val displayHud by register<Boolean>(true, BOOL)

    val rightHudLocation by register<HudLocation>(HudLocation.TOP, HudLocation.CODEC)
    val leftHudLocation by register<HudLocation>(HudLocation.TOP, HudLocation.CODEC)
    val hudXMargin by register<Int>(10, INT)
    val hudYMargin by register<Int>(10, INT)
    val hudScale by register<Float>(1f, FLOAT)

    val displayNewRoundInActionbar by register<Boolean>(true, BOOL)

    val useAccentColors by register<Boolean>(false, BOOL)
    val accentColorNumber by register<Int>(1, INT)
    val mainAccentColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)
    val secondAccentColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)
    val thirdAccentColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)

    val leftHudElements by register<List<HudElements>>(
        listOf(
            HudElements.ROUND_NUMBER, HudElements.PLAYERS_ALIVE, HudElements.MACE_CHANCE, HudElements.ACCURACY, HudElements.ELIMINATIONS, HudElements.STAR_FRAGMENTS, HudElements.PLAYTIME, HudElements.MODIFIERS
        ), HudElements.CODEC.listOf()
    )
    val rightHudElements by register<List<HudElements>>(listOf(HudElements.PING, HudElements.FPS, HudElements.BOUNTY_BOARD), HudElements.CODEC.listOf())

    // Toasts
    val showNewEventToasts by register<Boolean>(true, BOOL)
    val showConsumableToasts by register<Boolean>(true, BOOL)
    val showBountyToasts by register<Boolean>(true, BOOL)
    val showPlayerToasts by register<Boolean>(true, BOOL)
    val hidePlayerJoinedLeftMessages by register<Boolean>(false, BOOL)
    val playerStrings by register<List<String>>(listOf("flopsuh"), STRING.listOf())

    // Misc. Config
    val plotIds by register<List<String>>(listOf("mace", "statless"), STRING.listOf())

    // NESTED CONFIG ===========================================================

    //Round Number - Styling - Colors
    val roundNumberOverrideColors by register<Boolean>(false, BOOL)
    val roundNumberNumberColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)
    val roundNumberTextColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)

    //Players Alive - Styling - Colors
    val playersAliveOverrideColors by register<Boolean>(false, BOOL)
    val playersAliveNumberColorAlive by register<Color>(Color(0xd5fcf5), RGB_COLOR_CODEC)
    val playersAliveNumberColorTotal by register<Color>(Color(0xd0d0d0), RGB_COLOR_CODEC)
    val playersAliveTextColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)

    //Accuracy - Styling - Colors
    val accuracyOverrideColors by register<Boolean>(false, BOOL)
    val accuracyNumberColor by register<Color>(Color(0x79fc00), RGB_COLOR_CODEC)
    val accuracyIconColor by register<Color>(Color(0x79fc00), RGB_COLOR_CODEC)
    val accuracyTextColor by register<Color>(Color(0x79fc00), RGB_COLOR_CODEC)

    //Eliminations - Misc
    val eliminationsHideWhenEliminated by register<Boolean>(false, BOOL)

    //Eliminations - Styling - Colors
    val eliminationsOverrideColors by register<Boolean>(false, BOOL)
    val eliminationsNumberColor by register<Color>(Color(0xa63efc), RGB_COLOR_CODEC)
    val eliminationsIconColor by register<Color>(Color(0xa63efc), RGB_COLOR_CODEC)
    val eliminationsTextColor by register<Color>(Color(0xa63efc), RGB_COLOR_CODEC)

    //Star Fragments - Misc
    val starFragmentsHideWhenEliminated by register<Boolean>(true, BOOL)

    //Star Fragments - Styling - Colors
    val starFragmentsOverrideColors by register<Boolean>(false, BOOL)
    val starFragmentsNumberColor by register<Color>(Color(0xa0f9ff), RGB_COLOR_CODEC)
    val starFragmentsIconColor by register<Color>(Color(0xa0f9ff), RGB_COLOR_CODEC)
    val starFragmentsTextColor by register<Color>(Color(0xa0f9ff), RGB_COLOR_CODEC)

    //Playtime - Styling - Colors
    val playtimeOverrideColors by register<Boolean>(false, BOOL)
    val playtimeNumberColor by register<Color>(Color(0x3efca1), RGB_COLOR_CODEC)
    val playtimeIconColor by register<Color>(Color(0x3efca1), RGB_COLOR_CODEC)
    val playtimeTextColor by register<Color>(Color(0x3efca1), RGB_COLOR_CODEC)

    //Modifiers - Misc
    val modifiersMaxBoosters by register<Int>(5, INT)

    //Modifiers - Styling - Colors
    val modifiersOverrideColors by register<Boolean>(false, BOOL)
    val modifiersTextColor by register<Color>(Color(0xa63efc), RGB_COLOR_CODEC)
    val modifiersTextColorRegularModifier by register<Color>(Color(CommonColors.YELLOW), RGB_COLOR_CODEC)
    val modifiersTextColorEternalModifier by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)
    val modifiersShadowColorEternalModifier by register<Color>(Color(-10071549), RGB_COLOR_CODEC)
    val modifiersTextColorChargedModifier by register<Color>(Color(0x0786FF), RGB_COLOR_CODEC)
    val modifiersTextColorMysteryModifier by register<Color>(Color(0xD2B5FF), RGB_COLOR_CODEC)

    //Modifiers - Styling - Icons
    val modifiersUseCustomModifierIcons by register<Boolean>(false, BOOL)
    val modifiersUse2dHeadIcons by register<Boolean>(false, BOOL)

    //Mace Chance - Misc
    val maceChanceHideWhenEliminated by register<Boolean>(false, BOOL)

    //Mace Chance - Styling - Colors
    val maceChanceOverrideColors by register<Boolean>(false, BOOL)
    val maceChanceNumberColor by register<Color>(Color(0x42C1FF), RGB_COLOR_CODEC)
    val maceChanceIconColor by register<Color>(Color(0x42C1FF), RGB_COLOR_CODEC)
    val maceChanceTextColor by register<Color>(Color(0x42C1FF), RGB_COLOR_CODEC)

    //Bounty Board - Misc
    val bountyBoardMaxPlayers by register<Int>(3, INT)
    val bountyBoardMinBounty by register<Int>(1, INT)

    //Bounty Board - Styling - Colors
    val bountyBoardOverrideColors by register<Boolean>(false, BOOL)
    val bountyBoardNumberColor by register<Color>(Color(0xff7cf4), RGB_COLOR_CODEC)
    val bountyBoardTextColorPlayer by register<Color>(Color(CommonColors.YELLOW), RGB_COLOR_CODEC)
    val bountyBoardTextColor by register<Color>(Color(0xff7cf4), RGB_COLOR_CODEC)

    //Fps - Styling - Colors
    val fpsOverrideColors by register<Boolean>(false, BOOL)
    val fpsNumberColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)
    val fpsTextColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)

    //Ping - Styling - Colors
    val pingOverrideColors by register<Boolean>(false, BOOL)
    val pingNumberColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)
    val pingTextColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)

    //Tps - Styling - Colors
    val tpsOverrideColors by register<Boolean>(false, BOOL)
    val tpsNumberColor by register<Color>(Color(0xbfff00), RGB_COLOR_CODEC)
    val tpsTextColor by register<Color>(Color(0xbfff00), RGB_COLOR_CODEC)

    //Achievements
    val areAchievementsShown by register<MutableMap<String, Boolean>>(mutableMapOf(), Codec.unboundedMap(STRING, BOOL))

    fun getOverrideColorsOption(category: String): Boolean {
        return when (category) {
            "round_number" -> roundNumberOverrideColors.value
            "players_alive" -> playersAliveOverrideColors.value
            "accuracy" -> accuracyOverrideColors.value
            "eliminations" -> eliminationsOverrideColors.value
            "star_fragments" -> starFragmentsOverrideColors.value
            "playtime" -> playtimeOverrideColors.value
            "modifiers" -> modifiersOverrideColors.value
            "mace_chance" -> maceChanceOverrideColors.value
            "bounty_board" -> bountyBoardOverrideColors.value
            "fps" -> fpsOverrideColors.value
            "ping" -> pingOverrideColors.value
            "tps" -> tpsOverrideColors.value
            else -> false
        }
    }

    fun getOptionValue(category: String, type: String): Int? {
        return when (category) {
            "round_number" -> when (type) {
                "number_color" -> roundNumberNumberColor.value.rgb
                "text_color" -> roundNumberTextColor.value.rgb
                else -> null
            }

            "players_alive" -> when (type) {
                "number_color.alive" -> playersAliveNumberColorAlive.value.rgb
                "number_color.total" -> playersAliveNumberColorTotal.value.rgb
                "text_color" -> playersAliveTextColor.value.rgb
                else -> null
            }

            "accuracy" -> when (type) {
                "number_color" -> accuracyNumberColor.value.rgb
                "icon_color" -> accuracyIconColor.value.rgb
                "text_color" -> accuracyTextColor.value.rgb
                else -> null
            }

            "eliminations" -> when (type) {
                "number_color" -> eliminationsNumberColor.value.rgb
                "icon_color" -> eliminationsIconColor.value.rgb
                "text_color" -> eliminationsTextColor.value.rgb
                else -> null
            }

            "star_fragments" -> when (type) {
                "number_color" -> starFragmentsNumberColor.value.rgb
                "icon_color" -> starFragmentsIconColor.value.rgb
                "text_color" -> starFragmentsTextColor.value.rgb
                else -> null
            }

            "playtime" -> when (type) {
                "number_color" -> playtimeNumberColor.value.rgb
                "icon_color" -> playtimeIconColor.value.rgb
                "text_color" -> playtimeTextColor.value.rgb
                else -> null
            }


            "modifiers" -> when (type) {
                "text_color" -> modifiersTextColor.value.rgb
                "text_color.regular_modifier" -> modifiersTextColorRegularModifier.value.rgb
                "text_color.eternal_modifier" -> modifiersTextColorEternalModifier.value.rgb
                "shadow_color.eternal_modifier" -> modifiersShadowColorEternalModifier.value.rgb
                "text_color.charged_modifier" -> modifiersTextColorChargedModifier.value.rgb
                "text_color.mystery_modifier" -> modifiersTextColorMysteryModifier.value.rgb
                else -> null
            }

            "mace_chance" -> when (type) {
                "number_color" -> maceChanceNumberColor.value.rgb
                "icon_color" -> maceChanceIconColor.value.rgb
                "text_color" -> maceChanceTextColor.value.rgb
                else -> null
            }

            "bounty_board" -> when (type) {
                "number_color" -> bountyBoardNumberColor.value.rgb
                "text_color" -> bountyBoardTextColor.value.rgb
                "text_color.player" -> bountyBoardTextColorPlayer.value.rgb
                else -> null
            }

            "fps" -> when (type) {
                "number_color" -> fpsNumberColor.value.rgb
                "text_color" -> fpsTextColor.value.rgb
                else -> null
            }

            "ping" -> when (type) {
                "number_color" -> pingNumberColor.value.rgb
                "text_color" -> pingTextColor.value.rgb
                else -> null
            }

            "tps" -> when (type) {
                "number_color" -> tpsNumberColor.value.rgb
                "text_color" -> tpsTextColor.value.rgb
                else -> null
            }

            else -> null
        }
    }

    fun getFirstAvailableAccentColor(category: String, type: String): Int? {
        val colorIds: List<Int> = when (category) {
            "round_number" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "players_alive" -> when (type) {
                "number_color.alive" -> kotlin.collections.listOf(2, 1)
                "number_color.total" -> listOf(3, 2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "accuracy" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "icon_color" -> listOf(3, 2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "eliminations" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "icon_color" -> listOf(3, 2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "star_fragments" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "icon_color" -> listOf(3, 2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "playtime" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "icon_color" -> listOf(3, 2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "modifiers" -> when (type) {
                "text_color" -> listOf(1)
                else -> return null
            }

            "mace_chance" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "icon_color" -> listOf(3, 2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "bounty_board" -> when (type) {
                "number_color" -> listOf(3, 2, 1)
                "text_color.player" -> kotlin.collections.listOf(2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "fps" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "ping" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            "tps" -> when (type) {
                "number_color" -> kotlin.collections.listOf(2, 1)
                "text_color" -> listOf(1)
                else -> return null
            }

            else -> return null
        }
        val colorNumber = colorIds.firstOrNull { it <= accentColorNumber.value } ?: return null
        return when (colorNumber) {
            1 -> mainAccentColor.value.rgb
            2 -> secondAccentColor.value.rgb
            3 -> thirdAccentColor.value.rgb
            else -> null
        }
    }

    fun getAccentColor(category: String, type: String, defaultColor: Int): Int {
        return if (getOverrideColorsOption(category.lowercase())) getOptionValue(category.lowercase(), type)
            ?: defaultColor
        else if (useAccentColors.value) getFirstAvailableAccentColor(category.lowercase(), type) ?: defaultColor
        else defaultColor

    }
}