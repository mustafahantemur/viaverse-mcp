# BFF / API Gateway Guidelines

## Purpose

The BFF/API Gateway adapts backend APIs to client needs.

It may:

- attach auth context
- validate request envelope
- aggregate read models
- route to bounded contexts
- provide realtime transport adapter
- apply edge-level rate limits

It must not:

- own domain aggregates
- bypass service policies
- mutate another service database
- hide security failures
- contain payment provider business logic
