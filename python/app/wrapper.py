import yaml
import asyncio
from app import main, dummy_main

# config.yaml を読み込む
with open("config/config.yaml", "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

mode = config.get("mode", "production")

if mode == "dummy":
    print("Starting dummy mode (from config)...")
    dummy_main.run_dummy()
else:
    print("Starting production mode (from config)...")
    asyncio.run(main.run_production())
