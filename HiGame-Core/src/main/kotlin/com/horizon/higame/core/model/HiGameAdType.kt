package com.horizon.higame.core.model

/**
 * 广告类型定义与解析
 */
enum class HiGameAdType {
    BANNER,
    INTERSTITIAL,
    REWARDED,
    REWARDED_INTERSTITIAL,
    NATIVE,
    APP_OPEN,
    UNKNOWN;

    companion object {
        /**
         * 从字符串解析广告类型（服务端/三方SDK常见取值兼容）
         */
        fun from(value: String?): HiGameAdType {
            val v = value?.trim()?.lowercase() ?: return UNKNOWN
            return when (v) {
                "banner" -> BANNER
                "interstitial", "inter" -> INTERSTITIAL
                "reward", "rewarded" -> REWARDED
                "rewarded_interstitial", "reward_interstitial", "ri" -> REWARDED_INTERSTITIAL
                "native", "nativead", "native_ad" -> NATIVE
                "app_open", "appopen", "app-open" -> APP_OPEN
                else -> UNKNOWN
            }
        }

        /**
         * 从整型解析广告类型（若后端以数字枚举传递）
         * 1-BANNER, 2-INTERSTITIAL, 3-REWARDED, 4-REWARDED_INTERSTITIAL, 5-NATIVE, 6-APP_OPEN
         */
        fun from(value: Int?): HiGameAdType = when (value) {
            1 -> BANNER
            2 -> INTERSTITIAL
            3 -> REWARDED
            4 -> REWARDED_INTERSTITIAL
            5 -> NATIVE
            6 -> APP_OPEN
            else -> UNKNOWN
        }
    }
}