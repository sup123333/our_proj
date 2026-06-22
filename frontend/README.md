# Лендинг таролога — инструкция

## Папка img/ — положи сюда три картинки

```
frontend/
  index.html
  img/
    hero.png   ← первая картинка (тёмная с картами, баннер)
    about.png  ← тоже подойдёт первая (блок "обо мне")
    card2.png  ← вторая картинка (светлая иллюстрация)
    card3.png  ← третья картинка (на бежевом фоне)
```

## Что поменять перед запуском

- Цены и состав услуг **не редактируются в HTML** — они подгружаются с backend через
  `GET /api/services`. Чтобы поменять услугу или цену, меняй данные в БД (через
  `POST/PUT /api/admin/services` под аккаунтом таролога) — лендинг подхватит изменения сам.
- Отзывы (`.review-card`) и текст "Обо мне" — реальный текст, заменить вручную.
- Контакты в footer — реальные ссылки Telegram/Instagram.

## Как работает форма записи

Форма — это **публичная заявка (lead)**, без регистрации и логина:

```javascript
await fetch('/api/leads', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    name: '...',
    contact: '...',       // телефон или telegram
    serviceId: 3,          // id выбранной услуги или null
    question: '...'
  })
});
```

Заявка сохраняется в БД как `Lead` со статусом `NEW`. Полноценный `Client`/`Session`
с баллами и историей создаётся **не автоматически** — таролог видит заявку в админке
(`GET /api/admin/leads`), связывается с человеком и дальше работает с ним как с обычным
клиентом (регистрация → запись на сеанс → оплата → баллы).

Так разделены два разных юзкейса:
- **анонимный посетитель лендинга** — просто оставляет контакт, ничего не должен регистрировать;
- **зарегистрированный клиент** — копит баллы, видит историю сеансов в личном кабинете
  (это уже отдельный, авторизованный флоу через `/api/auth/register` + `/api/auth/login`).

## Деплой на Beget

1. Загрузи файлы `frontend/` в папку `public_html` через FTP или файловый менеджер
2. Папку `img/` с картинками туда же
3. Spring Boot JAR запускается отдельно через SSH: `java -jar tarot-backend.jar`
4. Nginx проксирует `/api/*` на `localhost:8080`

## Nginx конфиг (минимальный)

```nginx
server {
    listen 80;
    server_name your-domain.ru;

    root /var/www/public_html;
    index index.html;

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

`X-Forwarded-For` важен: backend читает его в `RateLimitFilter`, чтобы считать лимит
запросов по реальному IP посетителя, а не по адресу nginx.
