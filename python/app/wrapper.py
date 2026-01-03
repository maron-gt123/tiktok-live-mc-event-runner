import os
import subprocess

mode = os.environ.get("MODE", "production") 

if mode == "dummy":
    print("Starting dummy mode...")
    subprocess.run(["python", "dummy_main.py"])
else:
    print("Starting production mode...")
    subprocess.run(["python", "main.py"])
