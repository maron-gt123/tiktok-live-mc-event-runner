import yaml
import subprocess
import sys
import os

# =====================
# 設定読み込み
# =====================
CONFIG_FILE = "config/config.yaml"

if not os.path.exists(CONFIG_FILE):
    print(f"Error: {CONFIG_FILE} not found.")
    sys.exit(1)

with open(CONFIG_FILE, "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

mode = config.get("mode", "production").lower()
print(f"Running in mode: {mode}")

# =====================
# モードによって実行ファイルを切り替え
# =====================
if mode == "dummy":
    target_script = "dummy_main.py"
elif mode == "production":
    target_script = "main.py"
else:
    print(f"Unknown mode: {mode}")
    sys.exit(1)

# =====================
# サブプロセスで実行
# =====================
try:
    subprocess.run([sys.executable, target_script], check=True)
except FileNotFoundError:
    print(f"Error: {target_script} not found.")
except subprocess.CalledProcessError as e:
    print(f"{target_script} exited with error: {e}")
