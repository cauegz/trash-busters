# Copilot Instructions - Trash Busters Backend

## General Rules
- Be concise, direct, and avoid boilerplate explanations or introductory text.
- Write all code identifiers (classes, methods, variables, endpoints) in English; keep domain-specific API fields (`imagem`, `latitude`, `longitude`, `endereco`) and error messages in Portuguese if matching existing DTOs/schema.
- Do not modify `database/init/` SQL scripts unless explicitly requested; align JPA entities with `database/init/01-schema.sql`.

## Tech Stack & Environment
- **Runtime:** Java 21, Spring Boot 4.1.1, Maven (`./mvnw`).
- **Database:** PostgreSQL 15 + PostGIS 3.3, Spring Data JPA, Hibernate Spatial (`org.locationtech.jts.geom.Point`, SRID 4326).
- **AI:** Spring AI with Chain of Responsibility (`AiProvider` -> `SpringAiProvider` -> `AiProviderChain` ordered via `@Order`).
- **Security:** Stateless Spring Security + JWT (`Bearer` token).

## Architecture & Package Conventions
- `controllers/`: REST endpoints (`/api/v1/...`), validation (`@Valid`), multipart handling (`multipart/form-data`), and HTTP status mapping only. No business logic.
- `domain/`: JPA entities, Java `record` DTOs, enums (e.g., report status `VALIDATED`, `REJECTED_AI`), and Spring Data repositories.
- `services/`: Business rules, PostGIS spatial queries, boss combat/damage calculation, combat logs, file storage, and AI integration.
- `infra/`: Security filters, JWT utilities, `@ControllerAdvice` global exception handling, and Spring beans configuration.

## Domain & Technical Guidelines
- **AI Providers:** Never couple domain or services to a specific vendor SDK. Always inject and call `AiProviderChain` or `AiProvider` interfaces.
- **Spatial Data:** Always enforce SRID `4326` (WGS 84) when creating or querying `Point` geometries (`longitude` = X, `latitude` = Y).
- **Testing:** Write unit tests with JUnit 5 and Mockito for `services/` (especially boss combat math and `AiProviderChain` fallback behavior). Use `./mvnw test` to verify changes.