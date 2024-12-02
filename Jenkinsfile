pipeline {
    agent any
    
    environment {
        AWS_ACCOUNT_ID = "992382629018"
        AWS_DEFAULT_REGION = "ap-northeast-2"
        IMAGE_REPO_NAME = "castlehoo/backend"
        IMAGE_TAG = "1"
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}"
        GIT_CREDENTIALS = credentials('GIT_CREDENTIALS')
    }

    stages {
        stage('Prepare') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        // Clean workspace
                        sh 'rm -rf *'
                        // Checkout code
                        checkout scm
                    }
                }
            }
        }

        stage('Build') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            chmod +x gradlew
                            ./gradlew clean build -x test
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            docker build -t ${REPOSITORY_URI}:${IMAGE_TAG} .
                        """
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com
                            docker push ${REPOSITORY_URI}:${IMAGE_TAG}
                        """
                    }
                }
            }
        }

        stage('Update Image Tag') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    withCredentials([usernamePassword(credentialsId: 'GIT_CREDENTIALS', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh '''
                            git config --global user.email "jenkins@castlehoo.com"
                            git config --global user.name "Jenkins"
                            git remote set-url origin "https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/Coconut-Finance-Team/Coconut-Back-App.git"
                            sed -i "s|image: ${REPOSITORY_URI}:.*|image: ${REPOSITORY_URI}:${IMAGE_TAG}|" k8s/deployment.yaml
                            git add k8s/deployment.yaml
                            git commit -m "chore: Update image tag to ${IMAGE_TAG}"
                            git push origin HEAD:main
                        '''
                    }
                }
            }
        }

        stage('Sync ArgoCD Application') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        withCredentials([file(credentialsId: 'KUBE_CONFIG', variable: 'KUBECONFIG')]) {
                            sh '''
                                export KUBECONFIG="$KUBECONFIG"
                                argocd login aebaac6a687b24f28ad8311739898b12-2096717322.ap-northeast-2.elb.amazonaws.com \
                                    --username coconut \
                                    --password twinho3230 \
                                    --insecure \
                                    --grpc-web
                                argocd app sync backend-app --grpc-web
                                argocd app wait backend-app --timeout 300 --grpc-web
                            '''
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                echo '파이프라인 정리 작업 시작'
                sh '''
                    echo "Docker 로그아웃..."
                    docker logout
                    
                    echo "임시 파일 정리..."
                    rm -f $KUBECONFIG
                    
                    echo "작업 디렉토리 정리..."
                    rm -rf build .gradle
                    
                    echo "Docker 이미지 정리..."
                    docker system prune -af
                '''
                echo '정리 작업 완료'
            }
        }
        
        failure {
            script {
                echo '파이프라인 실패! 로그를 확인하세요.'
            }
        }
        
        success {
            script {
                echo '파이프라인 성공적으로 완료!'
            }
        }
    }
}
