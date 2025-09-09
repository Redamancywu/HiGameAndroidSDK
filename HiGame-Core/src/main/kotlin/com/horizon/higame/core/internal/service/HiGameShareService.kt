package com.horizon.higame.core.internal.service

import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.callback.HiGameShareCallback
import com.horizon.higame.core.model.HiGameShareContent

/**
 * 分享服务接口
 * 分享模块需要实现的具体接口
 */
internal interface HiGameShareService : HiGameBaseService {
    
    /**
     * 执行分享
     * @param shareContent 分享内容
     * @param callback 分享结果回调
     */
    fun share(shareContent: HiGameShareContent, callback: HiGameShareCallback)
    
    /**
     * 检查平台是否可用
     * @param platform 分享平台
     * @param callback 检查结果回调
     */
    fun isPlatformAvailable(platform: String, callback: HiGameCallback<Boolean>)
}