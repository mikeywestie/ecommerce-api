-- V16__refresh_demo_store.sql
-- Hard refresh of portfolio/demo store data.
-- Keeps live demo admin credentials managed by DemoAdminSeeder.

UPDATE inventory SET version = 0 WHERE version IS NULL;

-- Clean data in FK-safe order.
DELETE FROM coupon_redemptions;
DELETE FROM payments;
DELETE FROM order_items;
DELETE FROM customer_orders;
DELETE FROM cart_items;
DELETE FROM carts;
DELETE FROM inventory;
DELETE FROM products;
DELETE FROM coupons;
DELETE FROM users WHERE email LIKE '%@demo.local';

-- Reset sequences. Users are preserved except @demo.local customers.
ALTER SEQUENCE IF EXISTS products_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS inventory_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS customer_orders_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS order_items_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS payments_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS coupons_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS coupon_redemptions_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS carts_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS cart_items_id_seq RESTART WITH 1;
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 1), true);

-- Coupons
INSERT INTO coupons (code, type, value, active, expires_at, reusable, max_uses_per_customer, max_total_uses) VALUES
('SAVE10', 'PERCENTAGE', 10.00, TRUE, '2099-12-31 23:59:59+00', TRUE, NULL, NULL),
('WELCOME250', 'FIXED_AMOUNT', 250.00, TRUE, '2099-12-31 23:59:59+00', TRUE, NULL, NULL),
('FIRSTBUY', 'FIXED_AMOUNT', 100.00, TRUE, '2099-12-31 23:59:59+00', FALSE, 1, NULL),
('VIP5', 'PERCENTAGE', 5.00, TRUE, '2099-12-31 23:59:59+00', TRUE, NULL, NULL);

