package com.horizon.higame.core.internal.manager

import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.internal.service.HiGameServiceRegistry
import com.horizon.higame.core.utils.HiGameLogger

/**
 * 分享功能管理器
 * 第三层：功能层 - 负责分享相关的所有功能
 */
internal class HiGameShareManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameShareManager? = null
        
        fun getInstance(): HiGameShareManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameShareManager().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 分享内容
     * @param shareInfo 分享信息
     * @param callback 分享回调
     */
    fun share(shareInfo: Map<String, Any>, callback: HiGameCallback<String>) {
        val shareService = HiGameServiceRegistry.getShareService()
        if (shareService != null) {
            HiGameLogger.d("Delegating share to service: ${shareService::class.simpleName}")
            // 转换为HiGameShareContent
            val shareContent = com.horizon.higame.core.model.HiGameShareContent(
                title = shareInfo["title"] as? String ?: "",
                description = shareInfo["description"] as? String ?: "",
                imageUrl = shareInfo["imageUrl"] as? String,
                linkUrl = shareInfo["linkUrl"] as? String,
                platform = shareInfo["platform"] as? String ?: "",
                extraData = shareInfo["extraData"] as? Map<String, String>
            )
            shareService.share(shareContent, object : com.horizon.higame.core.callback.HiGameShareCallback {
                override fun onShareSuccess(platform: String) {
                    callback.onSuccess("Share successful on $platform")
                }
                override fun onShareError(code: Int, message: String) {
                    callback.onError(code, message)
                }
                override fun onShareCancel() {
                    callback.onError(-2, "Share cancelled")
                }
            })
        } else {
            HiGameLogger.e("Share service not found")
            callback.onError(-1, "Share service not available")
        }
    }
    
    /**
     * 检查分享平台是否可用
     * @param platform 分享平台
     * @return 是否可用
     */
    fun isPlatformAvailable(platform: String, callback: HiGameCallback<Boolean>) {
        val shareService = HiGameServiceRegistry.getShareService()
        if (shareService != null) {
            shareService.isPlatformAvailable(platform, callback)
        } else {
            callback.onError(-1, "Share service not available")
        }
    }
}