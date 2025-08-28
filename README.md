# Hsan-Abdellatif-Event-Connect-Lot1

## 📋 Description
Application de gestion d'événements.
Projet Lot1 avec architecture propre et code bien structuré.

## 🏗️ Architecture
- **Backend**: Spring Boot (Java)
- **Frontend**: Angular (TypeScript)
- **Base de données**: MySQL
- **Build**: Maven

## 📁 Structure du Projet
```
Hsan-Abdellatif-Event-Connect-Lot1/
├── backend/                    # Application Spring Boot
│   ├── src/main/java/
│   │   └── com/eventconnect/
│   │       ├── entities/       # Entités JPA
│   │       ├── repositories/   # Couche d'accès aux données
│   │       ├── services/       # Logique métier
│   │       ├── controllers/    # Contrôleurs REST
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── config/        # Configuration
│   │       └── exceptions/    # Gestion des exceptions
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/                   # Application Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── models/        # Modèles TypeScript
│   │   │   ├── services/      # Services Angular
│   │   │   ├── components/    # Composants Angular
│   │   │   ├── guards/        # Guards de routes
│   │   │   └── shared/        # Composants partagés
│   │   ├── assets/
│   │   └── environments/
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
└── docs/                       # Documentation
```

## 🚀 Getting Started

### Prérequis
- Java 17+
- MySQL 8.0+
- Node.js 16+ (pour le frontend)
- VS Code avec Extension Pack Java

### Démarrage du Backend
1. **Configuration de la base de données**
   ```bash
   # Créer la base de données MySQL
   mysql -u root -p
   CREATE DATABASE hsan_abdellatif_event_connect_lot1;
   ```

2. **Démarrer l'application Backend**
   - Ouvrir le projet dans VS Code
   - Ouvrir `EventConnectApplication.java`
   - Cliquer sur "Run" ou utiliser F5
   - L'API sera disponible sur `http://localhost:8080/api`

### Test de l'authentification JWT

#### 1. Vérifier le statut de l'API
```bash
GET http://localhost:8080/api/health
```

#### 2. Créer un compte utilisateur
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "nom": "Doe",
  "prenom": "John", 
  "email": "john.doe@example.com",
  "motDePasse": "password123"
}
```

#### 3. Se connecter
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "motDePasse": "password123"
}
```

#### 4. Tester un endpoint protégé
```bash
GET http://localhost:8080/api/test-protected
Authorization: Bearer YOUR_JWT_TOKEN
```

### Démarrage du Frontend
```bash
cd frontend
npm install --legacy-peer-deps
npm start
```
L'application sera disponible sur `http://localhost:4200`

## 📝 Changelog
- **v1.0** : Architecture backend complète (26 commits)
- **v1.1** : Intégration frontend Angular Material Dashboard (2 commits)
- **v1.2** : Implémentation authentification JWT complète (3 commits)
- **v1.3** : Tests et documentation API (commit actuel)

## 🔐 Fonctionnalités Sécurité
- ✅ Authentification JWT
- ✅ Endpoints de connexion/inscription
- ✅ Protection des routes API
- ✅ Gestion des erreurs d'authentification
- ✅ Configuration CORS pour Angular

## 🛠️ Technologies Utilisées
- **Backend**: Spring Boot 3.1.0, Spring Security, JWT, JPA/Hibernate
- **Frontend**: Angular 15, Angular Material, TypeScript
- **Base de données**: MySQL 8.0
- **Outils**: VS Code, Git, Maven, npm