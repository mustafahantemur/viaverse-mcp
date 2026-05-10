package app.viaverse.template.ui.screen.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.template.data.repository.DashboardRepository
import app.viaverse.template.data.repository.DiscoveryRepository
import app.viaverse.template.data.repository.WorkflowRepository
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AccountCapability
import app.viaverse.template.platform.PlatformBackHandler
import app.viaverse.template.ui.screen.explore.ExploreScreen
import app.viaverse.template.ui.theme.ViaverseColors

private enum class MainTab(
    val labelTr: String
) {
    Explore("Keşfet"),
    Requests("Talep"),
    Work("İşler"),
    Messages("Mesaj"),
    Profile("Profil")
}

private sealed class MainPanel {
    data object CreateRequest : MainPanel()
    data object ProviderSetup : MainPanel()
    data object BusinessWorkspace : MainPanel()
    data object ProfileSettings : MainPanel()
    data object ProviderDashboard : MainPanel()
    data object Wallet : MainPanel()
    data object Reviews : MainPanel()
    data object Support : MainPanel()
    data object PublicProfile : MainPanel()
    data class Chat(val conversationId: String) : MainPanel()
    data class ExploreItem(val itemId: String) : MainPanel()
    data class CustomerJob(val jobId: String) : MainPanel()
    data class SettingDetail(val itemId: String) : MainPanel()
}

@Composable
fun MainShell(
    account: Account?,
    discoveryRepository: DiscoveryRepository,
    dashboardRepository: DashboardRepository,
    workflowRepository: WorkflowRepository,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.Explore) }
    var activePanel by remember { mutableStateOf<MainPanel?>(null) }
    var providerModeUnlocked by remember(account) {
        mutableStateOf(account?.capabilities?.contains(AccountCapability.DO_WORK_INDIVIDUALLY) == true)
    }
    val dashboard = remember(account) { dashboardRepository.load(account) }

    PlatformBackHandler(enabled = activePanel != null) {
        activePanel = when (activePanel) {
            is MainPanel.SettingDetail -> MainPanel.ProfileSettings
            else -> null
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ViaverseColors.WarmBase
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    MainTab.Explore -> ExploreScreen(
                        account = account,
                        repository = discoveryRepository,
                        onOpenItem = { activePanel = MainPanel.ExploreItem(it) },
                        onOpenProfile = { selectedTab = MainTab.Profile }
                    )

                    MainTab.Requests -> RequestsScreen(
                        snapshot = dashboard,
                        onCreateRequest = { activePanel = MainPanel.CreateRequest },
                        onOpenJob = { activePanel = MainPanel.CustomerJob(it) }
                    )

                    MainTab.Work -> WorkScreen(
                        snapshot = dashboard,
                        providerModeUnlocked = providerModeUnlocked,
                        onOpenProviderSetup = { activePanel = MainPanel.ProviderSetup },
                        onOpenProviderDashboard = { activePanel = MainPanel.ProviderDashboard }
                    )

                    MainTab.Messages -> MessagesScreen(
                        snapshot = dashboard,
                        onOpenChat = { activePanel = MainPanel.Chat(it) }
                    )

                    MainTab.Profile -> ProfileScreen(
                        account = account,
                        snapshot = dashboard,
                        providerModeUnlocked = providerModeUnlocked,
                        onOpenBusiness = { activePanel = MainPanel.BusinessWorkspace },
                        onOpenSettings = { activePanel = MainPanel.ProfileSettings },
                        onOpenWallet = { activePanel = MainPanel.Wallet },
                        onOpenReviews = { activePanel = MainPanel.Reviews },
                        onOpenSupport = { activePanel = MainPanel.Support },
                        onOpenPublicProfile = { activePanel = MainPanel.PublicProfile },
                        onLogout = onLogout
                    )
                }

                when (val panel = activePanel) {
                    MainPanel.CreateRequest -> RequestDraftScreen(
                        repository = workflowRepository,
                        onBack = { activePanel = null }
                    )

                    MainPanel.ProviderSetup -> ProviderOnboardingScreen(
                        repository = workflowRepository,
                        onCompleted = { providerModeUnlocked = true },
                        onBack = { activePanel = null }
                    )

                    MainPanel.BusinessWorkspace -> BusinessWorkspaceScreen(
                        repository = workflowRepository,
                        summary = dashboard.businessWorkspace,
                        onBack = { activePanel = null }
                    )

                    MainPanel.ProfileSettings -> ProfileSettingsScreen(
                        repository = workflowRepository,
                        onOpenSetting = { activePanel = MainPanel.SettingDetail(it) },
                        onBack = { activePanel = null }
                    )

                    MainPanel.ProviderDashboard -> ProviderDashboardScreen(
                        repository = workflowRepository,
                        onOpenProviderSetup = { activePanel = MainPanel.ProviderSetup },
                        onOpenChat = { activePanel = MainPanel.Chat(it) },
                        onBack = { activePanel = null }
                    )

                    MainPanel.Wallet -> WalletScreen(
                        repository = workflowRepository,
                        onBack = { activePanel = null }
                    )

                    MainPanel.Reviews -> ReviewsScreen(
                        repository = workflowRepository,
                        onBack = { activePanel = null }
                    )

                    MainPanel.Support -> SupportCenterScreen(
                        repository = workflowRepository,
                        onBack = { activePanel = null }
                    )

                    MainPanel.PublicProfile -> PublicProfileScreen(
                        repository = workflowRepository,
                        onBack = { activePanel = null }
                    )

                    is MainPanel.Chat -> ChatDetailScreen(
                        repository = workflowRepository,
                        conversationId = panel.conversationId,
                        onBack = { activePanel = null }
                    )

                    is MainPanel.ExploreItem -> ExploreItemDetailScreen(
                        repository = discoveryRepository,
                        itemId = panel.itemId,
                        onBack = { activePanel = null }
                    )

                    is MainPanel.CustomerJob -> CustomerJobDetailScreen(
                        repository = workflowRepository,
                        jobId = panel.jobId,
                        onOpenChat = { activePanel = MainPanel.Chat(it) },
                        onBack = { activePanel = null }
                    )

                    is MainPanel.SettingDetail -> SettingDetailScreen(
                        repository = workflowRepository,
                        itemId = panel.itemId,
                        onBack = { activePanel = MainPanel.ProfileSettings }
                    )

                    null -> Unit
                }
            }
            MainBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

