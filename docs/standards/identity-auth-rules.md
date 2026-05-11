# Viaverse Identity/Auth Rules

These rules are global security rules. Identity-service is the main example, but JWT, auth, OTP, sessions, and abuse protection patterns apply wherever a backend service authenticates or authorizes requests.

## JWT and Spring Security

- Do not manually build, sign, parse, or verify JWTs.
- Use Spring Security OAuth2 Resource Server for Bearer token authentication.
- Use Spring Security `JwtEncoder` and `JwtDecoder` with Nimbus-backed implementations.
- Nimbus is acceptable as the JOSE/JWT engine behind Spring Security.
- Authorization header parsing belongs in the Spring Security filter chain, not scattered inside services.
- Services should use authenticated principal/context.

JWT access token claims must include:

- `iss`
- `sub`
- `sid`
- `iat`
- `exp`

`sub` is always the persisted `Account.id`. Access token TTL must be configurable.

## Refresh Tokens and Sessions

- Refresh tokens are opaque/random.
- Store refresh tokens hashed.
- Support rotation and revocation.
- Reused rotated refresh tokens should revoke the session when appropriate.
- Authenticated requests must verify the account still exists and is active.

## OTP and Abuse Protection

- OTP values are hashed in DB.
- Debug fixed OTP may exist only in local/test.
- Debug fixed OTP must not bypass rate limits, max attempts, expiry, used-state, or session security.
- Debug auth enabled outside local/test must fail startup.
- Auth start and OTP verification must be rate-limited.
- OTP resend cooldown must exist.
- Temporary lockout is preferred over permanent account lockout.
- Rate limiting belongs in focused abuse-protection classes/packages, not inside large auth services.

Recommended identity package shape:

```text
app.viaverse.identity.auth
  api
    dto
  application
    usecase
    service
  domain
    enums
    value
    policy
  infrastructure
    persistence
      entity
      repository
    security
    otp
    ratelimit

app.viaverse.identity.account
  api
    dto
  application
  domain
  infrastructure
    persistence
      entity
      repository

app.viaverse.identity.consent
  application
  domain
  infrastructure
    persistence
      entity
      repository

app.viaverse.identity.shared
  normalization
  error
  audit
```

## Identity Auth Error Codes

Use typed `AppErrorCode` values. Do not hardcode user-facing messages inside service logic.

Required auth codes:

- `AUTH_INVALID_ACCESS_TOKEN`
- `AUTH_ACCESS_TOKEN_EXPIRED`
- `AUTH_BEARER_TOKEN_REQUIRED`
- `AUTH_INVALID_AUTH_FLOW`
- `AUTH_INVALID_OTP`
- `AUTH_OTP_EXPIRED`
- `AUTH_RATE_LIMITED`
- `AUTH_INVALID_REGISTRATION_TOKEN`
- `AUTH_REGISTRATION_TOKEN_EXPIRED`
- `AUTH_REQUIRED_CONSENTS_MISSING`
- `AUTH_DISPLAY_NAME_REQUIRED`
- `AUTH_INVALID_REFRESH_TOKEN`
- `AUTH_REFRESH_TOKEN_EXPIRED`
- `AUTH_SESSION_EXPIRED`
- `AUTH_ACCOUNT_NOT_ACTIVE`

Exceptions should carry code, default message, and parameters. `GlobalExceptionHandler` maps exceptions consistently to ProblemDetail.
