import importlib.util
from pathlib import Path
import yaml

CONFIG_PATH = Path("/app/config/config.yaml")
with open(CONFIG_PATH, "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

mode = config.get("mode", "production").lower()
print(f"[WRAPPER] Mode is {mode}. Running corresponding main...")

def load_module(name, path):
    """指定パスからモジュールをロード"""
    spec = importlib.util.spec_from_file_location(name, path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module

main = load_module("main", "/app/main.py")
dummy_main = load_module("dummy_main", "/app/dummy_main.py")

if mode == "dummy":
    dummy_main.run_dummy()
else:
    main.run_main()
