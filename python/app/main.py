import time
import traceback
import requests
import yaml

from TikTokLive import TikTokLiveClient
from TikTokLive.events import (
    GiftEvent,
    LikeEvent,
    FollowEvent,
    ShareEvent,
    SubscribeEvent,
)
from TikTokLive.client.errors import UserOfflineError

# =====================
# config.yaml 読み込み
# =====================
with open("config.yaml", "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

TIKTOK_USER = config["tiktok"]["user"]
ENDPOINTS = config["sender"]["endpoints"]
HTTP_TIMEOUT = config.get("http", {}).get("timeout", 2)

if not TIKTOK_USER:
    raise RuntimeError("tiktok.user is not set in config.yaml")

print("TikTok User:", TIKTOK_USER)
print("Send Targets:")
for ep in ENDPOINTS:
    print(" -", ep["name"], "=>", ep["url"])

# =====================
# HTTP送信（複数送信）
# =====================
def send_to_targets(event_type: str, data: dict):
    payload = {
        "type": event_type,
        "timestamp": int(time.time()),
        "data": data,
    }

    for ep in ENDPOINTS:
        try:
            requests.post(
                ep["url"],
                json=payload,
                timeout=HTTP_TIMEOUT,
            )
        except Exception as e:
            print(f"[SEND ERROR] {ep['name']}:", e)

# =====================
# メインループ（再接続対応）
# =====================
while True:
    try:
        print("\n=== CONNECTING TO TIKTOK LIVE ===")

        # 再接続ごとに client を作り直す
        client = TikTokLiveClient(unique_id=TIKTOK_USER)

        # =====================
        # ギフト
        # =====================
        @client.on(GiftEvent)
        async def on_gift(event: GiftEvent):
            try:
                print("[GIFT]")
                print(" user:", event.user.unique_id)
                print(" gift_id:", event.gift.id)
                print(" name:", event.gift.name)
                print(" diamond:", event.gift.diamond_count)
                print(" repeat_count:", event.repeat_count)
                print(" repeat_end:", event.repeat_end)

                send_to_targets(
                    "gift",
                    {
                        "user": event.user.unique_id,
                        "gift_id": event.gift.id,
                        "gift_name": event.gift.name,
                        "diamond": event.gift.diamond_count,
                        "count": event.repeat_count,
                        "repeat_end": event.repeat_end,
                    },
                )
            except Exception:
                print("Gift handler error:")
                traceback.print_exc()

        # =====================
        # いいね
        # =====================
        @client.on(LikeEvent)
        async def on_like(event: LikeEvent):
            try:
                print("[LIKE]", event.user.unique_id)

                send_to_targets(
                    "like",
                    {
                        "user": event.user.unique_id,
                    },
                )
            except Exception:
                print("Like handler error:")
                traceback.print_exc()

        # =====================
        # フォロー
        # =====================
        @client.on(FollowEvent)
        async def on_follow(event: FollowEvent):
            try:
                print("[FOLLOW]", event.user.unique_id)

                send_to_targets(
                    "follow",
                    {
                        "user": event.user.unique_id,
                    },
                )
            except Exception:
                print("Follow handler error:")
                traceback.print_exc()

        # =====================
        # シェア
        # =====================
        @client.on(ShareEvent)
        async def on_share(event: ShareEvent):
            try:
                print("[SHARE]", event.user.unique_id)

                send_to_targets(
                    "share",
                    {
                        "user": event.user.unique_id,
                    },
                )
            except Exception:
                print("Share handler error:")
                traceback.print_exc()

        # =====================
        # サブスク
        # =====================
        @client.on(SubscribeEvent)
        async def on_subscribe(event: SubscribeEvent):
            try:
                print("[SUBSCRIBE]", event.user.unique_id)

                send_to_targets(
                    "subscribe",
                    {
                        "user": event.user.unique_id,
                    },
                )
            except Exception:
                print("Subscribe handler error:")
                traceback.print_exc()

        # =====================
        # 実行
        # =====================
        client.run()

    except UserOfflineError:
        print("User is offline. Retry in 30 seconds.")
        time.sleep(30)

    except KeyboardInterrupt:
        print("Exit requested.")
        break

    except Exception:
        print("Unexpected error:")
        traceback.print_exc()
        time.sleep(5)
