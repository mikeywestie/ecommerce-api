@echo off
echo ========================================
echo Building Ecommerce API
echo ========================================

call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo Backend build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo ========================================
echo Starting Docker Compose Stack
echo ========================================

docker compose down
docker compose up --build -d

echo ========================================
echo Backend stack started
echo ========================================

docker ps

echo.
echo API:        http://localhost:8080
echo Swagger:    http://localhost:8080/swagger-ui/index.html
echo Health:     http://localhost:8080/actuator/health
echo Prometheus: http://localhost:9090
echo Grafana:    http://localhost:3000
echo.

pause