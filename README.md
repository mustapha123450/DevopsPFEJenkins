#= 🚀 My REST API - DevOps PFE Project

## 📋 Description du Projet

Ce projet est une **REST API Node.js** déployée automatiquement sur **Kubernetes** via un pipeline **CI/CD complet** utilisant **GitHub Actions** et un **self-hosted runner**.

L'objectif est de démontrer une architecture **DevOps moderne** avec :
- ✅ Intégration Continue (CI)
- ✅ Déploiement Continu (CD)
- ✅ Containerisation Docker
- ✅ Orchestration Kubernetes
- ✅ Infrastructure as Code (IaC)

---

## 🎯 Objectifs réalisés

### **1️⃣ Application REST API**
- ✅ Endpoints CRUD pour gérer les utilisateurs
- ✅ Connexion à PostgreSQL
- ✅ Health checks (`/health`, `/ready`)
- ✅ Gestion des erreurs

### **2️⃣ Containerisation Docker**
- ✅ Dockerfile optimisé
- ✅ Images poussées sur GitHub Container Registry (ghcr.io)
- ✅ Tags multiples (`:latest`, `:SHA`)

### **3️⃣ Infrastructure Kubernetes**
- ✅ Deployment REST API (3 replicas)
- ✅ Deployment PostgreSQL (1 replica)
- ✅ Services (LoadBalancer pour API, ClusterIP pour DB)
- ✅ ConfigMaps et Secrets pour la configuration
- ✅ PersistentVolumeClaim pour le stockage DB
- ✅ HorizontalPodAutoscaler (2-10 replicas)

### **4️⃣ Pipeline CI/CD Complet**
- ✅ Tests automatiques (linter, unit tests, security audit)
- ✅ Build Docker automatique
- ✅ Push sur ghcr.io
- ✅ Déploiement sur Minikube via self-hosted runner
- ✅ Rolling updates sans downtime
- ✅ Notifications de succès/erreur

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────┐
│                    GitHub Repository                      │
│  (mustapha123450/DevopsPFE/my-rest-api)                  │
└────────────────────────┬─────────────────────────────────┘
                         │
                    git push main
                         │
        ┌────────────────▼────────────────┐
        │   GitHub Actions (Ubuntu)       │
        │  ┌──────────────────────────┐   │
        │  │ 1️⃣ TEST Job             │   │
        │  │ - npm lint               │   │
        │  │ - npm test               │   │
        │  │ - npm audit              │   │
        │  └──────────────────────────┘   │
        │  ┌──────────────────────────┐   │
        │  │ 2️⃣ BUILD Job            │   │
        │  │ - Docker build           │   │
        │  │ - Push ghcr.io           │   │
        │  └──────────────────────────┘   │
        └────────────────┬─────────────────┘
                         │
                    Docker Image
                 (ghcr.io/.../my-rest-api)
                         │
        ┌────────────────▼────────────────┐
        │  Self-Hosted Runner (Windows)   │
        │  ┌──────────────────────────┐   │
        │  │ 3️⃣ DEPLOY Job           │   │
        │  │ - kubectl set image      │   │
        │  │ - Rolling update         │   │
        │  │ - Verify deployment      │   │
        │  └──────────────────────────┘   │
        └────────────────┬─────────────────┘
                         │
                    Minikube
        ┌────────────────▼────────────────┐
        │  Kubernetes (production ns)     │
        │  ┌──────────────────────────┐   │
        │  │ REST API Deployment      │   │
        │  │ (3 pods, LoadBalancer)   │   │
        │  └──────────────────────────┘   │
        │  ┌──────────────────────────┐   │
        │  │ PostgreSQL Deployment    │   │
        │  │ (1 pod, ClusterIP)       │   │
        │  └──────────────────────────┘   │
        │  ┌──────────────────────────┐   │
        │  │ HPA (2-10 replicas)      │   │
        │  └──────────────────────────┘   │
        └─────────────────────────────────┘
