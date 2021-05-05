pipeline{
  agent any
  stages{
    stage('Build Backend'){
      steps{
        sh 'mvn clean package -DskipTests=true'
      }
    }
    stage('Unit Tests'){
      steps{
        sh 'mvn test'
      }
    }
    stage('Sonar Analysis'){
      environment{
        scannerHome = tool 'SONAR_SCANNER'
      }
      steps{
        withSonarQubeEnv('SONAR_LOCAL'){
          sh "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBack -Dsonar.host.url=http://localhost:9000 -Dsonar.login=99055c633395ddfae1780a4dff3b1d40b00fcf53 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"
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
  }
}
