pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        IMAGE_NAME = "ctscreener"
        REGISTRY   = "registry.example.com"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn verify -q'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${REGISTRY}/${IMAGE_NAME}:${env.BUILD_NUMBER} ."
                sh "docker tag ${REGISTRY}/${IMAGE_NAME}:${env.BUILD_NUMBER} ${REGISTRY}/${IMAGE_NAME}:latest"
            }
        }

        stage('Docker Push') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'registry-creds',
                                                   usernameVariable: 'REGISTRY_USER',
                                                   passwordVariable: 'REGISTRY_PASS')]) {
                    sh "echo $REGISTRY_PASS | docker login ${REGISTRY} -u $REGISTRY_USER --password-stdin"
                    sh "docker push ${REGISTRY}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
                    sh "docker push ${REGISTRY}/${IMAGE_NAME}:latest"
                }
            }
        }
    }

    post {
        failure {
            mail to: 'dev-team@example.com',
                 subject: "FAILED: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                 body: "Build failed. See: ${env.BUILD_URL}"
        }
    }
}
