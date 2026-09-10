package moe.pxe.macecompanion.stateManagers

import com.mojang.authlib.GameProfile
import moe.pxe.macecompanion.CustomToasts
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminated
import moe.pxe.macecompanion.util.PlayerProfile.getPlayerProfile
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import java.util.concurrent.CompletableFuture

object BountyManager {
    var bounties = HashMap<GameProfile, Int>()

    val placedBountyRegex = Regex("""⏵ (.+) placed a (\d+)⛂ bounty on (.+)!""")
    val selfPlacedBountyRegex = Regex("""⏵ (.+) placed a (\d+)⛂ bounty on themself!""")
    val raisedBountyRegex = Regex("""⏵ (.+) raised the bounty amount to (\d+)⛂ on (.+)!""")
    val selfRaisedBountyRegex = Regex("""⏵ (.+) raised the bounty amount on themself to (\d+)⛂!""")
    val rewardedBountyRegex = Regex("""⏵ (.+) was rewarded (\d+)⛂ for eliminating (.+)!""")
    val cashedInBountyRegex = Regex("""⏵ (.+) cashed in their bounty of (\d+)⛂!""")

    val playerListBountyRegex = Regex(""".+\s+◇\s+(\d+)⛂""")
    val client: Minecraft = Minecraft.getInstance()

    fun resetBountyData(){
        bounties.clear()
    }

    fun getBountyData() {
        val connection = client.connection ?: return
        val players = connection.onlinePlayers
        bounties.clear()
        val tabHUD = client.gui.hud.tabList

        for (player in players) {
            val displayName = tabHUD.getNameForDisplay(player).string
            if (!displayName.contains("⛂")) continue
            playerListBountyRegex.matchEntire(displayName)?.let { matchResult ->
                val matchGroup = matchResult.groups[1] ?: return@let
                val bountyValue = matchGroup.value.toIntOrNull()
                if (bountyValue != null) bounties[player.profile] = bountyValue
            }
        }
    }
    fun registerBountyListeners() {
        ClientReceiveMessageEvents.GAME.register{ message, overlay ->
            val text = message.string

            if (overlay) return@register
            if (!text.startsWith("⏵ ")) return@register
            if (!text.contains("⛂")) return@register
            if (!PlotManager.onMaceRoulette) return@register

            CompletableFuture.runAsync {

                placedBountyRegex.matchEntire(text)?.groups?.let {
                    val bountyPlacer = it[1]?.value
                    val bountyAmount = it[2]?.value?.toIntOrNull() ?: -1
                    val bountyReceiver = it[3]?.value
                    if (!eliminated) getPlayerProfile(bountyReceiver)?.let { profile ->
                        bounties[profile] = bountyAmount
                    }
                    if (bountyReceiver == client.user.name) CustomToasts.sendPlacedBountyToast(bountyAmount, bountyPlacer)
                }
                selfPlacedBountyRegex.matchEntire(text)?.groups?.let {
                    val bountyPlacer = it[1]?.value
                    val bountyAmount = it[2]?.value?.toIntOrNull() ?: -1
                    if (!eliminated) getPlayerProfile(bountyPlacer)?.let { profile ->
                        bounties[profile] = bountyAmount
                    }
                    if (bountyPlacer == client.user.name) CustomToasts.sendSelfPlacedBountyToast(bountyAmount)
                }
                raisedBountyRegex.matchEntire(text)?.groups?.let {
                    val bountyPlacer = it[1]?.value
                    val bountyAmount = it[2]?.value?.toIntOrNull() ?: -1
                    val bountyReceiver = it[3]?.value
                    if (!eliminated) getPlayerProfile(bountyReceiver)?.let { profile ->
                        bounties[profile] = bountyAmount
                    }
                    if (bountyReceiver == client.user.name) CustomToasts.sendRaisedBountyToast(bountyAmount, bountyPlacer)
                }
                selfRaisedBountyRegex.matchEntire(text)?.groups?.let {
                    val bountyPlacer = it[1]?.value
                    val bountyAmount = it[2]?.value?.toIntOrNull() ?: -1
                    if (!eliminated) getPlayerProfile(bountyPlacer)?.let { profile ->
                        bounties[profile] = bountyAmount
                    }
                    if (bountyPlacer == client.user.name) CustomToasts.sendSelfRaisedBountyToast(bountyAmount)
                }
                rewardedBountyRegex.matchEntire(text)?.groups?.let {
                    val bountyReceiver = it[1]?.value
                    val bountyAmount = it[2]?.value?.toIntOrNull() ?: -1
                    val playerWithBounty = it[3]?.value
                    if (!eliminated) {
                        val playerWithBountyProfile = getPlayerProfile(playerWithBounty)
                        bounties.remove(playerWithBountyProfile)
                    }
                    if (bountyReceiver == client.user.name) CustomToasts.sendRewardedBountyToast(bountyAmount, playerWithBounty)
                }
                cashedInBountyRegex.matchEntire(text)?.groups?.let {
                    val bountyReceiver = it[1]?.value
                    val bountyAmount = it[2]?.value?.toIntOrNull() ?: -1
                    if (!eliminated) {
                        val receiverProfile = getPlayerProfile(bountyReceiver)
                        bounties.remove(receiverProfile)
                    }
                    if (bountyReceiver == client.user.name) CustomToasts.sendCashedInBountyToast(bountyAmount)
                }
            }
            return@register
        }
    }
}