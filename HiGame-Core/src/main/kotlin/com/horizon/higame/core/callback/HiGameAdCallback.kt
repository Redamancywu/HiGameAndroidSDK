package com.horizon.higame.core.callback

import com.horizon.higame.core.model.AdInfo
import com.horizon.higame.core.model.AdType
import com.horizon.higame.core.model.NativeAdInfo

/**
 * 广告回调接口
 */
interface HiGameAdCallback {
    /**
     * 广告加载成功（通用）
     */
    fun onAdLoaded(info: AdInfo)

    /**
     * 原生广告素材加载成功（仅 NATIVE 类型）
     */
    fun onNativeAdLoaded(info: NativeAdInfo)

    /**
     * 广告展示
     */
    fun onAdShown(info: AdInfo)

    /**
     * 广告点击
     */
    fun onAdClicked(info: AdInfo)

    /**
     * 激励奖励发放（仅 REWARDED/REWARDED_INTERSTITIAL）
     * @param rewardAmount 奖励额度（单位与业务约定）
     * @param rewardType 奖励类型（如金币、道具等，可与后端协商）
     */
    fun onUserEarnedReward(info: AdInfo, rewardAmount: Long?, rewardType: String?)

    /**
     * 广告关闭/结束
     */
    fun onAdClosed(info: AdInfo)

    /**
     * 广告错误
     * @param code 错误码
     * @param message 错误信息
     * @param type 发生错误时的广告类型（若可获取）
     */
    fun onAdError(code: Int, message: String, type: AdType = AdType.UNKNOWN)
}