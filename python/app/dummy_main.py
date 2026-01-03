import json
import time
import websocket
import yaml

# === config.yaml 読み込み ===
with open("config/config.yaml", "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

WS_URL = config.get("ws_url", "ws://127.0.0.1:12345")
USER_ID = config.get("user_id", "dummy_user")
USER_NICK = config.get("user_nick", "DummyMC")

# === ダミーイベント作成関数 ===
def create_dummy_event(event_type, **kwargs):
    """
    本番用 JSON フォーマットに合わせたダミーイベントを生成
    """
    event = {
        "type": event_type,
        "user_unique_id": USER_ID,
        "user_nickname": USER_NICK,
        "timestamp": int(time.time())
    }
    event.update(kwargs)
    return event

# === ダミー連続送信処理 ===
def run_dummy():
    ws = websocket.WebSocket()
    ws.connect(WS_URL)
    print(f"Dummy mode connected to WebSocket: {WS_URL}")

    try:
        while True:
            # 1. ライク10回
            for _ in range(10):
                evt = create_dummy_event("like", count=1)
                ws.send(json.dumps(evt))
                print(f"Sent LIKE: {evt}")
                time.sleep(0.2)

            # 2. バラ（ローズ）
            evt = create_dummy_event("gift", gift_name="rose", gift_amount=1)
            ws.send(json.dumps(evt))
            print(f"Sent GIFT: {evt}")
            time.sleep(0.2)

            # 3. コメント（gg）
            evt = create_dummy_event("comment", comment="gg")
            ws.send(json.dumps(evt))
            print(f"Sent COMMENT: {evt}")
            time.sleep(0.2)

            # 4. フォロー
            evt = create_dummy_event("follow")
            ws.send(json.dumps(evt))
            print(f"Sent FOLLOW: {evt}")
            time.sleep(0.2)

            # 5. シェア
            evt = create_dummy_event("share")
            ws.send(json.dumps(evt))
            print(f"Sent SHARE: {evt}")
            time.sleep(0.2)

    except KeyboardInterrupt:
        print("Dummy mode stopped by user")
    finally:
        ws.close()

# === メイン ===
if __name__ == "__main__":
    run_dummy()
