package com.lightwhite.paperbot.utils

import com.lightwhite.paperbot.bot.impl.MainBot
import com.lightwhite.paperbot.manager.BotManager
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.announcement.OfflineAnnouncement

fun postAnnounce(message: String, vararg bots: Bot) {
    for (bot in bots) {
        bot.groups.filter { group -> group.id in BotManager.botInstants.filterIsInstance<MainBot>().filter { it.bot == bot }.map { it.listenGroups }.flatten() }.forEach {
            if ((it.botPermission.level >= 1)) {
                runBlocking {
                    OfflineAnnouncement(message).publishTo(it)
                }
            }
        }
    }
}