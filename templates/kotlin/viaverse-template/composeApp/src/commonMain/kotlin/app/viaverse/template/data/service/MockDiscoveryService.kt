package app.viaverse.template.data.service

import app.viaverse.template.domain.model.AiInsight
import app.viaverse.template.domain.model.DiscoveryItemType
import app.viaverse.template.domain.model.DiscoveryMode
import app.viaverse.template.domain.model.DiscoverySnapshot
import app.viaverse.template.domain.model.DiscoverySort
import app.viaverse.template.domain.model.DiscoveryUrgency
import app.viaverse.template.domain.model.ExploreItem
import app.viaverse.template.domain.model.SearchCriteria
import app.viaverse.template.domain.model.ServiceCategory
import app.viaverse.template.domain.model.ServiceCategoryId
import app.viaverse.template.domain.model.SocialComment
import app.viaverse.template.domain.model.SocialFeedPost
import app.viaverse.template.domain.model.SocialHashtag
import app.viaverse.template.domain.model.SocialMediaKind
import app.viaverse.template.domain.model.SocialPostType

class MockDiscoveryService {
    fun loadDiscovery(criteria: SearchCriteria): DiscoverySnapshot {
        val filtered = applyCriteria(criteria)
        val filteredPosts = applyPostCriteria(criteria)
        return DiscoverySnapshot(
            criteria = criteria,
            categories = categories,
            items = filtered,
            socialPosts = filteredPosts,
            trendingHashtags = buildTrendingHashtags(filteredPosts),
            insights = buildInsights(criteria, filtered)
        )
    }

    fun findItem(itemId: String): ExploreItem? {
        return items.firstOrNull { it.id == itemId }
    }

    private fun applyCriteria(criteria: SearchCriteria): List<ExploreItem> {
        val query = criteria.query.trim().lowercase()
        return items
            .asSequence()
            .filter { it.matchesMode(criteria.mode) }
            .filter { criteria.selectedCategoryId == null || it.categoryId == criteria.selectedCategoryId }
            .filter { criteria.urgency == DiscoveryUrgency.ANY || it.urgency == criteria.urgency }
            .filter { it.distanceKm <= criteria.maxDistanceKm }
            .filter { item ->
                query.isBlank() ||
                    item.titleTr.lowercase().contains(query) ||
                    item.descriptionTr.lowercase().contains(query) ||
                    item.locationTr.lowercase().contains(query)
            }
            .sortedWith(sorter(criteria.sort))
            .toList()
    }

    private fun applyPostCriteria(criteria: SearchCriteria): List<SocialFeedPost> {
        val query = criteria.query.trim().lowercase()
        return socialPosts
            .asSequence()
            .filter { criteria.selectedPostType == null || it.type == criteria.selectedPostType }
            .filter { criteria.selectedHashtagKey == null || it.hashtags.any { tag -> tag == criteria.selectedHashtagKey } }
            .filter { post ->
                query.isBlank() ||
                    post.titleTr.orEmpty().lowercase().contains(query) ||
                    post.bodyTr.lowercase().contains(query) ||
                    post.authorNameTr.lowercase().contains(query) ||
                    post.type.labelTr().lowercase().contains(query) ||
                    post.hashtags.any { tag -> "#$tag".contains(query) || tag.contains(query.removePrefix("#")) }
            }
            .toList()
    }

    private fun buildTrendingHashtags(posts: List<SocialFeedPost>): List<SocialHashtag> {
        return posts
            .flatMap { it.hashtags }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .take(8)
            .map { SocialHashtag(key = it.key, labelTr = "#${it.key}", postCount = it.value) }
    }

    private fun ExploreItem.matchesMode(mode: DiscoveryMode): Boolean {
        return when (mode) {
            DiscoveryMode.REQUEST_WORK -> type == DiscoveryItemType.PROVIDER
            DiscoveryMode.DO_WORK -> type != DiscoveryItemType.PROVIDER
        }
    }

