# EventConnect v2 - Best Practices

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-green)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

## 📋 Description

EventConnect v2 est une application de gestion d'événements développée en suivant les meilleures pratiques de développement Java/Spring Boot. Cette version améliore l'architecture du projet original en appliquant une séparation claire des responsabilités et une structure de code maintenable.

## 🏗️ Architecture

Le projet suit une architecture en couches (Layered Architecture) :

```
├── 📁 entities/          # Couche de persistance (JPA Entities)
├── 📁 repositories/      # Couche d'accès aux données (Spring Data JPA)
├── 📁 services/          # Couche logique métier
├── 📁 controllers/       # Couche présentation (REST Controllers)
├── 📁 dto/              # Objects de transfert de données
├── 📁 config/           # Configuration Spring
└── 📁 exceptions/       # Gestion des exceptions personnalisées
```

## ✨ Fonctionnalités

### 👥 Gestion des Utilisateurs
- ✅ Création, modification, suppression d'utilisateurs
- ✅ Validation des données (email unique, format valide)
- ✅ Recherche par email
- ✅ Gestion des mots de passe sécurisés

### 🎭 Gestion des Événements
- ✅ CRUD complet des événements
- ✅ Recherche par titre, catégorie
- ✅ Filtrage des événements futurs/disponibles
- ✅ Gestion de la capacité et des places disponibles
- ✅ Validation des dates (début/fin cohérentes)

### 🎫 Gestion des Réservations
- ✅ Création de réservations avec vérification de disponibilité
- ✅ Confirmation et annulation de réservations
- ✅ Calcul automatique du montant total
- ✅ Suivi des statuts (EN_ATTENTE, CONFIRMEE, ANNULEE)
- ✅ Rapports de chiffre d'affaires

## 🛠️ Technologies Utilisées

```bash
git clone https://github.com/hsanabdellatif/Hsan-Abdellatif-Event_Connect-Lot1.git
cd Hsan-Abdellatif-Event_Connect-Lot1
```

2. **Configuration de la base de données**
```sql
CREATE DATABASE eventconnect_db;
CREATE USER 'eventconnect_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON eventconnect_db.* TO 'eventconnect_user'@'localhost';
FLUSH PRIVILEGES;
```

3. **Configuration application.properties**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/eventconnect_db
spring.datasource.username=eventconnect_user
spring.datasource.password=password123

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server Configuration
server.port=8080
```

4. **Compiler et lancer l'application**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

## 📚 API Documentation

### Endpoints Utilisateurs
- `GET /api/utilisateurs` - Lister tous les utilisateurs
- `GET /api/utilisateurs/{id}` - Récupérer un utilisateur
- `POST /api/utilisateurs` - Créer un utilisateur
- `PUT /api/utilisateurs/{id}` - Modifier un utilisateur
- `DELETE /api/utilisateurs/{id}` - Supprimer un utilisateur

### Endpoints Événements
- `GET /api/evenements` - Lister tous les événements (avec pagination)
- `GET /api/evenements/{id}` - Récupérer un événement
- `POST /api/evenements` - Créer un événement
- `PUT /api/evenements/{id}` - Modifier un événement
- `DELETE /api/evenements/{id}` - Supprimer un événement
- `GET /api/evenements/futurs` - Événements futurs
- `GET /api/evenements/disponibles` - Événements avec places disponibles

### Endpoints Réservations
- `POST /api/reservations` - Créer une réservation
- `GET /api/reservations/{id}` - Récupérer une réservation
- `PUT /api/reservations/{id}/confirmer` - Confirmer une réservation
- `PUT /api/reservations/{id}/annuler` - Annuler une réservation
- `GET /api/reservations/utilisateur/{id}` - Réservations d'un utilisateur
- `GET /api/reservations/evenement/{id}` - Réservations d'un événement

## 🧪 Tests

```bash
# Lancer tous les tests
mvn test

# Lancer les tests avec rapport de couverture
mvn test jacoco:report
```

## 📋 Bonnes Pratiques Appliquées

### 🏗️ Architecture
- ✅ Séparation claire des couches (Controller, Service, Repository, Entity)
- ✅ Injection de dépendances avec Spring
- ✅ Configuration externalisée

### 💾 Persistence
- ✅ Utilisation de Spring Data JPA
- ✅ Entités JPA avec annotations de validation
- ✅ Relations entre entités bien définies
- ✅ Requêtes personnalisées dans les repositories

### 🛡️ Sécurité
- ✅ Configuration Spring Security
- ✅ Encodage des mots de passe avec BCrypt
- ✅ Configuration CORS pour les APIs

### 🧹 Code Quality
- ✅ Lombok pour réduire le boilerplate
- ✅ Validation des données avec Bean Validation
- ✅ Gestion d'erreurs avec exceptions personnalisées
- ✅ Logging avec SLF4J
- ✅ Documentation JavaDoc

### � Git Workflow
- ✅ Commits atomiques et descriptifs
- ✅ Messages de commit avec emojis pour clarté
- ✅ Une classe = un commit pour traçabilité

## 📈 Evolution du Projet

### Commits Par Phase
1. **🏗️ Structure de base** - Configuration projet Maven
2. **🗃️ Entities** - Utilisateur, Evenement, Reservation
3. **🗃️ Repositories** - Couche d'accès aux données
4. **⚙️ Services** - Logique métier
5. **🎮 Controllers** - APIs REST
6. **⚙️ Configuration** - Security, CORS
7. **🚨 Exceptions** - Gestion d'erreurs personnalisées

## 👥 Contributeurs

- **Hsan Abdellatif** - Développeur principal

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 🤝 Contribution

Les contributions sont les bienvenues ! Veuillez suivre ces étapes :

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

---

**EventConnect v2** - Développé avec ❤️ en suivant les meilleures pratiques Java/Spring Boot
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
[Instructions à venir]

## 📝 Changelog
- **Lot1**: Projet initial avec bonnes pratiques et architecture propre
