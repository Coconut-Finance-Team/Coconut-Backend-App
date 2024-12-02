pipeline {
    agent any
    
    triggers {
        githubPush()
    }
    
    environment {
        ECR_REPOSITORY = "castlehoo/backend"
        DOCKER_TAG = "${BUILD_NUMBER}"
        KUBE_CONFIG = credentials('eks-kubeconfig')
        GIT_CREDENTIALS = credentials('github-token')
        AWS_CREDENTIALS = credentials('aws-credentials')
        ECR_URL = "992382629018.dkr.ecr.ap-northeast-2.amazonaws.com"
        AWS_REGION = "ap-northeast-2"
        ARGOCD_SERVER = "aebaac6a687b24f28ad8311739898b12-2096717322.ap-northeast-2.elb.amazonaws.com"
    }
    
    stages {
        stage('Cleanup Workspace') {
            steps {
                script {
                    try {
                        echo "시스템 클린업 시작..."
                        sh '''
                            echo "Docker 시스템 정리 중..."
                            docker system prune -af || true
                            docker volume prune -f || true
                            docker network prune -f || true
                            
                            echo "이전 빌드 정리 중..."
                            rm -rf build || true
                            rm -rf .gradle || true
                            
                            echo "현재 디스크 사용량:"
                            df -h
                            echo "Docker 디스크 사용량:"
                            docker system df
                        '''
                    } catch (Exception e) {
                        echo "클린업 중 오류 발생: ${e.message}"
                    }
                }
            }
        }

        stage('Build Spring Boot Application') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
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
        }

        stage('Build and Push Docker Image') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    withCredentials([[
                        $class: 'AmazonWebServicesCredentialsBinding',
                        credentialsId: 'aws-credentials',
                        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                    ]]) {
                        script {
                            def FULL_IMAGE_NAME = "${ECR_URL}/${ECR_REPOSITORY}"
                            
                            sh """
                                # Docker 빌드
                                docker build -t ${ECR_REPOSITORY}:${DOCKER_TAG} .
                                
                                # ECR 로그인 및 푸시
                                aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_URL}
                                docker tag ${ECR_REPOSITORY}:${DOCKER_TAG} ${FULL_IMAGE_NAME}:${DOCKER_TAG}
                                docker push ${FULL_IMAGE_NAME}:${DOCKER_TAG}
                            """
                        }
                    }
                }
            }
        }

        stage('Update Image Tag') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    withCredentials([usernamePassword(
                        credentialsId: 'github-token',
                        usernameVariable: 'GIT_USERNAME',
                        passwordVariable: 'GIT_PASSWORD'
                    )]) {
                        script {
                            sh """
                                git config --global user.email "jenkins@castlehoo.com"
                                git config --global user.name "Jenkins"
                                
                                git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/Coconut-Finance-Team/Coconut-Back-App.git
                                
                                # deployment.yaml의 이미지 태그 업데이트
                                sed -i 's|image: ${ECR_URL}/${ECR_REPOSITORY}:.*|image: ${ECR_URL}/${ECR_REPOSITORY}:${DOCKER_TAG}|' k8s/deployment.yaml
                                
                                # 변경사항 커밋 및 푸시
                                git add k8s/deployment.yaml
                                git commit -m "Update backend deployment to version ${DOCKER_TAG}"
                                git push origin main
                            """
                        }
                    }
                }
            }
        }

        stage('Sync ArgoCD Application') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        script {
                            sh """
                                export KUBECONFIG=${KUBE_CONFIG}
                                
                                # ArgoCD 로그인
                                argocd login \${ARGOCD_SERVER} \
                                    --username coconut \
                                    --password twinho3230 \
                                    --insecure --grpc-web
                                
                                # 동기화 및 대기
                                argocd app sync backend-app --grpc-web
                                argocd app wait backend-app --health --timeout 300 --grpc-web
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                echo "파이프라인 정리 작업 시작"
                try {
                    sh '''
                        echo "Docker 로그아웃..."
                        docker logout
                        
                        echo "임시 파일 정리..."
                        rm -f ${KUBE_CONFIG}
                        
                        echo "작업 디렉토리 정리..."
                        rm -rf build .gradle
                        
                        echo "Docker 이미지 정리..."
                        docker system prune -af || true
                    '''
                } catch (Exception e) {
                    echo "정리 작업 중 오류 발생: ${e.message}"
                }
                echo "정리 작업 완료"
            }
        }
        failure {
            script {
                echo '파이프라인 실패! 로그를 확인하세요.'
            }
        }
        success {
            script {
                echo '파이프라인이 성공적으로 완료되었습니다!'
            }
        }
    }
}
