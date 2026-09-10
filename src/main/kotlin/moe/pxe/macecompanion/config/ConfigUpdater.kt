package moe.pxe.macecompanion.config

import com.google.gson.*
import moe.pxe.macecompanion.MaceCompanion.Companion.LOGGER
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path

object ConfigUpdater {

    private val GSON = GsonBuilder().setPrettyPrinting().create()
    
    fun getConfigVersion(configFile: Path): String {
        try {
            val jsonString = Files.readString(configFile)
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            return if (jsonObject.has("configVersion")) jsonObject.get("configVersion").asString
            else "null"
        } catch (e: Exception) {
            e.printStackTrace()
            return "null"
        }
    }

    fun setConfigVersion(configFile: Path, version: String) {
        try {
            val jsonString = Files.readString(configFile)
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            if (jsonObject.has("configVersion")) jsonObject.remove("configVersion")
            jsonObject.addProperty("configVersion", version)
            val prettyJson = GSON.toJson(jsonObject)
            Files.writeString(configFile, prettyJson)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeEntry(configFile: Path, entry: String) {
        try {
            val jsonString = Files.readString(configFile)
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            if (jsonObject.has(entry)) jsonObject.remove(entry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateStringConfigEntry(configFile: Path, oldEntry: String, newEntry: String) {
        try {
            val jsonString = Files.readString(configFile)
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            if (jsonObject.has(oldEntry)) {
                val oldValue = jsonObject.get(oldEntry).asString
                jsonObject.remove(oldEntry)
                jsonObject.addProperty(newEntry, oldValue)
                val prettyJson = GSON.toJson(jsonObject)
                Files.writeString(configFile, prettyJson)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateBooleanConfigEntry(configFile: Path, oldEntry: String, newEntry: String) {
        try {
            val jsonString = Files.readString(configFile)
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            if (jsonObject.has(oldEntry)) {
                val oldValue = jsonObject.get(oldEntry).asBoolean
                jsonObject.remove(oldEntry)
                jsonObject.addProperty(newEntry, oldValue)
                val prettyJson = GSON.toJson(jsonObject)
                Files.writeString(configFile, prettyJson)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateIntConfigEntry(configFile: Path, oldEntry: String, newEntry: String) {
        try {
            val jsonString = Files.readString(configFile)
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            if (jsonObject.has(oldEntry)) {
                val oldValue = jsonObject.get(oldEntry).asInt
                jsonObject.remove(oldEntry)
                jsonObject.addProperty(newEntry, oldValue)
                val prettyJson = GSON.toJson(jsonObject)
                Files.writeString(configFile, prettyJson)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateConfig(configFile: Path) {
        try {
            LOGGER.info("Updating MRC Config...")

            removeEntry(configFile, "useFlint")
            updateBooleanConfigEntry(configFile, "useAccentColor", "useAccentColors")
            updateStringConfigEntry(configFile, "accentColor", "mainAccentColor")
            updateBooleanConfigEntry(configFile, "chanceUseColor", "maceChanceOverrideColors")
            updateBooleanConfigEntry(configFile, "showPlayerJoinToasts", "showPlayerToasts")
            removeEntry(configFile, "showMysteryModifiers")
            updateBooleanConfigEntry(configFile, "showModifierChargerToasts", "showConsumableToasts")
            updateStringConfigEntry(configFile, "hudLocation", "leftHudLocation")
            updateStringConfigEntry(configFile, "hudElements", "leftHudElements")
            updateBooleanConfigEntry(configFile, "overrideRoundColors", "roundNumberOverrideColors")
            updateStringConfigEntry(configFile, "roundNumberColor", "roundNumberNumberColor")
            updateStringConfigEntry(configFile, "roundTextColor", "roundNumberTextColor")
            updateBooleanConfigEntry(configFile, "overridePlayerCountColors", "playersAliveOverrideColors")
            updateStringConfigEntry(configFile, "alivePlayersColor", "playersAliveNumberColorAlive")
            updateStringConfigEntry(configFile, "totalPlayersColor", "playersAliveNumberColorTotal")
            updateStringConfigEntry(configFile, "playerCountTextColor", "playersAliveTextColor")
            updateBooleanConfigEntry(configFile, "overrideAccuracyColors", "accuracyOverrideColors")
            updateStringConfigEntry(configFile, "accuracyColor", "accuracyNumberColor")
            updateBooleanConfigEntry(configFile, "hideEliminationsWhenEliminated", "eliminationsHideWhenEliminated")
            updateBooleanConfigEntry(configFile, "overrideEliminationsColors", "eliminationsOverrideColors")
            updateBooleanConfigEntry(configFile, "hideStarFragmentsWhenEliminated", "starFragmentsHideWhenEliminated")
            updateBooleanConfigEntry(configFile, "overrideStarFragmentsColors", "starFragmentsOverrideColors")
            updateBooleanConfigEntry(configFile, "overridePlaytimeColors", "playtimeOverrideColors")
            updateStringConfigEntry(configFile, "playtimeColor", "playtimeNumberColor")
            updateIntConfigEntry(configFile, "boosterListMax", "modifiersMaxBoosters")
            updateBooleanConfigEntry(configFile, "overrideModifiersColors", "modifiersOverrideColors")
            updateStringConfigEntry(configFile, "normalModifierTextColor", "modifiersTextColorRegularModifier")
            updateStringConfigEntry(configFile, "eternalModifierTextColor", "modifiersTextColorEternalModifier")
            updateStringConfigEntry(configFile, "eternalModifierTextShadowColor", "modifiersShadowColorEternalModifier")
            updateStringConfigEntry(configFile, "chargedModifierTextColor", "modifiersTextColorChargedModifier")
            updateStringConfigEntry(configFile, "mysteryModifierTextColor", "modifiersTextColorMysteryModifier")
            updateBooleanConfigEntry(configFile, "customModifierIcons", "modifiersUseCustomModifierIcons")
            updateBooleanConfigEntry(configFile, "use2dHeads", "modifiersUse2dHeadIcons")
            updateBooleanConfigEntry(configFile, "hideMaceChanceWhenEliminated", "maceChanceHideWhenEliminated")
            updateBooleanConfigEntry(configFile, "overrideMaceChanceColors", "maceChanceOverrideColors")
            updateBooleanConfigEntry(configFile, "overrideBountyBoardColors", "bountyBoardOverrideColors")
            updateStringConfigEntry(configFile, "bountyBoardPlayerColor", "bountyBoardTextColorPlayer")
            updateBooleanConfigEntry(configFile, "overrideFpsColors", "fpsOverrideColors")
            updateBooleanConfigEntry(configFile, "overridePingColors", "pingOverrideColors")
            updateBooleanConfigEntry(configFile, "overrideTpsColors", "tpsOverrideColors")

            setConfigVersion(configFile, "0.4.0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateConfig() {
        val configFile = FabricLoader.getInstance().configDir.resolve("mrc.json")
        if (!Files.exists(configFile)) return
        val version = getConfigVersion(configFile)
        if(version != "0.4.0") updateConfig(configFile)
    }

}