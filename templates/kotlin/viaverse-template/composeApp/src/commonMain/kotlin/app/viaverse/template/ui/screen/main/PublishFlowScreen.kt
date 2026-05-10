package app.viaverse.template.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.template.domain.model.SocialFeedPost
import app.viaverse.template.domain.model.SocialMediaKind
import app.viaverse.template.domain.model.SocialPostType
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors

@Composable
internal fun PublishFlowScreen(
    onPublished: (SocialFeedPost) -> Unit,
    onBack: () -> Unit
) {
    var selectedType by remember { mutableStateOf(SocialPostType.HELP) }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Yakın çevre") }
    var mediaKind by remember { mutableStateOf(SocialMediaKind.NONE) }
    val hashtags = remember(title, body) { extractPublishHashtags(title, body) }
    val spec = selectedType.publishSpec()

    OverlayScreen(title = "Yayınla", onBack = onBack) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ne yayınlamak istiyorsun?", color = ViaverseColors.Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Paylaşım türüne göre alanlar ve önerilen etiketler değişir.", color = ViaverseColors.MutedText, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(SocialPostType.entries, key = { it.name }) { type ->
                    PublishTypeCard(
                        type = type,
                        selected = selectedType == type,
                        onClick = { selectedType = type }
                    )
                }
            }
        }
        item {
            InfoCard(title = spec.titleTr, body = spec.bodyTr, status = spec.intentTr)
        }
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(spec.titleHintTr) },
                singleLine = true,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
        }
        item {
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(spec.bodyHintTr) },
                minLines = if (selectedType == SocialPostType.WORK) 5 else 4,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
        }
        item {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Konum görünürlüğü") },
                singleLine = true,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SocialMediaKind.entries, key = { it.name }) { kind ->
                    PublishChip(kind.labelTr(), mediaKind == kind) { mediaKind = kind }
                }
            }
        }
        item {
            PublishHashtags(
                hashtags = hashtags.ifEmpty { spec.suggestedHashtags },
                generated = hashtags.isNotEmpty()
            )
        }
        item {
            PrimaryOverlayAction(
                label = spec.actionTr,
                onClick = {
                    if (body.isNotBlank()) {
                        onPublished(
                            SocialFeedPost(
                                id = "published_${body.hashCode()}_${selectedType.name}",
                                type = selectedType,
                                authorNameTr = "Sen",
                                titleTr = title.takeIf { it.isNotBlank() },
                                bodyTr = body,
                                publishTimeTr = "şimdi",
                                distanceTr = location,
                                likes = 0,
                                commentsCount = 0,
                                mediaKind = mediaKind,
                                mediaLabelTr = if (mediaKind == SocialMediaKind.NONE) null else mediaKind.labelTr(),
                                priceHintTr = if (selectedType == SocialPostType.WORK) "Teklif bekliyor" else null,
                                hashtags = hashtags.ifEmpty { spec.suggestedHashtags },
                                comments = emptyList()
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun PublishTypeCard(type: SocialPostType, selected: Boolean, onClick: () -> Unit) {
    val spec = type.publishSpec()
    Column(
        modifier = Modifier
            .height(96.dp)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(if (selected) ViaverseColors.BrandOrange else ViaverseColors.CardSurface)
            .border(1.dp, if (selected) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(spec.iconTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(spec.labelTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(spec.intentTr, color = ViaverseColors.Ink.copy(alpha = 0.72f), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun PublishHashtags(hashtags: List<String>, generated: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(if (generated) "Algılanan etiketler" else "Önerilen etiketler", color = ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(hashtags, key = { it }) { tag ->
                PublishChip("#$tag", selected = false, onClick = {})
            }
        }
    }
}

@Composable
private fun PublishChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) ViaverseColors.BrandOrange else ViaverseColors.WarmMuted)
            .border(1.dp, if (selected) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

private data class PublishTypeSpec(
    val labelTr: String,
    val iconTr: String,
    val titleTr: String,
    val bodyTr: String,
    val intentTr: String,
    val titleHintTr: String,
    val bodyHintTr: String,
    val actionTr: String,
    val suggestedHashtags: List<String>
)

private fun SocialPostType.publishSpec(): PublishTypeSpec {
    return when (this) {
        SocialPostType.HELP -> PublishTypeSpec(
            labelTr = "Yardım",
            iconTr = "✚",
            titleTr = "Komşudan yardım iste",
            bodyTr = "Alet ödünç alma, kısa süreli destek veya hızlı mahalle yardımı için.",
            intentTr = "Dayanışma",
            titleHintTr = "Ne için yardıma ihtiyacın var?",
            bodyHintTr = "Kısa, net ve güvenli şekilde anlat",
            actionTr = "Yardım çağrısını yayınla",
            suggestedHashtags = listOf("yardım", "komşuyardımı", "mahalle")
        )
        SocialPostType.ANNOUNCEMENT -> PublishTypeSpec(
            labelTr = "Duyuru",
            iconTr = "!",
            titleTr = "Yerel duyuru paylaş",
            bodyTr = "Mahalle bilgisi, kayıp eşya, uyarı veya topluluk haberi için.",
            intentTr = "Bilgilendir",
            titleHintTr = "Duyuru başlığı",
            bodyHintTr = "Kimleri ilgilendiriyor, nerede ve ne zaman?",
            actionTr = "Duyuruyu yayınla",
            suggestedHashtags = listOf("duyuru", "mahalle", "bilgilendirme")
        )
        SocialPostType.ADVISORY -> PublishTypeSpec(
            labelTr = "Danışma",
            iconTr = "?",
            titleTr = "Çevreye danış",
            bodyTr = "Tavsiye, deneyim ve güvenli karar desteği almak için.",
            intentTr = "Fikir al",
            titleHintTr = "Neyi merak ediyorsun?",
            bodyHintTr = "Karar vermek için hangi bilgi eksik?",
            actionTr = "Danışmayı yayınla",
            suggestedHashtags = listOf("tavsiye", "deneyim", "güven")
        )
        SocialPostType.WORK -> PublishTypeSpec(
            labelTr = "Küçük iş",
            iconTr = "₺",
            titleTr = "Küçük iş yayınla",
            bodyTr = "Kısa süreli, düşük riskli ve yakın çevrede yapılabilecek işler için.",
            intentTr = "Teklif al",
            titleHintTr = "İş başlığı",
            bodyHintTr = "İşin kapsamı, süre, beklenti ve varsa bütçe",
            actionTr = "Küçük işi yayınla",
            suggestedHashtags = listOf("küçükiş", "teklif", "yakınımda")
        )
        SocialPostType.EVENT -> PublishTypeSpec(
            labelTr = "Etkinlik",
            iconTr = "◎",
            titleTr = "Etkinlik paylaş",
            bodyTr = "Mahalle buluşması, atölye, sosyal aktivite veya yerel organizasyon için.",
            intentTr = "Katılım",
            titleHintTr = "Etkinlik adı",
            bodyHintTr = "Tarih, yer, katılım şartı ve kısa açıklama",
            actionTr = "Etkinliği yayınla",
            suggestedHashtags = listOf("etkinlik", "buluşma", "yerel")
        )
    }
}

private fun SocialMediaKind.labelTr(): String {
    return when (this) {
        SocialMediaKind.NONE -> "Medya yok"
        SocialMediaKind.IMAGE -> "Fotoğraf"
        SocialMediaKind.VIDEO -> "Video"
    }
}

private fun extractPublishHashtags(title: String, body: String): List<String> {
    return Regex("""(^|\s)#([A-Za-z0-9_ğüşöçıİĞÜŞÖÇ]{2,30})""")
        .findAll("$title $body")
        .map { it.groupValues[2].lowercase() }
        .distinct()
        .take(8)
        .toList()
}
