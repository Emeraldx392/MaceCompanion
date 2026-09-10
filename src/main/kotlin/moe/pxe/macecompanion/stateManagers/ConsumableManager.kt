package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.CustomToasts.sendChaosDustToast
import moe.pxe.macecompanion.CustomToasts.sendEternalElectorToast
import moe.pxe.macecompanion.CustomToasts.sendModifierChargerToast
import moe.pxe.macecompanion.stateManagers.ModifierManager.getModifierFromMessage
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.concurrent.CompletableFuture

object ConsumableManager {
    var eternalElectorPlayer: String? = null
    var eternalElectorModifier: String? = null

    val modifierChargerRegex = Regex("""⏵ (.+) used a Modifier Charger on (.+)!\n\s+◇ It will be charged for its next (\d+) appearances!""")
    val chaosDustChaosRegex = Regex("""⏵ (.+) activated Chaos!\n\s+◇ The next round will have five modifiers!""")
    val chaosDustMayhemRegex = Regex("""⏵ (.+) activated Mayhem!\n\s+◇ The next round will have seven modifiers!""")
    val chaosDustDoomsdayRegex = Regex("""⏵ (.+) activated Doomsday!\n\s+◇ The next round will have ten modifiers!""")
    val eternalElectorRegex = Regex("""⏵ (.+) used a Eternal Elector for (.+)!""")
    val eternalElectorPositionRegex = Regex("""\s+◇ It has been queued at position #(\d+)!""")

    fun resetConsumableData(){
        eternalElectorModifier = null
        eternalElectorPlayer = null
    }

    fun registerConsumableListeners() {
        ClientReceiveMessageEvents.GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register
            if (!text.startsWith("⏵") && !text.contains("#")) return@register

            CompletableFuture.runAsync {
            modifierChargerRegex.matchEntire(text)?.groups?.let {
                val player = it[1]?.value.toString()
                val modifier = getModifierFromMessage(message).translatable.string
                val queueLength = it[3]?.value?.toIntOrNull() ?: 0
                sendModifierChargerToast(modifier, queueLength, player)
            }
            chaosDustChaosRegex.matchEntire(text)?.groups?.let {
                val player = it[1]?.value.toString()
                sendChaosDustToast(player, 5)
            }
            chaosDustMayhemRegex.matchEntire(text)?.groups?.let {
                val player = it[1]?.value.toString()
                sendChaosDustToast(player, 7)
            }
            chaosDustDoomsdayRegex.matchEntire(text)?.groups?.let {
                val player = it[1]?.value.toString()
                sendChaosDustToast(player, 10)
            }
            eternalElectorRegex.matchEntire(text)?.groups?.let {
                eternalElectorPlayer = it[1]?.value.toString()
                eternalElectorModifier = getModifierFromMessage(message).translatable.string
            }
            eternalElectorPositionRegex.matchEntire(text)?.groups?.let {
                val queuePosition = it[1]?.value!!.toInt()
                if (eternalElectorPlayer != null && eternalElectorModifier != null) sendEternalElectorToast(eternalElectorModifier, eternalElectorPlayer, queuePosition)
                eternalElectorPlayer = null
                eternalElectorModifier = null
            }
        }
            return@register
        }
    }
}