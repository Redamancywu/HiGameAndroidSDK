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
    trailing: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Text,
    onImeAction: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leading,
        trailingIcon = trailing,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction?.invoke() },
            onNext = { onImeAction?.invoke() },
            onGo = { onImeAction?.invoke() },
            onSearch = { onImeAction?.invoke() },
            onSend = { onImeAction?.invoke() }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiGameBottomSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        content()
    }
}

@Composable
fun HiGameDialog(
    onDismiss: () -> Unit,
    title: String? = null,
    confirmText: String = "OK",
    onConfirm: (() -> Unit)? = null,
    dismissText: String? = null,
    onDismissButton: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = title?.let { { Text(it) } },
        text = { content() },
        confirmButton = {
            if (onConfirm != null) {
                TextButton(onClick = onConfirm) {
                    Text(confirmText)
                }
            }
        },
        dismissButton = dismissText?.let {
            {
                TextButton(onClick = onDismissButton ?: onDismiss) {
                    Text(it)
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