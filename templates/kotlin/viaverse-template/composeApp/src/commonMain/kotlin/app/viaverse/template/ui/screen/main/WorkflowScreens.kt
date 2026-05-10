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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.template.data.repository.DiscoveryRepository
import app.viaverse.template.data.repository.WorkflowRepository
import app.viaverse.template.domain.model.BusinessTeamRole
import app.viaverse.template.domain.model.BusinessWorkspaceSummary
import app.viaverse.template.domain.model.CatalogItemStatus
import app.viaverse.template.domain.model.ChatMessage
import app.viaverse.template.domain.model.ChatPolicyState
import app.viaverse.template.domain.model.CustomerJobStatus
import app.viaverse.template.domain.model.DiscoveryItemType
import app.viaverse.template.domain.model.DiscoveryUrgency
import app.viaverse.template.domain.model.ExploreItem
import app.viaverse.template.domain.model.OfferStatus
import app.viaverse.template.domain.model.ProfileSettingGroup
import app.viaverse.template.domain.model.ProviderAvailability
import app.viaverse.template.domain.model.ProviderSetupStepStatus
import app.viaverse.template.domain.model.ReviewStatus
import app.viaverse.template.domain.model.RequestDraft
import app.viaverse.template.domain.model.RequestDraftStatus
import app.viaverse.template.domain.model.SafePaymentStatus
import app.viaverse.template.domain.model.SchedulePreference
import app.viaverse.template.domain.model.ServiceCategoryId
import app.viaverse.template.domain.model.SupportTicketStatus
import app.viaverse.template.domain.model.SubscriptionState
import app.viaverse.template.domain.model.WorkLifecycleStatus
import app.viaverse.template.ui.screen.explore.categoryDrawable
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun ExploreItemDetailScreen(
    repository: DiscoveryRepository,
    itemId: String,
    onBack: () -> Unit
) {
    val item = remember(itemId) { repository.findItem(itemId) }

    OverlayScreen(title = "Detay", onBack = onBack) {
        if (item == null) {
            item {
                InfoCard(title = "Kayıt bulunamadı", body = "Bu mock veri artık listede görünmüyor.", status = "Boş")
            }
        } else {
            item {
                ExploreDetailHero(item)
            }
            item {
                InfoCard(title = "Güven sinyali", body = item.trustSignalTr, status = item.priceHintTr)
            }
            item {
                InfoCard(title = "Konum ve zaman", body = "${item.locationTr} | ${item.distanceKm} km", status = item.urgency.labelTr())
            }
            item {
                InsightList(title = "AI özeti", items = listOf(item.aiSummaryTr))
            }
            item {
                PrimaryOverlayAction(
                    label = if (item.type == DiscoveryItemType.PROVIDER) "Talep taslağı oluştur" else "Teklif akışını başlat",
                    onClick = onBack
                )
            }
        }
    }
}

