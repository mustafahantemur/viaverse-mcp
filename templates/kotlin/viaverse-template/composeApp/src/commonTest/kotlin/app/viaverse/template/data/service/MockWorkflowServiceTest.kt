package app.viaverse.template.data.service

import app.viaverse.template.domain.model.BusinessWorkspaceStatus
import app.viaverse.template.domain.model.ProviderSetupStepStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockWorkflowServiceTest {
    private val service = MockWorkflowService()

    @Test
    fun customerJobsMatchDashboardRequestIds() {
        val jobIds = service.customerJobs().map { it.id }

        assertTrue(jobIds.contains("job_001"))
        assertTrue(jobIds.contains("job_002"))
    }

    @Test
    fun providerOnboardingCompletesOnePendingStepAtATime() {
        val initial = service.providerOnboarding()
        val progressed = service.progressProviderOnboarding(initial)

        assertEquals(
            initial.steps.count { it.status == ProviderSetupStepStatus.DONE } + 1,
            progressed.steps.count { it.status == ProviderSetupStepStatus.DONE }
        )
    }

    @Test
    fun businessWorkspaceCanReachPublishedState() {
        var detail = service.businessWorkspace(
            MockDashboardService().loadDashboard(account = null).businessWorkspace
        )

        repeat(detail.steps.size) {
            detail = service.progressBusinessWorkspace(detail)
        }

        assertEquals(BusinessWorkspaceStatus.PUBLISHED, detail.summary.status)
        assertTrue(detail.steps.all { it.status == ProviderSetupStepStatus.DONE })
    }
}
