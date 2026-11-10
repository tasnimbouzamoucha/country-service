pipeline {
    agent any
    tools {
        jdk 'JDK21'
        maven 'M2_HOME'
    }
    environment {
        NEXUS_URL = 'http://localhost:8081/repository/reports/country-service'
        // Jenkins lance par brew services ne voit pas les outils Homebrew (ansible, docker, kubectl)
        PATH = "/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
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
        // --- Etapes ajoutees : Continuous Delivery (Ansible -> Docker Hub -> Kubernetes) ---
        stage('Deploy using Ansible playbook') {
            steps {
                // Le mot de passe du vault est stocke dans Jenkins (Secret text, ID : ansible-vault-pass)
                withCredentials([string(credentialsId: 'ansible-vault-pass', variable: 'VAULT_PASS')]) {
                    sh '''
                        echo "$VAULT_PASS" > .vault_pass
                        ansible-playbook -i hosts playbookCICD.yml --vault-password-file .vault_pass
                        rm -f .vault_pass
                    '''
                }
            }
        }
        stage('Verify Kubernetes pods') {
            steps {
                sh '''
                    kubectl rollout status deployment/my-country-service -n jenkins --timeout=180s
                    kubectl get pods -n jenkins -l app=country-service
                '''
            }
        }
    }
}
