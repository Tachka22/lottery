# Тестирование Lottery API (Сценарий 3)

В данном руководстве приведены примеры `curl` запросов для тестирования API лотереи.
Тесты разделены на роли: **Администратор** (управление тиражами и типами) и **Пользователь** (покупка билетов и просмотр результатов).

> **Предварительные требования:**
> 1. Запустите приложение на `http://*:8080`.
> 2. Замените токены в примерах на реальные значения, полученные после входа.
> 3. Заменяйте `{id}` на реальные идентификаторы, полученные из ответов предыдущих запросов.

---

## 1. Администратор

### 1.1. Вход Администратора

```bash
# Вход (получение JWT токена)
curl -X 'POST' \
  'http://localhost:8080/auth/login' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "password": "admin",
  "email": "admin@lottery.com"
}'
```
### 1.2. Получение всех типов лотерей
```bash
# Получение всех типов лотерей
curl -X 'GET' \
  'http://localhost:8080/lottery-types' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}'
```

### 1.3. Создание лотереи нового типа

```bash
# Создание лотереи нового типа
curl -X 'POST' \
  'http://localhost:8080/lottery-types' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "NEWNEWNEW",
  "numbersCount": 5,
  "minNumber": 1,
  "maxNumber": 10,
  "hasBonus": false,
  "bonusMin": 0,
  "bonusMax": 0,
  "description": "string"
}'
```

### 1.4. Получить все тиражи

```bash
curl -X 'GET' \
  'http://localhost:8080/draws' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}'
```

### 1.5. Создать новый тираж

```bash
curl -X 'POST' \
  'http://localhost:8080/draws' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Новый тираж",
  "lotteryTypeName": "MEGA",
  "description": "Новый тираж"
}'
```

### 1.6. Запустить тираж
```bash
# id - идентификатор тиража который надо активировать
curl -X 'POST' \
  'http://localhost:8080/draws/{id}/start' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}' \
  -d ''
```

### 1.7. Завершить тираж, сгенерировать комбинацию
```bash
# id - идентификатор тиража который надо завершить
curl -X 'POST' \
  'http://localhost:8080/draws/{id}/finish' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}' \
  -d ''
```
В ответ выигранная комбинация
```
{"winningNumbers":"34,38,41,45,48,52","winningBonus":4}%  
```

### 1.7. Отменить тираж, никто не выиграл
```bash
# id - идентификатор тиража который надо отменить
curl -X 'POST' \
  'http://localhost:8080/draws/{id}/cancel' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}' \
  -d ''
```


### 1.8. Получить все завершённые тиражи

```bash
curl -X 'GET' \
  'http://localhost:8080/draws/completed?limit=20&offset=0' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}'
```

### 1.9. Получить отчет по всем тиражам

```bash
# id - номер тиража
curl -X 'GET' \
  'http://localhost:8080/reports/draws/{id}?format=json' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {adminJwtToken}'
```


## 2. Пользователь
### 2.1 Получить все тиражи
```bash
curl -X 'GET' \
  'http://localhost:8080/draws' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {token}'
```

### 2.2 Купить билет
```bash
curl -X 'POST' \
# id - номер тиража
  'http://localhost:8080/draws/{id}/tickets' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {token}' \
  -H 'Content-Type: application/json' \
  -d '{}'
```

```
# Пример ответа
{"id":8,"drawId":6,"userId":3,"numbers":"4,5,17,32,37","bonus":null,"status":"PENDING","createdAt":null}% 
```

### 2.3 История билетов пользователя с пагинацией

```bash
curl -X 'GET' \
  'http://localhost:8080/tickets?limit=20&offset=0' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {token}'
```

### 2.4 Получить результат билета
```bash
# id - номер билета
curl -X 'GET' \
  'http://localhost:8080/tickets/{id}/result' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {token}'
```

### 2.4.1 Получить отчет по билетам пользователя в json
```bash
curl -X 'GET' \
  'http://localhost:8080/reports/tickets?format=json' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {token}'
```

### 2.4.2 Получить отчет по билетам пользователя в csv
```bash
curl -X 'GET' \
  'http://localhost:8080/reports/tickets?format=csv' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {token}'
```

### 2.5 Получить все активные тиражи

```bash
curl -X 'GET' \
  'http://localhost:8080/draws/active?limit=20&offset=0' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {token}'
```
### 2.5 Получить историю пользователя (доступно пользователю владельцу или администратору)

```bash
curl -X 'GET' \
# id - пользователь
  'http://localhost:8080/users/{2}/history' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer {token}'
```