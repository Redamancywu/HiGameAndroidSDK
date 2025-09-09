package com.horizon.higame.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp

@Composable
fun HiGameButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier) {
        if (leading != null) leading()
        Text(text)
        if (trailing != null) trailing()
    }
}

@Composable
fun HiGameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leading: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Text,
    onImeAction: (() -> Unit)? = null
) {
    val visual = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        placeholder = { if (placeholder != null) Text(placeholder) },
        leadingIcon = leading,
        visualTransformation = visual,
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction?.invoke() },
            onGo = { onImeAction?.invoke() },
            onSearch = { onImeAction?.invoke() }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiGameBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!show) return
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = state) {
        content()
    }
}

@Composable
fun HiGameDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    confirmText: String = "OK",
    onConfirm: (() -> Unit)? = null,
    dismissText: String? = null,
    onDismissButton: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { if (title != null) Text(title) },
        text = content,
        confirmButton = {
            TextButton(onClick = { onConfirm?.invoke(); onDismissRequest() }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            if (dismissText != null) {
                TextButton(onClick = { onDismissButton?.invoke(); onDismissRequest() }) {
                    Text(dismissText)
                }
            }
        }
    )
}

@Composable
fun HiGameLoadingView(
    modifier: Modifier = Modifier,
    text: String? = null
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        if (text != null) {
            Spacer(Modifier.height(8.dp))
            Text(text)
        }
    }
}

object HiGameToast {
    @JvmStatic
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context.applicationContext, message, duration).show()
    }
}