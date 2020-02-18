pipeline {
    agent any
    tools{
        maven 'M3'
        jdk 'JDK8'
    }
    stages {
        stage ('Initialize') {
            steps {
                script{
                    bat '''
                    echo 'PATH = %PATH%'
                    echo 'M2_HOME = %M2_HOME%'
                    echo 'JAVA_HOME = %JAVA_HOME%'
                    echo 'DOCKER_HOST = %DOCKER_HOST%'
                    echo 'DOCKER_TOOLBOX_INSTALL_PATH = %DOCKER_TOOLBOX_INSTALL_PATH%'
                    '''
                }
            }
        }
        stage('Build') {
            steps {
                script{
                    bat "cd %M2_HOME%/bin"
                    bat "mvn clean package"
                }
            }
        }
    }
}
