import main
import dummy_main

import yaml
from pathlib import Path

CONFIG_PATH = Path("/app/config/config.yaml")

with open(CONFIG_PATH, "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

mode = config.get("mode", "production").lower()
print(f"[WRAPPER] Mode is {mode}. Running corresponding main...")

if mode == "dummy":
    dummy_main.run_dummy()
else:
    main.run_main()