    private fun sorter(sort: DiscoverySort): Comparator<ExploreItem> {
        return when (sort) {
            DiscoverySort.RECOMMENDED -> compareBy<ExploreItem> { it.distanceKm }.thenBy { it.titleTr }
            DiscoverySort.NEARBY -> compareBy { it.distanceKm }
            DiscoverySort.NEWEST -> compareByDescending { it.id }
            DiscoverySort.TRUST_SCORE -> compareByDescending { it.trustSignalTr }
        }
    }

    private fun buildInsights(criteria: SearchCriteria, filtered: List<ExploreItem>): List<AiInsight> {
        val modeText = when (criteria.mode) {
            DiscoveryMode.REQUEST_WORK -> "hizmet alma ve talep oluşturmaya yakın"
            DiscoveryMode.DO_WORK -> "çevrenle etkileşim ve yerel güven akışına yakın"
        }
        return listOf(
            AiInsight(
                screenId = "explore",
                summaryTr = "${filtered.size} uygun sonuç bulundu; ekran $modeText bir bağlamda.",
                recommendedNextActionTr = "Arama niyetini kategori ve mesafe filtresiyle daralt.",
                riskSignalsTr = listOf("Ödeme ve iletişim uygulama dışına taşmamalı.")
            )
        )
    }

    private fun SocialPostType.labelTr(): String {
        return when (this) {
            SocialPostType.HELP -> "yardım"
            SocialPostType.ANNOUNCEMENT -> "duyuru"
            SocialPostType.ADVISORY -> "danışma"
            SocialPostType.WORK -> "iş"
            SocialPostType.EVENT -> "etkinlik"
        }
    }

    private val categories = listOf(
        ServiceCategory(ServiceCategoryId.HOME_REPAIR, "Ev tamiri", "Usta, bakım, küçük onarım", listOf("musluk", "boya", "elektrik")),
        ServiceCategory(ServiceCategoryId.CLEANING, "Temizlik", "Ev, ofis, taşınma temizliği", listOf("haftalık temizlik", "detaylı temizlik")),
        ServiceCategory(ServiceCategoryId.EDUCATION, "Ders ve eğitim", "Özel ders, sınav, beceri", listOf("matematik", "ingilizce")),
        ServiceCategory(ServiceCategoryId.CARE_HEALTH, "Bakım ve sağlık", "Bakıcı, refakat, yaşam desteği", listOf("çocuk bakımı", "yaşlı refakat")),
        ServiceCategory(ServiceCategoryId.LOGISTICS, "Lojistik", "Taşıma, kurye, teslimat", listOf("küçük taşıma", "paket teslim")),
        ServiceCategory(ServiceCategoryId.CREATIVE_MEDIA, "Yaratıcı işler", "Tasarım, fotoğraf, içerik", listOf("logo", "çekim")),
        ServiceCategory(ServiceCategoryId.DIGITAL_SOFTWARE, "Yazılım", "Web, otomasyon, teknik destek", listOf("web sitesi", "entegrasyon")),
        ServiceCategory(ServiceCategoryId.PROFESSIONAL_CONSULTING, "Danışmanlık", "Uzman görüşü ve planlama", listOf("hukuk", "mali müşavir")),
        ServiceCategory(ServiceCategoryId.LOCAL_HELP, "Yerel yardım", "Komşu desteği ve küçük işler", listOf("alışveriş", "sıra bekleme")),
        ServiceCategory(ServiceCategoryId.EVENTS, "Etkinlik", "Planlama, ekip, organizasyon", listOf("doğum günü", "ses sistemi")),
        ServiceCategory(ServiceCategoryId.ANNOUNCEMENT, "Duyuru", "Çevre bilgilendirme", listOf("kayıp eşya", "site duyurusu")),
        ServiceCategory(ServiceCategoryId.PETS, "Evcil hayvan", "Gezdirme, bakım, veteriner destek", listOf("köpek gezdirme", "kedi bakımı")),
        ServiceCategory(ServiceCategoryId.WORK, "Küçük iş", "Günlük ve esnek işler", listOf("montaj", "yardımcı personel"))
    )

