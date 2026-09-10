package moe.pxe.macecompanion.stateManagers

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.dataClasses.MRAchievement
import moe.pxe.macecompanion.enums.AchievementLostTriggers
import moe.pxe.macecompanion.enums.AchievementLostTriggers.*
import moe.pxe.macecompanion.resourceLoaders.AchievementResourceLoader
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminated
import moe.pxe.macecompanion.stateManagers.PlotManager.isStatless
import moe.pxe.macecompanion.stateManagers.PlotManager.onMaceRoulette
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.client.Minecraft
import net.minecraft.client.player.ClientInput
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import java.util.concurrent.CompletableFuture

object AchievementManager {
    val achievementRegex = Regex("""⏵ (.+) earned the (.+) achievement!""")

    val player = Minecraft.getInstance().player

    var possibleAchievements: MutableList<MRAchievement> = mutableListOf()
    var achievementLostTriggers: MutableMap<String, MutableMap<AchievementLostTriggers, Int>> = mutableMapOf(
        "round" to mutableMapOf(), "game" to mutableMapOf(), "mace" to mutableMapOf()
    )
    var requirements: MutableList<String> = mutableListOf()

    fun updateAllAchievements() {
        possibleAchievements.clear()
        val allAch = AchievementResourceLoader.getAll().values
        possibleAchievements = allAch.filter { achievement -> achievement.lossTriggers.all { trigger -> getAmountTriggered(achievement.resetCondition, trigger.key) < trigger.value } && hasRequirement(achievement) }.toMutableList()
    }

    fun getAmountTriggered(resetCondition: String, achievementLostTrigger: AchievementLostTriggers): Int {
        return achievementLostTriggers[resetCondition]?.get(achievementLostTrigger) ?: 0
    }

    fun hasRequirement(ach: MRAchievement): Boolean {
        return ach.requirement == null || requirements.contains(ach.requirement)
    }

    fun addRequirement(reqirement: String) {
        if(!requirements.contains(reqirement)) requirements.addLast(reqirement)
        updateAllAchievements()
    }

    fun removeRequirement(reqirement: String) {
        if(requirements.contains(reqirement)) requirements.remove(reqirement)
        updateAllAchievements()
    }

    fun addAchievementLostTrigger(achievementLostTrigger: AchievementLostTriggers) {
        listOf("game", "round", "mace").forEach { resetCondition ->
            val innerMap = achievementLostTriggers.getOrPut(resetCondition) { mutableMapOf() }
            innerMap[achievementLostTrigger] = (innerMap[achievementLostTrigger] ?: 0) + 1
        }
        updateAllAchievements()
    }

    fun clearAllTriggers() {
        achievementLostTriggers.clear()
        achievementLostTriggers = mutableMapOf(
            "round" to mutableMapOf(), "game" to mutableMapOf(), "mace" to mutableMapOf()
        )
        updateAllAchievements()
    }

    fun clearTriggersForResetCondition(resetCondition: String) {
        achievementLostTriggers[resetCondition] = mutableMapOf()
        updateAllAchievements()
    }

    var lastTarget: LivingEntity? = null
    var timesDealtDamage = 0
    var wasOnGround = player?.onGround() ?: false

    fun registerAchievementListeners() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            val player: LocalPlayer = client.player ?: return@EndTick
            if (!onMaceRoulette || eliminated) return@EndTick

            if (player.xRot <= 0) addAchievementLostTrigger(LOOK_ABOVE_HORIZON)

            val input: ClientInput = player.input
            val isMoving = player.x != player.xOld || player.z != player.zOld

            val isPressingForward = input.hasForwardImpulse()
            if (isPressingForward && isMoving) addAchievementLostTrigger(WALK_FORWARD)

            val isPressingBackwards = input.moveVector.y < -1.0E-5f
            if (isPressingBackwards && isMoving) addAchievementLostTrigger(WALK_BACKWARDS)

            val isPressingLeft = input.moveVector.x < -1.0E-5f
            if (isPressingLeft && isMoving) addAchievementLostTrigger(WALK_LEFT)

            val isPressingRight = input.moveVector.x > 1.0E-5f
            if (isPressingRight && isMoving) addAchievementLostTrigger(WALK_RIGHT)

            if (player.isCrouching) addAchievementLostTrigger(CROUCH)

            val isOnGround = player.onGround()
            if (client.options.keyJump.isDown && wasOnGround && !isOnGround && player.deltaMovement.y > 0.0) {
                addAchievementLostTrigger(JUMP)
            }
            wasOnGround = isOnGround
        })
        AttackEntityCallback.EVENT.register { _, _, _, entity, _ ->
            if (entity is LivingEntity) lastTarget = entity
            InteractionResult.PASS
        }
        ClientReceiveMessageEvents.GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register
            if (!onMaceRoulette || isStatless) return@register
            if(!text.startsWith("⏵")) return@register

            val clientPlayerName = player?.name?.string ?: "null"

            CompletableFuture.runAsync {
                achievementRegex.find(text)?.groups?.let {
                    val playerName = it[1]?.value ?: "null"
                    val rawName = it[2]?.value ?: "null"
                    val achId = rawName.lowercase().replace(" ", "_").replace(",", "")
                    if(playerName == clientPlayerName){
                        synchronized(Config.areAchievementsShown) {
                            if (Config.areAchievementsShown.value[achId] != null) {
                                Config.areAchievementsShown.value[achId] = false
                                Config.saveToFileAndRefreshRendering()
                            }
                        }
                    }
                }
            }
        }
    }
}