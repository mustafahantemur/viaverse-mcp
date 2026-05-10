package app.viaverse.template.data.service

import app.viaverse.template.domain.model.AiInsight
import app.viaverse.template.domain.model.BusinessSetupStep
import app.viaverse.template.domain.model.BusinessWorkspaceDetail
import app.viaverse.template.domain.model.BusinessWorkspaceSummary
import app.viaverse.template.domain.model.ChatMessage
import app.viaverse.template.domain.model.ChatPolicyState
import app.viaverse.template.domain.model.ChatThread
import app.viaverse.template.domain.model.ProfileSettingGroup
import app.viaverse.template.domain.model.ProfileSettingItem
import app.viaverse.template.domain.model.ProfileSettingKind
import app.viaverse.template.domain.model.ProviderOnboardingSnapshot
import app.viaverse.template.domain.model.ProviderSetupStep
import app.viaverse.template.domain.model.ProviderSetupStepStatus
import app.viaverse.template.domain.model.RequestDraft
import app.viaverse.template.domain.model.RequestDraftStatus
import app.viaverse.template.domain.model.SchedulePreference
import app.viaverse.template.domain.model.ServiceCategoryId

class MockWorkflowService {
    fun newRequestDraft(): RequestDraft {
        return RequestDraft(
            titleTr = "Mutfak musluğu değişimi",
            descriptionTr = "Bugün veya bu hafta küçük bir tesisat işi için doğrulanmış sağlayıcılardan teklif almak istiyorum.",
            categoryId = ServiceCategoryId.HOME_REPAIR,
            locationTr = "Kadıköy, İstanbul",
            budgetHintTr = "750-1.200 TL",
            schedulePreference = SchedulePreference.THIS_WEEK,
            status = RequestDraftStatus.EDITING,
            aiSuggestionsTr = listOf(
                "Kapsamı net tuttuğun için eşleşme kalitesi yüksek olur.",
                "Fotoğraf eklendiğinde fiyat aralığı daha doğru önerilir.",
                "Ödeme ve iletişim iş kabul edilene kadar uygulama içinde kalmalı."
            )
        )
    }

    fun readyRequestDraft(draft: RequestDraft): RequestDraft {
        return draft.copy(status = RequestDraftStatus.READY_TO_MATCH)
    }

    fun submitRequestDraft(draft: RequestDraft): RequestDraft {
        return draft.copy(status = RequestDraftStatus.SENT_TO_MATCHING)
    }

    fun providerOnboarding(): ProviderOnboardingSnapshot {
        return ProviderOnboardingSnapshot(
            titleTr = "Bireysel hizmet verme kurulumu",
            bodyTr = "Kişisel hesaptan hafif bir geçişle hizmet verebilirsin; işletme workspace bundan ayrı açılır.",
            selectedCategories = listOf(ServiceCategoryId.HOME_REPAIR, ServiceCategoryId.LOCAL_HELP, ServiceCategoryId.DIGITAL_SOFTWARE),
            workAreaTr = "5 km yakın çevre, uzaktan dijital işler açık",
            steps = listOf(
                ProviderSetupStep("Kimlik ve güven", "Kimlik, telefon ve 2FA kontrolü.", ProviderSetupStepStatus.DONE),
                ProviderSetupStep("Kategori seçimi", "Hangi işlerde teklif vereceğin.", ProviderSetupStepStatus.READY),
                ProviderSetupStep("Müsaitlik ve bölge", "Konum, saat ve uzaktan çalışma tercihi.", ProviderSetupStepStatus.READY),
                ProviderSetupStep("Ödeme alma", "Payout bilgisi ve güvenli ödeme politikası.", ProviderSetupStepStatus.TODO)
            ),
            insights = listOf(
                AiInsight(
                    screenId = "provider_onboarding",
                    summaryTr = "Bireysel hizmet modu hızlı açılır; business doğrulaması gerektirmez.",
                    recommendedNextActionTr = "Önce kategori ve çalışma alanını tamamla, sonra teklif gönder.",
                    riskSignalsTr = listOf("İletişim paylaşımı iş kabulünden önce kısıtlıdır.")
                )
            )
        )
    }

