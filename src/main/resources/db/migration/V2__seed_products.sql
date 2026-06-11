INSERT INTO products (
    name,
    description,
    category,
    subcategory,
    brand,
    image_url,
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
    'Laptops',
    'Lenovo',
    'https://images.unsplash.com/photo-1496181133206-80ce9b88a853',
    TRUE,
    12999.99,
    NOW(),
    NOW()
),
(
    'Mechanical Keyboard',
    'RGB coding keyboard',
    'Accessories',
    'Keyboards',
    'Logitech',
    'https://images.unsplash.com/photo-1587829741301-dc798b83add3',
    TRUE,
    899.99,
    NOW(),
    NOW()
),
(
    'Wireless Mouse',
    'Ergonomic mouse',
    'Accessories',
    'Mice',
    'Logitech',
    'https://images.unsplash.com/photo-1527814050087-3793815479db',
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