    private val items = listOf(
        ExploreItem(
            id = "exp_001",
            type = DiscoveryItemType.REQUEST,
            categoryId = ServiceCategoryId.HOME_REPAIR,
            titleTr = "Mutfak musluğu değişimi",
            descriptionTr = "Bugün akşam saatlerinde küçük bir tesisat işi için teklif bekleniyor.",
            locationTr = "Kadıköy, İstanbul",
            distanceKm = 2.4,
            priceHintTr = "750-1.200 TL",
            trustSignalTr = "Kimliği doğrulanmış talep",
            urgency = DiscoveryUrgency.TODAY,
            aiSummaryTr = "Yakın mesafede, kısa süreli ve net kapsamlı bir ev tamiri talebi."
        ),
        ExploreItem(
            id = "exp_002",
            type = DiscoveryItemType.PROVIDER,
            categoryId = ServiceCategoryId.CLEANING,
            titleTr = "Detaylı ev temizliği için uygun ekipler",
            descriptionTr = "Yüksek puanlı iki ekip bu hafta müsait görünüyor.",
            locationTr = "Beşiktaş, İstanbul",
            distanceKm = 5.8,
            priceHintTr = "Saatlik 350 TL'den",
            trustSignalTr = "4.9 puan, 124 iş",
            urgency = DiscoveryUrgency.THIS_WEEK,
            aiSummaryTr = "Güven puanı yüksek, randevu esnekliği olan temizlik eşleşmeleri."
        ),
        ExploreItem(
            id = "exp_006",
            type = DiscoveryItemType.PROVIDER,
            categoryId = ServiceCategoryId.HOME_REPAIR,
            titleTr = "Yakındaki doğrulanmış tesisat ustaları",
            descriptionTr = "Bugün uygun, kimlik doğrulaması tamamlanmış üç sağlayıcı öneriliyor.",
            locationTr = "Kadıköy, İstanbul",
            distanceKm = 2.1,
            priceHintTr = "Teklif al",
            trustSignalTr = "Rozetli sağlayıcılar",
            urgency = DiscoveryUrgency.TODAY,
            aiSummaryTr = "Talep açmadan önce uygun sağlayıcı havuzunu ve ortalama fiyatı gösterir."
        ),
        ExploreItem(
            id = "exp_007",
            type = DiscoveryItemType.PROVIDER,
            categoryId = ServiceCategoryId.DIGITAL_SOFTWARE,
            titleTr = "Küçük işletme dijital destek ekipleri",
            descriptionTr = "Web, rezervasyon ve ödeme entegrasyonu için yayın hazırlığı olan profiller.",
            locationTr = "İstanbul geneli",
            distanceKm = 7.3,
            priceHintTr = "Paket teklif",
            trustSignalTr = "Portföy doğrulandı",
            urgency = DiscoveryUrgency.THIS_WEEK,
            aiSummaryTr = "İşletme talebi oluşturmadan önce uygun dijital hizmet profillerini listeler."
        ),
        ExploreItem(
            id = "exp_003",
            type = DiscoveryItemType.REQUEST,
            categoryId = ServiceCategoryId.DIGITAL_SOFTWARE,
            titleTr = "Küçük işletme için tek sayfa web sitesi",
            descriptionTr = "Kuaför salonu için hızlı, mobil uyumlu tanıtım sayfası isteniyor.",
            locationTr = "Bornova, İzmir",
            distanceKm = 8.6,
            priceHintTr = "4.000-7.500 TL",
            trustSignalTr = "İşletme profili doğrulama bekliyor",
            urgency = DiscoveryUrgency.FLEXIBLE,
            aiSummaryTr = "Dijital hizmet veren bireysel çalışanlar için düşük riskli başlangıç işi."
        ),
        ExploreItem(
            id = "exp_004",
            type = DiscoveryItemType.EVENT,
            categoryId = ServiceCategoryId.EVENTS,
            titleTr = "Apartman bahçesi etkinliği için ses sistemi",
            descriptionTr = "Hafta sonu küçük etkinlik için kurulum ve teknik destek aranıyor.",
            locationTr = "Mamak, Ankara",
            distanceKm = 6.1,
            priceHintTr = "2.000 TL civarı",
            trustSignalTr = "Site yönetimi talebi",
            urgency = DiscoveryUrgency.THIS_WEEK,
            aiSummaryTr = "Etkinlik ekipmanı olan sağlayıcılar için tarih odaklı iş fırsatı."
        ),
        ExploreItem(
            id = "exp_005",
            type = DiscoveryItemType.ANNOUNCEMENT,
            categoryId = ServiceCategoryId.LOCAL_HELP,
            titleTr = "Mahallede kısa süreli alışveriş desteği",
            descriptionTr = "Yakındaki marketten birkaç ürün alınması için yerel destek aranıyor.",
            locationTr = "Ataşehir, İstanbul",
            distanceKm = 1.2,
            priceHintTr = "150 TL",
            trustSignalTr = "Yakın çevre paylaşımı",
            urgency = DiscoveryUrgency.TODAY,
            aiSummaryTr = "Sosyal güven katmanı için düşük bütçeli ve hızlı tamamlanabilir yerel destek."
        )
    )

