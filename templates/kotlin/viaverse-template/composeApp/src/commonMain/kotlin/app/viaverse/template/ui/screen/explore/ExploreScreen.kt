package app.viaverse.template.ui.screen.explore

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.template.data.repository.DiscoveryRepository
import app.viaverse.template.domain.model.Account
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
import app.viaverse.template.domain.model.SocialMediaKind
import app.viaverse.template.domain.model.SocialPostType
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors
import org.jetbrains.compose.resources.painterResource

@Composable
fun ExploreScreen(
    account: Account?,
    repository: DiscoveryRepository,
    onOpenItem: (String) -> Unit,
    onOpenProfile: () -> Unit
) {
    var criteria by remember { mutableStateOf(SearchCriteria()) }
    var queryDraft by remember { mutableStateOf("") }
    var filtersOpen by remember { mutableStateOf(false) }
    var composerOpen by remember { mutableStateOf(false) }
    var localPosts by remember { mutableStateOf(emptyList<SocialFeedPost>()) }
    val snapshot = remember(criteria) { repository.load(criteria) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ViaverseColors.WarmBase),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ExploreHeader(
                account = account,
                query = queryDraft,
                mode = criteria.mode,
                activeCriteria = criteria,
                onQueryChange = { queryDraft = it },
                onSearch = { criteria = criteria.copy(query = queryDraft) },
                onModeSelected = { mode ->
                    criteria = criteria.copy(mode = mode, selectedCategoryId = null, selectedPostType = null, query = "")
                    queryDraft = ""
                    filtersOpen = false
                },
                onToggleFilters = { filtersOpen = !filtersOpen },
                onOpenProfile = onOpenProfile
            )
        }

        if (filtersOpen) {
            item {
                FilterSheet(criteria = criteria, onCriteriaChange = { criteria = it })
            }
        }

        if (criteria.mode == DiscoveryMode.DO_WORK) {
            item {
                LocalComposerCard(
                    expanded = composerOpen,
                    onToggle = { composerOpen = !composerOpen },
                    onPublish = { post ->
                        localPosts = listOf(post) + localPosts
                        composerOpen = false
                    }
                )
            }
            item {
                SocialTypeStrip(
                    selectedPostType = criteria.selectedPostType,
                    onSelected = { type ->
                        criteria = criteria.copy(selectedPostType = if (criteria.selectedPostType == type) null else type)
                    }
                )
            }
            item { InsightPanel(snapshot = snapshot) }
            val posts = (localPosts + snapshot.socialPosts).filter { it.matches(criteria) }
            if (posts.isEmpty()) {
                item { EmptyResultCard("Çevrende paylaşım bulunamadı", "Aramayı temizle veya filtreyi genişlet.") }
            }
            items(posts, key = { it.id }) { post -> SocialPostCard(post) }
        } else {
            item {
                CategorySection(
                    categories = snapshot.categories,
                    selectedCategoryId = criteria.selectedCategoryId,
                    onCategorySelected = { categoryId ->
                        criteria = criteria.copy(selectedCategoryId = if (criteria.selectedCategoryId == categoryId) null else categoryId)
                    }
                )
            }
            item { InsightPanel(snapshot = snapshot) }
            if (snapshot.items.isEmpty()) {
                item { EmptyResultCard("Sonuç bulunamadı", "Filtreleri genişlet veya farklı bir kategori seç.") }
            }
            items(snapshot.items, key = { it.id }) { item -> ExploreItemCard(item = item, onClick = { onOpenItem(item.id) }) }
        }

        item { Spacer(Modifier.height(18.dp)) }
    }
}

