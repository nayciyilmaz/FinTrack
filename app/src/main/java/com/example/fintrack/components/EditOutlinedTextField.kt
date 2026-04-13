package com.example.fintrack.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fintrack.R

@Composable
fun EditOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colors: TextFieldColors? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        colors = colors ?: OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(id = R.color.bottom_bar_fab),
            focusedLabelColor = colorResource(id = R.color.bottom_bar_fab)
        )
    )
}