-- Products
INSERT INTO products (name, description, price, created_at, updated_at, category, image_url, active, subcategory, brand) VALUES
('Logitech MX Master 3S', 'Premium ergonomic wireless mouse with quiet clicks and precision scrolling.', 2199.99, NOW() - INTERVAL '70 days', NOW() - INTERVAL '67 days', 'Peripherals', 'https://images.unsplash.com/photo-1527814050087-3793815479db', TRUE, 'Mice', 'Logitech'),
('Logitech G502 X Lightspeed', 'Wireless gaming mouse with high-performance optical switches.', 2499.99, NOW() - INTERVAL '69 days', NOW() - INTERVAL '66 days', 'Peripherals', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7', TRUE, 'Mice', 'Logitech'),
('Keychron K2 Pro Mechanical Keyboard', 'Compact hot-swappable mechanical keyboard for Mac and Windows.', 2399.99, NOW() - INTERVAL '68 days', NOW() - INTERVAL '65 days', 'Peripherals', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3', TRUE, 'Keyboards', 'Keychron'),
('Keychron Q1 Max Keyboard', 'Premium aluminium wireless mechanical keyboard with gasket mount.', 4299.99, NOW() - INTERVAL '67 days', NOW() - INTERVAL '64 days', 'Peripherals', 'https://images.unsplash.com/photo-1595225476474-87563907a212', TRUE, 'Keyboards', 'Keychron'),
('Razer DeathAdder V3 Pro', 'Ultra-lightweight esports mouse built for competitive gaming.', 3299.99, NOW() - INTERVAL '66 days', NOW() - INTERVAL '63 days', 'Peripherals', 'https://images.unsplash.com/photo-1629429408209-1f912961dbd8', TRUE, 'Mice', 'Razer'),
('SteelSeries Apex Pro TKL', 'Adjustable actuation mechanical gaming keyboard with OLED display.', 3999.99, NOW() - INTERVAL '65 days', NOW() - INTERVAL '62 days', 'Peripherals', 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef', TRUE, 'Keyboards', 'SteelSeries'),
('Logitech Brio 4K Webcam', 'Ultra HD webcam with HDR and noise-reducing microphones.', 2899.99, NOW() - INTERVAL '64 days', NOW() - INTERVAL '61 days', 'Peripherals', 'https://images.unsplash.com/photo-1588702547923-7093a6c3ba33', TRUE, 'Webcams', 'Logitech'),
('Elgato Stream Deck MK.2', 'Customizable control pad for streaming, productivity, and shortcuts.', 3499.99, NOW() - INTERVAL '63 days', NOW() - INTERVAL '60 days', 'Peripherals', 'https://images.unsplash.com/photo-1611532736597-de2d4265fba3', TRUE, 'Streaming', 'Elgato'),
('LG 34-inch UltraWide Monitor', 'Curved ultra-wide display for multitasking, design, and productivity.', 8999.99, NOW() - INTERVAL '62 days', NOW() - INTERVAL '59 days', 'Displays', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf', TRUE, 'Monitors', 'LG'),
('Dell UltraSharp U2724D', '27-inch QHD productivity monitor with excellent colour accuracy.', 7999.99, NOW() - INTERVAL '61 days', NOW() - INTERVAL '58 days', 'Displays', 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5', TRUE, 'Monitors', 'Dell'),
('Samsung Odyssey G5 32-inch', 'Curved gaming monitor with high refresh rate and immersive visuals.', 7499.99, NOW() - INTERVAL '60 days', NOW() - INTERVAL '57 days', 'Displays', 'https://images.unsplash.com/photo-1616711906333-23cf8de91dd3', TRUE, 'Gaming Monitors', 'Samsung'),
('ASUS ProArt Display PA278QV', 'Colour accurate monitor for creators and designers.', 6999.99, NOW() - INTERVAL '59 days', NOW() - INTERVAL '56 days', 'Displays', 'https://images.unsplash.com/photo-1547082299-de196ea013d6', TRUE, 'Creator Monitors', 'ASUS'),
('AOC 27G2 Gaming Monitor', 'Fast 27-inch gaming display with smooth refresh performance.', 4999.99, NOW() - INTERVAL '58 days', NOW() - INTERVAL '55 days', 'Displays', 'https://images.unsplash.com/photo-1585792180666-f7347c490ee2', TRUE, 'Gaming Monitors', 'AOC'),
('BenQ MOBIUZ EX2710', 'Entertainment gaming monitor with rich colour and built-in speakers.', 5299.99, NOW() - INTERVAL '57 days', NOW() - INTERVAL '54 days', 'Displays', 'https://images.unsplash.com/photo-1625842268584-8f3296236761', TRUE, 'Gaming Monitors', 'BenQ'),
('Samsung T7 Shield 1TB SSD', 'Rugged portable SSD with fast USB-C transfer speeds.', 2499.99, NOW() - INTERVAL '56 days', NOW() - INTERVAL '53 days', 'Storage', 'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b', TRUE, 'External SSD', 'Samsung'),
('Samsung 990 Pro 2TB NVMe SSD', 'High-performance NVMe storage for gaming and creative workloads.', 3999.99, NOW() - INTERVAL '55 days', NOW() - INTERVAL '52 days', 'Storage', 'https://images.unsplash.com/photo-1601737487795-dab272f52420', TRUE, 'Internal SSD', 'Samsung'),
('WD Black SN850X 1TB', 'Gaming-focused NVMe SSD with excellent read and write speeds.', 2899.99, NOW() - INTERVAL '54 days', NOW() - INTERVAL '51 days', 'Storage', 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef', TRUE, 'Internal SSD', 'Western Digital'),
('Crucial X9 Pro 2TB SSD', 'Compact external SSD for creators and mobile professionals.', 3799.99, NOW() - INTERVAL '53 days', NOW() - INTERVAL '50 days', 'Storage', 'https://images.unsplash.com/photo-1611174743420-3d7df880ce32', TRUE, 'External SSD', 'Crucial'),
('Seagate Expansion 4TB HDD', 'Reliable external hard drive for backups and large media libraries.', 2199.99, NOW() - INTERVAL '52 days', NOW() - INTERVAL '49 days', 'Storage', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3', TRUE, 'External HDD', 'Seagate'),
('Kingston XS1000 1TB SSD', 'Ultra-portable external SSD for everyday storage needs.', 1999.99, NOW() - INTERVAL '51 days', NOW() - INTERVAL '48 days', 'Storage', 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31', TRUE, 'External SSD', 'Kingston'),
('Sony WH-1000XM5 Headphones', 'Premium wireless noise-cancelling headphones with long battery life.', 6999.99, NOW() - INTERVAL '50 days', NOW() - INTERVAL '47 days', 'Audio', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e', TRUE, 'Headphones', 'Sony'),
('JBL Flip 6 Bluetooth Speaker', 'Portable waterproof speaker with punchy bass and clear sound.', 2499.99, NOW() - INTERVAL '49 days', NOW() - INTERVAL '46 days', 'Audio', 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1', TRUE, 'Speakers', 'JBL'),
('Bose QuietComfort Headphones', 'Comfortable wireless headphones with strong active noise cancellation.', 6499.99, NOW() - INTERVAL '48 days', NOW() - INTERVAL '45 days', 'Audio', 'https://images.unsplash.com/photo-1484704849700-f032a568e944', TRUE, 'Headphones', 'Bose'),
('HyperX Cloud III Gaming Headset', 'Comfortable gaming headset with clear voice communication.', 1999.99, NOW() - INTERVAL '47 days', NOW() - INTERVAL '44 days', 'Audio', 'https://images.unsplash.com/photo-1599669454699-248893623440', TRUE, 'Gaming Headsets', 'HyperX'),
('Audio-Technica ATH-M50x', 'Studio monitor headphones trusted by creators and musicians.', 3499.99, NOW() - INTERVAL '46 days', NOW() - INTERVAL '43 days', 'Audio', 'https://images.unsplash.com/photo-1546435770-a3e426bf472b', TRUE, 'Studio Headphones', 'Audio-Technica'),
('Logitech G733 Lightspeed', 'Wireless RGB gaming headset with lightweight comfort.', 2799.99, NOW() - INTERVAL '45 days', NOW() - INTERVAL '42 days', 'Audio', 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb', TRUE, 'Gaming Headsets', 'Logitech'),
('Blue Yeti USB Microphone', 'Popular USB microphone for podcasting, streaming, and meetings.', 2999.99, NOW() - INTERVAL '44 days', NOW() - INTERVAL '41 days', 'Audio', 'https://images.unsplash.com/photo-1590602847861-f357a9332bbc', TRUE, 'Microphones', 'Blue'),
('TP-Link Archer AX55 Router', 'Wi-Fi 6 router for fast home and office connectivity.', 2499.99, NOW() - INTERVAL '43 days', NOW() - INTERVAL '40 days', 'Networking', 'https://images.unsplash.com/photo-1606904825846-647eb07f5be2', TRUE, 'Routers', 'TP-Link'),
('TP-Link Deco X50 Mesh Kit', 'Whole-home Wi-Fi 6 mesh system with seamless coverage.', 4999.99, NOW() - INTERVAL '42 days', NOW() - INTERVAL '39 days', 'Networking', 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8', TRUE, 'Mesh Wi-Fi', 'TP-Link'),
('ASUS RT-AX58U Router', 'Dual-band Wi-Fi 6 router with strong security features.', 3299.99, NOW() - INTERVAL '41 days', NOW() - INTERVAL '38 days', 'Networking', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64', TRUE, 'Routers', 'ASUS'),
('Netgear Nighthawk AX5400', 'High-speed router for streaming, gaming, and smart homes.', 4299.99, NOW() - INTERVAL '40 days', NOW() - INTERVAL '37 days', 'Networking', 'https://images.unsplash.com/photo-1600267165477-6d4cc741b379', TRUE, 'Routers', 'Netgear'),
('Ubiquiti UniFi U6 Lite AP', 'Compact enterprise-style Wi-Fi access point for reliable coverage.', 2399.99, NOW() - INTERVAL '39 days', NOW() - INTERVAL '36 days', 'Networking', 'https://images.unsplash.com/photo-1516321497487-e288fb19713f', TRUE, 'Access Points', 'Ubiquiti'),
('TP-Link 24-Port Gigabit Switch', 'Rack-mountable network switch for small business networks.', 2999.99, NOW() - INTERVAL '38 days', NOW() - INTERVAL '35 days', 'Networking', 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31', TRUE, 'Switches', 'TP-Link'),
('Anker 7-in-1 USB-C Hub', 'Compact USB-C hub with HDMI, USB, and card reader ports.', 1199.99, NOW() - INTERVAL '37 days', NOW() - INTERVAL '34 days', 'Accessories', 'https://images.unsplash.com/photo-1625842268584-8f3296236761', TRUE, 'USB-C Hubs', 'Anker'),
('Rain Design Laptop Stand', 'Aluminium laptop stand for a cleaner ergonomic desk setup.', 899.99, NOW() - INTERVAL '36 days', NOW() - INTERVAL '33 days', 'Accessories', 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46', TRUE, 'Laptop Stands', 'Rain Design'),
('Logitech Litra Glow Light', 'Premium webcam light for streaming and video calls.', 1499.99, NOW() - INTERVAL '35 days', NOW() - INTERVAL '32 days', 'Accessories', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3', TRUE, 'Lighting', 'Logitech'),
('UGREEN Cable Organizer Kit', 'Desk cable management kit for tidy workstations.', 349.99, NOW() - INTERVAL '34 days', NOW() - INTERVAL '31 days', 'Accessories', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3', TRUE, 'Cable Management', 'UGREEN'),
('Belkin Wireless Charging Pad', 'Fast wireless charging pad for compatible smartphones and earbuds.', 799.99, NOW() - INTERVAL '33 days', NOW() - INTERVAL '30 days', 'Accessories', 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5', TRUE, 'Chargers', 'Belkin'),
('Dell WD19S Docking Station', 'Business docking station with USB-C, HDMI, DisplayPort, and Ethernet.', 4299.99, NOW() - INTERVAL '32 days', NOW() - INTERVAL '29 days', 'Accessories', 'https://images.unsplash.com/photo-1625842268584-8f3296236761', TRUE, 'Docking Stations', 'Dell'),
('Ergonomic Mesh Office Chair', 'Breathable office chair with adjustable lumbar support.', 3999.99, NOW() - INTERVAL '31 days', NOW() - INTERVAL '28 days', 'Office', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7', TRUE, 'Furniture', 'ErgoPro'),
('Electric Standing Desk', 'Height-adjustable desk for flexible sitting and standing work.', 8499.99, NOW() - INTERVAL '30 days', NOW() - INTERVAL '27 days', 'Office', 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd', TRUE, 'Furniture', 'FlexiDesk'),
('BenQ ScreenBar Desk Lamp', 'Monitor-mounted desk lamp with adjustable brightness.', 1799.99, NOW() - INTERVAL '29 days', NOW() - INTERVAL '26 days', 'Office', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c', TRUE, 'Lighting', 'BenQ'),
('North Bay Monitor Arm', 'Gas spring monitor arm for better screen positioning.', 1299.99, NOW() - INTERVAL '28 days', NOW() - INTERVAL '25 days', 'Office', 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc', TRUE, 'Desk Accessories', 'North Bay'),
('Memory Foam Foot Rest', 'Comfortable under-desk foot rest for long work sessions.', 499.99, NOW() - INTERVAL '27 days', NOW() - INTERVAL '24 days', 'Office', 'https://images.unsplash.com/photo-1515378791036-0648a3ef77b2', TRUE, 'Ergonomics', 'ComfyWork'),
('Xbox Wireless Controller', 'Wireless controller for PC and Xbox gaming.', 1499.99, NOW() - INTERVAL '26 days', NOW() - INTERVAL '23 days', 'Gaming', 'https://images.unsplash.com/photo-1600080972464-8e5f35f63d08', TRUE, 'Controllers', 'Microsoft'),
('DualSense Wireless Controller', 'PlayStation controller with adaptive triggers and haptic feedback.', 1699.99, NOW() - INTERVAL '70 days', NOW() - INTERVAL '67 days', 'Gaming', 'https://images.unsplash.com/photo-1606144042614-b2417e99c4e3', TRUE, 'Controllers', 'Sony'),
('SteelSeries Arctis Nova 7', 'Wireless gaming headset with multi-platform support.', 3999.99, NOW() - INTERVAL '69 days', NOW() - INTERVAL '66 days', 'Gaming', 'https://images.unsplash.com/photo-1599669454699-248893623440', TRUE, 'Gaming Headsets', 'SteelSeries'),
('Logitech G923 Racing Wheel', 'Force feedback racing wheel and pedals for realistic driving sims.', 7999.99, NOW() - INTERVAL '68 days', NOW() - INTERVAL '65 days', 'Gaming', 'https://images.unsplash.com/photo-1550745165-9bc0b252726f', TRUE, 'Racing Wheels', 'Logitech'),
('Razer RGB Mouse Pad', 'Large RGB mouse pad with smooth tracking surface.', 999.99, NOW() - INTERVAL '67 days', NOW() - INTERVAL '64 days', 'Gaming', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7', TRUE, 'Mouse Pads', 'Razer'),
('Elgato HD60 X Capture Card', 'External capture card for streaming console gameplay.', 3999.99, NOW() - INTERVAL '66 days', NOW() - INTERVAL '63 days', 'Gaming', 'https://images.unsplash.com/photo-1511512578047-dfb367046420', TRUE, 'Capture Cards', 'Elgato');

-- Inventory
INSERT INTO inventory (product_id, quantity_available, version)
SELECT p.id, v.quantity_available, 0
FROM (VALUES
('Logitech MX Master 3S', 32),
('Logitech G502 X Lightspeed', 21),
('Keychron K2 Pro Mechanical Keyboard', 18),
('Keychron Q1 Max Keyboard', 9),
('Razer DeathAdder V3 Pro', 14),
('SteelSeries Apex Pro TKL', 7),
('Logitech Brio 4K Webcam', 16),
('Elgato Stream Deck MK.2', 11),
('LG 34-inch UltraWide Monitor', 5),
('Dell UltraSharp U2724D', 6),
('Samsung Odyssey G5 32-inch', 4),
('ASUS ProArt Display PA278QV', 8),
('AOC 27G2 Gaming Monitor', 10),
('BenQ MOBIUZ EX2710', 3),
('Samsung T7 Shield 1TB SSD', 24),
('Samsung 990 Pro 2TB NVMe SSD', 15),
('WD Black SN850X 1TB', 17),
('Crucial X9 Pro 2TB SSD', 12),
('Seagate Expansion 4TB HDD', 20),
('Kingston XS1000 1TB SSD', 0),
('Sony WH-1000XM5 Headphones', 9),
('JBL Flip 6 Bluetooth Speaker', 27),
('Bose QuietComfort Headphones', 6),
('HyperX Cloud III Gaming Headset', 19),
('Audio-Technica ATH-M50x', 13),
('Logitech G733 Lightspeed', 8),
('Blue Yeti USB Microphone', 10),
('TP-Link Archer AX55 Router', 12),
('TP-Link Deco X50 Mesh Kit', 7),
('ASUS RT-AX58U Router', 5),
('Netgear Nighthawk AX5400', 4),
('Ubiquiti UniFi U6 Lite AP', 16),
('TP-Link 24-Port Gigabit Switch', 6),
('Anker 7-in-1 USB-C Hub', 41),
('Rain Design Laptop Stand', 28),
('Logitech Litra Glow Light', 18),
('UGREEN Cable Organizer Kit', 120),
('Belkin Wireless Charging Pad', 34),
('Dell WD19S Docking Station', 11),
('Ergonomic Mesh Office Chair', 5),
('Electric Standing Desk', 2),
('BenQ ScreenBar Desk Lamp', 14),
('North Bay Monitor Arm', 22),
('Memory Foam Foot Rest', 37),
('Xbox Wireless Controller', 23),
('DualSense Wireless Controller', 18),
('SteelSeries Arctis Nova 7', 8),
('Logitech G923 Racing Wheel', 3),
('Razer RGB Mouse Pad', 0),
('Elgato HD60 X Capture Card', 7)
) AS v(product_name, quantity_available)
JOIN products p ON p.name = v.product_name;

-- Demo customer accounts.
-- Customer password hashes are seeded only to satisfy user records; live demo admin login is managed by DemoAdminSeeder.
INSERT INTO users (name, email, password, role) VALUES
('Sarah Johnson', 'sarah.johnson@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('David Wilson', 'david.wilson@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Emma Brown', 'emma.brown@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Chris Miller', 'chris.miller@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('John Peterson', 'john.peterson@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Rebecca Jones', 'rebecca.jones@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Daniel Scott', 'daniel.scott@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Ashley Walker', 'ashley.walker@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Michael Smith', 'michael.smith@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Jessica Taylor', 'jessica.taylor@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Ryan Cooper', 'ryan.cooper@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Lauren Adams', 'lauren.adams@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Brandon Young', 'brandon.young@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Nicole Bennett', 'nicole.bennett@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER'),
('Jason Clark', 'jason.clark@demo.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiIX5FfupSQzPuIiXcYMLLhcjr6lhWy', 'CUSTOMER');

-- Orders, order items, matching payments, and coupon redemptions

-- Order 1: Sarah Johnson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Sarah Johnson', 'sarah.johnson@demo.local', 'PAID', 29449.94, NOW() - INTERVAL '5 days', 'VIP5', 1550.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('LG 34-inch UltraWide Monitor', 2, 8999.99),
    ('Dell WD19S Docking Station', 2, 4299.99),
    ('Logitech MX Master 3S', 2, 2199.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'SUCCESS', 29449.94, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'sarah.johnson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '5 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'sarah.johnson@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '5 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 2: David Wilson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('David Wilson', 'david.wilson@demo.local', 'PAID', 32579.92, NOW() - INTERVAL '8 days', 'SAVE10', 3619.99)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Electric Standing Desk', 2, 8499.99),
    ('Ergonomic Mesh Office Chair', 3, 3999.99),
    ('BenQ ScreenBar Desk Lamp', 4, 1799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'SUCCESS', 32579.92, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'david.wilson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '8 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'SAVE10'
WHERE o.customer_email = 'david.wilson@demo.local'
  AND o.coupon_code = 'SAVE10'
  AND o.created_at = NOW() - INTERVAL '8 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 3: Emma Brown / CANCELLED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Emma Brown', 'emma.brown@demo.local', 'CANCELLED', 26999.96, NOW() - INTERVAL '12 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Samsung Odyssey G5 32-inch', 2, 7499.99),
    ('Logitech G923 Racing Wheel', 1, 7999.99),
    ('SteelSeries Arctis Nova 7', 1, 3999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;

-- Order 4: Sarah Johnson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Sarah Johnson', 'sarah.johnson@demo.local', 'PAID', 9719.97, NOW() - INTERVAL '15 days', 'SAVE10', 1080.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Netgear Nighthawk AX5400', 2, 4299.99),
    ('Seagate Expansion 4TB HDD', 1, 2199.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'SUCCESS', 9719.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'sarah.johnson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '15 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'SAVE10'
WHERE o.customer_email = 'sarah.johnson@demo.local'
  AND o.coupon_code = 'SAVE10'
  AND o.created_at = NOW() - INTERVAL '15 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 5: David Wilson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('David Wilson', 'david.wilson@demo.local', 'PAID', 2749.99, NOW() - INTERVAL '16 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Blue Yeti USB Microphone', 1, 2999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'SUCCESS', 2749.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'david.wilson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '16 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'david.wilson@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '16 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 6: Emma Brown / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Emma Brown', 'emma.brown@demo.local', 'PAID', 2199.99, NOW() - INTERVAL '17 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Logitech MX Master 3S', 1, 2199.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'SUCCESS', 2199.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'emma.brown@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '17 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 7: Chris Miller / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Chris Miller', 'chris.miller@demo.local', 'PAID', 10709.96, NOW() - INTERVAL '18 days', 'SAVE10', 1190.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('North Bay Monitor Arm', 3, 1299.99),
    ('Dell UltraSharp U2724D', 1, 7999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'SUCCESS', 10709.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'chris.miller@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '18 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'SAVE10'
WHERE o.customer_email = 'chris.miller@demo.local'
  AND o.coupon_code = 'SAVE10'
  AND o.created_at = NOW() - INTERVAL '18 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 8: John Peterson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('John Peterson', 'john.peterson@demo.local', 'PAID', 4299.99, NOW() - INTERVAL '19 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Dell WD19S Docking Station', 1, 4299.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'SUCCESS', 4299.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'john.peterson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '19 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 9: Rebecca Jones / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Rebecca Jones', 'rebecca.jones@demo.local', 'PAID', 4299.99, NOW() - INTERVAL '20 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Dell WD19S Docking Station', 1, 4299.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'SUCCESS', 4299.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'rebecca.jones@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '20 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 10: Daniel Scott / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Daniel Scott', 'daniel.scott@demo.local', 'PAID', 12549.98, NOW() - INTERVAL '21 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Dell WD19S Docking Station', 1, 4299.99),
    ('Electric Standing Desk', 1, 8499.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'SUCCESS', 12549.98, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'daniel.scott@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '21 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'daniel.scott@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '21 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 11: Ashley Walker / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Ashley Walker', 'ashley.walker@demo.local', 'PAID', 5309.98, NOW() - INTERVAL '22 days', 'SAVE10', 590.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('WD Black SN850X 1TB', 1, 2899.99),
    ('Blue Yeti USB Microphone', 1, 2999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'SUCCESS', 5309.98, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'ashley.walker@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '22 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'SAVE10'
WHERE o.customer_email = 'ashley.walker@demo.local'
  AND o.coupon_code = 'SAVE10'
  AND o.created_at = NOW() - INTERVAL '22 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 12: Michael Smith / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Michael Smith', 'michael.smith@demo.local', 'PAID', 6349.96, NOW() - INTERVAL '23 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Anker 7-in-1 USB-C Hub', 1, 1199.99),
    ('BenQ ScreenBar Desk Lamp', 3, 1799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'SUCCESS', 6349.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'michael.smith@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '23 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'michael.smith@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '23 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 13: Jessica Taylor / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Jessica Taylor', 'jessica.taylor@demo.local', 'PAID', 11699.95, NOW() - INTERVAL '24 days', 'SAVE10', 1300.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Keychron K2 Pro Mechanical Keyboard', 1, 2399.99),
    ('Seagate Expansion 4TB HDD', 3, 2199.99),
    ('SteelSeries Apex Pro TKL', 1, 3999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'SUCCESS', 11699.95, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'jessica.taylor@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '24 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'SAVE10'
WHERE o.customer_email = 'jessica.taylor@demo.local'
  AND o.coupon_code = 'SAVE10'
  AND o.created_at = NOW() - INTERVAL '24 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 14: Ryan Cooper / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Ryan Cooper', 'ryan.cooper@demo.local', 'PAID', 9999.98, NOW() - INTERVAL '25 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Dell UltraSharp U2724D', 1, 7999.99),
    ('Kingston XS1000 1TB SSD', 1, 1999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'SUCCESS', 9999.98, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'ryan.cooper@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '25 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 15: Lauren Adams / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Lauren Adams', 'lauren.adams@demo.local', 'PAID', 13849.97, NOW() - INTERVAL '26 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Razer DeathAdder V3 Pro', 2, 3299.99),
    ('Samsung Odyssey G5 32-inch', 1, 7499.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'SUCCESS', 13849.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'lauren.adams@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '26 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'lauren.adams@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '26 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 16: Brandon Young / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Brandon Young', 'brandon.young@demo.local', 'PAID', 10549.97, NOW() - INTERVAL '27 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Logitech MX Master 3S', 1, 2199.99),
    ('Netgear Nighthawk AX5400', 2, 4299.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'SUCCESS', 10549.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'brandon.young@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '27 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'brandon.young@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '27 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 17: Nicole Bennett / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Nicole Bennett', 'nicole.bennett@demo.local', 'PAID', 2299.99, NOW() - INTERVAL '28 days', 'FIRSTBUY', 100.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Ubiquiti UniFi U6 Lite AP', 1, 2399.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'SUCCESS', 2299.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'nicole.bennett@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '28 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'FIRSTBUY'
WHERE o.customer_email = 'nicole.bennett@demo.local'
  AND o.coupon_code = 'FIRSTBUY'
  AND o.created_at = NOW() - INTERVAL '28 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 18: Jason Clark / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Jason Clark', 'jason.clark@demo.local', 'PAID', 2799.99, NOW() - INTERVAL '29 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Logitech G733 Lightspeed', 1, 2799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'SUCCESS', 2799.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'jason.clark@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '29 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 19: Sarah Johnson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Sarah Johnson', 'sarah.johnson@demo.local', 'PAID', 10499.94, NOW() - INTERVAL '30 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Keychron K2 Pro Mechanical Keyboard', 2, 2399.99),
    ('North Bay Monitor Arm', 3, 1299.99),
    ('BenQ ScreenBar Desk Lamp', 1, 1799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'SUCCESS', 10499.94, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'sarah.johnson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '30 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 20: David Wilson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('David Wilson', 'david.wilson@demo.local', 'PAID', 3349.97, NOW() - INTERVAL '31 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('North Bay Monitor Arm', 1, 1299.99),
    ('Memory Foam Foot Rest', 1, 499.99),
    ('BenQ ScreenBar Desk Lamp', 1, 1799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'SUCCESS', 3349.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'david.wilson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '31 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'david.wilson@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '31 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 21: Emma Brown / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Emma Brown', 'emma.brown@demo.local', 'PAID', 899.99, NOW() - INTERVAL '32 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Rain Design Laptop Stand', 1, 899.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'SUCCESS', 899.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'emma.brown@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '32 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 22: Chris Miller / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Chris Miller', 'chris.miller@demo.local', 'PAID', 2799.99, NOW() - INTERVAL '33 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Logitech G733 Lightspeed', 1, 2799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'SUCCESS', 2799.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'chris.miller@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '33 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 23: John Peterson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('John Peterson', 'john.peterson@demo.local', 'PAID', 1999.98, NOW() - INTERVAL '34 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Razer RGB Mouse Pad', 2, 999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'SUCCESS', 1999.98, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'john.peterson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '34 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 24: Rebecca Jones / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Rebecca Jones', 'rebecca.jones@demo.local', 'PAID', 18999.96, NOW() - INTERVAL '35 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Samsung 990 Pro 2TB NVMe SSD', 3, 3999.99),
    ('ASUS ProArt Display PA278QV', 1, 6999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'SUCCESS', 18999.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'rebecca.jones@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '35 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 25: Daniel Scott / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Daniel Scott', 'daniel.scott@demo.local', 'PAID', 13299.96, NOW() - INTERVAL '36 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Samsung 990 Pro 2TB NVMe SSD', 1, 3999.99),
    ('Elgato Stream Deck MK.2', 1, 3499.99),
    ('Logitech Brio 4K Webcam', 2, 2899.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'SUCCESS', 13299.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'daniel.scott@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '36 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 26: Ashley Walker / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Ashley Walker', 'ashley.walker@demo.local', 'PAID', 5349.98, NOW() - INTERVAL '37 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Logitech G733 Lightspeed', 2, 2799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'SUCCESS', 5349.98, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'ashley.walker@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '37 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'ashley.walker@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '37 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 27: Michael Smith / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Michael Smith', 'michael.smith@demo.local', 'PAID', 5889.98, NOW() - INTERVAL '38 days', 'VIP5', 310.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('ASUS RT-AX58U Router', 1, 3299.99),
    ('WD Black SN850X 1TB', 1, 2899.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'SUCCESS', 5889.98, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'michael.smith@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '38 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'michael.smith@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '38 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 28: Jessica Taylor / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Jessica Taylor', 'jessica.taylor@demo.local', 'PAID', 2499.99, NOW() - INTERVAL '39 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('TP-Link Archer AX55 Router', 1, 2499.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'SUCCESS', 2499.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'jessica.taylor@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '39 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 29: Ryan Cooper / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Ryan Cooper', 'ryan.cooper@demo.local', 'PAID', 13399.97, NOW() - INTERVAL '40 days', 'FIRSTBUY', 100.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Logitech Brio 4K Webcam', 1, 2899.99),
    ('BenQ MOBIUZ EX2710', 2, 5299.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'SUCCESS', 13399.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'ryan.cooper@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '40 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'FIRSTBUY'
WHERE o.customer_email = 'ryan.cooper@demo.local'
  AND o.coupon_code = 'FIRSTBUY'
  AND o.created_at = NOW() - INTERVAL '40 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 30: Lauren Adams / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Lauren Adams', 'lauren.adams@demo.local', 'PAID', 2089.99, NOW() - INTERVAL '41 days', 'VIP5', 110.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Seagate Expansion 4TB HDD', 1, 2199.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'SUCCESS', 2089.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'lauren.adams@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '41 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'lauren.adams@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '41 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 31: Brandon Young / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Brandon Young', 'brandon.young@demo.local', 'PAID', 10549.98, NOW() - INTERVAL '42 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Dell UltraSharp U2724D', 1, 7999.99),
    ('Logitech G733 Lightspeed', 1, 2799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'SUCCESS', 10549.98, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'brandon.young@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '42 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'brandon.young@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '42 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 32: Nicole Bennett / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Nicole Bennett', 'nicole.bennett@demo.local', 'PAID', 8499.97, NOW() - INTERVAL '43 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('TP-Link Archer AX55 Router', 1, 2499.99),
    ('TP-Link 24-Port Gigabit Switch', 2, 2999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'SUCCESS', 8499.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'nicole.bennett@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '43 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 33: Jason Clark / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Jason Clark', 'jason.clark@demo.local', 'PAID', 6099.97, NOW() - INTERVAL '44 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('North Bay Monitor Arm', 1, 1299.99),
    ('Ubiquiti UniFi U6 Lite AP', 2, 2399.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'SUCCESS', 6099.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'jason.clark@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '44 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 34: Sarah Johnson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Sarah Johnson', 'sarah.johnson@demo.local', 'PAID', 9699.96, NOW() - INTERVAL '45 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Logitech G502 X Lightspeed', 1, 2499.99),
    ('Ubiquiti UniFi U6 Lite AP', 3, 2399.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'SUCCESS', 9699.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'sarah.johnson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '45 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 35: David Wilson / PAID
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('David Wilson', 'david.wilson@demo.local', 'PAID', 2969.99, NOW() - INTERVAL '46 days', 'SAVE10', 330.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Razer DeathAdder V3 Pro', 1, 3299.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'SUCCESS', 2969.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'david.wilson@demo.local'
  AND o.status = 'PAID'
  AND o.created_at = NOW() - INTERVAL '46 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'SAVE10'
WHERE o.customer_email = 'david.wilson@demo.local'
  AND o.coupon_code = 'SAVE10'
  AND o.created_at = NOW() - INTERVAL '46 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 36: Emma Brown / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Emma Brown', 'emma.brown@demo.local', 'PAYMENT_FAILED', 8454.97, NOW() - INTERVAL '47 days', 'VIP5', 445.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Samsung 990 Pro 2TB NVMe SSD', 2, 3999.99),
    ('Rain Design Laptop Stand', 1, 899.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'FAILED', 8454.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'emma.brown@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '47 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'emma.brown@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '47 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 37: Chris Miller / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Chris Miller', 'chris.miller@demo.local', 'PAYMENT_FAILED', 32899.95, NOW() - INTERVAL '48 days', 'FIRSTBUY', 100.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('TP-Link 24-Port Gigabit Switch', 2, 2999.99),
    ('LG 34-inch UltraWide Monitor', 3, 8999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'FAILED', 32899.95, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'chris.miller@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '48 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'FIRSTBUY'
WHERE o.customer_email = 'chris.miller@demo.local'
  AND o.coupon_code = 'FIRSTBUY'
  AND o.created_at = NOW() - INTERVAL '48 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 38: John Peterson / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('John Peterson', 'john.peterson@demo.local', 'PAYMENT_FAILED', 3999.99, NOW() - INTERVAL '49 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('SteelSeries Arctis Nova 7', 1, 3999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'FAILED', 3999.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'john.peterson@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '49 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 39: Rebecca Jones / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Rebecca Jones', 'rebecca.jones@demo.local', 'PAYMENT_FAILED', 11199.96, NOW() - INTERVAL '50 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('SteelSeries Apex Pro TKL', 1, 3999.99),
    ('Ubiquiti UniFi U6 Lite AP', 3, 2399.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'FAILED', 11199.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'rebecca.jones@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '50 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 40: Daniel Scott / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Daniel Scott', 'daniel.scott@demo.local', 'PAYMENT_FAILED', 13499.96, NOW() - INTERVAL '51 days', 'SAVE10', 1500.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Samsung Odyssey G5 32-inch', 1, 7499.99),
    ('Logitech Brio 4K Webcam', 2, 2899.99),
    ('DualSense Wireless Controller', 1, 1699.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'FAILED', 13499.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'daniel.scott@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '51 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'SAVE10'
WHERE o.customer_email = 'daniel.scott@demo.local'
  AND o.coupon_code = 'SAVE10'
  AND o.created_at = NOW() - INTERVAL '51 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 41: Ashley Walker / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Ashley Walker', 'ashley.walker@demo.local', 'PAYMENT_FAILED', 2249.99, NOW() - INTERVAL '52 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Samsung T7 Shield 1TB SSD', 1, 2499.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'FAILED', 2249.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'ashley.walker@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '52 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'ashley.walker@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '52 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 42: Michael Smith / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Michael Smith', 'michael.smith@demo.local', 'PAYMENT_FAILED', 1449.99, NOW() - INTERVAL '53 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('DualSense Wireless Controller', 1, 1699.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'FAILED', 1449.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'michael.smith@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '53 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'michael.smith@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '53 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 43: Jessica Taylor / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Jessica Taylor', 'jessica.taylor@demo.local', 'PAYMENT_FAILED', 2374.99, NOW() - INTERVAL '54 days', 'VIP5', 125.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Samsung T7 Shield 1TB SSD', 1, 2499.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'FAILED', 2374.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'jessica.taylor@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '54 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'jessica.taylor@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '54 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 44: Ryan Cooper / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Ryan Cooper', 'ryan.cooper@demo.local', 'PAYMENT_FAILED', 17649.96, NOW() - INTERVAL '55 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Ergonomic Mesh Office Chair', 1, 3999.99),
    ('BenQ MOBIUZ EX2710', 1, 5299.99),
    ('Keychron Q1 Max Keyboard', 2, 4299.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'FAILED', 17649.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'ryan.cooper@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '55 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'ryan.cooper@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '55 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 45: Lauren Adams / PAYMENT_FAILED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Lauren Adams', 'lauren.adams@demo.local', 'PAYMENT_FAILED', 22799.97, NOW() - INTERVAL '56 days', 'VIP5', 1200.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Dell UltraSharp U2724D', 3, 7999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'FAILED', 22799.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'lauren.adams@demo.local'
  AND o.status = 'PAYMENT_FAILED'
  AND o.created_at = NOW() - INTERVAL '56 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'lauren.adams@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '56 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 46: Brandon Young / CANCELLED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Brandon Young', 'brandon.young@demo.local', 'CANCELLED', 15104.97, NOW() - INTERVAL '57 days', 'VIP5', 795.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('BenQ MOBIUZ EX2710', 3, 5299.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'brandon.young@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '57 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 47: Nicole Bennett / CANCELLED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Nicole Bennett', 'nicole.bennett@demo.local', 'CANCELLED', 29259.95, NOW() - INTERVAL '58 days', 'VIP5', 1540.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('North Bay Monitor Arm', 1, 1299.99),
    ('Electric Standing Desk', 3, 8499.99),
    ('SteelSeries Arctis Nova 7', 1, 3999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'nicole.bennett@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '58 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 48: Jason Clark / CANCELLED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Jason Clark', 'jason.clark@demo.local', 'CANCELLED', 23799.94, NOW() - INTERVAL '59 days', 'FIRSTBUY', 100.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('TP-Link 24-Port Gigabit Switch', 3, 2999.99),
    ('BenQ MOBIUZ EX2710', 2, 5299.99),
    ('Netgear Nighthawk AX5400', 1, 4299.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'FIRSTBUY'
WHERE o.customer_email = 'jason.clark@demo.local'
  AND o.coupon_code = 'FIRSTBUY'
  AND o.created_at = NOW() - INTERVAL '59 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 49: Sarah Johnson / CANCELLED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Sarah Johnson', 'sarah.johnson@demo.local', 'CANCELLED', 9699.98, NOW() - INTERVAL '60 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Logitech G923 Racing Wheel', 1, 7999.99),
    ('DualSense Wireless Controller', 1, 1699.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;

-- Order 50: David Wilson / CANCELLED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('David Wilson', 'david.wilson@demo.local', 'CANCELLED', 3999.99, NOW() - INTERVAL '61 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('SteelSeries Apex Pro TKL', 1, 3999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;

-- Order 51: Emma Brown / CANCELLED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Emma Brown', 'emma.brown@demo.local', 'CANCELLED', 4464.98, NOW() - INTERVAL '62 days', 'VIP5', 235.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('BenQ ScreenBar Desk Lamp', 1, 1799.99),
    ('Logitech Brio 4K Webcam', 1, 2899.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'VIP5'
WHERE o.customer_email = 'emma.brown@demo.local'
  AND o.coupon_code = 'VIP5'
  AND o.created_at = NOW() - INTERVAL '62 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 52: Chris Miller / CANCELLED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Chris Miller', 'chris.miller@demo.local', 'CANCELLED', 5999.97, NOW() - INTERVAL '63 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Xbox Wireless Controller', 1, 1499.99),
    ('Razer RGB Mouse Pad', 1, 999.99),
    ('Elgato Stream Deck MK.2', 1, 3499.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;

-- Order 53: John Peterson / PENDING
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('John Peterson', 'john.peterson@demo.local', 'PENDING', 17899.97, NOW() - INTERVAL '64 days', 'FIRSTBUY', 100.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('SteelSeries Apex Pro TKL', 1, 3999.99),
    ('ASUS ProArt Display PA278QV', 2, 6999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'PAYFAST', 'PENDING', 17899.97, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'john.peterson@demo.local'
  AND o.status = 'PENDING'
  AND o.created_at = NOW() - INTERVAL '64 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'FIRSTBUY'
WHERE o.customer_email = 'john.peterson@demo.local'
  AND o.coupon_code = 'FIRSTBUY'
  AND o.created_at = NOW() - INTERVAL '64 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 54: Rebecca Jones / PENDING
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Rebecca Jones', 'rebecca.jones@demo.local', 'PENDING', 349.99, NOW() - INTERVAL '65 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('UGREEN Cable Organizer Kit', 1, 349.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'BANK_TRANSFER', 'PENDING', 349.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'rebecca.jones@demo.local'
  AND o.status = 'PENDING'
  AND o.created_at = NOW() - INTERVAL '65 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 55: Daniel Scott / PENDING
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Daniel Scott', 'daniel.scott@demo.local', 'PENDING', 3999.99, NOW() - INTERVAL '66 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Elgato HD60 X Capture Card', 1, 3999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'CARD', 'PENDING', 3999.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'daniel.scott@demo.local'
  AND o.status = 'PENDING'
  AND o.created_at = NOW() - INTERVAL '66 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 56: Ashley Walker / PENDING
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Ashley Walker', 'ashley.walker@demo.local', 'PENDING', 749.99, NOW() - INTERVAL '67 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Razer RGB Mouse Pad', 1, 999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'EFT', 'PENDING', 749.99, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'ashley.walker@demo.local'
  AND o.status = 'PENDING'
  AND o.created_at = NOW() - INTERVAL '67 days'
ORDER BY o.id DESC LIMIT 1;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'ashley.walker@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '67 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 57: Michael Smith / PENDING
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Michael Smith', 'michael.smith@demo.local', 'PENDING', 11699.96, NOW() - INTERVAL '68 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Blue Yeti USB Microphone', 1, 2999.99),
    ('WD Black SN850X 1TB', 3, 2899.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO payments (order_id, payment_method, status, amount, paid_at)
SELECT o.id, 'VIRTUAL_CARD', 'PENDING', 11699.96, o.created_at + INTERVAL '5 minutes'
FROM customer_orders o
WHERE o.customer_email = 'michael.smith@demo.local'
  AND o.status = 'PENDING'
  AND o.created_at = NOW() - INTERVAL '68 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 58: Jessica Taylor / CREATED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Jessica Taylor', 'jessica.taylor@demo.local', 'CREATED', 32249.95, NOW() - INTERVAL '69 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('AOC 27G2 Gaming Monitor', 2, 4999.99),
    ('Bose QuietComfort Headphones', 1, 6499.99),
    ('Logitech G923 Racing Wheel', 2, 7999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'jessica.taylor@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '69 days'
ORDER BY o.id DESC LIMIT 1;

-- Order 59: Ryan Cooper / CREATED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Ryan Cooper', 'ryan.cooper@demo.local', 'CREATED', 12999.97, NOW() - INTERVAL '70 days', NULL, 0.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('AOC 27G2 Gaming Monitor', 1, 4999.99),
    ('SteelSeries Apex Pro TKL', 2, 3999.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;

-- Order 60: Lauren Adams / CREATED
WITH new_order AS (
    INSERT INTO customer_orders (customer_name, customer_email, status, total_amount, created_at, coupon_code, discount_amount)
    VALUES ('Lauren Adams', 'lauren.adams@demo.local', 'CREATED', 549.99, NOW() - INTERVAL '71 days', 'WELCOME250', 250.00)
    RETURNING id
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT new_order.id, p.id, v.quantity, v.unit_price
FROM new_order
JOIN (VALUES
    ('Belkin Wireless Charging Pad', 1, 799.99)
) AS v(product_name, quantity, unit_price) ON TRUE
JOIN products p ON p.name = v.product_name;
INSERT INTO coupon_redemptions (coupon_id, user_id, order_id, redeemed_at)
SELECT c.id, u.id, o.id, o.created_at
FROM customer_orders o
JOIN users u ON u.email = o.customer_email
JOIN coupons c ON c.code = 'WELCOME250'
WHERE o.customer_email = 'lauren.adams@demo.local'
  AND o.coupon_code = 'WELCOME250'
  AND o.created_at = NOW() - INTERVAL '71 days'
ORDER BY o.id DESC LIMIT 1;

-- Keep serial sequences aligned after cleanup and inserts.
SELECT setval(pg_get_serial_sequence('products', 'id'), COALESCE((SELECT MAX(id) FROM products), 1), true);
SELECT setval(pg_get_serial_sequence('inventory', 'id'), COALESCE((SELECT MAX(id) FROM inventory), 1), true);
SELECT setval(pg_get_serial_sequence('customer_orders', 'id'), COALESCE((SELECT MAX(id) FROM customer_orders), 1), true);
SELECT setval(pg_get_serial_sequence('order_items', 'id'), COALESCE((SELECT MAX(id) FROM order_items), 1), true);
SELECT setval(pg_get_serial_sequence('payments', 'id'), COALESCE((SELECT MAX(id) FROM payments), 1), true);
SELECT setval(pg_get_serial_sequence('coupons', 'id'), COALESCE((SELECT MAX(id) FROM coupons), 1), true);
SELECT setval(pg_get_serial_sequence('coupon_redemptions', 'id'), COALESCE((SELECT MAX(id) FROM coupon_redemptions), 1), true);
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 1), true);

-- Final safety check for optimistic locking.
UPDATE inventory SET version = 0 WHERE version IS NULL;