package com.lightwhite.paperbot.config.common

import kotlinx.serialization.Serializable

@Serializable
data class AdminConfig(
    val adminIds: MutableList<Long>,
    val banList: MutableMap<BanListType, MutableMap<Long, MutableList<Long>>>
) {
    companion object {
        fun default(): AdminConfig {
            return AdminConfig(
                mutableListOf(),
                mutableMapOf(
                    BanListType.GROUP to mutableMapOf(),
                    BanListType.GLOBAL to mutableMapOf()
                )
            )
        }
    }
}

enum class BanListType {
    GROUP,
    GLOBAL
}