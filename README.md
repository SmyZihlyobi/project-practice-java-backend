# 📚 Документация Project Practice API

## 📄 Оглавление

1. [Введение](#1-введение)
2. [🚀 Используемые технологии](#2--используемые-технологии)
3. [🏗️ Обзор архитектуры](#3--обзор-архитектуры)
4. [🔌 Эндпоинты API](#4--эндпоинты-api)

* [4.1 GraphQL API (`/graphql`)](#41-graphql-api-graphql)
  *   [Запросы (Queries)](#запросы-queries)
  *   [Мутации (Mutations)](#мутации-mutations)
* [4.2 REST API](#42-rest-api)
  *   [Аутентификация и управление компаниями (`/company/*`)](#аутентификация-и-управление-компаниями-company)
  *   [Управление файлами (`api/v1/files/*`)](#управление-файлами-apiv1files)
  *   [Экспорт (`/export/*`)](#экспорт-export)
  *   [Проверка reCAPTCHA](#проверка-recaptcha)

5. [🔄 Асинхронные операции (Kafka)](#5--асинхронные-операции-kafka)
6. [💾 Детали обработки файлов](#6--детали-обработки-файлов)
7. [📊 Миграции базы данных](#7--миграции-базы-данных)
8. [💡 Заметки для будущих разработчиков](#8--заметки-для-будущих-разработчиков)

---

## 1. Введение

Этот документ представляет собой документацию для бэкенд-приложения **Project Practice API**. Приложение разработано на
`Java` с использованием фреймворка `Spring Boot`. Его основная цель — управление жизненным циклом проектной практики,
включая обработку данных о компаниях, студентах, проектах, командах и связанных файлах (резюме, презентации, технические
задания).

API предоставляет:

* **GraphQL API:** Для гибких запросов и изменения данных.
* **REST эндпоинты:** Для специфических операций, таких как аутентификация, обработка файлов и экспорт данных.

---

## 2. 🚀 Используемые технологии

На основе `pom.xml` и исходного кода используются следующие ключевые технологии:

* **Язык:** `Java 17`
* **Фреймворк:** `Spring Boot (v3.4.3)`
* `Spring Web`: Для RESTful API.
* `Spring Data JPA`: Для работы с базой данных.
* `Spring Security`: Для аутентификации (`JWT`) и авторизации (`@PreAuthorize`).
* `Spring GraphQL`: Для реализации GraphQL API.
* `Spring Validation`: Для валидации DTO.
* `Spring Kafka`: Для асинхронной связи через `Apache Kafka`.
* `Spring Cache`: Для кэширования (вероятно, с `Redis`).
* `Spring Boot Actuator`: Для мониторинга и метрик.
* `Spring Mail`: Для отправки email.
* **База данных:** `PostgreSQL`
* **Миграции БД:** `Flyway`
* **Кэш:** `Redis` (подразумевается из `spring-boot-starter-data-redis`)
* **Хранилище файлов:** `MinIO` (S3-совместимое)
* **Очередь сообщений:** `Apache Kafka`
* **Утилиты:**
* `Lombok`: Уменьшение шаблонного кода.
* `MapStruct`: Маппинг между `Entity` и `DTO`.
* `JJWT (Java JWT)`: Работа с JSON Web Tokens.
* `Springdoc OpenAPI (Swagger)`: Генерация документации REST API.
* `Apache POI`: Работа с файлами MS Office (Excel экспорт).
* `Thymeleaf`: Шаблонизатор для email.
* **Сборка:** `Maven`

---

## 3. 🏗️ Обзор архитектуры

Приложение следует стандартной многоуровневой архитектуре:

* **`controller`:** Обработка HTTP (REST) и GraphQL запросов. Делегирование логики сервисам. Валидация ввода.
* **`service`:** Основная бизнес-логика. Взаимодействие с репозиториями и внешними системами (`MinIO`, `Kafka`,
  `Email`).
* **`store`:**
* `entity`: JPA-сущности (таблицы БД).
* `repos`: Spring Data JPA репозитории.
* `dto`: Data Transfer Objects (включая `input` для входных данных).
* `mapper`: MapStruct интерфейсы для маппинга.
* **`config`:** Классы конфигурации (`Spring Security`, `JWT`, `Kafka` и др.).
* **`exceptions`:** Пользовательские исключения (например, `NotFound`).

**Ключевые взаимодействия:**

* **Загрузка файлов:** Контроллеры -> `FileValidateService` (валидация) -> Сервисы (`ResumeService`,
  `PresentationService`, `TechnicalSpecificationsService`) -> `MinIO` (загрузка) -> БД (сохранение имени файла).
* **Аутентификация:** `CompanyAuthController` -> `CompanyService` -> `AuthenticationManager` & `JwtTokenUtils` ->
  Генерация JWT.
* **Авторизация:** `Spring Security` с аннотациями `@PreAuthorize` на методах контроллеров (роли `ROLE_ADMIN`,
  `ROLE_COMPANY`, `ROLE_STUDENT`).
* **Асинхронные операции:** Сервисы (`CompanyService`, `ProjectService`) -> `KafkaTemplate` (отправка события) ->
  `EmailService` (обработка события, отправка email).
* **Экспорт данных:** `ExportController` -> `ExcelService` -> `Apache POI` (генерация Excel).

---

## 4. 🔌 Эндпоинты API

### 4.1 GraphQL API (`/graphql`)

#### Запросы (Queries)

* **`companies(): List<CompanyDto>`**
* **Описание:** Получает список всех *одобренных* компаний.
* **Безопасность:** ⚠️ Публичный (неявно). *Рекомендуется защитить.*
* **`company(id: Long): CompanyDto`**
* **Описание:** Получает компанию по ID.
* **Безопасность:** ⚠️ Публичный (неявно). *Рекомендуется защитить.*
* **`unapprovedCompanies(): List<CompanyDto>`**
* **Описание:** Получает список компаний, ожидающих одобрения.
* **Безопасность:** ⚠️ Публичный (неявно). *Рекомендуется защитить (возможно, `ROLE_ADMIN`).*
* **`projects(): List<ProjectDto>`**
* **Описание:** Получает список всех проектов. Результаты могут кэшироваться (`projects` кэш).
* **Безопасность:** Публичный (неявно).
* **`project(id: Long): ProjectDto`**
* **Описание:** Получает проект по ID.
* **Безопасность:** Публичный (неявно).
* **`students(): List<StudentDto>`**
* **Описание:** Получает список всех студентов.
* **Безопасность:** `ROLE_ADMIN`, `ROLE_COMPANY` или `ROLE_STUDENT`. Требуется Bearer токен.
* **`student(id: Long): StudentDto`**
* **Описание:** Получает студента по ID.
* **Безопасность:** `ROLE_ADMIN`, `ROLE_COMPANY` или `ROLE_STUDENT`. Требуется Bearer токен.
* **`teams(): List<TeamDto>`**
* **Описание:** Получает список всех команд, отсортированных по имени (`name`).
* **Безопасность:** Публичный (неявно).
* **`team(id: Long): TeamDto`**
* **Описание:** Получает команду по ID.
* **Безопасность:** Публичный (неявно).

#### Мутации (Mutations)

* **`createCompany(input: CompanyInputDto): CompanyDto`**
* **Описание:** Создает новую регистрацию компании (требует одобрения). Валидирует `input`.
* **Вход:** `CompanyInputDto`.
* **Безопасность:** Публичный.
* **`deleteCompany(id: Long): CompanyDto`**
* **Описание:** Удаляет компанию по ID.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`deleteAllCompanies(): Void`**
* **Описание:** Удаляет все компании, кроме администратора по умолчанию.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`createProject(input: ProjectInputDto): ProjectDto`**
* **Описание:** Создает новый проект, связывает с компанией из JWT. Отправляет Kafka событие (`student-project-id`),
  если `isStudentProject=true`. Инвалидирует кэш `projects`. Валидирует `input`.
* **Вход:** `ProjectInputDto`.
* **Безопасность:** `ROLE_ADMIN` или `ROLE_COMPANY`. Требуется Bearer токен (заголовок `Authorization`).
* **`deleteProject(id: Long): Void`**
* **Описание:** Удаляет проект по ID. Инвалидирует кэш `projects`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`deleteAllProjects(): Void`**
* **Описание:** Удаляет все проекты. Инвалидирует кэш `projects`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`createStudent(input: StudentInputDto): StudentDto`**
* **Описание:** Создает нового студента. Создает команду, если `teamName` не существует, иначе назначает существующую
  или команду по умолчанию (`"Не выбрана"`). Валидирует `input`.
* **Вход:** `StudentInputDto`.
* **Безопасность:** Публичный.
* **`deleteStudent(id: Long): StudentDto`**
* **Описание:** Удаляет студента по ID.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`deleteAllStudents(): Void`**
* **Описание:** Удаляет всех студентов.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`changeStudentTeam(teamId: Long): StudentDto`**
* **Описание:** Изменяет команду для студента из JWT.
* **Вход:** `teamId`.
* **Безопасность:** `ROLE_ADMIN` или `ROLE_STUDENT`. Требуется Bearer токен (заголовок `Authorization`).
* **`deleteTeam(id: Long): TeamDto`**
* **Описание:** Удаляет команду по ID. Студенты переназначаются в команду по умолчанию (`"Не выбрана"`). Нельзя удалить
  команду по умолчанию.
* **Безопасность:** ⚠️ Публичный (неявно). *Рекомендуется защитить (возможно, `ROLE_ADMIN`).*
* **`deleteAllTeams(): Void`**
* **Описание:** Удаляет все команды, кроме команды по умолчанию. Студенты переназначаются в команду по умолчанию.
* **Безопасность:** ⚠️ Публичный (неявно). *Рекомендуется защитить (возможно, `ROLE_ADMIN`).*

### 4.2 REST API

#### Аутентификация и управление компаниями (`/company/*`)

* **`POST /company/login`**
* **Описание:** Аутентификация компании.
* **Тело запроса:** `application/json`
  ```json
  { "email": "user@example.com", "password": "password123" }
  ```
* **Ответ (Успех):** `200 OK` с `application/json`
  ```json
  { "token": "your_jwt_token_here" }
  ```
* **Ответ (Ошибка):** `401 Unauthorized`
* **Безопасность:** Публичный.
* **`POST /company/approve?companyId={id}`**
* **Описание:** Одобрение компании. Генерирует пароль, отправляет Kafka событие.
* **Параметр запроса:** `companyId` (тип `Long`).
* **Ответ:** `200 OK`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`POST /company/change-password?email={email}`**
* **Описание:** Смена пароля компании. Генерирует новый пароль, отправляет Kafka событие.
* **Параметр запроса:** `email` (тип `String`).
* **Ответ:** `200 OK`.
* **Безопасность:** Публичный.

#### Управление файлами (`api/v1/files/*`)

##### Резюме (`resume`)

* **`GET /api/v1/files/resume/{fileName}`**
* **Описание:** Скачать файл резюме.
* **Переменная пути:** `fileName`.
* **Ответ:** `200 OK` (`application/octet-stream`), `404 Not Found`.
* **Безопасность:** ⚠️ Публичный (неявно). *Рекомендуется защитить.*
* **`POST /api/v1/files/resume`**
* **Описание:** Загрузить файл резюме для студента.
* **Запрос:** `multipart/form-data` (поля `file`, `userId`). DTO: `ResumeUploadRequest`.
* **Ответ (Успех):** `200 OK` (`application/json` `{ "message": "...", "fileName": "..." }`).
* **Ответ (Ошибка):** `400 Bad Request`, `500 Internal Server Error`.
* **Безопасность:** ⚠️ Неясно (см. TODO). *Рекомендуется `ROLE_STUDENT` или `ROLE_ADMIN`.* Требуется Bearer токен.
* **`DELETE /api/v1/files/resume/{fileName}`**
* **Описание:** Удалить файл резюме.
* **Переменная пути:** `fileName`.
* **Ответ:** `200 OK` (`application/json` `{ "message": "..." }`), `500 Internal Server Error`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`DELETE /api/v1/files/resume/clear-bucket`**
* **Описание:** Удалить ВСЕ файлы резюме из бакета `resume`.
* **Ответ:** `200 OK` (`application/json` `{ "message": "..." }`), `500 Internal Server Error`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.

##### Презентации (`presentation`)

* **`GET /api/v1/files/presentation/{fileName}`**
* **Описание:** Скачать файл презентации.
* **Переменная пути:** `fileName`.
* **Ответ:** `200 OK` (`application/octet-stream`), `404 Not Found`.
* **Безопасность:** ⚠️ Публичный (неявно). *Рекомендуется защитить.*
* **`POST /api/v1/files/presentation`**
* **Описание:** Загрузить файл презентации для проекта.
* **Запрос:** `multipart/form-data` (поля `file`, `projectId`). DTO: `FilesToProjectUploadDto`.
* **Ответ (Успех):** `200 OK` (`application/json` `{ "message": "...", "fileName": "..." }`).
* **Ответ (Ошибка):** `400 Bad Request`, `500 Internal Server Error`.
* **Безопасность:** `ROLE_ADMIN` или `ROLE_COMPANY`. Требуется Bearer токен.
* **`DELETE /api/v1/files/presentation/{fileName}`**
* **Описание:** Удалить файл презентации.
* **Переменная пути:** `fileName`.
* **Ответ:** `200 OK` (`application/json` `{ "message": "..." }`), `500 Internal Server Error`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`DELETE /api/v1/files/presentation/clear-bucket`**
* **Описание:** Удалить ВСЕ файлы презентаций из бакета `presentation`.
* **Ответ:** `200 OK` (`application/json` `{ "message": "..." }`), `500 Internal Server Error`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.

##### Технические задания (`technicalSpecifications`)

* **`GET /api/v1/files/technicalSpecifications/{fileName}`**
* **Описание:** Скачать файл технического задания.
* **Переменная пути:** `fileName`.
* **Ответ:** `200 OK` (`application/octet-stream`), `404 Not Found`.
* **Безопасность:** ⚠️ Публичный (неявно). *Рекомендуется защитить.* Требуется Bearer токен.
* **`POST /api/v1/files/technicalSpecifications`**
* **Описание:** Загрузить файл технического задания для проекта.
* **Запрос:** `multipart/form-data` (поля `file`, `projectId`). DTO: `FilesToProjectUploadDto`.
* **Ответ (Успех):** `200 OK` (`application/json` `{ "message": "...", "fileName": "..." }`).
* **Ответ (Ошибка):** `400 Bad Request`, `500 Internal Server Error`.
* **Безопасность:** `ROLE_ADMIN` или `ROLE_COMPANY`. Требуется Bearer токен.
* **`DELETE /api/v1/files/technicalSpecifications/{fileName}`**
* **Описание:** Удалить файл технического задания.
* **Переменная пути:** `fileName`.
* **Ответ:** `200 OK` (`application/json` `{ "message": "..." }`), `500 Internal Server Error`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`DELETE /api/v1/files/technicalSpecifications/clear-bucket`**
* **Описание:** Удалить ВСЕ файлы тех. заданий из бакета `technical-specifications`.
* **Ответ:** `200 OK` (`application/json` `{ "message": "..." }`), `500 Internal Server Error`.
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.

#### Экспорт (`/export/*`)

* **`GET /export/students/file`**
* **Описание:** Экспорт всех студентов в Excel (`.xlsx`).
* **Ответ:** `200 OK` (`application/octet-stream`).
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.
* **`GET /export/students`**
* **Описание:** Экспорт всех студентов в JSON.
* **Ответ:** `200 OK` (`application/json` `List<StudentExportDto>`).
* **Безопасность:** `ROLE_ADMIN`. Требуется Bearer токен.

#### Проверка reCAPTCHA

* **`POST /verify-recaptcha`**
* **Описание:** Проверка токена Google reCAPTCHA v2.
* **Тело запроса:** `application/json`
  ```json
  { "token": "recaptcha_response_token" }
  ```
* **Ответ (Успех):** `200 OK` (`application/json` `{ "message": "Success" }`).
* **Ответ (Ошибка):** `405 Method Not Allowed`, `500 Internal Server Error`.
* **Безопасность:** Публичный.

---

## 5. 🔄 Асинхронные операции (Kafka)

* **Топик:** `company-password-email`
* **Событие:** `PasswordEvent` (`{ email, password, firstApprove }`)
* **Продюсер:** `CompanyService` (методы `approveCompany`, `changePassword`)
* **Потребитель:** `EmailService` (метод `sendCompanyPassword`)
* **Назначение:** Отправка email компании с паролем (начальным или новым) с использованием шаблонов `approval-email` или
  `password-update-email`.
* **Топик:** `student-project-id`
* **Событие:** `StudentProjectCreationEvent` (`{ email, projectId }`)
* **Продюсер:** `ProjectService` (метод `create`, если `isStudentProject=true`)
* **Потребитель:** `EmailService` (метод `sendStudentProject`)
* **Назначение:** Отправка email создателю проекта (контактному лицу компании) с ID созданного студенческого проекта,
  используя шаблон `project-id-email`.

---

## 6. 💾 Детали обработки файлов

* **Валидация:** `FileValidateService` проверяет:
* Файл не пустой.
* Тип контента (`application/pdf`, `...wordprocessingml.document`, `...presentationml.presentation` и т.д.).
* Расширение файла (`.pdf`, `.ppt`, `.pptx`, `.doc`, `.docx`).
* **Хранение:** `MinIO` с бакетами:
* `resume`
* `presentation`
* `technical-specifications`
* **Именование:**
* Резюме: `{studentId}_{firstName}_{lastName}_{creationDate}.{extension}` (например, `123_Иван_Иванов_2024-01-15.pdf`)
* Презентации / ТЗ: Исходное имя файла с заменой пробелов на `_` (например, `Презентация_проекта_Альфа.pptx`).
* **Связь с БД:** Имя файла сохраняется в полях `resumePdf` (`Student`), `presentation` (`Project`),
  `technicalSpecifications` (`Project`).

---

## 7. 📊 Миграции базы данных

`Flyway` используется для управления версиями схемы БД. Скрипты миграций находятся в `src/main/resources/db/migration` и
должны следовать формату `V<ВЕРСИЯ>__<Описание>.sql` (например, `V1__Initial_schema.sql`).