@Composable
private fun ExploreHeader(
    account: Account?,
    query: String,
    mode: DiscoveryMode,
    activeCriteria: SearchCriteria,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onModeSelected: (DiscoveryMode) -> Unit,
    onToggleFilters: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ViaverseColors.WarmSurface)
            .padding(horizontal = Dimensions.SpaceLg, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(ViaverseColors.CardSurface)
                    .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(999.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⌕", modifier = Modifier.padding(start = 14.dp), color = ViaverseColors.BrandOrange, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isBlank()) {
                        Text(
                            text = if (mode == DiscoveryMode.DO_WORK) "Çevrende ne oluyor?" else "Hangi hizmete ihtiyacın var?",
                            color = ViaverseColors.MutedText.copy(alpha = 0.72f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = ViaverseColors.Ink,
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(ViaverseColors.BrandOrange)
                        .clickable(onClick = onSearch)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ara", color = ViaverseColors.OnBrand, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ViaverseColors.BrandOrange)
                    .clickable(onClick = onOpenProfile),
                contentAlignment = Alignment.Center
            ) {
                Text(account?.displayName?.firstOrNull()?.uppercase().orEmpty(), color = ViaverseColors.OnBrand, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeTab("Çevre", mode == DiscoveryMode.DO_WORK, Modifier.weight(1f)) { onModeSelected(DiscoveryMode.DO_WORK) }
            ModeTab("Hizmet al", mode == DiscoveryMode.REQUEST_WORK, Modifier.weight(1f)) { onModeSelected(DiscoveryMode.REQUEST_WORK) }
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (activeCriteria.hasActiveFilters()) ViaverseColors.BrandOrange else ViaverseColors.CardSurface)
                    .border(1.dp, if (activeCriteria.hasActiveFilters()) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle, RoundedCornerShape(999.dp))
                    .clickable(onClick = onToggleFilters)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (activeCriteria.hasActiveFilters()) "Filtre •" else "Filtre",
                    color = if (activeCriteria.hasActiveFilters()) ViaverseColors.OnBrand else ViaverseColors.Ink,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ModeTab(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) ViaverseColors.BrandOrange else ViaverseColors.WarmMuted)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) ViaverseColors.OnBrand else ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FilterSheet(criteria: SearchCriteria, onCriteriaChange: (SearchCriteria) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = Dimensions.SpaceLg)
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Aramayı daralt", color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip("Bugün", criteria.urgency == DiscoveryUrgency.TODAY) {
                onCriteriaChange(criteria.copy(urgency = if (criteria.urgency == DiscoveryUrgency.TODAY) DiscoveryUrgency.ANY else DiscoveryUrgency.TODAY))
            }
            FilterChip("Bu hafta", criteria.urgency == DiscoveryUrgency.THIS_WEEK) {
                onCriteriaChange(criteria.copy(urgency = if (criteria.urgency == DiscoveryUrgency.THIS_WEEK) DiscoveryUrgency.ANY else DiscoveryUrgency.THIS_WEEK))
            }
            FilterChip("Yakınımda", criteria.maxDistanceKm <= 5) {
                onCriteriaChange(criteria.copy(maxDistanceKm = if (criteria.maxDistanceKm <= 5) 10 else 5))
            }
        }
        if (criteria.mode == DiscoveryMode.REQUEST_WORK) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("Güven skoru", criteria.sort == DiscoverySort.TRUST_SCORE) { onCriteriaChange(criteria.copy(sort = DiscoverySort.TRUST_SCORE)) }
                FilterChip("En yakın", criteria.sort == DiscoverySort.NEARBY) { onCriteriaChange(criteria.copy(sort = DiscoverySort.NEARBY)) }
            }
        }
    }
}

