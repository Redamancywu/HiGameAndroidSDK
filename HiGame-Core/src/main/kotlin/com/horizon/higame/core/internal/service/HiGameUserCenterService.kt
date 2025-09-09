package com.horizon.higame.core.internal.service

import android.content.Context
import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.model.HiGameUser

/**
 * 用户中心服务接口
 * 用户中心模块需要实现的具体接口
 */
internal interface HiGameUserCenterService : HiGameBaseService {
    
    /**
     * 显示用户中心
     * @param context 上下文
     * @param callback 操作回调
     */
    fun showUserCenter(context: Context, callback: HiGameCallback<String>)
    
    /**
     * 更新用户信息
     * @param userInfo 用户信息
     * @param callback 更新回调
     */
    fun updateUserInfo(userInfo: HiGameUser, callback: HiGameCallback<Boolean>)
    
    /**
     * 绑定第三方账号
     * @param platform 平台类型
     * @param callback 绑定回调
     */
    fun bindThirdParty(platform: String, callback: HiGameCallback<Boolean>)
}