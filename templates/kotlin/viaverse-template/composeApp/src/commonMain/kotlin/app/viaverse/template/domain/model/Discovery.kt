package app.viaverse.template.domain.model

enum class DiscoveryMode {
    REQUEST_WORK,
    DO_WORK
}

enum class SocialPostType {
    HELP,
    ANNOUNCEMENT,
    ADVISORY,
    WORK,
    EVENT
}

enum class SocialMediaKind {
    NONE,
    IMAGE,
    VIDEO
}

enum class ServiceCategoryId {
    HOME_REPAIR,
    CLEANING,
    EDUCATION,
    CARE_HEALTH,
    LOGISTICS,
    CREATIVE_MEDIA,
    DIGITAL_SOFTWARE,
    PROFESSIONAL_CONSULTING,
    LOCAL_HELP,
    EVENTS,
    ANNOUNCEMENT,
    PETS,
    WORK
}

enum class DiscoveryItemType {
    REQUEST,
    PROVIDER,
    ANNOUNCEMENT,
    EVENT
}

enum class DiscoveryUrgency {
    ANY,
    TODAY,
    THIS_WEEK,
    FLEXIBLE
}

enum class DiscoverySort {
    RECOMMENDED,
    NEARBY,
    NEWEST,
    TRUST_SCORE
}

data class SearchCriteria(
    val query: String = "",
    val mode: DiscoveryMode = DiscoveryMode.DO_WORK,
    val selectedCategoryId: ServiceCategoryId? = null,
    val selectedPostType: SocialPostType? = null,
    val urgency: DiscoveryUrgency = DiscoveryUrgency.ANY,
    val maxDistanceKm: Int = 10,
    val sort: DiscoverySort = DiscoverySort.RECOMMENDED
)

data class ServiceCategory(
    val id: ServiceCategoryId,
    val titleTr: String,
    val subtitleTr: String,
    val suggestedQueriesTr: List<String>
)

data class ExploreItem(
    val id: String,
    val type: DiscoveryItemType,
    val categoryId: ServiceCategoryId,
    val titleTr: String,
    val descriptionTr: String,
    val locationTr: String,
    val distanceKm: Double,
    val priceHintTr: String,
    val trustSignalTr: String,
    val urgency: DiscoveryUrgency,
    val aiSummaryTr: String
)

data class SocialComment(
    val authorNameTr: String,
    val textTr: String,
    val timeTr: String
)

data class SocialFeedPost(
    val id: String,
    val type: SocialPostType,
    val authorNameTr: String,
    val titleTr: String?,
    val bodyTr: String,
    val publishTimeTr: String,
    val distanceTr: String,
    val likes: Int,
    val commentsCount: Int,
    val mediaKind: SocialMediaKind,
    val mediaLabelTr: String?,
    val priceHintTr: String?,
    val comments: List<SocialComment>
)

data class DiscoverySnapshot(
    val criteria: SearchCriteria,
    val categories: List<ServiceCategory>,
    val items: List<ExploreItem>,
    val socialPosts: List<SocialFeedPost>,
    val insights: List<AiInsight>
)
