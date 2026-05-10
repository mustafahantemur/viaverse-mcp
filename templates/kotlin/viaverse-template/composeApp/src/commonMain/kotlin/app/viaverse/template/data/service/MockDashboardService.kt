package app.viaverse.template.data.service

import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AccountCapability
import app.viaverse.template.domain.model.AiInsight
import app.viaverse.template.domain.model.BusinessWorkspaceStatus
import app.viaverse.template.domain.model.BusinessWorkspaceSummary
import app.viaverse.template.domain.model.CapabilitySummary
import app.viaverse.template.domain.model.ConversationPreview
import app.viaverse.template.domain.model.ConversationStatus
import app.viaverse.template.domain.model.DashboardSnapshot
import app.viaverse.template.domain.model.RequestLifecycleStatus
import app.viaverse.template.domain.model.RequestSummary
import app.viaverse.template.domain.model.ServiceCategoryId
import app.viaverse.template.domain.model.WorkLifecycleStatus
import app.viaverse.template.domain.model.WorkSummary

class MockDashboardService {
    fun loadDashboard(account: Account?): DashboardSnapshot {
        return DashboardSnapshot(
            requests = requestSummaries(),
            work = workSummaries(account),
            conversations = conversationPreviews(),
            capabilities = capabilitySummaries(account),
            businessWorkspace = businessWorkspace(account),
            insights = insights(account)
        )
    }

    private fun requestSummaries(): List<RequestSummary> {
        return listOf(
            RequestSummary(
                id = "job_001",
                titleTr = "Mutfak musluğu değişimi",
                categoryId = ServiceCategoryId.HOME_REPAIR,
                status = RequestLifecycleStatus.OFFER_RECEIVED,
                locationTr = "Kadıköy, İstanbul",
                budgetHintTr = "750-1.200 TL",
                aiSummaryTr = "Kapsam net; iki teklif fiyat ve doğrulama sinyaline göre karşılaştırılmalı."
            ),
            RequestSummary(
                id = "job_002",
                titleTr = "Haftalık ev temizliği",
                categoryId = ServiceCategoryId.CLEANING,
                status = RequestLifecycleStatus.MATCHING,
                locationTr = "Beşiktaş, İstanbul",
                budgetHintTr = "Saatlik 350 TL",
                aiSummaryTr = "Tekrarlı iş olduğu için ekip puanı ve iptal geçmişi öne alınmalı."
            )
        )
    }

    private fun workSummaries(account: Account?): List<WorkSummary> {
        val hasWorkCapability = account?.capabilities?.contains(AccountCapability.DO_WORK_INDIVIDUALLY) == true
        if (!hasWorkCapability) {
            return listOf(
                WorkSummary(
                    id = "work_onboarding",
                    titleTr = "Bireysel hizmet verme profili",
                    categoryId = ServiceCategoryId.WORK,
                    status = WorkLifecycleStatus.ONBOARDING_REQUIRED,
                    payoutHintTr = "Profil açılışı",
                    distanceKm = 0.0,
                    trustFitTr = "Kimlik, kategori, müsaitlik ve ödeme bilgisi tamamlanmalı."
                )
            )
        }

        return listOf(
            WorkSummary(
                id = "work_001",
                titleTr = "Bugün kısa tesisat işi",
                categoryId = ServiceCategoryId.HOME_REPAIR,
                status = WorkLifecycleStatus.OPEN,
                payoutHintTr = "900 TL civarı",
                distanceKm = 2.4,
                trustFitTr = "Yakın mesafe ve net kapsam."
            ),
            WorkSummary(
                id = "work_002",
                titleTr = "Küçük işletme web sayfası",
                categoryId = ServiceCategoryId.DIGITAL_SOFTWARE,
                status = WorkLifecycleStatus.OFFER_SENT,
                payoutHintTr = "4.000-7.500 TL",
                distanceKm = 8.6,
                trustFitTr = "Portföy örneği isteyen düşük riskli teklif."
            )
        )
    }

