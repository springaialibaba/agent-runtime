FROM python:3.11-slim

# Set environment variables
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1 \
    WORKSPACE_DIR=/workspace \
    SESSION_ID="" \
    SECRET_TOKEN=default_token \
    DEBIAN_FRONTEND=noninteractive


# Install system dependencies and Python packages
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    git \
    && rm -rf /var/lib/apt/lists/* \
    && pip install --no-cache-dir \
        fastapi==0.104.1 \
        uvicorn[standard]==0.24.0 \
        python-multipart==0.0.6

# Create directories
RUN mkdir -p /workspace /app

# Copy application files
COPY sandbox_server/main.py /app/

# Set permissions
RUN chmod +x /app/main.py

# Create non-root user
RUN useradd -m -u 1000 sandbox && \
    chown -R sandbox:sandbox /workspace /app

# Switch to non-root user
USER sandbox

# Set working directory
WORKDIR /workspace

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/healthz || exit 1

# Start command
CMD ["python", "/app/main.py"]