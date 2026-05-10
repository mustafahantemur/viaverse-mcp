package app.viaverse.template.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.template.domain.model.Account
import app.viaverse.template.ui.theme.ViaverseColors

@Composable
fun LockedHomePlaceholder(
    account: Account?,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MVP ana akışı hazır değil",
            style = MaterialTheme.typography.titleLarge,
            color = ViaverseColors.Ink
        )
        Text(
            text = "Giriş kapısı çalışıyor. Sonraki fazda Keşfet, İşlerim, Talep Oluştur, Mesajlar ve Profil eklenecek.",
            modifier = Modifier.padding(top = 12.dp),
            color = ViaverseColors.MutedText
        )
        Text(
            text = "Aktif kullanıcı: ${account?.displayName ?: "Yok"}",
            modifier = Modifier.padding(top = 18.dp),
            fontWeight = FontWeight.Bold
        )
        Button(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onLogout
        ) {
            Text("Çıkış yap")
        }
    }
}

