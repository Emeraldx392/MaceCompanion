package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.config.ConfigMenu
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersTotal
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.tps
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.PauseScreen
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture

object PlotManager {

    val patchPlotRegex = Regex("""⏵ Current Patch""")
    val plotRegex = Regex("""You are currently playing on:\n\n→ .+ \[(\d+)] \[(.+)]""")
    val findPlayerCommandRegex = Regex("""→ In Lobby - .+/(\d+) Remain""")

    var hidePlotRegex = false

    var onDiamondfire = false
    var onMaceRoulette = false
    var isStatless = false
    var plotHandle: String? = null
    var plotId: Int? = null

    val plotIds = mutableSetOf<Int>()
    val plotHandles = mutableSetOf<String>()

    val client: Minecraft = Minecraft.getInstance()

    fun isOnDiamondfire(): Boolean {
        val serverEntry = client.currentServer ?: return false
        val address = serverEntry.ip.lowercase()
        return address.endsWith("diamondfire.games") || address == "mcdiamondfire.com" || address.contains("148.113.223.138")
    }

    fun fillPlotIds(ids: Set<String>) {
        plotIds.clear()
        plotHandles.clear()
        ids.forEach {
            it.toIntOrNull()?.let { i -> plotIds.add(i) } ?: plotHandles.add(it)
        }
    }

    fun requestPlotId() {
        if (onDiamondfire && !hidePlotRegex) {
            hidePlotRegex = true
            SendMessage.sendCommand("find ${client.user.name}")
        }
    }

    fun replaceButton(screen: PauseScreen) {
        val widgets = Screens.getWidgets(screen)
        if (onMaceRoulette) {
            val targetButton = widgets.find { widget ->
                widget is Button && widget.message == Component.translatable("gui.advancements")
            } as? Button
            if (targetButton != null) {
                val newButton = Button.builder(Component.literal("MRC Settings")) { _ ->
                    val configScreen = ConfigMenu.generateScreen(null)
                    client.gui.setScreen(configScreen)
                }.bounds(targetButton.x, targetButton.y, targetButton.width, targetButton.height).build()
                widgets.remove(targetButton)
                widgets.add(newButton)
            }
        }
    }

    fun registerServerAndPlotListeners() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            onMaceRoulette = false
            plotId = null
            plotHandle = null
            onDiamondfire = isOnDiamondfire()
            tps = -1f
            BountyManager.resetBountyData()
            EliminationManager.resetEliminationData()
            EventManager.resetEventData()
            StarFragmentManager.resetStarFragmentData()
            ConsumableManager.resetConsumableData()
            ShowdownManager.resetShowdownData()
            AccuracyManager.resetAccuracyData()
            RoundManager.resetRoundData()
            ModifierManager.resetModifierData()
            AchievementManager.clearAllTriggers()
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            onDiamondfire = false
            onMaceRoulette = false
            plotId = null
            plotHandle = null
            isStatless = false
        }
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true
            if (!text.contains('⏵') && !text.contains('→')) return@register true

            CompletableFuture.runAsync {
                if (patchPlotRegex.containsMatchIn(text)) requestPlotId()

                plotRegex.find(text)?.groups?.let {
                    plotId = it[1]?.value?.toIntOrNull()
                    plotHandle = it[2]?.value

                    onMaceRoulette = plotHandles.contains(plotHandle) || plotIds.contains(plotId)
                    isStatless = (plotId == 25000031 || plotHandle == "statless")
                    findPlayerCommandRegex.find(text)?.groups?.let {
                        val totalPlayersFound = it[1]?.value?.toIntOrNull() ?: -1
                        playersTotal = totalPlayersFound
                    }
                }
            }

            if (hidePlotRegex) {
                hidePlotRegex = false
                return@register false
            }

            return@register true
        }
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            if (screen is PauseScreen) replaceButton(screen)
        }
    }
}