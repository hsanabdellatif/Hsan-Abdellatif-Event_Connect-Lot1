# Script de démarrage EventConnect
# Automatise le démarrage du backend et frontend

Write-Host "🚀 EventConnect - Script de démarrage" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green

# Fonction pour vérifier si un processus écoute sur un port
function Test-Port {
    param([int]$Port)
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.Connect("localhost", $Port)
        $connection.Close()
        return $true
    }
    catch {
        return $false
    }
}

# Vérifier Java
Write-Host "🔍 Vérification de Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✅ Java détecté: $javaVersion" -ForegroundColor Green
}
catch {
    Write-Host "❌ Java non trouvé. Veuillez installer Java 17+." -ForegroundColor Red
    exit 1
}

# Vérifier MySQL
Write-Host "🔍 Vérification de MySQL..." -ForegroundColor Yellow
if (Test-Port 3306) {
    Write-Host "✅ MySQL détecté sur le port 3306" -ForegroundColor Green
}
else {
    Write-Host "⚠️ MySQL non détecté sur le port 3306. Assurez-vous qu'il est démarré." -ForegroundColor Yellow
}

# Vérifier Node.js
Write-Host "🔍 Vérification de Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = node --version
    Write-Host "✅ Node.js détecté: $nodeVersion" -ForegroundColor Green
}
catch {
    Write-Host "❌ Node.js non trouvé. Veuillez installer Node.js 16+." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📋 Options de démarrage:" -ForegroundColor Cyan
Write-Host "1. Démarrer uniquement le backend" -ForegroundColor White
Write-Host "2. Démarrer uniquement le frontend" -ForegroundColor White
Write-Host "3. Démarrer backend + frontend" -ForegroundColor White
Write-Host "4. Afficher les instructions manuelles" -ForegroundColor White
Write-Host "0. Quitter" -ForegroundColor White

$choice = Read-Host "`nChoix"

switch ($choice) {
    "1" {
        Write-Host "🚀 Démarrage du backend..." -ForegroundColor Green
        Write-Host "💡 Ouvrez VS Code et lancez EventConnectApplication.java" -ForegroundColor Yellow
        Write-Host "🌐 API disponible sur: http://localhost:8080/api" -ForegroundColor Cyan
        Start-Process "http://localhost:8080/api/health"
    }
    "2" {
        Write-Host "🚀 Démarrage du frontend..." -ForegroundColor Green
        Set-Location "frontend"
        if (!(Test-Path "node_modules")) {
            Write-Host "📦 Installation des dépendances..." -ForegroundColor Yellow
            npm install --legacy-peer-deps
        }
        Write-Host "🌐 Frontend disponible sur: http://localhost:4200" -ForegroundColor Cyan
        npm start
    }
    "3" {
        Write-Host "🚀 Démarrage complet..." -ForegroundColor Green
        Write-Host "💡 1. Ouvrez VS Code et lancez EventConnectApplication.java" -ForegroundColor Yellow
        Write-Host "💡 2. Ensuite, lancez ce script avec l'option 2 pour le frontend" -ForegroundColor Yellow
        Write-Host "🌐 API: http://localhost:8080/api" -ForegroundColor Cyan
        Write-Host "🌐 Frontend: http://localhost:4200" -ForegroundColor Cyan
        Start-Process "http://localhost:8080/api/health"
        Start-Process "http://localhost:4200"
    }
    "4" {
        Write-Host "📖 Instructions manuelles:" -ForegroundColor Green
        Write-Host ""
        Write-Host "Backend:" -ForegroundColor Yellow
        Write-Host "1. Ouvrir VS Code dans le dossier du projet" -ForegroundColor White
        Write-Host "2. Ouvrir src/main/java/com/eventconnect/EventConnectApplication.java" -ForegroundColor White
        Write-Host "3. Cliquer sur 'Run' ou appuyer F5" -ForegroundColor White
        Write-Host ""
        Write-Host "Frontend:" -ForegroundColor Yellow
        Write-Host "1. cd frontend" -ForegroundColor White
        Write-Host "2. npm install --legacy-peer-deps" -ForegroundColor White
        Write-Host "3. npm start" -ForegroundColor White
        Write-Host ""
        Write-Host "Tests API:" -ForegroundColor Yellow
        Write-Host "1. Installer Thunder Client dans VS Code" -ForegroundColor White
        Write-Host "2. Importer docs/eventconnect-api-tests.postman_collection.json" -ForegroundColor White
        Write-Host "3. Suivre le guide: docs/test-guide.md" -ForegroundColor White
    }
    "0" {
        Write-Host "👋 Au revoir!" -ForegroundColor Green
    }
    default {
        Write-Host "❌ Choix invalide" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "📚 Documentation: README.md" -ForegroundColor Cyan
Write-Host "🧪 Tests API: docs/test-guide.md" -ForegroundColor Cyan
Write-Host "📡 Collection Postman: docs/eventconnect-api-tests.postman_collection.json" -ForegroundColor Cyan
