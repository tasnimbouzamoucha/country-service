pipeline {
    agent any
    tools {
        jdk 'JDK21'
        maven 'M2_HOME'
    }
    stages {
        stage('Hello') {
            steps {
                echo 'Démarrage du pipeline CI pour country-service'
            }
        }
        stage('Checkout code') {
            steps {
                git branch: 'main', url: 'https://github.com/tasnimbouzamoucha/country-service.git'
            }
        }
        stage('Compile code') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('Test code') {
            steps {
                sh 'mvn test'
            }
            post {
                success {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        stage('Package code') {
            steps {
                sh 'mvn package'
            }
        }
    }
}
