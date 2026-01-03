FROM python:3.12-slim

WORKDIR /app

# 依存関係インストール
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# アプリ本体コピー
COPY app/ ./app/

ENV MODE=dummy
CMD ["python", "-m", "app.wrapper"]
