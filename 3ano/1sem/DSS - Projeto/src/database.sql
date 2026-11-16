CREATE USER IF NOT EXISTS 'username' @localhost IDENTIFIED BY 'password';

GRANT ALL PRIVILEGES ON *.* TO 'username' @localhost;

DROP DATABASE IF EXISTS dss;

CREATE DATABASE dss;

USE dss;

-- Schema ----------------------------------------------------------------------

CREATE TABLE restaurants (
    restaurant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(255),
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE workstation_types (
    workstation_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE workstations (
    workstation_id INT AUTO_INCREMENT PRIMARY KEY,
    workstation_type_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id),
    FOREIGN KEY (workstation_type_id) REFERENCES workstation_types (workstation_type_id)
);

-- Nova tabela para balcoes de levantamento
CREATE TABLE counters (
    counter_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    counter_number INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id),
    UNIQUE KEY unique_counter (restaurant_id, counter_number)
);

CREATE TABLE ingredients (
    ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    allergen_type VARCHAR(50),
    unit_type ENUM('GRAMS', 'PIECES', 'ML') NOT NULL,
    workstation_type_id INT,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (workstation_type_id) REFERENCES workstation_types (workstation_type_id)
);

CREATE TABLE menu_items (
    menu_item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    base_price DECIMAL(10, 2) NOT NULL,
    category ENUM(
        'MAIN',
        'SIDE',
        'DRINK',
        'DESSERT',
        'MENU'
    ) NOT NULL,
    is_menu BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE menu_item_ingredients (
    menu_item_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    is_optional BOOLEAN DEFAULT FALSE,
    is_customizable BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (menu_item_id, ingredient_id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items (menu_item_id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id) ON DELETE CASCADE
);

CREATE TABLE menu_item_alternatives (
    alternative_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_item_id INT NOT NULL,
    original_ingredient_id INT NOT NULL,
    alternative_ingredient_id INT NOT NULL,
    price_modifier DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items (menu_item_id) ON DELETE CASCADE,
    FOREIGN KEY (original_ingredient_id) REFERENCES ingredients (ingredient_id) ON DELETE CASCADE,
    FOREIGN KEY (alternative_ingredient_id) REFERENCES ingredients (ingredient_id) ON DELETE CASCADE,
    UNIQUE KEY unique_alternative (
        menu_item_id,
        original_ingredient_id,
        alternative_ingredient_id
    )
);

CREATE TABLE kiosks (
    kiosk_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    status ENUM(
        'ACTIVE',
        'INACTIVE',
        'ON_LEAVE'
    ) DEFAULT 'ACTIVE',
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id) ON DELETE CASCADE
);

-- Adicionado counter_id para balcao de levantamento (NULL para takeaway)
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(20) NOT NULL UNIQUE,
    kiosk_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    counter_id INT DEFAULT NULL,
    order_datetime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    order_type ENUM('DINE_IN', 'TAKEAWAY') NOT NULL,
    status ENUM(
        'PENDING',
        'IN_PROGRESS',
        'READY',
        'COMPLETED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING',
    total_price DECIMAL(10, 2) NOT NULL,
    order_notes TEXT DEFAULT NULL,
    payment_method ENUM('MBWAY', 'CASH', 'CARD') NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'FAILED') NOT NULL DEFAULT 'PENDING',
    estimated_completion_time DATETIME,
    actual_completion_time DATETIME,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id),
    FOREIGN KEY (kiosk_id) REFERENCES kiosks (kiosk_id),
    FOREIGN KEY (counter_id) REFERENCES counters (counter_id)
);

CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    special_instructions TEXT,
    FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items (menu_item_id) ON DELETE CASCADE
);

CREATE TABLE order_item_customizations (
    customization_id INT AUTO_INCREMENT PRIMARY KEY,
    order_item_id INT NOT NULL,
    customization_type ENUM(
        'REMOVE_INGREDIENT',
        'ADD_INGREDIENT',
        'SUBSTITUTE_INGREDIENT',
        'COOKING_METHOD'
    ) NOT NULL,
    ingredient_id INT,
    alternative_ingredient_id INT,
    cooking_instruction VARCHAR(100),
    FOREIGN KEY (order_item_id) REFERENCES order_items (order_item_id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id) ON DELETE CASCADE,
    FOREIGN KEY (alternative_ingredient_id) REFERENCES ingredients (ingredient_id) ON DELETE CASCADE
);

CREATE TABLE displays (
    display_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NULL,
    workstation_id INT NULL,
    CHECK (
        (
            restaurant_id IS NOT NULL
            AND workstation_id IS NULL
        )
        OR (
            restaurant_id IS NULL
            AND workstation_id IS NOT NULL
        )
    ),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id),
    FOREIGN KEY (workstation_id) REFERENCES workstations (workstation_id)
);

