package com.lightwhite.paperbot.manager

import com.lightwhite.paperbot.Serializer
import com.lightwhite.paperbot.config.common.AdminConfig
import com.lightwhite.paperbot.config.launch.Config
import com.lightwhite.paperbot.logger
import kotlinx.serialization.encodeToString
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.system.exitProcess

object ConfigManager {
    val configPath = Paths.get("config")
    val launchConfig: Config
    val adminConfig: AdminConfig

    init {
        val configFile = configPath.resolve("config.json")
        configFile.parent.createDirectories()
        if (!configFile.exists()) {
            val config = Config.default()
            configFile.writeText(Serializer.encodeToString(config))
            logger.error("第一次启动，请填写配置文件！")
            exitProcess(0)
        }
        launchConfig = Serializer.decodeFromString<Config>(configFile.readText())
        val adminConfigFile = configPath.resolve("admin.json")
        adminConfig = if (adminConfigFile.exists()) {
             Serializer.decodeFromString<AdminConfig>(adminConfigFile.readText())
        } else {
            AdminConfig.default()
        }
    }

    fun saveConfig() {
        val configFile = configPath.resolve("config.json")
        configFile.writeText(Serializer.encodeToString(launchConfig))
        val adminConfigFile = configPath.resolve("admin.json")
        adminConfigFile.writeText(Serializer.encodeToString(adminConfig))
    }
}