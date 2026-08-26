def call(Map config = [:]) {

    def appName = config.appName
    def environment = config.environment
    def version = config.version
    def deploy = config.deploy

    echo "======================================"
    echo "Application : ${appName}"
    echo "Environment : ${environment}"
    echo "Version     : ${version}"
    echo "Deploy      : ${deploy}"
    echo "======================================"

    stage('Build') {

        echo "Building ${appName}"

        sh """
            echo "Running build for ${appName}"
            mvn clean package
        """
    }

    stage('Test') {

        echo "Running tests"

        // sh '''
        //     mvn test
        // '''
    }

    stage('Docker Build') {

        echo "Building Docker image"

        // sh """
        //     docker build \
        //     -t ${appName}:${version} .
        // """
    }

    stage('Docker Push') {

        echo "Pushing Docker image"

        // sh """
        //     echo "Pushing ${appName}:${version}"
        //     # docker push ${appName}:${version}
        // """
    }

    stage('Deploy') {

        if (deploy) {

            echo "Deploying ${appName}"
            echo "Environment: ${environment}"

            sh """
                echo "Deploying ${appName}:${version} to ${environment}"
                # ./deploy.sh ${environment} ${appName} ${version}
            """

        } else {

            echo "DEPLOY=false. Skipping deployment."

        }
    }

    stage('Completed') {

        echo "======================================"
        echo "CI/CD Pipeline Completed"
        echo "Application : ${appName}"
        echo "Environment : ${environment}"
        echo "Version     : ${version}"
        echo "======================================"
    }
}
