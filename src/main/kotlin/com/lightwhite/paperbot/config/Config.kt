package com.lightwhite.paperbot.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val bots: List<BotConfig>
) {
    companion object {
        fun default(): Config {
            return Config(
                listOf(
                    BotConfig(
                        "ws://127.0.0.1:3001",
                        "1234567890",
                        emptyList()
                    )
                )
            )
        }
    }
}