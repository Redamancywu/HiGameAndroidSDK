package com.horizon.higame.core.callback

import com.horizon.higame.core.model.HiGameAdInfo
import com.horizon.higame.core.model.HiGameAdType
import com.horizon.higame.core.model.HiGameNativeInfo

/**
 * 广告回调接口
 */
interface HiGameAdCallback {
    /**
     * 广告加载成功（通用）
     */
    fun onAdLoaded(type: HiGameAdType, info: HiGameAdInfo)

    /**
     * 原生广告素材加载成功（仅 NATIVE 类型）
     */
    fun onNativeAdLoaded(type: HiGameAdType, info: HiGameNativeInfo)

    /**
     * 广告展示
     */
    fun onAdShown(type: HiGameAdType, info: HiGameAdInfo)

    /**
     * 广告点击
     */
    fun onAdClicked(type: HiGameAdType, info: HiGameAdInfo)

    /**
     * 激励奖励发放（仅 REWARDED/REWARDED_INTERSTITIAL）
     * @param rewardAmount 奖励额度（单位与业务约定）
     * @param rewardType 奖励类型（如金币、道具等，可与后端协商）
     */
    fun onUserEarnedReward(type: HiGameAdType, info: HiGameAdInfo, rewardAmount: Long?, rewardType: String?)

    /**
     * 广告关闭/结束
     */
    fun onAdClosed(type: HiGameAdType, info: HiGameAdInfo)

    /**
     * 广告错误
     * @param code 错误码
     * @param message 错误信息
     */
    fun onAdError(type: HiGameAdType, code: Int, message: String)
}