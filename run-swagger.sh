#!/bin/bash

# 🧹 1. 테스트 + OpenAPI 명세 생성
echo "📦 Running tests and generating OpenAPI spec..."
./gradlew clean test openapi3 || { echo "❌ Gradle build failed"; exit 1; }

# 📂 2. openapi3.json 복사
echo "📄 Copying OpenAPI spec to static docs folder..."
mkdir -p src/main/resources/static/docs
cp build/api-spec/openapi3.json src/main/resources/static/docs/openapi3.json || {
  echo "❌ openapi3.json not found!"
  exit 1
}

# 🐳 3. 기존 swagger-ui 컨테이너 중지 및 삭제
echo "🧼 Cleaning up old Swagger UI container (if exists)..."
docker rm -f swagger-ui 2>/dev/null

# 🚀 4. Swagger UI 실행
echo "🚀 Starting Swagger UI on http://localhost:8081"
docker run -d -p 8081:8080 \
  -e SWAGGER_JSON=/tmp/openapi3.json \
  -v $(pwd)/src/main/resources/static/docs:/tmp \
  --name swagger-ui \
  swaggerapi/swagger-ui

# ✅ 완료 메시지
echo "✅ Swagger UI is now available at: http://localhost:8081"