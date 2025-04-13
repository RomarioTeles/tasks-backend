pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_PROJECT_KEY = 'DeployBack'
        SONAR_LOGIN = '99055c633395ddfae1780a4dff3b1d40b00fcf53'
        MAVEN_HOME = tool 'Maven'  // Especificando o Maven, caso tenha várias versões configuradas
        TOMCAT_URL = 'http://localhost:8001'
        TOMCAT_CREDENTIALS_ID = 'TomcatLogin'  // Id da credencial do Tomcat
    }

    stages {
        stage('Build Backend') {
            steps {
                bat "${MAVEN_HOME}/bin/mvn clean package -DskipTests=true"
            }
        }

        stage('Unit Tests') {
            steps {
                bat "${MAVEN_HOME}/bin/mvn test"
            }
        }

        stage('Sonar Analysis') {
            environment {
                scannerHome = tool 'SONAR_SCANNER'
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL') {
                    bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_LOGIN} -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy Backend') {
            steps {
                deploy adapters: [tomcat8(credentialsId: TOMCAT_CREDENTIALS_ID, path: '', url: TOMCAT_URL)], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }

        stage('API Tests') {
            steps {
                dir('api-test') {
                    deleteDir()  // Limpa o diretório antes de clonar
                    git 'https://github.com/RomarioTeles/tasks-api-test'
                    bat "${MAVEN_HOME}/bin/mvn test"
                }
            }
        }

        stage('Deploy FrontEnd') {
            steps {
                dir('frontend') {
                    deleteDir()  // Limpa o diretório antes de clonar
                    git 'https://github.com/RomarioTeles/tasks-frontend'                 
                    bat "${MAVEN_HOME}/bin/mvn clean package -DskipTests=true"

                    deploy adapters: [tomcat8(credentialsId: TOMCAT_CREDENTIALS_ID, path: '', url: TOMCAT_URL)], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }

        stage('Funcional Test') {
            steps {
                dir('funcional-test') {
                    deleteDir()  // Limpa o diretório antes de clonar
                    git 'https://github.com/RomarioTeles/tasks-funcional-test'
                    bat "${MAVEN_HOME}/bin/mvn test"
                }
            }
        }
    }

    post {
        success {
            echo 'Build and deploy were successful!'
        }

        failure {
            emailext(
                subject: "Build Failed: ${currentBuild.fullDisplayName}",
                body: "Build failed! Please check the Jenkins logs for details.",
                to: 'rommario20@gmail.com'
            )
        }
    }
}
