import os
from app import main, dummy_main

mode = os.environ.get("MODE", "production")

if mode == "dummy":
    print("Starting dummy mode...")
    dummy_main.run_dummy()
else:
    print("Starting production mode...")
    import asyncio
    asyncio.run(main.run_production())