CREATE TABLE kitchen_tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    order_item_id INT NOT NULL,
    workstation_id INT NOT NULL,
    status ENUM(
        'PENDING',
        'IN_PROGRESS',
        'WAITING',
        'COMPLETED',
        'DELAYED'
    ) NOT NULL DEFAULT 'PENDING',
    priority INT DEFAULT 0,
    estimated_duration INT,
    scheduled_start_time DATETIME,
    actual_start_time DATETIME,
    actual_completion_time DATETIME,
    special_notes TEXT,
    FOREIGN KEY (order_item_id) REFERENCES order_items (order_item_id) ON DELETE CASCADE,
    FOREIGN KEY (workstation_id) REFERENCES workstations (workstation_id) ON DELETE CASCADE
);

CREATE TABLE task_dependencies (
    task_id INT NOT NULL,
    depends_on_task_id INT NOT NULL,
    PRIMARY KEY (task_id, depends_on_task_id),
    FOREIGN KEY (task_id) REFERENCES kitchen_tasks (task_id) ON DELETE CASCADE,
    FOREIGN KEY (depends_on_task_id) REFERENCES kitchen_tasks (task_id) ON DELETE CASCADE
);

CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    restaurant_id INT NOT NULL,
    role ENUM(
        'STATION_WORKER',
        'DELIVERY',
        'MANAGER',
        'COO'
    ) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id)
);

CREATE TABLE employee_authentication (
    employee_id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE
);

CREATE TABLE employee_workstation_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    workstation_id INT NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE,
    FOREIGN KEY (workstation_id) REFERENCES workstations (workstation_id) ON DELETE CASCADE
);

CREATE TABLE employee_messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_employee_id INT NOT NULL,
    message_text TEXT NOT NULL,
    receiver_display INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_display) REFERENCES displays (display_id) ON DELETE SET NULL
);

