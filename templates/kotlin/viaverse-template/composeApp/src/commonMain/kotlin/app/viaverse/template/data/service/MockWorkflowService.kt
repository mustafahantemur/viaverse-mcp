package app.viaverse.template.data.service

import app.viaverse.template.domain.model.AiInsight
import app.viaverse.template.domain.model.BusinessSetupStep
import app.viaverse.template.domain.model.BusinessWorkspaceStatus
import app.viaverse.template.domain.model.BusinessWorkspaceDetail
import app.viaverse.template.domain.model.BusinessWorkspaceSummary
import app.viaverse.template.domain.model.ChatMessage
import app.viaverse.template.domain.model.ChatPolicyState
import app.viaverse.template.domain.model.ChatThread
import app.viaverse.template.domain.model.CustomerJob
import app.viaverse.template.domain.model.CustomerJobStatus
import app.viaverse.template.domain.model.OfferPreview
import app.viaverse.template.domain.model.OfferStatus
import app.viaverse.template.domain.model.ProfileSettingGroup
import app.viaverse.template.domain.model.ProfileSettingItem
import app.viaverse.template.domain.model.ProfileSettingKind
import app.viaverse.template.domain.model.ProviderAvailability
import app.viaverse.template.domain.model.ProviderDashboardSnapshot
import app.viaverse.template.domain.model.ProviderJob
import app.viaverse.template.domain.model.ProviderMetric
import app.viaverse.template.domain.model.ProviderOnboardingSnapshot
import app.viaverse.template.domain.model.ProviderOpportunity
import app.viaverse.template.domain.model.ProviderSetupStep
import app.viaverse.template.domain.model.ProviderSetupStepStatus
import app.viaverse.template.domain.model.PublicProfileSnapshot
import app.viaverse.template.domain.model.ReviewStatus
import app.viaverse.template.domain.model.ReviewSummary
import app.viaverse.template.domain.model.RequestDraft
import app.viaverse.template.domain.model.RequestDraftStatus
import app.viaverse.template.domain.model.SafePaymentStatus
import app.viaverse.template.domain.model.SchedulePreference
import app.viaverse.template.domain.model.ServiceCategoryId
import app.viaverse.template.domain.model.SettingDetail
import app.viaverse.template.domain.model.SupportTicket
import app.viaverse.template.domain.model.SupportTicketStatus
import app.viaverse.template.domain.model.WalletSnapshot
import app.viaverse.template.domain.model.WalletTransaction
import app.viaverse.template.domain.model.WorkLifecycleStatus

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

    fun customerJobs(): List<CustomerJob> {
        return listOf(
            CustomerJob(
                id = "job_001",
                titleTr = "Mutfak musluğu değişimi",
                categoryId = ServiceCategoryId.HOME_REPAIR,
                status = CustomerJobStatus.OFFER_RECEIVED,
                locationTr = "Kadıköy, İstanbul",
                scheduleTr = "Bugün veya bu hafta",
                descriptionTr = "Mutfak musluğu değişimi ve küçük tesisat kontrolü.",
                offerCount = 2,
                paymentStatus = SafePaymentStatus.NOT_STARTED,
                aiSummaryTr = "İki teklif geldi; güven puanı, iptal geçmişi ve kapsam netliği karşılaştırılmalı."
            ),
            CustomerJob(
                id = "job_002",
                titleTr = "Haftalık ev temizliği",
                categoryId = ServiceCategoryId.CLEANING,
                status = CustomerJobStatus.MATCHING,
                locationTr = "Beşiktaş, İstanbul",
                scheduleTr = "Haftalık tekrar",
                descriptionTr = "3+1 ev için düzenli temizlik planı.",
                offerCount = 0,
                paymentStatus = SafePaymentStatus.NOT_STARTED,
                aiSummaryTr = "Talep yayında; uygun sağlayıcı havuzu genişletiliyor."
            ),
            CustomerJob(
                id = "job_003",
                titleTr = "Logo ve sosyal medya şablonu",
                categoryId = ServiceCategoryId.CREATIVE_MEDIA,
                status = CustomerJobStatus.IN_PROGRESS,
                locationTr = "Online",
                scheduleTr = "Bu hafta",
                descriptionTr = "Yeni marka için logo ve Instagram şablonları.",
                offerCount = 1,
                paymentStatus = SafePaymentStatus.HELD_IN_ESCROW,
                aiSummaryTr = "Ödeme güvenli havuzda; teslim onayı sonrası payout serbest bırakılır."
            )
        )
    }

    fun offersForJob(jobId: String): List<OfferPreview> {
        return listOf(
            OfferPreview(
                id = "offer_001",
                jobId = jobId,
                providerNameTr = "Temizeller Ekibi",
                priceTr = "1.450 TL",
                ratingTr = "4.9 / 124 iş",
                status = OfferStatus.PENDING,
                messageTr = "3 kişilik ekip, malzemeler dahil, aynı gün tamamlanır.",
                trustSignalsTr = listOf("Kimlik doğrulandı", "İşletme profili yayın hazır", "Son 30 günde 18 iş")
            ),
            OfferPreview(
                id = "offer_002",
                jobId = jobId,
                providerNameTr = "Ayşe Demir",
                priceTr = "1.200 TL",
                ratingTr = "4.8 / 67 iş",
                status = OfferStatus.PENDING,
                messageTr = "Detaylı temizlik ve cam temizliği için 5 saatlik plan öneriyorum.",
                trustSignalsTr = listOf("Kimlik doğrulandı", "Hızlı yanıt", "Tekrarlı müşteri oranı yüksek")
            )
        )
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

    fun providerDashboard(): ProviderDashboardSnapshot {
        return ProviderDashboardSnapshot(
            availability = ProviderAvailability.AVAILABLE,
            metrics = listOf(
                ProviderMetric("Yeni talep", "3", "Yakın çevrende yanıt bekleyen uygun işler."),
                ProviderMetric("Bekleyen teklif", "2", "Müşteri kararını bekleyen teklifler."),
                ProviderMetric("Aktif iş", "1", "Planlanmış ve güvenli ödeme adımında."),
                ProviderMetric("Profil gücü", "%68", "Portföy ve hizmet açıklaması eklenirse artar.")
            ),
            todayTasksTr = listOf(
                "2 yeni talebe yanıt ver.",
                "1 teklif için müşteri sorusunu cevapla.",
                "Portföy ekleyerek görünürlüğünü artır."
            ),
            opportunities = listOf(
                ProviderOpportunity(
                    id = "opp_001",
                    titleTr = "Logo ve sosyal medya tasarımı",
                    categoryId = ServiceCategoryId.CREATIVE_MEDIA,
                    requesterTr = "Elif K.",
                    locationTr = "Online",
                    budgetTr = "Teklif bekliyor",
                    descriptionTr = "Yeni marka için modern logo ve sosyal medya şablonları.",
                    fitTr = "Portföy örneği olan sağlayıcılar için yüksek eşleşme."
                ),
                ProviderOpportunity(
                    id = "opp_002",
                    titleTr = "Detaylı ev temizliği",
                    categoryId = ServiceCategoryId.CLEANING,
                    requesterTr = "Murat A.",
                    locationTr = "Kadıköy, 3 km",
                    budgetTr = "1.500-2.000 TL",
                    descriptionTr = "Taşınma öncesi 2+1 boş ev temizliği, camlar dahil.",
                    fitTr = "Yakın konum ve net kapsam nedeniyle hızlı teklif önerilir."
                )
            ),
            activeJobs = listOf(
                ProviderJob(
                    id = "pjob_001",
                    titleTr = "Kombi bakım randevusu",
                    status = WorkLifecycleStatus.IN_PROGRESS,
                    requesterTr = "Ayşe Y.",
                    payoutTr = "1.200 TL",
                    nextStepTr = "Yarın 14:00 randevu; iş tamamlanınca müşteri onayı beklenir."
                )
            ),
            insights = listOf(
                AiInsight(
                    screenId = "provider_dashboard",
                    summaryTr = "Bugün hızlı teklif verilebilecek iki iş ve bir aktif randevu var.",
                    recommendedNextActionTr = "Önce yakın konumdaki temizlik talebine fiyat ver.",
                    riskSignalsTr = listOf("İletişim paylaşımı iş kabulünden önce kısıtlı.")
                )
            )
        )
    }

    fun progressBusinessWorkspace(detail: BusinessWorkspaceDetail): BusinessWorkspaceDetail {
        val steps = completeNextStep(detail.steps) { step, status -> step.copy(status = status) }
        val doneCount = steps.count { it.status == ProviderSetupStepStatus.DONE }
        val nextStatus = when {
            doneCount == steps.size -> BusinessWorkspaceStatus.PUBLISHED
            doneCount >= steps.size - 1 -> BusinessWorkspaceStatus.PUBLISH_READY
            doneCount > 0 -> BusinessWorkspaceStatus.PENDING_VERIFICATION
            else -> detail.summary.status
        }
        val nextSummary = detail.summary.copy(
            status = nextStatus,
            verificationStepTr = when (nextStatus) {
                BusinessWorkspaceStatus.NOT_STARTED -> detail.summary.verificationStepTr
                BusinessWorkspaceStatus.PENDING_VERIFICATION -> "İşletme doğrulaması ve merchant bilgileri ilerliyor."
                BusinessWorkspaceStatus.PUBLISH_READY -> "Yayın öncesi son policy kontrolleri hazır."
                BusinessWorkspaceStatus.PUBLISHED -> "İşletme profili yayınlandı."
            },
            subscriptionStateTr = if (nextStatus == BusinessWorkspaceStatus.PUBLISHED) {
                "Abonelik aktif, merchant payout hazır"
            } else {
                detail.summary.subscriptionStateTr
            }
        )
        return detail.copy(summary = nextSummary, steps = steps)
    }

    fun progressProviderOnboarding(snapshot: ProviderOnboardingSnapshot): ProviderOnboardingSnapshot {
        val steps = completeNextStep(snapshot.steps) { step, status -> step.copy(status = status) }
        return snapshot.copy(
            steps = steps,
            bodyTr = if (steps.all { it.status == ProviderSetupStepStatus.DONE }) {
                "Bireysel hizmet verme modu hazır; artık uygun işlere teklif gönderebilirsin."
            } else {
                snapshot.bodyTr
            }
        )
    }

    fun wallet(): WalletSnapshot {
        return WalletSnapshot(
            escrowBalanceTr = "1.200 TL",
            payoutBalanceTr = "850 TL",
            providerTokenInfoTr = "Kart bilgisi saklanmaz; ödeme sağlayıcı tokenı kullanılır.",
            transactions = listOf(
                WalletTransaction("txn_001", "Logo işi güvenli ödeme", "4.500 TL", SafePaymentStatus.HELD_IN_ESCROW, "Bugün"),
                WalletTransaction("txn_002", "Kombi bakım payout", "850 TL", SafePaymentStatus.RELEASED, "Dün"),
                WalletTransaction("txn_003", "Temizlik işi ödeme yetkisi", "1.450 TL", SafePaymentStatus.AUTHORIZED, "2 gün önce")
            ),
            safetyNotesTr = listOf(
                "PAN/CVV uygulamada saklanmaz.",
                "Payout uyuşmazlık süresinde bloklanabilir.",
                "Komisyon tamamlanan ücretli iş sonrası uygulanır."
            )
        )
    }

    fun reviews(): List<ReviewSummary> {
        return listOf(
            ReviewSummary("rev_001", "Ev temizliği", "Temizeller Ekibi", ReviewStatus.WAITING_FOR_YOU, "4.9", "İş tamamlandı; iki taraflı yorum bekleniyor."),
            ReviewSummary("rev_002", "Kombi bakım", "Ahmet Usta", ReviewStatus.PUBLISHED, "5.0", "Zamanında ve temiz çalışma."),
            ReviewSummary("rev_003", "Logo tasarımı", "Elif K.", ReviewStatus.WAITING_FOR_COUNTERPARTY, "4.8", "Teslim onayı sonrası karşı taraf yorumu bekleniyor.")
        )
    }

    fun supportTickets(): List<SupportTicket> {
        return listOf(
            SupportTicket("sup_001", "İletişim paylaşımı uyarısı", SupportTicketStatus.WAITING_FOR_USER, "Chat içinde telefon paylaşımı iş kabulünden önce engellendi.", "Uygulama içinden devam et."),
            SupportTicket("sup_002", "Ödeme onayı sorusu", SupportTicketStatus.OPEN, "Güvenli ödeme havuzdaki tutarın serbest bırakma koşulu soruldu.", "Teslim/onay ekranı kontrol edilecek."),
            SupportTicket("sup_003", "Geç teslim bildirimi", SupportTicketStatus.RESOLVED, "Randevu gecikmesi çözüldü.", "Kayıt kapandı.")
        )
    }

    fun settingDetail(itemId: String): SettingDetail {
        return when (itemId) {
            "cards" -> SettingDetail(
                id = itemId,
                titleTr = "Kartlar ve ödeme tokenları",
                bodyTr = "Kart bilgisi sağlayıcıda saklanır; uygulama yalnızca token referansı kullanır.",
                fieldsTr = listOf("Varsayılan ödeme yöntemi", "Apple Pay / Google Pay hazırlığı", "iyzico token durumu"),
                riskNotesTr = listOf("PAN/CVV saklama yok.", "Step-up auth ödeme ve kart değişiminde gerekir.")
            )
            "escrow" -> SettingDetail(
                id = itemId,
                titleTr = "Güvenli ödeme",
                bodyTr = "Tutar iş tamamlanana kadar güvenli havuzda tutulur.",
                fieldsTr = listOf("Bekleyen ödemeler", "Serbest bırakma", "Uyuşmazlık durumu"),
                riskNotesTr = listOf("Uyuşmazlıkta payout bloklanır.", "Callback idempotent olmalı.")
            )
            "support" -> SettingDetail(
                id = itemId,
                titleTr = "Destek merkezi",
                bodyTr = "Rapor, uyuşmazlık, güvenlik ve ödeme destek akışları.",
                fieldsTr = listOf("Açık talepler", "Güvenlik raporları", "Canlı destek hazırlığı"),
                riskNotesTr = listOf("Destek paketleri gereksiz PII içermemeli.")
            )
            else -> SettingDetail(
                id = itemId,
                titleTr = "Ayar detayı",
                bodyTr = "Bu ayar template içinde gerçek uygulama davranışına yakın mock ekranla temsil edilir.",
                fieldsTr = listOf("Durum", "Son güncelleme", "Güvenlik etkisi"),
                riskNotesTr = listOf("Hassas değişikliklerde step-up auth gerekir.")
            )
        }
    }

    fun publicProfile(): PublicProfileSnapshot {
        return PublicProfileSnapshot(
            displayNameTr = "Ahmet Usta",
            headlineTr = "Ev tamiri ve kombi bakımında doğrulanmış bireysel hizmet veren.",
            ratingTr = "4.9",
            completedJobsTr = "124 tamamlanan iş",
            badgesTr = listOf("Kimlik doğrulandı", "Hızlı yanıt", "Rozetli sağlayıcı"),
            reviewHighlightsTr = listOf("Zamanında geldi ve işi temiz tamamladı.", "Fiyat/kapsam netti.", "Güvenli ödeme süreci sorunsuzdu."),
            portfolioTr = listOf("Kombi bakım öncesi/sonrası", "Musluk değişimi", "Küçük elektrik işleri")
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

    private fun <T> completeNextStep(
        steps: List<T>,
        copyWithStatus: (T, ProviderSetupStepStatus) -> T
    ): List<T> {
        var changed = false
        return steps.map { step ->
            val status = when (step) {
                is ProviderSetupStep -> step.status
                is BusinessSetupStep -> step.status
                else -> ProviderSetupStepStatus.DONE
            }
            if (!changed && status != ProviderSetupStepStatus.DONE) {
                changed = true
                copyWithStatus(step, ProviderSetupStepStatus.DONE)
            } else {
                step
            }
        }
    }
}