@Composable
private fun LocalComposerCard(expanded: Boolean, onToggle: () -> Unit, onPublish: (SocialFeedPost) -> Unit) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(SocialPostType.ANNOUNCEMENT) }
    var mediaKind by remember { mutableStateOf(SocialMediaKind.NONE) }

    Column(
        modifier = Modifier
            .padding(horizontal = Dimensions.SpaceLg)
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Çevrene paylaş", color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Duyuru, yardım, danışma veya küçük iş paylaş.", color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(ViaverseColors.BrandOrange)
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(if (expanded) "Kapat" else "Paylaş", color = ViaverseColors.OnBrand, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
        if (expanded) {
            SocialTypeStrip(selectedPostType = type, onSelected = { type = it })
            OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Başlık") }, singleLine = true, shape = RoundedCornerShape(Dimensions.RadiusMd))
            OutlinedTextField(value = body, onValueChange = { body = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Ne paylaşmak istiyorsun?") }, minLines = 3, shape = RoundedCornerShape(Dimensions.RadiusMd))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("Fotoğraf", mediaKind == SocialMediaKind.IMAGE) { mediaKind = SocialMediaKind.IMAGE }
                FilterChip("Video", mediaKind == SocialMediaKind.VIDEO) { mediaKind = SocialMediaKind.VIDEO }
                FilterChip("Medya yok", mediaKind == SocialMediaKind.NONE) { mediaKind = SocialMediaKind.NONE }
            }
            if (mediaKind != SocialMediaKind.NONE) {
                MediaPreview(mediaKind, if (mediaKind == SocialMediaKind.IMAGE) "Fotoğraf önizlemesi" else "Video önizlemesi")
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.ButtonHeight)
                    .clip(RoundedCornerShape(Dimensions.RadiusMd))
                    .background(ViaverseColors.BrandOrange)
                    .clickable {
                        if (body.isNotBlank()) {
                            onPublish(
                                SocialFeedPost(
                                    id = "local_${body.hashCode()}",
                                    type = type,
                                    authorNameTr = "Sen",
                                    titleTr = title.takeIf { it.isNotBlank() },
                                    bodyTr = body,
                                    publishTimeTr = "şimdi",
                                    distanceTr = "yakın çevre",
                                    likes = 0,
                                    commentsCount = 0,
                                    mediaKind = mediaKind,
                                    mediaLabelTr = if (mediaKind == SocialMediaKind.NONE) null else mediaKind.labelTr(),
                                    priceHintTr = if (type == SocialPostType.WORK) "Teklif bekliyor" else null,
                                    comments = emptyList()
                                )
                            )
                            title = ""
                            body = ""
                            mediaKind = SocialMediaKind.NONE
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Yayınla", color = ViaverseColors.OnBrand, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SocialTypeStrip(selectedPostType: SocialPostType?, onSelected: (SocialPostType) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = Dimensions.SpaceLg)) {
        items(SocialPostType.entries, key = { it.name }) { type ->
            FilterChip(type.labelTr(), selectedPostType == type) { onSelected(type) }
        }
    }
}

@Composable
private fun SocialPostCard(post: SocialFeedPost) {
    var liked by remember(post.id) { mutableStateOf(false) }
    var commentsOpen by remember(post.id) { mutableStateOf(post.comments.isNotEmpty()) }
    var commentDraft by remember(post.id) { mutableStateOf("") }
    var localComments by remember(post.id) { mutableStateOf(emptyList<SocialComment>()) }
    val comments = post.comments + localComments
    val likes = post.likes + if (liked) 1 else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle)
            .padding(horizontal = Dimensions.SpaceLg, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(ViaverseColors.WarmMuted), contentAlignment = Alignment.Center) {
                Text(post.authorNameTr.firstOrNull()?.uppercase().orEmpty(), color = ViaverseColors.BrandOrange, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(post.authorNameTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("${post.publishTimeTr} • ${post.distanceTr}", color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
            }
            Text(post.type.labelTr(), color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        if (post.titleTr != null) {
            Text(post.titleTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        Text(post.bodyTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.bodyMedium)
        if (post.mediaKind != SocialMediaKind.NONE) {
            MediaPreview(post.mediaKind, post.mediaLabelTr ?: post.mediaKind.labelTr())
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "${if (liked) "♥" else "♡"} $likes",
                    color = if (liked) ViaverseColors.BrandOrange else ViaverseColors.MutedText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { liked = !liked }
                )
                Text(
                    text = "◌ ${comments.size}",
                    color = if (commentsOpen) ViaverseColors.BrandOrange else ViaverseColors.MutedText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { commentsOpen = !commentsOpen }
                )
            }
            if (post.type == SocialPostType.WORK) {
                Box(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(ViaverseColors.BrandOrange).padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Text("teklif ver", color = ViaverseColors.OnBrand, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (commentsOpen) {
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimensions.RadiusMd)).background(ViaverseColors.WarmMuted).padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (comments.isEmpty()) {
                    Text("İlk yorumu sen yaz.", color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
                } else {
                    comments.take(4).forEach { comment ->
                        Text("${comment.authorNameTr}: ${comment.textTr}", color = ViaverseColors.Ink, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(ViaverseColors.CardSurface)
                        .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(999.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
                        if (commentDraft.isBlank()) {
                            Text("Yorum yaz", color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
                        }
                        BasicTextField(
                            value = commentDraft,
                            onValueChange = { commentDraft = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = ViaverseColors.Ink, fontSize = MaterialTheme.typography.labelMedium.fontSize)
                        )
                    }
                    Text(
                        text = "gönder",
                        color = ViaverseColors.BrandOrange,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                if (commentDraft.isNotBlank()) {
                                    localComments = localComments + SocialComment("Sen", commentDraft.trim(), "şimdi")
                                    commentDraft = ""
                                }
                            }
                            .padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaPreview(kind: SocialMediaKind, label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (kind == SocialMediaKind.VIDEO) 150.dp else 190.dp)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.WarmMuted)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(if (kind == SocialMediaKind.VIDEO) "▶" else "▧", color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CategorySection(categories: List<ServiceCategory>, selectedCategoryId: ServiceCategoryId?, onCategorySelected: (ServiceCategoryId) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title = "Kategoriler", action = "Hizmet al")
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = Dimensions.SpaceLg)) {
            items(categories, key = { it.id.name }) { category ->
                CategoryCard(category = category, selected = category.id == selectedCategoryId, onClick = { onCategorySelected(category.id) })
            }
        }
    }
}

@Composable
private fun CategoryCard(category: ServiceCategory, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(132.dp)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, if (selected) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(painter = painterResource(categoryDrawable(category.id)), contentDescription = category.titleTr, modifier = Modifier.size(38.dp), contentScale = ContentScale.Fit)
        Text(category.titleTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(category.subtitleTr, color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) ViaverseColors.BrandOrange else ViaverseColors.CardSurface)
            .border(1.dp, if (selected) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) ViaverseColors.OnBrand else ViaverseColors.Ink, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InsightPanel(snapshot: DiscoverySnapshot) {
    val insight = snapshot.insights.firstOrNull() ?: return
    Column(
        modifier = Modifier
            .padding(horizontal = Dimensions.SpaceLg)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.BrandOrange)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("AI içgörü", color = ViaverseColors.OnBrand, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(insight.summaryTr, color = ViaverseColors.OnBrand, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(insight.recommendedNextActionTr, color = ViaverseColors.OnBrand.copy(alpha = 0.78f), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ExploreItemCard(item: ExploreItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = Dimensions.SpaceLg)
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(ViaverseColors.WarmMuted), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(categoryDrawable(item.categoryId)), contentDescription = item.titleTr, modifier = Modifier.size(34.dp), contentScale = ContentScale.Fit)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.titleTr, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(item.priceHintTr, color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            Text(item.descriptionTr, color = ViaverseColors.MutedText, style = MaterialTheme.typography.bodySmall)
            Text("${item.locationTr} • ${item.distanceKm} km", color = ViaverseColors.Ink, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
            Text(item.aiSummaryTr, color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EmptyResultCard(title: String, body: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = Dimensions.SpaceLg)
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(body, color = ViaverseColors.MutedText, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SectionTitle(title: String, action: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.SpaceLg), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(action, color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

private fun SearchCriteria.hasActiveFilters(): Boolean {
    return selectedCategoryId != null ||
        selectedPostType != null ||
        urgency != DiscoveryUrgency.ANY ||
        maxDistanceKm <= 5 ||
        sort != DiscoverySort.RECOMMENDED
}

private fun SocialPostType.labelTr(): String {
    return when (this) {
        SocialPostType.HELP -> "Yardım"
        SocialPostType.ANNOUNCEMENT -> "Duyuru"
        SocialPostType.ADVISORY -> "Danışma"
        SocialPostType.WORK -> "Küçük iş"
        SocialPostType.EVENT -> "Etkinlik"
    }
}

private fun SocialMediaKind.labelTr(): String {
    return when (this) {
        SocialMediaKind.NONE -> "Medya yok"
        SocialMediaKind.IMAGE -> "Fotoğraf"
        SocialMediaKind.VIDEO -> "Video"
    }
}

private fun SocialFeedPost.matches(criteria: SearchCriteria): Boolean {
    val query = criteria.query.trim().lowercase()
    val queryMatches = query.isBlank() ||
        titleTr.orEmpty().lowercase().contains(query) ||
        bodyTr.lowercase().contains(query) ||
        authorNameTr.lowercase().contains(query) ||
        type.labelTr().lowercase().contains(query)
    val typeMatches = criteria.selectedPostType == null || type == criteria.selectedPostType
    return queryMatches && typeMatches
}
