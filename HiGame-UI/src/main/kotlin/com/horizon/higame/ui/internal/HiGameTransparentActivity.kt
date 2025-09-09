package com.horizon.higame.ui.internal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.horizon.higame.core.ui.HiGameUIConfig
import com.horizon.higame.ui.theme.HiGameMaterialAdapter
import com.horizon.higame.ui.screens.ScreenSpec

/**
 * 透明容器 Activity：用来承载 Compose UI，避免与游戏 GL 直接混用
 * 通过 Intent 传入 ScreenSpec，决定展示哪个界面
 */
class HiGameTransparentActivity : ComponentActivity() {

    companion object {
        private const val KEY_SCREEN = "higame_screen"

        fun start(context: Context, screen: ScreenSpec) {
            val intent = Intent(context, HiGameTransparentActivity::class.java)
            intent.putExtra(KEY_SCREEN, screen)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val screen = intent.getSerializableExtra(KEY_SCREEN) as? ScreenSpec
        setContent {
            HiGameMaterialAdapter(theme = HiGameUIConfig.getTheme()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) {
                    when (screen) {
                        is ScreenSpec.Login -> screen.Content()
                        is ScreenSpec.Share -> screen.Content()
                        is ScreenSpec.UserCenter -> screen.Content()
                        else -> FinishOnDraw()
                    }
                }
            }
        }
    }

    @Composable
    private fun FinishOnDraw() {
        // 未传入有效 screen，直接结束
        finish()
    }
}