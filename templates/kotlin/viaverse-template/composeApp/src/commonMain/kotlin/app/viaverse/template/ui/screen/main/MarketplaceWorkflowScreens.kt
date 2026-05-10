package app.viaverse.template.ui.screen.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.viaverse.template.data.repository.WorkflowRepository
import app.viaverse.template.domain.model.CustomerJobStatus
import app.viaverse.template.domain.model.OfferStatus
import app.viaverse.template.domain.model.ReviewStatus
import app.viaverse.template.domain.model.SafePaymentStatus
import app.viaverse.template.domain.model.WorkLifecycleStatus
import app.viaverse.template.ui.theme.Dimensions

@Composable
internal fun CustomerJobDetailScreen(
    repository: WorkflowRepository,
    jobId: String,
    onOpenChat: (String) -> Unit,
    onBack: () -> Unit
) {
    val job = remember(jobId) { repository.customerJobs().firstOrNull { it.id == jobId } }
    val offers = remember(jobId) { repository.offersForJob(jobId) }
    var selectedOfferId by remember(jobId) { mutableStateOf<String?>(null) }
    var acceptedOfferId by remember(jobId) { mutableStateOf<String?>(null) }

    OverlayScreen(title = "Talep detayı", onBack = onBack) {
        if (job == null) {
            item {
                InfoCard(
                    title = "Talep bulunamadı",
                    body = "Bu talep mock iş listesinde yok; liste ve detay id'leri servis katmanında eşleşmeli.",
                    status = "Boş"
                )
            }
        } else {
            item {
                HeaderBlock(job.titleTr, job.descriptionTr)
            }
            item {
                InfoCard(
                    title = "Durum ve ödeme",
                    body = "${job.categoryId.labelTr()} | ${job.locationTr} | ${job.scheduleTr}",
                    status = "${if (acceptedOfferId != null) CustomerJobStatus.ACCEPTED.labelTr() else job.status.labelTr()} / ${if (acceptedOfferId != null) SafePaymentStatus.AUTHORIZED.labelTr() else job.paymentStatus.labelTr()}"
                )
            }
            item {
                InsightList(title = "AI talep özeti", items = listOf(job.aiSummaryTr))
            }
            items(offers, key = { it.id }) { offer ->
                val offerStatus = when {
                    acceptedOfferId == offer.id -> OfferStatus.ACCEPTED
                    acceptedOfferId != null -> OfferStatus.DECLINED
                    else -> offer.status
                }
                InfoCard(
                    title = offer.providerNameTr,
                    body = "${offer.messageTr}\n${offer.trustSignalsTr.joinToString(" | ")}",
                    status = "${offer.priceTr} / ${offerStatus.labelTr()}",
                    onClick = { selectedOfferId = offer.id }
                )
            }
            if (selectedOfferId != null && acceptedOfferId == null) {
                item {
                    InfoCard(
                        title = "Seçilen teklif",
                        body = "Kabul edildiğinde güvenli ödeme yetkilendirilir ve iş kabulü sonrası chat politikası genişler.",
                        status = "Onay bekliyor"
                    )
                }
                item {
                    PrimaryOverlayAction(
                        label = "Güvenli ödemeyle kabul et",
                        onClick = { acceptedOfferId = selectedOfferId }
                    )
                }
            }
            if (acceptedOfferId != null) {
                item {
                    InsightList(
                        title = "Sonraki adımlar",
                        items = listOf(
                            "Ödeme yetkilendirildi; iş tamamlanana kadar güvenli ödeme akışı izlenir.",
                            "İş adresi ve operasyonel iletişim kabul sonrası paylaşılabilir.",
                            "Tamamlanınca iki taraflı yorum ekranı açılır."
                        )
                    )
                }
            }
            item {
                PrimaryOverlayAction(label = "Mesajlaşmaya geç", onClick = { onOpenChat("msg_001") })
            }
        }
    }
}

