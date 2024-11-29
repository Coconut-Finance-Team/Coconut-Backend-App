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
                    def hasHistory = sh(script: 'git rev-parse HEAD^1 > /dev/null 2>&1', returnStatus: true) == 0
                    
                    if (hasHistory) {
                        def changes = sh(script: '''
                            git fetch origin
                            git diff HEAD^1 HEAD --name-only | grep -v k8s/deployment.yaml || true
                        ''', returnStdout: true).trim()
                        
                        if (!changes) {
                            echo "k8s/deployment.yaml 외 변경된 파일이 없습니다."
                            echo "WARNING: No relevant changes detected, but continuing pipeline"
                        }
                    } else {
                        echo "첫 번째 빌드입니다. 모든 파일을 변경사항으로 간주합니다."
                    }
                }
            }
        }

        stage('Check Commit Message') {
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
            steps {
                checkout scm
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
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
               sh """
                    set -x  # 실행되는 명령어 상세 출력
                    
                    echo "Step 1: KUBECONFIG 설정"
                    export KUBECONFIG=${KUBE_CONFIG}
                    
                    echo "Step 2: ArgoCD 서버 설정"
                    ARGOCD_SERVER="afd51e96d120b4dce86e1aa21fe3316d-787997945.ap-northeast-2.elb.amazonaws.com"
                    
                    echo "Step 3: ArgoCD 버전 확인"
                    argocd version || true
                    
                    echo "Step 4: ArgoCD 로그인"
                    argocd login \${ARGOCD_SERVER} \
                        --username coconut \
                        --password coconutkr \
                        --insecure || exit 1
                    
                    echo "Step 5: ArgoCD 컨텍스트 확인"
                    argocd context list || true
                    
                    echo "Step 6: 현재 컨텍스트 확인"
                    argocd context | grep 'Current' || true
                    
                    echo "Step 7: 애플리케이션 목록 확인"
                    argocd app list || true
                    
                    echo "Step 8: 애플리케이션 상태 확인"
                    argocd app get backend-app || true
                    
                    echo "Step 9: 애플리케이션 동기화 시도"
                    argocd app sync backend-app || exit 1
                    
                    echo "Step 10: 애플리케이션 상태 대기"
                    argocd app wait backend-app --health --timeout 300 || exit 1
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
