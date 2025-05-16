package com.lightwhite.paperbot.bot.impl

import com.lightwhite.paperbot.bot.BotBuilder
import com.lightwhite.paperbot.bot.PaperBot
import com.lightwhite.paperbot.config.BotConfig
import com.lightwhite.paperbot.logger
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.UserMessageEvent

class MainBot(
    private val bot: net.mamoe.mirai.Bot,
    private val listenGroups: List<Long>
): PaperBot("MainBot") {
    override suspend fun start() {
        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            if (listenGroups.contains(this.group.id)) {
                logger.info("收到来自QQ群${this.group.id}的消息：${this.message}")
            }
        }

        bot.eventChannel.subscribeAlways<UserMessageEvent> {
            logger.info("收到来自QQ好友${this.sender.remarkOrNick}的消息：${this.message}")
        }
    }

    class Builder : BotBuilder(BotConfig::class.java) {
        override suspend fun buildBot(config: BotConfig): PaperBot? {
            logger.info("初始化QQ机器人中...")
            val botBuilder = top.mrxiaom.overflow.BotBuilder.positive(config.url)
            if (config.token.isNotEmpty()) {
                botBuilder.token(config.token)
            }
            val botInstant = botBuilder.connect()
            if (botInstant == null) {
                logger.error("初始化QQ机器人失败！")
                return null
            }
            logger.info("初始化QQ机器人成功！")
            return MainBot(botInstant, config.listenGroups)
        }
    }
}