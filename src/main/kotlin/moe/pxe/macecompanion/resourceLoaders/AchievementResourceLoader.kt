package moe.pxe.macecompanion.resourceLoaders

import com.google.gson.JsonParser
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config.areAchievementsShown
import moe.pxe.macecompanion.dataClasses.MRAchievement
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import java.nio.charset.StandardCharsets
import kotlin.collections.forEach

object AchievementResourceLoader : SimplePreparableReloadListener<Map<String, MRAchievement>>() {
    const val ACHIEVEMENT_DIRECTORY = "mace_roulette_companion/achievements"

    override fun prepare(resourceManager: ResourceManager, profiler: net.minecraft.util.profiling.ProfilerFiller): Map<String, MRAchievement> {
        val result = mutableMapOf<String, MRAchievement>()
        resourceManager.listResources(ACHIEVEMENT_DIRECTORY) { path ->
            path.path.endsWith(".json")
        }.forEach { (id, resource) ->
            resource.open().use { input ->
                val json = input.reader(StandardCharsets.UTF_8).use { JsonParser.parseReader(it) }
                val achievement = MRAchievement.fromJson(json)
                result[id.path] = achievement
            }
        }
        return result
    }

    fun refreshConfig() {
        val updated = areAchievementsShown.value.toMutableMap()
        achievements.forEach { (_, achievement) -> updated.putIfAbsent(achievement.id, false) }
        areAchievementsShown.value = updated
    }

    var forceStopRendering = false

    override fun apply(data: Map<String, MRAchievement>, resourceManager: ResourceManager, profiler: net.minecraft.util.profiling.ProfilerFiller) {
        achievements = data
        refreshConfig()
        forceStopRendering = true
    }

    private var achievements: Map<String, MRAchievement> = emptyMap()
    fun get(id: String): MRAchievement? = achievements[id]
    fun getAll(): Map<String, MRAchievement> = achievements
    fun getAllIdStrings(): List<String> = achievements.values.map { it.id }
}
