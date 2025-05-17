package com.lightwhite.paperbot.manager

import com.lightwhite.paperbot.bot.PaperBot
import com.lightwhite.paperbot.bot.impl.MainBot
import com.lightwhite.paperbot.config.launch.BotConfig

object BotManager {
    val botInstants = mutableListOf<PaperBot>()
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