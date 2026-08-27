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

    stage('Deploy') {

        if (deploy) {

            echo "Deploying ${dockerImage}:${dockerTag} on EC2"

    sh """
        # Stop and remove existing container if it exists
        docker rm -f ${appName} 2>/dev/null || true

        # Pull latest image from Docker Hub
        docker pull ${dockerImage}:${dockerTag}

        # Run new container
        docker run -d \
            --name ${appName} \
            -p 9090:8080 \
            ${dockerImage}:${dockerTag}

        # Show running container
        docker ps
    """

        } else {

            echo "DEPLOY=false"
            echo "Skipping deployment"

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
        ==========================================
        """
    }
}
