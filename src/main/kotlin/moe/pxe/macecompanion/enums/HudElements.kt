package moe.pxe.macecompanion.enums

import com.mojang.authlib.GameProfile
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.areAchievementsShown
import moe.pxe.macecompanion.config.Config.eliminationsHideWhenEliminated
import moe.pxe.macecompanion.config.Config.getAccentColor
import moe.pxe.macecompanion.config.Config.maceChanceHideWhenEliminated
import moe.pxe.macecompanion.config.Config.modifiersMaxBoosters
import moe.pxe.macecompanion.config.Config.modifiersUse2dHeadIcons
import moe.pxe.macecompanion.config.Config.modifiersUseCustomModifierIcons
import moe.pxe.macecompanion.config.Config.starFragmentsHideWhenEliminated
import moe.pxe.macecompanion.config.controllers.ConfigurableEnum
import moe.pxe.macecompanion.config.hudElementConfigs.AccuracyConfig
import moe.pxe.macecompanion.config.hudElementConfigs.AchievementsConfig
import moe.pxe.macecompanion.config.hudElementConfigs.BountyBoardConfig
import moe.pxe.macecompanion.config.hudElementConfigs.EliminationsConfig
import moe.pxe.macecompanion.config.hudElementConfigs.FpsConfig
import moe.pxe.macecompanion.config.hudElementConfigs.MaceChanceConfig
import moe.pxe.macecompanion.config.hudElementConfigs.ModifiersConfig
import moe.pxe.macecompanion.config.hudElementConfigs.PingConfig
import moe.pxe.macecompanion.config.hudElementConfigs.PlayersAliveConfig
import moe.pxe.macecompanion.config.hudElementConfigs.PlaytimeConfig
import moe.pxe.macecompanion.config.hudElementConfigs.RoundNumberConfig
import moe.pxe.macecompanion.config.hudElementConfigs.StarFrgamentsConfig
import moe.pxe.macecompanion.config.hudElementConfigs.TpsConfig
import moe.pxe.macecompanion.resourceLoaders.AchievementResourceLoader
import moe.pxe.macecompanion.resourceLoaders.AchievementResourceLoader.forceStopRendering
import moe.pxe.macecompanion.stateManagers.AccuracyManager.maceAttempts
import moe.pxe.macecompanion.stateManagers.AchievementManager.possibleAchievements
import moe.pxe.macecompanion.stateManagers.BountyManager.bounties
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminated
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminations
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersAlive
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersTotal
import moe.pxe.macecompanion.stateManagers.ModifierManager.eternalModifier
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifierBoosters
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifiers
import moe.pxe.macecompanion.stateManagers.ModifierManager.mysteryAmount
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.fps
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.ping
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.tps
import moe.pxe.macecompanion.stateManagers.PlotManager.isStatless
import moe.pxe.macecompanion.stateManagers.PlotManager.onDiamondfire
import moe.pxe.macecompanion.stateManagers.RoundManager.gameOngoing
import moe.pxe.macecompanion.stateManagers.RoundManager.maceChance
import moe.pxe.macecompanion.stateManagers.RoundManager.round
import moe.pxe.macecompanion.stateManagers.RoundManager.roundColor
import moe.pxe.macecompanion.stateManagers.StarFragmentManager.starFragments
import moe.pxe.macecompanion.stateManagers.TimeManager.playtime
import moe.pxe.macecompanion.util.PlayerProfile.headFromProfile
import moe.pxe.macecompanion.util.TextUtils.buildModifierTextWith2dBoosters
import moe.pxe.macecompanion.util.TextUtils.getStarFragmentIcon
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

