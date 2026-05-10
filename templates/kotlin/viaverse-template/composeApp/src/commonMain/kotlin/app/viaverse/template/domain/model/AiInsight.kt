package app.viaverse.template.domain.model

data class AiInsight(
    val screenId: String,
    val summaryTr: String,
    val recommendedNextActionTr: String,
    val riskSignalsTr: List<String> = emptyList()
)

