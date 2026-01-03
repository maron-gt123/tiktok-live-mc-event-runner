import time
import json
import requests
import yaml
from pathlib import Path

CONFIG_PATH = Path("/app/config/config.yaml")
with open(CONFIG_PATH, "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

http_timeout = config.get("http", {}).get("timeout", 2)
USER_ID = config.get("tiktok", {}).get("user", "dummy_user")
USER_NICK = "DummyMC"
endpoints = config.get("sender", {}).get("endpoints", [])

print("[DUMMY] Loaded endpoints:")
for ep in endpoints:
    print(f" - {ep['name']}: {ep['url']}")

def send_event(evt):
    for ep in endpoints:
        try:
            res = requests.post(ep["url"], json=evt, timeout=http_timeout)
            res.raise_for_status()
            print(f"[DUMMY] Sent {evt['type']} to {ep['name']}")
        except Exception as e:
            print(f"[DUMMY] Failed {evt['type']} to {ep['name']}: {e}")

def run_dummy():
    print(f"[DUMMY] Running dummy events as {USER_ID} ({USER_NICK})")
    try:
        while True:
            # LIKE x10
            for _ in range(10):
                evt = {
                    "type": "like",
                    "user_unique_id": USER_ID,
                    "user_nickname": USER_NICK,
                    "timestamp": int(time.time()),
                    "count": 1
                }
                send_event(evt)
                time.sleep(0.2)

            # GIFT
            evt = {
                "type": "gift",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time()),
                "gift_name": "rose",
                "gift_amount": 1
            }
            send_event(evt)
            time.sleep(0.2)

            # COMMENT
            evt = {
                "type": "comment",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time()),
                "comment": "gg"
            }
            send_event(evt)
            time.sleep(0.2)

            # FOLLOW
            evt = {
                "type": "follow",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time())
            }
            send_event(evt)
            time.sleep(0.2)

            # SHARE
            evt = {
                "type": "share",
                "user_unique_id": USER_ID,
                "user_nickname": USER_NICK,
                "timestamp": int(time.time())
            }
            send_event(evt)
            time.sleep(0.2)

    except KeyboardInterrupt:
        print("[DUMMY] Stopped by user")