    private val socialPosts = listOf(
        SocialFeedPost(
            id = "post_001",
            type = SocialPostType.HELP,
            authorNameTr = "Ayşe T.",
            titleTr = "Yıldız tornavida",
            bodyTr = "Ufak bir montaj işim var, 1 saatliğine yıldız tornavida ödünç verebilecek komşu var mı?",
            publishTimeTr = "5 dk",
            distanceTr = "150 m",
            likes = 2,
            commentsCount = 0,
            mediaKind = SocialMediaKind.NONE,
            mediaLabelTr = null,
            priceHintTr = null,
            hashtags = listOf("montaj", "komşuyardımı", "alet"),
            comments = emptyList()
        ),
        SocialFeedPost(
            id = "post_002",
            type = SocialPostType.ANNOUNCEMENT,
            authorNameTr = "Kemal S.",
            titleTr = "Kayıp kedi",
            bodyTr = "Dün akşam parkın orada kedimiz Tarçın kayboldu. Görenlerin haber vermesini rica ederiz.",
            publishTimeTr = "27 dk",
            distanceTr = "0.2 km",
            likes = 12,
            commentsCount = 5,
            mediaKind = SocialMediaKind.IMAGE,
            mediaLabelTr = "Kedi fotoğrafı",
            priceHintTr = null,
            hashtags = listOf("kayıpkedi", "mahalle", "yardım"),
            comments = listOf(
                SocialComment("Fatma Y.", "Çok geçmiş olsun, gözümüz yollarda olur.", "10 dk"),
                SocialComment("Murat C.", "Parkın köşesinde gördüm sanırım, birazdan bakacağım.", "5 dk")
            )
        ),
        SocialFeedPost(
            id = "post_003",
            type = SocialPostType.WORK,
            authorNameTr = "Cansu B.",
            titleTr = null,
            bodyTr = "Yarın 14:00'da ufak ırk köpeğimi 45 dakika gezdirecek biri lazım.",
            publishTimeTr = "1 sa",
            distanceTr = "1.2 km",
            likes = 4,
            commentsCount = 1,
            mediaKind = SocialMediaKind.VIDEO,
            mediaLabelTr = "Kısa tanıtım videosu",
            priceHintTr = "Teklif bekliyor",
            hashtags = listOf("köpekgezdirme", "küçükiş", "evcilhayvan"),
            comments = listOf(
                SocialComment("Selin G.", "Hangi parkta dolaştırılması gerekiyor?", "20 dk")
            )
        ),
        SocialFeedPost(
            id = "post_004",
            type = SocialPostType.ADVISORY,
            authorNameTr = "Deniz K.",
            titleTr = "Bebek koltuğu önerisi",
            bodyTr = "Kısa mesafeli şehir içi kullanım için güvenli ve pratik bebek koltuğu öneriniz var mı?",
            publishTimeTr = "2 sa",
            distanceTr = "0.8 km",
            likes = 7,
            commentsCount = 3,
            mediaKind = SocialMediaKind.NONE,
            mediaLabelTr = null,
            priceHintTr = null,
            hashtags = listOf("bebek", "tavsiye", "güvenlik"),
            comments = emptyList()
        )
    )
}
