Вот профессионально оформленный файл README.md для вашего репозитория. Он написан так, чтобы любой технический специалист (или преподаватель) сразу оценил чистоту архитектуры и серьезность подхода.

🔨 Auction Platform with Auto-Bidding Module

Современная веб-платформа для проведения онлайн-аукционов, разработанная в рамках курсового проекта. Система поддерживает автоматическое повышение ставок (прокси-биддинг), мгновенные уведомления через WebSockets и интеграцию с внешними сервисами.

![alt text](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)


![alt text](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=springboot)


![alt text](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)


![alt text](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)

🌟 Ключевые особенности

Proxy Bidding (Авто-биддер): Пользователи могут устанавливать скрытый лимит. Система автоматически перебивает ставки конкурентов минимально возможным шагом.

Real-time Updates: Мгновенное обновление текущей цены и таймера без перезагрузки страницы (STOMP over WebSockets).

RBAC Security: Разграничение ролей (Покупатель, Продавец, Администратор) на уровне Бэкенда и Фронтенда.

Hybrid Auth: Вход по логину/паролю или через Google OAuth 2.0.

Reports: Генерация официальных PDF-отчетов по результатам закрытых торгов.

Architecture: Соблюдение принципов Clean Architecture и использование паттернов проектирования (Strategy, Observer, Factory).

🛠 Технологический стек
Backend

Core: Java 21 LTS, Spring Boot 3.2

Security: Spring Security, JWT (Access + Refresh tokens), Google OAuth2

Data: Spring Data JPA (Hibernate 6.4), MySQL 8.0

Migrations: Flyway

Cache: Redis

API: RESTful, SpringDoc OpenAPI 3 (Swagger)

Testing: JUnit 5, Mockito, Testcontainers

Frontend

Core: React 18, TypeScript, Vite

State Management: Zustand

Styles: Tailwind CSS

Icons: Lucide React

Communication: Axios, @stomp/stompjs (WebSockets)

🏗 Архитектура базы данных

Схема приведена к 3-й нормальной форме (3НФ) и включает 9 связанных таблиц:

users, roles, categories, lots, bids, auto_bids, notifications, orders, oauth2_links.

🚀 Быстрый запуск (Docker)

Проект полностью контейнеризирован. Для запуска всей инфраструктуры (Frontend + Backend + DB + Redis) выполните одну команду в корневой папке:

code
Bash
download
content_copy
expand_less
docker-compose up --build -d

Приложение будет доступно по адресам:

Frontend: http://localhost:3000

Backend API: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui/index.html

Тестовые учетные данные:
Роль	Email	Пароль
Администратор	admin@test.com	admin
Продавец	seller@test.com	seller
Покупатель	user@test.com	1111
🧪 Тестирование

Для запуска тестов (включая интеграционные тесты в изолированных контейнерах MySQL):

code
Bash
download
content_copy
expand_less
cd backend
./mvnw test

Для генерации отчета о покрытии кода (JaCoCo):

code
Bash
download
content_copy
expand_less
./mvnw clean test jacoco:report

Отчет будет доступен в: target/site/jacoco/index.html (Требуемое покрытие > 40%).

🔌 Интеграции

Google Identity API: OAuth 2.0 авторизация.

Exchange Rates API: Получение актуального курса USD/EUR для динамической конвертации стоимости лотов.
