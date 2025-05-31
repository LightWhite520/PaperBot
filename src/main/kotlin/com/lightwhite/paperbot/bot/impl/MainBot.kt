package com.lightwhite.paperbot.bot.impl

import LightAppContactCard
import com.lightwhite.paperbot.*
import com.lightwhite.paperbot.bot.BotBuilder
import com.lightwhite.paperbot.bot.PaperBot
import com.lightwhite.paperbot.config.launch.BotConfig
import com.lightwhite.paperbot.manager.BanManager
import com.lightwhite.paperbot.manager.VerifyManager
import com.lightwhite.paperbot.service.CommandParser
import kotlinx.serialization.json.Json
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content

class MainBot(
    val bot: net.mamoe.mirai.Bot,
    val listenGroups: List<Long>
) : PaperBot("MainBot") {
    override suspend fun start() {
        bot.eventChannel.subscribeAlways<MessageEvent> {
            var rawMessage = it.message.contentToString()
            if (waiting && it.sender == invoker) {
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
                invoker = null
                return@subscribeAlways
            }

            if (it is GroupMessageEvent && VerifyManager.isVerifying(it.sender, it.group)) {
                val code = try {
                    rawMessage.toInt()
                } catch (_: Exception) {
                    -1
                }
                VerifyManager.verify(it.sender, it.group, code)
            }

            if (it is GroupMessageEvent) {
                if (it.group.id !in listenGroups) return@subscribeAlways
                val content = it.message.content
                if (content.isNotEmpty() && isJsonValid(content)) {
                    try {
                        val json = Json { ignoreUnknownKeys = true }
                        val card = json.decodeFromString<LightAppContactCard>(content)

                        if (card.view == "contact") {
                            val contact = card.meta.contact
                            logger.info("检测到群推荐卡片：${contact.nickname} - ${contact.tag}")
                            logger.info("加群链接：${contact.jumpUrl}")

                            BanManager.ban(it.sender)
                            it.message.recall()
                        }
                    } catch (e: Exception) {
                        logger.error("解析群推荐卡片失败", e)
                    }
                }

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
            }
        }

        bot.eventChannel.subscribeAlways<MemberJoinEvent> {
            if (this.groupId in listenGroups) VerifyManager.verify(it.user, it.group)
        }
    }

    private fun isJsonValid(jsonString: String): Boolean {
        return try {
            val element = Json.parseToJsonElement(jsonString)
            when (element) {
                is kotlinx.serialization.json.JsonObject,
                is kotlinx.serialization.json.JsonArray -> true
                else -> false
            }
        } catch (e: Exception) {
            false
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