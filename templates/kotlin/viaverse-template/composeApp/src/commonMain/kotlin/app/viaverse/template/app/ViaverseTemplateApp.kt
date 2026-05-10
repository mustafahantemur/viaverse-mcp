package app.viaverse.template.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.viaverse.template.config.AppConfig
import app.viaverse.template.data.repository.AuthRepository
import app.viaverse.template.data.repository.DashboardRepository
import app.viaverse.template.data.repository.DiscoveryRepository
import app.viaverse.template.data.service.MockAuthService
import app.viaverse.template.data.service.MockDashboardService
import app.viaverse.template.data.service.MockDiscoveryService
import app.viaverse.template.data.storage.MemoryStorageService
import app.viaverse.template.data.storage.StorageService
import app.viaverse.template.domain.model.Account
import app.viaverse.template.platform.PlatformBackHandler
import app.viaverse.template.ui.screen.auth.AuthScreen
import app.viaverse.template.ui.screen.main.MainShell
import app.viaverse.template.ui.screen.splash.SplashScreen
import app.viaverse.template.ui.theme.ViaverseTheme

private enum class AppRoute {
    Splash,
    Auth,
    Home
}

@Composable
fun ViaverseTemplateApp(
    storageService: StorageService = MemoryStorageService(),
    onCloseRequest: () -> Unit = {}
) {
    val repository = remember {
        AuthRepository(
            service = MockAuthService(AppConfig.mockUsers),
            storage = storageService
        )
    }
    val discoveryRepository = remember {
        DiscoveryRepository(service = MockDiscoveryService())
    }
    val dashboardRepository = remember {
        DashboardRepository(service = MockDashboardService())
    }

    var route by remember { mutableStateOf(AppRoute.Splash) }
    var account by remember { mutableStateOf<Account?>(null) }

    LaunchedEffect(Unit) {
        account = repository.restoreSession()
        if (account != null) {
            route = AppRoute.Home
        }
    }

    ViaverseTheme {
        PlatformBackHandler {
            onCloseRequest()
        }

        when (route) {
            AppRoute.Splash -> {
                if (account == null) {
                    AuthScreen(
                        config = AppConfig.auth,
                        onResolveIdentifier = repository::resolveIdentifier,
                        onRequestOtp = repository::requestOtp,
                        onVerifyOtp = repository::verifyOtp,
                        onSignUp = repository::signUp,
                        onVerifyPassword = repository::verifyPassword,
                        onRequiresTwoFactor = repository::requiresTwoFactor,
                        onAuthenticated = { authenticatedAccount ->
                            account = authenticatedAccount
                            route = AppRoute.Home
                        }
                    )
                } else {
                    MainShell(
                        account = account,
                        discoveryRepository = discoveryRepository,
                        dashboardRepository = dashboardRepository,
                        onLogout = {
                            repository.logout()
                            account = null
                            route = AppRoute.Auth
                        }
                    )
                }

                SplashScreen(
                    onComplete = {
                        route = if (account == null) AppRoute.Auth else AppRoute.Home
                    }
                )
            }

            AppRoute.Auth -> AuthScreen(
                config = AppConfig.auth,
                onResolveIdentifier = repository::resolveIdentifier,
                onRequestOtp = repository::requestOtp,
                onVerifyOtp = repository::verifyOtp,
                onSignUp = repository::signUp,
                onVerifyPassword = repository::verifyPassword,
                onRequiresTwoFactor = repository::requiresTwoFactor,
                onAuthenticated = { authenticatedAccount ->
                    account = authenticatedAccount
                    route = AppRoute.Home
                }
            )

            AppRoute.Home -> MainShell(
                account = account,
                discoveryRepository = discoveryRepository,
                dashboardRepository = dashboardRepository,
                onLogout = {
                    repository.logout()
                    account = null
                    route = AppRoute.Auth
                }
            )
        }
    }
}
