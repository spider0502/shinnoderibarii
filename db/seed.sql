SET
    NAMES utf8mb4;

INSERT INTO
    stores (code, name, description, address, phone, status)
VALUES
    (
        'TOKYO_CURRY',
        '東京カレー屋',
        'スパイスにこだわったカレー専門店',
        '東京都中野区中野1-1-1',
        '03-1234-5678',
        'OPEN'
    ),
    (
        'NAKANO_RAMEN',
        '中野ラーメン',
        '豚骨スープが自慢のラーメン店',
        '東京都中野区中野2-2-2',
        '03-2345-6789',
        'CLOSED'
    ) ON DUPLICATE KEY
UPDATE
    name =
VALUES
    (name),
    description =
VALUES
    (description),
    address =
VALUES
    (address),
    phone =
VALUES
    (phone),
    status =
VALUES
    (status);

INSERT INTO
    categories (store_id, name, sort_order)
SELECT
    s.id,
    seed.name,
    seed.sort_order
FROM
    stores s
    JOIN (
        SELECT
            'TOKYO_CURRY' AS store_code,
            'カレー' AS name,
            1 AS sort_order
        UNION
        ALL
        SELECT
            'TOKYO_CURRY',
            'サイドメニュー',
            2
        UNION
        ALL
        SELECT
            'NAKANO_RAMEN',
            'ラーメン',
            1
        UNION
        ALL
        SELECT
            'NAKANO_RAMEN',
            'トッピング',
            2
    ) AS seed ON seed.store_code = s.code ON DUPLICATE KEY
UPDATE
    sort_order = seed.sort_order;

INSERT INTO
    products (
        store_id,
        category_id,
        name,
        description,
        price,
        status,
        sort_order
    )
SELECT
    s.id,
    c.id,
    seed.name,
    seed.description,
    seed.price,
    seed.status,
    seed.sort_order
FROM
    (
        SELECT
            'TOKYO_CURRY' AS store_code,
            'カレー' AS category_name,
            'チキンカレー' AS name,
            'スパイス香る定番カレー' AS description,
            980.00 AS price,
            'ON_SALE' AS status,
            1 AS sort_order
        UNION
        ALL
        SELECT
            'TOKYO_CURRY',
            'カレー',
            '野菜カレー',
            '野菜たっぷりのカレー',
            1080.00,
            'ON_SALE',
            2
        UNION
        ALL
        SELECT
            'TOKYO_CURRY',
            'サイドメニュー',
            'ラッシー',
            'さっぱりとしたヨーグルトドリンク',
            300.00,
            'ON_SALE',
            1
        UNION
        ALL
        SELECT
            'NAKANO_RAMEN',
            'ラーメン',
            '豚骨ラーメン',
            '濃厚な豚骨スープ',
            950.00,
            'ON_SALE',
            1
        UNION
        ALL
        SELECT
            'NAKANO_RAMEN',
            'トッピング',
            '味玉',
            '味付け半熟卵',
            150.00,
            'ON_SALE',
            1
    ) AS seed
    JOIN stores s ON s.code = seed.store_code
    JOIN categories c ON c.store_id = s.id
    AND c.name = seed.category_name ON DUPLICATE KEY
UPDATE
    category_id = c.id,
    description = seed.description,
    price = seed.price,
    status = seed.status,
    sort_order = seed.sort_order;