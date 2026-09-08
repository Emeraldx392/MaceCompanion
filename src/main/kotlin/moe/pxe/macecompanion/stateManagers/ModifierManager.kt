package moe.pxe.macecompanion.stateManagers

import com.mojang.authlib.GameProfile
import moe.pxe.macecompanion.enums.Modifiers
import moe.pxe.macecompanion.stateManagers.RoundManager.updateMaceChance
import moe.pxe.macecompanion.util.PlayerProfile.getPlayerProfile
import moe.pxe.macecompanion.util.TextUtils.messageToJsonString
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import kotlin.text.Regex

object ModifierManager {
    var checkingModifiers = false
    var mysteryAmount = 0
    var modifiers = mutableMapOf<Modifiers, Boolean>()
    var eternalModifier: Modifiers? = null
    var modifierBoosters = mutableMapOf<Modifiers, MutableList<GameProfile>>()

    val chatModifierHeaderRegex = Regex("""⏵(.+)ᴍᴏᴅɪꜰɪᴇʀ:""")
    val chatModifierItemRegex = Regex("""\s+◇ .+""")
    val chatModifierBoostedRegex = Regex("""\s+◇ .+ \(☁ Boosted by (.+)\)""")
    val chatModifierReallyBoostedRegex = Regex("""\s+◇ .+ \(☁ Boosted by .+, .+, and .+ others\)""")

    const val ETERNAL_MODIFIER_TEXTURE = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFjNWQ3NjZjODQwMWM5NTY2Y2E1MDhhYTNkMjU0NDQwYjg4YjIxZjU5MGI1MWVjMTVjNGE5ZDk4YjE4OWMzZiJ9fX0="
    const val CHARGED_MODIFIER_TEXTURE = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc1Mzg2MDAwNWQzNGRkNTMwMmRhNWVmOTA1Y2Q3ODFhYzcxNDFkMjJhYmMxZGIzOWMzMWJhMmZlM2M2ODRiZCJ9fX0="
    const val MYSTERY_MODIFIER_TEXTURE = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzlkODliMGJmNmY2NjU1YWJjMGFlY2NjY2Q2YTE4OGQwZWNjMzY2YTRiNWU2ZDFmZTJhM2ExY2U1MWYzMGU4YSJ9fX0="

    val client: Minecraft = Minecraft.getInstance()

    fun resetModifierData() {
        checkingModifiers = false
        mysteryAmount = 0
        modifiers.clear()
        eternalModifier = null
        modifierBoosters.clear()
    }

    fun getHover(text: Component, textToSkip: String): String {
        val hover = text.style.hoverEvent
        if (hover != null && hover.action() == HoverEvent.Action.SHOW_TEXT) {
            val showText = hover as? HoverEvent.ShowText
            val content: Component? = showText?.value
            val readableText = content?.string
            if (readableText != null && !readableText.contains(textToSkip)) return readableText
        }
        for (sibling in text.siblings) {
            val found = getHover(sibling, textToSkip)
            if(found.contains(textToSkip)) return "null"
            if (found != "null") return found
        }
        return "null"
    }

    fun isModifierChargedFromMessage(message: Component): Boolean {
        val jsonString = messageToJsonString(message)
        return (jsonString.contains(CHARGED_MODIFIER_TEXTURE))
    }

    fun isModifierEternalFromMessage(message: Component): Boolean {
        val jsonString = messageToJsonString(message)
        return (jsonString.contains(ETERNAL_MODIFIER_TEXTURE))
    }

    fun getModifierFromMessage(message: Component): Modifiers {
        val jsonString = messageToJsonString(message)
        if (jsonString.contains(MYSTERY_MODIFIER_TEXTURE)){
            mysteryAmount++
            return Modifiers.MYSTERY
        }
        Modifiers.entries.forEach { modifier ->
            if (jsonString.contains(modifier.matchName)) return modifier
        }
        return Modifiers.UNKNOWN
    }

    fun registerModifierListeners() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true
            if (!PlotManager.onMaceRoulette) return@register true

            val hasTriangle = text.startsWith("⏵")
            val hasRotatedSquare = text.contains("◇")

            if (!hasTriangle && !hasRotatedSquare) return@register true

            if (checkingModifiers) {
                val reallyBoostedMatch = chatModifierReallyBoostedRegex.matchEntire(text)
                val boostedMatch = if(reallyBoostedMatch == null) chatModifierBoostedRegex.matchEntire(text) else null
                val modMatch = if(boostedMatch == null && reallyBoostedMatch == null) chatModifierItemRegex.matchEntire(text) else null

                val isReallyBoosted = reallyBoostedMatch != null
                val isBoosted = boostedMatch != null
                val isMod = modMatch != null

                if (isReallyBoosted || isBoosted || isMod) {
                    val modifier = getModifierFromMessage(message)
                    if (isModifierEternalFromMessage(message)) eternalModifier = modifier
                    modifiers[modifier] = isModifierChargedFromMessage(message)
                    val playerNames = when {
                        isReallyBoosted -> {
                            val hoverString = getHover(message, modifier.matchName).replace("§r", "")
                            hoverString.split(", ")
                        }
                        isBoosted -> boostedMatch.groups[1]?.value?.split(", ")
                        else -> null
                    }
                    if (playerNames != null) modifierBoosters[modifier] = playerNames.mapNotNull { getPlayerProfile(it) }.toMutableList()
                    updateMaceChance()
                } else checkingModifiers = false
            }
            if (hasTriangle) chatModifierHeaderRegex.matchEntire(text)?.groups[1]?.let {
                checkingModifiers = true
            }
            return@register true
        }
    }
}