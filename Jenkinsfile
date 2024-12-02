pipeline {
    agent any
    
    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
    }
    
    environment {
        ECR_REPOSITORY = "castlehoo/backend"
        DOCKER_TAG = "${BUILD_NUMBER}"
        ARGOCD_CREDENTIALS = credentials('argocd-token')
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

        stage('Check Commit Message') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        echo "단계: 커밋 메시지 확인 시작"
                        def commitMessage = sh(
                            script: 'git log -1 --pretty=%B',
                            returnStdout: true
                        ).trim()
                        echo "커밋 메시지: ${commitMessage}"
                        if (commitMessage.startsWith("Update deployment to version")) {
                            echo "배포 업데이트 커밋 감지, 빌드 중단"
                            currentBuild.result = 'ABORTED'
                            error("배포 업데이트 커밋으로 인한 빌드 스킵")
                        }
                        echo "커밋 메시지 확인 완료"
                    }
                }
            }
        }

        stage('Checkout') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        echo "단계: 소스 코드 체크아웃 시작"
                        retry(3) {
                            checkout scm
                        }
                        echo "소스 코드 체크아웃 완료"
                    }
                }
            }
        }

        stage('Build Spring Application') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        echo "단계: Spring 애플리케이션 빌드 시작"
                        timeout(time: 10, unit: 'MINUTES') {
                            sh '''
                                echo "Gradle 권한 설정..."
                                chmod +x gradlew
                                
                                echo "Spring 애플리케이션 빌드 중..."
                                ./gradlew clean build -x test
                                
                                echo "빌드 결과 확인..."
                                ls -la build/libs/
                            '''
                        }
                        echo "Spring 애플리케이션 빌드 완료"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        try {
                            echo "단계: Docker 이미지 빌드 시작"
                            sh """
                                echo "Docker 빌드 컨텍스트 확인..."
                                ls -la
                                
                                echo "Docker 이미지 빌드 중... (태그: ${DOCKER_TAG})"
                                docker build -t ${ECR_REPOSITORY}:${DOCKER_TAG} .
                                
                                echo "빌드된 Docker 이미지 확인"
                                docker images | grep ${ECR_REPOSITORY}
                            """
                            echo "Docker 이미지 빌드 완료"
                        } catch (Exception e) {
                            error("Docker 이미지 빌드 중 오류 발생: ${e.message}")
                        }
                    }
                }
            }
        }

        stage('Push to AWS ECR') {
            steps {
                script {
                    try {
                        withCredentials([[
                            credentialsId: 'aws-credentials',
                            accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                            secretKeyVariable: 'AWS_SECRET_ACCESS_KEY',
                            $class: 'AmazonWebServicesCredentialsBinding'
                        ]]) {
                            echo "단계: ECR 푸시 시작"
                            
                            // Docker 이미지 존재 여부 확인
                            def imageExists = sh(
                                script: "docker images ${ECR_REPOSITORY}:${DOCKER_TAG} --format '{{.Repository}}'",
                                returnStdout: true
                            ).trim()
                            
                            if (!imageExists) {
                                error("Docker 이미지 ${ECR_REPOSITORY}:${DOCKER_TAG}를 찾을 수 없습니다")
                            }
                            
                            sh """
                                echo "ECR 로그인 중..."
                                aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 992382629018.dkr.ecr.ap-northeast-2.amazonaws.com
                                
                                echo "이미지 태깅 중..."
                                docker tag ${ECR_REPOSITORY}:${DOCKER_TAG} 992382629018.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}
                                
                                echo "ECR로 이미지 푸시 중..."
                                docker push 992382629018.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}
                                
                                echo "푸시된 이미지 확인 중..."
                                aws ecr describe-images --repository-name ${ECR_REPOSITORY} --image-ids imageTag=${DOCKER_TAG} --region ap-northeast-2
                            """
                            echo "ECR 푸시 완료"
                        }
                    } catch (Exception e) {
                        error("ECR 푸시 중 오류 발생: ${e.message}")
                    }
                }
            }
        }

stage('Update Kubernetes Manifests') {
    steps {
        script {
            try {
                echo "단계: Kubernetes 매니페스트 업데이트 시작"
                withCredentials([usernamePassword(credentialsId: 'github-token', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                    sh """
                        set -x
                        echo "Git 상태 확인..."
                        git status
                        
                        echo "현재 작업 디렉토리 확인..."
                        pwd
                        ls -la
                        
                        echo "Git 저장소 다시 클론..."
                        rm -rf .git
                        git init
                        git remote add origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/Coconut-Finance-Team/Coconut-Back-App.git
                        git fetch origin
                        git checkout -B main origin/main
                        
                        echo "체크아웃 후 디렉토리 확인..."
                        ls -la
                        ls -la k8s/ || echo "k8s 디렉토리가 없습니다"
                        
                        echo "deployment.yaml 수정..."
                        sed -i 's|image:.*|image: 992382629018.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}|' k8s/deployment.yaml
                        
                        echo "Git 변경 사항 확인..."
                        git diff
                        
                        echo "Git 설정..."
                        git config --global user.email "jenkins@castlehoo.com"
                        git config --global user.name "Jenkins"
                        
                        echo "변경 사항 커밋..."
                        git add k8s/deployment.yaml
                        git commit -m "Update backend deployment to version ${DOCKER_TAG}" || echo "변경 사항 없음, 스킵"
                        
                        echo "GitHub로 푸시..."
                        git push origin main
                    """
                }
                echo "Kubernetes 매니페스트 업데이트 완료"
            } catch (Exception e) {
                error("Kubernetes 매니페스트 업데이트 중 오류 발생: ${e.message}")
            }
        }
    }
}

        stage('Sync ArgoCD Application') {
            steps {
                script {
                    try {
                        echo "단계: ArgoCD 동기화 시작"
                        
                        sh """
                            export PATH=\$PATH:/var/lib/jenkins/bin:/usr/local/bin
                            
                            echo "설치된 도구 확인..."
                            which kubectl
                            which argocd
                            kubectl version --client
                            argocd version --client
                            
                            echo "ArgoCD 서버 상태 확인..."
                            ARGOCD_SERVER="aebaac6a687b24f28ad8311739898b12-2096717322.ap-northeast-2.elb.amazonaws.com"
                            
                            echo "ArgoCD 로그인..."
                            argocd login \${ARGOCD_SERVER} \
                                --username coconut \
                                --password twinho3230 \
                                --insecure \
                                --grpc-web
                            
                            echo "애플리케이션 동기화 중..."
                            argocd app sync backend-app --grpc-web
                            
                            echo "애플리케이션 상태 대기 중..."
                            argocd app wait backend-app --health --timeout 300 --grpc-web
                            
                            echo "최종 애플리케이션 상태 확인..."
                            argocd app get backend-app --grpc-web
                        """
                        echo "ArgoCD 동기화 완료"
                    } catch (Exception e) {
                        error("ArgoCD 동기화 중 오류 발생: ${e.message}")
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
