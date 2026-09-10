package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.stateManagers.PlotManager.onDiamondfire
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import java.util.concurrent.CompletableFuture

object PerformanceStatsManager {

    val tpsRegex = Regex("""TPS from last 1m, 5m, 15m: (\d+\.\d+), (\d+\.\d+), (\d+\.\d+)""")

    var fps: Int = -1
    var ping: Int = -1
    var tps: Float = -1f
    var checkingTPS: Boolean = false

    fun askForTPS(){
        if(onDiamondfire) {
            checkingTPS = true
            SendMessage.sendCommand("tps")
        }
    }

    fun registerPerformanceStatsListeners(){
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true
            if (!PlotManager.onMaceRoulette) return@register true
            if (!text.startsWith("TPS")) return@register true

            CompletableFuture.runAsync {
                tpsRegex.matchEntire(text)?.groups?.let {
                    val tps1 = it[1]?.value?.toFloat() ?: -1f
                    val tps2 = it[2]?.value?.toFloat() ?: -1f
                    val tps3 = it[3]?.value?.toFloat() ?: -1f
                    tps = (tps1 + tps2 + tps3) / 3f
                }
            }

            if(checkingTPS) {
                checkingTPS = false
                return@register false
            }

            return@register true

        }
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            fps = client.fps
            ping = client.connection?.getPlayerInfo(client.user.name)?.latency ?: -1
        })
    }
}