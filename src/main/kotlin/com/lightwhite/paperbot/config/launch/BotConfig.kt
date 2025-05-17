package com.lightwhite.paperbot.config.launch

import kotlinx.serialization.Serializable

@Serializable
data class BotConfig(
    val url: String,
    val token: String,
    val listenGroups: List<Long>
)
