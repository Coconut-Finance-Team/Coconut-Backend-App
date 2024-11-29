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
                            git reset --hard HEAD || true
                            git clean -fdx || true
                            
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

        stage('Check for Changes') {
            steps {
                script {
                    echo "변경사항 감지 검사 시작..."
                    sh '''
                        git fetch origin
                        git diff HEAD^1 HEAD --name-only | grep -v k8s/deployment.yaml || true
                    '''
                }
            }
        }

        stage('Build Spring Boot Application') {
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
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            # Dockerfile 생성
                            cat > Dockerfile << 'EOL'
FROM openjdk:17-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EOL
                            # Docker 빌드
                            docker build -t ${ECR_REPOSITORY}:${DOCKER_TAG} .
                        """
                    }
                }
            }
        }

        stage('Push to AWS ECR') {
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
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            git config --global user.email "jenkins@example.com"
                            git config --global user.name "Jenkins"
                            
                            # 변경사항을 스태시하고 브랜치 전환
                            git stash || true
                            git checkout main || git checkout -b main
                            git pull origin main || true
                            
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
                            git commit -m "Update backend deployment to version ${DOCKER_TAG}" || true
                            git push origin main
                        """
                    }
                }
            }
        }

        stage('Sync ArgoCD Application') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        sh """
                            export KUBECONFIG=${KUBE_CONFIG}
                            ARGOCD_SERVER="afd51e96d120b4dce86e1aa21fe3316d-787997945.ap-northeast-2.elb.amazonaws.com"
                            
                            # ArgoCD 로그인 및 앱 생성 (없는 경우)
                            argocd login \${ARGOCD_SERVER} \
                                --username admin \
                                --password coconutkr \
                                --insecure
                            
                            # 애플리케이션이 없다면 생성
                            argocd app create backend-app \
                                --repo https://github.com/Coconut-Finance-Team/Coconut-Back-App.git \
                                --path k8s \
                                --dest-server https://kubernetes.default.svc \
                                --dest-namespace coconut-backend \
                                --sync-policy auto \
                                --self-heal \
                                --auto-prune || true
                            
                            # 동기화 및 대기
                            argocd app sync backend-app
                            argocd app wait backend-app --health --timeout 300
                        """
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