@Composable
private fun MainBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ViaverseColors.WarmSurface)
            .border(1.dp, ViaverseColors.BorderSubtle)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) ViaverseColors.WarmMuted else ViaverseColors.WarmSurface)
                    .clickable { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TabIcon(
                    tab = tab,
                    tint = if (selected) ViaverseColors.BrandOrange else ViaverseColors.MutedText
                )
                Text(
                    text = tab.labelTr,
                    color = if (selected) ViaverseColors.Ink else ViaverseColors.MutedText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TabIcon(
    tab: MainTab,
    tint: Color
) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val stroke = Stroke(width = 2.4f, cap = StrokeCap.Round)
        when (tab) {
            MainTab.Explore -> {
                drawCircle(tint, radius = size.minDimension * 0.26f, style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(size.width * 0.62f, size.height * 0.62f),
                    end = Offset(size.width * 0.86f, size.height * 0.86f),
                    strokeWidth = 2.4f,
                    cap = StrokeCap.Round
                )
                drawCircle(tint, radius = 2.2f, center = Offset(size.width * 0.42f, size.height * 0.42f))
            }

            MainTab.Requests -> {
                drawRoundRect(tint, topLeft = Offset(4f, 3f), size = Size(size.width - 8f, size.height - 6f), style = stroke)
                drawLine(tint, Offset(8f, 8f), Offset(size.width - 8f, 8f), 2.2f, StrokeCap.Round)
                drawLine(tint, Offset(8f, 13f), Offset(size.width - 12f, 13f), 2.2f, StrokeCap.Round)
            }

            MainTab.Work -> {
                drawRoundRect(tint, topLeft = Offset(3f, 7f), size = Size(size.width - 6f, size.height - 9f), style = stroke)
                drawLine(tint, Offset(8f, 7f), Offset(8f, 4f), 2.2f, StrokeCap.Round)
                drawLine(tint, Offset(size.width - 8f, 7f), Offset(size.width - 8f, 4f), 2.2f, StrokeCap.Round)
                drawCircle(tint, radius = 2.2f, center = Offset(size.width * 0.5f, size.height * 0.58f))
            }

            MainTab.Messages -> {
                drawRoundRect(tint, topLeft = Offset(3f, 4f), size = Size(size.width - 6f, size.height - 9f), style = stroke)
                drawLine(tint, Offset(8f, size.height - 5f), Offset(6f, size.height - 2f), 2.2f, StrokeCap.Round)
                drawLine(tint, Offset(8f, 10f), Offset(size.width - 8f, 10f), 2.2f, StrokeCap.Round)
            }

            MainTab.Profile -> {
                drawCircle(tint, radius = size.minDimension * 0.18f, center = Offset(size.width * 0.5f, size.height * 0.32f), style = stroke)
                drawArc(
                    color = tint,
                    startAngle = 205f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(4f, 9f),
                    size = Size(size.width - 8f, size.height - 7f),
                    style = stroke
                )
            }
        }
    }
}
