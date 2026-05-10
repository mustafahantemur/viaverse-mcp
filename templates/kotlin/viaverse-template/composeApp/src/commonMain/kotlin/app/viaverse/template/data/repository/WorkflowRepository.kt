package app.viaverse.template.data.repository

import app.viaverse.template.data.service.MockWorkflowService
import app.viaverse.template.domain.model.BusinessWorkspaceDetail
import app.viaverse.template.domain.model.BusinessWorkspaceSummary
import app.viaverse.template.domain.model.ChatThread
import app.viaverse.template.domain.model.ProfileSettingGroup
import app.viaverse.template.domain.model.ProviderOnboardingSnapshot
import app.viaverse.template.domain.model.RequestDraft

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

    fun providerOnboarding(): ProviderOnboardingSnapshot {
        return service.providerOnboarding()
    }

    fun businessWorkspace(detail: BusinessWorkspaceSummary): BusinessWorkspaceDetail {
        return service.businessWorkspace(detail)
    }

    fun profileSettings(): List<ProfileSettingGroup> {
        return service.profileSettings()
    }

    fun chatThread(conversationId: String): ChatThread {
        return service.chatThread(conversationId)
    }
}
