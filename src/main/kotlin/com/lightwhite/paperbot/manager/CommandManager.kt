package com.lightwhite.paperbot.manager

import com.lightwhite.paperbot.command.*

object CommandManager {
    private val commandList = listOf(
        BanCommand,
        UnbanCommand,
        AddAdminCommand,
        RemoveAdminCommand,
        AnnounceCommand,
        HelpCommand,
        MassCommand,
        SudoCommand
    )

    fun getCommand(command: String): Command? {
        return commandList.find { it.command.equals(command, true) }
    }
}