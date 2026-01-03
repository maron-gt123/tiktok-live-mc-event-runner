import asyncio
from pathlib import Path
from . import main, dummy_main
import yaml

# =====================
# config.yaml 読み込み
# =====================
CONFIG_PATH = Path(__file__).parent / "config" / "config.yaml"
with open(CONFIG_PATH, "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)


mode = config.get("mode", "production")

if mode == "dummy":
    print("Starting dummy mode...")
    dummy_main.run_dummy(config)  # config は dummy_main のみ使用
else:
    print("Starting production mode...")
    asyncio.run(main.run_production())  # main.py は既存のまま