enum class HudElements : NameableEnum, StringRepresentable, ConfigurableEnum {
    ROUND_NUMBER {
        private var cachedRound: Int = -2
        private var cachedColor: Int = -1
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (round == -1 || !gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            val currentColor = roundColor?.color?.value ?: 0x9ef6fc

            if (round != cachedRound || currentColor != cachedColor || cachedText == null) {
                cachedRound = round
                cachedColor = currentColor

                val numberText = Component.literal("$round").setStyle(Style.EMPTY.withBold(true)).withColor(getAccentColor(name, "number_color", currentColor))
                val newText = Component.translatable("mrc.roundhud.round", numberText).setStyle(Style.EMPTY.withBold(true)).withColor(getAccentColor(name, "text_color", currentColor))

                cachedText = newText
                cachedTextWidth = textRenderer.width(newText)
            }

            val text = cachedText!!
            val xPos = if (rightAligned) -cachedTextWidth.toFloat() else 0f
            val yPos = if (bottomAligned) (-yOffset shr 1).toFloat() - 12f else (yOffset shr 1).toFloat()

            context.pose().pushMatrix()
            context.pose().scale(2f)
            context.pose().translate(xPos, yPos)
            context.text(textRenderer, text, 0, 0, -1)
            context.pose().popMatrix()
            return 24
        }

        override fun generateConfig(parent: Screen): Screen? = RoundNumberConfig.generateConfig(parent)
    },
    PLAYERS_ALIVE {
        private var cachedAlive: Int = -2
        private var cachedTotal: Int = -2
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (playersAlive == -1 || !gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font

            if (playersAlive != cachedAlive || playersTotal != cachedTotal || cachedText == null) {
                cachedAlive = playersAlive
                cachedTotal = playersTotal
                val countText = Component.literal("$playersAlive").withColor(getAccentColor(name, "number_color.alive", 0xd5fcf5))
                if (playersTotal >= 0) countText.append(Component.literal("/$playersTotal").withColor(getAccentColor(name, "number_color.total", 0xd0d0d0)))
                val newText = Component.translatable("mrc.roundhud.alive", countText).withColor(getAccentColor(name, "text_color", CommonColors.WHITE))
                cachedText = newText
                cachedTextWidth = textRenderer.width(newText)
            }
            val text = cachedText!!
            val xPos = if (rightAligned) -cachedTextWidth else 0
            val yPos = if (bottomAligned) -yOffset - 12 else yOffset

            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = PlayersAliveConfig.generateConfig(parent)
    },
    ACCURACY {
        private var cachedMaceAttempts = mutableMapOf<Int, Boolean>()
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        val textColors = arrayOf(0xff2c01, 0xff5500, 0xff8400, 0xffa503, 0xffd202, 0xfff400, 0xe6ff01, 0xc0ff03, 0x92ff00, 0x74ff02, 0x3cff01, 0x13ff00, 0x01ff00)

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (maceAttempts.isEmpty() || !gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            if (cachedMaceAttempts != maceAttempts || cachedText == null) {
                cachedMaceAttempts = maceAttempts.toMutableMap()
                val totalMaceAttempts = maceAttempts.size
                var successCount = 0
                for (attempt in maceAttempts) {
                    if (attempt.value) successCount++
                }
                val accuracy = ((successCount.toFloat() / totalMaceAttempts) * 100f).roundToInt()
                val accuracyColorIdx = (accuracy / 7.7).toInt().coerceIn(0, textColors.lastIndex)
                val iconText = Component.literal("\uD83C\uDFF9 ").withColor(getAccentColor(name, "icon_color", 0x79fc00))
                val countText = Component.literal("$successCount/$totalMaceAttempts").withColor(getAccentColor(name, "text_color", 0x79fc00))
                val percentageText = Component.literal("$accuracy%").withColor(getAccentColor(name, "number_color", textColors[accuracyColorIdx]))
                val text = Component.translatable("mrc.roundhud.accuracy", percentageText, countText).withColor(getAccentColor(name, "text_color", 0x79fc00))
                val finalText = iconText.append(text)

                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            val xPos = if (rightAligned) -cachedTextWidth else 0
            val yPos = if (bottomAligned) -yOffset - 12 else yOffset

            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = AccuracyConfig.generateConfig(parent)
    },
    ELIMINATIONS {
        private var cachedEliminations: Int = -2
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (eliminations == -1 || !gameOngoing || (eliminationsHideWhenEliminated.value && eliminated)) return 0

            val textRenderer = Minecraft.getInstance().font

            if (eliminations != cachedEliminations || cachedText == null) {
                val iconText = Component.literal("\uD83E\uDE93 ").withColor(getAccentColor(name, "icon_color", 0xa63efc))
                val numberText = Component.literal("$eliminations").withColor(getAccentColor(name, "number_color", 0xa63efc))
                val text = Component.translatable("mrc.roundhud.eliminations", numberText).withColor(getAccentColor(name, "text_color", 0xa63efc))
                val finalText = iconText.append(text)
                cachedEliminations = eliminations
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = EliminationsConfig.generateConfig(parent)
    },
    STAR_FRAGMENTS {
        private val starFragmentIcon = getStarFragmentIcon()
        private var cachedStarFragments: Int = 0
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (starFragments == -1 || !gameOngoing || isStatless || (starFragmentsHideWhenEliminated.value && eliminated)) return 0

            val textRenderer = Minecraft.getInstance().font
            if (cachedStarFragments != starFragments || cachedText == null) {
                val starFragmentIconText = Component.empty().append(starFragmentIcon).withColor(getAccentColor(name, "icon_color", 0xa0f9ff))
                val numberText = Component.literal(" $starFragments").withColor(getAccentColor(name, "number_color", 0xa0f9ff))
                val text = Component.translatable("mrc.roundhud.starFragments", numberText).withColor(getAccentColor(name, "text_color", 0xa0f9ff))
                val finalText = starFragmentIconText.append(text)
                cachedStarFragments = starFragments
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = StarFrgamentsConfig.generateConfig(parent)
    },
    PLAYTIME {
        private var lastCachedSecond: Long = -1L
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing) return 0
            val currentPlaytime = playtime ?: return 0

            val currentSecond = currentPlaytime.elapsedNow().toLong(DurationUnit.SECONDS)
            val textRenderer = Minecraft.getInstance().font

            if (currentSecond != lastCachedSecond || cachedText == null) {
                lastCachedSecond = currentSecond

                val minutes = currentSecond / 60
                val seconds = currentSecond % 60

                val timeString = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

                val iconText = Component.literal("⌚ ").withColor(getAccentColor(name, "icon_color", 0x3efca1))
                val numberText = Component.literal(timeString).withColor(getAccentColor(name, "number_color", 0x3efca1))
                val text = Component.translatable("mrc.roundhud.playtime", numberText).withColor(getAccentColor(name, "text_color", 0x3efca1))

                val finalText = iconText.append(text)

                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            val xPos = if (rightAligned) -cachedTextWidth else 0
            val yPos = if (bottomAligned) -yOffset - 12 else yOffset

            context.text(textRenderer, text, xPos, yPos, -1)
            return 12

        }

        override fun generateConfig(parent: Screen): Screen? = PlaytimeConfig.generateConfig(parent)
    },
    MODIFIERS {
        private var cachedModifiers = mutableMapOf<Modifiers, Boolean>()
        private var cachedHeaderText: Component? = null
        private var cachedHeaderTextWidth: Int = 0
        private var cachedUse2dHeads: Boolean? = null
        private var cachedModifierIcons = mutableMapOf<Modifiers, ItemStack>()
        private var cachedRightModifierTexts = mutableMapOf<Modifiers, Component>()
        private var cachedLeftModifierTexts = mutableMapOf<Modifiers, Component>()
        private var cachedModifierTextWidths = mutableMapOf<Modifiers, Int>()

        override fun refreshRendering() {
            cachedHeaderText = null
            cachedModifierIcons.clear()
            cachedLeftModifierTexts.clear()
            cachedRightModifierTexts.clear()

        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing || modifiers.isEmpty()) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedHeaderText == null) {
                val headerText = Component.translatable("mrc.roundhud.modifiers").withColor(getAccentColor(name, "text_color", 0xa63efc))
                cachedHeaderText = headerText
                cachedHeaderTextWidth = textRenderer.width(headerText)
            }

            val headerText = cachedHeaderText!!
            var yPos = if (bottomAligned) -yOffset - 12 - (modifiers.size * 20) else yOffset
            var xPos = if (rightAligned) -cachedHeaderTextWidth else 0
            context.text(textRenderer, headerText, xPos, yPos, -1)
            yPos += 12
            val use2dHeads = modifiersUse2dHeadIcons.value
            val modifiersChanged = (cachedModifiers != modifiers)
            if (modifiersChanged) cachedModifiers = modifiers.toMutableMap()
            modifiers.forEach { (modifier, isCharged) ->
                var xPos = if (rightAligned) -16 else 0
                if (modifiersChanged || cachedModifierIcons[modifier] == null) {
                    val baseIcon = if (!modifiersUseCustomModifierIcons.value) modifier.icon else modifier.customIcon
                    cachedModifierIcons[modifier] = baseIcon.copy().apply {
                        if (modifier == Modifiers.MYSTERY) count = mysteryAmount
                    }
                }
                val icon = cachedModifierIcons[modifier]!!
                context.item(icon, xPos, yPos)
                context.itemDecorations(textRenderer, icon, xPos, yPos)
                val isEternal = eternalModifier == modifier
                if (modifiersChanged || (rightAligned && cachedRightModifierTexts[modifier] == null) || (!rightAligned && cachedLeftModifierTexts[modifier] == null) || cachedUse2dHeads != use2dHeads) {
                    cachedUse2dHeads = use2dHeads
                    val finalText = buildModifierTextWith2dBoosters(modifier, isEternal, isCharged, rightAligned, use2dHeads, modifierBoosters[modifier])
                    if (rightAligned) cachedRightModifierTexts[modifier] = finalText
                    else cachedLeftModifierTexts[modifier] = finalText
                    cachedModifierTextWidths[modifier] = textRenderer.width(finalText)
                }
                val text = if (rightAligned) cachedRightModifierTexts[modifier]!! else cachedLeftModifierTexts[modifier]!!
                val width = cachedModifierTextWidths[modifier] ?: 0
                xPos = if (rightAligned) -22 - width else 22
                context.text(textRenderer, text, xPos, yPos + 4, -1)

                if (!use2dHeads) modifierBoosters[modifier]?.let { playerList ->
                    val maxBoosters = modifiersMaxBoosters.value
                    val visibleBoosters = playerList.take(maxBoosters)
                    val bonusBoostersAmount = playerList.size - maxBoosters

                    val direction = if (rightAligned) -1 else 1
                    val stepSize = 15 * direction
                    val baseStart = if (rightAligned) -44 - width else 28 + width

                    visibleBoosters.forEachIndexed { index, profile ->
                        val currentX = baseStart + (index * stepSize)
                        context.item(headFromProfile(profile), currentX, yPos)
                    }

                    if (bonusBoostersAmount > 0) {
                        val textX = baseStart + (visibleBoosters.size * stepSize)
                        context.text(textRenderer, Component.literal("+$bonusBoostersAmount").withColor(getAccentColor(name, "text_color", 0xa63efc)), textX, yPos + 4, -1)
                    }
                }
                yPos += 20
            }

            return 12 + (modifiers.size * 20)
        }

        override fun generateConfig(parent: Screen): Screen? = ModifiersConfig.generateConfig(parent)
    },
    MACE_CHANCE {
        private var cachedMaceChance: Float = -2f
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        val textColors = arrayOf(0xff2c01, 0xff5500, 0xff8400, 0xffa503, 0xffd202, 0xfff400, 0xe6ff01, 0xc0ff03, 0x92ff00, 0x74ff02, 0x3cff01, 0x13ff00, 0x01ff00)

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing || maceChance < 0f || (maceChanceHideWhenEliminated.value && eliminated)) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedMaceChance != maceChance || cachedText == null) {
                val maceChanceColorIdx = (maceChance / 7.7).toInt().coerceIn(0, textColors.lastIndex)
                val iconText = Component.literal("⚄ ").withColor(getAccentColor(name, "icon_color", 0x42C1FF))
                val numberText = Component.literal("%.2f%%".format(maceChance)).withColor(getAccentColor(name, "number_color", textColors[maceChanceColorIdx]))
                val text = Component.translatable("mrc.roundhud.mace_chance", numberText).withColor(getAccentColor(name, "text_color", 0x42C1FF))
                val finalText = iconText.append(text)

                cachedMaceChance = maceChance
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = MaceChanceConfig.generateConfig(parent)
    },
    BOUNTY_BOARD {
        private var cachedBounties: HashMap<GameProfile, Int> = HashMap()
        private var cachedHeaderText: Component? = null
        private var cachedHeaderTextWidth: Int = 0
        private var cachedRightBountyTexts: HashMap<GameProfile, Component?> = HashMap()
        private var cachedLeftBountyTexts: HashMap<GameProfile, Component?> = HashMap()
        private var cachedBountyTextWidths: HashMap<GameProfile, Int> = HashMap()

        override fun refreshRendering() {
            cachedHeaderText = null
            cachedRightBountyTexts.isEmpty()
            cachedLeftBountyTexts.isEmpty()

        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing || bounties.isEmpty() || eliminated) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedHeaderText == null) {
                val headerText = Component.translatable("mrc.roundhud.bounty_board").withColor(getAccentColor(name, "text_color", 0xff7cf4))
                cachedHeaderText = headerText
                cachedHeaderTextWidth = textRenderer.width(headerText)
            }
            val headerText = cachedHeaderText!!
            val sortedBounties = bounties.entries.sortedByDescending { it.value }.associate { it.key to it.value }
            var yPos = if (bottomAligned) -yOffset - 12 - (sortedBounties.size * 20) else yOffset
            var xPos = if (rightAligned) -cachedHeaderTextWidth else 0
            context.text(textRenderer, headerText, xPos, yPos, -1)
            yPos += 12
            var index = 1
            var bountyCount = 0
            val bountiesChanged = (cachedBounties != bounties)
            sortedBounties.forEach { (profile, bountyAmount) ->
                if (bountyAmount >= Config.bountyBoardMinBounty.value && index <= Config.bountyBoardMaxPlayers.value) {
                    val playerUsername = profile.name
                    var xPos = if (rightAligned) -16 else 0
                    context.item(headFromProfile(profile), xPos, yPos)
                    if (bountiesChanged || (rightAligned && cachedRightBountyTexts[profile] == null) || (!rightAligned && cachedLeftBountyTexts[profile] == null)) {
                        val playerText = Component.literal("$playerUsername").withColor(getAccentColor(name, "text_color.player", CommonColors.YELLOW))
                        val bountyText = Component.literal("$bountyAmount⛂").withColor(getAccentColor(name, "number_color", 0xff7cf4))
                        val finalText = if (rightAligned) {
                            Component.empty().append(bountyText).append(" ").append(playerText)
                        } else {
                            Component.empty().append(playerText).append(" ").append(bountyText)
                        }
                        cachedBountyTextWidths[profile] = textRenderer.width(finalText)
                        if (rightAligned) cachedRightBountyTexts[profile] = finalText
                        else cachedLeftBountyTexts[profile] = finalText
                    }
                    val text = if (rightAligned) cachedRightBountyTexts[profile]!! else cachedLeftBountyTexts[profile]!!
                    xPos = if (rightAligned) -22 - cachedBountyTextWidths[profile]!! else 22
                    context.text(textRenderer, text, xPos, yPos + 4, -1)
                    yPos += 20
                    bountyCount++
                    index++
                }
            }
            return 12 + (bountyCount * 20)
        }

