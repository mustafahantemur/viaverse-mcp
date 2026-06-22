package app.viaverse.template.ui.screen.auth

internal enum class AuthStep {
    Identifier,
    Password,
    OtpForTwoFactor,
    OtpForSignUp,
    SignUp
}
