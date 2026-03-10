-- Таблица пользователей
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255),
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       role VARCHAR(50) NOT NULL, -- ROLE_BUYER, ROLE_SELLER, ROLE_ADMIN
                       status VARCHAR(50) NOT NULL, -- ACTIVE, BANNED
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для OAuth2 интеграции (Google, GitHub)
CREATE TABLE oauth2_links (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              provider VARCHAR(50) NOT NULL, -- google, github
                              provider_id VARCHAR(255) NOT NULL,
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              UNIQUE(provider, provider_id)
);

-- Таблица категорий товаров
CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Таблица лотов
CREATE TABLE lots (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      seller_id BIGINT NOT NULL,
                      category_id BIGINT NOT NULL,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      start_price DECIMAL(15, 2) NOT NULL,
                      current_price DECIMAL(15, 2) NOT NULL,
                      bid_step DECIMAL(15, 2) NOT NULL,
                      status VARCHAR(50) NOT NULL, -- ACTIVE, COMPLETED, CANCELLED
                      end_time DATETIME NOT NULL,
                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                      version INT DEFAULT 0, -- Для Optimistic Locking (защита от гонки при ставках)
                      FOREIGN KEY (seller_id) REFERENCES users(id),
                      FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Таблица ручных ставок
CREATE TABLE bids (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      lot_id BIGINT NOT NULL,
                      user_id BIGINT NOT NULL,
                      amount DECIMAL(15, 2) NOT NULL,
                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (lot_id) REFERENCES lots(id) ON DELETE CASCADE,
                      FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Таблица лимитов авто-биддера
CREATE TABLE auto_bids (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           lot_id BIGINT NOT NULL,
                           user_id BIGINT NOT NULL,
                           max_limit DECIMAL(15, 2) NOT NULL,
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (lot_id) REFERENCES lots(id) ON DELETE CASCADE,
                           FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Таблица заказов (ордеров) по завершенным аукционам
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        lot_id BIGINT NOT NULL UNIQUE,
                        buyer_id BIGINT NOT NULL,
                        amount DECIMAL(15, 2) NOT NULL,
                        status VARCHAR(50) NOT NULL, -- PENDING_PAYMENT, PAID, CANCELLED
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (lot_id) REFERENCES lots(id),
                        FOREIGN KEY (buyer_id) REFERENCES users(id)
);

-- Таблица уведомлений
CREATE TABLE notifications (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               message TEXT NOT NULL,
                               type VARCHAR(50) NOT NULL, -- OUTBID, WON, FINISHED
                               is_read BOOLEAN DEFAULT FALSE,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);