    fun businessWorkspace(detail: BusinessWorkspaceSummary): BusinessWorkspaceDetail {
        return BusinessWorkspaceDetail(
            summary = detail,
            steps = listOf(
                BusinessSetupStep("İşletme bilgileri", "Ticari unvan, vergi ve yetkili kişi bilgisi.", ProviderSetupStepStatus.READY),
                BusinessSetupStep("Merchant onboarding", "iyzico ödeme alma ve payout hazırlığı.", ProviderSetupStepStatus.TODO),
                BusinessSetupStep("Ekip ve roller", "Personel erişimi, işletme yetkileri ve audit izi.", ProviderSetupStepStatus.TODO),
                BusinessSetupStep("Katalog ve yayın", "Hizmet katalogları, bölge, fiyat ipuçları ve SEO görünümü.", ProviderSetupStepStatus.TODO)
            ),
            publishingChecksTr = listOf(
                "Doğrulama tamamlanmadan public işletme profili yayınlanmaz.",
                "Abonelik ve merchant durumu publish-ready policy içinde değerlendirilir.",
                "Kişisel hizmet verme modu ile işletme workspace yetkileri karıştırılmaz."
            ),
            insights = listOf(
                AiInsight(
                    screenId = "business_workspace",
                    summaryTr = "Business akışı ayrı kaynak ve doğrulama gerektirir.",
                    recommendedNextActionTr = "Önce işletme sahibi ve merchant bilgilerini tamamla.",
                    riskSignalsTr = listOf("Admin veya işletme yetkisi gerektiren eylemler audit log ister.")
                )
            )
        )
    }

    fun profileSettings(): List<ProfileSettingGroup> {
        return listOf(
            ProfileSettingGroup(
                titleTr = "Kimlik ve profil",
                kind = ProfileSettingKind.IDENTITY,
                items = listOf(
                    ProfileSettingItem("name_photo", "Ad ve fotoğraf", "Public güven sinyalinde görünen temel profil.", "Tamamlandı"),
                    ProfileSettingItem("phone", "Telefon doğrulama", "OTP ve güvenlik bildirimleri için kullanılır.", "Doğrulandı"),
                    ProfileSettingItem("edevlet", "Kimlik doğrulama", "Yüksek güvenli işlerde öne çıkar.", "Hazır")
                )
            ),
            ProfileSettingGroup(
                titleTr = "Güvenlik",
                kind = ProfileSettingKind.SECURITY,
                items = listOf(
                    ProfileSettingItem("2fa", "İki aşamalı doğrulama", "Ödeme ve güvenlik işlemleri için step-up.", "Aktif"),
                    ProfileSettingItem("devices", "Cihazlar", "Aktif oturumları ve cihazları yönet.", "2 cihaz"),
                    ProfileSettingItem("password", "Şifre", "Giriş güvenliği ve yenileme.", "Güncel")
                )
            ),
            ProfileSettingGroup(
                titleTr = "Ödeme ve belgeler",
                kind = ProfileSettingKind.PAYMENT,
                items = listOf(
                    ProfileSettingItem("cards", "Kartlar", "Kart verisi saklanmaz; sağlayıcı tokenı kullanılır.", "Tokenlı"),
                    ProfileSettingItem("escrow", "Güvenli ödeme", "İş tamamlanana kadar tutulan ödeme.", "2 işlem"),
                    ProfileSettingItem("receipts", "Makbuzlar", "İş ve ödeme geçmişi.", "Hazır")
                )
            ),
            ProfileSettingGroup(
                titleTr = "Gizlilik ve destek",
                kind = ProfileSettingKind.PRIVACY,
                items = listOf(
                    ProfileSettingItem("kvkk", "KVKK ve izinler", "Veri izinleri ve silme talepleri.", "Görüntüle"),
                    ProfileSettingItem("notifications", "Bildirimler", "İş, teklif, chat ve güvenlik bildirimleri.", "Açık"),
                    ProfileSettingItem("support", "Destek", "Uyuşmazlık, rapor ve güvenlik desteği.", "7/24")
                )
            )
        )
    }

    fun chatThread(conversationId: String): ChatThread {
        val supportReview = conversationId == "msg_002"
        return ChatThread(
            id = conversationId,
            titleTr = if (supportReview) "Destek kontrolü" else "Mutfak musluğu teklifi",
            contextTr = if (supportReview) "Güvenlik incelemesi" else "Teklif ve zamanlama",
            policyState = if (supportReview) ChatPolicyState.SUPPORT_REVIEW else ChatPolicyState.PRE_ACCEPTANCE_RESTRICTED,
            messages = listOf(
                ChatMessage("m1", false, "Merhaba, teklif kapsamını ve uygun saat aralığını paylaşabilir misiniz?", "10:42"),
                ChatMessage("m2", true, "Bugün 18:00 sonrası veya yarın sabah uygun. Fotoğraf ekliyorum.", "10:45"),
                ChatMessage("m3", false, "İş kabul edilmeden telefon paylaşımı kapalı; buradan ilerleyebiliriz.", "10:46")
            ),
            aiSafetyNotesTr = listOf(
                "İletişim bilgisi paylaşımı iş kabulünden önce kısıtlı.",
                "Ödeme uygulama dışına taşarsa risk sinyali oluşturulur."
            )
        )
    }
}
