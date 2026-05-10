package app.viaverse.template.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AccountCapability
import app.viaverse.template.domain.model.BusinessWorkspaceStatus
import app.viaverse.template.domain.model.ConversationPreview
import app.viaverse.template.domain.model.ConversationStatus
import app.viaverse.template.domain.model.DashboardSnapshot
import app.viaverse.template.domain.model.RequestLifecycleStatus
import app.viaverse.template.domain.model.RequestSummary
import app.viaverse.template.domain.model.WorkLifecycleStatus
import app.viaverse.template.domain.model.WorkSummary
import app.viaverse.template.ui.screen.explore.categoryDrawable
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun RequestsScreen(
    snapshot: DashboardSnapshot,
    onCreateRequest: () -> Unit,
    onOpenJob: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(RequestCenterTab.Active) }
    val activeRequests = snapshot.requests.filter { it.status.isActiveRequest() }
    val pastRequests = snapshot.requests.filterNot { it.status.isActiveRequest() }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ViaverseColors.WarmBase),
        contentPadding = PaddingValues(Dimensions.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "Talep merkezi",
                body = "Aktif talepler, geçmiş işler ve yeni talep oluşturma akışı."
            )
        }
        item {
            RequestCenterTabs(selected = selectedTab, onSelected = { selectedTab = it })
        }
        when (selectedTab) {
            RequestCenterTab.Active -> {
                items(activeRequests, key = { it.id }) { request ->
                    RequestCard(request, onClick = { onOpenJob(request.id) })
                }
            }
            RequestCenterTab.History -> {
                items(pastRequests, key = { it.id }) { request ->
                    RequestCard(request, onClick = { onOpenJob(request.id) })
                }
            }
            RequestCenterTab.Create -> {
                item {
                    StatusCard(
                        title = "Talep oluştur",
                        body = "Kategori, alt kategori, bütçe, zaman ve konumla detaylı talep taslağı aç.",
                        status = "Yeni"
                    )
                }
                item {
                    PrimaryAction(label = "Talep oluşturma akışına geç", onClick = onCreateRequest)
                }
            }
        }
    }
}

private enum class RequestCenterTab(val labelTr: String) {
    Active("Aktif talepler"),
    History("Geçmiş"),
    Create("Talep oluştur")
}

@Composable
private fun RequestCenterTabs(selected: RequestCenterTab, onSelected: (RequestCenterTab) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RequestCenterTab.entries.forEach { tab ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected == tab) ViaverseColors.BrandOrange else ViaverseColors.CardSurface)
                    .border(1.dp, if (selected == tab) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle, RoundedCornerShape(999.dp))
                    .clickable { onSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(tab.labelTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun WorkScreen(
    snapshot: DashboardSnapshot,
    providerModeUnlocked: Boolean,
    onOpenProviderSetup: () -> Unit,
    onOpenProviderDashboard: () -> Unit
) {
    val setupRequired = !providerModeUnlocked && snapshot.work.any { it.status == WorkLifecycleStatus.ONBOARDING_REQUIRED }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ViaverseColors.WarmBase),
        contentPadding = PaddingValues(Dimensions.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "İş yap",
                body = "Bireysel hizmet verme modu, kişisel hesaptan hafif onboarding ile açılır."
            )
        }
        items(snapshot.work, key = { it.id }) { work ->
            WorkCard(work)
        }
        item {
            PrimaryAction(
                label = if (setupRequired) "Bireysel hizmet verme kurulumunu aç" else "Hizmet veren panelini aç",
                onClick = if (setupRequired) onOpenProviderSetup else onOpenProviderDashboard
            )
        }
        if (!setupRequired) {
            item {
                SecondaryAction(label = "Bireysel hizmet verme ayarlarını düzenle", onClick = onOpenProviderSetup)
            }
        }
    }
}

@Composable
internal fun MessagesScreen(
    snapshot: DashboardSnapshot,
    onOpenChat: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ViaverseColors.WarmBase),
        contentPadding = PaddingValues(Dimensions.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "Mesajlar",
                body = "İş ve teklif bağlamına bağlı konuşmalar, güvenlik sinyalleriyle birlikte görünür."
            )
        }
        items(snapshot.conversations, key = { it.id }) { conversation ->
            ConversationCard(conversation, onClick = { onOpenChat(conversation.id) })
        }
    }
}

@Composable
internal fun ProfileScreen(
    account: Account?,
    snapshot: DashboardSnapshot,
    providerModeUnlocked: Boolean,
    onOpenBusiness: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenReviews: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenPublicProfile: () -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ViaverseColors.WarmBase),
        contentPadding = PaddingValues(Dimensions.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ProfileHero(account)
        }
        item {
            SecondaryAction(label = "Public profili görüntüle", onClick = onOpenPublicProfile)
        }
        item {
            InsightCard(snapshot.insights.firstOrNull()?.summaryTr.orEmpty())
        }
        items(snapshot.capabilities, key = { it.capability.name }) { capability ->
            val enabled = capability.enabled ||
                (capability.capability == AccountCapability.DO_WORK_INDIVIDUALLY && providerModeUnlocked)
            StatusCard(
                title = capability.titleTr,
                body = capability.bodyTr,
                status = if (enabled) "Aktif" else "Hazır değil"
            )
        }
        item {
            BusinessWorkspaceCard(
                status = snapshot.businessWorkspace.statusLabel(),
                title = snapshot.businessWorkspace.titleTr,
                body = snapshot.businessWorkspace.verificationStepTr,
                onClick = onOpenBusiness
            )
        }
        item {
            SecondaryAction(label = "Ayarlar ve güvenlik", onClick = onOpenSettings)
        }
        item {
            SecondaryAction(label = "Cüzdan ve güvenli ödeme", onClick = onOpenWallet)
        }
        item {
            SecondaryAction(label = "Yorumlar ve rozetler", onClick = onOpenReviews)
        }
        item {
            SecondaryAction(label = "Destek ve güvenlik merkezi", onClick = onOpenSupport)
        }
        item {
            PrimaryAction(label = "Çıkış yap", onClick = onLogout)
        }
    }
}

