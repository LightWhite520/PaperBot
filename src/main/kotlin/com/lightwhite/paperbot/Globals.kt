package com.lightwhite.paperbot

import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Serializer = Json {
    isLenient = true
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
}
val logger: Logger = LoggerFactory.getLogger("PaperBot")