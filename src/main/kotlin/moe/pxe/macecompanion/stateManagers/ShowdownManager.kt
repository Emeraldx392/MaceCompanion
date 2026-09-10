package moe.pxe.macecompanion.stateManagers

import com.mojang.authlib.GameProfile
import moe.pxe.macecompanion.AutoBet.sendAutoBet
import moe.pxe.macecompanion.util.PlayerProfile.resolvePlayerFromRawName
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import java.util.concurrent.CompletableFuture

object ShowdownManager {
    var redPlayer: GameProfile? = null
    var bluePlayer: GameProfile? = null
    var redVotesPercentage: Int = -1
    var blueVotesPercentage: Int = -1

    val showdownVotingRegex = Regex("""\s+(.+)\s+vs\.\s+(.+)""")
    val showdownBarRegex = Regex(""".+ - (\d+)%""")
    val showdownOverRegex = Regex("""☆ Showdown Over ☆""")

    val client: Minecraft = Minecraft.getInstance()

    fun getShowdownVotes(playerString: String): Int {
        val bossOverlay = client.gui.hud.bossOverlay
        val bossBars = bossOverlay.events
        val bossBarLerpingEvents = bossBars.values
        if (bossBarLerpingEvents.isEmpty()) return -1
        var value: Int = -1
        bossBarLerpingEvents.forEach { bar ->
            val name = bar.name.string
            if(name.contains(playerString)) value = showdownBarRegex.matchEntire(name)?.groups[1]?.value?.toIntOrNull() ?: -1
        }
        return value
    }

    fun resetShowdownData(){
        redPlayer = null
        bluePlayer = null
        redVotesPercentage = -1
        blueVotesPercentage = -1
    }

    fun registerShowdownListeners() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { _ ->
            redPlayer?.let { redVotesPercentage = getShowdownVotes(it.name) }
            bluePlayer?.let { blueVotesPercentage = getShowdownVotes(it.name) }
        })
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            val text = message.string

            val containsVS = text.contains("vs")
            val containsStar = text.startsWith("☆")
            if(!containsStar && !containsVS) return@register

            CompletableFuture.runAsync {
                if (containsVS) showdownVotingRegex.matchEntire(text)?.groups?.let {
                    redPlayer = resolvePlayerFromRawName(it[1]?.value)
                    bluePlayer = resolvePlayerFromRawName(it[2]?.value)
                    sendAutoBet()
                }
                if (containsStar) showdownOverRegex.matchEntire(text)?.groups?.let {
                    redPlayer = null
                    bluePlayer = null
                    redVotesPercentage = -1
                    blueVotesPercentage = -1
                }
            }
            return@register
        }
    }
}