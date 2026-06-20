pipeline {
    agent any

    environment {
        IMAGE_NAME = "rag-assistant"
        ACR_NAME   = "yourAzureContainerRegistry"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean verify'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${BUILD_NUMBER} ."
            }
        }

        stage('Push to ACR') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'acr-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh "docker login ${ACR_NAME}.azurecr.io -u $USER -p $PASS"
                    sh "docker push ${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${BUILD_NUMBER}"
                }
            }
        }

        stage('Deploy to Azure VM') {
            steps {
                sshagent(['azure-vm-ssh-key']) {
                    sh """
                    ssh azureuser@your-vm-ip '
                      docker pull ${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${BUILD_NUMBER} &&
                      docker stop rag-assistant || true &&
                      docker rm rag-assistant || true &&
                      docker run -d --name rag-assistant -p 8080:8080 \
                        --env-file /home/azureuser/rag.env \
                        ${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${BUILD_NUMBER}
                    '
                    """
                }
            }
        }
    }

    post {
        failure {
            echo 'Build or deployment failed -- check console output.'
        }
    }
}
