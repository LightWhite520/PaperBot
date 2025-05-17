package com.lightwhite.paperbot.command

import com.lightwhite.paperbot.*
import com.lightwhite.paperbot.bot.impl.MainBot
import com.lightwhite.paperbot.manager.BanManager
import com.lightwhite.paperbot.manager.BotManager
import com.lightwhite.paperbot.manager.ConfigManager
import com.lightwhite.paperbot.service.CommandParser
import com.lightwhite.paperbot.service.PermissionService
import com.lightwhite.paperbot.utils.postAnnounce
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.UserMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.buildMessageChain
import java.time.LocalTime

object BanCommand : Command("ban") {
    override suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean): Message {
        if (event is GroupMessageEvent) {
            if (PermissionService.checkAdmin(event) || sudo) {
                if (args.isEmpty()) {
                    return buildMessageChain { +"缺少参数" }
                }
                val userId = args[0].toLong()
                val user = event.group.getMember(userId)
                if (user != null) {
                    BanManager.groupBannedUsers[event.group.id]?.add(userId)
                        ?: BanManager.groupBannedUsers.put(event.group.id, mutableListOf(userId))
                    event.bot.groups.forEach { group ->
                        if (group.botPermission.level >= 1) {
                            user.kick("You are temporarily banned for 359d 23h 59m 59s from this server!")
                        }
                    }
                    return buildMessageChain { +"封禁成功" }
                }
                return buildMessageChain { +"用户不存在" }
            } else {
                return buildMessageChain { +"权限不足" }
            }
        }
        if (event is UserMessageEvent) {
            if (PermissionService.isAdmin(event.sender.id) || sudo) {
                if (args.isEmpty()) {
                    return buildMessageChain { +"缺少参数" }
                }
                val userId = args[0].toLong()
                BanManager.globalBannedUsers.add(userId)
                event.bot.groups.forEach { group ->
                    if (group.botPermission.level >= 1) {
                        group.getMember(userId)?.kick("You are temporarily banned for 359d 23h 59m 59s from this server!")
                    }
                }
                return buildMessageChain { +"封禁成功" }
            } else {
                return buildMessageChain { +"权限不足" }
            }
        }
        return buildMessageChain { +"未知错误" }
    }
}

object UnbanCommand : Command("unban") {
    override suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean): Message {
        if (event is GroupMessageEvent) {
            if (PermissionService.checkAdmin(event) || sudo) {
                if (args.isEmpty()) {
                    return buildMessageChain { +"缺少参数" }
                }
                val userId = args[0].toLong()
                val user = event.group.getMember(userId)
                if (user != null) {
                    BanManager.groupBannedUsers[event.group.id]?.remove(userId)
                    return buildMessageChain { +"解封成功" }
                }
                return buildMessageChain { +"用户不存在" }
            } else {
                return buildMessageChain { +"权限不足" }
            }
        }
        if (event is UserMessageEvent) {
            if (PermissionService.isAdmin(event.sender.id) || sudo) {
                if (args.isEmpty()) {
                    return buildMessageChain { +"缺少参数" }
                }
                val userId = args[0].toLong()
                BanManager.globalBannedUsers.remove(userId)
                return buildMessageChain { +"解封成功" }
            } else {
                return buildMessageChain { +"权限不足" }
            }
        }
        return buildMessageChain { +"未知错误" }
    }
}

object AddAdminCommand : Command("addadmin") {
    override suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean): Message {
        if (event is GroupMessageEvent) {
            if (PermissionService.checkAdmin(event) || sudo) {
                if (args.isEmpty()) {
                    return buildMessageChain { +"缺少参数" }
                }
                val userId = args[0].toLong()
                val user = event.group.getMember(userId)
                if (user != null) {
                    ConfigManager.adminConfig.adminIds.add(userId)
                    val timeGreeting = when (LocalTime.now().hour) {
                        in 5..10 -> "早上好喵~"
                        in 11..16 -> "午安喵！"
                        in 17..21 -> "晚上好哟~"
                        else -> "深夜了呢...（揉眼睛）"
                    }

                    val catAction = listOf(
                        "(*^ω^*) 轻轻蹭了蹭你的手心",
                        "尾巴缠上你的手腕～",
                        "用肉垫拍了拍你的头像",
                        "(=ↀωↀ=)✧ 盯着你眨了眨眼"
                    ).random()


                    val response = """
                    $timeGreeting 终于找到主人啦喵！
                    $catAction
                    请多给小喵下达指令吧～ 
                    「想要小喵摆出更多姿势请用『帮助』命令调教喵~（尾巴轻轻扫过屏幕）」
                    """.trimIndent()
                    return buildMessageChain {
                        +"添加成功\n"
                        +At(user)
                        +response
                    }
                }
                return buildMessageChain {
                    +"用户不存在"
                }
            } else {
                return buildMessageChain {
                    +"权限不足"
                }
            }
        }
        if (event is UserMessageEvent) {
            if (PermissionService.isAdmin(event.sender.id) || sudo) {
                if (args.isEmpty()) {
                    return buildMessageChain {
                        +"缺少参数"
                    }
                }
                val userId = args[0].toLong()
                ConfigManager.adminConfig.adminIds.add(userId)
                return buildMessageChain {
                    +"添加成功"
                }
            } else {
                return buildMessageChain {
                    +"权限不足"
                }
            }
        }
        return buildMessageChain {
            +"未知错误"
        }
    }
}

