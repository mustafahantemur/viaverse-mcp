package app.viaverse.template.ui.screen.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.DiscoveryMode
import app.viaverse.template.domain.model.DiscoverySnapshot
import app.viaverse.template.domain.model.DiscoverySort
import app.viaverse.template.domain.model.DiscoveryUrgency
import app.viaverse.template.domain.model.ExploreItem
import app.viaverse.template.domain.model.SearchCriteria
import app.viaverse.template.domain.model.ServiceCategory
import app.viaverse.template.domain.model.ServiceCategoryId
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors
import org.jetbrains.compose.resources.painterResource

@Composable
fun ExploreScreen(
    account: Account?,
    repository: DiscoveryRepository,
    onOpenProfile: () -> Unit
) {
    var criteria by remember { mutableStateOf(SearchCriteria()) }
    val snapshot = remember(criteria) { repository.load(criteria) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ViaverseColors.WarmBase),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            ExploreHeader(
                account = account,
                criteria = criteria,
                onCriteriaChange = { criteria = it },
                onOpenProfile = onOpenProfile
            )
        }
        item {
            ModeSwitcher(
                selectedMode = criteria.mode,
                onModeSelected = { criteria = criteria.copy(mode = it) }
            )
        }
        item {
            CategorySection(
                categories = snapshot.categories,
                selectedCategoryId = criteria.selectedCategoryId,
                onCategorySelected = { categoryId ->
                    criteria = criteria.copy(
                        selectedCategoryId = if (criteria.selectedCategoryId == categoryId) null else categoryId
                    )
                }
            )
        }
        item {
            FilterSection(
                criteria = criteria,
                onCriteriaChange = { criteria = it }
            )
        }
        item {
            InsightPanel(snapshot = snapshot)
        }
        if (snapshot.items.isEmpty()) {
            item {
                EmptyResultCard()
            }
        }
        items(snapshot.items, key = { it.id }) { item ->
            ExploreItemCard(item = item)
        }
        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ExploreHeader(
    account: Account?,
    criteria: SearchCriteria,
    onCriteriaChange: (SearchCriteria) -> Unit,
    onOpenProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ViaverseColors.WarmSurface)
            .padding(horizontal = Dimensions.SpaceLg, vertical = Dimensions.SpaceMd)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Merhaba ${account?.displayName?.substringBefore(" ") ?: ""}".trim(),
                    color = ViaverseColors.Ink,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Yakınındaki güvenli işleri ve hizmetleri keşfet.",
                    color = ViaverseColors.MutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ViaverseColors.DeepGreen)
                    .clickable(onClick = onOpenProfile),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = account?.displayName?.firstOrNull()?.uppercase().orEmpty(),
                    color = ViaverseColors.OnBrand,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = criteria.query,
            onValueChange = { onCriteriaChange(criteria.copy(query = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Hizmet, iş veya mahalle ihtiyacı ara") },
            singleLine = true,
            shape = RoundedCornerShape(Dimensions.RadiusMd)
        )
    }
}

@Composable
private fun ModeSwitcher(
    selectedMode: DiscoveryMode,
    onModeSelected: (DiscoveryMode) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = Dimensions.SpaceLg)
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.WarmMuted)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ModeButton(
            label = "İş ver",
            selected = selectedMode == DiscoveryMode.REQUEST_WORK,
            modifier = Modifier.weight(1f),
            onClick = { onModeSelected(DiscoveryMode.REQUEST_WORK) }
        )
        ModeButton(
            label = "İş yap",
            selected = selectedMode == DiscoveryMode.DO_WORK,
            modifier = Modifier.weight(1f),
            onClick = { onModeSelected(DiscoveryMode.DO_WORK) }
        )
    }
}