@Composable
private fun ScreenHeader(
    title: String,
    body: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = ViaverseColors.Ink,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = body,
            color = ViaverseColors.MutedText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RequestCard(
    request: RequestSummary,
    onClick: () -> Unit
) {
    StatusCard(
        title = request.titleTr,
        body = "${request.locationTr} | ${request.budgetHintTr}\n${request.aiSummaryTr}",
        status = request.status.labelTr(),
        categoryId = request.categoryId,
        onClick = onClick
    )
}

@Composable
private fun WorkCard(work: WorkSummary) {
    val body = if (work.status == WorkLifecycleStatus.ONBOARDING_REQUIRED) {
        work.trustFitTr
    } else {
        "${work.payoutHintTr} | ${work.distanceKm} km\n${work.trustFitTr}"
    }
    StatusCard(
        title = work.titleTr,
        body = body,
        status = work.status.labelTr(),
        categoryId = work.categoryId
    )
}

@Composable
private fun ConversationCard(
    conversation: ConversationPreview,
    onClick: () -> Unit
) {
    StatusCard(
        title = conversation.titleTr,
        body = "${conversation.contextTr} | ${conversation.lastMessageTr}",
        status = conversation.status.labelTr(),
        onClick = onClick
    )
}

@Composable
private fun StatusCard(
    title: String,
    body: String,
    status: String,
    categoryId: app.viaverse.template.domain.model.ServiceCategoryId? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (categoryId != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ViaverseColors.WarmMuted),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(categoryDrawable(categoryId)),
                    contentDescription = title,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(body, color = ViaverseColors.MutedText, style = MaterialTheme.typography.bodySmall)
        }
        StatusPill(status)
    }
}

@Composable
private fun StatusPill(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(ViaverseColors.WarmMuted)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProfileHero(account: Account?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(ViaverseColors.BrandOrange),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = account?.displayName?.firstOrNull()?.uppercase().orEmpty(),
                color = ViaverseColors.Ink,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = account?.displayName ?: "Viaverse kullanıcısı",
                color = ViaverseColors.Ink,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = account?.identifier.orEmpty(),
                color = ViaverseColors.MutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun InsightCard(body: String) {
    if (body.isBlank()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.BrandOrange)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("AI içgörü", color = ViaverseColors.Ink, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(body, color = ViaverseColors.Ink, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun BusinessWorkspaceCard(
    status: String,
    title: String,
    body: String,
    onClick: () -> Unit
) {
    StatusCard(
        title = title,
        body = body,
        status = status,
        onClick = onClick
    )
}

@Composable
private fun PrimaryAction(
    label: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.ButtonHeight)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.BrandOrange)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
    Spacer(Modifier.height(Dimensions.SpaceSm))
}

@Composable
private fun SecondaryAction(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.ButtonHeight)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

private fun RequestLifecycleStatus.labelTr(): String {
    return when (this) {
        RequestLifecycleStatus.DRAFT -> "Taslak"
        RequestLifecycleStatus.MATCHING -> "Eşleşiyor"
        RequestLifecycleStatus.OFFER_RECEIVED -> "Teklif var"
        RequestLifecycleStatus.SCHEDULED -> "Planlandı"
        RequestLifecycleStatus.COMPLETED -> "Tamamlandı"
        RequestLifecycleStatus.CANCELLED -> "İptal"
    }
}

private fun RequestLifecycleStatus.isActiveRequest(): Boolean {
    return when (this) {
        RequestLifecycleStatus.DRAFT,
        RequestLifecycleStatus.MATCHING,
        RequestLifecycleStatus.OFFER_RECEIVED,
        RequestLifecycleStatus.SCHEDULED -> true
        RequestLifecycleStatus.COMPLETED,
        RequestLifecycleStatus.CANCELLED -> false
    }
}

private fun WorkLifecycleStatus.labelTr(): String {
    return when (this) {
        WorkLifecycleStatus.ONBOARDING_REQUIRED -> "Kurulum"
        WorkLifecycleStatus.OPEN -> "Açık"
        WorkLifecycleStatus.OFFER_SENT -> "Teklif verildi"
        WorkLifecycleStatus.IN_PROGRESS -> "Sürüyor"
    }
}

private fun ConversationStatus.labelTr(): String {
    return when (this) {
        ConversationStatus.NEEDS_REPLY -> "Yanıt bekliyor"
        ConversationStatus.SAFE -> "Güvenli"
        ConversationStatus.SUPPORT_REVIEW -> "Destek"
    }
}

private fun BusinessWorkspaceStatus.labelTr(): String {
    return when (this) {
        BusinessWorkspaceStatus.NOT_STARTED -> "Ek hesap"
        BusinessWorkspaceStatus.PENDING_VERIFICATION -> "Doğrulama"
        BusinessWorkspaceStatus.PUBLISH_READY -> "Yayına hazır"
        BusinessWorkspaceStatus.PUBLISHED -> "Yayında"
    }
}

private fun app.viaverse.template.domain.model.BusinessWorkspaceSummary.statusLabel(): String {
    return status.labelTr()
}
