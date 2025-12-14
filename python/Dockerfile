FROM python:3.11-slim
WORKDIR /app
RUN pip install --no-cache-dir TikTokLive requests
COPY python/app/main.py /app/main.py
CMD ["python", "main.py"]
