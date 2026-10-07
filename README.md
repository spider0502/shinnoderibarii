# 宅配注文管理システム

飲食店の商品をオンラインで注文し、指定した住所まで配達してもらうためのWebアプリケーションです。

## 目的

日本のWeb系・業務系システム開発で使用される設計・実装プロセスを学習し、Java / Spring Bootを中心としたポートフォリオとして公開することを目的とします。

## 主な機能

### 利用者
- ユーザー登録・ログイン
- 店舗検索
- 商品検索・詳細確認
- カート管理
- 複数店舗の商品をまとめてカートに追加
- 注文確定
- 注文履歴・注文詳細確認
- 注文キャンセル
- ユーザー情報変更

### 店舗スタッフ
- ログイン
- カテゴリ管理
- 商品管理
- 商品の販売状態変更
- 注文管理
- 調理ステータス更新

### 配達員
- ログイン
- 配達待ち注文確認
- 配達開始
- 配達完了

## 技術スタック

- Java 21
- Spring Boot 3
- Spring Security
- MyBatis
- MySQL
- Redis
- Vue 3
- TypeScript
- Vite
- Element Plus
- Axios
- JUnit 5
- Mockito
- Docker / Docker Compose
- GitHub Actions

## ドキュメント

- [要件定義書](docs/01_requirements.md)
- [業務ルール](docs/02_business_rules.md)
- [ユースケース一覧](docs/03_use_cases.md)
- [画面一覧](docs/04_screen_list.md)
- [データベース設計](docs/05_database_design.md)
- [ER図](docs/06_er_diagram.md)

## 開発状況

現在は要件定義・基本設計フェーズです。

- [x] 要件定義
- [x] 業務ルール整理
- [x] ユースケース整理
- [x] 画面一覧整理
- [x] DB設計
- [x] ER図
- [ ] Spring Bootプロジェクト作成
- [ ] API実装
- [ ] フロントエンド実装
- [ ] テスト
- [ ] Docker環境構築
- [ ] CI/CD
