FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app/ ./app/
COPY app/config/config.yaml ./config/config.yaml

# 起動は wrapper.py に任せる
CMD ["python", "app/wrapper.py"]
