import time
import yaml
import json
import os
import requests
from pathlib import Path

def run_dummy():
    # =====================
    # config.yaml を読み込む（main.py / wrapper.py と同じ方法）
    # =====================
    CONFIG_PATH = Path(__file__).parent / "config" / "config.yaml"
    with open(CONFIG_PATH, "r", encoding="utf-8") as f:
        config = yaml.safe_load(f)

    endpoints = config["sender"]["endpoints"]
    http_timeout = config.get("http", {}).get("timeout", 2)

    USER_ID = config.get("user_id", "dummy_user")
    USER_NICK = config.get("user_nick", "DummyMC")

    print(f"[DUMMY] Starting dummy event sender as {USER_ID} ({USER_NICK})")

    try:
        while True:
            # 1. いいね10回
            for _ in range(10):
                evt = {
                    "type": "like",
                    "user_unique_id": USER_ID,
                    "user_nickname": USER_NICK,
                    "timestamp": int(time.time()),
                    "count": 1
                }
                for ep in endpoints:
                    try:
                        res = requests.post(ep["url"], json=evt, timeout=http_timeout)
                        print(f"[DUMMY] Sent LIKE to {ep['name']}: {res.status_code}")
                    except Exception as e:
                        print(f"[DUMMY] Failed LIKE to {ep['name']}: {e}")
                time.sleep(0.2)

            # 2. ギフト（rose）
            evt = {
                "type": "gift",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time()),
                "gift_name": "rose",
                "gift_amount": 1
            }
            for ep in endpoints:
                try:
                    res = requests.post(ep["url"], json=evt, timeout=http_timeout)
                    print(f"[DUMMY] Sent GIFT to {ep['name']}: {res.status_code}")
                except Exception as e:
                    print(f"[DUMMY] Failed GIFT to {ep['name']}: {e}")
            time.sleep(0.2)

            # 3. コメント（gg）
            evt = {
                "type": "comment",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time()),
                "comment": "gg"
            }
            for ep in endpoints:
                try:
                    res = requests.post(ep["url"], json=evt, timeout=http_timeout)
                    print(f"[DUMMY] Sent COMMENT to {ep['name']}: {res.status_code}")
                except Exception as e:
                    print(f"[DUMMY] Failed COMMENT to {ep['name']}: {e}")
            time.sleep(0.2)

            # 4. フォロー
            evt = {
                "type": "follow",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time())
            }
            for ep in endpoints:
                try:
                    res = requests.post(ep["url"], json=evt, timeout=http_timeout)
                    print(f"[DUMMY] Sent FOLLOW to {ep['name']}: {res.status_code}")
                except Exception as e:
                    print(f"[DUMMY] Failed FOLLOW to {ep['name']}: {e}")
            time.sleep(0.2)

            # 5. シェア
            evt = {
                "type": "share",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time())
            }
            for ep in endpoints:
                try:
                    res = requests.post(ep["url"], json=evt, timeout=http_timeout)
                    print(f"[DUMMY] Sent SHARE to {ep['name']}: {res.status_code}")
                except Exception as e:
                    print(f"[DUMMY] Failed SHARE to {ep['name']}: {e}")
            time.sleep(0.2)

    except KeyboardInterrupt:
        print("[DUMMY] Dummy mode stopped by user")

if __name__ == "__main__":
    run_dummy()
