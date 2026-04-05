pipeline {
    agent any

    stages {
        stage('Prepare') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot Application...'
                sh './gradlew clean build -x test'
            }
        }

        stage('Docker Deploy') {
            steps {
                echo 'Deploying with Docker Compose...'
                // --build를 통해 수정된 소스코드가 반영된 새 이미지를 만듭니다.
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