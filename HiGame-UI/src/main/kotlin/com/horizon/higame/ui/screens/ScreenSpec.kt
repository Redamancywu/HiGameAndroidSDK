package com.horizon.higame.ui.screens

import androidx.compose.runtime.Composable
import java.io.Serializable

/**
 * 用于通过 Intent 传递的目标界面描述
 * 每个子类提供其 Composable Content
 */
sealed class ScreenSpec : Serializable {
    class Login(private val content: @Composable () -> Unit) : ScreenSpec() {
        @Composable fun Content() = content()
    }
    class Share(private val content: @Composable () -> Unit) : ScreenSpec() {
        @Composable fun Content() = content()
    }
    class UserCenter(private val content: @Composable () -> Unit) : ScreenSpec() {
        @Composable fun Content() = content()
    }
}