pipeline {
    agent any
    tools {
        jdk 'JDK21'
        maven 'M2_HOME'
    }
    environment {
        NEXUS_URL = 'http://localhost:8081/repository/reports/country-service'
    }
    stages {
        stage('Checkout code') {
            steps {
                git branch: 'main', url: 'https://github.com/tasnimbouzamoucha/country-service.git'
            }
        }
        stage('Compile, test and coverage') {
            steps {
                sh 'mvn clean verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(installationName: 'MySonarQubeServer', credentialsId: 'sonarqubePWD') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=country-service -Dsonar.projectName=country-service'
                }
            }
        }
        stage('Deploy jar to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
        stage('Upload reports to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PWD')]) {
                    sh '''
                        zip -rq jacoco-report-${BUILD_NUMBER}.zip target/site/jacoco
                        zip -rq surefire-reports-${BUILD_NUMBER}.zip target/surefire-reports
                        curl -f -u $NEXUS_USER:$NEXUS_PWD --upload-file jacoco-report-${BUILD_NUMBER}.zip $NEXUS_URL/jacoco-report-${BUILD_NUMBER}.zip
                        curl -f -u $NEXUS_USER:$NEXUS_PWD --upload-file surefire-reports-${BUILD_NUMBER}.zip $NEXUS_URL/surefire-reports-${BUILD_NUMBER}.zip
                    '''
                }
            }
        }
    }
}
