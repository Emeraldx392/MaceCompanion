package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.stateManagers.AccuracyManager.lastRoundWithMace
import moe.pxe.macecompanion.stateManagers.AccuracyManager.maceAttempts
import moe.pxe.macecompanion.stateManagers.BountyManager.bounties
import moe.pxe.macecompanion.stateManagers.RoundManager.round
import moe.pxe.macecompanion.stateManagers.StarFragmentManager.calculateStarFragments
import moe.pxe.macecompanion.util.PlayerInventory.getPlayerSlotItemStack
import moe.pxe.macecompanion.util.PlayerProfile.getPlayerProfile
import moe.pxe.macecompanion.util.SubtitleCallback
import moe.pxe.macecompanion.util.TitleCallback
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture
import kotlin.collections.set
import kotlin.text.Regex

object EliminationManager {
    var eliminations = -1
    var eliminated = true
    var playersAlive = -1
    var playersTotal = -1

    val chatEliminationRegex = Regex("""⏵ (.+) was eliminated by .+! \((\d+) remain\)""")
    val chatEarlyLeaveRegex = Regex("""⏵ (.+) left while alive! \((\d+) remain\)""")
    val chatBlowUpRegex = Regex("""⏵ (.+) blew up! \((\d+) remain\)""")
    val chatVoidDeathRegex = Regex("""⏵ (.+) fell into the void! \((\d+) remain\)""")
    val chatVoidEliminationRegex = Regex("""⏵ (.+) was thrown into the void by .+! \((\d+) remain\)""")
    val chatSpikeDeathRegex = Regex("""⏵ (.+) fell on a spike! \((\d+) remain\)""")
    val chatLightningDeathRegex = Regex("""⏵ (.+) was caught in the lightning! \((\d+) remain\)""")
    val chatElimCounterRegex = Regex("""\s+◇ \+\d+🪓, total (\d+)🪓""")

    val titlePlayersAliveRegex = Regex("""(\d+) ᴀʟɪᴠᴇ""")
    val titleEliminatedRegex = Regex("""☠☠☠""")

    val client: Minecraft = Minecraft.getInstance()

    fun resetEliminationData() {
        eliminations = -1
        eliminated = true
        playersAlive = -1
        playersTotal = -1
    }

    fun checkIfEliminated() {
        if (client.player?.isSpectator == true) {
            eliminated = true
            return
        }
        val lastSlotItem = getPlayerSlotItemStack(8).item
        eliminated = (lastSlotItem == Items.STICK || lastSlotItem == Items.BREEZE_ROD)
    }

    fun registerEliminationListeners() {
        ClientReceiveMessageEvents.GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register
            val hasAxe = text.contains("🪓")
            val hasArrow = text.contains("→")
            if (!text.startsWith("⏵ ") && !hasAxe && !hasArrow) return@register

            CompletableFuture.runAsync {
                val eliminationMatch =
                    if (text.contains("eliminated")) chatEliminationRegex.matchEntire(text)
                    else if (text.contains("left")) chatEarlyLeaveRegex.matchEntire(text)
                    else if (text.contains("blew")) chatBlowUpRegex.matchEntire(text)
                    else if (text.contains("spike")) chatSpikeDeathRegex.matchEntire(text)
                    else if (text.contains("lightning")) chatLightningDeathRegex.matchEntire(text)
                    else if (text.contains("thrown")) chatVoidEliminationRegex.matchEntire(text)
                    else chatVoidDeathRegex.matchEntire(text)
                eliminationMatch?.groups?.let {
                    playersAlive = it[2]?.value?.toIntOrNull() ?: -1
                    if (!eliminated) calculateStarFragments()
                }
                if (hasAxe && !eliminated) chatElimCounterRegex.matchEntire(text)?.groups[1]?.let {
                    if (lastRoundWithMace == round) maceAttempts[round] = true
                    eliminations = it.value.toIntOrNull() ?: 0
                    calculateStarFragments()
                }
                if (!eliminated) chatEarlyLeaveRegex.matchEntire(text)?.groups[1]?.let {
                    val playerThatLeft = getPlayerProfile(it.value)
                    if (bounties.contains(playerThatLeft)) bounties.remove(playerThatLeft)
                }
            }
            return@register
        }
        TitleCallback.EVENT.register(
            object : TitleCallback {
                override fun setTitleText(packet: ClientboundSetTitleTextPacket): InteractionResult {
                    if (!PlotManager.onMaceRoulette) return InteractionResult.PASS
                    titleEliminatedRegex.matchEntire(packet.text.string)?.let {
                        eliminated = true
                        bounties.clear()
                    }
                    return InteractionResult.PASS
                }
            }
        )
        SubtitleCallback.EVENT.register(
            object : SubtitleCallback {
                override fun setSubtitleText(packet: ClientboundSetSubtitleTextPacket): InteractionResult {
                    if (!PlotManager.onMaceRoulette) return InteractionResult.PASS
                    titlePlayersAliveRegex.matchEntire(packet.text.string)?.let { playersAliveMatch ->
                        playersAliveMatch.groups[1]?.let {
                            playersAlive = it.value.toIntOrNull() ?: -1
                        }
                    }
                    return InteractionResult.PASS
                }
            }
        )
    }
}