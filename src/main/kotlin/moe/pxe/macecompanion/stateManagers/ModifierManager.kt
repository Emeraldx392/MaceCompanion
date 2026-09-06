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
    var modifiersToCheck = -1
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
        modifiersToCheck = -1
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

            if (hasTriangle) chatModifierHeaderRegex.matchEntire(text)?.groups[1]?.let {
                when (it.value) {
                    " " -> modifiersToCheck = 1
                    " ᴅᴏᴜʙʟᴇ " -> modifiersToCheck = 2
                    " ᴛʀɪᴘʟᴇ " -> modifiersToCheck = 3
                    " ᴄʜᴀᴏꜱ " -> modifiersToCheck = 5
                    " ᴍᴀʏʜᴇᴍ " -> modifiersToCheck = 7
                    " ᴅᴏᴏᴍꜱᴅᴀʏ " -> modifiersToCheck = 8
                }
            }
            if (modifiersToCheck > 0) {
                val reallyBoostedMatch = chatModifierReallyBoostedRegex.matchEntire(text)
                val boostedMatch = if(reallyBoostedMatch == null) chatModifierBoostedRegex.matchEntire(text) else null
                val modMatch = if(boostedMatch == null && reallyBoostedMatch == null) chatModifierItemRegex.matchEntire(text) else null
                when {
                    reallyBoostedMatch != null -> {
                        val modifier = getModifierFromMessage(message)
                        val hoverString = getHover(message, modifier.matchName).replace("§r", "")
                        val playerNames = hoverString.split(", ")
                        modifierBoosters[modifier] = mutableListOf()
                        if (isModifierEternalFromMessage(message)) eternalModifier = modifier
                        modifiers[modifier] = isModifierChargedFromMessage(message)
                        for (player in playerNames) {
                            getPlayerProfile(player)?.let { profile ->
                                modifierBoosters[modifier]?.add(profile)
                            }
                        }
                        modifiersToCheck--
                        if (modifiersToCheck < 1) updateMaceChance()
                    }

                    boostedMatch != null -> {
                        boostedMatch.groups[1]?.let {
                            val playerNames = it.value.split(", ")
                            val modifier = getModifierFromMessage(message)
                            modifierBoosters[modifier] = mutableListOf()
                            if (isModifierEternalFromMessage(message)) eternalModifier = modifier
                            modifiers[modifier] = isModifierChargedFromMessage(message)
                            for (player in playerNames) {
                                getPlayerProfile(player)?.let { profile ->
                                    modifierBoosters[modifier]?.add(profile)
                                }
                            }
                        }
                        modifiersToCheck--
                        if (modifiersToCheck < 1) updateMaceChance()
                    }

                    modMatch != null -> {
                        val modifier = getModifierFromMessage(message)
                        if (isModifierEternalFromMessage(message)) eternalModifier = modifier
                        modifiers[modifier] = isModifierChargedFromMessage(message)
                        modifiersToCheck--
                        if (modifiersToCheck < 1) updateMaceChance()
                    }
                }
            }

            return@register true
        }

    }
}