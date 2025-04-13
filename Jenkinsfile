pipeline{
  agent any

  stages{
    stage('Build Backend'){
      steps{
        bat 'mvn clean package -DskipTests=true'
      }
    }
    stage('Unit Tests'){
      steps{
        bat 'mvn test'
      }
    }
    stage('Sonar Analysis'){
      environment{
        scannerHome = tool 'SONAR_SCANNER'
      }
      steps{
        withSonarQubeEnv('SONAR_LOCAL'){
          bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBack -Dsonar.host.url=http://localhost:9000 -Dsonar.login=38b137b556454f5aca0c431040e996f691317840 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"
        }
      }
    }
    stage('Quality Gate'){
      steps{
        sleep(5)
        timeout(time: 1, unit: 'MINUTES'){
          waitForQualityGate abortPipeline:true
        }
      }
    }
    stage('Deploy Backend'){
      steps{
        deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
      }
    }
    stage('API Tests'){
      steps{
        dir('api-test'){
          git 'https://github.com/RomarioTeles/tasks-api-test'
          bat 'mvn test'
        }
      }
    }
    stage('Deploy FrontEnd'){
      steps{
        dir('frontend'){
          git 'https://github.com/RomarioTeles/tasks-frontend'
          bat 'mvn clean package'
          deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001')], contextPath: 'tasks', war: 'target/tasks.war'
        }
      }
    }
    stage('Funcional Test'){
      steps{
        dir('funcional-test'){
          git 'https://github.com/RomarioTeles/tasks-funcional-test'
          bat 'mvn test'
        }
      }
    }
  }
}
