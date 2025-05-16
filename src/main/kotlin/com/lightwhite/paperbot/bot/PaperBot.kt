package com.lightwhite.paperbot.bot

abstract class PaperBot(val name: String) {
    abstract suspend fun start()
}