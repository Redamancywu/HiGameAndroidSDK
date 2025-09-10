package com.horizon.higame.ui.user

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.horizon.higame.ui.R
import com.horizon.higame.ui.components.HiGameButton
import com.horizon.higame.ui.internal.HiGameTransparentActivity
import com.horizon.higame.ui.screens.ScreenSpec

object HiGameUserCenterView {

    @JvmStatic
    fun show(activity: Activity, style: String = "LIST_MODAL") {
        HiGameTransparentActivity.start(activity, ScreenSpec.UserCenter { UserCenterScreen(style) })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun UserCenterScreen(
        style: String,
        userInfo: UserInfo = UserInfo()
    ) {
        when (style) {
            "FULLSCREEN" -> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("个人中心") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                ) { paddingValues ->
                    UserCenterContent(userInfo = userInfo, modifier = Modifier.padding(paddingValues))
                }
            }
            "LIST_MODAL" -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    UserCenterContent(userInfo = userInfo, modifier = Modifier.padding(16.dp))
                }
            }
            "SLIDE_PANEL" -> {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    UserCenterContent(userInfo = userInfo, modifier = Modifier.padding(16.dp))
                }
            }
            else -> {
                UserCenterContent(userInfo = userInfo, modifier = Modifier.padding(16.dp))
            }
        }
    }

    @Composable
    private fun UserCenterContent(
        userInfo: UserInfo,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                UserProfileHeader(userInfo = userInfo)
            }
            item {
                UserStatsRow(userInfo = userInfo)
            }
            item {
                MenuSection(
                    title = "账号管理",
                    items = listOf(
                        MenuItem("绑定管理", Icons.Default.Link, "管理第三方账号绑定"),
                        MenuItem("安全设置", Icons.Default.Security, "密码和安全问题"),
                        MenuItem("隐私设置", Icons.Default.PrivacyTip, "隐私权限设置"),
                        MenuItem("设备管理", Icons.Default.Devices, "查看登录设备")
                    )
                )
            }
            item {
                MenuSection(
                    title = "系统功能",
                    items = listOf(
                        MenuItem("设置", Icons.Default.Settings, "应用设置"),
                        MenuItem("帮助", Icons.AutoMirrored.Filled.Help, "帮助与支持"),
                        MenuItem("关于", Icons.Default.Info, "关于应用"),
                        MenuItem("反馈", Icons.Default.Feedback, "意见反馈")
                    )
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HiGameButton(
                    text = "退出登录",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* 处理登出逻辑 */ }
                )
            }
        }
    }

    @Composable
    private fun UserProfileHeader(userInfo: UserInfo) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 用户头像 - 使用本地drawable资源
                    if (userInfo.useLocalAvatar || userInfo.avatarUrl.isNullOrEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.default_avatar),
                            contentDescription = "用户头像",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(userInfo.avatarUrl)
                                .crossfade(true)
                                .placeholder(R.drawable.default_avatar)
                                .error(R.drawable.default_avatar)
                                .build(),
                            contentDescription = "用户头像",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // 用户信息
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = userInfo.username,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ID: 123456789",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "等级",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFD700)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lv.${userInfo.level}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }
                    
                    // 编辑按钮
                    IconButton(
                        onClick = { /* 编辑个人信息 */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun UserStatsRow(userInfo: UserInfo) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(
                    listOf(
                        StatItem("积分", userInfo.points, Icons.Default.Stars),
                        StatItem("登录天数", "128", Icons.Default.CalendarToday),
                        StatItem("游戏时长", userInfo.gameTime, Icons.Default.Schedule),
                        StatItem("成就", "${userInfo.achievements}", Icons.Default.EmojiEvents)
                    )
                ) { stat ->
                    StatItemCard(stat)
                }
            }
        }
    }

    @Composable
    private fun StatItemCard(stat: StatItem) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(80.dp)
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = stat.label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }

    @Composable
    private fun MenuSection(
        title: String,
        items: List<MenuItem>
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                items.forEachIndexed { index, item ->
                    MenuItemRow(
                        item = item,
                        onClick = { /* 处理菜单点击 */ }
                    )
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun MenuItemRow(
        item: MenuItem,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "进入",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }

    // 数据类
    private data class MenuItem(
        val title: String,
        val icon: ImageVector,
        val subtitle: String = ""
    )

    private data class StatItem(
        val label: String,
        val value: String,
        val icon: ImageVector
    )
    
    /**
     * 用户信息数据类
     */
    data class UserInfo(
        val username: String = "游戏玩家",
        val level: Int = 1,
        val experience: String = "0/100",
        val avatarUrl: String? = null,
        val useLocalAvatar: Boolean = true,
        val gameTime: String = "0小时",
        val points: String = "0",
        val achievements: String = "0"
    )
}