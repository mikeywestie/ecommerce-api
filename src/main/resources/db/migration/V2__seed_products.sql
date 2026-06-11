INSERT INTO products (
    name,
    description,
    category,
    active,
    price,
    created_at,
    updated_at
)
VALUES
(
    'Java Developer Laptop',
    'Good laptop for Spring Boot development',
    'Computers',
    TRUE,
    12999.99,
    NOW(),
    NOW()
),
(
    'Mechanical Keyboard',
    'RGB coding keyboard',
    'Accessories',
    TRUE,
    899.99,
    NOW(),
    NOW()
),
(
    'Wireless Mouse',
    'Ergonomic mouse',
    'Accessories',
    TRUE,
    399.99,
    NOW(),
    NOW()
)
ON CONFLICT DO NOTHING;

INSERT INTO inventory (product_id, quantity_available)
SELECT id, 10 FROM products WHERE name = 'Java Developer Laptop'
ON CONFLICT DO NOTHING;

INSERT INTO inventory (product_id, quantity_available)
SELECT id, 25 FROM products WHERE name = 'Mechanical Keyboard'
ON CONFLICT DO NOTHING;

INSERT INTO inventory (product_id, quantity_available)
SELECT id, 40 FROM products WHERE name = 'Wireless Mouse'
ON CONFLICT DO NOTHING;