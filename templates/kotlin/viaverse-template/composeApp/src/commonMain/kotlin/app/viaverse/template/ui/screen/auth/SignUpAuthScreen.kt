package app.viaverse.template.ui.screen.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors

@Composable
internal fun SignUpAuthScreen(
    fullName: String,
    identifier: String,
    password: String,
    passwordConfirm: String,
    termsAccepted: Boolean,
    error: String?,
    onFullNameChange: (String) -> Unit,
    onIdentifierChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onTermsAcceptedChange: (Boolean) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    AuthScaffold(mode = AuthScaffoldMode.Form, error = error) {
        Text(
            text = "Hesap oluştur",
            style = MaterialTheme.typography.titleLarge,
            color = ViaverseColors.Ink
        )
        Text(
            text = "Tek hesabınla talep oluşturabilir, ileride bireysel hizmet veya işletme yeteneklerini açabilirsin.",
            modifier = Modifier.padding(top = 8.dp),
            color = ViaverseColors.MutedText,
            style = MaterialTheme.typography.bodyMedium
        )
        AuthFormSpacer()
        SignUpFields(
            fullName = fullName,
            identifier = identifier,
            password = password,
            passwordConfirm = passwordConfirm,
            termsAccepted = termsAccepted,
            onFullNameChange = onFullNameChange,
            onIdentifierChange = onIdentifierChange,
            onPasswordChange = onPasswordChange,
            onPasswordConfirmChange = onPasswordConfirmChange,
            onTermsAcceptedChange = onTermsAcceptedChange
        )
        Spacer(Modifier.height(24.dp))
        PrimaryAuthButton(label = "Hesap oluştur", onClick = onContinue)
        AuthBackButton(onBack = onBack)
    }
}

@Composable
private fun SignUpFields(
    fullName: String,
    identifier: String,
    password: String,
    passwordConfirm: String,
    termsAccepted: Boolean,
    onFullNameChange: (String) -> Unit,
    onIdentifierChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onTermsAcceptedChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Ad soyad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(Dimensions.RadiusMd)
        )
        OutlinedTextField(
            value = identifier,
            onValueChange = onIdentifierChange,
            label = { Text("E-posta veya telefon") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(Dimensions.RadiusMd)
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(Dimensions.RadiusMd)
        )
        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = onPasswordConfirmChange,
            label = { Text("Şifre tekrar") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(Dimensions.RadiusMd)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTermsAcceptedChange(!termsAccepted) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = onTermsAcceptedChange
            )
            Text(
                text = "Kullanım koşullarını ve güvenli ödeme/iletişim kurallarını kabul ediyorum.",
                color = ViaverseColors.MutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
