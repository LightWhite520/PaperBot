package com.lightwhite.paperbot.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.sendTo

class Verify(val user: User) {
    private val verifyCode = (100000..999999).random()
    val expireTime = System.currentTimeMillis() + 6 * 1000 * 1000
    suspend fun sendVerifyCode(group: Group) {
        buildMessageChain {
            +At(user)
            +" 请在60s内输入验证码 你的验证码为：${verifyCode}"
        }.sendTo(group)
    }

    fun verify(code: Int): Boolean {
        return code == verifyCode
    }
}
