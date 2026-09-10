package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.ConfigMenu
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import moe.pxe.macecompanion.stateManagers.PlotManager
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.resources.Identifier

object CustomKeybinds {
    lateinit var openProfileKeyBinding: KeyMapping
        private set
    lateinit var openCosmeticsKeyBinding: KeyMapping
        private set
    lateinit var openEventsKeyBinding: KeyMapping
        private set
    lateinit var openStarRoadKeyBinding: KeyMapping
        private set
    lateinit var openMidasMondayKeyBinding: KeyMapping
        private set
    lateinit var openLeaderboardKeyBinding: KeyMapping
        private set
    lateinit var openModOptionsKeyBinding: KeyMapping
        private set
    lateinit var toggleHudKeyBinding: KeyMapping
        private set
    lateinit var toggleAutoGLKeyBinding: KeyMapping
        private set
    lateinit var toggleAutoGGKeyBinding: KeyMapping
        private set

    fun registerKeybinds() {
        MaceCompanion.LOGGER.info("Registering mod keybinds")

        val maceRouletteCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "title"))

        openProfileKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.open_profile",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openCosmeticsKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.open_cosmetics",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openEventsKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.open_events",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openStarRoadKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.open_star_road",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openMidasMondayKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.open_midas_monday",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openLeaderboardKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.open_leaderboard",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openModOptionsKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.open_mod_options",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        toggleHudKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.toggle_hud",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        toggleAutoGLKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.toggle_auto_gl",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        toggleAutoGGKeyBinding = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.macecompanion.toggle_auto_gg",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (openProfileKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette && !PlotManager.isStatless) SendMessage.sendMessage("@profile")
            }
            while (openCosmeticsKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette && !PlotManager.isStatless) SendMessage.sendMessage("@cosmetics")
            }
            while (openEventsKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette && !PlotManager.isStatless) SendMessage.sendMessage("@events")
            }
            while (openStarRoadKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette && !PlotManager.isStatless) SendMessage.sendMessage("@starroad")
            }
            while (openMidasMondayKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette && !PlotManager.isStatless) SendMessage.sendMessage("@midas")
            }
            while (openLeaderboardKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette && !PlotManager.isStatless) SendMessage.sendMessage("@lb")
            }
            while (openModOptionsKeyBinding.consumeClick()) {
                if (client.gui.screen() == null) {
                    val configScreen = ConfigMenu.generateScreen(null)
                    client.gui.setScreen(configScreen)
                }
            }
            while (toggleHudKeyBinding.consumeClick()) {
                val newValue = !Config.displayHud.value
                Config.displayHud.set(newValue)
                Config.saveToFile()
            }
            while (toggleAutoGLKeyBinding.consumeClick()) {
                val newValue = !Config.useAutoGL.value
                Config.useAutoGL.set(newValue)
                Config.saveToFile()
                if(newValue) client.player?.sendSystemMessage(Component.literal("Auto GL enabled!").withColor(CommonColors.GREEN))
                if(!newValue) client.player?.sendSystemMessage(Component.literal("Auto GL disabled!").withColor(CommonColors.RED))
            }
            while (toggleAutoGGKeyBinding.consumeClick()) {
                val newValue = !Config.useAutoGG.value
                Config.useAutoGG.set(newValue)
                Config.saveToFile()
                if(newValue) client.player?.sendSystemMessage(Component.literal("Auto GG enabled!").withColor(CommonColors.GREEN))
                if(!newValue) client.player?.sendSystemMessage(Component.literal("Auto GG disabled!").withColor(CommonColors.RED))
            }
        }
    }
}