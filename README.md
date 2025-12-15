## tiktok-live-mc-event-runner

# はじめに
本リポジトリは TikTok ライブ配信をトリガーとして Minecraft にイベントを送信するための基盤 です<br>
主に 「妨害マインクラフト」や「参加型配信」 を実現することを目的としています<br>
TikTok Live 上で発生した以下のようなイベントを検知し、HTTP(JSON) 経由で Minecraft サーバーへリアルタイムに通知します<br>

* 👍 いいね
* 🎁 ギフト
* ➕ フォロー
* 🔁 シェア
* ⭐ サブスク
本プロジェクト自体は ゲームロジックを持ちません<br>
Minecraft 側のプラグインと連携することで、自由に演出・妨害・報酬処理を実装できます<br>

# 特徴
* TikTok Live イベントのリアルタイム取得
* Python 製の軽量イベントリスナー
* HTTP(JSON) によるシンプルな連携方式
* Docker / Kubernetes 対応
* Minecraft 側の実装と完全分離
* Stream To Earn / TikFinity 等の代替・補助用途にも利用可能
