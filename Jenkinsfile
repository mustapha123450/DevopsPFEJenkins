pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'ghcr.io'
        DOCKER_IMAGE = 'my-rest-api'
        REPO_OWNER = 'mustapha123450'
        REPO_NAME = 'devopspfejenkins'
        IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT.take(8)}"
        K8S_NAMESPACE = 'production'
    }
    
    tools {
        nodejs 'NodeJS-18'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo '📦 Code récupéré avec succès'
            }
        }
        
        stage('Install Dependencies') {
            steps {
                sh 'npm ci'
                echo '📚 Dépendances installées'
            }
        }
        
        stage('Lint') {
            steps {
                sh 'npm run lint || true'
                echo '✅ Linting terminé'
            }
        }
        
        stage('Test') {
            steps {
                sh 'npm test'
                echo '🧪 Tests passés avec succès'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:${IMAGE_TAG}")
                    docker.build("${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:latest")
                }
                echo '🐳 Images Docker construites'
            }
        }
        
        stage('Push to GitHub Container Registry') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'github-container-registry') {
                        docker.image("${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:${IMAGE_TAG}").push()
                        docker.image("${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:latest").push()
                    }
                }
                echo '📤 Images poussées sur ghcr.io'
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                withKubeConfig([credentialsId: 'kubeconfig']) {
                    withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                        sh """
                            # 1️⃣ Créer le secret pour ghcr.io (nécessaire pour les images privées)
                            kubectl create secret docker-registry ghcr-secret \\
                                --docker-server=${DOCKER_REGISTRY} \\
                                --docker-username=${REPO_OWNER} \\
                                --docker-password=${GITHUB_TOKEN} \\
                                --namespace=${K8S_NAMESPACE} \\
                                --dry-run=client -o yaml | kubectl apply -f -
                            
                            # 2️⃣ ✅ APPLIQUER TOUTE LA CONFIGURATION (POSTGRESQL + API)
                            kubectl apply -f k8s/all-in-one.yaml -n ${K8S_NAMESPACE}
                            
                            # 3️⃣ Mettre à jour l'image de l'API avec le nouveau tag
                            kubectl set image deployment/rest-api-deployment \\
                                rest-api=${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:${IMAGE_TAG} \\
                                -n ${K8S_NAMESPACE}
                            
                            # 4️⃣ Attendre que le déploiement soit terminé
                            kubectl rollout status deployment/rest-api-deployment \\
                                -n ${K8S_NAMESPACE} --timeout=5m
                        """
                    }
                }
                echo '🚀 Déploiement effectué'
            }
        }
        
        stage('Verify Deployment') {
            steps {
                withKubeConfig([credentialsId: 'kubeconfig']) {
                    sh """
                        echo '📊 Pods :'
                        kubectl get pods -n ${K8S_NAMESPACE}
                        
                        echo '\\n📊 Services :'
                        kubectl get services -n ${K8S_NAMESPACE}
                        
                        echo '\\n📊 Déploiement API :'
                        kubectl describe deployment rest-api-deployment -n ${K8S_NAMESPACE} | grep -E "Replicas|Image"
                        
                        echo '\\n📊 PostgreSQL :'
                        kubectl get pods -n ${K8S_NAMESPACE} | grep postgres
                    """
                }
                echo '✅ Déploiement vérifié'
            }
        }
    }
    
    post {
        success {
            echo "🎉 Pipeline terminé avec succès !"
            echo "Image: ${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:${IMAGE_TAG}"
            echo "📌 Pour tester : kubectl port-forward -n production service/rest-api-service 8080:80"
        }
        failure {
            echo "❌ Le pipeline a échoué. Vérifiez les logs ci-dessus."
            echo "🔍 Conseil: kubectl describe pods -n production | grep -A 10 Events"
        }
        always {
            echo "📝 Pipeline terminé à ${new Date()}"
        }
    }
}