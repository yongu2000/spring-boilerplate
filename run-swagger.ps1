# run-swagger.ps1

Write-Host "📦 Running tests and generating OpenAPI spec..."
./gradlew.bat clean test openapi3

if (!(Test-Path -Path "build\api-spec\openapi3.json")) {
    Write-Host "❌ openapi3.json not found! Check your REST Docs generation."
    exit 1
}

Write-Host "📄 Copying OpenAPI spec to static docs folder..."
New-Item -ItemType Directory -Force -Path "src\main\resources\static\docs" | Out-Null
Copy-Item "build\api-spec\openapi3.json" "src\main\resources\static\docs\openapi3.json" -Force

Write-Host "🧼 Cleaning up old Swagger UI container (if exists)..."
docker rm -f swagger-ui | Out-Null

Write-Host "🚀 Starting Swagger UI on http://localhost:8081"

$swaggerJsonPath = "$PWD\src\main\resources\static\docs"

docker run -d -p 8081:8080 `
  -e "SWAGGER_JSON=/tmp/openapi3.json" `
  -v "${swaggerJsonPath}:/tmp" `
  --name swagger-ui `
  swaggerapi/swagger-ui

Write-Host "✅ Swagger UI is now available at: http://localhost:8081"