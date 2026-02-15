pipeline {
  agent any
  
  environment {
    DOCKER_IMAGE = "my-rest-api"
    DOCKER_TAG = "${BUILD_NUMBER}-${GIT_COMMIT.take(8)}"
    REGISTRY = "ghcr.io"
    REGISTRY_PATH = "ghcr.io/mustapha123450/devopspfejenkins"
    K8S_NAMESPACE = "production"
  }
  
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    
    stage('Install Dependencies') {
      steps {
        sh 'npm ci'
      }
    }
    
    stage('Test') {
      steps {
        sh 'npm run lint || true'
        sh 'npm test'
      }
    }
    
    stage('Build Docker Image') {
      steps {
        script {
          // Construction avec le bon chemin (tout en minuscules)
          docker.build("${REGISTRY_PATH}/${DOCKER_IMAGE}:${DOCKER_TAG}")
          docker.build("${REGISTRY_PATH}/${DOCKER_IMAGE}:latest")
        }
      }
    }
    
    stage('Push to GitHub Container Registry') {
      steps {
        script {
          docker.withRegistry("https://${REGISTRY}", 'github-container-registry') {
            docker.image("${REGISTRY_PATH}/${DOCKER_IMAGE}:${DOCKER_TAG}").push()
            docker.image("${REGISTRY_PATH}/${DOCKER_IMAGE}:latest").push()
          }
        }
      }
    }
    
    stage('Deploy to Kubernetes') {
      steps {
        sh """
          # Appliquer la configuration (créer si n'existe pas)
          kubectl apply -f k8s/all-in-one.yaml -n ${K8S_NAMESPACE} || true
          
          # Mettre à jour l'image
          kubectl set image deployment/rest-api-deployment \
            rest-api=${REGISTRY_PATH}/${DOCKER_IMAGE}:${DOCKER_TAG} \
            -n ${K8S_NAMESPACE}
          
          # Attendre le déploiement
          kubectl rollout status deployment/rest-api-deployment \
            -n ${K8S_NAMESPACE} --timeout=5m
        """
      }
    }
    
    stage('Verify Deployment') {
      steps {
        sh """
          kubectl get pods -n ${K8S_NAMESPACE}
          kubectl get services -n ${K8S_NAMESPACE}
        """
      }
    }
  }
  
  post {
    success {
      echo "✅ Déploiement réussi ! Image: ${REGISTRY_PATH}/${DOCKER_IMAGE}:${DOCKER_TAG}"
    }
    failure {
      echo "❌ Le déploiement a échoué. Vérifiez les logs ci-dessus."
    }
    always {
      echo "📝 Pipeline terminé à ${new Date()}"
    }
  }
}