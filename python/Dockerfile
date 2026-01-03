FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY python/requirements.txt .
COPY python/app/ ./app/
COPY python/app/config/ ./config/

CMD ["python", "app/wrapper.py"]
