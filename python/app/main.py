from TikTokLive import TikTokLiveClient
from TikTokLive.events import CommentEvent, GiftEvent
import os

TIKTOK_USER = os.environ.get("TIKTOK_USER")

client = TikTokLiveClient(unique_id=TIKTOK_USER)

@client.on(CommentEvent)
async def on_comment(event: CommentEvent):
    print(f"[COMMENT] {event.user.nickname}: {event.comment}")

@client.on(GiftEvent)
async def on_gift(event: GiftEvent):
    if event.gift.streakable and event.streaking:
        return
    print(f"[GIFT] {event.user.nickname} -> {event.gift.name} x{event.repeat_count}")

client.run()
