package app.viaverse.template.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors

private enum class SocialProvider {
    Google,
    Instagram,
    Apple
}

@Composable
internal fun SocialLoginOptions() {
    Spacer(Modifier.height(18.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        SocialButton(
            provider = SocialProvider.Google,
            label = "Sign in with Google",
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SocialButton(
                provider = SocialProvider.Instagram,
                label = "Instagram",
                modifier = Modifier.weight(1f)
            )
            SocialButton(
                provider = SocialProvider.Apple,
                label = "Apple",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
internal fun ContinueDivider() {
    Spacer(Modifier.height(26.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(ViaverseColors.BorderSubtle)
        )
        Text(
            text = "VEYA",
            modifier = Modifier.padding(horizontal = 14.dp),
            color = ViaverseColors.MutedText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(ViaverseColors.BorderSubtle)
        )
    }
}

@Composable
private fun SocialButton(
    provider: SocialProvider,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(Color.White)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .clickable { }
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(if (provider == SocialProvider.Google) 30.dp else 26.dp),
            contentAlignment = Alignment.Center
        ) {
            when (provider) {
                SocialProvider.Google -> GoogleIcon(Modifier.fillMaxSize())
                SocialProvider.Instagram -> InstagramIcon(Modifier.size(26.dp))
                SocialProvider.Apple -> AppleIcon(Modifier.size(22.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(label, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelMedium)
    }
}
