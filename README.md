# Mini Payment System (учебный проект)

Этот репозиторий содержит пошаговую сборку платёжной системы на Spring Boot. Основная ветка `main` предназначена для документации: рабочий код лежит в ветках блоков, каждая из которых добавляет новую часть функционала поверх предыдущей.

## Быстрый старт (см. нужную ветку)
- Требования: Java 21, Docker (для Postgres/Redis/Kafka), Gradle wrapper из репозитория.
- Поднять инфраструктуру: `docker-compose up -d` (поднимет Postgres 5432, Redis 6379, Kafka 9092/29092).
- Выбрать ветку (обычно вариант с суффиксом `-done` — полностью готовое решение) и запустить сервисы:
  - `./gradlew :payment-service:bootRun` (порт 8080)
  - при наличии аудита (`block-4-*`) дополнительно `./gradlew :audit-service:bootRun` (порт 8081)
- Swagger UI доступен на `/swagger-ui` у каждого сервиса; OpenAPI JSON — `/api-docs`.

## Дерево модулей
- `payment-service` — основной REST API для платежей и пользователей.
- `audit-service` — приём и выдача истории событий платежей (подключается на этапе Kafka).
- `common` — общие DTO/события для обмена между сервисами.

## Прогресс по веткам
| Ветка | Что добавлено | Зависимости инфраструктуры |
| --- | --- | --- |
| `block-1-http-api` | Базовый REST API для платежей: создание, просмотр, подтверждение; JPA c Postgres; базовый обработчик ошибок. | Postgres |
| `block-1-http-api-done` | Готовое решение блока 1. | Postgres |
| `block-2-database` | Пользователи, связь платеж–пользователь, валидации email, вывод списка пользователей; подготовлены Liquibase-скрипты и сид `seed-users.sql`. | Postgres |
| `block-2-database-done` | Реализована проверка существования пользователя, `ddl-auto=validate`, миграции включены. | Postgres |
| `block-3-redis` | Кэширование платежей в Redis (ручное через `RedisTemplate`), TTL задаётся в `app.cache.payments-ttl`. | Postgres, Redis |
| `block-3-redis-done` | Перевод кэширования на аннотации `@Cacheable/@CacheEvict`, настроен `RedisCacheManager`. | Postgres, Redis |
| `block-4-kafka` | Публикация событий платежей в Kafka, аудит-сервис слушает и сохраняет события; TODO по дедупликации событий. | Postgres, Redis, Kafka |
| `block-4-kafka-done` | Дедупликация через таблицу `processed_events`, REST `/api/audit/payments/{id}` для истории, общий модуль с `PaymentEvent`. | Postgres, Redis, Kafka |

## API (финальное состояние в `block-4-kafka-done`)
- `payment-service` (8080)
  - `POST /api/users` — создать пользователя (email уникален)
  - `GET /api/users` / `GET /api/users/{id}` — список и детали с платежами
  - `POST /api/payments` — создать платёж (с проверкой существования пользователя и лимитом 10 000)
  - `GET /api/payments/{id}` — получить платёж (кэшируется в Redis)
  - `POST /api/payments/{id}/confirm` — подтвердить платёж (инвалидация кэша)
- `audit-service` (8081)
  - `GET /api/audit/payments/{id}` — история событий платежа, формируется из Kafka-сообщений

## Полезные подсказки
- Ветки без суффикса `-done` содержат заготовки с пометками `TODO` для самостоятельной доработки.
- Миграции Liquibase находятся в `payment-service/src/main/resources/db/changelog`; для тестовых данных есть `src/main/resources/seed/seed-users.sql`.
- Обе службы используют одну базу `payments` (см. `docker-compose.yaml`), поэтому поднимайте Postgres один раз для всех шагов.
