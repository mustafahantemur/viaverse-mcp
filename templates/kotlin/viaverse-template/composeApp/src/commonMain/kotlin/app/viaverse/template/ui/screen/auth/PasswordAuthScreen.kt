package app.viaverse.template.ui.screen.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors

@Composable
internal fun PasswordAuthScreen(
    password: String,
    error: String?,
    onPasswordChange: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    AuthScaffold(mode = AuthScaffoldMode.Form, error = error) {
        Text(
            text = "Şifreni gir",
            style = MaterialTheme.typography.titleLarge,
            color = ViaverseColors.Ink
        )
        Text(
            text = "Bu hesap için şifre doğrulaması gerekiyor.",
            modifier = Modifier.padding(top = 8.dp),
            color = ViaverseColors.MutedText,
            style = MaterialTheme.typography.bodyMedium
        )
        AuthFormSpacer()
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(Dimensions.RadiusMd),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(24.dp))
        PrimaryAuthButton(label = "Giriş yap", onClick = onContinue)
        AuthBackButton(onBack = onBack)
    }
}
