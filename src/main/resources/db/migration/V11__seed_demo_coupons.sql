INSERT INTO coupons (
    code,
    type,
    value,
    active,
    expires_at
)
VALUES
    (
        'SAVE10',
        'PERCENTAGE',
        10.00,
        TRUE,
        '2030-12-31T23:59:59Z'
    ),
    (
        'WELCOME250',
        'FIXED_AMOUNT',
        250.00,
        TRUE,
        '2030-12-31T23:59:59Z'
    )
ON CONFLICT (code) DO UPDATE
SET
    type = EXCLUDED.type,
    value = EXCLUDED.value,
    active = EXCLUDED.active,
    expires_at = EXCLUDED.expires_at;