package com.horizon.higame.ui.user

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizon.higame.ui.components.HiGameButton
import com.horizon.higame.ui.internal.HiGameTransparentActivity
import com.horizon.higame.ui.screens.ScreenSpec

object HiGameUserCenterView {

    @JvmStatic
    fun show(activity: Activity, style: String = "LIST_MODAL") {
        HiGameTransparentActivity.start(activity, ScreenSpec.UserCenter { UserCenterScreen(style) })
    }

    @Composable
    private fun UserCenterScreen(style: String) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("HiGame User Center ($style)")
            Spacer(Modifier.height(12.dp))
            HiGameButton(text = "设置") {}
            Spacer(Modifier.height(8.dp))
            HiGameButton(text = "登出") {}
        }
    }
}