-- Drop table if exists
DROP TABLE IF EXISTS BEVERAGE;

-- Create beverage table
CREATE TABLE BEVERAGE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description VARCHAR(500)
);

-- Insert initial beverages
INSERT INTO BEVERAGE (name, price, description) VALUES
    ('Americano', 2.80, 'Espresso diluted with hot water for a smooth taste'),
    ('Flat White', 3.75, 'Velvety microfoam with rich espresso'),
    ('Macchiato', 3.20, 'Espresso marked with a dollop of foam'),
    ('Cortado', 3.40, 'Equal parts espresso and steamed milk'),
    ('Irish Coffee', 5.50, 'Coffee with whiskey and whipped cream'),
    ('Affogato', 4.25, 'Espresso poured over vanilla ice cream'),
    ('Cold Brew', 3.80, 'Slow-steeped coffee served cold'),
    ('Nitro Coffee', 4.50, 'Cold brew infused with nitrogen for a creamy texture'),
    ('Iced Latte', 4.00, 'Espresso with cold milk and ice'),
    ('Iced Mocha', 4.50, 'Espresso with chocolate syrup, cold milk, and ice'),
    ('White Russian', 6.00, 'Coffee with vodka and coffee liqueur');

