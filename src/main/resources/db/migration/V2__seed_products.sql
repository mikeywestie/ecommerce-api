INSERT INTO products (name, description, price, created_at)
VALUES
('Java Developer Laptop', 'Good laptop for Spring Boot development', 12999.99, NOW()),
('Mechanical Keyboard', 'RGB coding keyboard', 899.99, NOW()),
('Wireless Mouse', 'Ergonomic mouse', 399.99, NOW());

INSERT INTO inventory (product_id, quantity_available)
SELECT id, 10 FROM products WHERE name = 'Java Developer Laptop';

INSERT INTO inventory (product_id, quantity_available)
SELECT id, 25 FROM products WHERE name = 'Mechanical Keyboard';

INSERT INTO inventory (product_id, quantity_available)
SELECT id, 40 FROM products WHERE name = 'Wireless Mouse';