object RemoveAdminCommand : Command("removeadmin") {
    override suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean): Message {
        if (event is GroupMessageEvent) {
            if (PermissionService.checkAdmin(event) || sudo) {
                if (args.isEmpty()) {
                    return buildMessageChain {
                        +"缺少参数"
                    }
                }
                val userId = args[0].toLong()
                val user = event.group.getMember(userId)
                if (user != null) {
                    ConfigManager.adminConfig.adminIds.remove(userId)
                    return buildMessageChain {
                        +"移除成功"
                    }
                }
                return buildMessageChain {
                    +"用户不存在"
                }
            } else {
                return buildMessageChain {
                    +"权限不足"
                }
            }
        }
        if (event is UserMessageEvent) {
            if (PermissionService.isAdmin(event.sender.id) || sudo) {
                if (args.isEmpty()) {
                    return buildMessageChain {
                        +"缺少参数"
                    }
                }
                val userId = args[0].toLong()
                ConfigManager.adminConfig.adminIds.remove(userId)
                return buildMessageChain {
                    +"移除成功"
                }
            }
        }
        return buildMessageChain {
            +"未知错误"
        }
    }
}

object AnnounceCommand : Command("announce") {
    override suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean): Message {
        if (PermissionService.checkAdmin(event) || sudo) {
            if (args.isEmpty()) {
                return buildMessageChain {
                    +"缺少参数"
                }
            }
            postAnnounce(args[0], event.bot)
            return buildMessageChain {
                +"发送成功"
            }
        } else {
            return buildMessageChain {
                +"权限不足"
            }
        }
    }
}

object HelpCommand : Command("help") {
    override suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean): Message {
        return buildMessageChain {
            +"小喵的指令有："
            +"1. /Help 显示本帮助"
            +"2. /Ban [userId] 封禁用户"
            +"3. /Unban [userId] 解封用户"
            +"4. /AddAdmin [userId] 添加管理员"
            +"5. /RemoveAdmin [userId] 移除管理员"
            +"5. /Announce [message] 发送公告"
            +"6. /Mass [message] 群发消息"
            +"7. /Sudo [command] 切换为sudo模式执行命令"
            +"====================================="
            +"Copyright (c) 2025 LightWhite"
            +"https://github.com/LightWhite520/PaperBot"
            +"====================================="
        }
    }
}

object MassCommand : Command("mass") {
    override suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean): Message {
        if (PermissionService.checkAdmin(event) || sudo) {
            if (args.isEmpty()) {
                return buildMessageChain {
                    +"缺少参数"
                }
            }
            val message = args[0]
            event.bot.groups.filter { group ->
                group.id in BotManager.botInstants.filterIsInstance<MainBot>().filter { it.bot == event.bot }
                    .map { it.listenGroups }.flatten()
            }.forEach {
                try {
                    it.sendMessage(message)
                } catch (e: Exception) {
                    return buildMessageChain {
                        +"发送失败"
                        +e.message.orEmpty()
                    }
                }
            }
            return buildMessageChain {
                +"发送成功"
            }
        } else {
            return buildMessageChain {
                +"权限不足"
            }
        }
    }
}

object SudoCommand : Command("sudo") {
    override suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean): Message {
        if (PermissionService.checkAdmin(event) || sudo) {
            if (args.isEmpty()) {
                return buildMessageChain {
                    +"缺少参数"
                }
            }
            val rawMessage = args.joinToString(" ")
            val (command, strings) = CommandParser.parse(rawMessage)
            event.subject.sendMessage(command?.execute(strings, event) ?: buildMessageChain { +"未知命令" })
            return buildMessageChain {
                +"执行成功"
            }
        } else {
            val rawMessage = "/" + args.joinToString(" ")
            val (command, strings) = CommandParser.parse(rawMessage)
            if (command == null) {
                return buildMessageChain {
                    +"未知命令"
                }
            }
            lastCommand = command to strings
            waiting = true
            lastKey = key
            logger.info("""
                
                ==========================
                动态秘钥: $lastKey
                ==========================
            """.trimIndent())
            return buildMessageChain {
                +"请输入动态秘钥"
            }
        }
    }
}