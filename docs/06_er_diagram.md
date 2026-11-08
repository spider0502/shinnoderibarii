# ER図

ER図のDBMLは [`db/schema.dbml`](../db/schema.dbml) に保存しています。

dbdiagram.ioにDBMLを貼り付けることでER図を確認できます。

## エンティティ

### 現在DBに作成済み
- users
- stores
- categories
- products
- carts
- cart_items

### 今後の機能で使用する設計
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
カートには複数店舗の商品を追加できるため、`carts` テーブルには `store_id` を持たせない。

商品所属店舗は `cart_items` → `products` → `stores` の関連から取得する。カート画面で店舗状態を表示するため、カート取得時に `stores` をJOINして店舗ID・店舗名・店舗状態を取得する方針とする。

### 休業中店舗の商品
休業中（`CLOSED`）の商品もカートに追加できる。ただし、注文時の商品選択では選択不可とする。店舗状態はカート画面の表示だけでなく、注文確定APIでも再確認する。

### OrderGroupとOrderの分離
1回の注文確定を `OrderGroup`、店舗ごとの注文を `Order` として管理する。複数店舗の商品を一括注文する場合、店舗ごとに `Order` を作成し、同じ `OrderGroup` に紐付ける。

### 注文履歴の保持
注文時点の商品名・単価・配送先住所を注文関連テーブルに保存し、マスタ変更によって過去の注文情報が変わらないようにする。

## 実装状況
現時点では、ER図に含まれるエンティティのうち `users`、`stores`、`categories`、`products`、`carts`、`cart_items` がDBに作成済み。注文・配送関連テーブルは設計段階であり、今後実装する。
