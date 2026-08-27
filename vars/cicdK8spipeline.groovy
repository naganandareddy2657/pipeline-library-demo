def call(Map config = [:]) {

    def appName     = config.appName
    def environment = config.environment
    def dockerTag   = config.dockerTag
    def deploy      = config.deploy

    def dockerImage = "naganandareddy7/${appName}"

    stage('Build') {

        echo "Building application: ${appName}"

        sh '''
            mvn clean package
        '''
    }

    stage('Docker Build') {

        echo "Building Docker image: ${dockerImage}:${dockerTag}"

        sh """
            docker build \
                -t ${dockerImage}:${dockerTag} .
        """
    }

    stage('Docker Login & Push') {

        echo "Pushing Docker image: ${dockerImage}:${dockerTag}"

        withCredentials([
            usernamePassword(
                credentialsId: 'Docker_credentails',
                usernameVariable: 'DOCKER_USERNAME',
                passwordVariable: 'DOCKER_PASSWORD'
            )
        ]) {

            sh """
                echo "\$DOCKER_PASSWORD" | docker login \
                    -u "\$DOCKER_USERNAME" \
                    --password-stdin

                docker push ${dockerImage}:${dockerTag}
            """
        }
    }

    stage('Kubernetes Deploy') {

        if (deploy) {

            echo "Deploying ${dockerImage}:${dockerTag} to Kubernetes"
            echo "Environment: ${environment}"

            sh """
                echo "=========================================="
                echo "KUBERNETES DEPLOYMENT"
                echo "=========================================="

                echo "Application : ${appName}"
                echo "Environment : ${environment}"
                echo "Image       : ${dockerImage}:${dockerTag}"

                echo ""
                echo "Applying Deployment..."

                kubectl apply -f k8s/deployment.yaml

                echo ""
                echo "Applying Service..."

                kubectl apply -f k8s/service.yaml

                echo ""
                echo "Updating Deployment Image..."

                kubectl set image deployment/${appName} \
                    ${appName}=${dockerImage}:${dockerTag}

                echo ""
                echo "Checking Deployment..."

                kubectl rollout status deployment/${appName}

                echo ""
                echo "Pods:"

                kubectl get pods -o wide

                echo ""
                echo "Services:"

                kubectl get svc

            """

        } else {

            echo "DEPLOY=false"
            echo "Skipping Kubernetes deployment"

        }
    }

    stage('Kubernetes Port Forward') {

        if (deploy) {

            echo "Starting Kubernetes port forwarding"

            sh """
                # Stop any previous port-forward process for this application
                pkill -f "kubectl port-forward service/${appName}-service 9090:8080" || true

                # Start port-forward in background
                nohup kubectl port-forward \
                    service/${appName}-service \
                    9090:8080 \
                    > /tmp/${appName}-port-forward.log 2>&1 &

                sleep 5

                echo "Port forwarding started"

                cat /tmp/${appName}-port-forward.log || true

                echo ""
                echo "Application available on EC2:"
                echo "http://localhost:9090"

            """

        } else {

            echo "DEPLOY=false"
            echo "Skipping port forwarding"

        }
    }

    stage('CI/CD Completed') {

        echo """
        ==========================================
        CI/CD PIPELINE COMPLETED
        ==========================================
        Application : ${appName}
        Environment : ${environment}
        Docker Tag  : ${dockerTag}
        Deploy      : ${deploy}
        Docker Image: ${dockerImage}:${dockerTag}
        Kubernetes  : ${deploy ? 'DEPLOYED' : 'SKIPPED'}
        Port         : 9090
        ==========================================
        """
    }
}

