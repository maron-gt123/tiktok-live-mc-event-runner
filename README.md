## tiktok-live-mc-event-runner

# はじめに
本リポジトリは TikTok ライブ配信をトリガーとして Minecraft にイベントを送信するための基盤 です<br>
主に 「妨害マインクラフト」や「参加型配信」 を実現することを目的としています<br>
TikTok Live 上で発生した以下のようなイベントを検知し、HTTP(JSON) 経由で Minecraft サーバーへリアルタイムに通知します<br>

* 👍 いいね
* 🎁 ギフト
* ➕ フォロー
* 🔁 シェア
* ⭐ サブスク<br>

本プロジェクト自体は ゲームロジックを持ちません<br>
Minecraft 側のプラグインと連携することで、自由に演出・妨害・報酬処理を実装できます<br>

# 特徴
* TikTok Live イベントのリアルタイム取得
* Python 製の軽量イベントリスナー
* HTTP(JSON) によるシンプルな連携方式
* Docker / Kubernetes 対応
* Minecraft 側の実装と完全分離
* Stream To Earn / TikFinity 等の代替・補助用途にも利用可能

# 全体構成
      TikTok Live
         ↓
      Python Event Runner
         ↓ HTTP (JSON)
      Minecraft Plugin

この構成により以下を実現しています<br>
* TikTok 側の仕様変更が Minecraft に影響しにくい
* Minecraft 側の妨害ロジックを自由に差し替え可能
* 配信者に Python 環境を要求しない

# 対象ユーザー
* TikTok で参加型 / 妨害系 Minecraft 配信を行いたい配信者
* Minecraft プラグイン開発者
* 自前サーバー・Kubernetes 環境で配信連携を行いたい方
* 商材・イベント企画用の配信基盤を構築したい方

# 本リポジトリで提供するもの
* TikTok Live イベント取得用 Python アプリ
* 設定ファイルによる送信先管理
* Docker イメージ構築用設定
* Kubernetes 配置用マニフェスト（kustomize 対応）

# 提供しないもの
* Minecraft の妨害ロジック
* ワールド操作処理
* ゲームバランス設計
これらは Minecraft プラグイン側で自由に実装 してください<br>

# イベント送信仕様（概要）
Minecraft 側へは以下形式の JSON が送信されます<br>

      {
        "type": "gift",
        "timestamp": 1734250000,
        "data": {
          "user": "example_user",
          "gift_id": 5655,
          "gift_name": "Rose",
          "diamond": 1,
          "count": 1,
          "repeat_end": 1
        }
      }

type によってイベント種別を判別できます<br>

# 想定ユースケース
* ギフトで TNT を落とす
* いいねで Mob をスポーン
* フォローで装備剥奪
* サブスクでバリア付与
* ギフト連打で難易度上昇
すべて Minecraft 側の実装次第 です

# 開発方針
* Python 側は「イベント中継」に徹する
* Minecraft 側にすべてのゲームロジックを集約
* 拡張よりも安定性を優先
* 配信中でも落ちない構成を重視
