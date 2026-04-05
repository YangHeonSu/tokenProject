pipeline {
    // 1. 전체 파이프라인에서 기본적으로 Java 17 컨테이너를 사용하도록 설정
    agent {
        docker {
            image 'eclipse-temurin:17-jdk-alpine'
            // 컨테이너 안에서 호스트의 docker 명령어를 쓸 수 있게 소켓 공유
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage('Prepare') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot Application (Java 17)...'
                // 컨테이너 환경이므로 Java 17이 이미 세팅되어 있음
                sh './gradlew clean build -x test'
            }
        }

        stage('Docker Deploy') {
            steps {
                echo 'Deploying with Docker Compose...'
                // 주의: 에이전트 이미지(alpine) 안에 docker-compose가 설치되어 있어야 함
                // 만약 없다면, 이 단계만 agent any(호스트)에서 실행하도록 분리해야 함
                sh 'docker-compose up --build -d'
            }
        }
    }

    post {
        success {
            echo '✅ 배포 성공!'
        }
        failure {
            echo '❌ 배포 실패.. 로그를 확인하세요.'
        }
    }
}