#!/bin/bash

echo "Building Todo App services..."

# Clean and build the project (skip tests for now)
echo "Running Maven clean install..."
mvn clean install

if [ $? -ne 0 ]; then
    echo "❌ Build failed! Check the error messages above."
    exit 1
fi


echo "✅ Build successful!"

# If Docker CLI is not installed, skip the container step and start services directly
if ! command -v docker &>/dev/null; then
    echo "ℹ️ Docker is not installed."
    echo "ℹ️ Run services manually:"
    echo "   Terminal 1: cd user-service && mvn spring-boot:run"
    echo "   Terminal 2: cd todo-service && mvn spring-boot:run"
    exit 0
fi

# Check if Docker is running
if ! docker info &> /dev/null; then
    echo "❌ Docker daemon is not running!"
    echo "Please start Docker Desktop and try again."
    echo ""
    echo "Alternative: Run services manually with:"
    echo "Terminal 1: cd user-service && mvn spring-boot:run"
    echo "Terminal 2: cd todo-service && mvn spring-boot:run"
    exit 1
fi

# Build and start Docker containers
echo "Building and starting Docker containers..."

if docker compose version &> /dev/null; then
    echo "Using docker compose..."
    docker compose up --build
elif command -v docker-compose &> /dev/null; then
    echo "Using docker-compose..."
    docker-compose up --build
else
    echo "❌ Neither 'docker compose' nor 'docker-compose' found!"
    echo "Please install Docker Desktop"
    exit 1
fi