        override fun generateConfig(parent: Screen): Screen? = BountyBoardConfig.generateConfig(parent)
    },
    FPS {
        private var cachedFps: Int = -2
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (fps == -1) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedFps != fps || cachedText == null) {
                cachedFps = fps
                val numberText = Component.literal("$fps").withColor(getAccentColor(name, "number_color", CommonColors.WHITE))
                val finalText = Component.translatable("mrc.roundhud.fps", numberText).withColor(getAccentColor(name, "text_color", CommonColors.WHITE))
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }
        override fun generateConfig(parent: Screen): Screen? = FpsConfig.generateConfig(parent)
    },
    PING {
        private var cachedPing: Int = -2
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (ping <= 0) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedPing != ping || cachedText == null) {
                cachedPing = ping
                val pingColor = if (ping < 50) 0x1eff00 else if (ping < 100) 0xfff100 else if (ping < 200) 0xff9500 else 0xff3b3b
                val numberText = Component.literal("$ping").withColor(getAccentColor(name, "number_color", pingColor))
                val finalText = Component.translatable("mrc.roundhud.ping", numberText).withColor(getAccentColor(name, "text_color", pingColor))
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = PingConfig.generateConfig(parent)
    },
    TPS {
        private var cachedTps: Float = -2f
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun refreshRendering() {
            cachedText = null
        }

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!onDiamondfire || tps == -1f) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedTps != tps || cachedText == null) {
                cachedTps = tps
                val numberText = Component.literal("$tps").withColor(getAccentColor(name, "number_color", 0xbfff00))
                val finalText = Component.translatable("mrc.roundhud.tps", numberText).withColor(getAccentColor(name, "text_color", 0xbfff00))
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = TpsConfig.generateConfig(parent)
    },
    ACHIEVEMENTS {
        override fun refreshRendering() {}

        override fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing || eliminated) return 0
            if(forceStopRendering) return 0
            if (isStatless) return 0

            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            var xPos = if (rightAligned) -16 else 0

            val ach = AchievementResourceLoader.getAll().values

            ach.forEach { achievement ->
                if (AchievementResourceLoader.getAllIdStrings().contains(achievement.id) && areAchievementsShown.value[achievement.id] ?: false) {
                    val itemStack = Items.ITEM_FRAME.defaultInstance.apply {
                        if (possibleAchievements.contains(achievement)) set(DataComponents.ITEM_MODEL, achievement.onItemModel) else set(DataComponents.ITEM_MODEL, achievement.offItemModel)
                    }
                    context.item(itemStack, xPos, yPos)
                    xPos += if (rightAligned) -16 else 16
                }
            }
            return 20
        }

        override fun generateConfig(parent: Screen): Screen? = AchievementsConfig.generateConfig(parent)
    };

    abstract fun refreshRendering()
    abstract fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int
    override fun generateConfig(parent: Screen): Screen? = null
    override fun getSerializedName(): String = name
    override fun getDisplayName(): Component = Component.translatable("mrc.hudelement.${name.lowercase()}")

    companion object {
        val CODEC = StringRepresentable.fromEnum(::values)
    }
}