@Composable
internal fun RequestDraftScreen(
    repository: WorkflowRepository,
    onBack: () -> Unit
) {
    var draft by remember { mutableStateOf(repository.newRequestDraft()) }

    OverlayScreen(title = "Talep oluştur", onBack = onBack) {
        item {
            DraftStatusCard(draft)
        }
        item {
            OutlinedTextField(
                value = draft.titleTr,
                onValueChange = { draft = draft.copy(titleTr = it, status = RequestDraftStatus.EDITING) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Başlık") },
                singleLine = true,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
        }
        item {
            OutlinedTextField(
                value = draft.descriptionTr,
                onValueChange = { draft = draft.copy(descriptionTr = it, status = RequestDraftStatus.EDITING) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("İş detayı") },
                minLines = 4,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
        }
        item {
            CategoryPicker(
                selected = draft.categoryId,
                onSelected = { draft = draft.copy(categoryId = it, status = RequestDraftStatus.EDITING) }
            )
        }
        item {
            SchedulePicker(
                selected = draft.schedulePreference,
                onSelected = { draft = draft.copy(schedulePreference = it, status = RequestDraftStatus.EDITING) }
            )
        }
        item {
            OutlinedTextField(
                value = draft.locationTr,
                onValueChange = { draft = draft.copy(locationTr = it, status = RequestDraftStatus.EDITING) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Konum") },
                singleLine = true,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
        }
        item {
            OutlinedTextField(
                value = draft.budgetHintTr,
                onValueChange = { draft = draft.copy(budgetHintTr = it, status = RequestDraftStatus.EDITING) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Bütçe aralığı") },
                singleLine = true,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
        }
        item {
            InsightList(title = "AI önerileri", items = draft.aiSuggestionsTr)
        }
        item {
            PrimaryOverlayAction(
                label = if (draft.status == RequestDraftStatus.SENT_TO_MATCHING) "Eşleştirme başlatıldı" else "Eşleştirmeye gönder",
                onClick = {
                    val ready = repository.readyRequestDraft(draft)
                    draft = repository.submitRequestDraft(ready)
                }
            )
        }
    }
}

@Composable
private fun ExploreDetailHero(item: ExploreItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(ViaverseColors.WarmMuted),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(categoryDrawable(item.categoryId)),
                contentDescription = item.titleTr,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.titleTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(item.descriptionTr, color = ViaverseColors.MutedText, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
internal fun ProviderOnboardingScreen(
    repository: WorkflowRepository,
    onCompleted: () -> Unit,
    onBack: () -> Unit
) {
    var snapshot by remember { mutableStateOf(repository.providerOnboarding()) }

    OverlayScreen(title = "Hizmet verme", onBack = onBack) {
        item {
            HeaderBlock(snapshot.titleTr, snapshot.bodyTr)
        }
        item {
            InfoCard(title = "Çalışma alanı", body = snapshot.workAreaTr, status = "Kişisel mod")
        }
        item {
            CategoryStrip(snapshot.selectedCategories)
        }
        items(snapshot.steps, key = { it.titleTr }) { step ->
            InfoCard(title = step.titleTr, body = step.bodyTr, status = step.status.labelTr())
        }
        item {
            InsightList(title = "AI yönlendirmesi", items = snapshot.insights.map { it.summaryTr })
        }
        item {
            PrimaryOverlayAction(
                label = if (snapshot.steps.all { it.status == ProviderSetupStepStatus.DONE }) "Panele dön" else "Sıradaki adımı tamamla",
                onClick = {
                    if (snapshot.steps.all { it.status == ProviderSetupStepStatus.DONE }) {
                        onCompleted()
                        onBack()
                    } else {
                        snapshot = repository.progressProviderOnboarding(snapshot)
                    }
                }
            )
        }
    }
}

@Composable
internal fun BusinessWorkspaceScreen(
    repository: WorkflowRepository,
    summary: BusinessWorkspaceSummary,
    onBack: () -> Unit
) {
    var detail by remember(summary) { mutableStateOf(repository.businessWorkspace(summary)) }

    OverlayScreen(title = "İşletme workspace", onBack = onBack) {
        item {
            HeaderBlock(detail.summary.titleTr, detail.summary.aiReadinessTr)
        }
        item {
            InfoCard(
                title = "Doğrulama",
                body = detail.summary.verificationStepTr,
                status = detail.summary.subscriptionStateTr
            )
        }
        items(detail.steps, key = { it.titleTr }) { step ->
            InfoCard(title = step.titleTr, body = step.bodyTr, status = step.status.labelTr())
        }
        item {
            InfoCard(
                title = "Abonelik ve merchant",
                body = "${detail.subscription.planNameTr}\n${detail.subscription.renewalHintTr}\n${detail.subscription.merchantStatusTr}",
                status = detail.subscription.state.labelTr()
            )
        }
        items(detail.teamMembers, key = { it.id }) { member ->
            InfoCard(
                title = member.nameTr,
                body = member.statusTr,
                status = member.role.labelTr()
            )
        }
        items(detail.catalogItems, key = { it.id }) { catalogItem ->
            InfoCard(
                title = catalogItem.titleTr,
                body = "${catalogItem.categoryId.labelTr()} | ${catalogItem.priceHintTr}",
                status = catalogItem.status.labelTr()
            )
        }
        item {
            InsightList(title = "Yayın kontrolleri", items = detail.publishingChecksTr)
        }
        item {
            InsightList(title = "AI yönlendirmesi", items = detail.insights.map { it.recommendedNextActionTr })
        }
        item {
            PrimaryOverlayAction(
                label = if (detail.steps.all { it.status == ProviderSetupStepStatus.DONE }) "Workspace yayında" else "Sıradaki workspace adımını tamamla",
                onClick = {
                    if (!detail.steps.all { it.status == ProviderSetupStepStatus.DONE }) {
                        detail = repository.progressBusinessWorkspace(detail)
                    }
                }
            )
        }
    }
}

@Composable
internal fun ProfileSettingsScreen(
    repository: WorkflowRepository,
    onOpenSetting: (String) -> Unit,
    onBack: () -> Unit
) {
    val groups = remember { repository.profileSettings() }

    OverlayScreen(title = "Ayarlar", onBack = onBack) {
        groups.forEach { group ->
            item {
                SettingGroupCard(group, onOpenSetting)
            }
        }
    }
}

@Composable
internal fun ChatDetailScreen(
    repository: WorkflowRepository,
    conversationId: String,
    onBack: () -> Unit
) {
    val thread = remember(conversationId) { repository.chatThread(conversationId) }
    var messageText by remember(conversationId) { mutableStateOf("") }
    var messages by remember(conversationId) { mutableStateOf(thread.messages) }

    OverlayScreen(title = "Mesaj", onBack = onBack) {
        item {
            HeaderBlock(thread.titleTr, thread.contextTr)
        }
        item {
            PolicyCard(thread.policyState, thread.aiSafetyNotesTr)
        }
        items(messages, key = { it.id }) { message ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (message.fromCurrentUser) Arrangement.End else Arrangement.Start
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .clip(RoundedCornerShape(Dimensions.RadiusMd))
                        .background(if (message.fromCurrentUser) ViaverseColors.DeepGreen else ViaverseColors.CardSurface)
                        .border(
                            1.dp,
                            if (message.fromCurrentUser) ViaverseColors.DeepGreen else ViaverseColors.BorderSubtle,
                            RoundedCornerShape(Dimensions.RadiusMd)
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = message.textTr,
                        color = if (message.fromCurrentUser) ViaverseColors.OnBrand else ViaverseColors.Ink,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = message.timeTr,
                        color = if (message.fromCurrentUser) ViaverseColors.OnBrand.copy(alpha = 0.7f) else ViaverseColors.MutedText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mesaj yaz") },
                minLines = 2,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
        }
        item {
            PrimaryOverlayAction(
                label = "Gönder",
                onClick = {
                    val trimmed = messageText.trim()
                    if (trimmed.isNotEmpty()) {
                        messages = messages + ChatMessage(
                            id = "local_${messages.size + 1}",
                            fromCurrentUser = true,
                            textTr = trimmed,
                            timeTr = "Şimdi"
                        )
                        messageText = ""
                    }
                }
            )
        }
    }
}

@Composable
internal fun OverlayScreen(
    title: String,
    onBack: () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ViaverseColors.WarmBase)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ViaverseColors.WarmSurface)
                .padding(horizontal = Dimensions.SpaceLg, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Geri",
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onBack)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                color = ViaverseColors.BrandOrange,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = ViaverseColors.Ink,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(42.dp))
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimensions.SpaceLg),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
private fun DraftStatusCard(draft: RequestDraft) {
    InfoCard(
        title = "Talep durumu",
        body = "${draft.categoryId.labelTr()} | ${draft.locationTr} | ${draft.budgetHintTr}",
        status = draft.status.labelTr()
    )
}

@Composable
internal fun HeaderBlock(
    title: String,
    body: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(body, color = ViaverseColors.MutedText, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun InfoCard(
    title: String,
    body: String,
    status: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = ViaverseColors.Ink,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = status,
                color = ViaverseColors.BrandOrange,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Text(body, color = ViaverseColors.MutedText, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
internal fun MetricGrid(metrics: List<app.viaverse.template.domain.model.ProviderMetric>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        metrics.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { metric ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Dimensions.RadiusMd))
                            .background(ViaverseColors.CardSurface)
                            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(metric.valueTr, color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(metric.titleTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(metric.bodyTr, color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryPicker(
    selected: ServiceCategoryId,
    onSelected: (ServiceCategoryId) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Kategori", color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        CategoryStrip(
            categories = listOf(
                ServiceCategoryId.HOME_REPAIR,
                ServiceCategoryId.CLEANING,
                ServiceCategoryId.LOCAL_HELP,
                ServiceCategoryId.DIGITAL_SOFTWARE,
                ServiceCategoryId.PROFESSIONAL_CONSULTING
            ),
            selected = selected,
            onSelected = onSelected
        )
    }
}

@Composable
private fun CategoryStrip(categories: List<ServiceCategoryId>) {
    CategoryStrip(categories = categories, selected = null, onSelected = null)
}

@Composable
private fun CategoryStrip(
    categories: List<ServiceCategoryId>,
    selected: ServiceCategoryId?,
    onSelected: ((ServiceCategoryId) -> Unit)?
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(categories, key = { it.name }) { categoryId ->
            val active = selected == categoryId
            Column(
                modifier = Modifier
                    .size(width = 112.dp, height = 104.dp)
                    .clip(RoundedCornerShape(Dimensions.RadiusMd))
                    .background(if (active) ViaverseColors.WarmMuted else ViaverseColors.CardSurface)
                    .border(
                        1.dp,
                        if (active) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle,
                        RoundedCornerShape(Dimensions.RadiusMd)
                    )
                    .then(if (onSelected != null) Modifier.clickable { onSelected(categoryId) } else Modifier)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(categoryDrawable(categoryId)),
                    contentDescription = categoryId.labelTr(),
                    modifier = Modifier.size(34.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = categoryId.labelTr(),
                    color = ViaverseColors.Ink,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SchedulePicker(
    selected: SchedulePreference,
    onSelected: (SchedulePreference) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Zaman", color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SchedulePreference.entries.forEach { preference ->
                val active = selected == preference
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) ViaverseColors.DeepGreen else ViaverseColors.CardSurface)
                        .border(
                            1.dp,
                            if (active) ViaverseColors.DeepGreen else ViaverseColors.BorderSubtle,
                            RoundedCornerShape(999.dp)
                        )
                        .clickable { onSelected(preference) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = preference.labelTr(),
                        color = if (active) ViaverseColors.OnBrand else ViaverseColors.Ink,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun InsightList(
    title: String,
    items: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.DeepGreen)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        items.forEach { item ->
            Text(item, color = ViaverseColors.OnBrand, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SettingGroupCard(
    group: ProfileSettingGroup,
    onOpenSetting: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(group.titleTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        group.items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimensions.RadiusSm))
                    .clickable { onOpenSetting(item.id) }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(item.titleTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(item.bodyTr, color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
                }
                Text(item.statusTr, color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PolicyCard(
    state: ChatPolicyState,
    notes: List<String>
) {
    InsightList(
        title = state.labelTr(),
        items = notes
    )
}

@Composable
internal fun PrimaryOverlayAction(
    label: String,
    onClick: () -> Unit
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
        Text(label, color = ViaverseColors.OnBrand, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

private fun RequestDraftStatus.labelTr(): String {
    return when (this) {
        RequestDraftStatus.EDITING -> "Düzenleniyor"
        RequestDraftStatus.READY_TO_MATCH -> "Hazır"
        RequestDraftStatus.SENT_TO_MATCHING -> "Gönderildi"
    }
}

private fun SchedulePreference.labelTr(): String {
    return when (this) {
        SchedulePreference.TODAY -> "Bugün"
        SchedulePreference.THIS_WEEK -> "Bu hafta"
        SchedulePreference.FLEXIBLE -> "Esnek"
    }
}

private fun DiscoveryUrgency.labelTr(): String {
    return when (this) {
        DiscoveryUrgency.ANY -> "Esnek"
        DiscoveryUrgency.TODAY -> "Bugün"
        DiscoveryUrgency.THIS_WEEK -> "Bu hafta"
        DiscoveryUrgency.FLEXIBLE -> "Esnek"
    }
}

private fun ProviderSetupStepStatus.labelTr(): String {
    return when (this) {
        ProviderSetupStepStatus.TODO -> "Bekliyor"
        ProviderSetupStepStatus.READY -> "Hazır"
        ProviderSetupStepStatus.DONE -> "Tamam"
    }
}

private fun ChatPolicyState.labelTr(): String {
    return when (this) {
        ChatPolicyState.PRE_ACCEPTANCE_RESTRICTED -> "İş kabulü öncesi güvenlik"
        ChatPolicyState.JOB_ACCEPTED -> "İş kabul edildi"
        ChatPolicyState.SUPPORT_REVIEW -> "Destek incelemesi"
    }
}

internal fun CustomerJobStatus.labelTr(): String {
    return when (this) {
        CustomerJobStatus.MATCHING -> "Eşleşiyor"
        CustomerJobStatus.OFFER_RECEIVED -> "Teklif geldi"
        CustomerJobStatus.ACCEPTED -> "Kabul edildi"
        CustomerJobStatus.IN_PROGRESS -> "Devam ediyor"
        CustomerJobStatus.COMPLETED -> "Tamamlandı"
        CustomerJobStatus.DISPUTED -> "Uyuşmazlık"
    }
}

internal fun OfferStatus.labelTr(): String {
    return when (this) {
        OfferStatus.PENDING -> "Bekliyor"
        OfferStatus.ACCEPTED -> "Kabul"
        OfferStatus.DECLINED -> "Reddedildi"
        OfferStatus.EXPIRED -> "Süresi doldu"
    }
}

internal fun SafePaymentStatus.labelTr(): String {
    return when (this) {
        SafePaymentStatus.NOT_STARTED -> "Başlamadı"
        SafePaymentStatus.AUTHORIZED -> "Yetkilendirildi"
        SafePaymentStatus.HELD_IN_ESCROW -> "Güvenli havuz"
        SafePaymentStatus.RELEASED -> "Serbest"
        SafePaymentStatus.REFUND_REVIEW -> "İade inceleme"
        SafePaymentStatus.DISPUTED -> "Uyuşmazlık"
    }
}

internal fun ProviderAvailability.labelTr(): String {
    return when (this) {
        ProviderAvailability.AVAILABLE -> "Müsait"
        ProviderAvailability.BUSY -> "Yoğun"
        ProviderAvailability.PAUSED -> "Duraklatıldı"
    }
}

internal fun ReviewStatus.labelTr(): String {
    return when (this) {
        ReviewStatus.WAITING_FOR_YOU -> "Seni bekliyor"
        ReviewStatus.WAITING_FOR_COUNTERPARTY -> "Karşı taraf"
        ReviewStatus.PUBLISHED -> "Yayında"
    }
}

internal fun SupportTicketStatus.labelTr(): String {
    return when (this) {
        SupportTicketStatus.OPEN -> "Açık"
        SupportTicketStatus.WAITING_FOR_USER -> "Yanıt bekliyor"
        SupportTicketStatus.ESCALATED -> "Eskalasyon"
        SupportTicketStatus.RESOLVED -> "Çözüldü"
    }
}

private fun BusinessTeamRole.labelTr(): String {
    return when (this) {
        BusinessTeamRole.OWNER -> "Sahip"
        BusinessTeamRole.MANAGER -> "Yönetici"
        BusinessTeamRole.STAFF -> "Personel"
    }
}

private fun CatalogItemStatus.labelTr(): String {
    return when (this) {
        CatalogItemStatus.DRAFT -> "Taslak"
        CatalogItemStatus.READY -> "Hazır"
        CatalogItemStatus.PUBLISHED -> "Yayında"
    }
}

private fun SubscriptionState.labelTr(): String {
    return when (this) {
        SubscriptionState.TRIAL -> "Deneme"
        SubscriptionState.ACTIVE -> "Aktif"
        SubscriptionState.PAYMENT_REQUIRED -> "Ödeme gerekli"
    }
}

private fun WorkLifecycleStatus.labelTr(): String {
    return when (this) {
        WorkLifecycleStatus.ONBOARDING_REQUIRED -> "Kurulum gerekli"
        WorkLifecycleStatus.OPEN -> "Açık"
        WorkLifecycleStatus.OFFER_SENT -> "Teklif verildi"
        WorkLifecycleStatus.IN_PROGRESS -> "Devam ediyor"
    }
}

internal fun ServiceCategoryId.labelTr(): String {
    return when (this) {
        ServiceCategoryId.HOME_REPAIR -> "Ev tamiri"
        ServiceCategoryId.CLEANING -> "Temizlik"
        ServiceCategoryId.EDUCATION -> "Eğitim"
        ServiceCategoryId.CARE_HEALTH -> "Bakım"
        ServiceCategoryId.LOGISTICS -> "Lojistik"
        ServiceCategoryId.CREATIVE_MEDIA -> "Yaratıcı"
        ServiceCategoryId.DIGITAL_SOFTWARE -> "Yazılım"
        ServiceCategoryId.PROFESSIONAL_CONSULTING -> "Danışmanlık"
        ServiceCategoryId.LOCAL_HELP -> "Yerel yardım"
        ServiceCategoryId.EVENTS -> "Etkinlik"
        ServiceCategoryId.ANNOUNCEMENT -> "Duyuru"
        ServiceCategoryId.PETS -> "Evcil hayvan"
        ServiceCategoryId.WORK -> "Küçük iş"
    }
}
