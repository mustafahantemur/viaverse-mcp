package app.viaverse.template.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.viaverse.template.config.AuthConfig
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AuthDecision
import app.viaverse.template.platform.PlatformBackHandler

@Composable
fun AuthScreen(
    config: AuthConfig,
    onResolveIdentifier: (String) -> AuthDecision,
    onRequestOtp: (String) -> Boolean,
    onVerifyOtp: (String, String) -> Account?,
    onSignUp: (String, String, String, String) -> Account?,
    onVerifyPassword: (String, String) -> Account?,
    onRequiresTwoFactor: (String) -> Boolean,
    onAuthenticated: (Account) -> Unit
) {
    var step by remember { mutableStateOf(AuthStep.Identifier) }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpPasswordConfirm by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun resetError() {
        error = null
    }

    fun returnToIdentifier() {
        step = AuthStep.Identifier
        password = ""
        otp = ""
        resetError()
    }

    fun continueFromIdentifier() {
        resetError()
        if (identifier.isBlank()) {
            error = "E-posta veya telefon gerekli."
            return
        }

        when (onResolveIdentifier(identifier)) {
            AuthDecision.OTP_FOR_SIGN_UP -> {
                onRequestOtp(identifier)
                otp = ""
                step = AuthStep.OtpForSignUp
            }

            AuthDecision.OTP_FOR_TWO_FACTOR -> {
                onRequestOtp(identifier)
                otp = ""
                step = AuthStep.OtpForTwoFactor
            }

            AuthDecision.PASSWORD_REQUIRED -> {
                password = ""
                step = AuthStep.Password
            }
        }
    }

    fun continueFromPassword() {
        resetError()
        val account = onVerifyPassword(identifier, password)

        if (account == null) {
            error = "Şifre hatalı."
            return
        }

        if (onRequiresTwoFactor(identifier)) {
            onRequestOtp(identifier)
            otp = ""
            step = AuthStep.OtpForTwoFactor
        } else {
            onAuthenticated(account)
        }
    }

    fun continueFromOtp() {
        resetError()
        when (step) {
            AuthStep.OtpForTwoFactor -> {
                val account = onVerifyOtp(identifier, otp)
                if (account == null) {
                    error = "OTP kodu hatalı."
                } else {
                    onAuthenticated(account)
                }
            }

            AuthStep.OtpForSignUp -> {
                if (otp == config.mockOtp) {
                    step = AuthStep.SignUp
                } else {
                    error = "OTP kodu hatalı."
                }
            }

            else -> Unit
        }
    }

    fun continueFromSignUp() {
        resetError()
        val validationError = validateSignUp(
            fullName = fullName,
            identifier = identifier,
            password = signUpPassword,
            passwordConfirm = signUpPasswordConfirm,
            termsAccepted = termsAccepted
        )
        if (validationError != null) {
            error = validationError
            return
        }

        val account = onSignUp(fullName, identifier, signUpPassword, config.mockOtp)

        if (account == null) {
            error = "Hesap oluşturulamadı."
        } else {
            onAuthenticated(account)
        }
    }

    PlatformBackHandler(enabled = step != AuthStep.Identifier) {
        returnToIdentifier()
    }

    when (step) {
        AuthStep.Identifier -> IdentifierAuthScreen(
            identifier = identifier,
            error = error,
            onIdentifierChange = { identifier = it },
            onContinue = ::continueFromIdentifier
        )

        AuthStep.Password -> PasswordAuthScreen(
            password = password,
            error = error,
            onPasswordChange = { password = it },
            onContinue = ::continueFromPassword,
            onBack = ::returnToIdentifier
        )

        AuthStep.OtpForTwoFactor,
        AuthStep.OtpForSignUp -> OtpAuthScreen(
            otp = otp,
            error = error,
            actionLabel = if (step == AuthStep.OtpForSignUp) "Üyeliğe devam et" else "Doğrula",
            onOtpChange = { otp = it },
            onContinue = ::continueFromOtp,
            onBack = ::returnToIdentifier
        )

        AuthStep.SignUp -> SignUpAuthScreen(
            fullName = fullName,
            identifier = identifier,
            password = signUpPassword,
            passwordConfirm = signUpPasswordConfirm,
            termsAccepted = termsAccepted,
            error = error,
            onFullNameChange = { fullName = it },
            onIdentifierChange = { identifier = it },
            onPasswordChange = { signUpPassword = it },
            onPasswordConfirmChange = { signUpPasswordConfirm = it },
            onTermsAcceptedChange = { termsAccepted = it },
            onContinue = ::continueFromSignUp,
            onBack = ::returnToIdentifier
        )
    }
}

private fun validateSignUp(
    fullName: String,
    identifier: String,
    password: String,
    passwordConfirm: String,
    termsAccepted: Boolean
): String? {
    if (fullName.trim().length < 3) return "Ad soyad gerekli."
    if (identifier.isBlank()) return "E-posta veya telefon gerekli."
    if (password.length < 6) return "Şifre en az 6 karakter olmalı."
    if (password != passwordConfirm) return "Şifreler eşleşmiyor."
    if (!termsAccepted) return "Devam etmek için onay gerekli."
    return null
}
