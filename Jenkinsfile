pipeline {
  agent any

  options {
    timestamps()
    skipDefaultCheckout(true)
  }

  environment {
    DOCKER_IMAGE = "boulifa25/student-management:latest"
    NEXUS_REPO   = "http://localhost:8081/repository/maven-public/"
    KUBE_NS      = "devops"
  }

  stages {
    /* =======================
       1. SOURCE CONTROL
    ======================== */
    stage('Checkout Source Code') {
      steps {
        git branch: 'main',
            url: 'https://github.com/boulifa25/reimagined-guide.git'
      }
    }

    stage('Prepare kubectl') {
      steps {
        sh '''
          set -e
          if [ ! -f ./kubectl ]; then
            echo "Downloading kubectl into workspace..."
            curl -LO https://dl.k8s.io/release/$(curl -Ls https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl
            chmod +x ./kubectl
          fi
          ./kubectl version --client
        '''
      }
    }

   

    /* =======================
       3. COMPILE
    ======================== */
    stage('Maven Compile') {
      steps {
        sh '''
          chmod +x mvnw
          ./mvnw -s settings.xml clean compile
        '''
      }
    }

    /* =======================
       4. UNIT TESTS
    ======================== */
    stage('Unit Tests (JUnit)') {
      steps {
        sh './mvnw -s settings.xml test'
      }
    }

    /* =======================
       5. CODE COVERAGE
    ======================== */
    stage('JaCoCo Coverage') {
      steps {
        sh './mvnw -s settings.xml jacoco:report'
      }
    }

    /* =======================
       6. SONARQUBE
    ======================== */
    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonarqube-docker') {
          withCredentials([string(credentialsId: 'sonar-token-student', variable: 'SONAR_TOKEN')]) {
            sh '''
              ./mvnw -s settings.xml clean verify sonar:sonar \
                -Dsonar.projectKey=tn.esprit:student-management \
                -Dsonar.projectVersion=${BUILD_NUMBER} \
                -Dsonar.token=$SONAR_TOKEN \
                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
            '''
          }
        }
      }
    }

    /* =======================
       7. PACKAGE APPLICATION
    ======================== */
    stage('Maven Package') {
      steps {
        sh './mvnw -s settings.xml package -DskipTests'
      }
    }

    /* =======================
       8. DOCKER IMAGE
    ======================== */
    stage('Build Docker Image') {
      steps {
        sh 'docker build -t ${DOCKER_IMAGE} -f Docker/student-app/Dockerfile .'
      }
    }

    stage('Push Docker Image') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: "dockerhub-creds",
          usernameVariable: 'DOCKER_USER',
          passwordVariable: 'DOCKER_PASS'
        )]) {
          sh '''
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push ${DOCKER_IMAGE}
          '''
        }
      }
    }

    /* =======================
       9. KUBERNETES
    ======================== */
    stage('Deploy to Kubernetes') {
      steps {
        sh '''
          set -e
          ./kubectl apply -n ${KUBE_NS} -f k8s/mysql-deployment.yaml
          ./kubectl apply -n ${KUBE_NS} -f k8s/spring-deployment.yaml

          ./kubectl rollout status -n ${KUBE_NS} deployment/mysql
          ./kubectl rollout status -n ${KUBE_NS} deployment/student-app
        '''
      }
    }

    
    stage('Deploy Monitoring (Prometheus & Grafana)') {
      steps {
        sh '''
          set -e
          echo "=== DEPLOY PROMETHEUS + GRAFANA ==="
          ./kubectl apply -n ${KUBE_NS} -f k8s/monitoring.yaml

          echo "=== WAIT FOR ROLLOUT ==="
          ./kubectl rollout status -n ${KUBE_NS} deployment/prometheus
          ./kubectl rollout status -n ${KUBE_NS} deployment/grafana

          echo "=== SERVICES ==="
          ./kubectl get svc -n ${KUBE_NS} prometheus grafana -o wide
        '''
      }
    }
    

    /* =======================
       10. APPLICATION CHECK
    ======================== */
    stage('Health Check (Spring Actuator)') {
      steps {
        sh '''
          set -e
          NODEPORT=$(./kubectl get svc spring-service -n ${KUBE_NS} -o=jsonpath='{.spec.ports[0].nodePort}')
          NODEIP=$(./kubectl get node minikube -o jsonpath='{.status.addresses[?(@.type=="InternalIP")].address}')

          curl -f http://$NODEIP:$NODEPORT/student/actuator/health
        '''
      }
    }
  }
}
