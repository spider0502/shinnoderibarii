# データベース設計

DBMS: MySQL 8.4

本書では、現在のDBスキーマと、今後の注文機能で使用する設計を区別して記載する。

## users

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| name | VARCHAR(100) | NO | |
| email | VARCHAR(255) | NO | UNIQUE |
| password | VARCHAR(255) | NO | |
| phone | VARCHAR(20) | YES | |
| role | VARCHAR(30) | NO | |
| address | VARCHAR(255) | YES | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

## stores

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| code | VARCHAR(50) | NO | UNIQUE |
| name | VARCHAR(100) | NO | |
| description | TEXT | YES | |
| address | VARCHAR(255) | NO | |
| phone | VARCHAR(20) | YES | |
| status | VARCHAR(30) | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

`status` は少なくとも `OPEN`（営業中）と `CLOSED`（休業中）を扱う。

## categories

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| store_id | BIGINT | NO | FK → stores.id |
| name | VARCHAR(100) | NO | |
| sort_order | INT | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |
| deleted_at | DATETIME | YES | |

## products

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| store_id | BIGINT | NO | FK → stores.id |
| category_id | BIGINT | NO | FK → categories.id |
| name | VARCHAR(100) | NO | |
| description | TEXT | YES | |
| price | DECIMAL(10,2) | NO | |
| image_url | VARCHAR(500) | YES | |
| status | VARCHAR(30) | NO | |
| sort_order | INT | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |
| deleted_at | DATETIME | YES | |

`status` は `ON_SALE`（販売中）などの商品販売状態を表す。

## carts

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| user_id | BIGINT | NO | FK → users.id / UNIQUE |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

## cart_items

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| cart_id | BIGINT | NO | FK → carts.id |
| product_id | BIGINT | NO | FK → products.id |
| quantity | INT | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

UNIQUE制約：

```text
(cart_id, product_id)
```

カート内の商品がどの店舗に属するかは、`cart_items.product_id` → `products.store_id` → `stores.id` の関連から取得する。`cart_items` に `store_id` を重複保持しない。

カート取得APIでは、画面表示に必要な以下の店舗情報を商品情報とともに返す方針とする。

- `storeId`
- `storeName`
- `storeStatus`

これらはレスポンス用DTOの項目であり、現時点で `cart_items` テーブルに追加するカラムではない。Mapperで `products` と `stores` をJOINして取得する。

## order_groups（注文機能実装時）

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| user_id | BIGINT | NO | FK → users.id |
| total_amount | DECIMAL(10,2) | NO | |
| created_at | DATETIME | NO | |

## orders（注文機能実装時）

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| order_group_id | BIGINT | NO | FK → order_groups.id |
| store_id | BIGINT | NO | FK → stores.id |
| status | VARCHAR(30) | NO | |
| total_amount | DECIMAL(10,2) | NO | |
| delivery_postal_code | VARCHAR(10) | NO | |
| delivery_address | VARCHAR(255) | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

## order_items（注文機能実装時）

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| order_id | BIGINT | NO | FK → orders.id |
| product_id | BIGINT | NO | FK → products.id |
| product_name | VARCHAR(100) | NO | |
| unit_price | DECIMAL(10,2) | NO | |
| quantity | INT | NO | |
| subtotal | DECIMAL(10,2) | NO | |
| created_at | DATETIME | NO | |

`product_name` と `unit_price` は注文時点のスナップショットとして保存する。

## deliveries（配送機能実装時）

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| order_id | BIGINT | NO | FK → orders.id / UNIQUE |
| delivery_staff_id | BIGINT | NO | FK → users.id |
| status | VARCHAR(30) | NO | |
| started_at | DATETIME | YES | |
| completed_at | DATETIME | YES | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

## 主なインデックス
- order_groups.user_id
- orders.order_group_id
- orders.store_id
- products.store_id
- products.category_id
- cart_items.cart_id
- order_items.order_id
- deliveries.delivery_staff_id

## 実装状況
- 現在のDBに作成済み：`users`、`stores`、`categories`、`products`、`carts`、`cart_items`
- 今後の注文・配送機能用の設計：`order_groups`、`orders`、`order_items`、`deliveries`
- 上記の注文・配送テーブルは設計済みであり、現時点で実装済みとは扱わない。
