package moe.pxe.macecompanion.stateManagers

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.stateManagers.PlotManager.onMaceRoulette
import moe.pxe.macecompanion.stateManagers.SoundManager.soundsToCancel
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import java.time.LocalDateTime
import kotlin.math.roundToInt
import kotlin.time.TimeMark

object TimeManager {
    val timeRegex = Regex("""» Daytime has been set to .+""")

    var playtime: TimeMark? = null
    var hideDayTimeMessage = false
    const val TARGET_TICKS = 300
    var tickCounter = 0

    fun get24HourFloat(): Float {
        val now = LocalDateTime.now()
        val hours = now.hour
        val minutes = now.minute
        val seconds = now.second
        return hours + (minutes / 60.0f) + (seconds / 3600.0f)
    }

    fun syncMRTimeToIRL() {
        if(!Config.syncTime.value) return
        if(!onMaceRoulette) return
        var hours = get24HourFloat()
        if (hours >= 12) hours -= 12
        else hours += 12
        val maceRouletteTime: Int = (hours * 1000).roundToInt()
        soundsToCancel["minecraft:block.respawn_anchor.charge"] = false
        hideDayTimeMessage = true
        SendMessage.sendMessage("@time $maceRouletteTime")
    }

    fun resetTimeData() {
        playtime = null
        hideDayTimeMessage = false
        syncMRTimeToIRL()
    }

    fun registerTimeListeners(){
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            if (client.level != null) {
                tickCounter++
                if (tickCounter >= TARGET_TICKS) {
                    syncMRTimeToIRL()
                    tickCounter = 0
                }
            }
        })
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true
            if(!text.startsWith("»")) return@register true

            timeRegex.matchEntire(text)?.let{
                if(hideDayTimeMessage){
                    hideDayTimeMessage = false
                    return@register false
                }
            }

            return@register true
        }
    }
}