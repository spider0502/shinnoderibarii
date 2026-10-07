# データベース設計

DBMS: MySQL

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
| name | VARCHAR(100) | NO | |
| description | TEXT | YES | |
| address | VARCHAR(255) | NO | |
| phone | VARCHAR(20) | YES | |
| status | VARCHAR(30) | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

## categories

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| store_id | BIGINT | NO | FK |
| name | VARCHAR(100) | NO | |
| sort_order | INT | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |
| deleted_at | DATETIME | YES | |

## products

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| store_id | BIGINT | NO | FK |
| category_id | BIGINT | NO | FK |
| name | VARCHAR(100) | NO | |
| description | TEXT | YES | |
| price | DECIMAL(10,2) | NO | |
| image_url | VARCHAR(500) | YES | |
| status | VARCHAR(30) | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |
| deleted_at | DATETIME | YES | |

## carts

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| user_id | BIGINT | NO | FK / UNIQUE |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

## cart_items

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| cart_id | BIGINT | NO | FK |
| product_id | BIGINT | NO | FK |
| quantity | INT | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

UNIQUE制約:

```text
(cart_id, product_id)
```

## order_groups

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| user_id | BIGINT | NO | FK |
| total_amount | DECIMAL(10,2) | NO | |
| created_at | DATETIME | NO | |

## orders

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| order_group_id | BIGINT | NO | FK |
| store_id | BIGINT | NO | FK |
| status | VARCHAR(30) | NO | |
| total_amount | DECIMAL(10,2) | NO | |
| delivery_postal_code | VARCHAR(10) | NO | |
| delivery_address | VARCHAR(255) | NO | |
| created_at | DATETIME | NO | |
| updated_at | DATETIME | NO | |

## order_items

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| order_id | BIGINT | NO | FK |
| product_id | BIGINT | NO | FK |
| product_name | VARCHAR(100) | NO | |
| unit_price | DECIMAL(10,2) | NO | |
| quantity | INT | NO | |
| subtotal | DECIMAL(10,2) | NO | |
| created_at | DATETIME | NO | |

`product_name` と `unit_price` は注文時点のスナップショットとして保存する。

## deliveries

| カラム | 型 | NULL | 制約 |
|---|---|---|---|
| id | BIGINT | NO | PK |
| order_id | BIGINT | NO | FK / UNIQUE |
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
