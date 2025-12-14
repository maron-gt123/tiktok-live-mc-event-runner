import os
import time
import json
import requests
from TikTokLive import TikTokLiveClient
from TikTokLive.events import (
    Event, CommentEvent, GiftEvent, LikeEvent,
    FollowEvent, ShareEvent, SubscribeEvent, JoinEvent
)
from TikTokLive.client.errors import UserOfflineError

# ===== 設定 =====
TIKTOK_USER = os.environ.get("TIKTOK_USER")
MC_ENDPOINT = os.environ.get(
    "MC_ENDPOINT",
    "http://minecraft-server:8080/tiktok/event"
)

client = TikTokLiveClient(unique_id=TIKTOK_USER)

# ===== HTTP 送信 =====
def send_to_mc(event_type: str, data: dict):
    payload = {
        "type": event_type,
        "timestamp": int(time.time()),
        "data": data
    }
    try:
        requests.post(
            MC_ENDPOINT,
            json=payload,
            timeout=2
        )
    except Exception as e:
        print("send error:", e)

# ===== イベント =====

@client.on(CommentEvent)
async def on_comment(event):
    send_to_mc("comment", {
        "user": event.user.unique_id,
        "nickname": event.user.nickname,
        "comment": event.comment,
    })

@client.on(GiftEvent)
async def on_gift(event):
    send_to_mc("gift", {
        "user": event.user.unique_id,
        "gift": event.gift.name,
        "diamond": event.gift.diamond_count,
        "count": event.repeat_count,
        "repeat_end": event.repeat_end,
    })

@client.on(LikeEvent)
async def on_like(event):
    send_to_mc("like", {
        "count": event.like_count,
        "total": event.total_like_count,
    })

@client.on(FollowEvent)
async def on_follow(event):
    send_to_mc("follow", {
        "user": event.user.unique_id,
    })

@client.on(ShareEvent)
async def on_share(event):
    send_to_mc("share", {
        "user": event.user.unique_id,
    })

@client.on(SubscribeEvent)
async def on_subscribe(event):
    send_to_mc("subscribe", {
        "user": event.user.unique_id,
    })

@client.on(JoinEvent)
async def on_join(event):
    send_to_mc("join", {
        "user": event.user.unique_id,
    })

# ===== 再接続ループ =====
while True:
    try:
        print("connecting...")
        client.run()
    except UserOfflineError:
        print("offline, retry 30s")
        time.sleep(30)
    except Exception as e:
        print("error:", e)
        time.sleep(5)
