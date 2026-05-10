package app.viaverse.template.data.repository

import app.viaverse.template.data.service.MockWorkflowService
import app.viaverse.template.domain.model.BusinessWorkspaceDetail
import app.viaverse.template.domain.model.BusinessWorkspaceSummary
import app.viaverse.template.domain.model.ChatThread
import app.viaverse.template.domain.model.CustomerJob
import app.viaverse.template.domain.model.OfferPreview
import app.viaverse.template.domain.model.ProfileSettingGroup
import app.viaverse.template.domain.model.ProviderDashboardSnapshot
import app.viaverse.template.domain.model.ProviderOnboardingSnapshot
import app.viaverse.template.domain.model.PublicProfileSnapshot
import app.viaverse.template.domain.model.ReviewSummary
import app.viaverse.template.domain.model.RequestDraft
import app.viaverse.template.domain.model.SettingDetail
import app.viaverse.template.domain.model.SupportTicket
import app.viaverse.template.domain.model.WalletSnapshot

class WorkflowRepository(
    private val service: MockWorkflowService
) {
    fun newRequestDraft(): RequestDraft {
        return service.newRequestDraft()
    }

    fun readyRequestDraft(draft: RequestDraft): RequestDraft {
        return service.readyRequestDraft(draft)
    }

    fun submitRequestDraft(draft: RequestDraft): RequestDraft {
        return service.submitRequestDraft(draft)
    }

    fun customerJobs(): List<CustomerJob> {
        return service.customerJobs()
    }

    fun offersForJob(jobId: String): List<OfferPreview> {
        return service.offersForJob(jobId)
    }

    fun providerOnboarding(): ProviderOnboardingSnapshot {
        return service.providerOnboarding()
    }

    fun providerDashboard(): ProviderDashboardSnapshot {
        return service.providerDashboard()
    }

    fun businessWorkspace(detail: BusinessWorkspaceSummary): BusinessWorkspaceDetail {
        return service.businessWorkspace(detail)
    }

    fun wallet(): WalletSnapshot {
        return service.wallet()
    }

    fun reviews(): List<ReviewSummary> {
        return service.reviews()
    }

    fun supportTickets(): List<SupportTicket> {
        return service.supportTickets()
    }

    fun profileSettings(): List<ProfileSettingGroup> {
        return service.profileSettings()
    }

    fun settingDetail(itemId: String): SettingDetail {
        return service.settingDetail(itemId)
    }

    fun publicProfile(): PublicProfileSnapshot {
        return service.publicProfile()
    }

    fun chatThread(conversationId: String): ChatThread {
        return service.chatThread(conversationId)
    }
}
