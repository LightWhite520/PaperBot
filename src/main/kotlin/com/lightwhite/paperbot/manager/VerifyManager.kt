package com.lightwhite.paperbot.manager

import com.lightwhite.paperbot.logger
import com.lightwhite.paperbot.service.Verify
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.getMember

object VerifyManager {
    private val verifyingUsers = mutableMapOf<Pair<User, Group>, Verify>()
    fun isVerifying(user: User, group: Group): Boolean = verifyingUsers.containsKey(Pair(user, group))

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun verify(user: User, group: Group) {
        val verify = Verify(user)
        verify.sendVerifyCode(group)
        val pair = Pair(user, group)
        verifyingUsers[pair] = verify
        GlobalScope.launch {
            while (true) {
                if (!verifyingUsers.containsKey(pair)) return@launch
                if (System.currentTimeMillis() > verify.expireTime) {
                    verifyingUsers.remove(pair)
                    group.getMember(user.id)?.kick("验证码过期, 已踢出") ?: return@launch
                    group.sendMessage("验证码过期, 已踢出")
                    logger.info("已踢出$user")
                    return@launch
                }
                delay(1000)
            }
        }
    }

    suspend fun verify(user: User, group: Group, code: Int) {
        val verify = verifyingUsers[Pair(user, group)] ?: return
        if (verify.verify(code)) {
            verifyingUsers.remove(Pair(user, group))
            group.sendMessage("验证通过")
        } else {
            group.getMember(user.id)?.kick("验证码错误, 已踢出")
            group.sendMessage("验证码错误")
        }
    }
}