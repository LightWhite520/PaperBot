package com.lightwhite.paperbot.command

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Message

abstract class Command(val command: String) {
    abstract suspend fun execute(args: List<String>, event: MessageEvent, sudo: Boolean = false): Message
}