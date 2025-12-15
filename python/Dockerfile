FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

# requirements
COPY python/app/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# app
COPY python/app/main.py .
COPY python/app/config.yaml .

CMD ["python", "main.py"]
