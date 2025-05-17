package com.lightwhite.paperbot.bot

import com.lightwhite.paperbot.config.launch.BotConfig

abstract class BotBuilder(private val clazz: Class<*>) {
    open fun isValidConfig(botConfig: BotConfig): Boolean {
        return clazz.isInstance(botConfig)
    }

    abstract suspend fun buildBot(config: BotConfig): PaperBot?
}