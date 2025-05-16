package com.lightwhite.paperbot

import com.lightwhite.paperbot.bot.BotManager
import com.lightwhite.paperbot.bot.impl.MainBot
import com.lightwhite.paperbot.config.Config
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import net.mamoe.mirai.contact.announcement.OfflineAnnouncement
import java.io.File
import java.util.*
import kotlin.system.exitProcess

fun main(): Unit = runBlocking {
    val configFile = File("config/config.json")
    if (!configFile.exists()) {
        val config = Config.default()
        configFile.parentFile.mkdirs()
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
    Thread {//终端
        while (true) {
            val input = readlnOrNull()?.trim()?.lowercase(Locale.getDefault()) ?: continue
            when (input) {
                "exit" -> {
                    exitProcess(0)
                }

                "announce" -> {
                    logger.info("请输入公告内容：")
                    val content = readln()
                    BotManager.botInstants.forEach { bot ->
                        (bot as MainBot).bot.groups.filter { it.id in bot.listenGroups }.forEach {
                            println(it.name)
                            if ((it.botPermission.level >= 1)) {
                                runBlocking {
                                    OfflineAnnouncement(content).publishTo(it)
                                }
                            }
                        }
                    }
                }

                "help" -> {
                    logger.info("""
                        exit: 退出程序
                        announce: 发布公告
                        help: 帮助
                    """.trimIndent())
                }
            }
        }
    }.start()
}