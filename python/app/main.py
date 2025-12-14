from TikTokLive import TikTokLiveClient
from TikTokLive.events import CommentEvent, GiftEvent
import os

TIKTOK_USER = os.environ.get("TIKTOK_USER")

client = TikTokLiveClient(unique_id=TIKTOK_USER)

# すべてのイベントを捕まえる
@client.on("event")
async def on_any_event(event):
    print("\n=== EVENT RECEIVED ===")
    print(type(event))
    print(vars(event))
    print("======================\n")

while True:
    try:
        print("connecting...")
        client.run()
    except UserOfflineError:
        print("LIVE offline. retry in 30s")
        time.sleep(30)