```

---

## 📁 Structure du Projet

```
my-rest-api/
├── .github/
│   └── workflows/
│       └── ci-cd.yaml              # Pipeline CI/CD complet
├── k8s/
│   ├── all-in-one.yaml             # Configuration K8s complète
│   ├── deployment.yaml             # Deployment REST API
│   ├── service.yaml                # Service REST API
│   ├── configmap.yaml              # Configuration app
│   ├── postgres-deployment.yaml    # Deployment PostgreSQL
│   ├── postgres-pvc.yaml           # PersistentVolumeClaim
│   └── namespace.yaml              # Namespace production
├── src/
│   └── server.js                   # Application Node.js
├── Dockerfile                      # Image Docker
├── package.json                    # Dépendances Node.js
├── .dockerignore                   # Fichiers à ignorer
├── .gitignore                      # Fichiers Git à ignorer
└── README.md                       # Ce fichier
```

---

## 🛠️ Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| **Node.js** | 18 | Runtime applicatif |
| **Express.js** | Latest | Framework REST API |
| **PostgreSQL** | 15-alpine | Base de données |
| **Docker** | Latest | Containerisation |
| **Kubernetes** | Minikube | Orchestration |
| **GitHub Actions** | - | CI/CD |
| **ghcr.io** | - | Container Registry |

---

## 🚀 Démarrage rapide

### **Prérequis**
- Docker installé
- Minikube installé
- kubectl installé
- Node.js 18+
- Git

### **1️⃣ Cloner et installer**

```bash
git clone https://github.com/mustapha123450/DevopsPFE.git
cd my-rest-api
npm install
```

### **2️⃣ Lancer localement**

```bash
npm run dev
# L'API tourne sur http://localhost:3000
```

### **3️⃣ Déployer sur Kubernetes**

```bash
# Démarrer Minikube
minikube start

# Appliquer la configuration K8s
kubectl apply -f k8s/all-in-one.yaml

# Vérifier les pods
kubectl get pods -n production -w

# Exposer le service
kubectl port-forward svc/rest-api-service 3000:80 -n production
```

---

## 📡 Endpoints API

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/health` | Health check |
| `GET` | `/ready` | Readiness probe |
| `GET` | `/api/users` | Lister tous les utilisateurs |
| `POST` | `/api/users` | Créer un utilisateur |
| `GET` | `/api/users/:id` | Récupérer un utilisateur |
| `PUT` | `/api/users/:id` | Modifier un utilisateur |
| `DELETE` | `/api/users/:id` | Supprimer un utilisateur |

### **Exemples de requêtes**

```bash
# GET health
curl http://localhost:3000/health

# POST un utilisateur
curl -X POST http://localhost:3000/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com"}'

# GET tous les utilisateurs
curl http://localhost:3000/api/users
```

---

## 🔄 Pipeline CI/CD Expliqué

### **Déclenchement**
```yaml
Quand : git push origin main ou develop
Ou : Création d'une PR vers main ou develop
```

### **Job 1️⃣ : TEST (Ubuntu)**
```bash
✅ npm ci               # Install dépendances
✅ npm run lint        # Qualité du code
✅ npm test            # Tests unitaires
✅ npm audit           # Vulnérabilités
```

### **Job 2️⃣ : BUILD (Ubuntu)**
```bash
✅ Docker login ghcr.io
✅ Docker build
✅ Docker push (tags: :latest, :SHA)
Résultat : Image sur ghcr.io/mustapha123450/devopspfe/my-rest-api
```

### **Job 3️⃣ : DEPLOY (Self-hosted / Minikube)**
```bash
✅ kubectl set image      # Change l'image
✅ Rolling update         # 3 pods redémarrés
✅ Vérification           # Affiche les pods et services
```

### **Job 4️⃣ : NOTIFY**
```bash
✅ Affiche le résumé du deployment
```

---

## 📊 Détails de l'infrastructure Kubernetes

### **Namespace : production**

#### **Deployment : REST API**
- Replicas : 3 (haute disponibilité)
- Image : `ghcr.io/mustapha123450/devopspfe/my-rest-api:latest`
- Port : 3000
- CPU : 100m (request), 200m (limit)
- Memory : 128Mi (request), 256Mi (limit)

#### **Service : REST API**
- Type : LoadBalancer
- Port externe : 80
- Port interne : 3000

#### **Deployment : PostgreSQL**
- Replicas : 1
- Image : `postgres:15-alpine`
- Port : 5432
- Storage : 5Gi (PersistentVolumeClaim)

#### **Service : PostgreSQL**
- Type : ClusterIP (interne seulement)
- Port : 5432

#### **ConfigMap : app-config**
```yaml
DB_HOST: postgres-service
DB_PORT: 5432
DB_NAME: mydb
REDIS_HOST: redis-service
REDIS_PORT: 6379
```

