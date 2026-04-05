pipeline {
    agent any

    // 1단계에서 등록한 JDK 이름을 여기서 불러옵니다.
    tools {
        jdk 'jdk17'
    }

    stages {
        stage('Prepare') {
            steps {
                // gradlew에 실행 권한 부여
                sh 'chmod +x gradlew'
            }
        }

stage('Build') {
    steps {
        // 도구 설정 무시하고 시스템에 깔린(또는 직접 지정한) java 확인
        sh 'java -version'
        sh './gradlew clean build -x test'
    }
}

        stage('Docker Deploy') {
            steps {
                echo 'Deploying with Docker Compose...'
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