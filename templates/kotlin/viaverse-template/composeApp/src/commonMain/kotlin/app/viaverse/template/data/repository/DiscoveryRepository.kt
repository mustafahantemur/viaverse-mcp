package app.viaverse.template.data.repository

import app.viaverse.template.data.service.MockDiscoveryService
import app.viaverse.template.domain.model.DiscoverySnapshot
import app.viaverse.template.domain.model.SearchCriteria

class DiscoveryRepository(
    private val service: MockDiscoveryService
) {
    fun load(criteria: SearchCriteria): DiscoverySnapshot {
        return service.loadDiscovery(criteria)
    }
}
