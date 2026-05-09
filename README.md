# Проектная практика — Кейс № 3. Лотерейная система (Сценарий 3)

В рамках сценария 3 был реализован бэкенд для лотерейного сервиса с историей тиражей, билетов, отчётов в JSON/CSV, несколькими типами лотерей и email-уведомлениями после розыгрыша.

Помимо основной задачи, с учётом наличия в команде специалистов по фронтенду был также реализован веб-сайт лотереи, на котором можно в удобном формате опробовать весь функционал, требуемый в задании.

Сайт доступен по адресу: [http://37.18.102.122](http://37.18.102.122)

## Команда № 23

**Александр** — тимлид (организационные вопросы и взаимодействие с организаторами), просмотр и фильтрация тиражей, презентация и речь для защиты
Реализовал получение списка всех тиражей с фильтром по статусу и историю завершённых тиражей. 
Эндпоинты: `GET /draws`, `GET /draws/completed`.

**Влад Ч.** — техлид (архитектура сервиса, правки других участников, сборка проекта), система управления тиражами (административная часть).
Работал над реализацией создания тиражей, запуска, завершения с определением победителей и отменой с автоматическим переводом всех билетов в LOSE.
Эндпоинты: `POST /draws`, `POST /draws/{id}/start`, `POST /draws/{id}/finish`, `POST /draws/{id}/cancel`.

**Влад В.** — DevOps и инфраструктура.
Настроил окружение для запуска проекта: написал `Dockerfile` и `docker-compose.dev.yml`, поднял PostgreSQL в контейнере, настроил переменные окружения. Весь стек запускается одной командой.
Дополнительно разработал и задеплоил React SPA на живом сервере, интегрированный со всеми группами API.
Кроме того, принял участие в работе над реализацией создания тиражей, запуска, завершения с определением победителей и отменой с автоматическим переводом всех билетов в LOSE.
Эндпоинты: `POST /draws`, `POST /draws/{id}/start`, `POST /draws/{id}/finish`, `POST /draws/{id}/cancel`.

**Дмитрий** — покупка билетов.
Реализовал получение списка активных тиражей и покупку билета пользователем (числа генерируются на сервере случайно).
Эндпоинты: `GET /draws/active`, `POST /draws/{id}/tickets`.

**Роман** — полностью сделал весь фронтенд.
Реализовал страницу лотереи, её оформление и UX/UI-дизайн, а также удобную и наглядную реализацию всех требуемых в задании функций.

**Юлия** — аутентификация и управление доступом.
Реализовала регистрацию и вход пользователей, выдачу JWT-токена, разделение на роли USER и ADMIN.
Эндпоинты: `POST /auth/register`, `POST /auth/login`.

**Юрий** — история билетов и отчёты.
Реализовал просмотр истории своих билетов с постраничной выдачей, проверку результата конкретного билета, а также выгрузку отчётов по тиражам и билетам в форматах JSON и CSV.
Эндпоинты: `GET /tickets`, `GET /tickets/{id}/result`, `GET /reports/draws/{id}`, `GET /reports/tickets`.


## Используемый технологический стек

- **Java 21**
- **Javalin 6** — лёгкий HTTP-фреймворк (использован из-за ограничений на Spring)
- **PostgreSQL 17** + **HikariCP** — база данных с пулом соединений
- **Flyway** — автоматическое обновление структуры БД при внесении изменений
- **Google Guice** — управление зависимостями между компонентами
- **JWT (jjwt)** — авторизация по токенам
- **Jakarta Mail** — email-уведомления
- **Docker / Docker Compose** — запуск окружения

## Что реализовано в рамках проделанной работы

Обязательные требования по заданию (сценарий 3):
- создание и управление тиражами (ADMIN)
- покупка билетов (USER): вызов эндпоинта фиксирует покупку без реального платёжного шлюза, числа генерируются случайно на сервере
- определение результата — WIN/LOSE после завершения тиража
- история завершённых тиражей
- история билетов пользователя с постраничной выдачей
- отчёты по тиражу и по билетам в JSON и CSV

Дополнительно:
- история действий пользователя (лог, запись в фоне AuditService)
- отмена тиража — все билеты переходят в LOSE
- email-уведомления участникам при завершении/отмене тиража
- три типа лотерей: CLASSIC, MEGA (с бонусным шаром), KENO
- создание новых типов лотерей через API и сайт

## Архитектура
```
    React SPA  →  Controller  →  Service  →  Repository  →  PostgreSQL (слоем приложения не является)
   API        →
```

Клиент:
- **React SPA** — клиент фронтенд, живёт отдельно на сервере, общается с бэкендом через REST API.
- **API** — прямой доступ для сторонних интеграций.

Бэкенд построен по принципу многослойности (Layered Architecture): Три слоя, разделены через интерфейсы:
- **Controller** — принимает HTTP-запрос, проверяет токен, достаёт параметры, отдаёт ответ
- **Service** — вся бизнес-логика: генерация комбинаций, смена статусов, аудит, уведомления
- **Repository** — SQL-запросы к базе данных, преобразование результатов в объекты

- **PostgreSQL** — база данных, в которой хранятся все данные сервиса *(не слой приложения — внешнее хранилище, с которым напрямую работает только Repository)*

Все зависимости между компонентами подключаются через Guice.

Аудит работает в фоне: события складываются во внутреннюю очередь (Event Bus паттерн), отдельный фоновый поток записывает их в БД — основной поток не блокируется.

Email-уведомления тоже отправляются в фоне — ответ клиенту уходит сразу, не дожидаясь почтового сервера.

## Модель данных (БД)

Пять таблиц:

- `users` — пользователи, пароль хранится в зашифрованном виде
- `lottery_types` — справочник типов лотерей (CLASSIC, MEGA, KENO и т.д.)
- `draws` — тиражи, статусы: `DRAFT → ACTIVE → FINISHED / CANCELLED`
- `tickets` — билеты пользователей, статусы: `PENDING → WIN / LOSE`
- `user_actions` — лог действий, поле `details` хранит дополнительный контекст в JSON

Связи: `draws` ссылается на `lottery_types`, `tickets` ссылается на `draws` и `users`, `user_actions` ссылается на `users`.

Два индекса: один ускоряет фильтрацию тиражей по типу лотереи, второй проверяет уникальность комбинации чисел при покупке билета.

### Жизненный цикл тиража

```
DRAFT  →  ACTIVE  →  FINISHED
                 ↘  CANCELLED
```

Из DRAFT можно перейти только в ACTIVE или сразу отменить. Из FINISHED и CANCELLED переходов нет. При отмене все билеты тиража автоматически получают статус LOSE.

## Запуск сервиса

### Через Docker (проще всего)

Docker запустит PostgreSQL и применит миграции:

```bash
docker compose -f docker-compose.dev.yml up -d
```
Приложение доступно на` http://localhost:8080`

При первом запуске Flyway создаст схему и заполнит тестовые данные автоматически.

### Локально без Docker

Нужны Java 21 и PostgreSQL. Создайте БД:
```sql
CREATE DATABASE lottery;
```

Задайте переменные окружения (или оставьте стандартные — они уже указывают на localhost):
```bash
export DB_URL="jdbc:postgresql://localhost:5432/lottery"
export DB_USER="postgres"
export DB_PASSWORD="postgres"
```

Запуск:
```bash
./gradlew run
```

### Переменные окружения

| Переменная | Значение по умолчанию |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/lottery` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |
| `JWT_SECRET` | `dev-secret-key-must-be-long-enough-32chars` |
| `SMTP_HOST` | `smtp.gmail.com` |
| `SMTP_PORT` | `587` |
| `SMTP_USER` / `SMTP_PASSWORD` | не заданы |

JWT_SECRET при развёртывании на сервере нужно заменить на своё значение длиной не менее 32 символов. Если SMTP не настроен — уведомления не отправляются, но ничего не сломается.

## Тестовые данные

После запуска в БД уже есть:

**Пользователи:**

| Email | Пароль | Роль |
|---|---|---|
| admin@lottery.com | password | ADMIN |
| user@lottery.com | password | USER |

**Типы лотерей:** CLASSIC (5 из 50), MEGA (6 из 60 + бонус 1–10), KENO (10 из 80)

**Тиражи:**
- #1 Новогодний розыгрыш 2025 — FINISHED, есть выигрышный билет
- #2 Весенний мега-тираж — ACTIVE, можно покупать билеты
- #3 Летнее кено — DRAFT
- #4 Осенний кубок — CANCELLED
- #5 Зимний тираж — FINISHED, без победителей
- #6 Счастливый четверг — ACTIVE

## API

Полная спецификация в файле `openapi.yaml` (находится в корне репозитория) — можно открыть на [editor.swagger.io](https://editor.swagger.io), загрузив файл через кнопку «File → Import file».

Все запросы кроме `/auth/*` требуют заголовок:
```
Authorization: Bearer <токен>
```

### Основные эндпоинты

```
POST /auth/register          — регистрация
POST /auth/login             — вход, возвращает JWT

GET  /draws/active           — активные тиражи
GET  /draws/completed        — завершённые тиражи
GET  /draws/{id}             — конкретный тираж
GET  /draws/filter?status=   — все тиражи с фильтром (ADMIN)

POST /draws                  — создать тираж (ADMIN)
POST /draws/{id}/start       — запустить: DRAFT → ACTIVE (ADMIN)
POST /draws/{id}/finish      — завершить, определить победителей (ADMIN)
POST /draws/{id}/cancel      — отменить тираж (ADMIN)

POST /draws/{id}/tickets     — купить билет
GET  /tickets                — история своих билетов
GET  /tickets/{id}/result    — результат билета (WIN/LOSE/PENDING)

GET  /reports/draws/{id}?format=json|csv   — отчёт по тиражу (ADMIN)
GET  /reports/tickets?format=json|csv      — отчёт по своим билетам

GET  /lottery-types          — список типов лотерей
POST /lottery-types          — создать тип лотереи (ADMIN)

GET  /users/{id}/history     — история действий пользователя
```

## Проверка через curl

Для проверки через командную строку есть два варианта — локальный запуск или обращение к живому серверу.

### Вариант 1: локальный запуск (localhost)

> Предварительно запустите сервис по инструкции из раздела «Запуск сервиса».

**Получить токены:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@lottery.com","password":"password"}'

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@lottery.com","password":"password"}'
```

> Ответ вернёт JSON вида `{"token": "eyJ..."}`. Скопируйте значение поля `token` и используйте его вместо `<USER_TOKEN>` или `<ADMIN_TOKEN>` в командах ниже.

**Создать и запустить новый тираж (от ADMIN):**
```bash
curl -X POST http://localhost:8080/draws \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Тестовый тираж","lotteryTypeName":"CLASSIC"}'

# Взять id из ответа, например 7
curl -X POST http://localhost:8080/draws/7/start \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**Купить билет и посмотреть историю:**
```bash
curl -X POST http://localhost:8080/draws/7/tickets \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -d '{}'

curl http://localhost:8080/tickets \
  -H "Authorization: Bearer <USER_TOKEN>"
```

**Завершить тираж и проверить результат билета:**
```bash
curl -X POST http://localhost:8080/draws/7/finish \
  -H "Authorization: Bearer <ADMIN_TOKEN>"

# Взять ticket id из истории билетов
curl http://localhost:8080/tickets/3/result \
  -H "Authorization: Bearer <USER_TOKEN>"
```

**Скачать отчёт по тиражу в CSV:**
```bash
curl "http://localhost:8080/reports/draws/7?format=csv" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**Посмотреть историю действий пользователя (user id = 2):**
```bash
curl http://localhost:8080/users/2/history \
  -H "Authorization: Bearer <USER_TOKEN>"
```

### Вариант 2: живой сервер (без локальной установки)

> Ничего устанавливать не нужно — фронтенд и бэкенд задеплоены на одном сервере: фронтенд доступен на порту 80 ([http://37.18.102.122](http://37.18.102.122)), бэкенд API — на порту 8080 (`37.18.102.122:8080`).

**Получить токены:**
```bash
curl -X POST http://37.18.102.122:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@lottery.com","password":"password"}'

curl -X POST http://37.18.102.122:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@lottery.com","password":"password"}'
```

> Ответ вернёт JSON вида `{"token": "eyJ..."}`. Скопируйте значение поля `token` и используйте его вместо `<USER_TOKEN>` или `<ADMIN_TOKEN>` в командах ниже.

**Создать и запустить новый тираж (от ADMIN):**
```bash
curl -X POST http://37.18.102.122:8080/draws \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Тестовый тираж","lotteryTypeName":"CLASSIC"}'

# Взять id из ответа, например 7
curl -X POST http://37.18.102.122:8080/draws/7/start \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**Купить билет и посмотреть историю:**
```bash
curl -X POST http://37.18.102.122:8080/draws/7/tickets \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -d '{}'

curl http://37.18.102.122:8080/tickets \
  -H "Authorization: Bearer <USER_TOKEN>"
```

**Завершить тираж и проверить результат билета:**
```bash
curl -X POST http://37.18.102.122:8080/draws/7/finish \
  -H "Authorization: Bearer <ADMIN_TOKEN>"

# Взять ticket id из истории билетов
curl http://37.18.102.122:8080/tickets/3/result \
  -H "Authorization: Bearer <USER_TOKEN>"
```

**Скачать отчёт по тиражу в CSV:**
```bash
curl "http://37.18.102.122:8080/reports/draws/7?format=csv" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**Посмотреть историю действий пользователя (user id = 2):**
```bash
curl http://37.18.102.122:8080/users/2/history \
  -H "Authorization: Bearer <USER_TOKEN>"
```

---

## Фронтенд

Дополнительно к бэкенду сделан React SPA, задеплоен на живом сервере. Это не обязательная часть задания, но полностью интегрирован со всеми API-группами бэкенда.

**Технологический стек:** React 18, Redux Toolkit, Ant Design, React Router 6, Axios, Vite. Шрифт Euclid Circular A. После входа токен сохраняется в памяти приложения и автоматически добавляется к каждому запросу.

### Как попробовать

Приложение доступно по адресу: [http://37.18.102.122](http://37.18.102.122)

Тестовые учётки те же, что и для API:

| Email | Пароль | Роль |
|---|---|---|
| admin@lottery.com | password | ADMIN |
| user@lottery.com | password | USER |

### Страницы

**Панель пользователя** (после входа как USER):
- `/user/draws` — список активных тиражей, кнопка покупки билета
- `/user/tickets` — история своих билетов, сразу видно WIN или LOSE
- `/user/history` — лог всех действий в хронологии

**Панель администратора** (после входа как ADMIN):
- `/admin/draws` — создание тиражей, запуск, завершение, отмена
- `/admin/lottery-types` — просмотр и добавление типов лотерей
- `/admin/reports` — выгрузка отчётов по тиражам в JSON или CSV

### Быстрый сценарий для проверки

1. Открыть `http://37.18.102.122`, войти как **admin@lottery.com**
2. На странице `/admin/draws` создать новый тираж, нажать «Запустить»
3. Выйти, войти как **user@lottery.com**
4. На странице `/user/draws` купить билет в только что созданный тираж
5. Выйти, войти как **admin** → на странице `/admin/draws` нажать кнопку «Завершить»
6. Снова войти как **user** → в `/user/tickets` появится результат WIN или LOSE
