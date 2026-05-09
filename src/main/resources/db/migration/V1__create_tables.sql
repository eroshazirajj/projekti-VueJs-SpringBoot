-- Krijimi i tabelës së kategorive
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Krijimi i tabelës së produkteve me relacionin category_id
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    description VARCHAR(255),
    category_id BIGINT,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Shto disa të dhëna fillestare (Seed Data)
INSERT INTO categories (name) VALUES ('Sedan'), ('SUV'), ('Sport'), ('Luxury');