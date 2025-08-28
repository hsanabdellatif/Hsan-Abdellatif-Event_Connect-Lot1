# Guide de Tests EventConnect API

## Configuration Thunder Client (VS Code)

1. **Installer Thunder Client** dans VS Code
2. **Importer la collection** : `docs/eventconnect-api-tests.postman_collection.json`
3. **Configurer les variables** :
   - `base_url`: `http://localhost:8080/api`
   - `jwt_token`: (sera rempli automatiquement après login)

## Tests Étape par Étape

### 1. Démarrer l'application
- Ouvrir `EventConnectApplication.java` dans VS Code
- Cliquer sur "Run" ou appuyer F5
- Vérifier que l'application démarre sans erreurs

### 2. Test de santé
```http
GET http://localhost:8080/api/health
```
**Résultat attendu**: Status 200 avec `{"status": "UP", "message": "EventConnect API is running"}`

### 3. Test endpoint public
```http
GET http://localhost:8080/api/test-public
```
**Résultat attendu**: Status 200 avec message de succès

### 4. Test endpoint protégé (sans token)
```http
GET http://localhost:8080/api/test-protected
```
**Résultat attendu**: Status 401 Unauthorized

### 5. Créer un compte utilisateur
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "nom": "Doe",
  "prenom": "John", 
  "email": "john.doe@example.com",
  "motDePasse": "password123"
}
```
**Résultat attendu**: Status 200 avec token JWT

### 6. Se connecter
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "motDePasse": "password123"
}
```
**Résultat attendu**: Status 200 avec token JWT
**Action**: Copier le token pour les tests suivants

### 7. Test endpoint protégé (avec token)
```http
GET http://localhost:8080/api/test-protected
Authorization: Bearer YOUR_JWT_TOKEN
```
**Résultat attendu**: Status 200 avec message de succès

### 8. Obtenir les informations utilisateur
```http
GET http://localhost:8080/api/auth/me
Authorization: Bearer YOUR_JWT_TOKEN
```
**Résultat attendu**: Status 200 avec informations utilisateur

## Tests d'Erreurs

### Token expiré/invalide
```http
GET http://localhost:8080/api/test-protected
Authorization: Bearer invalid_token
```
**Résultat attendu**: Status 401 Unauthorized

### Données de connexion incorrectes
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "wrong@example.com",
  "motDePasse": "wrongpassword"
}
```
**Résultat attendu**: Status 400 Bad Request

### Email déjà utilisé
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "nom": "Test",
  "prenom": "User", 
  "email": "john.doe@example.com",
  "motDePasse": "password123"
}
```
**Résultat attendu**: Status 400 Bad Request

## Validation de l'Authentification JWT

✅ **Endpoints publics** : Accessibles sans authentification
✅ **Endpoints protégés** : Nécessitent un token JWT valide
✅ **Génération de tokens** : Lors de l'inscription et connexion
✅ **Validation de tokens** : Vérification automatique par le filtre JWT
✅ **Gestion d'erreurs** : Retour approprié pour tokens invalides

## Prochaines Étapes

1. **Tests des CRUD** : Tester la création/lecture/modification/suppression des entités
2. **Tests de validation** : Vérifier les contraintes de validation des DTOs
3. **Tests d'intégration** : Tester le frontend Angular avec l'API
4. **Tests de performance** : Charger plusieurs utilisateurs simultanément
