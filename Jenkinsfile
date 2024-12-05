pipeline {
    agent any

    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
    }

    environment {
        ECR_REPOSITORY = "castlehoo/backend"
        DOCKER_TAG = "${BUILD_NUMBER}"
        ARGOCD_CREDENTIALS = credentials('argocd-credentials')
        ARGOCD_SERVER_URL = credentials('argocd-server-url')
        KUBE_CONFIG = credentials('eks-kubeconfig')
        GIT_CREDENTIALS = credentials('github-token-2')
        AWS_CREDENTIALS = credentials('aws-credentials')
        AWS_ACCOUNT_ID = credentials('aws-account-id')
    }

    stages {
        stage('Cleanup Workspace') {
            steps {
                script {
                     try {
                    echo "단계: Docker 이미지 빌드 시작"
                    sh """
                        echo "빌드된 JAR 파일 확인..."
                        ls -la build/libs/
                        
                        echo "Docker 빌드 컨텍스트 확인..."
                        ls -la
                        
                        echo "Docker 이미지 빌드 중... (태그: ${DOCKER_TAG})"
                        docker build -t ${ECR_REPOSITORY}:${DOCKER_TAG} .
                    """
                    echo "Docker 이미지 빌드 완료"
                } catch (Exception e) {
                    error("Docker 이미지 빌드 중 오류 발생: ${e.message}")
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
                            checkout([
                                $class: 'GitSCM',
                                branches: [[name: '*/test']],
                                userRemoteConfigs: [[url: 'https://github.com/Coconut-Finance-Team/Coconut-Backend-App.git', credentialsId: 'github-token-2']]
                            ])
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
                    withCredentials([
                        file(credentialsId: 'application-es', variable: 'ES_PROPERTIES'),
                        file(credentialsId: 'application-db', variable: 'DB_PROPERTIES'),
                        file(credentialsId: 'application-koreainvestment', variable: 'KOREA_PROPERTIES'),
                        file(credentialsId: 'application-oauth', variable: 'OAUTH_PROPERTIES'),
                        file(credentialsId: 'application-email', variable: 'EMAIL_PROPERTIES'),
                        file(credentialsId: 'application-redis', variable: 'REDIS_PROPERTIES'),
                        file(credentialsId: 'application-monitoring', variable: 'MONITORING_PROPERTIES'),
                        string(credentialsId: 'es-host', variable: 'ES_HOST'),
                        string(credentialsId: 'cloud-db-url', variable: 'CLOUD_DB_URL'),
                        string(credentialsId: 'onprem-db-url', variable: 'ONPREM_DB_URL'),
                        string(credentialsId: 'onprem-read-db-url', variable: 'ONPREM_READ_DB_URL'),
                        usernamePassword(credentialsId: 'CLOUD_DB_MASTER', usernameVariable: 'CLOUD_DB_USERNAME', passwordVariable: 'CLOUD_DB_PASSWORD'),
                        usernamePassword(credentialsId: 'ONPREM_DB_MASTER', usernameVariable: 'ONPREM_DB_USERNAME', passwordVariable: 'ONPREM_DB_PASSWORD')
                    ]) {
                        sh '''
                            echo "Gradle 권한 설정..."
                            chmod +x gradlew
                            
                            echo "Properties 파일 복사..."
                            mkdir -p src/main/resources
                            
                            # ES Properties
                            cp $ES_PROPERTIES src/main/resources/application-es.properties
                            sed -i "s|\\${ES_HOST}|$ES_HOST|g" src/main/resources/application-es.properties
                            
                            # DB Properties
                            cp $DB_PROPERTIES src/main/resources/application-db.properties
                            sed -i "s|\\${CLOUD_DB_URL}|$CLOUD_DB_URL|g" src/main/resources/application-db.properties
                            sed -i "s|\\${CLOUD_DB_USERNAME}|$CLOUD_DB_USERNAME|g" src/main/resources/application-db.properties
                            sed -i "s|\\${CLOUD_DB_PASSWORD}|$CLOUD_DB_PASSWORD|g" src/main/resources/application-db.properties
                            sed -i "s|\\${ONPREM_DB_URL}|$ONPREM_DB_URL|g" src/main/resources/application-db.properties
                            sed -i "s|\\${ONPREM_READ_DB_URL}|$ONPREM_READ_DB_URL|g" src/main/resources/application-db.properties
                            sed -i "s|\\${ONPREM_DB_USERNAME}|$ONPREM_DB_USERNAME|g" src/main/resources/application-db.properties
                            sed -i "s|\\${ONPREM_DB_PASSWORD}|$ONPREM_DB_PASSWORD|g" src/main/resources/application-db.properties
                            
                            # Redis Properties
                            cp $REDIS_PROPERTIES src/main/resources/application-redis.properties
                            
                            # Other Properties
                            cp $KOREA_PROPERTIES src/main/resources/application-koreainvestment.properties
                            cp $OAUTH_PROPERTIES src/main/resources/application-oauth.properties
                            cp $EMAIL_PROPERTIES src/main/resources/application-email.properties

                            # Monitoring Properties 추가
                            cp $MONITORING_PROPERTIES src/main/resources/application-monitoring.properties
                            
                            echo "Spring 애플리케이션 빌드 중..."
                            ./gradlew clean build -x test --info
                            
                            echo "빌드 결과 확인..."
                            ls -la build/libs/
                            echo "생성된 JAR 파일:"
                            find build/libs/ -name "*.jar" -type f -exec ls -l {} \;
                        '''
                    }
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
    $class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: 'aws-credentials',
    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
]]) {
                            echo "단계: ECR 푸시 시작"
                            
                            def imageExists = sh(
                                script: "docker images ${ECR_REPOSITORY}:${DOCKER_TAG} --format '{{.Repository}}'",
                                returnStdout: true
                            ).trim()
                            
                            if (!imageExists) {
                                error("Docker 이미지 ${ECR_REPOSITORY}:${DOCKER_TAG}를 찾을 수 없습니다")
                            }
                            
                            sh """
                                echo "ECR 로그인 중..."
                                aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com
                                
                                echo "이미지 태깅 중..."
                                docker tag ${ECR_REPOSITORY}:${DOCKER_TAG} ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}
                                
                                echo "ECR로 이미지 푸시 중..."
                                docker push ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}
                                
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
                        withCredentials([usernamePassword(credentialsId: 'github-token-2', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                            sh """
                                set -x
                                echo "Git 저장소 업데이트..."
                                git fetch --all
                                git stash
                                git checkout -B test origin/test
                                git reset --hard origin/test
                                
                                echo "deployment.yaml 수정..."
                                sed -i 's|image:.*|image: ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPOSITORY}:${DOCKER_TAG}|' k8s/deployment.yaml
                                
                                echo "Git 변경 사항 확인..."
                                git diff
                                
                                echo "Git 설정..."
                                git config --global user.email "jenkins@castlehoo.com"
                                git config --global user.name "Jenkins"
                                
                                echo "변경 사항 커밋..."
                                git add k8s/deployment.yaml
                                git commit -m "Update backend deployment to version ${DOCKER_TAG}" || echo "변경 사항 없음, 스킵"
                                
                                echo "GitHub로 푸시..."
                                git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/Coconut-Finance-Team/Coconut-Backend-App.git
                                git push origin test
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
                        
                        withCredentials([
                            usernamePassword(credentialsId: 'github-token-2', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD'),
                            usernamePassword(credentialsId: 'argocd-credentials', usernameVariable: 'ARGOCD_USERNAME', passwordVariable: 'ARGOCD_PASSWORD')
                        ]) {
                            sh """
                                export PATH=\$PATH:/var/lib/jenkins/bin:/usr/local/bin
                                
                                echo "ArgoCD 로그인..."
                                argocd login \${ARGOCD_SERVER_URL} \
                                    --username \${ARGOCD_USERNAME} \
                                    --password \${ARGOCD_PASSWORD} \
                                    --insecure \
                                    --grpc-web
                                
                                echo "Git 리포지토리 인증 정보 ArgoCD에 추가..."
                                argocd repo add https://github.com/Coconut-Finance-Team/Coconut-Backend-App.git \
                                    --username \${GIT_USERNAME} \
                                    --password \${GIT_PASSWORD} \
                                    --grpc-web \
                                    --upsert

                                echo "coconut-backend 상태 확인..."
                                argocd app get coconut-backend --grpc-web || true
                                
                                echo "coconut-backend 동기화 중..."
                                argocd app sync coconut-backend --grpc-web
                                
                                echo "coconut-backend 상태 대기 중..."
                                argocd app wait coconut-backend --health --timeout 300 --grpc-web
                            """
                        }
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
