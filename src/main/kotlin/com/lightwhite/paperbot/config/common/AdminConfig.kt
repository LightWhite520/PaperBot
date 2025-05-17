package com.lightwhite.paperbot.config.common

import kotlinx.serialization.Serializable

@Serializable
data class AdminConfig(val adminIds: MutableList<Long>) {
    companion object {
        fun default(): AdminConfig {
            return AdminConfig(mutableListOf())
        }
    }
}