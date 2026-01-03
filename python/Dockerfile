FROM python:3.12-slim

WORKDIR /app

# 依存関係インストール
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Python ファイルを /app に直接コピー
COPY app/*.py ./

# Config を /app/config にコピー
COPY app/config ./config

# 実行
CMD ["python", "-m", "wrapper"]
