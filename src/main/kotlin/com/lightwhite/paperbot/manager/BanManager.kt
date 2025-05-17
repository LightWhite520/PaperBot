package com.lightwhite.paperbot.manager

object BanManager {
    val globalBannedUsers = mutableListOf<Long>()
    val groupBannedUsers = mutableMapOf<Long, MutableList<Long>>()// 群号 -> 用户id

    fun isBanned(userId: Long, group: Long? = null): Boolean {
        if (group != null) {
            return groupBannedUsers[group]?.contains(userId) ?: false
        }
        return userId in globalBannedUsers
    }
}