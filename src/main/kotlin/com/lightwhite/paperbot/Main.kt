package com.lightwhite.paperbot

import com.lightwhite.paperbot.bot.impl.MainBot
import com.lightwhite.paperbot.manager.BotManager
import com.lightwhite.paperbot.manager.ConfigManager
import com.lightwhite.paperbot.utils.postAnnounce
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.system.exitProcess

fun main(): Unit = runBlocking {
    val config = ConfigManager.launchConfig
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
                    postAnnounce(
                        content,
                        *BotManager.botInstants.filterIsInstance<MainBot>().map { it.bot }.toTypedArray()
                    )
                }

                "help" -> {
                    logger.info(
                        """
                        exit: 退出程序
                        announce: 发布公告
                        help: 帮助
                        addadmin: 添加管理员
                    """.trimIndent()
                    )
                }

                "addadmin" -> {
                    logger.info("请输入用户ID：")
                    val id = readln().toLong()
                    ConfigManager.adminConfig.adminIds.add(id)
                    ConfigManager.saveConfig()
                    logger.info("添加成功")
                }
            }
        }
    }.start()

    val timer = Timer()

    timer.schedule(object : TimerTask() {
        override fun run() = ConfigManager.saveConfig()
    }, 5 * 60 * 1000, 5 * 60 * 1000)

    val thread = Thread {
        ConfigManager.saveConfig()
    }

    Runtime.getRuntime().addShutdownHook(thread)
}