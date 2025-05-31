package com.lightwhite.paperbot

import com.lightwhite.paperbot.command.Command
import kotlinx.serialization.json.Json
import net.mamoe.mirai.contact.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

val Serializer = Json {
    isLenient = true
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
}

val logger: Logger = LoggerFactory.getLogger("PaperBot")

var waiting = false

val key
    get() = Random.nextInt(100000, 999999)

var lastKey = 0

var lastCommand: Pair<Command, List<String>>? = null

var invoker: User? = null