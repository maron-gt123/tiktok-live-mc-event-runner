import time
import traceback
import requests
import yaml
from collections import defaultdict
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
with open("config/config.yaml", "r", encoding="utf-8") as f:
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
# ユーザーごとの Like 分計用
# =====================
like_map = defaultdict(int)  # {user_id: like_count}
last_seen_map = {}           # {user_id: last_event_time}
CLEANUP_INTERVAL = 60        # 古いユーザー削除のチェック間隔（秒）
INACTIVE_TIMEOUT = 120       # 一定時間イベントなしのユーザーは削除（秒）
last_cleanup_time = time.time()
overallLikes = 0             # 配信全体いいね総数

def cleanup_inactive_users():
    global last_cleanup_time
    now = time.time()
    # CLEANUP_INTERVALごとに実行
    if now - last_cleanup_time < CLEANUP_INTERVAL:
        return
    last_cleanup_time = now

    to_delete = [uid for uid, t in last_seen_map.items() if now - t > INACTIVE_TIMEOUT]
    for uid in to_delete:
        like_map.pop(uid, None)
        last_seen_map.pop(uid, None)

def reset_like_counts():
    """配信開始時や必要に応じて全リセット"""
    global like_map, last_seen_map, overallLikes
    like_map.clear()
    last_seen_map.clear()
    overallLikes = 0

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
                print("[GIFT]", event.user.unique_id, event.gift.name, "x", event.repeat_count)

                send_to_targets(
                    "gift",
                    {
                        "user": event.user.unique_id,
                        "nickname": event.user.nickname,
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
            global overallLikes
            try:
                uid = event.user.unique_id
                nickname = event.user.nickname
                like_map[uid] += 1
                last_seen_map[uid] = time.time()
                total_likes = like_map[uid]
                unique_users = len(like_map)
                overallLikes += 1

                print(f"[LIKE] {nickname} ({uid}) total this session: {total_likes} | unique users: {unique_users} | overall likes: {overall_likes}")
                send_to_targets(
                    "like",
                    {
                        "user": uid,
                        "nickname": nickname,
                        "total_likes": total_likes,
                        "uniqueUserCount": unique_users,
                        "overallLikes": overallLikes,
                    },
                )
                # 古いユーザーを削除
                cleanup_inactive_users()

            except Exception:
                print("Like handler error:")
                traceback.print_exc()

        # =====================
        # フォロー
        # =====================
        @client.on(FollowEvent)
        async def on_follow(event: FollowEvent):
            try:
                print("[FOLLOW]", event.user.unique_id,"(" + event.user.nickname + ")",)

                send_to_targets(
                    "follow",
                    {
                        "user": event.user.unique_id,
                        "nickname": event.user.nickname,
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
                print("[SHARE]", event.user.unique_id,"(" + event.user.nickname + ")",)

                send_to_targets(
                    "share",
                    {
                        "user": event.user.unique_id,
                        "nickname": event.user.nickname,
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
                print("[SUBSCRIBE]", event.user.unique_id, "(" + event.user.nickname + ")")

                send_to_targets(
                    "subscribe",
                    {
                        "user": event.user.unique_id,
                        "nickname": event.user.nickname,
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
