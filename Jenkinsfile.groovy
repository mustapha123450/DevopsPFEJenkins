pipeline {
  agent any
  
  environment {
    DOCKER_IMAGE = "my-rest-api"
    DOCKER_TAG = "${BUILD_NUMBER}"
    REGISTRY = "your-registry.com"
  }
  
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    
    stage('Test') {
      steps {
        sh 'npm ci'
        sh 'npm run lint'
        sh 'npm test'
      }
    }
    
    stage('Build Docker Image') {
      steps {
        script {
          docker.build("${REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}")
        }
      }
    }
    
    stage('Push to Registry') {
      steps {
        script {
          docker.withRegistry("https://${REGISTRY}", 'docker-credentials') {
            docker.image("${REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}").push()
            docker.image("${REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}").push('latest')
          }
        }
      }
    }
    
    stage('Deploy to Kubernetes') {
      steps {
        sh """
          kubectl set image deployment/rest-api-deployment \
          rest-api=${REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG} \
          -n production
          kubectl rollout status deployment/rest-api-deployment -n production
        """
      }
    }
  }
  
  post {
    success {
      echo 'Deployment successful!'
    }
    failure {
      echo 'Deployment failed!'
    }
  }
}