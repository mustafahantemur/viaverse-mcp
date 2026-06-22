package app.viaverse.template.data.repository

import app.viaverse.template.data.service.MockDashboardService
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.DashboardSnapshot

class DashboardRepository(
    private val service: MockDashboardService
) {
    fun load(account: Account?): DashboardSnapshot {
        return service.loadDashboard(account)
    }
}
