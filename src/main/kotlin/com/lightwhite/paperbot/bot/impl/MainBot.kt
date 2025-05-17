package com.lightwhite.paperbot.bot.impl

import com.lightwhite.paperbot.bot.BotBuilder
import com.lightwhite.paperbot.bot.PaperBot
import com.lightwhite.paperbot.config.launch.BotConfig
import com.lightwhite.paperbot.lastCommand
import com.lightwhite.paperbot.lastKey
import com.lightwhite.paperbot.logger
import com.lightwhite.paperbot.manager.BanManager
import com.lightwhite.paperbot.service.CommandParser
import com.lightwhite.paperbot.waiting
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.buildMessageChain

class MainBot(
    val bot: net.mamoe.mirai.Bot,
    val listenGroups: List<Long>
) : PaperBot("MainBot") {
    override suspend fun start() {
        bot.eventChannel.subscribeAlways<MessageEvent> {
            var rawMessage = it.message.contentToString()
            if (waiting) {
                waiting = false
                if (lastKey == rawMessage.toInt()) {
                    if (lastCommand != null) {
                        it.subject.sendMessage(lastCommand!!.first.execute(lastCommand!!.second, it, true))
                    }
                } else {
                    it.subject.sendMessage("动态秘钥错误")
                }
                lastCommand = null
                lastKey = 0
                return@subscribeAlways
            }
            if (it is GroupMessageEvent) {
                if (it.group.id !in listenGroups) return@subscribeAlways
                if (!rawMessage.startsWith("@${bot.id} /")) return@subscribeAlways
                rawMessage = rawMessage.substring(rawMessage.indexOf("/")).trim()
            }
            val (command, args) = CommandParser.parse(rawMessage)
            it.subject.sendMessage(command?.execute(args, it) ?: buildMessageChain { +"未知命令" })
        }

        bot.eventChannel.subscribeAlways<MemberJoinRequestEvent> {
            if (this.groupId in listenGroups) {
                if (it.fromId in BanManager.groupBannedUsers || it.fromId in BanManager.globalBannedUsers) {
                    this.reject()
                    return@subscribeAlways
                }
                this.accept()
            }
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