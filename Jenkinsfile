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
                echo '📦 Code récupéré'
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
            }
        }
        
        stage('Test') {
            steps {
                sh 'npm test'
                echo '🧪 Tests OK'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:${IMAGE_TAG}")
                    docker.build("${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:latest")
                }
                echo '🐳 Images construites'
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
                    sh """
                        # Appliquer la configuration (créer si n'existe pas)
                        kubectl apply -f k8s/all-in-one.yaml -n ${K8S_NAMESPACE} || true
                        
                        # Mettre à jour l'image
                        kubectl set image deployment/rest-api-deployment \
                            rest-api=${DOCKER_REGISTRY}/${REPO_OWNER}/${REPO_NAME}/${DOCKER_IMAGE}:${IMAGE_TAG} \
                            -n ${K8S_NAMESPACE}
                        
                        # Attendre le déploiement
                        kubectl rollout status deployment/rest-api-deployment \
                            -n ${K8S_NAMESPACE} --timeout=5m
                    """
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
                        
                        echo '\n📊 Services :'
                        kubectl get services -n ${K8S_NAMESPACE}
                        
                        echo '\n📊 Déploiement :'
                        kubectl describe deployment rest-api-deployment -n ${K8S_NAMESPACE} | grep -E "Replicas|Image"
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
        }
        failure {
            echo "❌ Le pipeline a échoué. Vérifiez les logs ci-dessus."
        }
        always {
            echo "📝 Pipeline terminé à ${new Date()}"
        }
    }
}