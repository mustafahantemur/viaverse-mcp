package app.viaverse.template.ui.screen.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.viaverse.template.ui.theme.Dimensions

@Composable
internal fun IdentifierAuthScreen(
    identifier: String,
    error: String?,
    onIdentifierChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    AuthScaffold(mode = AuthScaffoldMode.Top, error = error) {
        Spacer(Modifier.height(34.dp))
        OutlinedTextField(
            value = identifier,
            onValueChange = onIdentifierChange,
            label = { Text("E-posta veya telefon") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(Dimensions.RadiusMd)
        )
        Spacer(Modifier.height(24.dp))
        PrimaryAuthButton(label = "Devam et", onClick = onContinue)
        ContinueDivider()
        SocialLoginOptions()
    }
}
