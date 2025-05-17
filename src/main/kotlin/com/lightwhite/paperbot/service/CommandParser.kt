package com.lightwhite.paperbot.service

import com.lightwhite.paperbot.command.Command
import com.lightwhite.paperbot.manager.CommandManager

object CommandParser {
    fun parse(rawMessage: String): Pair<Command?, List<String>> {
        if (!rawMessage.startsWith("/") && !rawMessage.startsWith("sudo")) return null to emptyList()
        val parts = rawMessage.substring(rawMessage.indexOf("/") + 1).trim().split("\\s+".toRegex())
        if (parts.isEmpty()) return null to emptyList()
        val command = CommandManager.getCommand(parts[0].trim())
        val args = parts.drop(1)
        return command to args
    }
}