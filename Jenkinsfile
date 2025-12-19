pipeline {
  agent any

  options {
    timestamps()
    // Let Jenkins automatically checkout the repo that contains this Jenkinsfile
    // (boulifa25/devops_finale), which has the Maven project (pom.xml).
    skipDefaultCheckout(false)
  }

  environment {
    DOCKER_IMAGE = "boulifa25/student-management:latest"
    NEXUS_REPO   = "http://localhost:8081/repository/maven-public/"
    KUBE_NS      = "devops"
  }

  stages {
    // Source code is checked out automatically by Jenkins based on the job SCM configuration.

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
       2. PREPARE MAVEN (local download)
    ======================== */
    stage('Prepare Maven') {
      steps {
        sh '''
          set -e
          MAVEN_VERSION=3.9.9
          MAVEN_DIR="apache-maven-${MAVEN_VERSION}"

          if [ ! -d "$MAVEN_DIR" ]; then
            echo "Downloading Maven $MAVEN_VERSION..."
            curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -o maven.tgz
            tar -xzf maven.tgz
            rm maven.tgz
          fi

          ./${MAVEN_DIR}/bin/mvn -v
        '''
      }
    }

   

    /* =======================
       3. COMPILE
    ======================== */
    stage('Maven Compile') {
      steps {
        sh '''
          MAVEN_VERSION=3.9.9
          MAVEN_DIR="apache-maven-${MAVEN_VERSION}"
          if [ -f settings.xml ]; then
            ./${MAVEN_DIR}/bin/mvn -s settings.xml clean compile
          else
            echo "settings.xml not found, using default Maven settings"
            ./${MAVEN_DIR}/bin/mvn clean compile
          fi
        '''
      }
    }

    /* =======================
       4. UNIT TESTS
    ======================== */
    stage('Unit Tests (JUnit)') {
      steps {
        sh '''
          MAVEN_VERSION=3.9.9
          MAVEN_DIR="apache-maven-${MAVEN_VERSION}"
          if [ -f settings.xml ]; then
            ./${MAVEN_DIR}/bin/mvn -s settings.xml test
          else
            ./${MAVEN_DIR}/bin/mvn test
          fi
        '''
      }
    }

    /* =======================
       5. CODE COVERAGE
    ======================== */
    stage('JaCoCo Coverage') {
      steps {
        sh '''
          MAVEN_VERSION=3.9.9
          MAVEN_DIR="apache-maven-${MAVEN_VERSION}"
          if [ -f settings.xml ]; then
            ./${MAVEN_DIR}/bin/mvn -s settings.xml jacoco:report
          else
            ./${MAVEN_DIR}/bin/mvn jacoco:report
          fi
        '''
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
              MAVEN_VERSION=3.9.9
              MAVEN_DIR="apache-maven-${MAVEN_VERSION}"
              if [ -f settings.xml ]; then
                ./${MAVEN_DIR}/bin/mvn -s settings.xml clean verify sonar:sonar \
                  -Dsonar.projectKey=tn.esprit:student-management \
                  -Dsonar.projectVersion=${BUILD_NUMBER} \
                  -Dsonar.token=$SONAR_TOKEN \
                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
              else
                ./${MAVEN_DIR}/bin/mvn clean verify sonar:sonar \
                  -Dsonar.projectKey=tn.esprit:student-management \
                  -Dsonar.projectVersion=${BUILD_NUMBER} \
                  -Dsonar.token=$SONAR_TOKEN \
                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
              fi
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
        sh '''
          MAVEN_VERSION=3.9.9
          MAVEN_DIR="apache-maven-${MAVEN_VERSION}"
          if [ -f settings.xml ]; then
            ./${MAVEN_DIR}/bin/mvn -s settings.xml package -DskipTests
          else
            ./${MAVEN_DIR}/bin/mvn package -DskipTests
          fi
        '''
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
