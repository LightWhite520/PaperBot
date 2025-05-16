package com.lightwhite.paperbot

import com.lightwhite.paperbot.bot.BotManager
import com.lightwhite.paperbot.config.Config
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import java.io.File

fun main(): Unit = runBlocking {
    val configFile = File("config/config.json")
    if (!configFile.exists()) {
        val config = Config.default()
        configFile.writeText(Serializer.encodeToString(config))
        logger.error("第一次启动，请填写配置文件！")
        return@runBlocking
    }

    val config = Serializer.decodeFromString<Config>(configFile.readText())
    awaitAll(
        *config.bots.map {
            async {
                BotManager.buildBot(it)?.start()
            }
        }.toTypedArray()
    )
}