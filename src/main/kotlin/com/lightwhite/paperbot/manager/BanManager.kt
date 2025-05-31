package com.lightwhite.paperbot.manager

import com.lightwhite.paperbot.bot.impl.MainBot
import com.lightwhite.paperbot.config.common.BanListType
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.getMember

object BanManager {
    val globalBannedUsers = mutableListOf<Long>()
    val groupBannedUsers = mutableMapOf<Long, MutableList<Long>>()// 群号 -> 用户id

    fun isBanned(user: User, group: Group? = null): Boolean {
        if (group != null) {
            return groupBannedUsers[group.id]?.contains(user.id) ?: false
        }
        return user.id in globalBannedUsers
    }

    suspend fun ban(user: User, group: Group? = null) {
        if (group != null) {
            groupBannedUsers[group.id]?.add(user.id) ?: {
                groupBannedUsers[group.id] = mutableListOf()
                groupBannedUsers[group.id]?.add(user.id)
            }
            group.getMember(user.id)?.kick("You are temporarily banned for 359d 23h 59m 59s from this server!")
            val map = ConfigManager.adminConfig.banList[BanListType.GROUP] ?: return
            map[group.id] = groupBannedUsers[group.id] ?: mutableListOf()
            ConfigManager.saveConfig()
        } else {
            globalBannedUsers.add(user.id)
            BotManager.botInstants.filterIsInstance<MainBot>().forEach {
                it.bot.groups.forEach { group ->
                    group.getMember(user.id)?.kick("You are temporarily banned for 359d 23h 59m 59s from this server!")
                }
            }
            val map = ConfigManager.adminConfig.banList[BanListType.GLOBAL] ?: return
            map[0] = globalBannedUsers
            ConfigManager.saveConfig()
        }
    }

    suspend fun ban(userId: Long, groupId: Long? = null) {
        if (groupId != null) {
            groupBannedUsers[groupId]?.add(userId)
            BotManager.botInstants.filterIsInstance<MainBot>().forEach {
                it.bot.getGroup(groupId)?.getMember(userId)
                    ?.kick("You are temporarily banned for 359d 23h 59m 59s from this server!")
            }
            val map = ConfigManager.adminConfig.banList[BanListType.GROUP] ?: return
            map[groupId] = groupBannedUsers[groupId] ?: mutableListOf()
            ConfigManager.saveConfig()
        } else {
            globalBannedUsers.add(userId)
            BotManager.botInstants.filterIsInstance<MainBot>().forEach {
                it.bot.groups.forEach { group ->
                    group.getMember(userId)?.kick("You are temporarily banned for 359d 23h 59m 59s from this server!")
                }
            }
            val map = ConfigManager.adminConfig.banList[BanListType.GLOBAL] ?: return
            map[0] = globalBannedUsers
            ConfigManager.saveConfig()
        }
    }

    init {
        val banList = ConfigManager.adminConfig.banList
        for ((type, map) in banList) {
            when (type) {
                BanListType.GROUP -> {
                    for ((groupId, list) in map) {
                        groupBannedUsers[groupId] = list
                    }
                }

                BanListType.GLOBAL -> {
                    globalBannedUsers.addAll(map[0] ?: emptyList())
                }
            }
        }
    }
}