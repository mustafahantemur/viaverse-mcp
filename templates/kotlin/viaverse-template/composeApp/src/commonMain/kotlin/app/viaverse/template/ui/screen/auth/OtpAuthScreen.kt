package app.viaverse.template.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.viaverse.template.ui.theme.ViaverseColors

private const val OtpLength = 6

@Composable
internal fun OtpAuthScreen(
    otp: String,
    error: String?,
    actionLabel: String,
    onOtpChange: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    AuthScaffold(mode = AuthScaffoldMode.Centered, error = error) {
        Spacer(Modifier.height(34.dp))
        Text(
            text = "Size gönderilen 6 haneli doğrulama kodunu girin.",
            modifier = Modifier.padding(top = 8.dp),
            color = ViaverseColors.MutedText,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(28.dp))
        OtpBoxes(
            otp = otp,
            onOtpChange = { onOtpChange(it.take(OtpLength)) }
        )
        Spacer(Modifier.height(32.dp))
        PrimaryAuthButton(label = actionLabel, onClick = onContinue)
        AuthBackButton(onBack = onBack)
    }
}

@Composable
private fun OtpBoxes(
    otp: String,
    onOtpChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusRequester.requestFocus() }
    ) {
        BasicTextField(
            value = otp,
            onValueChange = { value ->
                onOtpChange(value.filter { it.isDigit() }.take(OtpLength))
            },
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            cursorBrush = SolidColor(Color.Transparent),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(OtpLength) { index ->
                OtpBox(
                    digit = otp.getOrNull(index)?.toString().orEmpty(),
                    isFocused = otp.length == index
                )
            }
        }
    }
}

@Composable
private fun RowScope.OtpBox(
    digit: String,
    isFocused: Boolean
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = if (isFocused) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle,
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            color = ViaverseColors.Ink,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
