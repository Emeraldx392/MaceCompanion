package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.stateManagers.AchievementManager.addRequirement
import moe.pxe.macecompanion.stateManagers.AchievementManager.clearTriggersForResetCondition
import moe.pxe.macecompanion.stateManagers.AchievementManager.removeRequirement
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminated
import moe.pxe.macecompanion.stateManagers.RoundManager.round
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.item.Items

object AccuracyManager {
    var maceAttempts = mutableMapOf<Int, Boolean>()
    var hasMace: Boolean = false
    var hadMaceLastTick: Boolean = false
    var hasWindCharge: Boolean = false
    var lastRoundWithMace: Int = -1

    fun resetAccuracyData(){
        maceAttempts = mutableMapOf()
        hasMace = false
        lastRoundWithMace = -1
    }
    fun registerAccuracyListeners(){
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            hasMace = (!eliminated && (client.player?.inventory?.hasAnyOf(setOf(Items.MACE)) ?: false))
            hasWindCharge = (!eliminated && (client.player?.inventory?.hasAnyOf(setOf(Items.WIND_CHARGE)) ?: false))
            if (hasMace) {
                addRequirement("mace")
                lastRoundWithMace = round
                if (!(maceAttempts[round] ?: false) && !hasWindCharge) maceAttempts[round] = false
                if(!hadMaceLastTick) clearTriggersForResetCondition("mace")
            }else removeRequirement("mace")
            if(hasMace != hadMaceLastTick) hadMaceLastTick = hasMace

        })
    }
}