package com.horizon.higame.ui.share

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizon.higame.ui.components.HiGameButton
import com.horizon.higame.ui.internal.HiGameTransparentActivity
import com.horizon.higame.ui.screens.ScreenSpec

object HiGameShareView {

    @JvmStatic
    fun show(activity: Activity, style: String = "GRID_MODAL") {
        HiGameTransparentActivity.start(activity, ScreenSpec.Share { ShareScreen(style) })
    }

    @Composable
    private fun ShareScreen(style: String) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("HiGame Share ($style)")
            Spacer(Modifier.height(12.dp))
            HiGameButton(text = "微信好友") {}
            Spacer(Modifier.height(8.dp))
            HiGameButton(text = "复制链接") {}
        }
    }
}