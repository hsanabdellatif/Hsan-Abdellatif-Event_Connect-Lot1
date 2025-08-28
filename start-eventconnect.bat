@echo off
echo.
echo ================================
echo EventConnect - Script Demarrage
echo ================================
echo.

echo Verification des prerequis...

REM Verification Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR: Java non trouve. Installez Java 17+
    pause
    exit /b 1
) else (
    echo ✓ Java detecte
)

REM Verification Node.js
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR: Node.js non trouve. Installez Node.js 16+
    pause
    exit /b 1
) else (
    echo ✓ Node.js detecte
)

echo.
echo Options:
echo 1. Demarrer backend (ouvrir VS Code)
echo 2. Demarrer frontend
echo 3. Afficher instructions
echo 0. Quitter
echo.

set /p choice="Votre choix: "

if "%choice%"=="1" (
    echo Ouverture de VS Code...
    start code .
    echo.
    echo Instructions:
    echo 1. Ouvrir EventConnectApplication.java
    echo 2. Cliquer sur "Run" ou F5
    echo 3. API sera sur http://localhost:8080/api
    start http://localhost:8080/api/health
    pause
) else if "%choice%"=="2" (
    echo Demarrage du frontend...
    cd frontend
    if not exist "node_modules" (
        echo Installation des dependances...
        npm install --legacy-peer-deps
    )
    echo Frontend sera sur http://localhost:4200
    npm start
) else if "%choice%"=="3" (
    echo.
    echo === Instructions manuelles ===
    echo.
    echo Backend:
    echo 1. Ouvrir VS Code: code .
    echo 2. Ouvrir EventConnectApplication.java
    echo 3. Cliquer "Run" ou F5
    echo.
    echo Frontend:
    echo 1. cd frontend
    echo 2. npm install --legacy-peer-deps
    echo 3. npm start
    echo.
    echo Tests:
    echo 1. Installer Thunder Client dans VS Code
    echo 2. Importer: docs/eventconnect-api-tests.postman_collection.json
    echo 3. Suivre: docs/test-guide.md
    echo.
    pause
) else if "%choice%"=="0" (
    echo Au revoir!
) else (
    echo Choix invalide
    pause
)

echo.
echo Documentation: README.md
echo Tests API: docs/test-guide.md
echo Collection Postman: docs/eventconnect-api-tests.postman_collection.json