CREATE TABLE inventory_items (
    restaurant_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantity_available DECIMAL(10, 2) NOT NULL DEFAULT 0,
    reorder_threshold DECIMAL(10, 2) NOT NULL DEFAULT 0,
    warehouse_quantity DECIMAL(10, 2) NOT NULL DEFAULT 0,
    last_updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (restaurant_id, ingredient_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (restaurant_id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id) ON DELETE CASCADE
);

CREATE TABLE inventory_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    transaction_type ENUM(
        'USAGE',
        'RESTOCK',
        'WASTE',
        'WAREHOUSE_TO_KITCHEN'
    ) NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    transaction_datetime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    employee_id INT,
    FOREIGN KEY (restaurant_id, ingredient_id) REFERENCES inventory_items (restaurant_id, ingredient_id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE SET NULL
);

-- Initial data -----------------------------------------------------------------

-- 1. Restaurantes
INSERT INTO
    restaurants (
        name,
        location,
        phone,
        is_active
    )
VALUES (
        'FastFood Braga Centro',
        'Rua do Castelo, 123, Braga',
        '+351 253 000 001',
        TRUE
    ),
    (
        'FastFood Porto Baixa',
        'Rua de Santa Catarina, 456, Porto',
        '+351 222 000 002',
        TRUE
    ),
    (
        'FastFood Lisboa Saldanha',
        'Av. da República, 789, Lisboa',
        '+351 213 000 003',
        TRUE
    );

-- 2. Tipos de Workstations
INSERT INTO
    workstation_types (type_name, description)
VALUES (
        'GRILL',
        'Estacao de grelhados para carnes'
    ),
    (
        'FRYER',
        'Fritadeira para batatas e fritos'
    ),
    (
        'EGG_STATION',
        'Estacao de preparacao de ovos'
    ),
    (
        'VEGETABLE_PREP',
        'Preparacao de vegetais e saladas'
    ),
    (
        'ASSEMBLY',
        'Montagem final e embalagem'
    ),
    (
        'DELIVERY',
        'Balcao de entrega ao cliente'
    );

-- 2.5 Balcoes de levantamento (3 por restaurante)
INSERT INTO
    counters (restaurant_id, counter_number)
VALUES (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (3, 2),
    (3, 3);

-- 3. Ingredientes (associados aos tipos de workstations)
INSERT INTO
    ingredients (
        name,
        allergen_type,
        unit_type,
        workstation_type_id,
        active
    )
VALUES (
        'Beef Patty',
        NULL,
        'PIECES',
        1,
        TRUE
    ),
    (
        'Chicken Breast',
        NULL,
        'PIECES',
        1,
        TRUE
    ),
    (
        'Burger Bun',
        'GLUTEN',
        'PIECES',
        1,
        TRUE
    ),
    (
        'Lettuce',
        NULL,
        'GRAMS',
        4,
        TRUE
    ),
    (
        'Tomato',
        NULL,
        'GRAMS',
        4,
        TRUE
    ),
    (
        'Onion',
        NULL,
        'GRAMS',
        4,
        TRUE
    ),
    (
        'Cheese Slice',
        'DAIRY',
        'PIECES',
        NULL,
        TRUE
    ),
    (
        'Egg',
        'EGGS',
        'PIECES',
        3,
        TRUE
    ),
    (
        'Potato',
        NULL,
        'GRAMS',
        2,
        TRUE
    ),
    ('Oil', NULL, 'ML', 2, TRUE),
    (
        'Ketchup',
        NULL,
        'ML',
        5,
        TRUE
    ),
    (
        'Mayonnaise',
        'EGGS',
        'ML',
        5,
        TRUE
    ),
    (
        'Bacon',
        NULL,
        'PIECES',
        1,
        TRUE
    ),
    (
        'Coca-Cola',
        NULL,
        'ML',
        5,
        TRUE
    ),
    ('Water', NULL, 'ML', 5, TRUE);

-- 4. Postos de trabalho (por restaurante)
INSERT INTO
    workstations (
        restaurant_id,
        workstation_type_id,
        is_active
    )
VALUES
    -- Braga (IDs 1-6)
    (1, 1, TRUE), -- GRILL
    (1, 2, TRUE), -- FRYER
    (1, 3, TRUE), -- EGG_STATION
    (1, 4, TRUE), -- VEGETABLE_PREP
    (1, 5, TRUE), -- ASSEMBLY
    (1, 6, TRUE), -- DELIVERY
    -- Porto (IDs 7-12)
    (2, 1, TRUE),
    (2, 2, TRUE),
    (2, 3, TRUE),
    (2, 4, TRUE),
    (2, 5, TRUE),
    (2, 6, TRUE), -- DELIVERY
    -- Lisboa (IDs 13-18)
    (3, 1, TRUE),
    (3, 2, TRUE),
    (3, 3, TRUE),
    (3, 4, TRUE),
    (3, 5, TRUE),
    (3, 6, TRUE);
-- DELIVERY

-- 5. Empregados
INSERT INTO
    employees (
        name,
        restaurant_id,
        role,
        is_active
    )
VALUES
    -- COO (global - associado ao primeiro restaurante por convenção)
    ('Maria Silva', 1, 'COO', TRUE),
    -- Braga
    (
        'João Santos',
        1,
        'MANAGER',
        TRUE
    ),
    (
        'Pedro Costa',
        1,
        'STATION_WORKER',
        TRUE
    ),
    (
        'Ana Ferreira',
        1,
        'STATION_WORKER',
        TRUE
    ),
    (
        'Carlos Mendes',
        1,
        'DELIVERY',
        TRUE
    ),
    -- Porto
    (
        'Sofia Oliveira',
        2,
        'MANAGER',
        TRUE
    ),
    (
        'Miguel Pereira',
        2,
        'STATION_WORKER',
        TRUE
    ),
    (
        'Rita Sousa',
        2,
        'STATION_WORKER',
        TRUE
    ),
    (
        'Bruno Alves',
        2,
        'DELIVERY',
        TRUE
    ),
    -- Lisboa
    (
        'Tiago Rodrigues',
        3,
        'MANAGER',
        TRUE
    ),
    (
        'Catarina Martins',
        3,
        'STATION_WORKER',
        TRUE
    ),
    (
        'Rui Gomes',
        3,
        'STATION_WORKER',
        TRUE
    ),
    (
        'Beatriz Lopes',
        3,
        'DELIVERY',
        TRUE
    );

-- 6. Autenticação de empregados- TODAS as passwords sao 'dss'
INSERT INTO
    employee_authentication (
        employee_id,
        username,
        password_hash
    )
VALUES
    -- COO
    (
        1,
        'maria.silva',
        '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
    ),

-- Braga
(
    2,
    'joao.santos',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    3,
    'pedro.costa',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    4,
    'ana.ferreira',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    5,
    'carlos.mendes',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),

-- Porto
(
    6,
    'sofia.oliveira',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    7,
    'miguel.pereira',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    8,
    'rita.sousa',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    9,
    'bruno.alves',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),

-- Lisboa
(
    10,
    'tiago.rodrigues',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    11,
    'catarina.martins',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    12,
    'rui.gomes',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
),
(
    13,
    'beatriz.lopes',
    '$2a$10$cVRCD89h1KUKVOcvg2zo.u/4HpWE5imt9KJ7VKRpqwHarCbHWjpOm'
);

-- 7. Menu Items (globais)
INSERT INTO
    menu_items (
        name,
        description,
        base_price,
        category,
        is_menu,
        active
    )
VALUES
    -- Principais
    (
        'Classic Burger',
        'Beef patty with lettuce, tomato, and cheese',
        6.50,
        'MAIN',
        FALSE,
        TRUE
    ),
    (
        'Chicken Burger',
        'Grilled chicken with lettuce and mayo',
        6.00,
        'MAIN',
        FALSE,
        TRUE
    ),
    (
        'Bacon Burger',
        'Beef patty with bacon and cheese',
        7.50,
        'MAIN',
        FALSE,
        TRUE
    ),
    -- Acompanhamentos
    (
        'French Fries',
        'Crispy golden fries',
        2.50,
        'SIDE',
        FALSE,
        TRUE
    ),
    (
        'Onion Rings',
        'Crispy fried onion rings',
        3.00,
        'SIDE',
        FALSE,
        TRUE
    ),
    -- Bebidas
    (
        'Coca-Cola 330ml',
        'Coca-Cola can',
        1.50,
        'DRINK',
        FALSE,
        TRUE
    ),
    (
        'Water 500ml',
        'Still water bottle',
        1.00,
        'DRINK',
        FALSE,
        TRUE
    ),
    -- Menus
    (
        'Classic Combo',
        'Classic Burger + Fries + Drink',
        9.00,
        'MENU',
        TRUE,
        TRUE
    ),
    (
        'Chicken Combo',
        'Chicken Burger + Fries + Drink',
        8.50,
        'MENU',
        TRUE,
        TRUE
    );

-- 8. Ingredientes dos Menu Items
-- Classic Burger
INSERT INTO
    menu_item_ingredients (
        menu_item_id,
        ingredient_id,
        quantity,
        is_optional,
        is_customizable
    )
VALUES (1, 1, 1, FALSE, FALSE), -- Beef Patty
    (1, 3, 1, FALSE, FALSE), -- Burger Bun
    (1, 4, 30, TRUE, TRUE), -- Lettuce
    (1, 5, 20, TRUE, TRUE), -- Tomato
    (1, 7, 1, TRUE, TRUE);
-- Cheese Slice

-- Chicken Burger
INSERT INTO
    menu_item_ingredients (
        menu_item_id,
        ingredient_id,
        quantity,
        is_optional,
        is_customizable
    )
VALUES (2, 2, 1, FALSE, FALSE), -- Chicken Breast
    (2, 3, 1, FALSE, FALSE), -- Burger Bun
    (2, 4, 30, TRUE, TRUE), -- Lettuce
    (2, 12, 20, TRUE, TRUE);
-- Mayonnaise

-- Bacon Burger
INSERT INTO
    menu_item_ingredients (
        menu_item_id,
        ingredient_id,
        quantity,
        is_optional,
        is_customizable
    )
VALUES (3, 1, 1, FALSE, FALSE), -- Beef Patty
    (3, 3, 1, FALSE, FALSE), -- Burger Bun
    (3, 13, 2, FALSE, TRUE), -- Bacon
    (3, 7, 1, TRUE, TRUE);
-- Cheese Slice

-- French Fries
INSERT INTO
    menu_item_ingredients (
        menu_item_id,
        ingredient_id,
        quantity,
        is_optional,
        is_customizable
    )
VALUES (4, 9, 200, FALSE, FALSE), -- Potato
    (4, 10, 50, FALSE, FALSE);
-- Oil

-- Classic Combo (ID 8)
INSERT INTO
    menu_item_ingredients (
        menu_item_id,
        ingredient_id,
        quantity,
        is_optional,
        is_customizable
    )
VALUES (8, 1, 1, FALSE, FALSE), -- Beef Patty
    (8, 3, 1, FALSE, FALSE), -- Bun
    (8, 4, 30, TRUE, TRUE), -- Lettuce
    (8, 5, 20, TRUE, TRUE), -- Tomato
    (8, 7, 1, TRUE, TRUE), -- Cheese
    (8, 9, 200, FALSE, FALSE), -- Potato (Fries)
    (8, 10, 50, FALSE, FALSE);
-- Oil

-- Chicken Combo (ID 9)
INSERT INTO
    menu_item_ingredients (
        menu_item_id,
        ingredient_id,
        quantity,
        is_optional,
        is_customizable
    )
VALUES (9, 2, 1, FALSE, FALSE), -- Chicken Breast
    (9, 3, 1, FALSE, FALSE), -- Bun
    (9, 4, 30, TRUE, TRUE), -- Lettuce
    (9, 12, 20, TRUE, TRUE), -- Mayonnaise
    (9, 9, 200, FALSE, FALSE), -- Potato (Fries)
    (9, 10, 50, FALSE, FALSE);
-- Oil

-- 9. Inventário por restaurante
INSERT INTO
    inventory_items (
        restaurant_id,
        ingredient_id,
        quantity_available,
        reorder_threshold,
        warehouse_quantity
    )
VALUES
    -- Braga
    (1, 1, 50, 20, 200), -- Beef Patty
    (1, 2, 40, 15, 150), -- Chicken Breast
    (1, 3, 100, 30, 300), -- Burger Bun
    (1, 4, 500, 200, 1000), -- Lettuce
    (1, 5, 300, 100, 800), -- Tomato
    (1, 6, 200, 80, 500), -- Onion
    (1, 7, 80, 30, 200), -- Cheese Slice
    (1, 8, 60, 20, 150), -- Egg
    (1, 9, 2000, 500, 5000), -- Potato
    (1, 10, 5000, 1000, 10000), -- Oil
    (1, 11, 1000, 300, 2000), -- Ketchup
    (1, 12, 800, 200, 1500), -- Mayonnaise
    (1, 13, 40, 15, 100), -- Bacon
    (1, 14, 200, 50, 500), -- Coca-Cola
    (1, 15, 300, 100, 1000), -- Water
    -- Porto (quantidades diferentes)
    (2, 1, 60, 20, 250),
    (2, 2, 50, 15, 180),
    (2, 3, 120, 30, 350),
    (2, 4, 600, 200, 1200),
    (2, 5, 350, 100, 900),
    (2, 6, 250, 80, 600),
    (2, 7, 90, 30, 220),
    (2, 8, 70, 20, 170),
    (2, 9, 2500, 500, 6000),
    (2, 10, 6000, 1000, 12000),
    (2, 11, 1200, 300, 2500),
    (2, 12, 900, 200, 1800),
    (2, 13, 50, 15, 120),
    (2, 14, 250, 50, 600),
    (2, 15, 350, 100, 1200),
    -- Lisboa (quantidades diferentes)
    (3, 1, 70, 20, 280),
    (3, 2, 55, 15, 200),
    (3, 3, 130, 30, 400),
    (3, 4, 650, 200, 1300),
    (3, 5, 400, 100, 1000),
    (3, 6, 280, 80, 650),
    (3, 7, 100, 30, 250),
    (3, 8, 80, 20, 180),
    (3, 9, 2800, 500, 6500),
    (3, 10, 6500, 1000, 13000),
    (3, 11, 1300, 300, 2800),
    (3, 12, 1000, 200, 2000),
    (3, 13, 60, 15, 130),
    (3, 14, 280, 50, 650),
    (3, 15, 400, 100, 1300);

-- 10. Atribuições de empregados a postos (exemplos)
INSERT INTO
    employee_workstation_assignments (employee_id, workstation_id)
VALUES
    -- Braga
    (3, 1), -- Pedro Costa -> GRILL
    (4, 2), -- Ana Ferreira -> FRYER
    (5, 6), -- Carlos Mendes -> DELIVERY
    -- Porto
    (7, 7), -- Miguel Pereira -> GRILL
    (8, 8), -- Rita Sousa -> FRYER
    (9, 12), -- Bruno Alves -> DELIVERY
    -- Lisboa
    (11, 13), -- Catarina Martins -> GRILL
    (12, 14), -- Rui Gomes -> FRYER
    (13, 18);
-- Beatriz Lopes -> DELIVERY

--11. Kiosks
INSERT INTO
    kiosks (restaurant_id, status)
VALUES (1, 'ACTIVE'), -- Restaurante Braga Centro
    (1, 'ACTIVE'), -- Restaurante Braga Centro
    (2, 'ACTIVE'), -- Restaurante Porto Baixa
    (2, 'ACTIVE'), -- Restaurante Porto Baixa
    (3, 'ACTIVE'), -- Restaurante Lisboa Saldanha
    (3, 'ACTIVE');
-- Restaurante Lisboa Saldanha

--12. Displays (updated for new workstation IDs)
INSERT INTO
    displays (workstation_id)
VALUES (1),
    (2),
    (3),
    (4),
    (5),
    (6),
    (7),
    (8),
    (9),
    (10),
    (11),
    (12),
    (13),
    (14),
    (15),
    (16),
    (17),
    (18);