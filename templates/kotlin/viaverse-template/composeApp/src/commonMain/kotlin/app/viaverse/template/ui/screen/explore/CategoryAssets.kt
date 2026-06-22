package app.viaverse.template.ui.screen.explore

import app.viaverse.template.domain.model.ServiceCategoryId
import app.viaverse.template.generated.resources.Res
import app.viaverse.template.generated.resources.category_care_health
import app.viaverse.template.generated.resources.category_cleaning
import app.viaverse.template.generated.resources.category_creative_media
import app.viaverse.template.generated.resources.category_digital_software
import app.viaverse.template.generated.resources.category_education
import app.viaverse.template.generated.resources.category_events
import app.viaverse.template.generated.resources.category_home_repair
import app.viaverse.template.generated.resources.category_local_help
import app.viaverse.template.generated.resources.category_logistics
import app.viaverse.template.generated.resources.category_megaphone
import app.viaverse.template.generated.resources.category_pets
import app.viaverse.template.generated.resources.category_professional_consulting
import app.viaverse.template.generated.resources.category_work
import org.jetbrains.compose.resources.DrawableResource

internal fun categoryDrawable(categoryId: ServiceCategoryId): DrawableResource {
    return when (categoryId) {
        ServiceCategoryId.HOME_REPAIR -> Res.drawable.category_home_repair
        ServiceCategoryId.CLEANING -> Res.drawable.category_cleaning
        ServiceCategoryId.EDUCATION -> Res.drawable.category_education
        ServiceCategoryId.CARE_HEALTH -> Res.drawable.category_care_health
        ServiceCategoryId.LOGISTICS -> Res.drawable.category_logistics
        ServiceCategoryId.CREATIVE_MEDIA -> Res.drawable.category_creative_media
        ServiceCategoryId.DIGITAL_SOFTWARE -> Res.drawable.category_digital_software
        ServiceCategoryId.PROFESSIONAL_CONSULTING -> Res.drawable.category_professional_consulting
        ServiceCategoryId.LOCAL_HELP -> Res.drawable.category_local_help
        ServiceCategoryId.EVENTS -> Res.drawable.category_events
        ServiceCategoryId.ANNOUNCEMENT -> Res.drawable.category_megaphone
        ServiceCategoryId.PETS -> Res.drawable.category_pets
        ServiceCategoryId.WORK -> Res.drawable.category_work
    }
}
