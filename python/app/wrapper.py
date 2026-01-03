# python/app/wrapper.py

import asyncio
from pathlib import Path
from . import main, dummy_main
import yaml

# =====================
# config.yaml を読み込む（main.py / dummy_main.py と同じ方法）
# =====================
CONFIG_PATH = Path(__file__).parent / "config" / "config.yaml"
with open(CONFIG_PATH, "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

# =====================
# mode 判定
# =====================
mode = config.get("mode", "production").lower()

if mode == "dummy":
    print("[WRAPPER] Mode is dummy. Running dummy_main...")
    dummy_main.run_dummy()
else:
    print("[WRAPPER] Mode is production. Running main...")
    asyncio.run(main.run_production())
