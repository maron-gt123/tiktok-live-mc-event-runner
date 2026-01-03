import time
import requests
import traceback
import yaml
import random

# =====================
# 設定読み込み
# =====================
with open("config/config.yaml", "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

ENDPOINTS = config.get("sender", {}).get("endpoints", [])
HTTP_TIMEOUT = config.get("http", {}).get("timeout", 2)

if not ENDPOINTS:
    raise RuntimeError("sender.endpoints is not set in config.yaml")

print("Send Targets:")
for ep in ENDPOINTS:
    print(" -", ep.get("name"), "=>", ep.get("url"))

# =====================
# HTTP送信関数
# =====================
def send_to_targets(event_type: str, data: dict):
    payload = {
        "type": event_type,
        "timestamp": int(time.time()),
        "data": data,
    }

    for ep in ENDPOINTS:
        try:
            print(f"[SEND] {event_type} -> {ep['name']}")
            response = requests.post(
                ep["url"],
                json=payload,
                timeout=HTTP_TIMEOUT,
            )
            print(" status:", response.status_code)
        except Exception as e:
            print(f"[SEND ERROR] {ep['name']}:", e)

# =====================
# ダミーイベント定義
# =====================
dummy_events = [
    ("gift", {
        "user": "Alice",
        "gift_id": 1,
        "gift_name": "Rose",
        "diamond": 5,
        "count": 1,
        "repeat_end": True,
    }),
    ("gift", {
        "user": "Bob",
        "gift_id": 2,
        "gift_name": "GG",
        "diamond": 10,
        "count": 1,
        "repeat_end": True,
    }),
    ("share", {
        "user": "Charlie",
    }),
    ("like", {
        "user": "Dave",
        "count": 3,  # いいねの数はランダムに変化させる
    }),
    ("follow", {
        "user": "Eve",
    }),
]

# =====================
# メインループ
# =====================
if __name__ == "__main__":
    try:
        print("\n=== DEBUG SIMULATOR START ===")
        while True:
            for event_type, data in dummy_events:
                # いいねのcountだけランダムに変化
                if event_type == "like":
                    data["count"] = random.randint(1, 5)
                send_to_targets(event_type, data)
                time.sleep(1)  # 本番っぽく1秒待機
    except KeyboardInterrupt:
        print("\nExit requested by user.")
    except Exception:
        print("Unexpected error:")
        traceback.print_exc()
