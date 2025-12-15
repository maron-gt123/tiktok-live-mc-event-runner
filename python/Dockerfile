# =========================
# Python 実行用ベース
# =========================
FROM python:3.12-slim

# =========================
# 環境変数
# =========================
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# =========================
# 作業ディレクトリ
# =========================
WORKDIR /app

# =========================
# 依存関係
# =========================
COPY app/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# =========================
# アプリ本体
# =========================
COPY app/main.py .
COPY app/config.yaml .

# =========================
# 実行
# =========================
CMD ["python", "main.py"]
