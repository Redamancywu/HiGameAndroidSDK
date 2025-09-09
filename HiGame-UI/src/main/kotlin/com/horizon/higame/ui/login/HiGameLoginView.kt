package com.horizon.higame.ui.login

import android.app.Activity
import com.horizon.higame.ui.internal.HiGameTransparentActivity
import com.horizon.higame.ui.screens.ScreenSpec
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.horizon.higame.ui.components.HiGameButton

/**
 * 对外门面：以 Activity 方式展示登录 UI（Compose 无感知）
 */
object HiGameLoginView {

    @JvmStatic
    fun show(activity: Activity, style: String = "CARD_MODAL") {
        HiGameTransparentActivity.start(activity, ScreenSpec.Login { LoginScreen(style) })
    }

    @Composable
    private fun LoginScreen(style: String) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "HiGame Login ($style)")
            Spacer(Modifier.height(12.dp))
            HiGameButton(text = "微信登录") {}
            Spacer(Modifier.height(8.dp))
            HiGameButton(text = "游客登录") {}
        }
    }
}