package com.lightwhite.paperbot.service

import com.lightwhite.paperbot.manager.ConfigManager
import net.mamoe.mirai.event.events.MessageEvent

object PermissionService {
    private val adminList: MutableList<Long> = ConfigManager.adminConfig.adminIds
    fun isAdmin(userId: Long): Boolean {
        return adminList.contains(userId)
    }

    fun checkAdmin(event: MessageEvent): Boolean {
        return isAdmin(event.sender.id)
    }
}