@Composable
internal fun ProviderDashboardScreen(
    repository: WorkflowRepository,
    onOpenProviderSetup: () -> Unit,
    onOpenChat: (String) -> Unit,
    onBack: () -> Unit
) {
    val snapshot = remember { repository.providerDashboard() }
    var selectedOpportunityId by remember { mutableStateOf<String?>(null) }
    var sentOpportunityIds by remember { mutableStateOf(setOf<String>()) }
    var offerPrice by remember { mutableStateOf("1.500 TL") }
    var offerMessage by remember { mutableStateOf("Kapsam net; malzeme ve zaman planını uygulama içinden onaylayabilirim.") }

    OverlayScreen(title = "Hizmet veren paneli", onBack = onBack) {
        item {
            HeaderBlock(
                title = "Bugünkü iş merkezi",
                body = "Müsaitlik: ${snapshot.availability.labelTr()}. Teklif, aktif iş ve profil hazırlığı tek yerden yönetilir."
            )
        }
        item {
            MetricGrid(snapshot.metrics)
        }
        item {
            InsightList(title = "Bugün", items = snapshot.todayTasksTr)
        }
        items(snapshot.opportunities, key = { it.id }) { opportunity ->
            val sent = sentOpportunityIds.contains(opportunity.id)
            InfoCard(
                title = opportunity.titleTr,
                body = "${opportunity.descriptionTr}\n${opportunity.requesterTr} | ${opportunity.locationTr} | ${opportunity.fitTr}",
                status = if (sent) "Teklif gönderildi" else opportunity.budgetTr,
                onClick = { selectedOpportunityId = opportunity.id }
            )
        }
        if (selectedOpportunityId != null) {
            item {
                OutlinedTextField(
                    value = offerPrice,
                    onValueChange = { offerPrice = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Teklif tutarı") },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimensions.RadiusMd)
                )
            }
            item {
                OutlinedTextField(
                    value = offerMessage,
                    onValueChange = { offerMessage = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Teklif mesajı") },
                    minLines = 3,
                    shape = RoundedCornerShape(Dimensions.RadiusMd)
                )
            }
            item {
                PrimaryOverlayAction(
                    label = "Teklifi gönder",
                    onClick = {
                        sentOpportunityIds = sentOpportunityIds + selectedOpportunityId.orEmpty()
                        selectedOpportunityId = null
                    }
                )
            }
        }
        items(snapshot.activeJobs, key = { it.id }) { job ->
            InfoCard(
                title = job.titleTr,
                body = "${job.requesterTr} | ${job.nextStepTr}",
                status = "${job.payoutTr} / ${job.status.providerLabelTr()}",
                onClick = { onOpenChat("msg_001") }
            )
        }
        item {
            InsightList(title = "AI yönlendirmesi", items = snapshot.insights.map { it.recommendedNextActionTr })
        }
        item {
            PrimaryOverlayAction(label = "Profil kurulumunu düzenle", onClick = onOpenProviderSetup)
        }
    }
}

private fun WorkLifecycleStatus.providerLabelTr(): String {
    return when (this) {
        WorkLifecycleStatus.ONBOARDING_REQUIRED -> "Kurulum gerekli"
        WorkLifecycleStatus.OPEN -> "Açık"
        WorkLifecycleStatus.OFFER_SENT -> "Teklif verildi"
        WorkLifecycleStatus.IN_PROGRESS -> "Devam ediyor"
    }
}

@Composable
internal fun WalletScreen(
    repository: WorkflowRepository,
    onBack: () -> Unit
) {
    val wallet = remember { repository.wallet() }
    var releasedTransactionIds by remember { mutableStateOf(setOf<String>()) }

    OverlayScreen(title = "Cüzdan", onBack = onBack) {
        item {
            HeaderBlock("Güvenli ödeme", wallet.providerTokenInfoTr)
        }
        item {
            InfoCard(
                title = "Güvenli havuz",
                body = "Havuzdaki tutar: ${wallet.escrowBalanceTr}\nÇekilebilir bakiye: ${wallet.payoutBalanceTr}",
                status = "Tokenlı"
            )
        }
        items(wallet.transactions, key = { it.id }) { transaction ->
            val released = releasedTransactionIds.contains(transaction.id)
            InfoCard(
                title = transaction.titleTr,
                body = "${transaction.dateTr} | ${transaction.amountTr}",
                status = if (released) SafePaymentStatus.RELEASED.labelTr() else transaction.status.labelTr(),
                onClick = if (transaction.status == SafePaymentStatus.HELD_IN_ESCROW) {
                    { releasedTransactionIds = releasedTransactionIds + transaction.id }
                } else {
                    null
                }
            )
        }
        item {
            InsightList(title = "Ödeme güvenliği", items = wallet.safetyNotesTr)
        }
    }
}

@Composable
internal fun ReviewsScreen(
    repository: WorkflowRepository,
    onBack: () -> Unit
) {
    val reviews = remember { repository.reviews() }
    var completedReviewIds by remember { mutableStateOf(setOf<String>()) }

    OverlayScreen(title = "Yorumlar", onBack = onBack) {
        item {
            HeaderBlock(
                title = "İki taraflı yorumlar",
                body = "Yorumlar iş tamamlandıktan sonra iki taraf için ayrı durumlarla takip edilir."
            )
        }
        items(reviews, key = { it.id }) { review ->
            val status = if (completedReviewIds.contains(review.id)) ReviewStatus.PUBLISHED else review.status
            InfoCard(
                title = review.titleTr,
                body = "${review.counterpartyTr} | ${review.bodyTr}",
                status = "${review.ratingTr} / ${status.labelTr()}",
                onClick = if (review.status == ReviewStatus.WAITING_FOR_YOU) {
                    { completedReviewIds = completedReviewIds + review.id }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
internal fun SupportCenterScreen(
    repository: WorkflowRepository,
    onBack: () -> Unit
) {
    val tickets = remember { repository.supportTickets() }
    var selectedTicketId by remember { mutableStateOf<String?>(null) }

    OverlayScreen(title = "Destek", onBack = onBack) {
        item {
            HeaderBlock(
                title = "Güvenlik ve destek merkezi",
                body = "Uyuşmazlık, rapor, ödeme ve chat policy sinyalleri bu merkezden izlenir."
            )
        }
        items(tickets, key = { it.id }) { ticket ->
            InfoCard(
                title = ticket.titleTr,
                body = "${ticket.bodyTr}\nSonraki adım: ${ticket.nextStepTr}",
                status = ticket.status.labelTr(),
                onClick = { selectedTicketId = ticket.id }
            )
        }
        if (selectedTicketId != null) {
            item {
                InfoCard(
                    title = "Destek yanıt taslağı",
                    body = "Seçilen kayıt için kullanıcıdan ek bilgi istenir, ödeme/chat/job state birlikte değerlendirilir.",
                    status = "Hazır"
                )
            }
        }
        item {
            InsightList(
                title = "Destek policy",
                items = listOf(
                    "Rapor paketleri gereksiz PII içermemeli.",
                    "Uyuşmazlıkta payout bloklanır.",
                    "Tekrarlı iletişim bypass denemesi risk sinyali üretir."
                )
            )
        }
    }
}

@Composable
internal fun PublicProfileScreen(
    repository: WorkflowRepository,
    onBack: () -> Unit
) {
    val profile = remember { repository.publicProfile() }

    OverlayScreen(title = "Public profil", onBack = onBack) {
        item {
            HeaderBlock(profile.displayNameTr, profile.headlineTr)
        }
        item {
            InfoCard(
                title = "Güven ve performans",
                body = "${profile.completedJobsTr}\n${profile.badgesTr.joinToString(" | ")}",
                status = profile.ratingTr
            )
        }
        item {
            InsightList(title = "Yorum öne çıkanları", items = profile.reviewHighlightsTr)
        }
        item {
            InsightList(title = "Portföy", items = profile.portfolioTr)
        }
    }
}

@Composable
internal fun SettingDetailScreen(
    repository: WorkflowRepository,
    itemId: String,
    onBack: () -> Unit
) {
    val detail = remember(itemId) { repository.settingDetail(itemId) }

    OverlayScreen(title = "Ayar detayı", onBack = onBack) {
        item {
            HeaderBlock(detail.titleTr, detail.bodyTr)
        }
        item {
            InsightList(title = "Alanlar", items = detail.fieldsTr)
        }
        item {
            InsightList(title = "Risk notları", items = detail.riskNotesTr)
        }
    }
}