@Composable
private fun ModeButton(
    label: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) ViaverseColors.CardSurface else ViaverseColors.WarmMuted)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) ViaverseColors.Ink else ViaverseColors.MutedText,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun CategorySection(
    categories: List<ServiceCategory>,
    selectedCategoryId: ServiceCategoryId?,
    onCategorySelected: (ServiceCategoryId) -> Unit
) {
    Column {
        SectionTitle(title = "Kategoriler", action = "Tümü")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = Dimensions.SpaceLg)
        ) {
            items(categories, key = { it.id.name }) { category ->
                CategoryCard(
                    category = category,
                    selected = category.id == selectedCategoryId,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: ServiceCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(132.dp)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(
                width = 1.dp,
                color = if (selected) ViaverseColors.BrandOrange else ViaverseColors.BorderSubtle,
                shape = RoundedCornerShape(Dimensions.RadiusMd)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(categoryDrawable(category.id)),
            contentDescription = category.titleTr,
            modifier = Modifier.size(38.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = category.titleTr,
            color = ViaverseColors.Ink,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = category.subtitleTr,
            color = ViaverseColors.MutedText,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FilterSection(
    criteria: SearchCriteria,
    onCriteriaChange: (SearchCriteria) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title = "Filtre", action = "${criteria.maxDistanceKm} km")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = Dimensions.SpaceLg)
        ) {
            item {
                FilterChip(
                    label = "Bugün",
                    selected = criteria.urgency == DiscoveryUrgency.TODAY,
                    onClick = {
                        onCriteriaChange(
                            criteria.copy(
                                urgency = if (criteria.urgency == DiscoveryUrgency.TODAY) DiscoveryUrgency.ANY else DiscoveryUrgency.TODAY
                            )
                        )
                    }
                )
            }
            item {
                FilterChip(
                    label = "Bu hafta",
                    selected = criteria.urgency == DiscoveryUrgency.THIS_WEEK,
                    onClick = {
                        onCriteriaChange(
                            criteria.copy(
                                urgency = if (criteria.urgency == DiscoveryUrgency.THIS_WEEK) DiscoveryUrgency.ANY else DiscoveryUrgency.THIS_WEEK
                            )
                        )
                    }
                )
            }
            item {
                FilterChip(
                    label = "Yakınımda",
                    selected = criteria.maxDistanceKm <= 5,
                    onClick = { onCriteriaChange(criteria.copy(maxDistanceKm = if (criteria.maxDistanceKm <= 5) 10 else 5)) }
                )
            }
            item {
                FilterChip(
                    label = "Güven skoru",
                    selected = criteria.sort == DiscoverySort.TRUST_SCORE,
                    onClick = { onCriteriaChange(criteria.copy(sort = DiscoverySort.TRUST_SCORE)) }
                )
            }
            item {
                FilterChip(
                    label = "En yakın",
                    selected = criteria.sort == DiscoverySort.NEARBY,
                    onClick = { onCriteriaChange(criteria.copy(sort = DiscoverySort.NEARBY)) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) ViaverseColors.DeepGreen else ViaverseColors.CardSurface)
            .border(
                width = 1.dp,
                color = if (selected) ViaverseColors.DeepGreen else ViaverseColors.BorderSubtle,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) ViaverseColors.OnBrand else ViaverseColors.Ink,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InsightPanel(snapshot: DiscoverySnapshot) {
    val insight = snapshot.insights.firstOrNull() ?: return
    Column(
        modifier = Modifier
            .padding(horizontal = Dimensions.SpaceLg)
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.DeepGreen)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "AI içgörü",
            color = ViaverseColors.BrandOrange,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = insight.summaryTr,
            color = ViaverseColors.OnBrand,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = insight.recommendedNextActionTr,
            color = ViaverseColors.OnBrand.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ExploreItemCard(item: ExploreItem) {
    Row(
        modifier = Modifier
            .padding(horizontal = Dimensions.SpaceLg)
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.RadiusMd))
            .background(ViaverseColors.CardSurface)
            .border(1.dp, ViaverseColors.BorderSubtle, RoundedCornerShape(Dimensions.RadiusMd))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ViaverseColors.WarmMuted),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(categoryDrawable(item.categoryId)),
                contentDescription = item.titleTr,
                modifier = Modifier.size(34.dp),
                contentScale = ContentScale.Fit
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.titleTr,
                    color = ViaverseColors.Ink,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = item.priceHintTr,
                    color = ViaverseColors.BrandOrange,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(item.descriptionTr, color = ViaverseColors.MutedText, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "${item.locationTr} | ${item.distanceKm} km",
                color = ViaverseColors.Ink,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
            Text(item.aiSummaryTr, color = ViaverseColors.MutedText, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EmptyResultCard() {
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
        Text(
            text = "Sonuç bulunamadı",
            color = ViaverseColors.Ink,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Filtreleri genişlet veya farklı bir kategori seç.",
            color = ViaverseColors.MutedText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    action: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.SpaceLg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = ViaverseColors.Ink, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(action, color = ViaverseColors.BrandOrange, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}
