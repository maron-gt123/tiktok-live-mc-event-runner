FROM python:3.12-slim

WORKDIR /app

# 依存関係インストール
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Python ファイルを /app に直接コピー
COPY app/*.py ./

# Config を /app/config にコピー
COPY app/config ./config

# モード設定（必要に応じて override 可能）
ENV MODE=dummy

# 実行
CMD ["python", "wrapper.py"]
