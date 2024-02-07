def appPublish(String machine, String cmd, String files, String dir) {
    sshPublisher(
        publishers: [
            sshPublisherDesc(
                configName: machine,
                transfers: [
                    sshTransfer(
                        cleanRemote: false,
                        excludes: '',
                        execCommand: cmd,
                        execTimeout: 120000,
                        flatten: false,
                        makeEmptyDirs: false,
                        noDefaultExcludes: false,
                        patternSeparator: '[, ]+',
                        remoteDirectory: dir,
                        remoteDirectorySDF: false,
                        removePrefix: '',
                        sourceFiles: files
                    )
                ],
                usePromotionTimestamp: false,
                useWorkspaceInPromotion: false,
                verbose: true
            )
        ]
    )
}

pipeline {
    agent any

    environment {
        // 项目目录
        PROJECT_PATH = '/home/ubuntu/projects/jenkins_demo'
        // 更新命令
        APP_UPDATE_CMD = 'cd "${HOME}/tmp/demo" && bash -x deploy.sh'
        // 需要传输的文件列表
        SSH_SOURCE_FILES = 'demo.tar.gz,deploy.sh'
    }

    stages {
        stage('build') {
            steps {
                sh "cd ${PROJECT_PATH} && git checkout main && git pull && git checkout ${GIT_COMMIT} && git pull && git rev-parse HEAD"
                sh "cd ${PROJECT_PATH} && bash -x package.sh"
                sh "pwd && mv ${PROJECT_PATH}/demo.tar.gz . && cp ${PROJECT_PATH}/deploy.sh . && ls"
            }
        }

        stage("parallel deploy") {

            parallel {
                stage("deploy-detamine") {
                    when {
                        expression {
                            env.DEPLOY_MACHINE.contains("datamine")
                        }
                    }
                    steps {
                        appPublish("datamine", "${APP_UPDATE_CMD}", "${SSH_SOURCE_FILES}", "/tmp/demo")
                    }
                }
            }
        }
    }
}
