package moe.pxe.macecompanion.stateManagers

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.CustomToasts
import moe.pxe.macecompanion.config.Config
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft

object PlayersOnlineManager {
    val chatJoinRegex = Regex("""\+ (.+)""")
    val chatJoinDFnNormalRegex = Regex("""(.+) joined\.""")
    val chatJoinDFnSpecialRegex = Regex("""\[.+](.+) joined!""")
    val chatLeaveRegex = Regex("""(.+) left\.""")

    val client = Minecraft.getInstance()

    fun registerPlayersOnlineListeners() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true

            val containsPlus = text.startsWith("+")
            val containsDot = text.endsWith(".")
            val containsExclamation = text.endsWith("!")

            if (!containsPlus && !containsDot && !containsExclamation) return@register true

            val shouldHide = Config.hidePlayerJoinedLeftMessages.value
            val showToasts = Config.showPlayerToasts.value

            if (containsPlus) {
                chatJoinRegex.matchEntire(text)?.groups?.let { match ->
                    if (showToasts) {
                        val playerName = match[1]?.value.toString()
                        client.execute { CustomToasts.sendPlayerJoinedToast(playerName) }
                    }
                    if (shouldHide) return@register false
                }
            }

            chatJoinDFnNormalRegex.matchEntire(text)?.let {
                if (shouldHide) return@register false
            }

            if (containsExclamation) {
                chatJoinDFnSpecialRegex.matchEntire(text)?.groups?.let { match ->
                    if (showToasts) {
                        val playerName = match[1]?.value.toString()
                        client.execute { CustomToasts.sendPlayerJoinedToast(playerName) }
                    }
                    if (shouldHide) return@register false
                }
            }

            chatLeaveRegex.matchEntire(text)?.groups?.let { match ->
                if (showToasts) {
                    val playerName = match[1]?.value.toString()
                    client.execute { CustomToasts.sendPlayerLeftToast(playerName) }
                }
                if (shouldHide) return@register false
            }

            return@register true
        }
    }
}