# Kotlin Template Guardrails

Bu dosya Viaverse Kotlin/Compose template'inde tekrar edilmemesi gereken hatalar için kısa kontrol listesidir.

## Auth Mock Config

- Mock kullanıcı şifresi ve 2FA bilgisi tek kaynakta tutulmalı: `AppConfig.auth.mockCredentials`.
- Identifier her auth işleminde `trim().lowercase()` ile normalize edilmeli.
- Mevcut kullanıcı akışı: identifier -> password -> 2FA açıksa OTP -> session.
- 2FA açık kullanıcıda session şifreden sonra değil, OTP doğrulamasından sonra kaydedilmeli.
- Mock OTP ekranda açıkça yazılmamalı; sadece config/test bilgisi olarak kalmalı.

## Compose Resource Rules

- Android target'ta Compose Multiplatform `painterResource` ile SVG kullanma. Android runtime SVG formatını desteklemiyor ve crash üretir.
- PNG kaynakları `composeResources/drawable` altında kullanılabilir.
- Vektör ikon gerekiyorsa `ImageVector` veya Android-safe vector drawable yaklaşımı kullanılmalı.

## OTP UI Rules

- OTP ayrı ekran/dosya olarak tutulmalı: `OtpAuthScreen.kt`.
- OTP ekranında hem metin inputu hem kutular aynı anda bulunmamalı.
- Tek gerçek input kullanılmalı; kutular sadece görsel temsil olmalı.
- Kullanıcı kutulara tek tek tıklamak zorunda kalmamalı.
- OTP kutuları tam kare olmalı ve ekranın merkezine yakın, biraz yukarıda durmalı.
- OTP ekranında kısa açıklama yeterli; mock/debug metni gösterilmemeli.

## Auth Screen Structure

- `AuthScreen.kt` mümkün olduğunca ince kalmalı; state ve ekran geçişlerini yönetmeli.
- Her auth adımı ayrı dosyada olmalı: identifier, password, OTP, sign up.
- Ortak layout/header/button/social login parçaları ayrı composable dosyalarında tutulmalı.
- Büyük auth dosyası tekrar oluşmamalı; review edilebilirlik için dosyalar küçük ve tek sorumluluklu kalmalı.
- UI dosyaları mock veri üretmemeli; veri/config/service çağrıları repository/service katmanından gelmeli.

## Main Workflow Structure

- Explore, dashboard, request draft, provider onboarding, business workspace, settings and chat data service/repository arkasından gelmeli.
- Business workspace kişisel hizmet verme geçişinden ayrı tutulmalı; doğrulama, merchant, ekip rolleri, katalog ve publish-readiness akışı göstermeli.
- Kişisel hizmet verme Airbnb tarzı hafif onboarding olarak kalmalı; işletme hesabı gibi davranmamalı.
- UI içinde business state veya role string karşılaştırması yapılmamalı; enum/sealed state kullanılmalı.
- AI insight metinleri Türkçe olmalı ve ekranın karar/riski anlatmalı.

## Validation

- Her UI/resource değişiminden sonra:

```bash
./gradlew :composeApp:assembleDebug
```

- Android emülatörde install/launch sonrası logcat'te şunlar aranmalı:

```text
FATAL EXCEPTION
ComposeInternal
AndroidRuntime
```
