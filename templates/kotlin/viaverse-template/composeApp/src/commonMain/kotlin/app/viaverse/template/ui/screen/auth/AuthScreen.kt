package app.viaverse.template.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.viaverse.template.config.AuthConfig
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AuthDecision

@Composable
fun AuthScreen(
    config: AuthConfig,
    onResolveIdentifier: (String) -> AuthDecision,
    onRequestOtp: (String) -> Boolean,
    onVerifyOtp: (String, String) -> Account?,
    onSignUp: (String, String, String) -> Account?,
    onVerifyPassword: (String, String) -> Account?,
    onRequiresTwoFactor: (String) -> Boolean,
    onAuthenticated: (Account) -> Unit
) {
    var step by remember { mutableStateOf(AuthStep.Identifier) }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
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
        val account = onSignUp(fullName, identifier, config.mockOtp)

        if (account == null) {
            error = "Hesap oluşturulamadı."
        } else {
            onAuthenticated(account)
        }
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
            error = error,
            onFullNameChange = { fullName = it },
            onIdentifierChange = { identifier = it },
            onContinue = ::continueFromSignUp,
            onBack = ::returnToIdentifier
        )
    }
}
