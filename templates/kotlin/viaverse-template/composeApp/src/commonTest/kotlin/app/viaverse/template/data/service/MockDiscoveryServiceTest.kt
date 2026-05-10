package app.viaverse.template.data.service

import app.viaverse.template.domain.model.DiscoveryMode
import app.viaverse.template.domain.model.SearchCriteria
import app.viaverse.template.domain.model.SocialPostType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockDiscoveryServiceTest {
    private val service = MockDiscoveryService()

    @Test
    fun socialFeedCanBeFilteredByPostType() {
        val snapshot = service.loadDiscovery(
            SearchCriteria(
                mode = DiscoveryMode.DO_WORK,
                selectedPostType = SocialPostType.ANNOUNCEMENT
            )
        )

        assertTrue(snapshot.socialPosts.isNotEmpty())
        assertTrue(snapshot.socialPosts.all { it.type == SocialPostType.ANNOUNCEMENT })
    }

    @Test
    fun socialFeedSearchMatchesPostBodyAndTitle() {
        val snapshot = service.loadDiscovery(
            SearchCriteria(
                mode = DiscoveryMode.DO_WORK,
                query = "kedi"
            )
        )

        assertEquals(listOf(SocialPostType.ANNOUNCEMENT), snapshot.socialPosts.map { it.type })
    }
}
