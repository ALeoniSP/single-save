# Changelog

## Unreleased

- (next) Improve OpenAI error handling and retry strategy
- (next) Fix static resource / 404 handling in GlobalErrorHandler

---

## v0.4

- Added suggestion generation endpoint for SavePoints
- Added suggestion service
- Added OpenAI client integration
- Added AI-based suggestion response flow using recent SavePoints
- Improved end-to-end authenticated suggestion flow with Google OAuth2

---

## v0.3

- Added AccountController
- Added dev profile

---

## v0.2.1

- Minor merge fix

---

## v0.2

- Added Flyway migration
- Added SavePoint controller
- Added SavePoint repository
- Added SavePoint service

---

## v0.1

- Added CHANGELOG
- Added .gitignore
- Added WebFlux skeleton (Spring Boot + WebFlux)
- Added basic endpoints: /health, /, /me
- Added SavePoint feature skeleton (DTOs, controller, in-memory store)
- Added WebFlux Security configuration
- Added Google OAuth2 client configuration
- Added Docker setup for DB (docker-compose + init schema)

---

## v0

- Added initial README