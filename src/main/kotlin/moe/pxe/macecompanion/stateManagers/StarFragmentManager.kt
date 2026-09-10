package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminated
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminations
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersAlive
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersTotal
import moe.pxe.macecompanion.stateManagers.PlotManager.isStatless
import moe.pxe.macecompanion.stateManagers.PlotManager.onMaceRoulette
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt

object StarFragmentManager {
    var starFragments = -1

    val totalStarFragmentGainRegex = Regex(""".+ᴛᴏᴛᴀʟ ɢᴀɪɴ: \+(\d+).+""")

    fun resetStarFragmentData() {
        starFragments = -1
    }

    fun calculateStarFragments() {
        val quarterPlayers = playersTotal ushr 2
        val halfPlayers = playersTotal ushr 1
        var multiplier = 1.0f
        if (playersAlive == 1) multiplier = 3.125f
        else if (playersAlive <= quarterPlayers) multiplier = 1.5625f
        else if (playersAlive <= halfPlayers) multiplier = 1.25f
        if (EventManager.doubleXp) multiplier *= 2.0f
        if(playersTotal != -1) starFragments = (((eliminations * 3) + (playersTotal - playersAlive)) * multiplier).roundToInt()
    }

    fun registerStarFragmentListeners() {
        ClientReceiveMessageEvents.GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register
            if (!onMaceRoulette || isStatless) return@register
            if (!text.contains("ᴛ")) return@register

            CompletableFuture.runAsync {
                totalStarFragmentGainRegex.matchEntire(text)?.groups?.let {
                    eliminated = true
                    val actualStarFragments = it[1]?.value?.toInt() ?: -1
                    if (starFragments < actualStarFragments) EventManager.doubleXp = true
                    else if (starFragments > actualStarFragments) EventManager.doubleXp = false
                    starFragments = actualStarFragments
                }
            }
            return@register
        }
    }
}