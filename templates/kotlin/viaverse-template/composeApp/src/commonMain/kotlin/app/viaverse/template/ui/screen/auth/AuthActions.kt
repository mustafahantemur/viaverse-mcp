package app.viaverse.template.ui.screen.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors

@Composable
internal fun PrimaryAuthButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.ButtonHeight),
        shape = RoundedCornerShape(Dimensions.RadiusMd),
        colors = ButtonDefaults.buttonColors(containerColor = ViaverseColors.BrandOrange),
        onClick = onClick
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun AuthBackButton(onBack: () -> Unit) {
    TextButton(onClick = onBack) {
        Text("Geri dön", color = ViaverseColors.MutedText)
    }
}

@Composable
internal fun AuthFormSpacer() {
    Spacer(Modifier.height(30.dp))
}
