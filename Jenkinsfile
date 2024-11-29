pipeline {
    agent any
    
    triggers {
        githubPush()
    }
    
    environment {
        ECR_REPOSITORY = "castlehoo/backend"
        DOCKER_TAG = "${BUILD_NUMBER}"
        ARGOCD_CREDENTIALS = credentials('argocd-token')
        KUBE_CONFIG = credentials('eks-kubeconfig')
        GIT_CREDENTIALS = credentials('github-token')
        AWS_CREDENTIALS = credentials('aws-credentials')
        CHANGES_DETECTED = 'false'
    }
    
    stages {
        stage('Check for Changes') {
            steps {
                script {
                    echo "변경사항 감지 검사 시작..."
                    def changes = sh(
                        script: '''
                            git fetch origin
                            git diff HEAD@{1} --name-only | grep -v "k8s/deployment.yaml" || true
                        ''',
                        returnStdout: true
                    ).trim()
                    
                    if (changes.isEmpty()) {
                        currentBuild.result = 'ABORTED'
                        error('No relevant changes detected')
                    } else {
                        env.CHANGES_DETECTED = 'true'
                    }
                }
            }
        }

        stage('Check Commit Message') {
            when {
                expression { env.CHANGES_DETECTED == 'true' }
            }
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        def commitMessage = sh(
                            script: 'git log -1 --pretty=%B',
                            returnStdout: true
                        ).trim()
                        if (commitMessage.startsWith("Update deployment to version")) {
                            currentBuild.result = 'ABORTED'
                            error("배포 업데이트 커밋으로 인한 빌드 스킵")
                        }
                    }
                }
            }
        }

        stage('Checkout') {
            when {
                expression { env.CHANGES_DETECTED == 'true' }
            }
            steps {
                checkout scm
            }
        }

        stage('Build Spring Boot Application') {
            when {
                expression { env.CHANGES_DETECTED == 'true' }
            }
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh '''
                            chmod +x ./gradlew
                            ./gradlew clean build -x test
                        '''
                    }
                }
            }
        }

        stage('Build Docker Image') {
            when {
                expression { env.CHANGES_DETECTED == 'true' }
            }
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            docker build -t ${ECR_REPOSITORY}:${DOCKER_TAG} .
                        """
                    }
                }
            }
        }

        stage('Push to AWS ECR') {
            when {
                expression { env.CHANGES_DETECTED == 'true' }
            }
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    withCredentials([[
                        $class: 'AmazonWebServicesCredentialsBinding',
                        credentialsId: 'aws-credentials',
                        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                    ]]) {
                        script {
                            sh """
                                aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 992382629018.dkr.ecr.ap-northeast-2.amazonaws.com
                                docker tag ${ECR_REPOSITORY}:${DOCKER_TAG} 992382629018.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}
                                docker push 992382629018.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}
                            """
                        }
                    }
                }
            }
        }

        stage('Update Kubernetes Manifests') {
            when {
                expression { env.CHANGES_DETECTED == 'true' }
            }
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            git config --global user.email "jenkins@example.com"
                            git config --global user.name "Jenkins"
                            
                            git remote set-url origin https://${GIT_CREDENTIALS_USR}:${GIT_CREDENTIALS_PSW}@github.com/Coconut-Finance-Team/Coconut-Back-App.git
                            git checkout main
                            
                            mkdir -p k8s
                            cat << EOF > k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend-deployment
  namespace: coconut-backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: 992382629018.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}
EOF
                            
                            git add k8s/deployment.yaml
                            git commit -m "Update backend deployment to version ${DOCKER_TAG}"
                            git push origin main
                        """
                    }
                }
            }
        }

        stage('Sync ArgoCD Application') {
            when {
                expression { env.CHANGES_DETECTED == 'true' }
            }
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            export KUBECONFIG=${KUBE_CONFIG}
                            ARGOCD_SERVER="afd51e96d120b4dce86e1aa21fe3316d-787997945.ap-northeast-2.elb.amazonaws.com"
                            
                            argocd login \${ARGOCD_SERVER} \
                                --core \
                                --auth-token ${ARGOCD_CREDENTIALS} \
                                --grpc-web \
                                --insecure \
                                --plaintext
                            
                            argocd app sync backend-app
                            argocd app wait backend-app --sync --health --timeout 300
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout'
            sh 'rm -f ${KUBE_CONFIG}'
        }
    }
}