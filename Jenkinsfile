pipeline {
  agent any

  options {
    timestamps()
    skipDefaultCheckout(true)
  }

  environment {
    MAVEN_VERSION = "3.9.9"
    MAVEN_DIR     = "apache-maven-${MAVEN_VERSION}"
    DOCKER_IMAGE  = "boulifa25/student-management:latest"
    KUBE_NS       = "devops"
  }

  stages {

    /* =======================
       1. SOURCE CONTROL
    ======================== */
    stage('Checkout Source Code') {
      steps {
        git branch: 'main',
            url: 'https://github.com/boulifa25/devops_finale.git'
      }
    }

    /* =======================
       2. PREPARE KUBECTL
    ======================== */
    stage('Prepare kubectl') {
      steps {
        sh '''
          set -e
          if [ ! -f ./kubectl ]; then
            curl -LO https://dl.k8s.io/release/$(curl -Ls https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl
            chmod +x kubectl
          fi
          ./kubectl version --client
        '''
      }
    }

    /* =======================
       3. PREPARE MAVEN
    ======================== */
    stage('Prepare Maven') {
      steps {
        sh '''
          set -e
          if [ ! -d "$MAVEN_DIR" ]; then
            curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -o maven.tgz
            tar -xzf maven.tgz
            rm maven.tgz
          fi
          ./${MAVEN_DIR}/bin/mvn -v
        '''
      }
    }

    /* =======================
       4. BUILD + TESTS
    ======================== */
    stage('Compile & Unit Tests') {
      steps {
        sh '''
          ./${MAVEN_DIR}/bin/mvn clean test
        '''
      }
    }

    /* =======================
       5. JACOCO
    ======================== */
    stage('JaCoCo Coverage') {
      steps {
        sh '''
          ./${MAVEN_DIR}/bin/mvn jacoco:report
        '''
      }
    }

    /* =======================
       6. SONARQUBE ANALYSIS
    ======================== */
    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonarqube-docker') {
          sh '''
            ./${MAVEN_DIR}/bin/mvn verify sonar:sonar \
              -Dsonar.projectKey=tn.esprit:student-management \
              -Dsonar.projectVersion=${BUILD_NUMBER} \
              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
          '''
        }
      }
    }

    /* =======================
       7. PACKAGE
    ======================== */
    stage('Maven Package') {
      steps {
        sh '''
          ./${MAVEN_DIR}/bin/mvn package -DskipTests
        '''
      }
    }

    /* =======================
       8. DOCKER
    ======================== */
    stage('Build Docker Image') {
      steps {
        sh '''
          docker build -t ${DOCKER_IMAGE} -f Docker/student-app/Dockerfile .
        '''
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
       9. KUBERNETES DEPLOY
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

    /* =======================
       10. MONITORING
    ======================== */
    stage('Deploy Monitoring') {
      steps {
        sh '''
          ./kubectl apply -n ${KUBE_NS} -f k8s/monitoring.yaml
          ./kubectl rollout status -n ${KUBE_NS} deployment/prometheus
          ./kubectl rollout status -n ${KUBE_NS} deployment/grafana
        '''
      }
    }

    /* =======================
       11. HEALTH CHECK
    ======================== */
    stage('Health Check') {
      steps {
        sh '''
          NODEPORT=$(./kubectl get svc spring-service -n ${KUBE_NS} -o=jsonpath='{.spec.ports[0].nodePort}')
          NODEIP=$(./kubectl get node minikube -o=jsonpath='{.status.addresses[?(@.type=="InternalIP")].address}')
          curl -f http://$NODEIP:$NODEPORT/student/actuator/health
        '''
      }
    }
  }
}
