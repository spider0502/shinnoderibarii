# ER図

ER図のDBMLは [`db/schema.dbml`](../db/schema.dbml) に保存しています。

dbdiagram.ioにDBMLを貼り付けることでER図を確認できます。

## エンティティ

- users
- stores
- categories
- products
- carts
- cart_items
- order_groups
- orders
- order_items
- deliveries

## 主要な関係

```text
users 1 --- 1 carts
users 1 --- N order_groups
order_groups 1 --- N orders
stores 1 --- N orders
orders 1 --- N order_items
orders 1 --- 1 deliveries
stores 1 --- N categories
categories 1 --- N products
carts 1 --- N cart_items
products 1 --- N cart_items
products 1 --- N order_items
users 1 --- N deliveries
```

## 設計上のポイント

### 複数店舗カート

カートには複数店舗の商品を追加できるため、cartsテーブルにはstore_idを持たせない。

### OrderGroupとOrderの分離

1回の注文確定をOrderGroup、店舗ごとの注文をOrderとして管理する。

### 注文履歴の保持

注文時点の商品名・単価・配送先住所を注文関連テーブルに保存し、マスタ変更によって過去の注文情報が変わらないようにする。
