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

if mode == "production":
    from . import main
    main.run()
else:
    from . import dummy_main
    dummy_main.run()
