FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY app/ ./   # app 内をすべて /app にコピー
COPY config/ ./config/

# 直接 main.py を起動
CMD ["python", "-u", "main.py"]
