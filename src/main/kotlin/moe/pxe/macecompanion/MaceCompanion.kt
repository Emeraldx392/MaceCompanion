package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.ConfigUpdater
import moe.pxe.macecompanion.stateManagers.AccuracyManager
import moe.pxe.macecompanion.stateManagers.AchievementManager
import moe.pxe.macecompanion.stateManagers.BountyManager
import moe.pxe.macecompanion.stateManagers.ConsumableManager
import moe.pxe.macecompanion.stateManagers.EliminationManager
import moe.pxe.macecompanion.stateManagers.EventManager
import moe.pxe.macecompanion.stateManagers.ModifierManager
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager
import moe.pxe.macecompanion.stateManagers.PlayersOnlineManager
import moe.pxe.macecompanion.stateManagers.PlotManager
import moe.pxe.macecompanion.stateManagers.ResourcePackManager
import moe.pxe.macecompanion.stateManagers.RoundManager
import moe.pxe.macecompanion.stateManagers.ShowdownManager
import moe.pxe.macecompanion.stateManagers.StarFragmentManager
import moe.pxe.macecompanion.stateManagers.TimeManager
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory

class MaceCompanion : ModInitializer {
    companion object {
        const val MOD_ID = "macecompanion"
        val LOGGER = LoggerFactory.getLogger(MOD_ID)!!
        const val DEBUG_MODE = false
    }
    override fun onInitialize() {
        LOGGER.info("Initializing Mace Roulette Companion...")
        if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
            LOGGER.error("Mace Roulette Companion is not a server-side mod!")
            return
        }
        ConfigUpdater.updateConfig()
        if (!Config.loadFromFile()) Config.saveToFile()
        CustomKeybinds.registerKeybinds()
        PlotManager.fillPlotIds(Config.plotIds.value.toSet())
        PlotManager.registerServerAndPlotListeners()
        BountyManager.registerBountyListeners()
        EliminationManager.registerEliminationListeners()
        EventManager.registerEventListeners()
        StarFragmentManager.registerStarFragmentListeners()
        ConsumableManager.registerConsumableListeners()
        PerformanceStatsManager.registerPerformanceStatsListeners()
        ShowdownManager.registerShowdownListeners()
        PlayersOnlineManager.registerPlayersOnlineListeners()
        AccuracyManager.registerAccuracyListeners()
        RoundManager.registerRoundListeners()
        ModifierManager.registerModifierListeners()
        AchievementManager.registerAchievementListeners()
        TimeManager.registerTimeListeners()
        ResourcePackManager.registerResourcePackListeners()
        SendMessage.registerTickListener()
        HideGLGG.registerListener()
        RoundInfoHud.registerListener()
    }

}