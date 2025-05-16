package com.lightwhite.paperbot.bot

import com.lightwhite.paperbot.bot.impl.MainBot
import com.lightwhite.paperbot.config.BotConfig

object BotManager {
    private val botInstants = mutableListOf<PaperBot>()
    private val builders = listOf(
        MainBot.Builder()
    )

    suspend fun buildBot(botConfig: BotConfig): PaperBot? {
        return builders.firstNotNullOfOrNull {
            if (it.isValidConfig(botConfig)) {
                val bot = it.buildBot(botConfig) ?: return@firstNotNullOfOrNull null
                botInstants.add(bot)
                bot
            } else {
                null
            }
        }
    }
}