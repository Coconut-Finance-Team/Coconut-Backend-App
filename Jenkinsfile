pipeline {
    agent any
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials-id')
        DOCKER_IMAGE = "castlehoo/backend"
        DOCKER_TAG = "${BUILD_NUMBER}"
        ARGOCD_CREDENTIALS = credentials('argocd-auth')
        KUBE_CONFIG = credentials('eks-kubeconfig')
        GIT_CREDENTIALS = credentials('github-token')
    }
    stages {
        stage('Check Commit Message') {
            steps {
                script {
                    def commitMessage = sh(
                        script: 'git log -1 --pretty=%B',
                        returnStdout: true
                    ).trim()
                    if (commitMessage.startsWith("Update deployment to version")) {
                        currentBuild.result = 'ABORTED'
                        error("Skipping build for deployment update commit")
                    }
                }
            }
        }
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Spring Application') {
            steps {
                script {
                    sh './gradlew clean build -x test'
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                script {
                    sh "echo ${DOCKER_HUB_CREDENTIALS_PSW} | docker login -u ${DOCKER_HUB_CREDENTIALS_USR} --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }
        stage('Update Kubernetes Manifests') {
            steps {
                script {
                    sh """
                        git config user.email "jenkins@example.com"
                        git config user.name "Jenkins"
                        git remote set-url origin https://${GIT_CREDENTIALS_USR}:${GIT_CREDENTIALS_PSW}@github.com/castlhoo/k8s-manifests.git
                        git config pull.rebase false
                        git checkout main
                        git pull origin main
                        sed -i 's|image: ${DOCKER_IMAGE}:.*|image: ${DOCKER_IMAGE}:${DOCKER_TAG}|' backend/deployment.yaml
                        git add backend/deployment.yaml
                        git commit -m "Update backend deployment to version ${DOCKER_TAG}"
                        git push origin main
                    """
                }
            }
        }
        stage('Sync ArgoCD Application') {
            steps {
                script {
                    sh """
                        export KUBECONFIG=${KUBE_CONFIG}
                        argocd login ab43828f8bac94f8b9d8bd97a8d579c0-700015596.ap-northeast-2.elb.amazonaws.com --username ${ARGOCD_CREDENTIALS_USR} --password ${ARGOCD_CREDENTIALS_PSW} --insecure
                        argocd app sync backend-app --prune
                        argocd app wait backend-app --health
                        argocd logout ab43828f8bac94f8b9d8bd97a8d579c0-700015596.ap-northeast-2.elb.amazonaws.com
                    """
                }
            }
        }
    }
    post {
        always {
            sh 'docker logout'
        }
    }
}