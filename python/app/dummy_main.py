import json
import time
import websocket
import yaml

from pathlib import Path

def run_dummy():
    # config.yaml を読み込む（全ファイル共通の読み方）
    CONFIG_PATH = Path(__file__).parent / "config" / "config.yaml"
    with open(CONFIG_PATH, "r", encoding="utf-8") as f:
        config = yaml.safe_load(f)

    WS_URL = config.get("ws_url", "ws://127.0.0.1:12345")
    USER_ID = "dummy_user"
    USER_NICK = "DummyMC"

    print(f"[DUMMY] Connecting to WebSocket {WS_URL} as {USER_ID} ({USER_NICK})")
    ws = websocket.WebSocket()
    ws.connect(WS_URL)

    try:
        while True:
            # 1. ライク10回
            for _ in range(10):
                evt = {
                    "type": "like",
                    "user_unique_id": USER_ID,
                    "user_nickname": USER_NICK,
                    "timestamp": int(time.time()),
                    "count": 1
                }
                ws.send(json.dumps(evt))
                print(f"Sent LIKE: {evt}")
                time.sleep(0.2)

            # 2. バラ（ローズ）
            evt = {
                "type": "gift",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time()),
                "gift_name": "rose",
                "gift_amount": 1
            }
            ws.send(json.dumps(evt))
            print(f"Sent GIFT: {evt}")
            time.sleep(0.2)

            # 3. コメント（gg）
            evt = {
                "type": "comment",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time()),
                "comment": "gg"
            }
            ws.send(json.dumps(evt))
            print(f"Sent COMMENT: {evt}")
            time.sleep(0.2)

            # 4. フォロー
            evt = {
                "type": "follow",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time())
            }
            ws.send(json.dumps(evt))
            print(f"Sent FOLLOW: {evt}")
            time.sleep(0.2)

            # 5. シェア
            evt = {
                "type": "share",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time())
            }
            ws.send(json.dumps(evt))
            print(f"Sent SHARE: {evt}")
            time.sleep(0.2)

    except KeyboardInterrupt:
        print("Dummy mode stopped by user")
    finally:
        ws.close()
