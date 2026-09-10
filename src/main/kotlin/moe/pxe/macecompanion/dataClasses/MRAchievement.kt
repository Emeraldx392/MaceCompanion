package moe.pxe.macecompanion.dataClasses

import moe.pxe.macecompanion.enums.AchievementLostTriggers
import net.minecraft.resources.Identifier

data class MRAchievement(val id: String, val onItemModel: Identifier, val offItemModel: Identifier, val lossTriggers: Map<AchievementLostTriggers, Int>, val resetCondition: String, val requirement: String? = null) {
    companion object {
        fun fromJson(json: com.google.gson.JsonElement): MRAchievement {
            val obj = json.asJsonObject
            val lossTriggerElement = obj.get("loss_triggers")
            val parsedLossTriggers = when {
                lossTriggerElement.isJsonArray -> {
                    lossTriggerElement.asJsonArray.associate { element ->
                        when {
                            element.isJsonObject -> {
                                val elementObj = element.asJsonObject
                                val trigger = AchievementLostTriggers.valueOf(elementObj.get("trigger").asString.uppercase())
                                val amount = elementObj.get("amount").asInt
                                trigger to amount
                            }
                            else -> AchievementLostTriggers.valueOf(element.asString.uppercase()) to 1
                        }
                    }
                }
                lossTriggerElement.isJsonObject -> {
                    val jsonObject = lossTriggerElement.asJsonObject
                    mapOf(AchievementLostTriggers.valueOf(jsonObject.get("trigger").asString.uppercase()) to jsonObject.get("amount").asInt)
                }
                else -> mapOf(AchievementLostTriggers.valueOf(lossTriggerElement.asString.uppercase()) to 1)
            }
            return MRAchievement(
                id = obj.get("id").asString,
                onItemModel = Identifier.parse(obj.get("on_item_model").asString),
                offItemModel = Identifier.parse(obj.get("off_item_model").asString),
                lossTriggers = parsedLossTriggers,
                resetCondition = obj.get("reset_condition").asString,
                requirement = obj.get("requirement")?.takeIf { !it.isJsonNull }?.asString
            )
        }
    }
}