    private fun conversationPreviews(): List<ConversationPreview> {
        return listOf(
            ConversationPreview(
                id = "msg_001",
                titleTr = "Mutfak musluğu teklifi",
                lastMessageTr = "Teklif detayını ve uygun saat aralığını onaylayın.",
                status = ConversationStatus.NEEDS_REPLY,
                contextTr = "Teklif"
            ),
            ConversationPreview(
                id = "msg_002",
                titleTr = "Destek kontrolü",
                lastMessageTr = "İletişim paylaşımı politikaya göre iş kabulünden sonra açılır.",
                status = ConversationStatus.SUPPORT_REVIEW,
                contextTr = "Güvenlik"
            )
        )
    }

    private fun capabilitySummaries(account: Account?): List<CapabilitySummary> {
        val capabilities = account?.capabilities.orEmpty()
        return listOf(
            CapabilitySummary(
                capability = AccountCapability.REQUEST_WORK,
                titleTr = "Hizmet talep et",
                bodyTr = "Kategori, bütçe, konum ve güvenli ödeme akışıyla talep oluştur.",
                enabled = capabilities.contains(AccountCapability.REQUEST_WORK)
            ),
            CapabilitySummary(
                capability = AccountCapability.DO_WORK_INDIVIDUALLY,
                titleTr = "Bireysel hizmet ver",
                bodyTr = "Airbnb benzeri hafif geçiş: kimlik, kategori, müsaitlik ve ödeme bilgisi.",
                enabled = capabilities.contains(AccountCapability.DO_WORK_INDIVIDUALLY)
            ),
            CapabilitySummary(
                capability = AccountCapability.OPERATE_BUSINESS,
                titleTr = "İşletme workspace",
                bodyTr = "Ayrı doğrulama, ekip rolleri, katalog, abonelik ve yayın hazırlığı gerektirir.",
                enabled = capabilities.contains(AccountCapability.OPERATE_BUSINESS)
            )
        )
    }

    private fun businessWorkspace(account: Account?): BusinessWorkspaceSummary {
        val hasBusinessCapability = account?.capabilities?.contains(AccountCapability.OPERATE_BUSINESS) == true
        return if (hasBusinessCapability) {
            BusinessWorkspaceSummary(
                status = BusinessWorkspaceStatus.PENDING_VERIFICATION,
                titleTr = "İşletme doğrulaması",
                verificationStepTr = "Ticari bilgi ve yetkili kişi doğrulaması bekliyor.",
                subscriptionStateTr = "İlk ay ücretsiz abonelik hazırlığı",
                aiReadinessTr = "Katalog, hizmet bölgesi ve yayın kriterleri tamamlanmadan public profil açılmaz."
            )
        } else {
            BusinessWorkspaceSummary(
                status = BusinessWorkspaceStatus.NOT_STARTED,
                titleTr = "İşletme hesabı ekle",
                verificationStepTr = "İşletme ayrı workspace olarak açılır; kişisel iş modundan farklıdır.",
                subscriptionStateTr = "Abonelik ve merchant bilgisi daha sonra istenir.",
                aiReadinessTr = "AI yalnızca hazırlık eksiklerini önerir; yayın kararı policy ile verilir."
            )
        }
    }

    private fun insights(account: Account?): List<AiInsight> {
        val hasWorkCapability = account?.capabilities?.contains(AccountCapability.DO_WORK_INDIVIDUALLY) == true
        return listOf(
            AiInsight(
                screenId = "main",
                summaryTr = if (hasWorkCapability) {
                    "Hesap hem talep oluşturma hem bireysel hizmet verme akışlarını gösterebilir."
                } else {
                    "Hesap şu an talep oluşturma ağırlıklı; bireysel hizmet verme ayrı onboarding ile açılmalı."
                },
                recommendedNextActionTr = "Business workspace kişisel mod geçişinden ayrı tutulmalı.",
                riskSignalsTr = listOf("Ödeme ve iletişim policy durumuna göre açılmalı.")
            )
        )
    }
}
