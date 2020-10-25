stage('pull source code') {
    node('master'){
        git([url: 'git@github.com:sisul1204/iWeb.git', branch: 'master'])
    }
}

stage('maven compile & package') {
    node('master'){
        sh ". /etc/profile"
        sh ". ~/.bash_profile"

        //定义maven java环境
        def mvnHome = tool 'apache-maven-3.6.3'
        def jdkHome = tool 'javamaster'
        env.PATH = "${mvnHome}/bin:${env.PATH}"
        env.PATH = "${jdkHome}/bin:${env.PATH}"
        sh "mvn clean install"
        sh "mv target/iWeb.war target/ROOT.war"
    }
}

stage('clean docker environment') {
    node('master'){
        try{
            sh 'docker stop iWebObj'
        }catch(exc){
            echo 'iWebObj container is not running!'
        }

        try{
            sh 'docker rm iWebObj'
        }catch(exc){
            echo 'iWebObj container does not exist!'
        }
        try{
            sh 'docker rmi iweb'
        }catch(exc){
            echo 'iweb image does not exist!'
        }
    }
}

stage('make new docker image') {
    node('master'){
        try{
            sh 'docker build -t iweb .'
        }catch(exc){
            echo 'Make iweb docker image failed, please check the environment!'
        }
    }
}

stage('start docker container') {
    node('master'){
        try{
            sh 'docker run --name iWebObj -d -p 8083:8080 iweb'
        }catch(exc){
            echo 'Start docker image failed, please check the environment!'
        }
    }
}