#### **Secret : db-secret**
```yaml
username: postgres
password: (from GitHub Secrets)
```

#### **HorizontalPodAutoscaler**
```yaml
Min replicas : 2
Max replicas : 10
CPU threshold : 70%
Memory threshold : 80%
```

---

## 🔐 Sécurité

### **Configuration**
- ✅ Secrets pour les mots de passe (pas en dur)
- ✅ ConfigMaps pour la configuration
- ✅ RBAC (Role-Based Access Control) via ServiceAccount
- ✅ Network Policies (possibilité d'ajouter)

### **Registry**
- ✅ Images sur ghcr.io (privé par défaut)
- ✅ Authentification via GITHUB_TOKEN
- ✅ Tags uniques par build (SHA)

### **Health Checks**
- ✅ Liveness Probe : `/health`
- ✅ Readiness Probe : `/ready`
- ✅ Redémarrage automatique en cas de crash

---

## 📈 Scalabilité

### **Horizontal Scaling (HPA)**
```yaml
Minimum : 2 pods
Maximum : 10 pods
Trigger : CPU > 70% ou Memory > 80%
```

### **Rolling Updates**
```yaml
Stratégie : RollingUpdate
Max Surge : 1 pod
Max Unavailable : 1 pod
→ Garantit zéro downtime
```

---

## 🔧 Commandes utiles

```bash
# Minikube
minikube start
minikube stop
minikube status
minikube ip
minikube dashboard

# Kubernetes - Pods
kubectl get pods -n production
kubectl get pods -n production -w              # Watch
kubectl describe pod <pod-name> -n production
kubectl logs <pod-name> -n production
kubectl exec -it <pod-name> -n production -- bash

# Kubernetes - Services
kubectl get services -n production
kubectl port-forward svc/rest-api-service 3000:80 -n production

# Kubernetes - Deployments
kubectl get deployments -n production
kubectl describe deployment rest-api-deployment -n production
kubectl rollout status deployment/rest-api-deployment -n production
kubectl rollout history deployment/rest-api-deployment -n production

# Kubernetes - Apply/Delete
kubectl apply -f k8s/all-in-one.yaml
kubectl delete namespace production
kubectl delete -f k8s/all-in-one.yaml

# GitHub Actions
# Voir les workflows : https://github.com/mustapha123450/DevopsPFE/actions
```

---

## 🐛 Dépannage

### **Les pods ne démarrent pas**
```bash
# Vérifiez les logs
kubectl logs <pod-name> -n production

# Vérifiez les événements
kubectl describe pod <pod-name> -n production

# Vérifiez l'image
kubectl describe deployment rest-api-deployment -n production
```

### **Erreur : InvalidImageName**
```bash
# La casse du tag doit être en minuscules
# ✅ ghcr.io/mustapha123450/devopspfe/my-rest-api:latest
# ❌ ghcr.io/mustapha123450/DevopsPFE/my-rest-api:latest
```

### **Minikube ne trouve pas l'image**
```bash
# Tirez manuellement l'image
docker pull ghcr.io/mustapha123450/devopspfe/my-rest-api:latest

# Ou avec imagePullPolicy: Always (déjà configuré)
```

### **Database connection refused**
```bash
# Vérifiez que PostgreSQL est prêt
kubectl get pods -n production | grep postgres

# Vérifiez les logs PostgreSQL
kubectl logs postgres-deployment-xxx -n production
```

---

## 📚 Documentation additionnelle

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [Express.js Guide](https://expressjs.com/)
- [PostgreSQL Docker](https://hub.docker.com/_/postgres)

---

## 👨‍💻 Auteur

**Mustapha Kammoun**  
Projet PFE - DevOps Engineer  
GitHub : [@mustapha123450](https://github.com/mustapha123450)

---

## 📝 Licence

Ce projet est sous licence MIT.

---

## ✅ Checklist Final

- ✅ Application REST API fonctionnelle
- ✅ Containerisation Docker
- ✅ Kubernetes deployment avec haute disponibilité
- ✅ Pipeline CI/CD automatisé
- ✅ Health checks et readiness probes
- ✅ Secrets et ConfigMaps
- ✅ HorizontalPodAutoscaler
- ✅ Rolling updates sans downtime
- ✅ Self-hosted runner configuré
- ✅ GitHub Container Registry (ghcr.io)
- ✅ Monitoring et logs (via kubectl)

---

**🎉 Projet DevOps complet et fonctionnel !**
