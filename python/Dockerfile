FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

# requirements
COPY python/app/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# アプリ本体
COPY python/app/app/ ./app/
COPY python/app/config.yaml ./config/

CMD ["python", "app/wrapper.py"]
